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

import org.apache.commons.io.FilenameUtils
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

    Properties updateProperties(Properties props, Locale locale = null) {
        def tenant = TenantUtils.tenant
        def localeName = locale ? locale.toString() : null
        props.each {key, value ->
            def msg = CrmMessage.createCriteria().get() {
                eq('tenantId', tenant)
                if (localeName) {
                    eq('locale', localeName)
                } else {
                    isNull('locale')
                }
                eq('code', key)
            }
            if (value == '-') {
                msg?.delete() // Dash/minus removes an existing key
            } else {
                if (msg) {
                    msg.text = value
                } else {
                    msg = new CrmMessage(tenantId: tenant, locale: localeName, code: key, text: value)
                }
                msg.save()
            }
        }

        if (messageCache) {
            messageCache.removeAll() // TODO remove only the updated messages from cache.
        }

        getProperties(locale)
    }

    private Properties getProperties(Locale locale = null) {
        def tenant = TenantUtils.tenant
        def localeName = locale ? locale.toString() : null
        def result = CrmMessage.createCriteria().list() {
            eq('tenantId', tenant)
            if (localeName) {
                eq('locale', localeName)
            } else {
                isNull('locale')
            }
        }
        def props = new Properties()
        for(msg in result) {
            props.setProperty(msg.code, msg.text)
        }
        return props
    }

    File exportToFile(String filename = null) {

        def result = CrmMessage.createCriteria().list {
            eq('tenantId', TenantUtils.tenant)
            order 'locale', 'asc'
            order 'code', 'asc'
        }

        if (!result) {
            return null
        }

        // Create Properties instances, one for each locale.
        def messages = new HashMap<String, Properties>()
        for (msg in result) {
            messages.get(msg.locale ?: '', new Properties()).setProperty(msg.code, msg.text)
        }

        // If no file name is specified, use application name.
        def appName = grailsApplication.metadata['app.name'] ?: "Grails CRM"
        if (filename) {
            filename = FilenameUtils.getBaseName(filename)
        } else {
            filename = appName
        }

        def file
        if (messages.size() > 1) {
            // Multiple locales, create a .zip file.
            file = File.createTempFile(filename, ".zip")
            ZipOutputStream zos = new ZipOutputStream(file.newOutputStream())

            messages.each {lang, props ->
                def name = filename
                if (lang) {
                    name += ('_' + lang)
                }
                name += '.properties'
                zos.putNextEntry(new ZipEntry(name))
                props.store(zos, "Messages ${lang ? 'for locale ' + lang : ''} exported from $appName")
                zos.closeEntry()
            }
            zos.close()
        } else {
            // Only one locale, create a .properties file
            def lang = messages.keySet().first()
            def props = messages.get(lang)
            if (lang) {
                filename = filename + '_' + lang
            }
            file = File.createTempFile(filename, ".properties")
            file.withOutputStream {out ->
                props.store(out, "Messages ${lang ? 'for locale ' + lang : ''} exported from $appName")
            }
        }
        return file
    }

    void importText(InputStream inputStream, Locale locale = null) {
        def props = new Properties()
        props.load(inputStream)
        updateProperties(props, locale)
    }
}
