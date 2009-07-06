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

public enum Order {

    /**
     * Defaults to the value of the org.unitils.easymock.annotation.LenientMock$Order configuration setting.
     */
    DEFAULT,


    /**
     * The actual order of collections and arrays arguments and inner fields of arguments are ignored. It will
     * only check whether they both contain the same elements.
     */
    LENIENT,

    /**
     * The order of collectios will be checked.
     */
    STRICT
}

