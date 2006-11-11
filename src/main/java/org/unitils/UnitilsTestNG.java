/*
 * Copyright 2006 the original author or authors.
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
