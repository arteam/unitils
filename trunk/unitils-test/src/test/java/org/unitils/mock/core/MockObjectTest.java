/*
 * Copyright Unitils.org
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

import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.unitils.mock.ArgumentMatchers.notNull;
import static org.unitils.mock.core.proxy.CloneUtil.createDeepClone;

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
        TestClass clone = createDeepClone(mockObject.getMock());
        assertEquals(mockObject.getMock(), clone);
    }


    @Test
    public void useRawTypeWhenMockingGenericTypes() {
        MockObject<List<String>> mockObject = new MockObject<List<String>>("testMock", List.class, this);

        mockObject.returns("value").get(0);
        assertEquals("value", mockObject.getMock().get(0));
    }


    @Test(expected = ClassCastException.class)
    public void typeMismatch() {
        MockObject<List<String>> mockObject = new MockObject<List<String>>("testMock", Map.class, this);
        mockObject.returns("value").get(0);  //raises classcast
    }

    @Test
    public void defaultMockName() {
        MockObject<TestClass> mockObject = new MockObject<TestClass>(TestClass.class, this);
        assertEquals("testClassMock", mockObject.getName());
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
