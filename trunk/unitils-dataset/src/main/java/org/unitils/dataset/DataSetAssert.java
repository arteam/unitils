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
package org.unitils.dataset;

import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.unitils.dataset.assertstrategy.AssertDataSetStrategyHandler;
import org.unitils.util.TestMethodFinder;

import java.lang.reflect.Method;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DataSetAssert {


    public static void assertDefaultDataSet(Object testInstance, String... variables) {
        Method testMethod;
        try {
            testMethod = TestMethodFinder.findCurrentTestMethod(testInstance.getClass());
        } catch (UnitilsException e) {
            throw new UnitilsException("Unable to assert using a default data set file. Could not find a test method needed to construct the default data set file name 'test-class'.'method'-result.xml.\n" +
                    "A method call to the given test instance should be on the call stack.\nThe assertDefaultDataSet method should typically be called from within a test method, passing 'this' as test instance.", e);
        }

        String defaultExpectedDataSetFileName = getDataSetModule().getDefaultExpectedDataSetFileName(testMethod, testInstance.getClass());
        assertDataSet(testInstance, asList(defaultExpectedDataSetFileName), variables);
    }

    public static void assertDataSet(Object testInstance, String expectedDataSetFileName, String... variables) {
        assertDataSet(testInstance, asList(expectedDataSetFileName), variables);
    }

    public static void assertDataSet(Object testInstance, String expectedDataSetFileName, boolean logDatabaseContentOnAssertionError, String... variables) {
        assertDataSet(testInstance, asList(expectedDataSetFileName), logDatabaseContentOnAssertionError, variables);
    }

    public static void assertDataSet(Object testInstance, List<String> expectedDataSetFileNames, String... variables) {
        assertDataSet(testInstance, expectedDataSetFileNames, true, variables);
    }

    public static void assertDataSet(Object testInstance, List<String> expectedDataSetFileNames, boolean logDatabaseContentOnAssertionError, String... variables) {
        getAssertDataSetStrategyHandler().assertDataSetFiles(testInstance, expectedDataSetFileNames, logDatabaseContentOnAssertionError, variables);
    }


    // todo add inline assert methods


    private static AssertDataSetStrategyHandler getAssertDataSetStrategyHandler() {
        DataSetModuleFactory dataSetModuleFactory = getDataSetModule().getDataSetModuleFactory();
        return dataSetModuleFactory.getAssertDataSetStrategyHandler();
    }

    /**
     * Gets the instance DataSetModule that is registered in the modules repository.
     * This instance implements the actual behavior of the static methods in this class.
     * This way, other implementations can be plugged in, while keeping the simplicity of using static methods.
     *
     * @return the instance, not null
     */
    private static DataSetModule getDataSetModule() {
        return Unitils.getInstance().getModulesRepository().getModuleOfType(DataSetModule.class);
    }
}