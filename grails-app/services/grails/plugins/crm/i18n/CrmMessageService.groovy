/*
 * Copyright (c) 2012 Goran Ehrsson.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * under the License.
 */

package grails.plugins.crm.i18n

import org.apache.tools.zip.ZipEntry
import org.apache.tools.zip.ZipOutputStream
import grails.plugins.crm.core.TenantUtils

/**
 * i18n message service.
 */
class CrmMessageService {

    def grailsApplication
    def messageCache

    void removeFromCache(CrmMessage message) {
        if (messageCache) {
            messageCache.removeAll() // TODO remove only the updated message from cache.
        }
    }

    File export(String filename = null) {

        def result = CrmMessage.createCriteria().list {
            eq('tenantId', TenantUtils.tenant)
            order 'locale', 'asc'
            order 'code', 'asc'
        }

        def messages = [:]
        def zipFile

        // Put all messages in a Map keyed by language.
        for (msg in result) {
            messages.get(msg.locale ?: '', [:]).put(msg.code, msg.text)
        }

        // If no file name is specified, use application name.
        if (!filename) {
            filename = grailsApplication.metadata['app.name']
        }

        if (messages) {
            zipFile = File.createTempFile(filename, ".zip")
            ZipOutputStream zos = new ZipOutputStream(zipFile.newOutputStream())

            messages.each {lang, map ->
                def name = filename
                if (lang) {
                    name += ('_' + lang)
                }
                name += '.properties'
                zos.putNextEntry(new ZipEntry(name))
                map.each {key, text ->
                    zos << "$key = $text\n"
                }
                zos.closeEntry()
            }
            zos.close()
        }
        return zipFile
    }
}
