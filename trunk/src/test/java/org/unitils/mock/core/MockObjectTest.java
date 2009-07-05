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

import static junit.framework.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.unitils.core.util.CloneUtil;
import static org.unitils.mock.ArgumentMatchers.notNull;
import org.unitils.mock.core.proxy.ProxyInvocation;
import org.unitils.mock.mockbehavior.MockBehavior;

/**
 * Tests the mock object functionality.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class MockObjectTest {

    /* Class under test */
    private MockObject<TestClass> mockObject;

    private MockObject<TestClass> equalMockObject;


    @Before
    public void setUp() {
        mockObject = new MockObject<TestClass>("testMock", TestClass.class, this);
        equalMockObject = new MockObject<TestClass>("testMock", TestClass.class, this);
    }


    @Test
    public void testInvocationSpreadOverMoreThanOneLine() {
        TestMockBehavior testMockBehavior = new TestMockBehavior();

        mockObject.performs( //
                testMockBehavior
        ).      //
                testMethod(//
                notNull(//
                        String.class//
                ));

        mockObject. //
                getMock().//
                testMethod(//
                null);
        assertEquals(0, testMockBehavior.invocationCount);

        mockObject.getMock().//
                testMethod( //
                "value");
        assertEquals(1, testMockBehavior.invocationCount);
    }


    @Test
    public void invokedMethodOnDifferentLine() throws Exception {
        mockObject.returns(null).//
                testMethod(null);
    }


    @Test
    public void testEquals() {
        assertTrue(mockObject.getMock().equals(mockObject.getMock()));
        assertFalse(mockObject.getMock().equals(equalMockObject.getMock()));
    }


    @Test
    public void testHashcode_equalObjects() {
        assertTrue(mockObject.getMock().hashCode() == mockObject.getMock().hashCode());
    }


    @Test
    public void testHashcode_differentObjects() {
        assertFalse(mockObject.getMock().hashCode() == equalMockObject.getMock().hashCode());
    }


    @Test
    public void testDeepCloneEqualToOriginal() {
        assertTrue(mockObject.getMock() instanceof Cloneable);
        TestClass clone = CloneUtil.createDeepClone(mockObject.getMock());
        assertEquals(mockObject.getMock(), clone);
    }


    private void assertTopOfStackTracePointsToCurrentTest(Throwable e, Object testMethodName) {
        StackTraceElement topOfStackTrace = e.getStackTrace()[0];
        assertEquals(MockObjectTest.class.getName(), topOfStackTrace.getClassName());
        assertEquals(testMethodName, topOfStackTrace.getMethodName());
    }


    /**
     * Interface that is mocked during the tests
     */
    private static interface TestClass {

        public String testMethod(String arg);

        public int[] testMethodArray();

        public Object clone() throws CloneNotSupportedException;
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
