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
package org.unitilsnew;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;
import org.unitils.spring.SpringUnitilsJUnit38TestBase;
import org.unitils.spring.SpringUnitilsJUnit4TestBase;
import org.unitilsnew.TracingTestListener.Call;
import org.unitilsnew.TracingTestListener.Invocation;
import org.unitilsnew.TracingTestListener.TestFramework;

import java.util.Iterator;
import java.util.List;

import static junit.framework.Assert.*;
import static org.unitilsnew.TracingTestListener.ListenerInvocation.*;
import static org.unitilsnew.TracingTestListener.TestFramework.*;
import static org.unitilsnew.TracingTestListener.TestInvocation.*;

/**
 * Base class for the invocation listener tests.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
abstract public class UnitilsInvocationTestBase {

    /* TestListener normally used by Unitils, to be restored after test */
    private static TestListener originalTestListener;

    /* Listener that records all method invocations during the tests */
    protected TracingTestListener tracingTestListener;

    private Iterator<Call> callListIterator;

    protected TestFramework testFramework;

    protected TestExecutor testExecutor;

    public UnitilsInvocationTestBase(TestFramework testFramework, TestExecutor testExecutor) {
        this.testFramework = testFramework;
        this.testExecutor = testExecutor;
    }

    @BeforeClass
    public static void storeOriginalTestListener() {
        originalTestListener = Unitils.getInstance().getTestListener();
    }

    @AfterClass
    public static void restoreOrginalTestListener() {
        // todo
//        InjectionUtils.injectInto(originalTestListener, Unitils.getInstance(), "testListener");
    }


    /**
     * Sets up the test installing the tracing test listener that will record all method invocations during the test.
     * The current test listeners are stored so that they can be restored during the class tear down.
     * Also re-initializes the base-classes so that, for example, beforeAll() will be called another time.
     */
    @Before
    public void init() throws Exception {
        // Create a test listener that traces the test execution, and make sure it is used by the tests to
        // record their calls
        tracingTestListener = new TracingTestListener();

        UnitilsJUnit3TestBase.setTracingTestListener(tracingTestListener);
        SpringUnitilsJUnit38TestBase.setTracingTestListener(tracingTestListener);

        UnitilsJUnit4TestBase.setTracingTestListener(tracingTestListener);
        SpringUnitilsJUnit4TestBase.setTracingTestListener(tracingTestListener);

        // todo
//        InjectionUtils.injectInto(tracingTestListener, Unitils.getInstance(), "testListener");
    }

    @After
    public void cleanUp() throws Exception {
        UnitilsJUnit3TestBase.setTracingTestListener(null);
        SpringUnitilsJUnit38TestBase.setTracingTestListener(null);

        UnitilsJUnit4TestBase.setTracingTestListener(null);
        SpringUnitilsJUnit4TestBase.setTracingTestListener(null);
    }


    public void assertInvocationOrder(Class<?> testClass1, Class<?> testClass2) throws Exception {
        assertInvocation(LISTENER_BEFORE_CLASS, testClass1);
        assertInvocation(LISTENER_AFTER_CREATE_TEST_OBJECT, testClass1, TESTNG);
        assertInvocation(TEST_BEFORE_CLASS, testClass1, JUNIT4, TESTNG);
        assertInvocation(LISTENER_AFTER_CREATE_TEST_OBJECT, testClass1, JUNIT3, JUNIT4);
        // testClass 1, testMethod 1
        assertInvocation(LISTENER_BEFORE_TEST_SET_UP, testClass1);
        assertInvocation(TEST_SET_UP, testClass1);
        assertInvocation(LISTENER_BEFORE_TEST_METHOD, testClass1);
        assertInvocation(TEST_METHOD, testClass1);
        assertInvocation(LISTENER_AFTER_TEST_METHOD, testClass1);
        assertInvocation(TEST_TEAR_DOWN, testClass1);
        assertInvocation(LISTENER_AFTER_TEST_TEARDOWN, testClass1);
        // testClass 1, testMethod 2
        assertInvocation(LISTENER_AFTER_CREATE_TEST_OBJECT, testClass1, JUNIT3, JUNIT4);
        assertInvocation(LISTENER_BEFORE_TEST_SET_UP, testClass1);
        assertInvocation(TEST_SET_UP, testClass1);
        assertInvocation(LISTENER_BEFORE_TEST_METHOD, testClass1);
        assertInvocation(TEST_METHOD, testClass1);
        assertInvocation(LISTENER_AFTER_TEST_METHOD, testClass1);
        assertInvocation(TEST_TEAR_DOWN, testClass1);
        assertInvocation(LISTENER_AFTER_TEST_TEARDOWN, testClass1);
        assertInvocation(TEST_AFTER_CLASS, testClass1, JUNIT4, TESTNG);

        // testClass 2, testMethod 1
        assertInvocation(LISTENER_BEFORE_CLASS, testClass2);
        assertInvocation(LISTENER_AFTER_CREATE_TEST_OBJECT, testClass2, TESTNG);
        assertInvocation(TEST_BEFORE_CLASS, testClass2, JUNIT4, TESTNG);
        assertInvocation(LISTENER_AFTER_CREATE_TEST_OBJECT, testClass2, JUNIT3, JUNIT4);
        assertInvocation(LISTENER_BEFORE_TEST_SET_UP, testClass2);
        assertInvocation(TEST_SET_UP, testClass2);
        assertInvocation(LISTENER_BEFORE_TEST_METHOD, testClass2);
        assertInvocation(TEST_METHOD, testClass2);
        assertInvocation(LISTENER_AFTER_TEST_METHOD, testClass2);
        assertInvocation(TEST_TEAR_DOWN, testClass2);
        assertInvocation(LISTENER_AFTER_TEST_TEARDOWN, testClass2);
        // testClass 2, testMethod 2
        assertInvocation(LISTENER_AFTER_CREATE_TEST_OBJECT, testClass2, JUNIT3, JUNIT4);
        assertInvocation(LISTENER_BEFORE_TEST_SET_UP, testClass2);
        assertInvocation(TEST_SET_UP, testClass2);
        assertInvocation(LISTENER_BEFORE_TEST_METHOD, testClass2);
        assertInvocation(TEST_METHOD, testClass2);
        assertInvocation(LISTENER_AFTER_TEST_METHOD, testClass2);
        assertInvocation(TEST_TEAR_DOWN, testClass2);
        assertInvocation(LISTENER_AFTER_TEST_TEARDOWN, testClass2);
        assertInvocation(TEST_AFTER_CLASS, testClass2, JUNIT4, TESTNG);
        assertNoMoreInvocations();

        assertEquals(4, testExecutor.getRunCount());
        assertEquals(0, testExecutor.getFailureCount());
    }


    protected void assertInvocation(Invocation invocation, Class<?> testClass, TestFramework... testFrameworks) {
        if (isApplicableFor(testFrameworks)) {
            if (!getCallListIterator().hasNext()) {
                fail("No more invocations in calllist. Calllist:\n" + toString(tracingTestListener.getCallList()));
            }
            assertEquals("Calllist:\n" + toString(tracingTestListener.getCallList()), new Call(invocation, testClass), getCallListIterator().next());
        }
    }

    protected void assertNoMoreInvocations() {
        assertFalse("No more invocations expected, calllist:\n" + toString(tracingTestListener.getCallList()), getCallListIterator().hasNext());
    }


    private Iterator<Call> getCallListIterator() {
        if (callListIterator == null) {
            callListIterator = tracingTestListener.getCallList().iterator();
        }
        return callListIterator;
    }

    protected boolean isApplicableFor(TestFramework[] testFrameworks) {
        if (testFrameworks.length == 0) {
            return true;
        }
        for (TestFramework framework : testFrameworks) {
            if (framework.equals(this.testFramework)) {
                return true;
            }
        }
        return false;
    }

    private String toString(List<Call> callList) {
        StringBuffer result = new StringBuffer();
        for (Call call : callList) {
            result.append(call.toString()).append("\n");
        }
        return result.toString();
    }

}
