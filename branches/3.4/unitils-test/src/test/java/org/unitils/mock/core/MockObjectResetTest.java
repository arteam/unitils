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

import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the reset behavior of the mock object.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class MockObjectResetTest {

    /* Class under test */
    private MockObject<TestClass> mockObject;

    @Before
    public void setUp() {
        mockObject = new MockObject<TestClass>("testMock", TestClass.class, this);
    }


    @Test
    public void resetBehavior() {
        mockObject.onceReturns("aValue").testMethod();
        mockObject.returns("aValue").testMethod();

        mockObject.resetBehavior();

        String result = mockObject.getMock().testMethod();
        assertNull(result);
    }


    /**
     * Interface that is mocked during the tests
     */
    private static interface TestClass {

        public String testMethod();

    }

}