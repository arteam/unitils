/*
 * Copyright Unitils.org
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

import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;
import org.unitils.core.*;

/**
 * Test execution listener implementation that delegates the called test method to the test execution listeners
 * of all registered modules, e.g. the DataSetModule.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class UnitilsTestExecutionListener implements TestExecutionListener {

    private boolean supportsBeforeAndAfterTestClass = false;

    public void beforeTestClass(TestContext testContext) throws Exception {
        System.out.println("**** UnitilsTestExecutionListener.beforeTestClass before");
        supportsBeforeAndAfterTestClass = true;
        performBeforeTestClass(testContext);
        System.out.println("**** UnitilsTestExecutionListener.beforeTestClass after");
    }

    protected void performBeforeTestClass(TestContext testContext) throws Exception {
        System.out.println("**** UnitilsTestExecutionListener.performBeforeTestClass before");
        try {
            CurrentTestClass currentTestClass = new CurrentTestClass(testContext);
            setCurrentTestClass(currentTestClass);

            ModulesRepository modulesRepository = getModulesRepository();
            for (Module module : modulesRepository.getModules()) {
                modulesRepository.getTestListener(module).beforeTestClass(currentTestClass);
            }
        } catch (Exception e) {
            System.out.println("**** UnitilsTestExecutionListener.performBeforeTestClass exception");
            e.printStackTrace();
            throw e;
        }
        System.out.println("UnitilsTestExecutionListener.performBeforeTestClass after");
    }

    @Override
    public void prepareTestInstance(TestContext testContext) throws Exception {
        if (!supportsBeforeAndAfterTestClass) {
            // work-around for junit-3 and spring 2.5.6: these don't have a before or after test class
            performBeforeTestClass(testContext);
        }
        System.out.println("**** UnitilsTestExecutionListener.prepareTestInstance before");
        try {
            CurrentTestInstance currentTestInstance = new CurrentTestInstance(testContext);
            setCurrentTestClass(currentTestInstance);
            setCurrentTestInstance(currentTestInstance);
            // ignored, do behavior in before test method
        } catch (Exception e) {
            System.out.println("**** UnitilsTestExecutionListener.prepareTestInstance exception");
            e.printStackTrace();
            throw e;
        }
        System.out.println("UnitilsTestExecutionListener.prepareTestInstance after");
    }

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        System.out.println("**** UnitilsTestExecutionListener.beforeTestMethod before");
        try {
            CurrentTestInstance currentTestInstance = new CurrentTestInstance(testContext);
            setCurrentTestClass(currentTestInstance);
            setCurrentTestInstance(currentTestInstance);

            ModulesRepository modulesRepository = getModulesRepository();
            for (Module module : modulesRepository.getModules()) {
                modulesRepository.getTestListener(module).beforeTest(currentTestInstance);
            }
        } catch (Exception e) {
            System.out.println("**** UnitilsTestExecutionListener.beforeTestMethod exception ");
            e.printStackTrace();
            throw e;
        }
        System.out.println("UnitilsTestExecutionListener.beforeTestMethod after");
    }

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        System.out.println("**** UnitilsTestExecutionListener.afterTestMethod before");
        try {
            CurrentTestInstance currentTestInstance = new CurrentTestInstance(testContext);
            setCurrentTestClass(currentTestInstance);
            setCurrentTestInstance(currentTestInstance);

            ModulesRepository modulesRepository = getModulesRepository();
            for (Module module : modulesRepository.getModules()) {
                modulesRepository.getTestListener(module).afterTest(currentTestInstance);
            }
            System.out.println("UnitilsTestExecutionListener.afterTestMethod after");
        } catch (Exception e) {
            System.out.println("**** UnitilsTestExecutionListener.afterTestMethod exception");
            e.printStackTrace();
            throw e;
        } finally {
            setCurrentTestInstance(null);
            if (!supportsBeforeAndAfterTestClass) {
                // work-around for junit-3 and spring 2.5.6: these don't have a before or after test class
                afterTestClass(testContext);
            }
        }

    }

    public void afterTestClass(TestContext testContext) throws Exception {
        try {
            System.out.println("**** UnitilsTestExecutionListener.afterTestClass before");
            setCurrentTestClass(null);
            // ignored, not always called
        } catch (Exception e) {
            System.out.println("**** UnitilsTestExecutionListener.afterTestClass exception");
            e.printStackTrace();
            throw e;
        }
        System.out.println("UnitilsTestExecutionListener.afterTestClass after");
    }

    /**
     * Returns the {@link org.unitils.core.ModulesRepository} that provides access to the modules that are configured in unitils.
     *
     * @return the {@link org.unitils.core.ModulesRepository}
     */
    public ModulesRepository getModulesRepository() {
        return Unitils.getInstance().getModulesRepository();
    }


    private void setCurrentTestClass(CurrentTestClass currentTestClass) {
        Unitils.getInstance().setCurrentTestClass(currentTestClass);
    }

    private void setCurrentTestInstance(CurrentTestInstance currentTestInstance) {
        Unitils.getInstance().setCurrentTestInstance(currentTestInstance);
    }
}
