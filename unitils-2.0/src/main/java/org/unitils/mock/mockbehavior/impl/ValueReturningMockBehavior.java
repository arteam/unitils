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
import static org.unitils.util.ReflectionUtils.isAssignable;

/**
 * Mock behavior that returns a given value.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class ValueReturningMockBehavior implements ValidatableMockBehavior {

    /* The value to return */
    private Object valueToReturn;


    /**
     * Creates the returning behavior for the given value.
     *
     * @param valueToReturn The value
     */
    public ValueReturningMockBehavior(Object valueToReturn) {
        this.valueToReturn = valueToReturn;
    }


    /**
     * Checks whether the mock behavior can be executed for the given invocation.
     * An exception is raised if the method is a void method or has a non-assignable return type.
     *
     * @param proxyInvocation The proxy method invocation, not null
     */
    public void assertCanExecute(ProxyInvocation proxyInvocation) throws UnitilsException {
        Class<?> returnType = proxyInvocation.getMethod().getReturnType();
        if (returnType == Void.TYPE) {
            throw new UnitilsException("Trying to make a void method return a value");
        }
        if (valueToReturn != null && !isAssignable(valueToReturn.getClass(), returnType)) {
            throw new UnitilsException("Trying to make a method return a value who's type is not compatible with the return type. Value type: " + valueToReturn.getClass() + ", return type: " + returnType);
        }
    }


    /**
     * Executes the mock behavior.
     *
     * @param proxyInvocation The proxy method invocation, not null
     * @return The value
     */
    public Object execute(ProxyInvocation proxyInvocation) {
        return valueToReturn;
    }

}
