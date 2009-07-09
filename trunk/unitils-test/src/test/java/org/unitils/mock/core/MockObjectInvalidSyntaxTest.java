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

import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;
import static org.unitils.mock.ArgumentMatchers.notNull;
import org.unitils.mock.core.proxy.ProxyInvocation;
import org.unitils.mock.mockbehavior.MockBehavior;

import java.io.IOException;
import java.util.ArrayList;

/**
 * todo javadoc
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class MockObjectInvalidSyntaxTest {

    /* Class under test */
    private MockObject<TestClass> mockObject;


    @Before
    public void setUp() {
        mockObject = new MockObject<TestClass>("testMock1", TestClass.class, this);
    }


    @Test(expected = UnitilsException.class)
    public void incompleteBehaviorDefinition_returns_followedBySecondBehaviorDefinition() {
        mockObject.returns("aValue");
        mockObject.returns("aValue");
    }


    @Test(expected = UnitilsException.class)
    public void incompleteBehaviorDefinition_returns_followedByActualInvocation() {
        mockObject.returns("aValue");
        mockObject.getMock().testMethod();
    }


    @Test(expected = UnitilsException.class)
    public void incompleteBehaviorDefinition_raises() {
        mockObject.raises(new RuntimeException());
        mockObject.returns("aValue");
    }


    @Test(expected = UnitilsException.class)
    public void incompleteBehaviorDefinition_performs() {
        mockObject.performs(new MockBehavior() {
            public Object execute(ProxyInvocation mockInvocation) throws Throwable {
                return null;
            }
        });
        mockObject.assertInvoked();
    }


    @Test(expected = UnitilsException.class)
    public void incompleteBehaviorDefinition_onceReturns() {
        mockObject.onceReturns("aValue");
        mockObject.returns("aValue");
    }


    @Test(expected = UnitilsException.class)
    public void incompleteBehaviorDefinition_onceRaises() {
        mockObject.onceRaises(new RuntimeException());
        mockObject.returns("aValue");
    }


    @Test(expected = UnitilsException.class)
    public void incompleteBehaviorDefinition_oncePerforms() {
        mockObject.oncePerforms(new MockBehavior() {
            public Object execute(ProxyInvocation mockInvocation) throws Throwable {
                return null;
            }
        });
        mockObject.assertInvoked();
    }


    @Test(expected = UnitilsException.class)
    public void incompleteAssertStatement_assertInvoked() {
        mockObject.assertInvoked();
        mockObject.assertInvoked();
    }


    @Test(expected = UnitilsException.class)
    public void incompleteAssertStatement_assertInvokedInOrder() {
        mockObject.assertInvokedInSequence();
        mockObject.assertInvoked();
    }


    @Test(expected = UnitilsException.class)
    public void incompleteAssertStatement_assertNotInvoked() {
        mockObject.assertNotInvoked();
        mockObject.assertNotInvoked();
    }


    @Test(expected = UnitilsException.class)
    public void tryToLetVoidMethodReturnValue() {
        mockObject.returns("value").testMethod();
        mockObject.getMock().testMethod();
    }


    @Test(expected = UnitilsException.class)
    public void tryToLetMethodReturnIncompatibleReturnValue() {
        mockObject.returns(new ArrayList<String>()).testMethod();
        mockObject.getMock().testMethod();
    }


    @Test(expected = UnitilsException.class)
    public void tryToLetMethodThrowUndeclaredCheckedException() {
        mockObject.raises(IOException.class).testMethod();
        mockObject.getMock().testMethod();
    }


    @Test(expected = UnitilsException.class)
    public void argumentMatcherUsedOutsideBehaviorDefinition() {
        String notNull = notNull(String.class);
        mockObject.raises(IllegalArgumentException.class).testMethodArgument(notNull);
    }


    @Test(expected = UnitilsException.class)
    public void nestedBehaviorDefintionCall() {
        mockObject.raises(IllegalArgumentException.class).testMethodArgument(mockObject.returns("aValue").testMethodReturningString());
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
