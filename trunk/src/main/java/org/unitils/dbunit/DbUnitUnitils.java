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
package org.unitils.dbunit;

import org.unitils.core.Unitils;
import org.unitils.dbunit.datasetfactory.DataSetFactory;
import org.unitils.dbunit.datasetloadstrategy.DataSetLoadStrategy;

import java.io.InputStream;

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
     */
	public static void insertDefaultDataSet() {
		getDbUnitModule().insertDefaultDataSet(Unitils.getInstance().getTestContext().getTestClass());
	}
	
	
	/**
     * Inserts the dataset consisting of the given list of files into the database
     * 
     * @param dataSetFileNames The names of the files that define the test data
     */
	public static void insertDataSet(String... dataSetFileNames) {
		getDbUnitModule().insertDataSet(Unitils.getInstance().getTestContext().getTestClass(), dataSetFileNames);
	}
	
	
	/**
     * Inserts the test data coming from the DbUnit dataset file coming from the given <code>InputStream</code>,
     * using the default {@link DataSetLoadStrategy} and {@link DataSetFactory} class.
     * 
     * @param inputStream         The stream containing the test data set, not null
     */
	public static void insertDataSet(InputStream inputStream) {
		getDbUnitModule().insertDataSet(inputStream);
	}
	
	
	/**
     * Inserts the test data coming from the DbUnit dataset file coming from the given <code>InputStream</code>
     * 
     * @param inputStream         The stream containing the test data set, not null
	 * @param dataSetFactoryClass The class of the factory that must be used to read this dataset
	 * @param dataSetLoadStrategyClass The class of the load strategy that must be used to load this dataset
     */
	public static void insertDataSet(InputStream inputStream, Class<? extends DataSetFactory> dataSetFactoryClass, 
			Class<? extends DataSetLoadStrategy> dataSetLoadStrategyClass) {
		getDbUnitModule().insertDataSet(inputStream, dataSetFactoryClass, dataSetLoadStrategyClass);
	}
	
	
	private static DbUnitModule getDbUnitModule() {
		return Unitils.getInstance().getModulesRepository().getModuleOfType(DbUnitModule.class);
	}

}
