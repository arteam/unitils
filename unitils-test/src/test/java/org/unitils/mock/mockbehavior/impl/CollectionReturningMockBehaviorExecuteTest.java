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
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;

/**
 * @author Tim Ducheyne
 */
public class CollectionReturningMockBehaviorExecuteTest {

    private CollectionReturningMockBehavior collectionReturningMockBehavior;


    @Test
    public void listMethod() throws Exception {
        Method method = MyInterface.class.getMethod("listMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, method, null, null);
        collectionReturningMockBehavior = new CollectionReturningMockBehavior("1", "2");

        Object result = collectionReturningMockBehavior.execute(proxyInvocation);
        assertTrue(result instanceof List);
        assertEquals(asList("1", "2"), result);
    }

    @Test
    public void setMethod() throws Exception {
        Method method = MyInterface.class.getMethod("setMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, method, null, null);
        collectionReturningMockBehavior = new CollectionReturningMockBehavior("1", "2", "1", "2");

        Object result = collectionReturningMockBehavior.execute(proxyInvocation);
        assertTrue(result instanceof Set);
        assertLenientEquals(asList("1", "2"), result);
    }

    @Test
    public void arrayMethod() throws Exception {
        Method method = MyInterface.class.getMethod("arrayMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, method, null, null);
        collectionReturningMockBehavior = new CollectionReturningMockBehavior("1", "2");

        Object result = collectionReturningMockBehavior.execute(proxyInvocation);
        assertTrue(result instanceof String[]);
        assertArrayEquals(new String[]{"1", "2"}, (String[]) result);
    }

    @Test
    public void nullWhenNullValue() throws Exception {
        Method method = MyInterface.class.getMethod("listMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, method, null, null);
        collectionReturningMockBehavior = new CollectionReturningMockBehavior(null);

        Object result = collectionReturningMockBehavior.execute(proxyInvocation);
        assertNull(result);
    }

    @Test
    public void unwrapWrappedTypes() throws Exception {
        Method method = MyInterface.class.getMethod("listMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, method, null, null);
        collectionReturningMockBehavior = new CollectionReturningMockBehavior(new MyWrapper("1"), new MyWrapper("2"));

        Object result = collectionReturningMockBehavior.execute(proxyInvocation);
        assertEquals(asList("1", "2"), result);
    }


    private static interface MyInterface {

        List<String> listMethod();

        Set<String> setMethod();

        String[] arrayMethod();
    }

    private static class MyWrapper implements ObjectToInjectHolder<String> {

        private String value;

        private MyWrapper(String value) {
            this.value = value;
        }

        public String getObjectToInject() {
            return value;
        }

        public Type getObjectToInjectType(Type declaredType) {
            return String.class;
        }
    }
}

