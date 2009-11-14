/*
 * Copyright 2008,  Unitils.org
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
package org.unitils;

import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.unitils.TracingTestListener.ListenerInvocation.*;
import org.unitils.TracingTestListener.TestFramework;
import static org.unitils.TracingTestListener.TestFramework.TESTNG;
import static org.unitils.TracingTestListener.TestInvocation.*;
import org.unitils.spring.SpringUnitilsTestNGTest;

import java.util.Arrays;
import java.util.Collection;

/**
 * Test for the main flow of the unitils test listeners for JUnit3 ({@link org.unitils.UnitilsJUnit3}),
 * JUnit4 (@link UnitilsJUnit4TestClassRunner}) and TestNG ({@link UnitilsTestNG}).
 * <p/>
 * Except for some minor differences, the flows for all these test frameworks
 * are expected to be the same.
 * <p/>
 * 3 tests are performed: TestClass1 and TestClass2 both with 2 test methods and EmptyTestClass
 * that does not contain any methods. TestClass1 also contains an ignored test (not for JUnit3).
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 * @see UnitilsTestNGTest_TestClass1
 * @see UnitilsTestNGTest_TestClass2
 * @see UnitilsTestNGTest_EmptyTestClass
 */
@RunWith(Parameterized.class)
public class TestNGUnitilsInvocationTest extends UnitilsInvocationTestBase {

    private Class<?> testClass1, testClass2;

    public TestNGUnitilsInvocationTest(TestFramework testFramework, TestExecutor testExecutor, Class<?> testClass1, Class<?> testClass2) {
        super(testFramework, testExecutor);
        this.testClass1 = testClass1;
        this.testClass2 = testClass2;
    }

    @Parameters
    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][]{
                {TESTNG, new TestNGTestExecutor(), UnitilsTestNGTest_TestClass1.class, UnitilsTestNGTest_TestClass2.class},
                //{TESTNG, new TestNGTestExecutor(), SpringUnitilsTestNGTest_TestClass1.class, SpringUnitilsTestNGTest_TestClass2.class},
        });
    }


    @Before
    public void initialize() throws Exception {
        UnitilsTestNGTestBase.setTracingTestListener(tracingTestListener);
        UnitilsTestNGTest_EmptyTestClass.setTracingTestListener(tracingTestListener);
        UnitilsTestNGTest_GroupsTest.setTracingTestListener(tracingTestListener);
        SpringUnitilsTestNGTest.setTracingTestListener(tracingTestListener);
    }


    @After
    public void cleanUp() throws Exception {
        UnitilsTestNGTestBase.setTracingTestListener(null);
        UnitilsTestNGTest_EmptyTestClass.setTracingTestListener(null);
        UnitilsTestNGTest_GroupsTest.setTracingTestListener(null);
        SpringUnitilsTestNGTest.setTracingTestListener(null);
    }


    @Test
    public void testSuccessfulRun() throws Exception {
        testExecutor.runTests(testClass1, testClass2);
        assertInvocationOrder(testClass1, testClass2);
    }


    /**
     * Tests the correct invocation sequence of listener methods for a TestNG test that defines a test group.
     */
    @Test
    public void testUnitilsTestNG_group() throws Exception {
        assumeTrue(TESTNG.equals(testFramework));

        testExecutor.runTests("testGroup", UnitilsTestNGTest_GroupsTest.class);

        assertInvocation(LISTENER_BEFORE_CLASS, UnitilsTestNGTest_GroupsTest.class);
        assertInvocation(LISTENER_AFTER_CREATE_TEST_OBJECT, UnitilsTestNGTest_GroupsTest.class);
        assertInvocation(TEST_BEFORE_CLASS, UnitilsTestNGTest_GroupsTest.class);

        assertInvocation(LISTENER_BEFORE_TEST_SET_UP, UnitilsTestNGTest_GroupsTest.class);
        assertInvocation(TEST_SET_UP, UnitilsTestNGTest_GroupsTest.class);
        assertInvocation(LISTENER_BEFORE_TEST_METHOD, UnitilsTestNGTest_GroupsTest.class);
        assertInvocation(TEST_METHOD, UnitilsTestNGTest_GroupsTest.class);
        assertInvocation(LISTENER_AFTER_TEST_METHOD, UnitilsTestNGTest_GroupsTest.class);
        assertInvocation(TEST_TEAR_DOWN, UnitilsTestNGTest_GroupsTest.class);
        assertInvocation(LISTENER_AFTER_TEST_TEARDOWN, UnitilsTestNGTest_GroupsTest.class);
        assertInvocation(TEST_AFTER_CLASS, UnitilsTestNGTest_GroupsTest.class);
        assertNoMoreInvocations();

        assertEquals(0, testExecutor.getFailureCount());
    }


    /**
     * Tests the correct invocation sequence of listener methods for a TestNG test that defines an unknown test group.
     */
    @Test
    public void testUnitilsTestNG_unknownGroup() throws Exception {
        assumeTrue(TESTNG.equals(testFramework));

        testExecutor.runTests("xxxx", UnitilsTestNGTest_GroupsTest.class);

        assertNoMoreInvocations();

        assertEquals(0, testExecutor.getFailureCount());
    }
}