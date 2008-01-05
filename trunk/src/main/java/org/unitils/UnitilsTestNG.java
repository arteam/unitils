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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
 * @author Filip Neven
 */
public abstract class UnitilsTestNG implements IHookable {

    /* True if beforeTestClass was called */
    private static boolean beforeTestClassCalled = false;

    /* True if beforeTestSetUp was called */
    private static boolean beforeTestSetUpCalled = false;


    /**
     * Called at the beginning of the test run. This will initialize unitils and the test listener
     * and call {@link TestListener#beforeAll}.
     */
    @BeforeSuite(alwaysRun = true)
    protected void unitilsBeforeSuite() {
        getTestListener().beforeAll();
    }


    /**
     * Called at the end of the test run. This is where {@link TestListener#afterAll} is called.
     */
    @AfterSuite(alwaysRun = true)
    protected void unitilsAfterSuite() {
        getTestListener().afterAll();
    }


    /**
     * Called before a test of a test class is run. This is where {@link TestListener#beforeTestClass} is called.
     */
    @BeforeClass(alwaysRun = true)
    protected void unitilsBeforeClass() {
        beforeTestClassCalled = true;
        getTestListener().beforeTestClass(getClass());
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
            getTestListener().afterTestClass(getClass());
        }
    }


    /**
     * Called before all test setup. This is where {@link TestListener#beforeTestSetUp} is called.
     */
    @BeforeMethod(alwaysRun = true)
    protected void unitilsBeforeTestSetUp(Method testMethod) {
        beforeTestSetUpCalled = true;
        getTestListener().beforeTestSetUp(this, testMethod);
    }


    /**
     * Called after all test tear down. This is where {@link TestListener#afterTestTearDown} is called.
     * <p/>
     * NOTE: alwaysRun is enabled to be sure that this method is called even when an exception occurs during
     * {@link #unitilsBeforeTestSetUp}.
     */
    @AfterMethod(alwaysRun = true)
    protected void unitilsAfterTestTearDown(Method testMethod) {
        // alwaysRun is enabled, extra test to ensure that unitilsBeforeTestSetUp was called
        if (beforeTestSetUpCalled) {
            beforeTestSetUpCalled = false;
            getTestListener().afterTestTearDown(this, testMethod);
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
        Throwable firstException = null;
        try {
            getTestListener().beforeTestMethod(this, testResult.getMethod().getMethod());
        
        } catch (Throwable e) {
            // hold exception until later, first call afterTestMethod
            firstException = e;
        }
        
        if (firstException == null) {
        	callBack.runTestMethod(testResult);
        	Throwable testMethodException = testResult.getThrowable();
			if (testMethodException != null) {
				// The exception was wrapped in an InvocationTargetException, since the test method is
				// invoked using reflection
        		if (testMethodException instanceof InvocationTargetException) {
        			testMethodException = ((InvocationTargetException) testMethodException).getCause();
        		}
        		firstException = testMethodException;
        	}
        }

        try {
            getTestListener().afterTestMethod(this, testResult.getMethod().getMethod(), firstException);

        } catch (Throwable e) {
            // first exception is typically the most meaningful, so ignore second exception
            if (firstException == null) {
                firstException = e;
            }
        }

        // if there were exceptions, throw the first one
        if (firstException != null) {
            if (firstException instanceof RuntimeException) {
            	throw (RuntimeException) firstException;
            } else {
            	throw new RuntimeException(firstException);
            }
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
    
    protected TestListener getTestListener() {
    	return getUnitils().getTestListener();
    }

}
