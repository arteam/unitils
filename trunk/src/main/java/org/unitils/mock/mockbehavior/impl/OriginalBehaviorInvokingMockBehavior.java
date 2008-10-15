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
package org.unitils.mock.mockbehavior.impl;

import org.unitils.core.UnitilsException;
import org.unitils.mock.mockbehavior.ValidatableMockBehavior;
import org.unitils.mock.proxy.ProxyInvocation;

import static java.lang.reflect.Modifier.isAbstract;

/**
 * Mock behavior that, instead of mocking the method invocation, performs the actual behavior of the mocked class.
 * This is used to implement the partial mock behavior.
 *
 * If there is no original behavior, e.g. mocking of an interface or abstract method, an exception is raised.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class OriginalBehaviorInvokingMockBehavior implements ValidatableMockBehavior {

    /**
     * Checks whether the mock behavior can be executed for the given invocation.
     * An exception is raised if the method is a void method or has a non-assignable return type.
     *
     * @param proxyInvocation The proxy method invocation, not null
     */
    public void assertCanExecute(ProxyInvocation proxyInvocation) throws UnitilsException {
        if (isAbstract(proxyInvocation.getMethod().getModifiers())) {
            throw new UnitilsException("Trying to define mock behavior that invokes the original method behavior for an abstract method.");
        }
    }


    /**
     * Executes the mock behavior.
     *
     * @param proxyInvocation The proxy method invocation, not null
     * @return The result value
     */
    public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
        return proxyInvocation.invokeOriginalBehavior();
    }

}