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

import org.junit.Before;
import org.junit.Test;
import org.unitils.mock.core.proxy.ProxyInvocation;
import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.mockbehavior.impl.DefaultValueReturningMockBehavior;
import org.unitils.mock.mockbehavior.impl.OriginalBehaviorInvokingMockBehavior;

import java.lang.reflect.Method;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class PartialMockProxyInvocationHandlerGetDefaultMockBehaviorTest {

    private PartialMockProxyInvocationHandler partialMockProxyInvocationHandler;


    @Before
    public void initialize() {
        partialMockProxyInvocationHandler = new PartialMockProxyInvocationHandler(null, null, null, null);
    }


    @Test
    public void originalBehaviorWhenRegularMethod() throws Exception {
        Method method = TestClass.class.getMethod("regularMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, null, method, null, null);

        MockBehavior result = partialMockProxyInvocationHandler.getDefaultMockBehavior(proxyInvocation);
        assertTrue(result instanceof OriginalBehaviorInvokingMockBehavior);
    }

    @Test
    public void defaultValueReturningMockBehaviorWhenAbstractMethodWithReturnValueMethod() throws Exception {
        Method method = TestClass.class.getMethod("returnAbstractMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, null, method, null, null);

        MockBehavior result = partialMockProxyInvocationHandler.getDefaultMockBehavior(proxyInvocation);
        assertTrue(result instanceof DefaultValueReturningMockBehavior);
    }

    @Test
    public void nullWhenVoidAbstractMethod() throws Exception {
        Method method = TestClass.class.getMethod("voidAbstractMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, null, method, null, null);

        MockBehavior result = partialMockProxyInvocationHandler.getDefaultMockBehavior(proxyInvocation);
        assertNull(result);
    }


    private static abstract class TestClass {

        public abstract void voidAbstractMethod();

        public abstract int returnAbstractMethod();

        public void regularMethod() {
        }
    }
}
