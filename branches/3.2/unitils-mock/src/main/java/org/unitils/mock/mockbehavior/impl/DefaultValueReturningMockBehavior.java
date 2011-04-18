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
import org.unitils.mock.core.proxy.ProxyInvocation;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * Mock behavior that returns a default value.
 * <p/>
 * Following defaults are used:
 * <ul>
 * <li>Number values: 0</li>
 * <li>Object values: null</li>
 * <li>Collectionn, arrays etc: empty values</li>
 * </ul>
 * <p/>
 * 
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class DefaultValueReturningMockBehavior implements ValidatableMockBehavior {


    /**
     * Checks whether the mock behavior can be executed for the given invocation. An exception is raised if the method is a void method.
     * 
     * @param proxyInvocation The proxy method invocation, not null
     */
    public void assertCanExecute(ProxyInvocation proxyInvocation) throws UnitilsException {
        Class<?> returnType = proxyInvocation.getMethod().getReturnType();
        if (returnType == Void.TYPE) {
            throw new UnitilsException("Trying to define mock behavior that returns a value for a void method.");
        }
    }


    /**
     * Executes the mock behavior.
     * 
     * @param proxyInvocation The proxy method invocation, not null
     * @return The default value
     */
    @SuppressWarnings("unchecked")
    public Object execute(ProxyInvocation proxyInvocation) {
        Class<?> returnType = proxyInvocation.getMethod().getReturnType();
        if (Number.class.isAssignableFrom(returnType)) {
            return resolveNumber(returnType);
        }
        if (List.class.equals(returnType)) {
            return new ArrayList();
        }
        if (Set.class.equals(returnType)) {
            return new TreeSet();
        }
        if (Map.class.equals(returnType)) {
            return new HashMap();
        }

        if (returnType.isArray()) {
            Array.newInstance(returnType.getComponentType(), 0);
        }
        return null;
    }

    /*
     * checking for the default java implementations of Number, this avoids class cast exceptions when using them
     */
    private Number resolveNumber(Class<?> returnType) {
        Number result = 0;

        if (Integer.class.isAssignableFrom(returnType)) {
            result = 0;
        } else if (Short.class.isAssignableFrom(returnType)) {
            result = (short) 0;
        } else if (BigInteger.class.isAssignableFrom(returnType)) {
            result = BigInteger.ZERO;
        } else if (Long.class.isAssignableFrom(returnType)) {
            result = 0l;
        } else if (BigDecimal.class.isAssignableFrom(returnType)) {
            result = BigDecimal.ZERO;
        } else if (Double.class.isAssignableFrom(returnType)) {
            result = 0d;
        } else if (Byte.class.isAssignableFrom(returnType)) {
            result = (byte) 0;
        } else if (Float.class.isAssignableFrom(returnType)) {
            result = 0f;
        }

        return result;
    }

}