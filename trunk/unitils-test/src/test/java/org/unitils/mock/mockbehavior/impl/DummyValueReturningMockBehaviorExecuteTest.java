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
import org.unitils.mock.core.MockFactory;
import org.unitils.mock.core.proxy.Argument;
import org.unitils.mock.core.proxy.ProxyInvocation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.unitils.mock.ArgumentMatchers.isNull;

/**
 * @author Tim Ducheyne
 */
public class DummyValueReturningMockBehaviorExecuteTest extends UnitilsJUnit4 {

    private DummyValueReturningMockBehavior dummyValueReturningMockBehavior;

    private Mock<MockFactory> mockFactoryMock;


    @Before
    public void initialize() {
        dummyValueReturningMockBehavior = new DummyValueReturningMockBehavior(mockFactoryMock.getMock());
    }


    @Test
    public void defaultValue() throws Exception {
        Method method = TestInterface.class.getMethod("intMethod");
        ProxyInvocation proxyInvocation = createProxyInvocation(method);

        Object result = dummyValueReturningMockBehavior.execute(proxyInvocation);
        assertTrue(result instanceof Integer);
        assertEquals(0, ((Integer) result).intValue());
    }

    @Test
    public void stringDefaultsToEmptyString() throws Exception {
        Method method = TestInterface.class.getMethod("stringMethod");
        ProxyInvocation proxyInvocation = createProxyInvocation(method);

        Object result = dummyValueReturningMockBehavior.execute(proxyInvocation);
        assertEquals("", result);
    }

    @Test
    public void nullWhenVoidMethod() throws Exception {
        Method method = TestInterface.class.getMethod("voidMethod");
        ProxyInvocation proxyInvocation = createProxyInvocation(method);

        Object result = dummyValueReturningMockBehavior.execute(proxyInvocation);
        assertNull(result);
    }

    @Test
    public void nullWhenReturnTypeIsFinalClass() throws Exception {
        Method method = TestInterface.class.getMethod("finalClassMethod");
        ProxyInvocation proxyInvocation = createProxyInvocation(method);

        Object result = dummyValueReturningMockBehavior.execute(proxyInvocation);
        assertNull(result);
    }

    @Test
    public void dummyWhenObjectReturnType() throws Exception {
        Method method = TestInterface.class.getMethod("dummyMethod");
        ProxyInvocation proxyInvocation = createProxyInvocation(method);
        TestClass dummy = new TestClass();
        mockFactoryMock.returns(dummy).createDummy(isNull(String.class), TestClass.class);

        Object result = dummyValueReturningMockBehavior.execute(proxyInvocation);
        assertSame(dummy, result);
    }

    @Test
    public void sameInstanceIsReturnedForEachCall() throws Exception {
        Method method = TestInterface.class.getMethod("listMethod");
        ProxyInvocation proxyInvocation = createProxyInvocation(method);

        Object result1 = dummyValueReturningMockBehavior.execute(proxyInvocation);
        Object result2 = dummyValueReturningMockBehavior.execute(proxyInvocation);
        assertSame(result1, result2);
    }


    private ProxyInvocation createProxyInvocation(Method method) {
        List<Argument<?>> arguments = new ArrayList<Argument<?>>();
        return new ProxyInvocation(null, null, method, arguments, null);
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
