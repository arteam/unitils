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

import org.junit.Assume.AssumptionViolatedException;
import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.internal.runners.MethodRoadie;
import org.junit.internal.runners.TestClass;
import org.junit.internal.runners.TestMethod;
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
 *
 * @author Tim Ducheyne
 */
public class UnitilsJUnit4TestClassRunner extends JUnit4ClassRunner {

	/* The main test listener, that hooks this test into unitils */
    private static TestListener testListener;

    /* True if beforeAll was succesfully called */
    private static boolean beforeAllCalled;
    
    /**
     * Creates a test runner that runs all test methods in the given class.
     *
     * @param testClass the class, not null
     * @throws InitializationError
     */
    public UnitilsJUnit4TestClassRunner(Class<?> testClass) throws InitializationError {
        super(testClass);

        if (testListener == null) {
            testListener = getUnitils().createTestListener();
            createShutdownHook();
        }
    }
    
    /**
     * Overriden JUnit4 method to be able to call {@link TestListener#beforeAll}, {@link TestListener#beforeTestClass},
     * {@link TestListener#afterTestClass}.
     */
    @Override
    public void run(RunNotifier notifier) {
        // if this the first test, call beforeAll
        if (!beforeAllCalled) {
            try {
                testListener.beforeAll();
                beforeAllCalled = true;

            } catch (Throwable t) {
                notifier.fireTestFailure(new Failure(getDescription(), t));
                return;
            }
        }

        try {
            testListener.beforeTestClass(getTestClass().getJavaClass());
            super.run(notifier);

        } catch (Throwable t) {
            notifier.fireTestFailure(new Failure(getDescription(), t));
        }

        try {
            testListener.afterTestClass(getTestClass().getJavaClass());

        } catch (Throwable t) {
            notifier.fireTestFailure(new Failure(getDescription(), t));
        }
    }
    
    @Override
	protected void invokeTestMethod(Method method, RunNotifier notifier) {
		Description description= methodDescription(method);
		Object test;
		try {
			test= createTest();
		} catch (InvocationTargetException e) {
			notifier.testAborted(description, e.getCause());
			return;			
		} catch (Exception e) {
			notifier.testAborted(description, e);
			return;
		}
		TestMethodWrapper testMethodWrapper = new TestMethodWrapper(method, getTestClass());
		new UnitilsMethodRoadie(test, testMethodWrapper, notifier, description).run();
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
                if (testListener != null) {
                    testListener.afterAll();
                }
            }
        });
    }
    
    protected static class UnitilsMethodRoadie extends MethodRoadie {

    	protected Object testObject;
    	
    	protected TestMethodWrapper testMethodWrapper;
    	
		/**
		 * Constructor for UnitilsMethodRoadie.
		 * @param test
		 * @param methodWrapper
		 * @param notifier
		 * @param description
		 */
		public UnitilsMethodRoadie(Object test, TestMethodWrapper methodWrapper, RunNotifier notifier, Description description) {
			super(test, methodWrapper, notifier, description);
			this.testObject = test;
			this.testMethodWrapper = methodWrapper;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void runBeforesThenTestThenAfters(Runnable test) {
			try {
				testListener.beforeTestSetUp(testObject);
			} catch (Throwable e) {
				addFailure(e);
			}			
			super.runBeforesThenTestThenAfters(test);
			try {
				testListener.afterTestTearDown(testObject);
			} catch (Throwable e) {
				if (!testMethodWrapper.hasFailed()) {
					addFailure(e);
				}
			}			
		}
		
		@Override
		protected void runTestMethod() {
			try {
				testListener.beforeTestMethod(testObject, testMethodWrapper
						.getMethod());
			} catch (Throwable e) {
				addFailure(e);
			}			
			super.runTestMethod();
			try {
				testListener.afterTestMethod(testObject, testMethodWrapper
						.getMethod(), null);
			} catch (Throwable e) {
				addFailure(e);
			}			
		}
    	
    }
    
    protected class TestMethodWrapper extends TestMethod {

    	protected Method method;
    	
    	protected boolean failed;
    	
		/**
		 * Constructor for TestMethodWrapper.
		 * @param method
		 * @param testClass
		 */
		public TestMethodWrapper(Method method, TestClass testClass) {
			super(method, testClass);
			this.method = method;
		}

		@Override
		public void invoke(Object test) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
			try {
				super.invoke(test);
			} catch (InvocationTargetException e) {
				Throwable targetException = e.getTargetException();
				// If this is not an AssumptionViolationException, and also not an expected exception, the test has failed
				if (!(targetException instanceof AssumptionViolatedException) || getExpectedException() == null || 
						!getExpectedException().isAssignableFrom(targetException.getClass())) {
					failed = true;
				}
				throw e;
			} catch (IllegalArgumentException e) {
				failed = true;
				throw e;
			} catch (IllegalAccessException e) {
				failed = true;
				throw e;
			}
		}

		/**
		 * @return Whether the execution of the junit test method caused an exception
		 */
		public boolean hasFailed() {
			return failed;
		}

		/**
		 * Getter for method.
		 * @return the method
		 */
		public Method getMethod() {
			return method;
		}
		
    }
}

