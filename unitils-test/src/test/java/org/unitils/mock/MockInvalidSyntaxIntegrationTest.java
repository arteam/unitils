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
package org.unitils.mock;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import org.unitils.mock.core.proxy.ProxyInvocation;
import org.unitils.mock.mockbehavior.MockBehavior;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.unitils.mock.ArgumentMatchers.notNull;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class MockInvalidSyntaxIntegrationTest extends UnitilsJUnit4 {

    private Mock<TestInterface> mockObject;


    @Test
    public void exceptionWhenPreviousReturnsWasNotCompleted() {
        mockObject.returns("aValue"); // 42
        try {
            mockObject.returns("aValue");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Invalid syntax: mockObject.returns() must be followed by a method invocation on the returned proxy. E.g. mockObject.returns().myMethod();", e.getMessage());
            assertFirstStackTraceElement("exceptionWhenPreviousReturnsWasNotCompleted", 42, e);
        }
    }

    @Test
    public void exceptionWhenMockCallAndPreviousReturnsWasNotCompleted() {
        mockObject.returns("aValue"); // 54
        try {
            mockObject.getMock().testMethod();
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Invalid syntax: mockObject.returns() must be followed by a method invocation on the returned proxy. E.g. mockObject.returns().myMethod();", e.getMessage());
            assertFirstStackTraceElement("exceptionWhenMockCallAndPreviousReturnsWasNotCompleted", 54, e);
        }
    }

    @Test
    public void exceptionWhenPreviousRaisesWasNotCompleted() {
        mockObject.raises(new RuntimeException()); // 66
        try {
            mockObject.returns("aValue");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Invalid syntax: mockObject.raises() must be followed by a method invocation on the returned proxy. E.g. mockObject.raises().myMethod();", e.getMessage());
            assertFirstStackTraceElement("exceptionWhenPreviousRaisesWasNotCompleted", 66, e);
        }
    }

    @Test
    public void exceptionWhenPreviousPerformsWasNotCompleted() {
        mockObject.performs(new MockBehavior() { // 78
            public Object execute(ProxyInvocation mockInvocation) throws Throwable {
                return null;
            }
        });
        try {
            mockObject.assertInvoked();
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Invalid syntax: mockObject.performs() must be followed by a method invocation on the returned proxy. E.g. mockObject.performs().myMethod();", e.getMessage());
            assertFirstStackTraceElement("exceptionWhenPreviousPerformsWasNotCompleted", 78, e);
        }
    }

    @Test
    public void exceptionWhenPreviousOnceReturnsWasNotCompleted() {
        mockObject.onceReturns("aValue"); // 94
        try {
            mockObject.returns("aValue");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Invalid syntax: mockObject.onceReturns() must be followed by a method invocation on the returned proxy. E.g. mockObject.onceReturns().myMethod();", e.getMessage());
            assertFirstStackTraceElement("exceptionWhenPreviousOnceReturnsWasNotCompleted", 94, e);
        }
    }

    @Test
    public void exceptionWhenPreviousOnceRaisesWasNotCompleted() {
        mockObject.onceRaises(new RuntimeException()); // 106
        try {
            mockObject.returns("aValue");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Invalid syntax: mockObject.onceRaises() must be followed by a method invocation on the returned proxy. E.g. mockObject.onceRaises().myMethod();", e.getMessage());
            assertFirstStackTraceElement("exceptionWhenPreviousOnceRaisesWasNotCompleted", 106, e);
        }
    }

    @Test
    public void exceptionWhenPreviousOncePerformsWasNotCompleted() {
        mockObject.oncePerforms(new MockBehavior() { // 118
            public Object execute(ProxyInvocation mockInvocation) throws Throwable {
                return null;
            }
        });
        try {
            mockObject.assertInvoked();
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Invalid syntax: mockObject.oncePerforms() must be followed by a method invocation on the returned proxy. E.g. mockObject.oncePerforms().myMethod();", e.getMessage());
            assertFirstStackTraceElement("exceptionWhenPreviousOncePerformsWasNotCompleted", 118, e);
        }
    }

    @Test
    public void exceptionWhenPreviousAssertInvokedWasNotCompleted() {
        mockObject.assertInvoked(); // 134
        try {
            mockObject.assertInvoked();
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Invalid syntax: mockObject.assertInvoked() must be followed by a method invocation on the returned proxy. E.g. mockObject.assertInvoked().myMethod();", e.getMessage());
            assertFirstStackTraceElement("exceptionWhenPreviousAssertInvokedWasNotCompleted", 134, e);
        }
    }

    @Test
    public void exceptionWhenPreviousAssertInvokedInSequenceWasNotCompleted() {
        mockObject.assertInvokedInSequence(); // 146
        try {
            mockObject.assertInvoked();
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Invalid syntax: mockObject.assertInvokedInSequence() must be followed by a method invocation on the returned proxy. E.g. mockObject.assertInvokedInSequence().myMethod();", e.getMessage());
            assertFirstStackTraceElement("exceptionWhenPreviousAssertInvokedInSequenceWasNotCompleted", 146, e);
        }
    }

    @Test
    public void exceptionWhenPreviousAssertNotInvokedWasNotCompleted() {
        mockObject.assertNotInvoked(); // 158
        try {
            mockObject.assertNotInvoked();
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Invalid syntax: mockObject.assertNotInvoked() must be followed by a method invocation on the returned proxy. E.g. mockObject.assertNotInvoked().myMethod();", e.getMessage());
            assertFirstStackTraceElement("exceptionWhenPreviousAssertNotInvokedWasNotCompleted", 158, e);
        }
    }

    @Test
    public void exceptionWhenAssertInvokedCallAndPreviousReturnsWasNotCompleted() {
        mockObject.returns("aValue"); // 170
        try {
            mockObject.assertInvoked();
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Invalid syntax: mockObject.returns() must be followed by a method invocation on the returned proxy. E.g. mockObject.returns().myMethod();", e.getMessage());
            assertFirstStackTraceElement("exceptionWhenAssertInvokedCallAndPreviousReturnsWasNotCompleted", 170, e);
        }
    }

    @Test
    public void exceptionWhenReturnsCallAndPreviousAssertInvokedWasNotCompleted() {
        mockObject.assertInvoked(); // 182
        try {
            mockObject.returns("aValue");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Invalid syntax: mockObject.assertInvoked() must be followed by a method invocation on the returned proxy. E.g. mockObject.assertInvoked().myMethod();", e.getMessage());
            assertFirstStackTraceElement("exceptionWhenReturnsCallAndPreviousAssertInvokedWasNotCompleted", 182, e);
        }
    }

    @Test
    public void exceptionWhenReturningValueForVoidMethod() {
        mockObject.returns("value").testMethod(); // 194
        try {
            mockObject.getMock().testMethod();
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Trying to define mock behavior that returns a value for a void method.", e.getMessage());
            assertFirstStackTraceElement("exceptionWhenReturningValueForVoidMethod", 194, e);
        }
    }

    @Test
    public void exceptionWhenReturningValueOfWrongType() {
        mockObject.returns(new ArrayList<String>()).testMethodReturningString(); // 206
        try {
            mockObject.getMock().testMethodReturningString();
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Trying to make a method return a value that is not assignable to the return type. Return type: class java.lang.String, value type: class java.util.ArrayList, value: []", e.getMessage());
            assertFirstStackTraceElement("exceptionWhenReturningValueOfWrongType", 206, e);
        }
    }

    @Test
    public void exceptionWhenRaisingExceptionThatIsNotDeclared() {
        mockObject.raises(IOException.class).testMethod(); // 218
        try {
            mockObject.getMock().testMethod();
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Trying to make a method throw an exception that it doesn't declare. Exception type: class java.io.IOException, no declared exceptions", e.getMessage());
            assertFirstStackTraceElement("exceptionWhenRaisingExceptionThatIsNotDeclared", 218, e);
        }
    }

    @Test
    public void ignoreArgumentMatcherUsedOutsideBehaviorDefinition() {
        try {
            notNull(String.class);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to register argument matcher. Argument matchers can only be used when defining behavior for a mock (e.g. returns) or when doing an assert on a mock. Argument matcher: class org.unitils.mock.argumentmatcher.impl.NotNullArgumentMatcher", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenNestedBehaviorDefinitionCall() {
        try {
            mockObject.raises(IllegalArgumentException.class).testMethodArgument(mockObject.returns("aValue").testMethodReturningString()); // 241
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Invalid syntax: mockObject.raises() must be followed by a method invocation on the returned proxy. E.g. mockObject.raises().myMethod();", e.getMessage());
            assertFirstStackTraceElement("exceptionWhenNestedBehaviorDefinitionCall", 241, e);
        }
    }

    @Test
    public void exceptionWhenInvalidInvocationOnProxy() {
        TestInterface proxy = mockObject.returns("aValue");
        proxy.testMethodReturningString();
        try {
            proxy.testMethodReturningString();
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unexpected matching proxy invocation. Expected following syntax 'mock'.'matching method'.'method'. E.g. myMock.returns().myMethod();", e.getMessage());
        }
    }


    private void assertFirstStackTraceElement(String methodName, int lineNr, UnitilsException e) {
        StackTraceElement stackTraceElement = e.getStackTrace()[0];
        assertEquals(MockInvalidSyntaxIntegrationTest.class.getName(), stackTraceElement.getClassName());
        assertEquals(methodName, stackTraceElement.getMethodName());
        assertEquals(lineNr, stackTraceElement.getLineNumber());
    }


    private static interface TestInterface {

        String testMethodReturningString();

        void testMethod();

        void testMethodArgument(String str);
    }
}
