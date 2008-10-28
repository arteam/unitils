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
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import static org.unitils.mock.ArgumentMatchers.notNull;
import static org.unitils.mock.ArgumentMatchers.refEq;
import static org.unitils.mock.argumentmatcher.ArgumentMatcherPositionFinder.getArgumentMatcherIndexes;
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
@SuppressWarnings({"UnusedDeclaration"})
public class ArgumentMatcherPositionFinderTest extends UnitilsJUnit4 {

    /* The line nrs of the proxy method invocations in the TestClass.test method */
    private int invocationLineNr = 151;
    private int noMatcherInvocationLineNr = 153;
    private int doubleInvocationLineNr = 155;
    private int staticInvocationLineNr = 157;
    private int noArgumentsInvocationLineNr = 159;

    /* A regular target method on the proxy */
    private Method proxyMethod;

    /* A static target method on the proxy */
    private Method staticProxyMethod;

    /* A target method without args on the proxy */
    private Method noArgumentsProxyMethod;


    /**
     * Initializes the proxy methods
     */
    @Before
    public void setUp() {
        proxyMethod = getMethod(TestProxy.class, "someMethod", false, String.class, String.class, String.class);
        staticProxyMethod = getMethod(TestProxy.class, "someStaticMethod", true, Integer.TYPE, Integer.TYPE);
        noArgumentsProxyMethod = getMethod(TestProxy.class, "someMethod", false);
    }


    /**
     * Test finding matchers for a proxy method invocation with the first and third arguments being an argument matcher.
     */
    @Test
    public void testGetArgumentMatcherIndexes() {
        List<Integer> result = getArgumentMatcherIndexes(TestClass.class, "test", proxyMethod, invocationLineNr, 1);
        assertReflectionEquals(asList(0, 2), result);
    }


    /**
     * Test finding matchers for a proxy method invocation without argument matcher.
     */
    @Test
    public void testGetArgumentMatcherIndexes_noArgumentMatchers() {
        List<Integer> result = getArgumentMatcherIndexes(TestClass.class, "test", proxyMethod, noMatcherInvocationLineNr, 1);
        assertTrue(result.isEmpty());
    }


    /**
     * Test finding matchers for a proxy method that has no arguments.
     */
    @Test
    public void testGetArgumentMatcherIndexes_noArguments() {
        List<Integer> result = getArgumentMatcherIndexes(TestClass.class, "test", noArgumentsProxyMethod, noArgumentsInvocationLineNr, 1);
        assertTrue(result.isEmpty());
    }


    /**
     * Test finding matchers for two method invocations on the same line. The index determines which one.
     */
    @Test
    public void testGetArgumentMatcherIndexes_twoInvocationsOnSameLine() {
        List<Integer> firstInvocationResult = getArgumentMatcherIndexes(TestClass.class, "test", proxyMethod, doubleInvocationLineNr, 1);
        List<Integer> secondInvocationResult = getArgumentMatcherIndexes(TestClass.class, "test", proxyMethod, doubleInvocationLineNr, 2);

        assertReflectionEquals(asList(0), firstInvocationResult);
        assertReflectionEquals(asList(2), secondInvocationResult);
    }


    /**
     * Test finding matchers for a static proxy method invocation.
     */
    @Test
    public void testGetArgumentMatcherIndexes_staticInvocation() {
        List<Integer> result = getArgumentMatcherIndexes(TestClass.class, "test", staticProxyMethod, staticInvocationLineNr, 1);
        assertReflectionEquals(asList(0), result);
    }


    /**
     * Test for trying to find matchers on a non-existing method.
     */
    @Test(expected = UnitilsException.class)
    public void testGetArgumentMatcherIndexes_wrongMethodName() {
        getArgumentMatcherIndexes(TestClass.class, "xxxx", proxyMethod, invocationLineNr, 1);
    }


    /**
     * Test for trying to find matchers on a wrong line.
     */
    @Test(expected = UnitilsException.class)
    public void testGetArgumentMatcherIndexes_wrongLineNr() {
        List<Integer> result = getArgumentMatcherIndexes(TestClass.class, "test", proxyMethod, invocationLineNr, 9999);
    }


    /**
     * Test class with 5 proxy method invocations.
     */
    public static class TestClass {

        private TestProxy testProxy = new TestProxy();

        public void test() {
            // regular invocation
            testProxy.someMethod(notNull(String.class), "aValue", notNull(String.class));
            // invocation without argument matchers
            testProxy.someMethod("aValue", "aValue", "aValue");
            // 2 invocations on same line  DO NOT FORMAT
            testProxy.someMethod(notNull(String.class), "aValue", "aValue"); testProxy.someMethod("aValue", "aValue", notNull(String.class));
            // static invocation
            TestProxy.someStaticMethod(refEq(1), 33);
            // no arguments invocation
            testProxy.someMethod();
        }
    }


    /**
     * Simulates a proxy
     */
    public static class TestProxy {

        public void someMethod(String value1, String value2, String value3) {
        }

        public static void someStaticMethod(int value1, int value2) {
        }

        public void someMethod() {
        }
    }
}
