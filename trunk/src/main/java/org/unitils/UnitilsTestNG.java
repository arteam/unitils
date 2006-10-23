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
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;

/**
 * todo javadoc
 */
public abstract class UnitilsTestNG implements IHookable {

    private static TestListener testListener;

    @BeforeSuite
    protected void unitilsBeforeSuite() {
        testListener = Unitils.getInstance().getTestListener();
        testListener.beforeAll();
    }

    @AfterSuite
    protected void unitilsAfterSuite() {
        testListener.afterAll();
    }


    @BeforeClass
    protected void unitilsBeforeClass() {
        testListener.beforeTestClass(this);
    }

    @AfterClass
    protected void unitilsAfterClass() {
        testListener.afterTestClass(this);
    }


    public void run(IHookCallBack callBack, ITestResult testResult) {

        testListener.beforeTestMethod(this, testResult.getMethod().getMethod());
        callBack.runTestMethod(testResult);
        testListener.afterTestMethod(this, testResult.getMethod().getMethod());
    }
}
