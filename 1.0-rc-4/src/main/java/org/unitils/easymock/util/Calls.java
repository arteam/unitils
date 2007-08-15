/*
 * Copyright 2006 the original author or authors.
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
 * Possible values for allowing unexpected (non-recorded) method calls on mock objects. When set to lenient, a 'Nice
 * mock' is created, as called in the EasyMock terminology. For non-void methods this means that a Java default (null,
 * 0 or false) is returned.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public enum Calls {

    /**
     * Defaults to the value of the org.unitils.easymock.annotation.LenientMock$Calls configuration setting.
     */
    DEFAULT,

    /**
     * Accept unexpected method calls. Return default values (null, 0…) for unexpected non-void method calls
     */
    LENIENT,

    /**
     * Throw an exception when unexpected method calls occur
     */
    STRICT
}
