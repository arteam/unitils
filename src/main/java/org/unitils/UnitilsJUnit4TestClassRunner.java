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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.internal.runners.*;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Custom test runner that will Unitils-enable your test. This will make sure that the
 * core unitils test listener methods are invoked in the expected order. See {@link TestListener} for
 * more information on the listener invocation order.
 * <p/>
 * NOTE: if a test fails, the error is logged as debug logging. This is a temporary work-around for
 * a problem with IntelliJ JUnit-4 runner that reports a 'Wrong test finished' error when something went wrong
 * in the before. [IDEA-12498]
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class UnitilsJUnit4TestClassRunner extends JUnit4ClassRunner {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(UnitilsJUnit4TestClassRunner.class);

    private static boolean shutdownHookCreated = false;
    
    /* True if beforeAll was successfully called */
    private static boolean beforeAllCalled = false;


    /**
     * Creates a test runner that runs all test methods in the given class.
     *
     * @param testClass the class, not null
     * @throws InitializationError
     */
    public UnitilsJUnit4TestClassRunner(Class<?> testClass) throws InitializationError {
        super(testClass);

        if (!shutdownHookCreated) {
            createShutdownHook();
            shutdownHookCreated = true;
        }
    }


    @Override
    public void run(final RunNotifier notifier) {
        // if this is the first test, call beforeAll
        if (!beforeAllCalled) {
            try {
                getTestListener().beforeAll();
                beforeAllCalled = true;

            } catch (Throwable t) {
                logger.debug(getDescription(), t);
                notifier.testAborted(getDescription(), t);
                return;
            }
        }

        ClassRoadie classRoadie = new ClassRoadie(notifier, getTestClass(), getDescription(), new Runnable() {
            public void run() {
                runMethods(notifier);
            }
        });

        Throwable throwable = null;
        try {
        	getTestListener().beforeTestClass(getTestClass().getJavaClass());
            classRoadie.runProtected();
        } catch (Throwable t) {
            notifier.fireTestFailure(new Failure(getDescription(), t));
            throwable = t;
        }
        try {
        	getTestListener().afterTestClass(getTestClass().getJavaClass());
        } catch (Throwable t) {
            // first exception is typically the most meaningful, so ignore second exception
            if (throwable == null) {
                notifier.fireTestFailure(new Failure(getDescription(), t));
            }
        }
    }


    /**
     * Overridden JUnit4 method to be able to create a CustomMethodRoadie that will invoke the
     * unitils test listener methods at the appropriate moments.
     */
    protected void invokeTestMethod(Method method, RunNotifier notifier) {
        Description description = methodDescription(method);
        Object testObject;
        try {
            testObject = createTest();
        } catch (InvocationTargetException e) {
            notifier.testAborted(description, e.getCause());
            return;
        } catch (Exception e) {
            notifier.testAborted(description, e);
            return;
        }
        TestMethod testMethod = wrapMethod(method);
        getMethodRoadie(method, notifier, description, testObject, testMethod).run();
    }


    /**
     * Returns the JUnit 4 MethodRoadie object that is used to execute the test method
     * 
     * @param method
     * @param notifier
     * @param description
     * @param testObject
     * @param testMethod
     * @return An implementation of <code>org.junit.internal.runners.MethodRoadie</code>
     */
	protected MethodRoadie getMethodRoadie(Method method,
			RunNotifier notifier, Description description, Object testObject,
			TestMethod testMethod) {
		
		return new TestListenerInvokingMethodRoadie(testObject, method, testMethod, notifier, description);
	}


    /**
     * Custom method roadie that invokes the unitils test listener methods at the apropriate moments.
     */
    protected class TestListenerInvokingMethodRoadie extends MethodRoadie {


        /* Instance under test */
        protected Object testObject;

        /* Method under test */
        protected Method testMethod;
        
        protected Throwable throwable;


        /**
         * Creates a method roadie.
         *
         * @param testObject      The test instance, not null
         * @param testMethod      The test method, not null
         * @param jUnitTestMethod The JUnit test method
         * @param notifier        The run listener, not null
         * @param description     A test description
         */
        public TestListenerInvokingMethodRoadie(Object testObject, Method testMethod, TestMethod jUnitTestMethod, RunNotifier notifier, Description description) {
            super(testObject, jUnitTestMethod, notifier, description);
            this.testObject = testObject;
            this.testMethod = testMethod;
        }


        /**
         * Overriden JUnit4 method to be able to call {@link TestListener#afterTestTearDown}.
         */
        @Override
        public void runBeforesThenTestThenAfters(Runnable test) {
            try {
            	getTestListener().beforeTestSetUp(testObject, testMethod);
            } catch (Throwable t) {
                addFailure(t);
            }
            if (throwable == null) {
            	super.runBeforesThenTestThenAfters(test);
            }
            try {
            	getTestListener().afterTestTearDown(testObject, testMethod);
            } catch (Throwable t) {
                // first exception is typically the most meaningful, so ignore second exception
                if (throwable == null) {
                    addFailure(t);
                }
            }
        }


        @Override
        protected void runTestMethod() {
            try {
            	getTestListener().beforeTestMethod(testObject, testMethod);
            } catch (Throwable t) {
                addFailure(t);
            }
            if (throwable == null) {
            	super.runTestMethod();
            }
            try {
            	getTestListener().afterTestMethod(testObject, testMethod, throwable);
            } catch (Throwable t) {
                // first exception is typically the most meaningful, so ignore second exception
                addFailure(t);
            }
        }
        
        
        @Override
        protected void addFailure(Throwable e) {
        	if (throwable == null) {
        		throwable = e;
        		super.addFailure(e);
        	}
    	}
    }


    /**
     * Creates a hook that will call {@link TestListener#afterAll} during the shutdown of the VM.
     * <p/>
     * This seems te be the only way in JUnit4 to this, since there is no way to
     * able to know when all tests have run.
     */
    protected void createShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (getTestListener() != null) {
                	getTestListener().afterAll();
                }
            }
        });
    }
    
    /**
	 * @return The unitils test listener
	 */
	protected TestListener getTestListener() {
		return getUnitils().getTestListener();
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

