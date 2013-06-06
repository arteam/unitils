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

import org.unitils.dbunit.core.DataSetService;
import org.unitils.dbunit.datasetfactory.DataSetFactory;
import org.unitils.dbunit.datasetloadstrategy.DataSetLoadStrategy;

import java.util.List;

import static java.util.Arrays.asList;
import static org.unitilsnew.core.Unitils.getInstanceOfType;

/**
 * Class providing access to the functionality of the DbUnit module using static methods. Meant
 * to be used directly in unit tests.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public abstract class DbUnitUnitils {

    protected static DataSetService dataSetService = getInstanceOfType(DataSetService.class);


    /**
     * Inserts the default data set for the given test class into the database
     */
    public static void insertDefaultDataSet(Class<?> testClass) {
        dataSetService.loadDataSets(null, testClass, null, null);
    }

    /**
     * Inserts the data set consisting of the given list of files into the database
     *
     * @param fileNames The names of the files that define the test data
     */
    public static void insertDataSet(Class<?> testClass, String... fileNames) {
        List<String> dataSetFileNames = fileNames == null ? null : asList(fileNames);
        dataSetService.loadDataSets(dataSetFileNames, testClass, null, null);
    }

    // todo td insertDataSet  refreshDataSet etc

    /**
     * Inserts the test data coming from the given DbUnit data set file.
     *
     * @param dataSetFactoryClass      The class of the factory that must be used to read this data set
     * @param dataSetLoadStrategyClass The class of the load strategy that must be used to load this data set
     */
    public static void insertDataSet(Class<?> testClass, Class<? extends DataSetLoadStrategy> dataSetLoadStrategyClass, Class<? extends DataSetFactory> dataSetFactoryClass, String... fileNames) {
        List<String> dataSetFileNames = fileNames == null ? null : asList(fileNames);
        dataSetService.loadDataSets(dataSetFileNames, testClass, dataSetLoadStrategyClass, dataSetFactoryClass);
    }
}
