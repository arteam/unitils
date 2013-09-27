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
package org.unitils.mock.core;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;
import org.unitils.mock.core.proxy.ProxyInvocation;
import org.unitils.mock.core.util.CloneService;
import org.unitils.mock.core.util.ObjectFactory;
import org.unitils.mock.mockbehavior.MockBehavior;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;
import static org.unitils.mock.ArgumentMatchers.notNull;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class MockIntegrationTest extends UnitilsJUnit4 {

    private Mock<TestInterface> mockObject;
    private Mock<TestInterface> equalMockObject;


    @Test
    public void testInvocationSpreadOverMoreThanOneLine() {
        TestMockBehavior testMockBehavior = new TestMockBehavior();

        mockObject.performs( //
                testMockBehavior
        ).      //
                testMethod(//
                notNull(//
                        String.class//
                ));

        mockObject. //
                getMock().//
                testMethod(//
                null);
        assertEquals(0, testMockBehavior.invocationCount);

        mockObject.getMock().//
                testMethod( //
                "value");
        assertEquals(1, testMockBehavior.invocationCount);
    }

    @Test
    public void invokedMethodOnDifferentLine() throws Exception {
        mockObject.returns(null).//
                testMethod(null);
    }

    @Test
    public void testEquals() {
        assertTrue(mockObject.getMock().equals(mockObject.getMock()));
        assertFalse(mockObject.getMock().equals(equalMockObject.getMock()));
    }

    @Test
    public void testHashcode_equalObjects() {
        assertTrue(mockObject.getMock().hashCode() == mockObject.getMock().hashCode());
    }

    @Test
    public void testHashcode_differentObjects() {
        assertFalse(mockObject.getMock().hashCode() == equalMockObject.getMock().hashCode());
    }

    @Test
    public void testDeepCloneEqualToOriginal() {
        CloneService cloneService = new CloneService(new ObjectFactory());

        assertTrue(mockObject.getMock() instanceof Cloneable);
        TestInterface clone = cloneService.createDeepClone(mockObject.getMock());
        assertEquals(mockObject.getMock(), clone);
    }


    @Test
    public void useRawTypeWhenMockingGenericTypes() {
        // todo td implement
//        MockObject<List<String>> mockObject = new MockObject<List<String>>("testMock", List.class, this);
        MockObject<List<String>> mockObject = null;

        mockObject.returns("value").get(0);
        assertEquals("value", mockObject.getMock().get(0));
    }


    @Test(expected = ClassCastException.class)
    public void typeMismatch() {
        // todo td implement
//        MockObject<List<String>> mockObject = new MockObject<List<String>>("testMock", Map.class, this);
        MockObject<List<String>> mockObject = null;
        mockObject.returns("value").get(0);  //raises classcast
    }

    @Test
    public void defaultMockName() {
        // todo td implement
//        MockObject<TestInterface> mockObject = new MockObject<TestInterface>(TestInterface.class, this);
        MockObject<TestInterface> mockObject = null;
        assertEquals("testClassMock", mockObject.getName());
    }

    @Test
    public void proxyArgumentsAndResult() {
        Object proxy = Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{Collection.class}, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return null;
            }
        });

        // todo td implement
//        MockObject<TestInterface> mockObject = new MockObject<TestInterface>(TestInterface.class, this);
        MockObject<TestInterface> mockObject = null;
        mockObject.returns(proxy).doSomething(proxy);

        Object result = mockObject.getMock().doSomething(proxy);
        assertSame(proxy, result);
    }


    private static interface TestInterface {

        String testMethod(String arg);

        int[] testMethodArray();

        Object clone() throws CloneNotSupportedException;

        Object doSomething(Object proxy);
    }

    private static class TestMockBehavior implements MockBehavior {

        public int invocationCount = 0;

        public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
            invocationCount++;
            return null;
        }
    }
}
