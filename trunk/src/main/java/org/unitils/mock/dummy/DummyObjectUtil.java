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

import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.mockbehavior.impl.DefaultValueReturningMockBehavior;
import org.unitils.mock.proxy.ProxyInvocation;
import org.unitils.mock.proxy.ProxyInvocationHandler;
import static org.unitils.mock.proxy.ProxyUtils.createProxy;

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
        return createProxy("todo", type, new Class<?>[]{DummyObject.class, Cloneable.class}, new DummyObjectInvocationHandler(type));
    }                              //todo mock name


    /**
     * Invocation handler for the dummy proxy object that will return default values for every invocation.
     */
    public static class DummyObjectInvocationHandler implements ProxyInvocationHandler {

        /* The type of the dummy object */
        private Class<?> dummyObjectType;

        /* The hash code that is returned when the hashCode method is called */
        private Integer dummyObjectHashCode = new Object().hashCode();

        /* The behavior that will return the default values */
        private MockBehavior dummyObjectBehavior = new DefaultValueReturningMockBehavior();


        public DummyObjectInvocationHandler(Class<?> dummyObjectType) {
            this.dummyObjectType = dummyObjectType;
        }

        /**
         * Handles the given method invocation of the dummy object.
         *
         * @param invocation The method invocation, not null
         * @return The result value for the method invocation
         */
        public Object handleInvocation(ProxyInvocation invocation) throws Throwable {
            return dummyObjectBehavior.execute(invocation);
        }

    }
}
