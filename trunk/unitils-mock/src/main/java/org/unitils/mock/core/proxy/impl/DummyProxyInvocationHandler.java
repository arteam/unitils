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
package org.unitils.mock.core.proxy.impl;

import org.unitils.mock.core.proxy.ProxyInvocation;
import org.unitils.mock.core.proxy.ProxyInvocationHandler;
import org.unitils.mock.mockbehavior.MockBehavior;

/**
 * @author Tim Ducheyne
 */
public class DummyProxyInvocationHandler implements ProxyInvocationHandler {

    /* The type of the dummy object */
    protected Class<?> dummyObjectType;
    /* The hash code that is returned when the hashCode method is called */
    protected Integer dummyObjectHashCode = new Object().hashCode();
    /* The behavior that will return the default values */
    protected MockBehavior dummyObjectBehavior;


    public DummyProxyInvocationHandler(Class<?> dummyObjectType, MockBehavior mockBehavior) {
        this.dummyObjectType = dummyObjectType;
        this.dummyObjectBehavior = mockBehavior;
    }

    public Object handleInvocation(ProxyInvocation invocation) throws Throwable {
        return dummyObjectBehavior.execute(invocation);
    }
}
