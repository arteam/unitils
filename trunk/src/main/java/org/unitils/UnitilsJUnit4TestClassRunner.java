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

import org.junit.Ignore;
import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.TestClassMethodsRunner;
import org.junit.internal.runners.TestClassRunner;
import org.junit.internal.runners.TestMethodRunner;
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
public class UnitilsJUnit4TestClassRunner extends TestClassRunner {

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
        super(testClass, new CustomTestClassMethodsRunner(testClass));

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
            testListener.beforeTestClass(getTestClass());
            super.run(notifier);

        } catch (Throwable t) {
            notifier.fireTestFailure(new Failure(getDescription(), t));
        }

        try {
            testListener.afterTestClass(getTestClass());

        } catch (Throwable t) {
            notifier.fireTestFailure(new Failure(getDescription(), t));
        }
    }


    /**
     * Custom runner that runs all test methods in the given class and invokes the unitils test listener methods
     * at the apropriate moments.
     */
    public static class CustomTestClassMethodsRunner extends TestClassMethodsRunner {

        /* The current test instance */
        private Object testObject;


        /**
         * Creates a runner for all test methods in the given class
         *
         * @param testClass the class, not null
         */
        public CustomTestClassMethodsRunner(Class<?> testClass) {
            super(testClass);
        }


        /**
         * Overriden JUnit4 method to be able to call {@link TestListener#beforeTestSetUp}.
         * This will also create a custom test method runner that enables us to call the before and after
         * test methods.
         */
        @Override
        protected TestMethodRunner createMethodRunner(Object test, Method method, RunNotifier notifier) {

            if (!isIgnored(method)) {
                testObject = test;   // store current test for afterTestTearDown
                testListener.beforeTestSetUp(testObject);
            }
            return new CustomTestMethodRunner(test, method, notifier, methodDescription(method));
        }


        /**
         * Overriden JUnit4 method to be able to call {@link TestListener#afterTestTearDown}.
         */
        @Override
        protected void invokeTestMethod(Method method, RunNotifier notifier) {
            try {
                super.invokeTestMethod(method, notifier);

            } catch (Throwable t) {
                notifier.fireTestFailure(new Failure(getDescription(), t));
            }

            if (!isIgnored(method)) {
                try {
                    testListener.afterTestTearDown(testObject);

                } catch (Throwable t) {
                    notifier.fireTestFailure(new Failure(getDescription(), t));
                }
            }
            testObject = null;
        }


        /**
         * Checks whether the given test method should be skipped.
         *
         * @param testMethod the method, not null
         * @return true if ignored
         */
        public boolean isIgnored(Method testMethod) {
            return testMethod.getAnnotation(Ignore.class) != null;
        }
    }


    /**
     * Custom runner that runs the given test method and invokes the unitils test listener methods
     * at the apropriate moments.
     */
    public static class CustomTestMethodRunner extends TestMethodRunner {

        /* The test instance on which to invoke the test method */
        private Object testObject;

        /* The test method */
        private Method testMethod;


        /**
         * Creates a runner for the given test method.
         *
         * @param testObject  the test instance on which to invoke the test method, not null
         * @param testMethod  the test method, not null
         * @param notifier    JUnits test listener, not null
         * @param description the descriptor for the test, not null
         */
        public CustomTestMethodRunner(Object testObject, Method testMethod, RunNotifier notifier, Description description) {
            super(testObject, testMethod, notifier, description);
            this.testObject = testObject;
            this.testMethod = testMethod;
        }


        /**
         * Overriden JUnit4 method to be able to call {@link TestListener#beforeTestMethod} and
         * {@link TestListener#afterTestMethod}.
         */
        @Override
        protected void executeMethodBody() throws IllegalAccessException, InvocationTargetException {

            Throwable throwable = null;
            try {
                testListener.beforeTestMethod(testObject, testMethod);
                super.executeMethodBody();

            } catch (Throwable t) {  // hold exceptions until later, first call afterTestMethod
                throwable = t;
            }

            try {
                testListener.afterTestMethod(testObject, testMethod, throwable);

            } catch (Throwable t) {
                // first exception is typically the most meaningful, so ignore second exception
                if (throwable == null) {
                    throwable = t;
                }
            }

            if (throwable instanceof IllegalAccessException) {
                throw (IllegalAccessException) throwable;
            }
            if (throwable instanceof InvocationTargetException) {
                throw (InvocationTargetException) throwable;
            }
            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            }
            if (throwable != null) {
                throw new RuntimeException(throwable);
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
}

