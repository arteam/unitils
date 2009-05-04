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

import static org.unitils.mock.ArgumentMatchers.notNull;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import org.unitils.core.UnitilsException;
import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.proxy.ProxyInvocation;

/**
 * todo javadoc
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class MockObjectInvalidSyntaxTest {

    /* Class under test */
    private MockObject<TestClass> mockObject1, mockObject2;


    @Before
    public void setUp() {
        Scenario scenario = new Scenario(null);
        mockObject1 = new MockObject<TestClass>("testMock1", TestClass.class, false, scenario);
        mockObject2 = new MockObject<TestClass>("testMock2", TestClass.class, false, scenario);
    }


    @Test(expected = UnitilsException.class)
    public void incompleteBehaviorDefinition_returns_followedBySecondBehaviorDefinition() {
        mockObject1.returns("aValue");
        mockObject2.returns("aValue");
    }

    @Test(expected = UnitilsException.class)
    public void incompleteBehaviorDefinition_returns_followedByActualInvocation() {
        mockObject1.returns("aValue");
        mockObject2.getMock().testMethod();
    }


    @Test(expected = UnitilsException.class)
    public void incompleteBehaviorDefinition_raises() {
        mockObject1.raises(new RuntimeException());
        mockObject2.returns("aValue");
    }


    @Test(expected = UnitilsException.class)
    @Ignore
    public void incompleteBehaviorDefinition_performs() {
        mockObject1.performs(new MockBehavior() {
            public Object execute(ProxyInvocation mockInvocation) throws Throwable {
                return null;
            }
        });
        mockObject2.assertInvoked();
    }


    @Test(expected = UnitilsException.class)
    public void incompleteBehaviorDefinition_onceReturns() {
        mockObject1.onceReturns("aValue");
        mockObject2.returns("aValue");
    }


    @Test(expected = UnitilsException.class)
    public void incompleteBehaviorDefinition_onceRaises() {
        mockObject1.onceRaises(new RuntimeException());
        mockObject2.returns("aValue");
    }


    @Test(expected = UnitilsException.class)
    public void incompleteBehaviorDefinition_oncePerforms() {
        mockObject1.oncePerforms(new MockBehavior() {
            public Object execute(ProxyInvocation mockInvocation) throws Throwable {
                return null;
            }
        });
        mockObject2.assertInvoked();
    }


    @Test(expected = UnitilsException.class)
    public void incompleteAssertStatement_assertInvoked() {
        mockObject1.assertInvoked();
        mockObject2.assertInvoked();
    }


    @Test(expected = UnitilsException.class)
    public void incompleteAssertStatement_assertInvokedInOrder() {
        mockObject1.assertInvokedInSequence();
        mockObject2.assertInvoked();
    }


    @Test(expected = UnitilsException.class)
    public void incompleteAssertStatement_assertNotInvoked() {
        mockObject1.assertNotInvoked();
        mockObject2.assertNotInvoked();
    }

    @Test(expected = UnitilsException.class)
    public void tryToLetVoidMethodReturnValue() {
        mockObject1.returns("value").testMethod();
    }


    @Test(expected = UnitilsException.class)
    public void tryToLetMethodReturnIncompatibleReturnValue() {
        mockObject1.returns(new ArrayList<String>()).testMethod();
    }
    
    @Test(expected = UnitilsException.class)
    public void tryToLetMethodThrowUndeclaredCheckedException() {
        mockObject1.raises(IOException.class).testMethod();
    }


    @Test(expected = UnitilsException.class)
    public void argumentMatcherUsedOutsideBehaviorDefinition() {
        String notNull = notNull(String.class);
        mockObject1.raises(IllegalArgumentException.class).testMethodArgument(notNull);
    }
    
    @Test(expected = UnitilsException.class)
    public void nestedBehaviorDefintionCall() {
        mockObject1.raises(IllegalArgumentException.class).testMethodArgument(mockObject1.returns("aValue").testMethodReturningString());
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
