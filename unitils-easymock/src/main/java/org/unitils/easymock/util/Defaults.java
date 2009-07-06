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
package org.unitils.easymock.util;

/**
 * Possible values for checking arguments with default values.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public enum Defaults {

    /**
     * Defaults to the value of the org.unitils.easymock.annotation.LenientMock$Defaults configuration setting.
     */
    DEFAULT,


    /**
     * All arguments that have default values as expected values will not be checked. E.g. if a null value is recorded
     * as argument it will not be checked when the actual invocation occurs. The same applies for inner-fields of
     * object arguments that contain default java values.
     */
    IGNORE_DEFAULTS,

    /**
     * Arguments with default values will also be checked.
     */
    STRICT
}