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
package org.unitils;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;
import static org.unitils.TracingTestListener.ListenerInvocation.LISTENER_BEFORE_CLASS;
import static org.unitils.TracingTestListener.ListenerInvocation.LISTENER_AFTER_CREATE_TEST_OBJECT;
import static org.unitils.TracingTestListener.ListenerInvocation.LISTENER_AFTER_TEST_METHOD;
import static org.unitils.TracingTestListener.ListenerInvocation.LISTENER_AFTER_TEST_TEARDOWN;
import static org.unitils.TracingTestListener.ListenerInvocation.LISTENER_BEFORE_TEST_METHOD;
import static org.unitils.TracingTestListener.ListenerInvocation.LISTENER_BEFORE_TEST_SET_UP;
import static org.unitils.TracingTestListener.TestFramework.JUNIT3;
import static org.unitils.TracingTestListener.TestFramework.JUNIT4;
import static org.unitils.TracingTestListener.TestFramework.TESTNG;
import static org.unitils.TracingTestListener.TestInvocation.TEST_AFTER_CLASS;
import static org.unitils.TracingTestListener.TestInvocation.TEST_BEFORE_CLASS;
import static org.unitils.TracingTestListener.TestInvocation.TEST_METHOD;
import static org.unitils.TracingTestListener.TestInvocation.TEST_SET_UP;
import static org.unitils.TracingTestListener.TestInvocation.TEST_TEAR_DOWN;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.unitils.TracingTestListener.TestFramework;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;
import org.unitils.inject.util.InjectionUtils;

/**
 * Test for the main flow of the unitils test listeners for JUnit3 ({@link UnitilsJUnit3}),
 * JUnit4 (@link UnitilsJUnit4TestClassRunner}) and TestNG ({@link UnitilsTestNG}).
 * <p/>
 * Except for some minor differences, the flows for all these test frameworks
 * are expected to be the same (see {@link #assertInvocationOrder}.
 * <p/>
 * 3 tests are performed: TestClass1 and TestClass2 both with 2 test methods and EmptyTestClass
 * that does not contain any methods. TestClass1 also contains an ignored test (not for JUnit3).
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 * @see UnitilsJUnit3Test_TestClass1
 * @see UnitilsJUnit3Test_TestClass2
 * @see UnitilsJUnit3Test_EmptyTestClass
 * @see UnitilsJUnit4Test_TestClass1
 * @see UnitilsJUnit4Test_TestClass2
 * @see UnitilsTestNGTest_TestClass1
 * @see UnitilsTestNGTest_TestClass2
 * @see UnitilsTestNGTest_EmptyTestClass
 */
@RunWith(Parameterized.class)
public class UnitilsInvocationTest extends UnitilsInvocationTestBase {
    
    Class<?> testClass1, testClass2;
    
	public UnitilsInvocationTest(TestFramework testFramework, TestExecutor testExecutor, Class<?> testClass1, Class<?> testClass2) {
		super(testFramework, testExecutor);
		this.testClass1 = testClass1;
		this.testClass2 = testClass2;
	}
	
	@Parameters
	public static Collection<Object[]> testData() {
		return Arrays.asList(new Object[][] {
				{JUNIT3, new JUnit3TestExecutor(), UnitilsJUnit3Test_TestClass1.class, UnitilsJUnit3Test_TestClass2.class},
				{JUNIT4, new JUnit4TestExecutor(), UnitilsJUnit4Test_TestClass1.class, UnitilsJUnit4Test_TestClass2.class},
				{TESTNG, new TestNGTestExecutor(), UnitilsTestNGTest_TestClass1.class, UnitilsTestNGTest_TestClass2.class},
				//{JUNIT3, new JUnit3TestExecutor(), SpringUnitilsJUnit38Test_TestClass1.class, SpringUnitilsJUnit38Test_TestClass2.class},
				//{JUNIT4, new JUnit4TestExecutor(), SpringUnitilsJUnit4Test_TestClass1.class, SpringUnitilsJUnit4Test_TestClass2.class},
				//{TESTNG, new TestNGTestExecutor(), SpringUnitilsTestNGTest_TestClass1.class, SpringUnitilsTestNGTest_TestClass2.class},
		});
	}
	
	
	@Before
    public void resetJunit3() {
    	InjectionUtils.injectIntoStatic(null, UnitilsJUnit3.class, "currentTestClass");
    }
    
	
    @Test
    public void testSuccessfulRun() throws Exception {
    	testExecutor.runTests(testClass1, testClass2);
    	
    	assertInvocation(LISTENER_BEFORE_CLASS, 			testClass1);
    	assertInvocation(LISTENER_AFTER_CREATE_TEST_OBJECT, testClass1, TESTNG);
    	assertInvocation(TEST_BEFORE_CLASS,        	        testClass1, JUNIT4, TESTNG);
    	assertInvocation(LISTENER_AFTER_CREATE_TEST_OBJECT, testClass1, JUNIT3, JUNIT4);
    	// testClass 1, testMethod 1
    	assertInvocation(LISTENER_BEFORE_TEST_SET_UP,       testClass1);
    	assertInvocation(TEST_SET_UP,                       testClass1);
    	assertInvocation(LISTENER_BEFORE_TEST_METHOD,       testClass1);
    	assertInvocation(TEST_METHOD,                       testClass1);
    	assertInvocation(LISTENER_AFTER_TEST_METHOD,        testClass1);
        assertInvocation(TEST_TEAR_DOWN,                    testClass1);
        assertInvocation(LISTENER_AFTER_TEST_TEARDOWN,      testClass1);
        // testClass 1, testMethod 2
        assertInvocation(LISTENER_AFTER_CREATE_TEST_OBJECT, testClass1, JUNIT3, JUNIT4);
        assertInvocation(LISTENER_BEFORE_TEST_SET_UP,       testClass1);
    	assertInvocation(TEST_SET_UP,                       testClass1);
    	assertInvocation(LISTENER_BEFORE_TEST_METHOD,       testClass1);
    	assertInvocation(TEST_METHOD,                       testClass1);
    	assertInvocation(LISTENER_AFTER_TEST_METHOD,        testClass1);
        assertInvocation(TEST_TEAR_DOWN,                    testClass1);
        assertInvocation(LISTENER_AFTER_TEST_TEARDOWN,      testClass1);
		assertInvocation(TEST_AFTER_CLASS,         			testClass1, JUNIT4, TESTNG);
        
        // testClass 2, testMethod 1
		assertInvocation(LISTENER_BEFORE_CLASS, 			testClass2);
        assertInvocation(LISTENER_AFTER_CREATE_TEST_OBJECT, testClass2, TESTNG);
		assertInvocation(TEST_BEFORE_CLASS,        			testClass2, JUNIT4, TESTNG);
		assertInvocation(LISTENER_AFTER_CREATE_TEST_OBJECT, testClass2, JUNIT3, JUNIT4);
    	assertInvocation(LISTENER_BEFORE_TEST_SET_UP,  		testClass2);
    	assertInvocation(TEST_SET_UP,                  		testClass2);
    	assertInvocation(LISTENER_BEFORE_TEST_METHOD, 	 	testClass2);
    	assertInvocation(TEST_METHOD,                  		testClass2);
    	assertInvocation(LISTENER_AFTER_TEST_METHOD,   		testClass2);
        assertInvocation(TEST_TEAR_DOWN,               		testClass2);
        assertInvocation(LISTENER_AFTER_TEST_TEARDOWN, 		testClass2);
        // testClass 2, testMethod 2
        assertInvocation(LISTENER_AFTER_CREATE_TEST_OBJECT, testClass2, JUNIT3, JUNIT4);
        assertInvocation(LISTENER_BEFORE_TEST_SET_UP,  		testClass2);
    	assertInvocation(TEST_SET_UP,                  		testClass2);
    	assertInvocation(LISTENER_BEFORE_TEST_METHOD,  		testClass2);
    	assertInvocation(TEST_METHOD,                  		testClass2);
    	assertInvocation(LISTENER_AFTER_TEST_METHOD,   		testClass2);
        assertInvocation(TEST_TEAR_DOWN,               		testClass2);
        assertInvocation(LISTENER_AFTER_TEST_TEARDOWN, 		testClass2);
    	assertInvocation(TEST_AFTER_CLASS,         			testClass2, JUNIT4, TESTNG);
    	assertNoMoreInvocations();

        assertEquals(4, testExecutor.getRunCount());
    	assertEquals(0, testExecutor.getFailureCount());
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
        assertInvocation(TEST_BEFORE_CLASS,        	        UnitilsTestNGTest_GroupsTest.class);
    	
        assertInvocation(LISTENER_BEFORE_TEST_SET_UP,       UnitilsTestNGTest_GroupsTest.class);
    	assertInvocation(TEST_SET_UP,                       UnitilsTestNGTest_GroupsTest.class);
    	assertInvocation(LISTENER_BEFORE_TEST_METHOD,       UnitilsTestNGTest_GroupsTest.class);
    	assertInvocation(TEST_METHOD,                       UnitilsTestNGTest_GroupsTest.class);
    	assertInvocation(LISTENER_AFTER_TEST_METHOD,        UnitilsTestNGTest_GroupsTest.class);
        assertInvocation(TEST_TEAR_DOWN,                    UnitilsTestNGTest_GroupsTest.class);
        assertInvocation(LISTENER_AFTER_TEST_TEARDOWN,      UnitilsTestNGTest_GroupsTest.class);
        assertInvocation(TEST_AFTER_CLASS,         			UnitilsTestNGTest_GroupsTest.class);
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
    
    
	/**
     * JUnit 3 test class without any tests. Inner class to avoid a failing test.
     */
    protected static class UnitilsJUnit3Test_EmptyTestClass extends UnitilsJUnit3 {

        private static TracingTestListener tracingTestListener;

        public static void setTracingTestListener(TracingTestListener testListener) {
            tracingTestListener = testListener;
        }


        @Override
        protected Unitils getUnitils() {
            if (tracingTestListener != null) {
                return new Unitils() {

                    @Override
                    public TestListener getTestListener() {
                        return tracingTestListener;
                    }
                };
            }
            return super.getUnitils();
        }
    }
	
}
