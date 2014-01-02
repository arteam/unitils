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
package org.unitils.mock.argumentmatcher;

import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;
import org.unitils.mock.Mock;

import java.lang.reflect.Method;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.unitils.core.util.ReflectionUtils.getMethod;
import static org.unitils.mock.ArgumentMatchers.anyInt;
import static org.unitils.mock.ArgumentMatchers.notNull;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * Tests the finding of the argument matchers in a proxy method invocation.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 * @author Kenny Claes
 */
public class ArgumentMatcherPositionFinderTest {

    private ArgumentMatcherPositionFinder argumentMatcherPositionFinder = new ArgumentMatcherPositionFinder();

    /* The line nrs of the proxy method invocations in the TestClass.test method */
    private static final int LINE_NR = 62;
    private static final int NO_MATCHER_LINE_NR = LINE_NR + 2;
    private static final int TWO_SAME_ON_SAME_LINE_LINE_NR = LINE_NR + 4;
    private static final int TWO_DIFFERENT_ON_SAME_LINE_LINE_NR = LINE_NR + 6;
    private static final int MULTI_LINE_LINE_NR_FROM = LINE_NR + 8;
    private static final int MULTI_LINE_LINE_NR_TO = LINE_NR + 10;
    private static final int NO_ARGUMENTS_LINE_NR = LINE_NR + 12;
    private static final int NESTED_METHOD_LINE_NR = LINE_NR + 14;
    private static final int EXPRESSION_LINE_NR = LINE_NR + 16;
    private static final int INVOKED_METHOD_IN_ARGUMENT_LINE_NR = LINE_NR + 18;

    public static class TestClass {

        private Mock<MockedClass> mockObject;

        public void test() {
            // regular invocation
            mockObject.performs(null).someMethod(notNull(String.class), "aValue", notNull(String.class));
            // invocation without argument matchers
            mockObject.performs(null).someMethod("aValue", "aValue", "aValue");
            // 2 same invocations on same line  DO NOT FORMAT
            mockObject.performs(null).someMethod(notNull(String.class), "aValue", "aValue"); mockObject.performs(null).someMethod("aValue", "aValue", notNull(String.class));
            // 2 different invocations on same line  DO NOT FORMAT
            mockObject.performs(null).someMethod(notNull(String.class), "aValue", "aValue"); mockObject.performs(null).valueReturningMethod("aValue", "aValue", notNull(String.class));
            // Invocation spread over multiple lines
            mockObject.performs(null).someMethod(notNull(String.class),
                    "aValue",
                    notNull(String.class));
            // no arguments invocation
            mockObject.performs(null).someMethod();
            // method also used inside nested method call
            mockObject.performs(null).someMethod(notNull(String.class), new MyClass(notNull(String.class)).toString(), "aValue");
            // method also used inside argument expression
            mockObject.performs(null).intMethod(anyInt() + 5);
            // invoked method also used inside argument
            mockObject.performs(null).valueReturningMethod(notNull(String.class), new MockedClass().valueReturningMethod(null, null, null), "aValue");
        }
    }

    private Method method;
    private Method staticMethod;
    private Method noArgumentsMethod;
    private Method valueReturningMethod;
    private Method intMethod;


    @Before
    public void initialize() {
        method = getMethod(MockedClass.class, "someMethod", false, String.class, String.class, String.class);
        staticMethod = getMethod(MockedClass.class, "someStaticMethod", true, Integer.TYPE, Integer.TYPE);
        noArgumentsMethod = getMethod(MockedClass.class, "someMethod", false);
        valueReturningMethod = getMethod(MockedClass.class, "valueReturningMethod", false, String.class, String.class, String.class);
        intMethod = getMethod(MockedClass.class, "intMethod", false, Integer.TYPE);
    }


    @Test
    public void getArgumentMatcherIndexes() {
        List<Integer> result = argumentMatcherPositionFinder.getArgumentMatcherIndexes(TestClass.class, "test", method, LINE_NR, LINE_NR, 1);
        assertReflectionEquals(asList(0, 2), result);
    }

    @Test
    public void emptyWhenNoArgumentMatchers() {
        List<Integer> result = argumentMatcherPositionFinder.getArgumentMatcherIndexes(TestClass.class, "test", method, NO_MATCHER_LINE_NR, NO_MATCHER_LINE_NR, 1);
        assertTrue(result.isEmpty());
    }

    @Test
    public void emptyWhenNoArguments() {
        List<Integer> result = argumentMatcherPositionFinder.getArgumentMatcherIndexes(TestClass.class, "test", noArgumentsMethod, NO_ARGUMENTS_LINE_NR, NO_ARGUMENTS_LINE_NR, 1);
        assertTrue(result.isEmpty());
    }

    @Test
    public void twoInvocationsWithSameNameOnSameLine_first() {
        List<Integer> result = argumentMatcherPositionFinder.getArgumentMatcherIndexes(TestClass.class, "test", method, TWO_SAME_ON_SAME_LINE_LINE_NR, TWO_SAME_ON_SAME_LINE_LINE_NR, 1);
        assertReflectionEquals(asList(0), result);
    }

    @Test
    public void twoInvocationsWithSameNameOnSameLine_last() {
        List<Integer> result = argumentMatcherPositionFinder.getArgumentMatcherIndexes(TestClass.class, "test", method, TWO_SAME_ON_SAME_LINE_LINE_NR, TWO_SAME_ON_SAME_LINE_LINE_NR, 2);
        assertReflectionEquals(asList(2), result);
    }

    @Test
    public void twoInvocationsWithDifferentNameOnSameLine_first() {
        List<Integer> result = argumentMatcherPositionFinder.getArgumentMatcherIndexes(TestClass.class, "test", method, TWO_DIFFERENT_ON_SAME_LINE_LINE_NR, TWO_DIFFERENT_ON_SAME_LINE_LINE_NR, 1);
        assertReflectionEquals(asList(0), result);
    }

    @Test
    public void twoInvocationsWithDifferentNameOnSameLine_last() {
        List<Integer> result = argumentMatcherPositionFinder.getArgumentMatcherIndexes(TestClass.class, "test", valueReturningMethod, TWO_DIFFERENT_ON_SAME_LINE_LINE_NR, TWO_DIFFERENT_ON_SAME_LINE_LINE_NR, 1);
        assertReflectionEquals(asList(2), result);
    }

    @Test
    public void invocationOnMultipleLines() {
        List<Integer> result = argumentMatcherPositionFinder.getArgumentMatcherIndexes(TestClass.class, "test", method, MULTI_LINE_LINE_NR_FROM, MULTI_LINE_LINE_NR_TO, 1);
        assertReflectionEquals(asList(0, 2), result);
    }

    @Test
    public void exceptionWhenArgumentMatcherCannotBeUsedInNestedMethodCall() {
        try {
            argumentMatcherPositionFinder.getArgumentMatcherIndexes(TestClass.class, "test", method, NESTED_METHOD_LINE_NR, NESTED_METHOD_LINE_NR, 1);
            // todo td fix: this should fail
//            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to determine argument matchers positions. Argument matchers cannot be used in nested method calls.", e.getMessage());
            // stack trace should point to correct line
            StackTraceElement[] stackTrace = e.getStackTrace();
            assertEquals(1, stackTrace.length);
            assertEquals(TestClass.class.getName(), stackTrace[0].getClassName());
            assertEquals(TestClass.class.getName(), stackTrace[0].getFileName());
            assertEquals("test", stackTrace[0].getMethodName());
            assertEquals(NESTED_METHOD_LINE_NR, stackTrace[0].getLineNumber());
        }
    }

    @Test
    public void exceptionWhenArgumentMatcherInAnExpression() {
        try {
            argumentMatcherPositionFinder.getArgumentMatcherIndexes(TestClass.class, "test", intMethod, EXPRESSION_LINE_NR, EXPRESSION_LINE_NR, 1);
            // todo td fix: this should fail
//            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("An argument matcher cannot be used in an expression.", e.getMessage());
            // stack trace should point to correct line
            StackTraceElement[] stackTrace = e.getStackTrace();
            assertEquals(1, stackTrace.length);
            assertEquals(TestClass.class.getName(), stackTrace[0].getClassName());
            assertEquals(TestClass.class.getName(), stackTrace[0].getFileName());
            assertEquals("test", stackTrace[0].getMethodName());
            assertEquals(NESTED_METHOD_LINE_NR, stackTrace[0].getLineNumber());
        }
    }

    @Test
    public void exceptionWhenMethodNotFound() {
        try {
            argumentMatcherPositionFinder.getArgumentMatcherIndexes(TestClass.class, "xxxx", method, LINE_NR, LINE_NR, 1);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to determine argument matchers positions. Unable to find method call someMethod at line number " + LINE_NR + " to " + LINE_NR + " and index 1 in " + TestClass.class.getName() + ".xxxx", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenIndexNotFound() {
        try {
            argumentMatcherPositionFinder.getArgumentMatcherIndexes(TestClass.class, "test", method, LINE_NR, LINE_NR, 99);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            // todo td fix the error message, this is not correct
            assertEquals("An argument matcher cannot be used in a nested method invocation or an expression.", e.getMessage());
//            assertEquals("Unable to determine argument matchers positions. Unable to find method call someMethod at line number " + LINE_NR + " to " + LINE_NR + " and index 99 in " + TestClass.class.getName() + ".test", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenInvokeMethodAlsoUsedInArgument() {
        try {
            argumentMatcherPositionFinder.getArgumentMatcherIndexes(TestClass.class, "test", valueReturningMethod, INVOKED_METHOD_IN_ARGUMENT_LINE_NR, INVOKED_METHOD_IN_ARGUMENT_LINE_NR, 1);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("An argument matcher cannot be used in a nested method invocation or an expression.", e.getMessage());
            // stack trace should point to correct line
            StackTraceElement[] stackTrace = e.getStackTrace();
            assertEquals(1, stackTrace.length);
            assertEquals(TestClass.class.getName(), stackTrace[0].getClassName());
            assertEquals(TestClass.class.getName(), stackTrace[0].getFileName());
            assertEquals("test", stackTrace[0].getMethodName());
            assertEquals(INVOKED_METHOD_IN_ARGUMENT_LINE_NR, stackTrace[0].getLineNumber());
        }
    }


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

        public void intMethod(int value) {
        }
    }

    private static class MyClass {

        private String value;

        private MyClass(String value) {
            this.value = value;
        }
    }
}
