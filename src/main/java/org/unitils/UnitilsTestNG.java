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

    /* True if beforeTestSetUp was called */
    private boolean beforeTestSetUpCalled = false;


    /**
     * Called before a test of a test class is run. This is where {@link TestListener#beforeTestClass} is called.
     */
    @BeforeClass(alwaysRun = true)
    protected void unitilsBeforeClass() {
        getTestListener().afterCreateTestObject(this);
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
        Throwable beforeTestMethodException = null;
        try {
            getTestListener().beforeTestMethod(this, testResult.getMethod().getMethod());
        
        } catch (Throwable e) {
            // hold exception until later, first call afterTestMethod
            beforeTestMethodException = e;
        }
        
        Throwable testMethodException = null;
        if (beforeTestMethodException == null) {
        	callBack.runTestMethod(testResult);
        	testMethodException = testResult.getThrowable();
        	// Since TestNG calls the method using reflection, the exception is wrapped in an InvocationTargetException
        	if (testMethodException != null && testMethodException instanceof InvocationTargetException) {
        		testMethodException = ((InvocationTargetException) testMethodException).getTargetException();
        	}
        }

        Throwable afterTestMethodException = null;
        try {
            getTestListener().afterTestMethod(this, testResult.getMethod().getMethod(), 
            		beforeTestMethodException != null ? beforeTestMethodException : testMethodException);

        } catch (Throwable e) {
            afterTestMethodException = e;
        }

        // if there were exceptions, make sure the exception that occurred first is reported by TestNG
        if (beforeTestMethodException != null) {
            throwException(beforeTestMethodException);
        } else {
        	// We don't throw the testMethodException, it is already registered by TestNG and will be reported
        	// to the user
        	if (testMethodException == null && afterTestMethodException != null) {
        		throwException(afterTestMethodException);
        	}
        }
    }


	private void throwException(Throwable exception) {
		if (exception instanceof RuntimeException) {
			throw (RuntimeException) exception;
		} else if (exception instanceof Error) {
			throw (Error) exception;
		} else {
			throw new RuntimeException(exception);
		}
	}


    /**
	 * @return The Unitils test listener
	 */
	protected TestListener getTestListener() {
		return getUnitils().getTestListener();
	}
	
	
	/**
     * Returns the default singleton instance of Unitils
     *
     * @return the Unitils instance, not null
     */
    protected Unitils getUnitils() {
        return Unitils.getInstance();
    }

}
