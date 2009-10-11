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

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import static org.unitils.mock.MockUnitils.assertNoMoreInvocations;

/**
 * Tests the mock object functionality.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class MockObjectAssertTest {

    /* Class under test */
    private MockObject<TestClass> mockObject;


    @Before
    public void setUp() {
        mockObject = new MockObject<TestClass>("testMock", TestClass.class, this);
    }


    @Test
    public void assertInvoked() {
        mockObject.getMock().testMethod1();
        mockObject.assertInvoked().testMethod1();
    }


    @Test
    public void assertInvokedButNotInvoked() {
        try {
            mockObject.assertInvoked().testMethod1();
            fail();
        } catch (AssertionError e) {
            assertTopOfStackTracePointsToCurrentTest(e, "assertInvokedButNotInvoked");
        }
    }


    @Test
    public void assertNotInvoked() {
        mockObject.assertNotInvoked().testMethod1();
    }


    @Test
    public void assertNotInvokedButInvoked() {
        try {
            mockObject.getMock().testMethod1();
            mockObject.assertNotInvoked().testMethod1();
            fail();
        } catch (AssertionError e) {
            assertTopOfStackTracePointsToCurrentTest(e, "assertNotInvokedButInvoked");
        }
    }


    @Test
    public void assertInvokedInSequence() {
        mockObject.getMock().testMethod1();
        mockObject.getMock().testMethod2();

        mockObject.assertInvokedInSequence().testMethod1();
        mockObject.assertInvokedInSequence().testMethod2();
    }


    @Test
    public void assertInvokedInSequenceButOutOfSequence() {
        try {
            mockObject.getMock().testMethod1();
            mockObject.getMock().testMethod2();

            mockObject.assertInvokedInSequence().testMethod2();
            mockObject.assertInvokedInSequence().testMethod1();
            fail();
        } catch (AssertionError e) {
            assertTopOfStackTracePointsToCurrentTest(e, "assertInvokedInSequenceButOutOfSequence");
        }
    }


    @Test
    public void testNoMoreInvocations() {
        mockObject.getMock().testMethod1();

        mockObject.assertInvoked().testMethod1();
        assertNoMoreInvocations();
    }


    @Test
    public void testNoMoreInvocationsButMoreInvocations() {
        try {
            mockObject.getMock().testMethod1();
            mockObject.getMock().testMethod1();

            mockObject.assertInvoked().testMethod1();
            assertNoMoreInvocations();

        } catch (AssertionError e) {
            assertTopOfStackTracePointsToCurrentTest(e, "testNoMoreInvocationsButMoreInvocations");
        }
    }


    private void assertTopOfStackTracePointsToCurrentTest(Throwable e, Object testMethodName) {
        StackTraceElement topOfStackTrace = e.getStackTrace()[0];
        assertEquals(MockObjectAssertTest.class.getName(), topOfStackTrace.getClassName());
        assertEquals(testMethodName, topOfStackTrace.getMethodName());
    }


    /**
     * Interface that is mocked during the tests
     */
    private static interface TestClass {

        public void testMethod1();

        public void testMethod2();

    }

}