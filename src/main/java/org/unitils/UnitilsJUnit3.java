/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils;

import junit.framework.TestCase;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;

import java.lang.reflect.Method;

/**
 * todo test logging of exceptions in different hook methods (already fixed in runbare: exceptions were not logged)
 * javadoc
 */
public abstract class UnitilsJUnit3 extends TestCase {

    /* Logger */
    private static final Logger logger = Logger.getLogger(UnitilsJUnit3.class);

    private TestListener testListener;

    private static Class<?> lastTestClass;


    public UnitilsJUnit3() {
        this(null);
    }

    public UnitilsJUnit3(String name) {
        super(name);
        testListener = createTestListener();
    }


    private void createShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                super.run();
                if (lastTestClass != null) {
                    testListener.afterTestClass(lastTestClass);
                }
                testListener.afterAll();
            }
        });
    }


    public void runBare() throws Throwable {

        Class testClass = getClass();

        if (lastTestClass == null) {
            testListener.beforeAll();
            createShutdownHook();
        }

        if (lastTestClass != testClass) {
            if (lastTestClass != null) {
                testListener.afterTestClass(lastTestClass);
            }
            testListener.beforeTestClass(testClass);
            lastTestClass = testClass;
        }

        try {
            testListener.beforeTestSetUp(this);
        } catch (Throwable e) {  //todo remove??
            logger.error(e);
            throw e;
        }
        super.runBare();
        try {
            testListener.afterTestTearDown(this);
        } catch (Throwable e) {
            logger.error(e);
            throw e;
        }
    }


    protected void runTest() throws Throwable {
        try {
            testListener.beforeTestMethod(this, getCurrentTestMethod());
        } catch (Throwable e) {
            logger.error(e);
            throw e;
        }
        super.runTest();
        try {
            testListener.afterTestMethod(this, getCurrentTestMethod());
        } catch (Throwable e) {
            logger.error(e);
            throw e;
        }
    }

    protected TestListener createTestListener() {
        return Unitils.getInstance().getTestListener();
    }


    private Method getCurrentTestMethod() {

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


}
