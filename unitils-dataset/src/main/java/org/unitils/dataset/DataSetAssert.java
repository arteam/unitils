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
import org.unitils.dataset.assertstrategy.InlineAssertDataSetStrategyHandler;
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

        DataSetModule dataSetModule = Unitils.getInstance().getModulesRepository().getModuleOfType(DataSetModule.class);
        String defaultExpectedDataSetFileName = dataSetModule.getDefaultExpectedDataSetFileName(testMethod, testInstance.getClass());
        assertDataSet(testInstance, asList(defaultExpectedDataSetFileName), variables);
    }

    public static void assertDataSet(Object testInstance, String expectedDataSetFileName, String... variables) {
        assertDataSet(testInstance, asList(expectedDataSetFileName), variables);
    }

    public static void assertDataSet(Object testInstance, List<String> expectedDataSetFileNames, String... variables) {
        DataSetAssert dataSetAssert = new DataSetAssert();
        dataSetAssert.doAssertDataSet(testInstance, expectedDataSetFileNames, variables);
    }


    public static void assertInlineDataSet(String... dataSetRows) {
        DataSetAssert dataSetAssert = new DataSetAssert();
        dataSetAssert.doAssertInlineDataSet(dataSetRows);
    }


    private boolean logDatabaseContentOnAssertionError;
    private String databaseName;


    public DataSetAssert() {
        this(null, true);
    }

    public DataSetAssert(String databaseName, boolean logDatabaseContentOnAssertionError) {
        this.logDatabaseContentOnAssertionError = logDatabaseContentOnAssertionError;
        this.databaseName = databaseName;
    }


    public void doAssertDataSet(Object testInstance, List<String> expectedDataSetFileNames, String... variables) {
        getAssertDataSetStrategyHandler(databaseName).assertDataSetFiles(testInstance, expectedDataSetFileNames, logDatabaseContentOnAssertionError, variables);
    }

    public void doAssertInlineDataSet(String... dataSetRows) {
        getInlineAssertDataSetStrategyHandler(databaseName).assertExpectedDataSet(logDatabaseContentOnAssertionError, dataSetRows);
    }


    private AssertDataSetStrategyHandler getAssertDataSetStrategyHandler(String databaseName) {
        return getDataSetStrategyHandlerFactory().createAssertDataSetStrategyHandler(databaseName);
    }

    private InlineAssertDataSetStrategyHandler getInlineAssertDataSetStrategyHandler(String databaseName) {
        return getDataSetStrategyHandlerFactory().createInlineAssertDataSetStrategyHandler(databaseName);
    }

    private DataSetStrategyHandlerFactory getDataSetStrategyHandlerFactory() {
        DataSetModule dataSetModule = Unitils.getInstance().getModulesRepository().getModuleOfType(DataSetModule.class);
        return dataSetModule.getDataSetStrategyHandlerFactory();
    }
}