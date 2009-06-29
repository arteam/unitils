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

/**
 * Test written for bugfix: if a mocked method returns another mock object, this other mock object is cloned
 * at each invocation and added to the scenario. Because the mock object references the scenario, the scenario
 * itself is also cloned each time. The result is that, at each invocation, the amount of objects which is cloned
 * is twice the amount of the previous one. This quickly results in a large time delay. The bug fix involves that
 * the mock object proxy now implements the {@link Cloneable} interface and the {@link Object#clone()} method
 * returns the mock object proxy itself.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class MockObjectReturningOtherMockTest {

    /* Class under test */
    private MockObject<MockReturning> mockReturning;
    private MockObject<TestClass> mockObject;


    @Before
    public void setUp() {
        Scenario scenario = new Scenario(null);
        mockReturning = new MockObject<MockReturning>("testMock", MockReturning.class, scenario);
        mockObject = new MockObject<TestClass>("testMock", TestClass.class, scenario);
    }


    @Test(timeout = 1000)
    public void testMockThatReturnsOtherMock() {
        mockReturning.returns(mockObject.getMock()).getOtherMock();

        for (int i = 0; i < 50; i++) {
            mockReturning.getMock().getOtherMock().testMethod();
        }
    }


    /**
     * Interface that is mocked during the tests
     */
    private static interface TestClass {

        public String testMethod();

    }


    private static interface MockReturning {

        TestClass getOtherMock();
    }
}