/*
 * Copyright 2013,  Unitils.org
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

import org.testng.IHookCallBack;
import org.testng.IHookable;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.unitils.core.Unitils;
import org.unitils.core.engine.UnitilsTestListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Base test class that will Unitils-enable your test. This base class will make sure that the
 * core unitils test listener methods are invoked in the expected order.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
// todo unit test
public abstract class UnitilsTestNG implements IHookable {

    /* True if beforeTestSetUp was called */
    protected boolean beforeTestSetUpCalled = false;


    /**
     * Called before a test of a test class is run. This is where afterCreateTestObject is called.
     */
    @BeforeClass(alwaysRun = true)
    protected void unitilsBeforeClass() {
        getUnitilsTestListener().beforeTestClass(this.getClass());
    }


    /**
     * Called before all test setup. This is where beforeTestSetUp is called.
     *
     * @param testMethod The test method, not null
     */
    @BeforeMethod(alwaysRun = true)
    protected void unitilsBeforeTestSetUp(Method testMethod) {
        beforeTestSetUpCalled = true;
        getUnitilsTestListener().beforeTestSetUp(this, testMethod);
    }


    /**
     * Called after all test tear down. This is where afterTestTearDown is called.
     * <p/>
     * NOTE: alwaysRun is enabled to be sure that this method is called even when an exception occurs during
     * {@link #unitilsBeforeTestSetUp}.
     *
     * @param testMethod The test method, not null
     */
    @AfterMethod(alwaysRun = true)
    protected void unitilsAfterTestTearDown(Method testMethod) {
        // alwaysRun is enabled, extra test to ensure that unitilsBeforeTestSetUp was called
        if (beforeTestSetUpCalled) {
            beforeTestSetUpCalled = false;
            getUnitilsTestListener().afterTestTearDown();
        }
    }


    /**
     * Implementation of the hookable interface to be able to call beforeTestMethod and afterTestMethod.
     *
     * @param callBack   the TestNG test callback, not null
     * @param testResult the TestNG test result, not null
     */
    public void run(IHookCallBack callBack, ITestResult testResult) {
        Throwable beforeTestMethodException = null;
        try {
            getUnitilsTestListener().beforeTestMethod();

        } catch (Throwable e) {
            // hold exception until later, first call afterTestMethod
            beforeTestMethodException = e;
        }

        Throwable testMethodException = null;
        if (beforeTestMethodException == null) {
            callBack.runTestMethod(testResult);

            // Since TestNG calls the method using reflection, the exception is wrapped in an InvocationTargetException
            testMethodException = testResult.getThrowable();
            if (testMethodException != null && testMethodException instanceof InvocationTargetException) {
                testMethodException = ((InvocationTargetException) testMethodException).getTargetException();
            }
        }

        Throwable afterTestMethodException = null;
        try {
            Throwable testThrowable = beforeTestMethodException != null ? beforeTestMethodException : testMethodException;
            getUnitilsTestListener().afterTestMethod(testThrowable);

        } catch (Throwable e) {
            afterTestMethodException = e;
        }

        // if there were exceptions, make sure the exception that occurred first is reported by TestNG
        if (beforeTestMethodException != null) {
            throwException(beforeTestMethodException);
        } else {
            // We don't throw the testMethodException, it is already registered by TestNG and will be reported to the user
            if (testMethodException == null && afterTestMethodException != null) {
                throwException(afterTestMethodException);
            }
        }
    }


    /**
     * Throws an unchecked excepton for the given throwable.
     *
     * @param throwable The throwable, not null
     */
    protected void throwException(Throwable throwable) {
        if (throwable instanceof RuntimeException) {
            throw (RuntimeException) throwable;
        } else if (throwable instanceof Error) {
            throw (Error) throwable;
        } else {
            throw new RuntimeException(throwable);
        }
    }


    /**
     * @return The unitils test listener
     */
    protected UnitilsTestListener getUnitilsTestListener() {
        return Unitils.getUnitilsTestListener();
    }
}
