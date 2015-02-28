/*
 * Copyright 2014 Goran Ehrsson.
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
 */

package grails.plugins.crm.i18n

/**
 * Tag library for Grails CRM message (i18n) admin.
 */
class CrmMessageTagLib {

    static namespace = "crm"

    /**
     * Render input field for administration of i18n messages.
     * The rendered markup contains Twitter Bootstrap classes.
     * If tag body is specified, it will override the rendered input field.
     *
     * @attr key REQUIRED i18n message key
     * @attr label label above field (plain text or i18n key)
     * @attr maxlength maximum length of input field, default is 255
     */
    def i18nAdminField = {attrs, body ->
        def bodyContent = body().toString().trim()
        def bean = attrs.bean ?: pageScope.properties

        out << """<div class="control-group">
                    <label class="control-label">${message(code: attrs.label, default: attrs.label)}</label>
                    <div class="controls">\n"""

        if (bodyContent) {
            out << bodyContent
        } else {
            out << """<input type="text" maxlength="${attrs.maxlength ?: 255}" name="properties.${attrs.key}"
                        value="${bean[attrs.key] ?: ''}"
                        placeholder="${message(code: attrs.key, default: '')}"
                        class="${attrs['class'] ?: 'span11'}"/>"""
        }

        out << "</div></div>"
    }
}
