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

import groovy.transform.CompileStatic

/**
 *
 * @author Goran Ehrsson
 * @since 0.1
 */
//@Immutable
@CompileStatic
final class MessageKey implements Serializable {
    final Long tenant
    final String code
    final Locale locale

    MessageKey(Long tenant, String code, Locale locale) {
        this.tenant = tenant
        this.code = code
        this.locale = locale
    }

    boolean equals(o) {
        if (this.is(o)) return true;
        if (!(o instanceof MessageKey)) return false;

        MessageKey that = (MessageKey) o;

        if (code != that.code) return false;
        if (locale != that.locale) return false;
        if (tenant != that.tenant) return false;

        return true;
    }

    int hashCode() {
        int result;
        result = (tenant != null ? tenant.hashCode() : 0);
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (locale != null ? locale.hashCode() : 0);
        return result;
    }
}


