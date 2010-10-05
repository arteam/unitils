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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.unitils.mock.core.proxy.ProxyInvocation;
import org.unitils.mock.dummy.DummyObjectUtil;

/**
 * Mock behavior that returns a default value. The default value for an object will be a proxy object with the same behaviour. When doing
 * the same call multiple times the same 'proxy' object will be returned each time. Doing the same method call with other parameters will
 * howerver result a new object returned.
 * <p/>
 * Following defaults are used:
 * <ul>
 * <li>Number values: 0</li>
 * <li>Object values: DummyObject</li>
 * <li>Collection: DummyObject</li>
 * <li>arrays etc: empty values</li>
 * </ul>
 * <p/>
 * 
 * @author Jeroen Horemans
 */
public class DummyValueReturningMockBehavior extends DefaultValueReturningMockBehavior {

    /*
     * this list keeps track of what object we have returned. So that we return the "same" instance on each invocation
     */
    private Map<MethodKey, Object> returnValues = new HashMap<MethodKey, Object>();

    /**
     * Executes the mock behavior.
     * 
     * @param proxyInvocation The proxy method invocation, not null
     * @return The default value defined by this behavior
     */
    public Object execute(ProxyInvocation proxyInvocation) {
        Object result = null;

        Method method = proxyInvocation.getMethod();
        Class<?> returnType = method.getReturnType();
        MethodKey key = new MethodKey(method.getName(), proxyInvocation.getArguments());
        if (returnValues.containsKey(key)) {
            result = returnValues.get(key);
        }

        if (result == null) {
            result = super.execute(proxyInvocation);
        }
        if (result == null && String.class.equals(returnType)) {
            result = "";
        }

        if (result == null && isDummyProof(returnType)) {
            result = DummyObjectUtil.createDummy(returnType, new DummyValueReturningMockBehavior());
        }
        returnValues.put(key, result);

        return result;
    }


    private boolean isDummyProof(Class<?> returnType) {
        return !returnType.isPrimitive() && !(returnType == Void.TYPE) && !Modifier.isFinal(returnType.getModifiers());
    }

    private class MethodKey {

        private String methodName;

        private List<Object> arguments;

        public MethodKey(String methodName, List<Object> arguments) {
            this.methodName = methodName;
            this.arguments = arguments;
        }

        @Override
        public int hashCode() {
            return methodName.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            MethodKey other = (MethodKey) obj;
            if (methodName == null) {
                if (other.methodName != null) {
                    return false;
                }
            } else if (!methodName.equals(other.methodName)) {
                return false;
            }
            if (arguments.size() != other.arguments.size()) {
                return false;
            }
            if (arguments.size() > 0) {
                Iterator<Object> it2 = other.arguments.iterator();
                for (Iterator<Object> it1 = arguments.iterator(); it1.hasNext();) {
                    Object object1 = it1.next();
                    Object object2 = it2.next();
                    if (object1 == null && object2 == null) {
                        continue;
                    } else if (object1 != null && !object1.equals(object2)) {
                        return false;
                    }
                }
            }
            return true;
        }


    }

}
