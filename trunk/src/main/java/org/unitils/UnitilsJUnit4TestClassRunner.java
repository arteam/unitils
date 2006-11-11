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
import org.junit.runner.notification.RunNotifier;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * todo javadoc
 */
public class UnitilsJUnit4TestClassRunner extends TestClassRunner {

    private static TestListener testListener;

    private static boolean beforeAllCalled;


    public UnitilsJUnit4TestClassRunner(Class<?> testClass) throws InitializationError {
        super(testClass, new CustomTestClassMethodsRunner(testClass));

        if (testListener == null) {
            testListener = getUnitils().createTestListener();
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


    @Override
    public void run(RunNotifier notifier) {

        if (!beforeAllCalled) {
            testListener.beforeAll();
            beforeAllCalled = true;
        }

        testListener.beforeTestClass(getTestClass());
        super.run(notifier);
        testListener.afterTestClass(getTestClass());
    }


    protected Unitils getUnitils() {
        Unitils unitils = Unitils.getInstance();
        if (unitils == null) {
            Unitils.initSingletonInstance();
            unitils = Unitils.getInstance();
        }
        return unitils;
    }


    public static class CustomTestClassMethodsRunner extends TestClassMethodsRunner {

        private Object testObject;


        public CustomTestClassMethodsRunner(Class<?> testClass) {
            super(testClass);
        }


        @Override
        protected TestMethodRunner createMethodRunner(Object test, Method method, RunNotifier notifier) {

            if (!isIgnored(method)) {
                testObject = test;
                testListener.beforeTestSetUp(testObject);
            }
            return new CustomTestMethodRunner(test, method, notifier, methodDescription(method));
        }

        @Override
        protected void invokeTestMethod(Method method, RunNotifier notifier) {

            super.invokeTestMethod(method, notifier);
            if (!isIgnored(method)) {
                testListener.afterTestTearDown(testObject);
                testObject = null;
            }
        }


        public boolean isIgnored(Method testMethod) {
            return testMethod.getAnnotation(Ignore.class) != null;
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
        protected void executeMethodBody() throws IllegalAccessException, InvocationTargetException {
            testListener.beforeTestMethod(testObject, testMethod);
            super.executeMethodBody();
            testListener.afterTestMethod(testObject, testMethod);
        }
    }
}
