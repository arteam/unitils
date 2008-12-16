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
package org.unitils.mock.dummy;

import java.lang.reflect.Method;

import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.mockbehavior.impl.DefaultValueReturningMockBehavior;
import org.unitils.mock.proxy.ProxyInvocation;
import org.unitils.mock.proxy.ProxyInvocationHandler;
import org.unitils.mock.proxy.ProxyUtil;

/**
 * Class for handling the dummy object behavior. A dummy object is a proxy that will return
 * default values for every method. This can be used to quickly create test objects without
 * having to worry about correctly filling in every field.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DummyObjectUtil {

    /**
     * Creates the dummy proxy object.
     *
     * @param type The type for the proxy, not null
     * @return The proxy, not null
     */
    @SuppressWarnings("unchecked")
    public static <T> T createDummy(Class<T> type) {
        Class<?> superClass;
        Class<?>[] interfaces;
        if (type.isInterface()) {
            superClass = Object.class;
            interfaces = new Class<?>[] {type, DummyObject.class, Cloneable.class};
        } else {
            superClass = type;
            interfaces = new Class<?>[] {DummyObject.class, Cloneable.class};
        }
        return (T) ProxyUtil.createProxy(superClass, interfaces, new DummyObjectInvocationHandler(type));
    }


    /**
     * Invocation handler for the dummy proxy object that will return default values for every invocation.
     */
    public static class DummyObjectInvocationHandler implements ProxyInvocationHandler {

        private Class<?> dummyObjectClass;
        
        /* The hash code that is returned when the hashCode method is called */
        private Integer dummyObjectHashCode = new Object().hashCode();

        /* The behavior that will return the default values */
        private MockBehavior dummyObjectBehavior = new DefaultValueReturningMockBehavior();

        /**
         * @param dummyObjectClass
         */
        public DummyObjectInvocationHandler(Class<?> dummyObjectClass) {
            this.dummyObjectClass = dummyObjectClass;
        }

        /**
         * Handles the given method invocation of the dummy object.
         *
         * @param invocation The method invocation, not null
         * @return The result value for the method invocation
         */
        public Object handleInvocation(ProxyInvocation invocation) throws Throwable {
            if (isEqualsMethod(invocation.getMethod())) {
                Object other = invocation.getArguments().get(0);
                return invocation.getProxy() == other;
            } else if (isHashCodeMethod(invocation.getMethod())) {
                return dummyObjectHashCode;
            } else if (isCloneMethod(invocation.getMethod())) {
                return invocation.getProxy();
            } else if (isToStringMethod(invocation.getMethod()) || isFormatAdviseFormatMethod(invocation.getMethod())) {
                return "DUMMY " + dummyObjectClass.getSimpleName() + "@" + Integer.toHexString(dummyObjectHashCode);
            }
            return dummyObjectBehavior.execute(invocation);
        }



        /**
         * @param method The method to check, not null
         * @return True if the given method is the equals method
         */
        protected boolean isEqualsMethod(Method method) {
            return "equals".equals(method.getName())
                    && 1 == method.getParameterTypes().length
                    && Object.class.equals(method.getParameterTypes()[0]);
        }


        /**
         * @param method The method to check, not null
         * @return True if the given method is the equals method
         */
        protected boolean isHashCodeMethod(Method method) {
            return "hashCode".equals(method.getName())
                    && 0 == method.getParameterTypes().length;
        }
        protected boolean isToStringMethod(Method method) {
            return "toString".equals(method.getName())
                    && 0 == method.getParameterTypes().length;
        }
        
        protected boolean isCloneMethod(Method method) {
            return "clone".equals(method.getName()) 
                    && 0 == method.getParameterTypes().length;
        }
        
        protected boolean isFormatAdviseFormatMethod(Method method) {
            return "$_format".equals(method.getName())
            && 0 == method.getParameterTypes().length;
        }
    }
}
