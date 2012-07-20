/*
 * Copyright 2012,  Unitils.org
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
package org.unitils.spring.util;

import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;

// todo remove
public class SpringUnitilsAdaptorTestExecutionListener implements TestExecutionListener {

    public void beforeTestClass(TestContext testContext) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void prepareTestInstance(TestContext testContext) throws Exception {
        registerTestContext(testContext);
        getTestListener().afterCreateTestObject(testContext.getTestInstance());
    }


    public void beforeTestSetUp(TestContext testContext) throws Exception {
        getTestListener().beforeTestSetUp(testContext.getTestInstance(), testContext.getTestMethod());
    }


    public void beforeTestMethod(TestContext testContext) throws Exception {
        getTestListener().beforeTestMethod(testContext.getTestInstance(), testContext.getTestMethod());
    }


    public void afterTestMethod(TestContext testContext) throws Exception {
        getTestListener().afterTestMethod(testContext.getTestInstance(), testContext.getTestMethod(), testContext.getTestException());
    }

    public void afterTestClass(TestContext testContext) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    public void afterTestTearDown(TestContext testContext) throws Exception {
        getTestListener().afterTestTearDown(testContext.getTestInstance(), testContext.getTestMethod());
    }


    private void registerTestContext(TestContext testContext) {
        //getSpringModule().registerTestContext(testContext);
    }


    /**
     * @return The Unitils test listener
     */
    protected TestListener getTestListener() {
        return getUnitils().getTestListener();
    }


    /**
     * Returns the default singleton instance of Unitils
     *
     * @return the Unitils instance, not null
     */
    protected Unitils getUnitils() {
        return Unitils.getInstance();
    }

}
