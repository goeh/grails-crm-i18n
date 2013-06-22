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

import org.apache.commons.logging.Log
import org.springframework.cache.Cache

import java.text.MessageFormat
import org.codehaus.groovy.grails.context.support.PluginAwareResourceBundleMessageSource

import grails.plugins.crm.core.TenantUtils
import org.apache.commons.logging.LogFactory

/**
 *
 * @author Goran Ehrsson
 * @since 0.1
 */
class CrmMessageSource extends PluginAwareResourceBundleMessageSource {

    public static final String CRM_MESSAGE_CACHE = 'crmMessageCache'

    private static final Log LOG = LogFactory.getLog(CrmMessageSource)

    def grailsCacheManager

    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {
        final Long tenant = TenantUtils.getTenant()
        final String key = tenant.toString() + code + locale.toString()
        final Cache messageCache = grailsCacheManager.getCache(CRM_MESSAGE_CACHE)
        def format = messageCache?.get(key)?.get()
        if (format == Boolean.FALSE) {
            return null
        }
        if (format == null) {
            if (LOG.isDebugEnabled() && (messageCache != null)) {
                LOG.debug('1 - ' + key)
            }
            format = findCode(code, locale, tenant) { String a, Locale l ->
                super.resolveCode(a, l)
            }
            if (messageCache != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug('1 > ' + key)
                }
                messageCache.put(key, format != null ? format : Boolean.FALSE)
            }
        } else if (!(format instanceof MessageFormat)) {
            format = new MessageFormat(format.toString(), locale)
            if (messageCache != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug('1 ! ' + key)
                }
                messageCache.put(key, format)
            }
        }
        return format
    }

    @Override
    protected String resolveCodeWithoutArguments(String code, Locale locale) {
        final Long tenant = TenantUtils.getTenant()
        final String key = tenant.toString() + code + locale.toString()
        final Cache messageCache = grailsCacheManager.getCache(CRM_MESSAGE_CACHE)
        def format = messageCache?.get(key)?.get()
        if (format == Boolean.FALSE) {
            return null
        }
        if (format == null) {
            if (LOG.isDebugEnabled() && (messageCache != null)) {
                LOG.debug('2 - ' + key)
            }
            format = findCode(code, locale, tenant) { String a, Locale l ->
                super.resolveCodeWithoutArguments(a, l)
            }
            if (messageCache != null) {
                LOG.debug('2 > ' + key)
                messageCache.put(key, format != null ? format : Boolean.FALSE)
            }
        } else if (LOG.isDebugEnabled() && (messageCache != null)) {
            LOG.debug('2 + ' + key)
        }
        return (format instanceof MessageFormat) ? format.toPattern() : format
    }

    private Object findCode(String code, Locale locale, Long tenant, Closure fallback) {
        // Try with exact match
        def alternatives = [code]
        // Add '.label' suffix.
        if (!code.endsWith('.label')) {
            alternatives << (code + '.label')
        }
        // Replace first group with 'default'
        def altCode = (code =~ /^[^\.]+\./).replaceFirst("default.")
        if (altCode != code) {
            alternatives << altCode
            if (!altCode.endsWith('.label')) {
                alternatives << (altCode + '.label')
            }
        }

        def result = CrmMessage.withCriteria {
            eq('tenantId', tenant)
            inList('code', alternatives)
            or {
                eq('locale', locale.toString()) // sv_SE, en_UK
                eq('locale', locale.language) // sv, en
                isNull('locale')
            }
            order('locale', 'desc')
            cache true
        }

        // If multiple result, make sure we return the most wanted message.
        def format
        for (alt in alternatives) {
            def msg = result.find { it.code == alt }
            format = msg ? new MessageFormat(msg.text, locale) : fallback.call(alt, locale)
            if (format != null) {
                break
            }
        }
        return format
    }
}

