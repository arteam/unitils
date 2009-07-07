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
import org.unitils.mock.core.proxy.ProxyInvocation;
import org.unitils.mock.mockbehavior.MockBehavior;
import static org.unitils.mock.core.proxy.ReflectionAssert.assertLenientEquals;

/**
 * Tests the mock object functionality.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class MockObjectPerformsTest {

    /* Class under test */
    private MockObject<TestClass> mockObject;


    @Before
    public void setUp() {
        mockObject = new MockObject<TestClass>("testMock", TestClass.class, this);
    }


    /**
     * Tests setting a peforms behavior for the mock. The behavior is an always matching behavior
     * so the method should keep performing that same behavior.
     */
    @Test
    public void performs() {
        TestMockBehavior testMockBehavior = new TestMockBehavior();
        mockObject.performs(testMockBehavior).testMethodString();

        mockObject.getMock().testMethodString();
        mockObject.getMock().testMethodString();
        assertLenientEquals(2, testMockBehavior.invocationCount);
    }


    /**
     * Tests setting a once peforms behavior for the mock. The behavior should be executed only once, the second time
     * nothing should have happened.
     */
    @Test
    public void oncePerforms() {
        TestMockBehavior testMockBehavior = new TestMockBehavior();
        mockObject.oncePerforms(testMockBehavior).testMethodString();

        mockObject.getMock().testMethodString();
        mockObject.getMock().testMethodString();
        assertLenientEquals(1, testMockBehavior.invocationCount);
    }


    /**
     * Interface that is mocked during the tests
     */
    private static interface TestClass {

        public String testMethodString();

    }


    /**
     * Dummy mock behavior that counts how many times it was invoked.
     */
    private static class TestMockBehavior implements MockBehavior {

        public int invocationCount = 0;

        public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
            invocationCount++;
            return null;
        }
    }
}