/*
 * Copyright 2008,  Unitils.org
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

import org.junit.internal.runners.*;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.runner.notification.RunListener;
import org.unitils.core.UnitilsException;

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
public class UnitilsJUnit4TestClassRunner extends JUnit4ClassRunner implements TestRunnerAccessor{


    /**
     * Creates a test runner that runs all test methods in the given class.
     *
     * @param testClass the class, not null
     * @throws InitializationError
     */
    public UnitilsJUnit4TestClassRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }


    @Override
    public void run(final RunNotifier notifier) {
        ClassRoadie classRoadie = new ClassRoadie(notifier, getTestClass(), getDescription(), new Runnable() {
            public void run() {
                runMethods(notifier);
            }
        });

        Unitils.getInstance().getTestContext().setRunner(this);
        try {
            getTestListener().beforeTestClass(getTestClass().getJavaClass());
            classRoadie.runProtected();
        } catch (Throwable t) {
            notifier.fireTestFailure(new Failure(getDescription(), t));
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
            testAborted(notifier, description, e.getCause());
            return;
        } catch (Exception e) {
            testAborted(notifier, description, e);
            return;
        }
        TestMethod testMethod = wrapMethod(method);
        if (!testMethod.isIgnored()) {
            getTestListener().afterCreateTestObject(testObject);
        }
        if(getTestListener().shouldInvokeTestMethod(testObject, method)) {
            createMethodRoadie(testObject, method, testMethod, notifier, description).run();
        }
    }

    private void testAborted(RunNotifier notifier, Description description,
                             Throwable e) {
        notifier.fireTestStarted(description);
        notifier.fireTestFailure(new Failure(description, e));
        notifier.fireTestFinished(description);
    }


    /**
     * Returns the JUnit 4 MethodRoadie object that is used to execute the test method
     *
     * @param testObject      The test instance, not null
     * @param testMethod      The test method, not null
     * @param jUnitTestMethod The JUnit test method
     * @param notifier        The run listener, not null
     * @param description     A test description
     * @return An JUnit MethodRoadie
     */
    protected MethodRoadie createMethodRoadie(Object testObject, Method testMethod, TestMethod jUnitTestMethod, RunNotifier notifier, Description description) {
        return new TestListenerInvokingMethodRoadie(testObject, testMethod, jUnitTestMethod, notifier, description);
    }

    /**
     * This method allows access to the test invocation, from within the modules (who get a reference to the runner
     * via the context). We do not check for listeners veto's (typically, these modules will veto the execution of
     * the test in normal execution mode, and call this method later on to do their logic).
     * 
     * @param testObject
     * @param method 
     */
    public void executeTestMethod(Object testObject, Method method) {
        TestMethod testMethod = wrapMethod(method);        
        RunNotifier notifier = new RunNotifier();
        InterceptingListener listener = new InterceptingListener();
        notifier.addListener(listener);
        Description description = methodDescription(method);
        createMethodRoadie(testObject, method, testMethod, notifier, description).run();
        if(listener.isFailed()) {
            StringBuilder msg = new StringBuilder("Exeception while executing ");
            msg.append(method.getName()).append(": ").append(listener.getFailure().getDescription());
            throw new UnitilsException(msg.toString(),listener.getFailure().getException());
        }
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
                addFailure(t);
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
                addFailure(t);
            }
        }


        /**
         * Registers a test failure
         *
         * @param t The exception, not null
         */
        @Override
        protected void addFailure(Throwable t) {
            // first exception is typically the most meaningful, so ignore second exception
            if (throwable == null) {
                throwable = t;
                super.addFailure(t);
            }
        }
    }


    /**
     * @return The unitils test listener
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

class InterceptingListener extends RunListener {
    
    private Failure failure = null;    

    @Override
    public void testFailure(Failure failure) throws Exception {
        this.failure = failure;
    }        
    
    public boolean isFailed() {
        return this.failure != null;
    }
    
    public Failure getFailure() {
        return this.failure;
    }
    
    
}
