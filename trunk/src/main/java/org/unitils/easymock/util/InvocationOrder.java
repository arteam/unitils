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
 * Possible values for checking the order of method invocation on the mock.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public enum InvocationOrder {


    /**
     * NONE or STRICT as defined by the default value in the configuration.
     */
    DEFAULT,

    /**
     * No order checking of method invocations.
     */
    NONE,

    /**
     * Strict order checking of method invocations.
     */
    STRICT

}
