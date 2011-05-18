/*
 *
 *  * Copyright 2010,  Unitils.org
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package org.unitils.mock.mockbehavior.impl;

import org.unitils.core.UnitilsException;
import org.unitils.mock.core.proxy.ProxyInvocation;

/**
 * Mock behavior that returns a default value if a value can be returned, nothing happens when the result type is void.
 * See {@link org.unitils.mock.mockbehavior.impl.DefaultValueReturningMockBehavior} for more info on the defaults.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class StubMockBehavior extends DefaultValueReturningMockBehavior {

    /**
     * Stub behavior is always allowed.
     *
     * @param proxyInvocation The proxy method invocation, not null
     */
    public void assertCanExecute(ProxyInvocation proxyInvocation) throws UnitilsException {
        // allow void methods
    }

}