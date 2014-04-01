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

/**
 * Tests for CrmMessageService.
 */
class CrmMessageTests extends GroovyTestCase {

    def crmMessageService
    def messageSource

    void testUpdateMessages() {
        def locale = new Locale("sv", "SE")
        TenantUtils.withTenant(1) {
            assert messageSource.getMessage('crmMessage.label', [] as Object[], locale) == 'Systemtext'
            crmMessageService.setMessage('crmMessage.label', 'Applikationstext', locale)
            assert messageSource.getMessage('crmMessage.label', [] as Object[], locale) == 'Applikationstext'
        }
    }

    void testCopyMessage() {
        def locale = new Locale("sv", "SE")
        TenantUtils.withTenant(2) {
            crmMessageService.setMessage('crmMessage.label', 'Copy Me!', locale)
            assert messageSource.getMessage('crmMessage.label', [] as Object[], locale) == 'Copy Me!'
        }
        TenantUtils.withTenant(3) {
            assert messageSource.getMessage('crmMessage.label', [] as Object[], locale) == 'Systemtext'
        }
        crmMessageService.copyMessages(2, 3)
        TenantUtils.withTenant(3) {
            assert messageSource.getMessage('crmMessage.label', [] as Object[], locale) == 'Copy Me!'
        }
    }

    void testExportImport() {
        def value = "Räksmörgås is Swedish for \"shrimp sandwich\" and the word contains all the crazy umlauts"
        def file
        TenantUtils.withTenant(4) {
            new CrmMessage(tenantId: TenantUtils.tenant, locale: "sv", code: "test.integration.shrimp",
                    text: value).save(failOnError: true, flush: true)
            file = crmMessageService.exportToFile()
        }
        assert file != null
        def text = file.text
        assert text.length() > 200 && text.length() < 210 // was 205 bytes on my machine.

        TenantUtils.withTenant(4) {
            file.withInputStream { is ->
                crmMessageService.importText(is, new Locale("no"))
            }
        }

        assert CrmMessage.findByCodeAndLocaleAndTenantId("test.integration.shrimp", "no", 4)?.text == value

        TenantUtils.withTenant(5) {
            file.withInputStream { is ->
                crmMessageService.importText(is, new Locale("sv"))
            }
        }

        assert CrmMessage.findByCodeAndLocaleAndTenantId("test.integration.shrimp", "sv", 5)?.text == value

        file.delete()

    }
}
