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

import org.junit.Test;
import org.unitils.core.util.ObjectToInjectHolder;
import org.unitils.mock.core.proxy.ProxyInvocation;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Tim Ducheyne
 */
public class ValueReturningMockBehaviorExecuteTest {

    private ValueReturningMockBehavior valueReturningMockBehavior;


    @Test
    public void canExecute() throws Exception {
        valueReturningMockBehavior = new ValueReturningMockBehavior("test");

        ProxyInvocation proxyInvocation = createProxyInvocation(MyInterface.class.getMethod("method"));
        Object result = valueReturningMockBehavior.execute(proxyInvocation);
        assertEquals("test", result);
    }

    @Test
    public void nullReturnValue() throws Exception {
        valueReturningMockBehavior = new ValueReturningMockBehavior(null);

        ProxyInvocation proxyInvocation = createProxyInvocation(MyInterface.class.getMethod("method"));
        Object result = valueReturningMockBehavior.execute(proxyInvocation);
        assertNull(result);
    }


    @Test
    public void unwrapWrappedTypes() throws Exception {
        valueReturningMockBehavior = new ValueReturningMockBehavior(new MyWrapper());

        ProxyInvocation proxyInvocation = createProxyInvocation(MyInterface.class.getMethod("wrapperMethod"));
        Object result = valueReturningMockBehavior.execute(proxyInvocation);
        assertEquals("object to inject", result);
    }


    private ProxyInvocation createProxyInvocation(Method method) {
        return new ProxyInvocation(null, null, method, emptyList(), emptyList(), null);
    }


    private static interface MyInterface {

        String method();

        MyWrapper wrapperMethod();
    }

    private static class MyWrapper implements ObjectToInjectHolder<String> {

        public String getObjectToInject() {
            return "object to inject";
        }

        public Type getObjectToInjectType(Type declaredType) {
            return String.class;
        }
    }
}

