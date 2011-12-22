/*
 * Copyright 2006-2007,  Unitils.org
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;
import static org.unitils.mock.ArgumentMatchers.notNull;
import org.unitils.mock.core.proxy.ProxyInvocation;
import org.unitils.mock.mockbehavior.MockBehavior;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Test for verifying syntax problems when defining mock behavior and assertions.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class MockObjectInvalidSyntaxTest {

    /* Class under test */
    private MockObject<TestClass> mockObject;


    @Before
    public void setUp() {
        mockObject = new MockObject<TestClass>("testMock1", TestClass.class, this);
    }


    @Test
    public void incompleteBehaviorDefinition_returns_followedBySecondBehaviorDefinition() {
        try {
            mockObject.returns("aValue");
            mockObject.returns("aValue");
            fail("Expected UnitilsException");
        } catch (UnitilsException e) {
            assertCorrectTopLevelClassInStackTrace(e);
        }
    }

    private void assertCorrectTopLevelClassInStackTrace(UnitilsException e) {
        StackTraceElement[] stackTrace = e.getStackTrace();
        assertEquals(MockObjectInvalidSyntaxTest.class.getName(), stackTrace[0].getClassName());
    }


    @Test
    public void incompleteBehaviorDefinition_returns_followedByActualInvocation() {
        try {
            mockObject.returns("aValue");
            mockObject.getMock().testMethod();
        } catch (UnitilsException e) {
            assertCorrectTopLevelClassInStackTrace(e);
        }
    }


    @Test
    public void incompleteBehaviorDefinition_raises() {
        try {
            mockObject.raises(new RuntimeException());
            mockObject.returns("aValue");
        } catch (UnitilsException e) {
            assertCorrectTopLevelClassInStackTrace(e);
        }
    }


    @Test
    public void incompleteBehaviorDefinition_performs() {
        try {
            mockObject.performs(new MockBehavior() {
                public Object execute(ProxyInvocation mockInvocation) throws Throwable {
                    return null;
                }
            });
            mockObject.assertInvoked();
        } catch (UnitilsException e) {
            assertCorrectTopLevelClassInStackTrace(e);
        }
    }


    @Test
    public void incompleteBehaviorDefinition_onceReturns() {
        try {
            mockObject.onceReturns("aValue");
            mockObject.returns("aValue");
        } catch (UnitilsException e) {
            assertCorrectTopLevelClassInStackTrace(e);
        }
    }


    @Test
    public void incompleteBehaviorDefinition_onceRaises() {
        try {
            mockObject.onceRaises(new RuntimeException());
            mockObject.returns("aValue");
        } catch (UnitilsException e) {
            assertCorrectTopLevelClassInStackTrace(e);
        }
    }


    @Test
    public void incompleteBehaviorDefinition_oncePerforms() {
        try {
            mockObject.oncePerforms(new MockBehavior() {
                public Object execute(ProxyInvocation mockInvocation) throws Throwable {
                    return null;
                }
            });
            mockObject.assertInvoked();
        } catch (UnitilsException e) {
            assertCorrectTopLevelClassInStackTrace(e);
        }
    }


    @Test
    public void incompleteAssertStatement_assertInvoked() {
        try {
            mockObject.assertInvoked();
            mockObject.assertInvoked();
        } catch (UnitilsException e) {
            assertCorrectTopLevelClassInStackTrace(e);
        }
    }


    @Test
    public void incompleteAssertStatement_assertInvokedInOrder() {
        try {
            mockObject.assertInvokedInSequence();
            mockObject.assertInvoked();
        } catch (UnitilsException e) {
            assertCorrectTopLevelClassInStackTrace(e);
        }
    }


    @Test
    public void incompleteAssertStatement_assertNotInvoked() {
        try {
            mockObject.assertNotInvoked();
            mockObject.assertNotInvoked();
        } catch (UnitilsException e) {
            assertCorrectTopLevelClassInStackTrace(e);
        }
    }


    @Test
    public void tryToLetVoidMethodReturnValue() {
        try {
            mockObject.returns("value").testMethod();
            mockObject.getMock().testMethod();
        } catch (UnitilsException e) {
            assertCorrectTopLevelClassInStackTrace(e);
        }
    }


    @Test
    public void tryToLetMethodReturnIncompatibleReturnValue() {
        try {
            mockObject.returns(new ArrayList<String>()).testMethod();
            mockObject.getMock().testMethod();
        } catch (UnitilsException e) {
            assertCorrectTopLevelClassInStackTrace(e);
        }
    }


    @Test
    public void tryToLetMethodThrowUndeclaredCheckedException() {
        try {
            mockObject.raises(IOException.class).testMethod();
            mockObject.getMock().testMethod();
        } catch (UnitilsException e) {
            assertCorrectTopLevelClassInStackTrace(e);
        }
    }


    @Test(expected = UnitilsException.class)
    public void argumentMatcherUsedOutsideBehaviorDefinition() {
        notNull(String.class);
    }


    @Test
    public void nestedBehaviorDefintionCall() {
        try {
            mockObject.raises(IllegalArgumentException.class).testMethodArgument(mockObject.returns("aValue").testMethodReturningString());
        } catch (UnitilsException e) {
            assertCorrectTopLevelClassInStackTrace(e);
        }
    }


    /**
     * Interface that is mocked during the tests
     */
    private static interface TestClass {

        public String testMethodReturningString();

        public void testMethod();

        public void testMethodArgument(String str);

    }
}
