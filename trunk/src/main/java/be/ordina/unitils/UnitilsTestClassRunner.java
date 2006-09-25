/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils;

import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.TestClassMethodsRunner;
import org.junit.internal.runners.TestClassRunner;
import org.junit.internal.runners.TestMethodRunner;
import org.junit.runner.notification.RunNotifier;

import java.lang.reflect.Method;

/**
 * todo javadoc
 */
public class UnitilsTestClassRunner extends TestClassRunner {

    private static Unitils unitils;


    public UnitilsTestClassRunner(Class<?> testClass) throws InitializationError {
        super(testClass, new CustomTestClassMethodsRunner(testClass));

        if (unitils == null) {
            unitils = new Unitils();
            unitils.beforeAll();
            createShutdownHook();
        }
    }

    private void createShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                super.run();
                unitils.afterAll();
            }
        });
    }

    public static class CustomTestClassMethodsRunner extends TestClassMethodsRunner {

        private Object testObject;


        public CustomTestClassMethodsRunner(Class<?> testClass) {
            super(testClass);
        }


        public void run(RunNotifier notifier) {
            unitils.beforeTestClass(getTestClass());
            super.run(notifier);
            unitils.afterTestClass(getTestClass());
        }

        protected TestMethodRunner createMethodRunner(Object test, Method method, RunNotifier notifier) {

            testObject = test;
            unitils.beforeTestMethod(testObject, method.getName());
            return new TestMethodRunner(testObject, method, notifier, methodDescription(method));
        }

        protected void invokeTestMethod(Method method, RunNotifier notifier) {

            super.invokeTestMethod(method, notifier);
            unitils.afterTestMethod(testObject, method.getName());
        }


    }
}
