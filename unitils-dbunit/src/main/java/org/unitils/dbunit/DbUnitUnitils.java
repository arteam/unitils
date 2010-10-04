/*
 * Copyright 2008,  Unitils.org
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

import org.unitils.core.Unitils;
import org.unitils.dbunit.datasetfactory.DataSetFactory;
import org.unitils.dbunit.datasetloadstrategy.DataSetLoadStrategy;

import java.io.File;

/**
 * Class providing access to the functionality of the dbunit module using static methods. Meant
 * to be used directly in unit tests.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DbUnitUnitils {


    /**
     * Inserts the default dataset for the given test class into the database
     *
     * @param testInstance The current test instance (e.g. this if your in the test), not null
     */
    public static void insertDefaultDataSet(Object testInstance) {
        getDbUnitModule().insertDefaultDataSet(testInstance.getClass());
    }


    /**
     * Inserts the dataset consisting of the given list of files into the database
     *
     * @param testInstance     The current test instance (e.g. this if your in the test), not null
     * @param dataSetFileNames The names of the files that define the test data
     */
    public static void insertDataSet(Object testInstance, String... dataSetFileNames) {
        getDbUnitModule().insertDataSet(testInstance.getClass(), dataSetFileNames);
    }


    /**
     * Inserts the test data coming from the given DbUnit dataset file,
     * using the default {@link DataSetLoadStrategy} and {@link DataSetFactory} class.
     *
     * @param dataSetFile The test data set, not null
     */
    public static void insertDataSet(File dataSetFile) {
        getDbUnitModule().insertDataSet(dataSetFile);
    }


    /**
     * Inserts the test data coming from the given DbUnit dataset file.
     *
     * @param dataSetFile              The test data set, not null
     * @param dataSetFactoryClass      The class of the factory that must be used to read this dataset
     * @param dataSetLoadStrategyClass The class of the load strategy that must be used to load this dataset
     */
    public static void insertDataSet(File dataSetFile, Class<? extends DataSetFactory> dataSetFactoryClass, Class<? extends DataSetLoadStrategy> dataSetLoadStrategyClass) {
        getDbUnitModule().insertDataSet(dataSetFile, dataSetFactoryClass, dataSetLoadStrategyClass);
    }


    /**
     * Gets the instance DbUnitModule that is registered in the modules repository.
     * This instance implements the actual behavior of the static methods in this class.
     * This way, other implementations can be plugged in, while keeping the simplicity of using static methods.
     *
     * @return the instance, not null
     */
    private static DbUnitModule getDbUnitModule() {
        return Unitils.getInstance().getModulesRepository().getModuleOfType(DbUnitModule.class);
    }

}
