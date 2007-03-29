/*
 * Copyright 2006 the original author or authors.
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
import org.testng.annotations.*;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;

/**
 * Base test class that will Unitils-enable your test. This base class will make sure that the
 * core unitils test listener methods are invoked in the expected order. See {@link TestListener} for
 * more information on the listener invocation order.
 *
 * @author Tim Ducheyne
 */
public abstract class UnitilsTestNG implements IHookable {

    /* The main test listener, that hooks this test into unitils */
    private static TestListener testListener;

    /* True if beforeTestClass was called */
    private static boolean beforeTestClassCalled = false;

    /* True if beforeTestSetUp was called */
    private static boolean beforeTestSetUpCalled = false;


    /**
     * Called at the beginning of the test run. This will initialize unitils and the test listener
     * and call {@link TestListener#beforeAll}.
     */
    @BeforeSuite
    protected void unitilsBeforeSuite() {

        testListener = getUnitils().createTestListener();
        testListener.beforeAll();
    }


    /**
     * Called at the end of the test run. This is where {@link TestListener#afterAll} is called.
     */
    @AfterSuite(alwaysRun = true)
    protected void unitilsAfterSuite() {
        testListener.afterAll();
    }


    /**
     * Called before a test of a test class is run. This is where {@link TestListener#beforeTestClass} is called.
     */
    @BeforeClass
    protected void unitilsBeforeClass() {
        beforeTestClassCalled = true;
        testListener.beforeTestClass(getClass());
    }


    /**
     * Called after all tests of a test class were run. This is where {@link TestListener#afterTestClass} is called.
     * <p/>
     * NOTE: alwaysRun is enabled to be sure that this method is called even when an exception occurs during
     * {@link #unitilsBeforeClass}.
     */
    @AfterClass(alwaysRun = true)
    protected void unitilsAfterClass() {
        // alwaysRun is enaled, extra test to ensure that unitilsBeforeClass was called
        if (beforeTestClassCalled) {
            beforeTestClassCalled = false;
            testListener.afterTestClass(getClass());
        }
    }


    /**
     * Called before all test setup. This is where {@link TestListener#beforeTestSetUp} is called.
     */
    @BeforeMethod
    protected void unitilsBeforeTestSetUp() {
        beforeTestSetUpCalled = true;
        testListener.beforeTestSetUp(this);
    }


    /**
     * Called after all test tear down. This is where {@link TestListener#afterTestTearDown} is called.
     * <p/>
     * NOTE: alwaysRun is enabled to be sure that this method is called even when an exception occurs during
     * {@link #unitilsBeforeTestSetUp}.
     */
    @AfterMethod(alwaysRun = true)
    protected void unitilsAfterTestTearDown() {
        // alwaysRun is enaled, extra test to ensure that unitilsBeforeTestSetUp was called
        if (beforeTestSetUpCalled) {
            beforeTestSetUpCalled = false;
            testListener.afterTestTearDown(this);
        }
    }


    /**
     * Implementation of the hookable interface to be able to call {@link TestListener#beforeTestMethod} and
     * {@link TestListener#afterTestMethod}.
     *
     * @param callBack   the TestNG test callback, not null
     * @param testResult the TestNG test result, not null
     */
    public void run(IHookCallBack callBack, ITestResult testResult) {

        RuntimeException firstRuntimeException = null;
        try {
            testListener.beforeTestMethod(this, testResult.getMethod().getMethod());
            callBack.runTestMethod(testResult);

        } catch (RuntimeException e) {
            // hold exception until later, first call afterTestMethod
            firstRuntimeException = e;
        }

        try {
            testListener.afterTestMethod(this, testResult.getMethod().getMethod());

        } catch (RuntimeException e) {
            // first exception is typically the most meaningful, so ignore second exception
            if (firstRuntimeException == null) {
                firstRuntimeException = e;
            }
        }

        // if there were exceptions, throw the first one
        if (firstRuntimeException != null) {
            throw firstRuntimeException;
        }
    }


    /**
     * This will return the default singleton instance by calling {@link Unitils#getInstance()}.
     * <p/>
     * You can override this method to let it create and set your own singleton instance. For example, you
     * can let it create an instance of your own Unitils subclass and set it by using {@link Unitils#setInstance}.
     *
     * @return the unitils core instance, not null
     */
    protected Unitils getUnitils() {
        return Unitils.getInstance();
    }

}
