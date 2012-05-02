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
package org.unitils.mock.dummy;

import org.unitils.mock.core.proxy.ProxyInvocation;
import org.unitils.mock.core.proxy.ProxyInvocationHandler;
import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.mockbehavior.impl.DummyValueReturningMockBehavior;

import static org.unitils.mock.core.proxy.ProxyFactory.createProxy;

/**
 * Class for handling the dummy object behavior. A dummy object is a proxy that will return default values for every method. This can be
 * used to quickly create test objects without having to worry about correctly filling in every field.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DummyObjectFactory {

    /**
     * Creates the dummy proxy object.
     *
     * @param type The type for the proxy, not null
     * @return The proxy, not null
     */
    public <T> T createDummy(Class<T> type) {
        return createDummy(type, new DummyValueReturningMockBehavior(this));
    }

    public <T> T createDummy(Class<T> type, MockBehavior mockBehaviour) {
        String dummyName = type.getSimpleName();
        return createProxy(dummyName, new DummyObjectInvocationHandler(type, mockBehaviour), type, DummyObject.class, Cloneable.class);
    }


    /**
     * Invocation handler for the dummy proxy object that will return default values for every invocation.
     */
    public static class DummyObjectInvocationHandler implements ProxyInvocationHandler {

        /* The type of the dummy object */
        private Class<?> dummyObjectType;

        /* The hash code that is returned when the hashCode method is called */
        private Integer dummyObjectHashCode = new Object().hashCode();

        /* The behavior that will return the default values */
        private MockBehavior dummyObjectBehavior;


        public DummyObjectInvocationHandler(Class<?> dummyObjectType, MockBehavior mockBehavior) {
            this.dummyObjectType = dummyObjectType;
            this.dummyObjectBehavior = mockBehavior;
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
