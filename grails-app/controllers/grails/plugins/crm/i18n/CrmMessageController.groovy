/*
 *  Copyright 2012 Goran Ehrsson.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package grails.plugins.crm.i18n

import grails.plugins.crm.core.TenantUtils

class CrmMessageController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']

    static navigation = [
            [group: 'crmAdmin',
                    order: 980,
                    title: 'crmMessage.label',
                    action: 'list'
            ],
            [group: 'crmMessage',
                    order: 20,
                    title: 'crmMessage.create.label',
                    action: 'create',
                    isVisible: { actionName != 'create' }
            ],
            [group: 'crmMessage',
                    order: 30,
                    title: 'crmMessage.list.label',
                    action: 'list',
                    isVisible: { actionName != 'list' }
            ],
            [group: 'crmMessage',
                    order: 40,
                    title: 'crmMessage.export.label',
                    action: 'export',
                    isVisible: { actionName == 'list' }
            ]
    ]

    def grailsApplication
    def crmMessageService

    def index = {
        redirect(action: "list", params: params)
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

                flash.message = message(code: 'default.created.message', args: [message(code: 'crmMessage.label', default: 'Message'), crmMessage.toString()])
                redirect action: 'list'
                break
        }
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def crmMessage = CrmMessage.get(params.id)
                if (!crmMessage) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'crmMessage.label', default: 'Message'), params.id])
                    redirect action: 'list'
                    return
                }

                [crmMessage: crmMessage]
                break
            case 'POST':
                def crmMessage = CrmMessage.get(params.id)
                if (!crmMessage) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'crmMessage.label', default: 'Message'), params.id])
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

                flash.message = message(code: 'default.updated.message', args: [message(code: 'crmMessage.label', default: 'Message'), crmMessage.toString()])
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
                flash.message = "${message(code: 'crmMessage.deleted.message', args: [message(code: 'crmMessage.label', default: 'Message'), tmp])}"
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
        def zipFile = crmMessageService.export()
        if (zipFile?.exists()) {
            try {
                def filename = grailsApplication.metadata['app.name'] ?: 'i18n'
                response.setHeader("Content-disposition", "attachment; filename=${filename}.zip")
                response.contentType = "application/zip"
                response.characterEncoding = "UTF-8"
                response.setContentLength(zipFile.length().intValue())
                response.setHeader("Pragma", "")
                response.setHeader("Cache-Control", "private,no-store,max-age=120")
                Calendar cal = Calendar.getInstance()
                cal.add(Calendar.MINUTE, 2)
                response.setDateHeader("Expires", cal.getTimeInMillis())

                def out = response.outputStream
                zipFile.withInputStream {is ->
                    out << is
                }
                out.flush()
            } finally {
                zipFile.delete()
            }
        } else {
            redirect action: 'list'
        }
    }
}
