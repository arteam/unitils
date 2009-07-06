/*
 * Copyright 2008,  Unitils.org
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
package org.unitils.core.util;

/**
 * Possible values for the stored identifier case.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public enum StoredIdentifierCase {

    /**
     * The database stores unquoted identifiers as lower case
     */
    LOWER_CASE,

    /**
     * The database stores unquoted identifiers as upper case
     */
    UPPER_CASE,

    /**
     * The database stores unquoted identifiers as case sensitive
     */
    MIXED_CASE
}
