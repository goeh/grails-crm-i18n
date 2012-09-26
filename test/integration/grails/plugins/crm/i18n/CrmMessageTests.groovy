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

    void testExportImport() {
        def value = "Räksmörgås is Swedish for \"shrimp sandwich\" and the word contains all the crazy umlauts"
        def file
        TenantUtils.withTenant(1) {
            def msg = new CrmMessage(tenantId: TenantUtils.tenant, locale: "sv", code: "test.integration.shrimp",
                    text: value).save(failOnError: true, flush: true)
            file = crmMessageService.exportToFile()
        }
        assert file != null
        def text = file.text
        assert text.length() > 200 && text.length() < 210 // was 205 bytes on my machine.

        TenantUtils.withTenant(1) {
            file.withInputStream {is ->
                crmMessageService.importText(is, new Locale("no"))
            }
        }

        assert CrmMessage.findByCodeAndLocaleAndTenantId("test.integration.shrimp", "no", 1)?.text == value

        TenantUtils.withTenant(2) {
            file.withInputStream {is ->
                crmMessageService.importText(is, new Locale("sv"))
            }
        }

        assert CrmMessage.findByCodeAndLocaleAndTenantId("test.integration.shrimp", "sv", 2)?.text == value

        file.delete()

    }
}
