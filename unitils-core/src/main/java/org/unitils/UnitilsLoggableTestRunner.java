package org.unitils;

import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.MethodRoadie;
import org.junit.internal.runners.TestMethod;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

import java.lang.reflect.Method;

public class UnitilsLoggableTestRunner extends UnitilsJUnit4TestClassRunner {
    public UnitilsLoggableTestRunner(Class<?> testClass)
            throws InitializationError {
        super(testClass);
    }

    protected MethodRoadie createMethodRoadie(Object testObject, Method testMethod, TestMethod jUnitTestMethod, RunNotifier notifier, Description description) {
        return new LoggingTestListenerInvokingMethodRoadie(testObject, testMethod, jUnitTestMethod, notifier, description);
    }

    protected class LoggingTestListenerInvokingMethodRoadie extends UnitilsJUnit4TestClassRunner.TestListenerInvokingMethodRoadie {
        public LoggingTestListenerInvokingMethodRoadie(Object testObject, Method testMethod, TestMethod jUnitTestMethod, RunNotifier notifier, Description description) {
            super(testObject, testMethod, jUnitTestMethod, notifier, description);
        }

        protected void runTestMethod() {
            String name = this.testMethod.getName();
            System.out.println("\n--------- " + name + " started --------- ");
            super.runTestMethod();
            System.out.println("--------- " + name + " ended ----------- \n");
        }
    }
}