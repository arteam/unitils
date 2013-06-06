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
package org.unitils.dbunit.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbunit.dataset.IDataSet;
import org.unitils.core.UnitilsException;
import org.unitils.dbunit.connection.DbUnitDatabaseConnection;
import org.unitils.dbunit.connection.DbUnitDatabaseConnectionManager;
import org.unitils.dbunit.datasetfactory.DataSetFactory;
import org.unitils.dbunit.datasetfactory.DataSetResolver;
import org.unitils.dbunit.datasetfactory.MultiSchemaDataSet;
import org.unitils.dbunit.datasetloadstrategy.DataSetLoadStrategy;
import org.unitilsnew.core.context.Context;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * @author Tim Ducheyne
 */
public class DataSetService {

    /* The logger instance for this class */
    protected static Log logger = LogFactory.getLog(DataSetService.class);

    protected DataSetResolver dataSetResolver;
    protected DataSetFactory defaultDataSetFactory;
    protected DataSetLoadStrategy defaultDataSetLoadStrategy;
    protected DataSetAssert dataSetAssert;
    protected DbUnitDatabaseConnectionManager dbUnitDatabaseConnectionManager;
    protected Context context;


    public DataSetService(DataSetResolver dataSetResolver, DataSetFactory defaultDataSetFactory, DataSetLoadStrategy defaultDataSetLoadStrategy, DataSetAssert dataSetAssert,
                          DbUnitDatabaseConnectionManager dbUnitDatabaseConnectionManager, Context context) {
        this.dataSetResolver = dataSetResolver;
        this.defaultDataSetFactory = defaultDataSetFactory;
        this.defaultDataSetLoadStrategy = defaultDataSetLoadStrategy;
        this.dataSetAssert = dataSetAssert;
        this.dbUnitDatabaseConnectionManager = dbUnitDatabaseConnectionManager;
        this.context = context;
    }


    /**
     * Inserts the data set consisting of the given list of files into the database.
     * File names that start with '/' are treated absolute. File names that do not start with '/', are relative to the current class.
     * A default file name is used when no file names are specified
     *
     * @param fileNames                The names of the files, (start with '/' for absolute names), the default file name is used when null or empty
     * @param testClass                The test class for which the data set must be loaded, not null
     * @param dataSetLoadStrategyClass The type of load strategy to use, not null
     * @param dataSetFactoryClass      The type of factory that will create the data sets, not null
     */
    public void loadDataSets(List<String> fileNames, Class<?> testClass, Class<? extends DataSetLoadStrategy> dataSetLoadStrategyClass, Class<? extends DataSetFactory> dataSetFactoryClass) {
        DataSetLoadStrategy dataSetLoadStrategy = defaultDataSetLoadStrategy;
        if (dataSetLoadStrategyClass != null) {
            dataSetLoadStrategy = context.getInstanceOfType(dataSetLoadStrategyClass);
        }
        DataSetFactory dataSetFactory = defaultDataSetFactory;
        if (dataSetFactoryClass != null) {
            dataSetFactory = context.getInstanceOfType(dataSetFactoryClass);
        }
        if (fileNames == null || fileNames.isEmpty()) {
            // empty means, use default file name, which is the name of the class + extension
            String defaultFileName = getDefaultDataSetFileName(testClass, dataSetFactory);
            fileNames = asList(defaultFileName);
        }

        logger.info("Loading data sets. File names: " + fileNames);
        MultiSchemaDataSet multiSchemaDataSet = getDataSet(fileNames, testClass, dataSetFactory);
        for (String schemaName : multiSchemaDataSet.getSchemaNames()) {
            DbUnitDatabaseConnection dbUnitDatabaseConnection = dbUnitDatabaseConnectionManager.getDbUnitDatabaseConnection(schemaName);
            try {
                IDataSet schemaDataSet = multiSchemaDataSet.getDataSetForSchema(schemaName);
                dataSetLoadStrategy.loadDataSet(dbUnitDatabaseConnection, schemaDataSet);
            } finally {
                dbUnitDatabaseConnection.closeJdbcConnection();
            }
        }
    }


    /**
     * Compares the contents of the expected DbUnitDataSet with the contents of the database. Only the tables and columns
     * that occur in the expected DbUnitDataSet are compared with the database contents.
     *
     * @param fileNames           The names of the files, (start with '/' for absolute names), the default file name is used when null or empty
     * @param testMethod          The test method, not null
     * @param testClass           The test class, not null
     * @param dataSetFactoryClass The type of DataSetFactory responsible for creating the data set file
     */
    public void assertExpectedDataSets(List<String> fileNames, Method testMethod, Class<?> testClass, Class<? extends DataSetFactory> dataSetFactoryClass) {
        DataSetFactory dataSetFactory = defaultDataSetFactory;
        if (dataSetFactoryClass != null) {
            dataSetFactory = context.getInstanceOfType(dataSetFactoryClass);
        }
        if (fileNames == null) {
            fileNames = new ArrayList<String>(1);
        }
        if (fileNames == null || fileNames.isEmpty()) {
            // empty means, use default file name, which is the name of the class + extension
            String defaultFileName = getDefaultExpectedDataSetFileName(testMethod, testClass, dataSetFactory.getDataSetFileExtension());
            fileNames = asList(defaultFileName);
        }

        logger.info("Asserting expected data sets. File names: " + fileNames);
        MultiSchemaDataSet multiSchemaDataSet = getDataSet(fileNames, testClass, dataSetFactory);
        for (String schemaName : multiSchemaDataSet.getSchemaNames()) {
            DbUnitDatabaseConnection dbUnitDatabaseConnection = dbUnitDatabaseConnectionManager.getDbUnitDatabaseConnection(schemaName);
            try {
                IDataSet expectedDataSet = multiSchemaDataSet.getDataSetForSchema(schemaName);
                IDataSet actualDataSet = dbUnitDatabaseConnection.createDataSet();

                dataSetAssert.assertEqualDbUnitDataSets(schemaName, expectedDataSet, actualDataSet);

            } catch (Exception e) {
                throw new UnitilsException("Unable to assert expected data set for schema " + schemaName, e);
            } finally {
                dbUnitDatabaseConnection.closeJdbcConnection();
            }
        }
    }


    protected MultiSchemaDataSet getDataSet(List<String> fileNames, Class<?> testClass, DataSetFactory dataSetFactory) {
        List<File> dataSetFiles = new ArrayList<File>();
        for (String dataSetFileName : fileNames) {
            File dataSetFile = dataSetResolver.resolve(testClass, dataSetFileName);
            dataSetFiles.add(dataSetFile);
        }
        return dataSetFactory.createDataSet(dataSetFiles);
    }

    /**
     * Gets the name of the default data set file at class level The default name is constructed as
     * follows: 'classname without package name'.xml
     *
     * @param testClass      The test class, not null
     * @param dataSetFactory The configured data set factory, not null
     * @return The default filename, not null
     */
    protected String getDefaultDataSetFileName(Class<?> testClass, DataSetFactory dataSetFactory) {
        String className = testClass.getName();
        String extension = dataSetFactory.getDataSetFileExtension();
        return className.substring(className.lastIndexOf(".") + 1) + '.' + extension;
    }

    /**
     * Gets the name of the expected data set file. The default name of this file is constructed as
     * follows: 'classname without package name'.'test name'-result.xml.
     *
     * @param method    The test method, not null
     * @param testClass The test class, not null
     * @param extension The configured extension of data set files, not null
     * @return The expected data set filename, not null
     */
    protected String getDefaultExpectedDataSetFileName(Method method, Class<?> testClass, String extension) {
        String className = testClass.getName();
        return className.substring(className.lastIndexOf(".") + 1) + "." + method.getName() + "-result." + extension;
    }
}
