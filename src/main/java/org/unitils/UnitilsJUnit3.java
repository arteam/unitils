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

import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;

/**
 * Base test class that will Unitils-enable your test. This base class will make sure that the
 * core unitils test listener methods are invoked in the expected order. See {@link TestListener} for
 * more information on the listener invocation order.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public abstract class UnitilsJUnit3 extends TestCase {


	/**
     * Creates a test without a name. Be sure to call {@link TestCase#setName} afterwards.
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
     * Overriden JUnit3 method to be able to call {@link TestListener#beforeTestClass}, {@link TestListener#afterTestClass},
     * {@link TestListener#beforeTestSetUp} and {@link TestListener#afterTestTearDown}.
     * <p/>
     * JUnit3 does not have a concept of class level hooks, such as BeforeClass and AfterClass in JUnit4. Therefore
     * we need to simulate this behavior for unitils.
     * When a test is about to be run that belongs to a new test class, we first call the afterTestClass of the previous class
     * and the beforeTestClass of that new class. The last afterTestClass is called just before the afterAll,
     * during the shutdown of the VM.
     */
    @Override
    public void runBare() throws Throwable {
        getTestListener().afterCreateTestObject(this);

        Throwable firstThrowable = null;
        try {
            getTestListener().beforeTestSetUp(this, getCurrentTestMethod());
            super.runBare();

        } catch (Throwable t) {
            // hold exception until later, first call afterTestTearDown
            firstThrowable = t;
        }

        try {
            getTestListener().afterTestTearDown(this, getCurrentTestMethod());

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
    @Override
    protected void runTest() throws Throwable {
        Throwable firstThrowable = null;
        try {
            getTestListener().beforeTestMethod(this, getCurrentTestMethod());
            super.runTest();

        } catch (Throwable t) {
            // hold exception until later, first call afterTestMethod
            firstThrowable = t;
        }

        try {
            getTestListener().afterTestMethod(this, getCurrentTestMethod(), firstThrowable);

        } catch (Throwable t) {
            // first exception is typically the most meaningful, so ignore second exception
            if (firstThrowable == null) {
                firstThrowable = t;
            }
        }

        // if an exception occured during beforeTestMethod, the test or afterTestMethod, throw it
        if (firstThrowable != null) {
            throw firstThrowable;
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
	protected TestListener getTestListener() {
		return getUnitils().getTestListener();
	}

}
