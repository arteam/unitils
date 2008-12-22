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
package org.unitils.mock.mockbehavior;

import org.unitils.core.UnitilsException;
import org.unitils.mock.proxy.ProxyInvocation;


/**
 * todo javadoc
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public interface ValidatableMockBehavior extends MockBehavior {


    /**
     * Checks whether the mock behavior can be executed for the given invocation.
     * An exception should be raised if this behavior is not suited for the given invocation.
     *
     * @param proxyInvocation The proxy method invocation, not null
     */
    void assertCanExecute(ProxyInvocation proxyInvocation) throws UnitilsException;

}