/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils;

import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.TestClassMethodsRunner;
import org.junit.internal.runners.TestClassRunner;
import org.junit.internal.runners.TestMethodRunner;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;

import java.lang.reflect.Method;

/**
 * todo javadoc
 */
public class UnitilsJUnit4TestClassRunner extends TestClassRunner {

    private static TestListener testListener;


    public UnitilsJUnit4TestClassRunner(Class<?> testClass) throws InitializationError {
        super(testClass, new CustomTestClassMethodsRunner(testClass));

        if (testListener == null) {
            testListener = createTestListener();
            testListener.beforeAll();
            createShutdownHook();
        }
    }

    private void createShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                super.run();
                testListener.afterAll();
            }
        });
    }


    protected TestListener createTestListener() {
        return Unitils.getInstance().getTestListener();
    }


    public static class CustomTestClassMethodsRunner extends TestClassMethodsRunner {

        private Object testObject;


        public CustomTestClassMethodsRunner(Class<?> testClass) {
            super(testClass);
        }


        public void run(RunNotifier notifier) {
            testListener.beforeTestClass(getTestClass());
            super.run(notifier);
            testListener.afterTestClass(getTestClass());
        }

        protected TestMethodRunner createMethodRunner(Object test, Method method, RunNotifier notifier) {

            testObject = test;
            testListener.beforeTestSetUp(testObject);
            return new CustomTestMethodRunner(testObject, method, notifier, methodDescription(method));
        }

        protected void invokeTestMethod(Method method, RunNotifier notifier) {

            super.invokeTestMethod(method, notifier);
            testListener.afterTestTearDown(testObject);
        }
    }

    public static class CustomTestMethodRunner extends TestMethodRunner {

        private Object testObject;

        private Method testMethod;

        public CustomTestMethodRunner(Object testObject, Method testMethod, RunNotifier notifier, Description description) {
            super(testObject, testMethod, notifier, description);
            this.testObject = testObject;
            this.testMethod = testMethod;
        }


        @Override
        protected void runUnprotected() {
            testListener.beforeTestMethod(testObject, testMethod);
            super.runUnprotected();
            testListener.afterTestMethod(testObject, testMethod);
        }
    }
}
