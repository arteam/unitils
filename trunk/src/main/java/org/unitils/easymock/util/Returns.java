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
 * Possible values for default return values for non-void method invocations on the mock.
 */
public enum Returns {

    /**
     * Defaults to the value of the org.unitils.easymock.annotation.LenientMock$Returns configuration setting.
     */
    DEFAULT,

    /**
     * Return default values (null, 0…) when no return type is specified.
     */
    NICE,

    /**
     * Throw an exception when no return type is specified
     */
    STRICT
}
