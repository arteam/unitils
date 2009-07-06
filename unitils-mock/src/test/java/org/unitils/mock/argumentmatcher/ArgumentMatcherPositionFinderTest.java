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
package org.unitils.mock.argumentmatcher;

import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;
import static org.unitils.mock.ArgumentMatchers.notNull;
import static org.unitils.mock.argumentmatcher.ArgumentMatcherPositionFinder.getArgumentMatcherIndexes;
import org.unitils.mock.core.MockObject;
import org.unitils.mock.dummy.DummyObjectUtil;
import org.unitils.mock.mockbehavior.MockBehavior;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;
import static org.unitils.util.ReflectionUtils.getMethod;

import java.lang.reflect.Method;
import static java.util.Arrays.asList;
import java.util.List;

/**
 * Tests the finding of the argument matchers in a proxy method invocation.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 * @author Kenny Claes
 */
public class ArgumentMatcherPositionFinderTest {

    /* The line nrs of the proxy method invocations in the TestClass.test method */
    private int invocationLineNr = 171;
    private int noMatcherInvocationLineNr = invocationLineNr + 2;
    private int twoSameInvocationsOnSameLineLineNr = invocationLineNr + 4;
    private int twoDifferentInvocationsOnSameLineLineNr = invocationLineNr + 6;
    private int multiLineInvocationLineNrFrom = invocationLineNr + 8;
    private int multiLineInvocationLineNrTo = invocationLineNr + 10;
    private int noArgumentsInvocationLineNr = invocationLineNr + 12;
    private int nestedArgumentMatcherLineNr = invocationLineNr + 14;
    private int nestedMethodInvocationLineNr = invocationLineNr + 16;

    /* A regular target method on the proxy */
    private Method proxyMethod;

    /* A static target method on the proxy */
    private Method staticProxyMethod;

    /* A target method without args on the proxy */
    private Method noArgumentsProxyMethod;

    private Method valueReturningProxyMethod;

    /**
     * Initializes the proxy methods and mock object
     */
    @Before
    public void setUp() {
        proxyMethod = getMethod(MockedClass.class, "someMethod", false, String.class, String.class, String.class);
        staticProxyMethod = getMethod(MockedClass.class, "someStaticMethod", true, Integer.TYPE, Integer.TYPE);
        noArgumentsProxyMethod = getMethod(MockedClass.class, "someMethod", false);
        valueReturningProxyMethod = getMethod(MockedClass.class, "valueReturningMethod", false, String.class, String.class, String.class);
    }


    /**
     * Test finding matchers for a proxy method invocation with the first and third arguments being an argument matcher.
     */
    @Test
    public void testGetArgumentMatcherIndexes() {
        List<Integer> result = getArgumentMatcherIndexes(TestClass.class, "test", proxyMethod, invocationLineNr, invocationLineNr, 1);
        assertReflectionEquals(asList(0, 2), result);
    }


    /**
     * Test finding matchers for a proxy method invocation without argument matcher.
     */
    @Test
    public void noArgumentMatchers() {
        List<Integer> result = getArgumentMatcherIndexes(TestClass.class, "test", proxyMethod, noMatcherInvocationLineNr, noMatcherInvocationLineNr, 1);
        assertTrue(result.isEmpty());
    }


    /**
     * Test finding matchers for a proxy method that has no arguments.
     */
    @Test
    public void noArguments() {
        List<Integer> result = getArgumentMatcherIndexes(TestClass.class, "test", noArgumentsProxyMethod, noArgumentsInvocationLineNr, noArgumentsInvocationLineNr, 1);
        assertTrue(result.isEmpty());
    }


    @Test
    public void twoInvocationsWithSameNameOnSameLine_first() {
        List<Integer> result = getArgumentMatcherIndexes(TestClass.class, "test", proxyMethod, twoSameInvocationsOnSameLineLineNr, twoSameInvocationsOnSameLineLineNr, 1);
        assertReflectionEquals(asList(0), result);
    }


    @Test
    public void twoInvocationsWithSameNameOnSameLine_last() {
        List<Integer> result = getArgumentMatcherIndexes(TestClass.class, "test", proxyMethod, twoSameInvocationsOnSameLineLineNr, twoSameInvocationsOnSameLineLineNr, 2);
        assertReflectionEquals(asList(2), result);
    }


    @Test
    public void twoInvocationsWithDifferentNameOnSameLine_first() {
        List<Integer> result = getArgumentMatcherIndexes(TestClass.class, "test", proxyMethod, twoDifferentInvocationsOnSameLineLineNr, twoDifferentInvocationsOnSameLineLineNr, 1);
        assertReflectionEquals(asList(0), result);
    }


    @Test
    public void twoInvocationsWithDifferentNameOnSameLine_last() {
        List<Integer> result = getArgumentMatcherIndexes(TestClass.class, "test", valueReturningProxyMethod, twoDifferentInvocationsOnSameLineLineNr, twoDifferentInvocationsOnSameLineLineNr, 1);
        assertReflectionEquals(asList(2), result);
    }


    @Test
    public void invocationOnMultipleLines() {
        List<Integer> result = getArgumentMatcherIndexes(TestClass.class, "test", proxyMethod, multiLineInvocationLineNrFrom, multiLineInvocationLineNrTo, 1);
        assertReflectionEquals(asList(0, 2), result);
    }


    @Test(expected = UnitilsException.class)
    public void argumentMatcherInAnExpression() {
        getArgumentMatcherIndexes(TestClass.class, "test", valueReturningProxyMethod, nestedMethodInvocationLineNr, nestedMethodInvocationLineNr, 1);
    }


    @Test(expected = UnitilsException.class)
    public void wrongMethodName() {
        getArgumentMatcherIndexes(TestClass.class, "xxxx", proxyMethod, invocationLineNr, invocationLineNr, 1);
    }


    @Test(expected = UnitilsException.class)
    public void indexNotFound() {
        getArgumentMatcherIndexes(TestClass.class, "test", proxyMethod, invocationLineNr, invocationLineNr, 99);
    }


    /**
     * Test class with 5 proxy method invocations.
     */
    public static class TestClass {

        MockObject<MockedClass> mockObject = new MockObject<MockedClass>("testMock", MockedClass.class, this);

        MockBehavior dummyBehavior = DummyObjectUtil.createDummy(MockBehavior.class);

        public void test() {
            // regular invocation
            mockObject.performs(dummyBehavior).someMethod(notNull(String.class), "aValue", notNull(String.class));
            // invocation without argument matchers
            mockObject.performs(dummyBehavior).someMethod("aValue", "aValue", "aValue");
            // 2 same invocations on same line  DO NOT FORMAT
            mockObject.performs(dummyBehavior).someMethod(notNull(String.class), "aValue", "aValue"); mockObject.performs(dummyBehavior).someMethod("aValue", "aValue", notNull(String.class));
            // 2 different invocations on same line  DO NOT FORMAT
            mockObject.performs(dummyBehavior).someMethod(notNull(String.class), "aValue", "aValue"); mockObject.performs(dummyBehavior).valueReturningMethod("aValue", "aValue", notNull(String.class));
            // Invocation spread over multiple lines
            mockObject.performs(dummyBehavior).someMethod(notNull(String.class),
                    "aValue",
                    notNull(String.class));
            // no arguments invocation
            mockObject.performs(dummyBehavior).someMethod();
            // nested argument matcher
            mockObject.performs(dummyBehavior).someMethod(notNull(String.class), "aValue", "aValue");
            // method also used inside argument expression
            mockObject.performs(dummyBehavior).valueReturningMethod(notNull(String.class), new MockedClass().valueReturningMethod(null, null, null), "aValue");
        }
    }


    /**
     * Simulates a proxy
     */
    public static class MockedClass {

        public void someMethod(String param1, String param2, String param3) {
        }

        public static void someStaticMethod(int param1, int param2) {
        }

        public void someMethod() {
        }

        public String valueReturningMethod(String param1, String param2, String param3) {
            return null;
        }
    }
}
