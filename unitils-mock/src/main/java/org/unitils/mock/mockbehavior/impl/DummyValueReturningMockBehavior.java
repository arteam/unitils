/*
 * Copyright 2013,  Unitils.org
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

import org.unitils.mock.core.DummyFactory;
import org.unitils.mock.core.proxy.ProxyInvocation;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.reflect.Modifier.isFinal;

/**
 * Mock behavior that returns a default value. The default value for an object will be a proxy object with the same behaviour. When doing
 * the same call multiple times the same 'proxy' object will be returned each time. Doing the same method call with other parameters will
 * however result a new object returned.
 * <p/>
 * Following defaults are used:
 * <ul>
 * <li>Number values: 0</li>
 * <li>String values: ""</li>
 * <li>Object values: DummyObject</li>
 * <li>Collection: empty list</li>
 * <li>Arrays: empty array</li>
 * </ul>
 * <p/>
 *
 * @author Tim Ducheyne
 * @author Jeroen Horemans
 */
public class DummyValueReturningMockBehavior extends DefaultValueReturningMockBehavior {

    protected DummyFactory dummyFactory;
    /* Keeps track of what object we have returned, so that we return the "same" instance on each invocation */
    protected Map<MethodKey, Object> returnValues = new HashMap<MethodKey, Object>();


    public DummyValueReturningMockBehavior(DummyFactory dummyFactory) {
        this.dummyFactory = dummyFactory;
    }


    /**
     * Executes the mock behavior.
     *
     * @param proxyInvocation The proxy method invocation, not null
     * @return The default value defined by this behavior
     */
    public Object execute(ProxyInvocation proxyInvocation) {
        Method method = proxyInvocation.getMethod();
        Class<?> returnType = method.getReturnType();
        List<?> argumentValues = proxyInvocation.getArgumentValues();
        MethodKey key = new MethodKey(method.getName(), argumentValues);

        if (returnValues.containsKey(key)) {
            return returnValues.get(key);
        }
        Object result = getReturnValue(proxyInvocation, returnType);
        returnValues.put(key, result);
        return result;
    }


    protected Object getReturnValue(ProxyInvocation proxyInvocation, Class<?> returnType) {
        Object result = super.execute(proxyInvocation);
        if (result != null) {
            return result;
        }
        if (String.class.equals(returnType)) {
            return "";
        }
        if (cannotCreateDummy(returnType)) {
            return null;
        }
        return dummyFactory.createDummy(null, returnType);
    }

    protected boolean cannotCreateDummy(Class<?> returnType) {
        return returnType == Void.TYPE || isFinal(returnType.getModifiers());
    }


    protected static class MethodKey {

        protected String methodName;
        protected List<?> arguments;


        public MethodKey(String methodName, List<?> arguments) {
            this.methodName = methodName;
            this.arguments = arguments;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            MethodKey methodKey = (MethodKey) o;
            if (arguments != null ? !arguments.equals(methodKey.arguments) : methodKey.arguments != null) {
                return false;
            }
            if (methodName != null ? !methodName.equals(methodKey.methodName) : methodKey.methodName != null) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int result = methodName != null ? methodName.hashCode() : 0;
            result = 31 * result + (arguments != null ? arguments.hashCode() : 0);
            return result;
        }
    }
}
