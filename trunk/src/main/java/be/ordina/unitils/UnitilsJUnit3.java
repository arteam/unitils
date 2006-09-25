/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils;

import junit.framework.TestCase;
import junit.framework.TestResult;

/**
 * javadoc
 */
public class UnitilsJUnit3 extends TestCase {

    private static Unitils unitils;


    public UnitilsJUnit3() {
        this(null);
    }

    public UnitilsJUnit3(String name) {
        super(name);

        if (unitils == null) {
            unitils = new Unitils();
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
        unitils.beforeTestMethod(this, getName());
        super.runBare();
        unitils.afterTestMethod(this, getName());
    }


}
