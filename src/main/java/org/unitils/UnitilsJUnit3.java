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
import java.util.HashMap;
import java.util.Map;

/**
 * todo test logging of exceptions in different hook methods (already fixed in runbare: exceptions were not logged)
 * javadoc
 */
public abstract class UnitilsJUnit3 extends TestCase {

    /* Logger */
    private static final Logger logger = Logger.getLogger(UnitilsJUnit3.class);

    private TestListener testListener;

    private static Map<Class, Class> testClasses;


    public UnitilsJUnit3() {
        this(null);
    }

    public UnitilsJUnit3(String name) {
        super(name);
        testListener = createTestListener();

        if (testClasses == null) {
            testClasses = new HashMap<Class, Class>();
            testListener.beforeAll();
            createShutdownHook();
        }

        if (!testClasses.containsKey(getClass())) {
            testClasses.put(getClass(), getClass());

            try {
                testListener.beforeTestClass(this);
            } catch (UnitilsException e) {
                logger.error("Error in Unitils beforeTestClass", e);
                throw e;
            }
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


    public void runBare() throws Throwable {
        try {
            testListener.beforeTestSetUp(this);
        } catch (Throwable e) {
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
