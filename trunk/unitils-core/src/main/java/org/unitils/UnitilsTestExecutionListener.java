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

    public void beforeTestClass(TestContext testContext) throws Exception {
        CurrentTestClass currentTestClass = new CurrentTestClass(testContext);
        setCurrentTestClass(currentTestClass);

        ModulesRepository modulesRepository = getModulesRepository();
        for (Module module : modulesRepository.getModules()) {
            modulesRepository.getTestListener(module).beforeTestClass(currentTestClass);
        }
    }

    public void prepareTestInstance(TestContext testContext) throws Exception {
        CurrentTestInstance currentTestInstance = new CurrentTestInstance(testContext);
        setCurrentTestClass(currentTestInstance);
        setCurrentTestInstance(currentTestInstance);

        ModulesRepository modulesRepository = getModulesRepository();
        for (Module module : modulesRepository.getModules()) {
            modulesRepository.getTestListener(module).prepareTestInstance(currentTestInstance);
        }
    }

    public void beforeTestMethod(TestContext testContext) throws Exception {
        CurrentTestInstance currentTestInstance = new CurrentTestInstance(testContext);
        setCurrentTestClass(currentTestInstance);
        setCurrentTestInstance(currentTestInstance);

        ModulesRepository modulesRepository = getModulesRepository();
        for (Module module : modulesRepository.getModules()) {
            modulesRepository.getTestListener(module).beforeTestMethod(currentTestInstance);
        }
    }

    public void afterTestMethod(TestContext testContext) throws Exception {
        CurrentTestInstance currentTestInstance = new CurrentTestInstance(testContext);
        setCurrentTestClass(currentTestInstance);
        setCurrentTestInstance(currentTestInstance);
        try {
            ModulesRepository modulesRepository = getModulesRepository();
            for (Module module : modulesRepository.getModules()) {
                modulesRepository.getTestListener(module).afterTestMethod(currentTestInstance);
            }
        } finally {
            setCurrentTestInstance(null);
        }
    }

    public void afterTestClass(TestContext testContext) throws Exception {
        setCurrentTestClass(null);
        // ignored, not always called
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
