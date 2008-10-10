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

import net.sf.cglib.proxy.MethodProxy;
import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.mockbehavior.impl.DefaultValueReturningMockBehavior;
import org.unitils.mock.proxy.ProxyInvocation;
import org.unitils.mock.proxy.ProxyInvocationHandler;
import static org.unitils.mock.proxy.ProxyUtil.createProxy;

import java.lang.reflect.Method;

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
    public static <T> T createDummy(Class<T> type) {
        return createProxy(type, new DummyObjectInvocationHandler());
    }


    /**
     * Invocation handler for the dummy proxy object that will return default values for every invocation.
     */
    public static class DummyObjectInvocationHandler implements ProxyInvocationHandler {

        /* The hash code that is returned when the hashCode method is called */
        private Integer dummyObjectHashCode = new Object().hashCode();

        /* The behavior that will return the default values */
        private MockBehavior dummyObjectBehavior = new DefaultValueReturningMockBehavior();


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
            }
            return dummyObjectBehavior.execute(invocation);
        }


        /**
         * Intercepts the method call.
         *
         * @param proxy       The proxy, not null
         * @param method      The method that was called, not null
         * @param arguments   The arguments that were used, not null
         * @param methodProxy The cglib method proxy, not null
         * @return The value to return for the method call, ignored for void methods
         */
        public Object intercept(Object proxy, Method method, Object[] arguments, MethodProxy methodProxy) throws Throwable {
            if (isEqualsMethod(method)) {
                Object other = arguments[0];
                return proxy == other;
            } else if (isHashCodeMethod(method)) {
                return dummyObjectHashCode;
            }
            return null;
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
    }
}
