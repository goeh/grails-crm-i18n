/*
 * Copyright 2012 Goran Ehrsson.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package grails.plugins.crm.i18n

import grails.plugins.crm.core.TenantUtils
import grails.plugins.crm.core.WebUtils
import org.apache.commons.lang.LocaleUtils

class CrmMessageController {

    static allowedMethods = [index: ['GET', 'POST'], create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']

    static navigation = [
            [group: 'admin',
                    order: 980,
                    title: 'crmMessage.index.label',
                    action: 'index'
            ],
            [group: 'crmAdmin',
                    order: 980,
                    title: 'crmMessage.list.label',
                    action: 'list'
            ]
    ]

    def crmMessageService

    def index = {
        def locale = params.lang ? LocaleUtils.toLocale(params.lang) : null
        def properties = new Properties()
        switch (request.method) {
            case 'GET':
                if (locale) {
                    request.session.crmMessageAdminLocale = locale
                }
                break
            case 'POST':
                request.session.crmMessageAdminLocale = locale
                int count = 0
                params.findAll{it.key.startsWith('properties.')}.each{key, value->
                    if(value) {
                        // TODO Figure out how to only update texts that has been modified by user.
                        // This if statement makes it impossible to save a text as blank.
                        properties.setProperty(key[11..-1], value)
                        count++
                    }
                }
                properties = crmMessageService.updateProperties(properties, locale)
                flash.success = message(code: 'crmMessage.all.updated.message', args: [message(code: 'crmMessage.label', default: 'Message'), count.toString()])
                break
        }
        return [properties: properties, locale:locale]
    }

    def list = {
        def tenant = TenantUtils.tenant
        params.max = Math.min(params.max ? params.int('max') : 25, 100)
        [crmMessageList: CrmMessage.findAllByTenantId(tenant, params), crmMessageTotal: CrmMessage.countByTenantId(tenant)]
    }

    def create = {
        switch (request.method) {
            case 'GET':
                return [crmMessage: new CrmMessage(params)]
            case 'POST':
                def crmMessage = new CrmMessage(tenantId: TenantUtils.tenant)
                crmMessage.properties = params
                if (!crmMessage.save(flush: true)) {
                    render view: 'create', model: [crmMessage: crmMessage]
                    return
                }

                crmMessageService.removeFromCache(crmMessage)

                flash.success = message(code: 'crmMessage.created.message', args: [message(code: 'crmMessage.label', default: 'Message'), crmMessage.toString()])
                redirect action: 'list'
                break
        }
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def crmMessage = CrmMessage.get(params.id)
                if (!crmMessage) {
                    flash.error = message(code: 'crmMessage.not.found.message', args: [message(code: 'crmMessage.label', default: 'Message'), params.id])
                    redirect action: 'list'
                    return
                }

                [crmMessage: crmMessage]
                break
            case 'POST':
                def crmMessage = CrmMessage.get(params.id)
                if (!crmMessage) {
                    flash.error = message(code: 'crmMessage.not.found.message', args: [message(code: 'crmMessage.label', default: 'Message'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (crmMessage.version > version) {
                        crmMessage.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'crmMessage.label', default: 'Message')] as Object[],
                                "Another user has updated this Message while you were editing")
                        render view: 'edit', model: [crmMessage: crmMessage]
                        return
                    }
                }

                crmMessage.properties = params

                if (!crmMessage.save(flush: true)) {
                    render view: 'edit', model: [crmMessage: crmMessage]
                    return
                }

                crmMessageService.removeFromCache(crmMessage)

                flash.success = message(code: 'crmMessage.updated.message', args: [message(code: 'crmMessage.label', default: 'Message'), crmMessage.toString()])
                redirect action: 'list'
                break
        }
    }

    def delete = {
        def crmMessage = CrmMessage.get(params.id)
        if (crmMessage) {
            try {
                def tmp = crmMessage.toString()
                crmMessage.delete(flush: true)
                flash.warning = "${message(code: 'crmMessage.deleted.message', args: [message(code: 'crmMessage.label', default: 'Message'), tmp])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.error = "${message(code: 'crmMessage.not.deleted.message', args: [message(code: 'crmMessage.label', default: 'Message'), params.id])}"
                redirect(action: "edit", id: params.id)
            }
        }
        else {
            flash.error = "${message(code: 'crmMessage.not.found.message', args: [message(code: 'crmMessage.label', default: 'Message'), params.id])}"
            redirect(action: "list")
        }
    }

    def export() {
        def file = crmMessageService.exportToFile()
        if (file?.exists()) {
            try {
                def fileName = file.name
                def contentType = fileName.endsWith(".zip") ? 'application/zip' : 'text/plain'
                WebUtils.attachmentHeaders(response, contentType, fileName)
                WebUtils.renderFile(response, file)
            } finally {
                file.delete()
            }
        } else {
            flash.error = message(code: "crmMessage.export.error", default: "Failed to export")
            redirect action: 'index'
        }
    }

    def upload() {
        def fileItem = request.getFile("file")
        if (fileItem?.isEmpty()) {
            flash.error = message(code: "crmMessage.upload.empty", default: "You must select a file to import")
        } else if (fileItem) {
            try {
                crmMessageService.importText(fileItem.inputStream)
                flash.success = message(code: "crmMessage.upload.success", args: [fileItem.originalFilename], default: "Texts imported from {0}")
            } catch (Exception e) {
                log.error("Failed to import: ${fileItem.originalFilename}", e)
                flash.error = message(code: "crmMessage.upload.error", args: [fileItem.originalFilename], default: "Failed to import {0}")
            }
        }
        redirect action: "index"
    }
}
