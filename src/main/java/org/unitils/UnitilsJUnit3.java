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

import junit.framework.TestCase;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;

import java.lang.reflect.Method;

/**
 * Base test class that will Unitils-enable your test. This base class will make sure that the
 * core unitils test listener methods are invoked in the expected order. See {@link TestListener} for
 * more information on the listener invocation order.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public abstract class UnitilsJUnit3 extends TestCase {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(UnitilsJUnit3.class);

    /* The main test listener, that hooks this test into unitils */
    private static TestListener testListener;

    /* True if beforeAll was succesfully called */
    private static boolean beforeAllCalled;

    /* The class to which the last test belonged to */
    private static Class<?> lastTestClass;


    /**
     * Creates a test without a name. Be sure to call {@link #setName} afterwards.
     */
    public UnitilsJUnit3() {
        this(null);
    }


    /**
     * Creates a test with the given name. The name should be the name of the test method.
     *
     * @param name the name of the test method
     */
    public UnitilsJUnit3(String name) {
        super(name);

        if (testListener == null) {
            testListener = createUnitils().createTestListener();
            createShutdownHook();
        }
    }


    /**
     * Overriden JUnit3 method to be able to call {@link TestListener#beforeAll}, {@link TestListener#beforeTestClass},
     * {@link TestListener#afterTestClass}, {@link TestListener#beforeTestSetUp} and {@link TestListener#afterTestTearDown}.
     * <p/>
     * JUnit3 does not have a concept of class level hooks, such as BeforeClass and AfterClass in JUnit4. Therefore
     * we need to simulate this behavior for unitils.
     * When a test is about to be run that belongs to a new test class, we first call the afterTestClass of the previous class
     * and the beforeTestClass of that new class. The last afterTestClass is called just before the afterAll,
     * during the shutdown of the VM.
     */
    public void runBare() throws Throwable {

        // if this the first test, call beforeAll
        if (!beforeAllCalled) {
            testListener.beforeAll();
            beforeAllCalled = true;
        }

        // simulate class level methods
        // if this is the first test of a test class (previous test was of a different test class),
        // first finalize the previous test class by calling afterTestClass, then call beforeTestClass
        // to start the new one
        Class testClass = getClass();
        if (lastTestClass != testClass) {
            if (lastTestClass != null) {
                try {
                    testListener.afterTestClass(lastTestClass);

                } catch (Throwable t) {
                    logger.error("An exception occured during afterTestClass.", t);
                }
            }
            testListener.beforeTestClass(testClass);
            lastTestClass = testClass;
        }

        Throwable firstThrowable = null;
        try {
            testListener.beforeTestSetUp(this);
            super.runBare();

        } catch (Throwable t) {
            // hold exception until later, first call afterTestTearDown
            firstThrowable = t;
        }

        try {
            testListener.afterTestTearDown(this);

        } catch (Throwable t) {
            // first exception is typically the most meaningful, so ignore second exception
            if (firstThrowable == null) {
                firstThrowable = t;
            }
        }

        // if there were exceptions, throw the first one
        if (firstThrowable != null) {
            throw firstThrowable;
        }
    }


    /**
     * Overriden JUnit3 method to be able to call {@link TestListener#beforeTestMethod} and
     * {@link TestListener#afterTestMethod}.
     */
    protected void runTest() throws Throwable {

        Throwable firstThrowable = null;
        try {
            testListener.beforeTestMethod(this, getCurrentTestMethod());
            super.runTest();

        } catch (Throwable t) {
            // hold exception until later, first call afterTestMethod
            firstThrowable = t;
        }

        try {
            testListener.afterTestMethod(this, getCurrentTestMethod());

        } catch (Throwable t) {
            // first exception is typically the most meaningful, so ignore second exception
            if (firstThrowable == null) {
                firstThrowable = t;
            }
        }

        // if there were exceptions, throw the first one
        if (firstThrowable != null) {
            throw firstThrowable;
        }
    }


    /**
     * Creates and initializes a unitils core instance.
     * <p/>
     * This will create and set the default singleton instance by calling {@link Unitils#initSingletonInstance}.
     * <p/>
     * You can override this method to let it create and set your own singleton instance. For example, you
     * can let it create an instance of your own Unitils subclass and set it by using {@link Unitils#setInstance}.
     *
     * @return the unitils core instance, not null
     */
    protected Unitils createUnitils() {

        Unitils unitils = Unitils.getInstance();
        if (unitils == null) {
            Unitils.initSingletonInstance();
            unitils = Unitils.getInstance();
        }
        return unitils;
    }


    /**
     * Gets the method that has the same name as the current test.
     *
     * @return the method, not null
     * @throws UnitilsException if the method could not be found
     */
    protected Method getCurrentTestMethod() {

        String testName = getName();
        if (StringUtils.isEmpty(testName)) {
            throw new UnitilsException("Unable to find current test method. No test name provided (null) for test. Test class: " + getClass());
        }

        try {
            return getClass().getMethod(getName());

        } catch (NoSuchMethodException e) {
            throw new UnitilsException("Unable to find current test method. Test name: " + getName() + " , test class: " + getClass(), e);
        }
    }


    /**
     * Creates a hook that will call {@link TestListener#afterTestClass} of the last test class and
     * {@link TestListener#afterAll} during the shutdown of the VM.
     * <p/>
     * This seems te be the only way in JUnit3 to this, since there is no way (without writing a custom test runner) to
     * able to know when all test (of a class or in total) have run.
     */
    protected void createShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                if (testListener != null) {
                    if (lastTestClass != null) {
                        testListener.afterTestClass(lastTestClass);
                    }
                    testListener.afterAll();
                }
            }
        });
    }

}
