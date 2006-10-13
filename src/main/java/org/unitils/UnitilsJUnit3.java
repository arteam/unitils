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

import java.lang.reflect.Method;

/**
 * javadoc
 */
public abstract class UnitilsJUnit3 extends TestCase {

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
        unitils.beforeTestClass(this);
        super.run(result);
        unitils.afterTestClass(this);
    }

    public void runBare() throws Throwable {
        unitils.beforeTestMethod(this, getCurrentTestMethod());
        super.runBare();
        unitils.afterTestMethod(this, getCurrentTestMethod());
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
