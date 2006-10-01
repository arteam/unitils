/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils;

import org.testng.IHookCallBack;
import org.testng.IHookable;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.unitils.core.Unitils;

/**
 * todo javadoc
 */
public class UnitilsTestNG implements IHookable {

    private static Unitils unitils;

    @BeforeSuite
    protected void unitilsBeforeSuite() {
        unitils = new Unitils();
        unitils.beforeAll();
    }

    @AfterSuite
    protected void unitilsAfterSuite() {
        unitils.afterAll();
    }


    @BeforeClass
    protected void unitilsBeforeClass() {
        unitils.beforeTestClass(this);
    }

    @AfterClass
    protected void unitilsAfterClass() {
        unitils.afterTestClass(this);
    }

    /**
     * This method is invoked automatically by the TestNG framework. We use this
     *
     * @param callBack
     * @param testResult
     */
    public void run(IHookCallBack callBack, ITestResult testResult) {

        unitils.beforeTestMethod(this, testResult.getMethod().getMethod());
        callBack.runTestMethod(testResult);
        unitils.afterTestMethod(this,testResult.getMethod().getMethod());
    }
}
