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

import org.apache.commons.configuration.Configuration;
import org.dbunit.Assertion;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.*;
import org.dbunit.dataset.datatype.DefaultDataTypeFactory;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.ext.db2.Db2DataTypeFactory;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.ext.oracle.OracleDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.unitils.core.Module;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.unitils.db.DatabaseModule;
import org.unitils.db.annotations.DatabaseTest;
import org.unitils.dbunit.annotation.DbUnitDataSet;
import org.unitils.dbunit.annotation.ExpectedDbUnitDataSet;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Module that provides support for managing database test data using DBUnit.
 * <p/>
 * This module depends on the {@link DatabaseModule} for database connection management and identificatiof database test
 * classes.
 * <p/>
 * This module only provides services to unit test classes that are annotated with an annotation that identifies it as
 * a database test (see {@link DatabaseModule} for more information)
 * <p/>
 * For each database test method, this module will try to find a test dataset file in the classpath and, if found, insert
 * this dataset into the test database. DbUnits <code>DatabaseOperation.CLEAN_INSERT</code> is used for inserting this
 * dataset. This means that the tables specified in the dataset are cleared before inserting the test records. As dataset
 * file format, DbUnit's <code>FlatXmlDataSet</code> is used.
 * <p/>
 * For each database test method, the dataset file is found as follows:
 * <ol><li>If the test method is annotated with {@link DbUnitDataSet}, the file with the name specified by the fileName
 * property of this annotation is used</li>
 * <li>If a file can be found in the classpath in the same package as the testclass, with the name
 * 'classname without packagename'.'test method name'.xml, this file is used</li>
 * <li>If the test class is annotated with {@link DbUnitDataSet}, the file with the name specified by the fileName
 * property of this annotation is used</li>
 * <li>If a file can be found in the classpath in the same package as the testclass, with the name
 * 'classname without packagename'.xml, this file is used</li>
 * <p/>
 * Using the method {@link #assertDBContentAsExpected(Object,String)}, the contents of the database can be compared with
 * the contents of a dataset. The expected dataset file should be located in the classpath in the same package as the
 * testclass, with the name 'classname without packagename'.'test method name'-result.xml.
 * <p/>
 * todo expected dataset annotation
 */
public class DbUnitModule implements Module {

    /* Property key of the name of the database schema */
    private static final String PROPKEY_SCHEMA_NAME = "dataSource.schemaName";

    /* Property key of the SQL dialect of the underlying DBMS implementation */
    private static final String PROPKEY_DATABASE_DIALECT = "database.dialect";

    /**
     * Object that DbUnit uses to connect to the database and to cache some database metadata. Since DBUnit's data
     * caching is time-consuming, this object is created only once and used througout the entire test run.
     */
    private IDatabaseConnection dbUnitDatabaseConnection;

    /**
     * Name of the database schema, needed to configure DBUnit
     */
    private String databaseSchemaName;

    /**
     * The database dialect, needed to configure DBUnit
     */
    private String databaseDialect;

    /**
     * Initializes the DbUnitModule using the given Configuration
     *
     * @param configuration
     */
    public void init(Configuration configuration) {

        databaseSchemaName = configuration.getString(PROPKEY_SCHEMA_NAME).toUpperCase();
        databaseDialect = configuration.getString(PROPKEY_DATABASE_DIALECT);
    }

    /**
     * Checks whether the given test instance is a database test.
     *
     * @param testClass the test class, not null
     * @return true if the test class is a database test false otherwise
     * @see DatabaseModule#isDatabaseTest(Class<?>)
     */
    protected boolean isDatabaseTest(Class<?> testClass) {

        return getDatabaseModule().isDatabaseTest(testClass);
    }

    /**
     * If this is the first time that we encounter a test class annotated with {@link DatabaseTest}, a new instance
     * of the dbUnit's {@link IDatabaseConnection} is created, that is used througout the whole test run.
     */
    protected void initDbUnitConnection() {

        if (dbUnitDatabaseConnection == null) {
            dbUnitDatabaseConnection = createDbUnitConnection();
        }
    }

    /**
     * @return The current JDBC database connection, as provided by the {@link DatabaseModule}
     */
    private Connection getCurrentConnection() {

        DatabaseModule databaseModule = getDatabaseModule();
        return databaseModule.getCurrentConnection();
    }

    /**
     * Creates a new instance of dbUnit's <code>IDatabaseConnection</code>
     *
     * @return a new instance of dbUnit's <code>IDatabaseConnection</code>
     */
    protected IDatabaseConnection createDbUnitConnection() {

        // Create connection
        IDatabaseConnection connection = new DatabaseConnection(getCurrentConnection(), databaseSchemaName);

        // Set correct dialect
        if ("oracle".equals(databaseDialect)) {
            DatabaseConfig config = connection.getConfig();
            config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new OracleDataTypeFactory());
        }
        if ("db2".equals(databaseDialect)) {
            DatabaseConfig config = connection.getConfig();
            config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new Db2DataTypeFactory());
        }
        if ("mysql".equals(databaseDialect)) {
            DatabaseConfig config = connection.getConfig();
            config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory());
        }
        if ("hsqldb".equals(databaseDialect)) {
            DatabaseConfig config = connection.getConfig();
            config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new DefaultDataTypeFactory());
        }
        return connection;
    }

    /**
     * @return The DbUnit connection
     */
    public IDatabaseConnection getDbUnitDatabaseConnection() {

        return dbUnitDatabaseConnection;
    }

    /**
     * Closes the DbUnit connection to the database
     */
    protected void closeDbUnitConnection() {

        try {
            if (dbUnitDatabaseConnection != null) {
                dbUnitDatabaseConnection.close();
            }
        } catch (SQLException e) {
            throw new UnitilsException("Error while closing database connection");
        }
    }

    /**
     * Uses DbUnit to insert test data in the database. The DbUnit data file that is loaded is defined by the following
     * rules:
     * First, we try to load a test specific dataset (see {@link #getTestDataSetFileName(Class,Method)}. If such a file
     * does not exist, the default dataset is loaded (see {@link #getDefaultDataSetFileName(Class)}. If neither of
     * these exists, a <code>UnitilsException</code> is thrown.
     *
     * @param testClass
     * @param testMethod
     */
    protected void insertTestData(Class testClass, Method testMethod) {

        try {
            IDataSet dataSet = getDataSet(testClass, testMethod);
            if (dataSet != null) { // Dataset is null when there is no data xml file.
                getInsertDatabaseOperation().execute(dbUnitDatabaseConnection, dataSet);
            }
        } catch (Exception e) {
            throw new UnitilsException("Error while trying to insert test data from DbUnit xml file", e);
        }
    }

    /**
     * @return The DbUnit <code>DatabaseOperation</code> that is used for loading the data file
     */
    protected DatabaseOperation getInsertDatabaseOperation() {

        return DatabaseOperation.CLEAN_INSERT;
    }

    /**
     * This method will first try to load a test specific dataset (see {@link #getTestDataSetFileName(Class,Method)}.
     * If that file does not exist, the default dataset is loaded (see {@link #getDefaultDataSetFileName(Class)}.
     * If neither of these files exist, a <code>UnitilsException</code> is thrown.
     *
     * @param testClass
     * @param method
     * @return the dataset, can be null if the files were not found
     */
    private IDataSet getDataSet(Class testClass, Method method) {

        //load the test specific dataset
        String testDataSetFileName = getTestDataSetFileName(testClass, method);
        IDataSet dataSet = loadDataSet(testClass, testDataSetFileName);
        if (dataSet == null) {
            //load the default dataset
            String defaultDataSetFileName = getDefaultDataSetFileName(testClass);
            dataSet = loadDataSet(testClass, defaultDataSetFileName);
        }
        return dataSet;
    }

    /**
     * Gets the name of the testdata file that is specific to the current test.
     * The default name is constructed as follows: 'classname without packagename'.'test method name'.xml.
     * The default name can be overridden when the given method is annotated with the DbUnitDataSet annotation.
     *
     * @param testClass
     * @param method
     * @return the test specific filename
     */
    protected String getTestDataSetFileName(Class testClass, Method method) {

        DbUnitDataSet dbUnitDataSetAnnotation = method.getAnnotation(DbUnitDataSet.class);
        if (dbUnitDataSetAnnotation != null) {
            return dbUnitDataSetAnnotation.fileName();
        } else {
            String className = testClass.getName();
            return className.substring(className.lastIndexOf(".") + 1) + "." + method.getName() + ".xml";
        }
    }

    /**
     * Gets the name of the default testdata file.
     * The default name is constructed as follows: 'classname without packagename'.xml
     * The default name can be overridden when the class is annotated with the DbUnitDataSet annotation.
     *
     * @param testClass
     * @return the default filename
     */
    protected String getDefaultDataSetFileName(Class<?> testClass) {

        DbUnitDataSet dbUnitDataSetAnnotation = testClass.getAnnotation(DbUnitDataSet.class);
        if (dbUnitDataSetAnnotation != null) {
            return dbUnitDataSetAnnotation.fileName();
        } else {
            String className = testClass.getName();
            return className.substring(className.lastIndexOf(".") + 1) + ".xml";
        }
    }

    /**
     * Loads a dataset from the file in the classpath with the given name.
     * Filenames that start with '/' are treated absolute. Filenames that do not start with '/', are relative
     * to the current class.
     *
     * @param dataSetFilename the name, (start with '/' for absolute names)
     * @return the data set, or null if the file did not exist
     */
    private IDataSet loadDataSet(Class testClass, String dataSetFilename) {

        try {
            if (dataSetFilename == null) {
                return null;
            }

            InputStream in = null;
            try {
                in = testClass.getResourceAsStream(dataSetFilename);
                if (in == null) {
                    return null;
                }

                IDataSet dataSet = createDbUnitDataSet(in);
                // todo make configurable
                ReplacementDataSet replacementDataSet = new ReplacementDataSet(dataSet);
                replacementDataSet.addReplacementObject("[null]", null);
                return replacementDataSet;

            } finally {
                if (in != null) {
                    in.close();
                }
            }
        } catch (IOException e) {
            throw new UnitilsException("Error while loading DbUnit dataset", e);
        }
    }

    /**
     * Constructs a DbUnit DataSet object from the given InputStream
     *
     * @param in
     * @return a DbUnit DataSet object
     */
    protected IDataSet createDbUnitDataSet(InputStream in) {

        try {
            return new FlatXmlDataSet(in);
        } catch (Exception e) {
            throw new UnitilsException("Error while reading DbUnit dataset", e);
        }
    }

    /**
     * Compares the contents of the expected DbUnitDataSet with the contents of the database. Only the tables and columns
     * that occur in the expected DbUnitDataSet are compared with the database contents.
     */
    public void assertDBContentAsExpected(Object testObject, String testMethodName) {

        try {
            IDatabaseConnection databaseConnection = getDbUnitDatabaseConnection();

            IDataSet expectedDataSet = getExpectedDataSet(testObject.getClass(), testMethodName);
            IDataSet actualDataSet = databaseConnection.createDataSet(expectedDataSet.getTableNames());
            ITableIterator tables = expectedDataSet.iterator();

            while (tables.next()) {
                ITable expectedTable = tables.getTable();
                ITableMetaData metaData = expectedTable.getTableMetaData();
                ITable actualTable = actualDataSet.getTable(expectedTable.getTableMetaData().getTableName());
                ITable filteredActualTable = DefaultColumnFilter.includedColumnsTable(actualTable, metaData.getColumns());

                Assertion.assertEquals(new SortedTable(expectedTable), new SortedTable(filteredActualTable,
                        expectedTable.getTableMetaData()));
            }
        } catch (Exception e) {
            throw new UnitilsException("Error while verifying db contents", e);
        }
    }

    /**
     * Gets the expected dataset with a filename specified by {@link #getExpectedDataSetFileName(Class,String)}.
     * If the file does not exist, a file not found exception is thrown.
     *
     * @return the dataset, not null
     */
    private IDataSet getExpectedDataSet(Class testClass, String methodName) {

        String dataSetFileName = getExpectedDataSetFileName(testClass, methodName);
        IDataSet dataSet = loadDataSet(testClass, dataSetFileName);
        if (dataSet == null) {
            throw new UnitilsException("Unable to find test dataset with file name: " + dataSetFileName);
        }
        return dataSet;
    }

    /**
     * Gets the name of the expected dataset file.
     * The default name of this file is constructed as follows: 'classname without packagename'.'testname'-result.xml.
     * This default name can be overridden by annotating the test method with the {@link ExpectedDbUnitDataSet}
     * annotation.
     *
     * @return the expected dataset filename
     */
    protected static String getExpectedDataSetFileName(Class<?> testClass, String methodName) {

        ExpectedDbUnitDataSet expectedDbUnitDataSetAnnotation = testClass.getAnnotation(ExpectedDbUnitDataSet.class);
        if (expectedDbUnitDataSetAnnotation != null) {
            return expectedDbUnitDataSetAnnotation.fileName();
        } else {
            String className = testClass.getName();
            return className.substring(className.lastIndexOf(".") + 1) + "." + methodName + "-result.xml";
        }
    }

    /**
     * @return Implementation of DatabaseModule, on which this module is dependent
     */
    protected DatabaseModule getDatabaseModule() {

        Unitils unitils = Unitils.getInstance();
        return unitils.getModulesRepository().getFirstModule(DatabaseModule.class);
    }


    /**
     * @return The TestListener object that implements Unitils' DbUnit support
     */
    public TestListener createTestListener() {

        return new DbUnitListener();
    }

    /**
     * Test listener that is called while the test framework is running tests
     */
    private class DbUnitListener extends TestListener {

        @Override
        public void beforeAll() {
            if (getDatabaseModule() == null) {
                throw new UnitilsException("Invalid configuration: When the DbUnitModule is enabled, the DatabaseModule " +
                        "should also be enabled and the DbUnitModule should be configured to run after the DatabaseModule");
            }
        }

        @Override
        public void beforeTestClass(Class<?> testClass) {
            if (isDatabaseTest(testClass)) {
                initDbUnitConnection();
            }
        }

        @Override
        public void beforeTestMethod(Object testObject, Method testMethod) {
            if (isDatabaseTest(testObject.getClass())) {
                insertTestData(testObject.getClass(), testMethod);
            }
        }

        @Override
        public void afterAll() {
            closeDbUnitConnection();
        }

    }

}
