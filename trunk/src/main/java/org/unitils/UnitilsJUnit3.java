/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils;

import junit.framework.TestCase;
import junit.framework.TestResult;
import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;

/**
 * todo test logging of exceptions in different hook methods (already fixed in runbare: exceptions were not logged)
 * javadoc
 */
public abstract class UnitilsJUnit3 extends TestCase {

    /* Logger */
    private static final Logger logger = Logger.getLogger(UnitilsJUnit3.class);

    private static Unitils unitils;


    public UnitilsJUnit3() {
        this(null);
    }

    public UnitilsJUnit3(String name) {
        super(name);

        if (unitils == null) {
            unitils = Unitils.getInstance();
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

    public void run(TestResult result) {
        try {
            unitils.beforeTestClass(this);
        } catch (UnitilsException e) {
            logger.error("Error in Unitils beforeTestClass", e);
            throw e;
        }
        super.run(result);
        try {
            unitils.afterTestClass(this);
        } catch (UnitilsException e) {
            logger.error("Error in Unitils afterTestClass", e);
            throw e;
        }
    }

    public void runBare() throws Throwable {
        try {
            unitils.beforeTestMethod(this, getCurrentTestMethod());
        } catch (Throwable e) {
            logger.error(e);
            throw e;
        }
        super.runBare();
        try {
            unitils.afterTestMethod(this, getCurrentTestMethod());
        } catch (Throwable e) {
            logger.error(e);
            throw e;
        }
    }

    private Method getCurrentTestMethod() {
        String methodName = getName();
        Method method = null;
        try {
            method = getClass().getMethod(methodName);
        } catch (NoSuchMethodException e) {
            throw new UnitilsException(e);
        }
        return method;
    }


}
