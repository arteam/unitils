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

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;
import org.unitils.mock.core.proxy.ProxyInvocation;
import org.unitils.mock.dummy.DummyObjectFactory;

import java.lang.reflect.Method;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class DummyValueReturningMockBehaviorExecuteTest extends UnitilsJUnit4 {

    private DummyValueReturningMockBehavior dummyValueReturningMockBehavior;

    private Mock<DummyObjectFactory> dummyObjectFactoryMock;


    @Before
    public void initialize() {
        dummyValueReturningMockBehavior = new DummyValueReturningMockBehavior(dummyObjectFactoryMock.getMock());
    }


    @Test
    public void defaultValue() throws Exception {
        ProxyInvocation proxyInvocation = createProxyInvocation(TestInterface.class.getMethod("intMethod"));

        Object result = dummyValueReturningMockBehavior.execute(proxyInvocation);
        assertTrue(result instanceof Integer);
        assertEquals(0, ((Integer) result).intValue());
    }

    @Test
    public void stringDefaultsToEmptyString() throws Exception {
        ProxyInvocation proxyInvocation = createProxyInvocation(TestInterface.class.getMethod("stringMethod"));

        Object result = dummyValueReturningMockBehavior.execute(proxyInvocation);
        assertEquals("", result);
    }

    @Test
    public void nullWhenVoidMethod() throws Exception {
        ProxyInvocation proxyInvocation = createProxyInvocation(TestInterface.class.getMethod("voidMethod"));

        Object result = dummyValueReturningMockBehavior.execute(proxyInvocation);
        assertNull(result);
    }

    @Test
    public void nullWhenReturnTypeIsFinalClass() throws Exception {
        ProxyInvocation proxyInvocation = createProxyInvocation(TestInterface.class.getMethod("finalClassMethod"));

        Object result = dummyValueReturningMockBehavior.execute(proxyInvocation);
        assertNull(result);
    }

    @Test
    public void dummyWhenObjectReturnType() throws Exception {
        ProxyInvocation proxyInvocation = createProxyInvocation(TestInterface.class.getMethod("dummyMethod"));
        TestClass dummy = new TestClass();
        dummyObjectFactoryMock.returns(dummy).createDummy(TestClass.class, new DummyValueReturningMockBehavior(dummyObjectFactoryMock.getMock()));

        Object result = dummyValueReturningMockBehavior.execute(proxyInvocation);
        assertSame(dummy, result);
    }

    @Test
    public void sameInstanceIsReturnedForEachCall() throws Exception {
        ProxyInvocation proxyInvocation = createProxyInvocation(TestInterface.class.getMethod("listMethod"));

        Object result1 = dummyValueReturningMockBehavior.execute(proxyInvocation);
        Object result2 = dummyValueReturningMockBehavior.execute(proxyInvocation);
        assertSame(result1, result2);
    }


    private ProxyInvocation createProxyInvocation(Method method) {
        return new ProxyInvocation(null, null, method, emptyList(), emptyList(), null);
    }


    private static interface TestInterface {

        int intMethod();

        void voidMethod();

        String stringMethod();

        FinalClass finalClassMethod();

        List<String> listMethod();

        TestClass dummyMethod();
    }

    private static final class FinalClass {
    }

    private static class TestClass {

        List<String> listMethod() {
            return null;
        }
    }
}
