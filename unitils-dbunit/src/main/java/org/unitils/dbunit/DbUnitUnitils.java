/*
 * Copyright 2013,  Unitils.org
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
package org.unitils.dbunit;

import org.unitils.dbunit.connection.DbUnitConnectionManager;
import org.unitils.dbunit.core.DataSetService;
import org.unitils.dbunit.datasetfactory.DataSetFactory;
import org.unitils.dbunit.datasetloadstrategy.DataSetLoadStrategy;

import java.util.List;

import static java.util.Arrays.asList;
import static org.unitils.core.Unitils.getInstanceOfType;
import static org.unitils.util.ReflectionUtils.getTestClass;

/**
 * Class providing access to the functionality of the DbUnit module using static methods. Meant
 * to be used directly in unit tests.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DbUnitUnitils {

    /**
     * Inserts the data set consisting of the given list of files into the database
     *
     * @param fileNames The names of the files that define the test data
     */
    public static void insertDataSet(Object testInstanceOrClass, String... fileNames) {
        Class<?> testClass = getTestClass(testInstanceOrClass);
        List<String> dataSetFileNames = fileNames == null ? null : asList(fileNames);
        getDataSetService().loadDataSets(dataSetFileNames, testClass, null, null);
    }

    /**
     * Inserts the test data coming from the given DbUnit data set file.
     *
     * @param dataSetFactoryClass      The class of the factory that must be used to read this data set
     * @param dataSetLoadStrategyClass The class of the load strategy that must be used to load this data set
     */
    public static void insertDataSet(Object testInstanceOrClass, Class<? extends DataSetLoadStrategy> dataSetLoadStrategyClass, Class<? extends DataSetFactory> dataSetFactoryClass, String... fileNames) {
        Class<?> testClass = getTestClass(testInstanceOrClass);
        List<String> dataSetFileNames = fileNames == null ? null : asList(fileNames);
        getDataSetService().loadDataSets(dataSetFileNames, testClass, dataSetLoadStrategyClass, dataSetFactoryClass);
    }


    public static void assertExpectedDataSet(Object testInstanceOrClass, String... fileNames) {
        Class<?> testClass = getTestClass(testInstanceOrClass);
        List<String> dataSetFileNames = fileNames == null ? null : asList(fileNames);
        getDataSetService().assertExpectedDataSets(dataSetFileNames, null, testClass, null);
    }

    public static void assertExpectedDataSet(Object testInstanceOrClass, Class<? extends DataSetFactory> dataSetFactoryClass, String... fileNames) {
        Class<?> testClass = getTestClass(testInstanceOrClass);
        List<String> dataSetFileNames = fileNames == null ? null : asList(fileNames);
        getDataSetService().assertExpectedDataSets(dataSetFileNames, null, testClass, dataSetFactoryClass);
    }


    public static void resetDbUnitConnections() {
        getDbUnitConnectionManager().resetDbUnitConnections();
    }


    protected static DataSetService getDataSetService() {
        return getInstanceOfType(DataSetService.class);
    }

    protected static DbUnitConnectionManager getDbUnitConnectionManager() {
        return getInstanceOfType(DbUnitConnectionManager.class);
    }
}
