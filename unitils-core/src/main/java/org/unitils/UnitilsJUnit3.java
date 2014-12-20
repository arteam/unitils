/*
 * Copyright 2013,  Unitils.org
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
import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.unitils.core.engine.UnitilsTestListener;

import java.lang.reflect.Method;

/**
 * Base test class that will Unitils-enable your test. This base class will make sure that the
 * core unitils test listener methods are invoked in the expected order.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
// todo unit test
public abstract class UnitilsJUnit3 extends TestCase {

    /* Keeps track of the test class for which tests are currently being executed. */
    protected static Class<?> currentTestClass;


    /**
     * Creates a test without a name. Be sure to call {@link junit.framework.TestCase#setName} afterwards.
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
    }


    /**
     * Overridden JUnit3 method to be able to call beforeTestSetUp and afterTestTearDown.
     *
     * @throws Throwable If an error occurs during the test
     */
    @Override
    public void runBare() throws Throwable {
        // simulate class level methods
        // if this is the first test of a test class (previous test was of a different test class),
        // first finalize the previous test class by calling afterTestClass, then call beforeTestClass
        // to start the new one
        if (!getClass().equals(currentTestClass)) {
            if (currentTestClass != null) {
                try {
                    getUnitilsTestListener().afterTestClass();
                } catch (Throwable e) {
                    throw new UnitilsException("After test class failed for the previous test class: " + currentTestClass, e);
                }
            }
            currentTestClass = getClass();
            getUnitilsTestListener().beforeTestClass(getClass());
        }

        Throwable firstThrowable = null;
        try {
            getUnitilsTestListener().beforeTestSetUp(this, getCurrentTestMethod());
            super.runBare();

        } catch (Throwable t) {
            // hold exception until later, first call afterTestTearDown
            firstThrowable = t;
        }
        try {
            getUnitilsTestListener().afterTestTearDown();

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
     * Overridden JUnit3 method to be able to call beforeTestMethod and afterTestMethod.
     *
     * @throws Throwable If an error occurs during the test
     */
    @Override
    protected void runTest() throws Throwable {
        Throwable firstThrowable = null;
        try {
            getUnitilsTestListener().beforeTestMethod();
            super.runTest();

        } catch (Throwable t) {
            // hold exception until later, first call afterTestMethod
            firstThrowable = t;
        }
        try {
            getUnitilsTestListener().afterTestMethod(firstThrowable);

        } catch (Throwable t) {
            // first exception is typically the most meaningful, so ignore second exception
            if (firstThrowable == null) {
                firstThrowable = t;
            }
        }
        // if an exception occurred during beforeTestMethod, the test or afterTestMethod, throw it
        if (firstThrowable != null) {
            throw firstThrowable;
        }
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
     * @return The unitils test listener
     */
    protected UnitilsTestListener getUnitilsTestListener() {
        return Unitils.getUnitilsTestListener();
    }
}
