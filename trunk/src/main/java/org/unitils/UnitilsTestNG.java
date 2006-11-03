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
import org.testng.annotations.*;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;

/**
 * todo javadoc
 */
public abstract class UnitilsTestNG implements IHookable {

    private static TestListener testListener;

    @BeforeSuite
    protected void unitilsBeforeSuite() {
        testListener = getUnitils().createTestListener();
        testListener.beforeAll();
    }

    @AfterSuite
    protected void unitilsAfterSuite() {
        testListener.afterAll();
    }

    @BeforeClass
    protected void unitilsBeforeClass() {
        testListener.beforeTestClass(getClass());
    }

    @AfterClass
    protected void unitilsAfterClass() {
        testListener.afterTestClass(getClass());
    }

    @BeforeMethod
    protected void unitilsBeforeTestSetUp() {
        testListener.beforeTestSetUp(this);
    }

    @AfterMethod
    protected void unitilsAfterTestTearDown() {
        testListener.afterTestTearDown(this);
    }


    public void run(IHookCallBack callBack, ITestResult testResult) {

        testListener.beforeTestMethod(this, testResult.getMethod().getMethod());
        callBack.runTestMethod(testResult);
        testListener.afterTestMethod(this, testResult.getMethod().getMethod());
    }


    protected Unitils getUnitils() {
        Unitils unitils = Unitils.getInstance();
        if (unitils == null) {
            Unitils.initSingletonInstance();
            unitils = Unitils.getInstance();
        }
        return unitils;
    }

}
