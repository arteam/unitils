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
import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.dbunit.Assertion.assertEquals;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.dataset.*;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.operation.DatabaseOperation;
import org.unitils.core.Module;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.unitils.database.DatabaseModule;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.annotation.ExpectedDataSet;
import org.unitils.dbunit.util.DbUnitDatabaseConnection;
import org.unitils.dbunit.util.TablePerRowXmlDataSet;
import org.unitils.util.ConfigUtils;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.sql.SQLException;

/**
 * todo javadoc
 * <p/>
 * Module that provides support for managing database test data using DBUnit.
 * <p/>
 * This module depends on the {@link DatabaseModule} for database connection management and identificatiof database test
 * classes.
 * <p/>
 * This module only provides services to unit test classes that are annotated with an annotation that identifies it as
 * a database test (see {@link DatabaseModule} for more information)
 * <p/>
 * For each database test method, this module will try to find a test dataset file in the classpath and, if found, insert
 * this dataset into the test database. DbUnits <code>DatabaseTask.CLEAN_INSERT</code> is used for inserting this
 * dataset. This means that the tables specified in the dataset are cleared before inserting the test records. As dataset
 * file format, DbUnit's <code>FlatXmlDataSet</code> is used.
 * <p/>
 * For each database test method, the dataset file is found as follows:
 * <ol><li>If the test method is annotated with {@link DataSet}, the file with the specified name is used</li>
 * <li>If a file can be found in the classpath in the same package as the testclass, with the name
 * 'classname without packagename'.'test method name'.xml, this file is used</li>
 * <li>If the test class is annotated with {@link DataSet}, the file with the specified name is used</li>
 * <li>If a file can be found in the classpath in the same package as the testclass, with the name
 * 'classname without packagename'.xml, this file is used</li>
 * <p/>
 * Using the method {@link #assertDbContentAsExpected}, the contents of the database can be compared with
 * the contents of a dataset. The expected dataset file should be located in the classpath in the same package as the
 * testclass, with the name 'classname without packagename'.'test method name'-result.xml.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DbUnitModule implements Module {

    /* Property key of the name of the database schema */
    public static final String PROPKEY_SCHEMA_NAME = "dataSource.schemaName";

    /* Property key of the SQL dialect of the underlying DBMS implementation */
    public static final String PROPKEY_DATABASE_DIALECT = "database.dialect";

    /*
     * Object that DbUnit uses to connect to the database and to cache some database metadata. Since DBUnit's data
     * caching is time-consuming, this object is created only once and used througout the entire test run. The
     * underlying JDBC Connection however is 'closed' (returned to the pool) after every database operation.
     */
    private DbUnitDatabaseConnection dbUnitDatabaseConnection;

    /* Name of the database schema, needed to configure DBUnit */
    private String schemaName;

    /* Instance of DbUnits IDataTypeFactory, that handles dbms specific data type issues */
    private IDataTypeFactory dataTypeFactory;


    /**
     * Initializes the DbUnitModule using the given Configuration
     *
     * @param configuration The config, not null
     */
    public void init(Configuration configuration) {
        schemaName = configuration.getString(PROPKEY_SCHEMA_NAME).toUpperCase();
        String databaseDialect = configuration.getString(PROPKEY_DATABASE_DIALECT);
        dataTypeFactory = ConfigUtils.getConfiguredInstance(IDataTypeFactory.class, configuration, databaseDialect);
    }


    /**
     * Gets the DbUnit connection or creates one if it does not exist yet.
     *
     * @return The DbUnit connection, not null
     */
    public DbUnitDatabaseConnection getDbUnitDatabaseConnection() {
        if (dbUnitDatabaseConnection == null) {
            dbUnitDatabaseConnection = createDbUnitConnection();
        }
        return dbUnitDatabaseConnection;
    }


    /**
     * This method will first try to load a method level defined dataset. If no such file exists, a class level defined
     * dataset will be loaded. If neither of these files exist, nothing is done.
     * The name of the test data file at both method level and class level can be overridden using the
     * {@link DataSet} annotation. If specified using this annotation but not found, a {@link UnitilsException} is
     * thrown.
     *
     * @param testMethod The method, not null
     */
    public void insertTestData(Method testMethod) {
        try {
            IDataSet dataSet = getTestDataSet(testMethod);
            if (dataSet == null) {
                // no data set should be loaded
                return;
            }
            DatabaseOperation.CLEAN_INSERT.execute(getDbUnitDatabaseConnection(), dataSet);

        } catch (Exception e) {
            throw new UnitilsException("Error inserting test data from DbUnit dataset for method " + testMethod, e);
        } finally {
            closeJdbcConnection();
        }
    }


    /**
     * todo javadoc
     * <p/>
     * Compares the contents of the expected DbUnitDataSet with the contents of the database. Only the tables and columns
     * that occur in the expected DbUnitDataSet are compared with the database contents.
     *
     * @param testMethod The test method, not null
     */
    public void assertDbContentAsExpected(Method testMethod) {
        try {
            // get the expected dataset
            IDataSet expectedDataSet = getExpectedTestDataSet(testMethod);
            if (expectedDataSet == null) {
                // no data set should be compared
                return;
            }

            // first make sure every database update is flushed to the database
            getDatabaseModule().flushDatabaseUpdates();

            // get the actual data set
            IDataSet actualDataSet = getDbUnitDatabaseConnection().createDataSet(expectedDataSet.getTableNames());
            ITableIterator tables = expectedDataSet.iterator();

            // compare expected and actual data set
            while (tables.next()) {
                ITable expectedTable = tables.getTable();
                ITableMetaData metaData = expectedTable.getTableMetaData();
                ITable actualTable = actualDataSet.getTable(expectedTable.getTableMetaData().getTableName());
                ITable filteredActualTable = DefaultColumnFilter.includedColumnsTable(actualTable, metaData.getColumns());

                assertEquals(new SortedTable(expectedTable), new SortedTable(filteredActualTable, expectedTable.getTableMetaData()));
            }
        } catch (Exception e) {
            throw new UnitilsException("Error asserting db content as expected for test method " + testMethod, e);
        } finally {
            closeJdbcConnection();
        }
    }


    /**
     * Using the values of the method-level or class-level {@link DataSet} annotations, returns the data set for the
     * given test method. If no method-level or class-level {@link DataSet} annotation is found, null is returned.
     * If a method-level {@link DataSet} annotation is found this will be used, else the class-level will be used.
     * <p/>
     * The value of the found annotation determines which file needs to be used for the dataset. If a filename is
     * explicitly specified, this name will be used. Filenames that start with '/' are treated absolute. Filenames
     * that do not start with '/', are relative to the current class.
     * If an empty filename ("") is specified, it will first look for a file named 'classname'.'testmethod'.xml (as defined
     * in {@link #getMethodLevelDefaultTestDataSetFileName}). If that file does not exist it will look for a file
     * named 'classname'.xml {@link #getClassLevelDefaultTestDataSetFileName}).
     * <p/>
     * If a file is not found or could not be loaded (but was requested, because there is an annotation), an exception
     * is raised.
     *
     * @param testMethod The test method, not null
     * @return The dataset, null if there is no data set
     */
    public IDataSet getTestDataSet(Method testMethod) {
        Class<?> testClass = testMethod.getDeclaringClass();

        // get the value of the method-level annotation
        String dataSetFileName = null;
        DataSet dataSetAnnotationMethod = testMethod.getAnnotation(DataSet.class);
        if (dataSetAnnotationMethod != null) {
            dataSetFileName = dataSetAnnotationMethod.value();

        } else {
            // no method-level found, try class-level
            DataSet dataSetAnnotationClass = testClass.getAnnotation(DataSet.class);
            if (dataSetAnnotationClass != null) {
                dataSetFileName = dataSetAnnotationClass.value();
            }
        }

        // check no annotations found
        if (dataSetFileName == null) {
            return null;
        }

        // empty means, use default file name
        if ("".equals(dataSetFileName)) {
            // first try method specific default file name
            dataSetFileName = getMethodLevelDefaultTestDataSetFileName(testClass, testMethod);
            IDataSet dataSet = createDataSet(testClass, dataSetFileName);
            if (dataSet != null) {
                // found file, so return
                return dataSet;
            }
            // not found, try class default file name
            dataSetFileName = getClassLevelDefaultTestDataSetFileName(testClass);
        }

        IDataSet dataSet = createDataSet(testClass, dataSetFileName);
        if (dataSet == null) {
            throw new UnitilsException("Could not find DbUnit dataset with name " + dataSetFileName);
        }
        return dataSet;
    }


    //todo javadoc
    /**
     * @param testMethod The test method, not null
     * @return The dataset, null if there is no data set
     */
    public IDataSet getExpectedTestDataSet(Method testMethod) {
        Class<?> testClass = testMethod.getDeclaringClass();

        // get the value of the method-level annotation
        String dataSetFileName = null;
        ExpectedDataSet expectedDataSetAnnotation = testMethod.getAnnotation(ExpectedDataSet.class);
        if (expectedDataSetAnnotation != null) {
            dataSetFileName = expectedDataSetAnnotation.value();

        } else {
            // no method-level found, try class-level
            ExpectedDataSet expectedDataSetAnnotationClass = testClass.getAnnotation(ExpectedDataSet.class);
            if (expectedDataSetAnnotationClass != null) {
                dataSetFileName = expectedDataSetAnnotationClass.value();
            }
        }

        // check no annotations found
        if (dataSetFileName == null) {
            return null;
        }

        // empty means, use default file name
        if ("".equals(dataSetFileName)) {
            // first try method specific default file name
            dataSetFileName = getDefaultExpectedDataSetFileName(testClass, testMethod);
        }

        IDataSet dataSet = createDataSet(testClass, dataSetFileName);
        if (dataSet == null) {
            throw new UnitilsException("Could not find expected DbUnit dataset with name " + dataSetFileName);
        }
        return dataSet;
    }


    /**
     * Creates the dataset for the given file. Filenames that start with '/' are treated absolute. Filenames that
     * do not start with '/', are relative to the current class.
     *
     * @param testClass       The test class, not null
     * @param dataSetFilename The name, (start with '/' for absolute names), not null
     * @return The data set, null if the file does not exist
     */
    protected IDataSet createDataSet(Class testClass, String dataSetFilename) {
        InputStream in = null;
        try {
            in = testClass.getResourceAsStream(dataSetFilename);
            if (in == null) {
                // file does not exist
                return null;
            }

            IDataSet dataSet = new TablePerRowXmlDataSet(in);
            ReplacementDataSet replacementDataSet = new ReplacementDataSet(dataSet);
            replacementDataSet.addReplacementObject("[null]", null);
            return replacementDataSet;

        } catch (Exception e) {
            throw new UnitilsException("Unble to create DbUnit dataset for file " + dataSetFilename, e);
        } finally {
            closeQuietly(in);
        }
    }


    /**
     * Creates a new instance of dbUnit's <code>IDatabaseConnection</code>
     *
     * @return A new instance of dbUnit's <code>IDatabaseConnection</code>
     */
    protected DbUnitDatabaseConnection createDbUnitConnection() {
        // Create connection
        DbUnitDatabaseConnection connection = new DbUnitDatabaseConnection(getDatabaseModule().getDataSource(), schemaName);

        // Make sure correct dbms specific data types are used
        DatabaseConfig config = connection.getConfig();
        config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, dataTypeFactory);
        return connection;
    }


    /**
     * Closes (i.e. return to the pool) the JDBC Connection that is currently in use by the DbUnitDatabaseConnection
     */
    protected void closeJdbcConnection() {
        try {
            getDbUnitDatabaseConnection().closeJdbcConnection();

        } catch (SQLException e) {
            throw new UnitilsException("Error while closing connection", e);
        }
    }


    /**
     * Gets the name of the default testdata file at method level. The default name is constructed as
     * follows: classname + '.' + methodName + '.xml'
     *
     * @param testClass The test class, not null
     * @param method    The test method, not null
     * @return The default filename, not null
     */
    protected String getMethodLevelDefaultTestDataSetFileName(Class<?> testClass, Method method) {
        String className = testClass.getName();
        return className.substring(className.lastIndexOf(".") + 1) + "." + method.getName() + ".xml";
    }


    /**
     * Gets the name of the default testdata file at class level The default name is constructed as
     * follows: 'classname without packagename'.xml
     *
     * @param testClass The test class, not null
     * @return The default filename, not null
     */
    protected String getClassLevelDefaultTestDataSetFileName(Class<?> testClass) {
        String className = testClass.getName();
        return className.substring(className.lastIndexOf(".") + 1) + ".xml";
    }


    /**
     * Gets the name of the expected dataset file. The default name of this file is constructed as
     * follows: 'classname without packagename'.'testname'-result.xml.
     *
     * @param testClass The test class, not null
     * @param method    The test method, not null
     * @return The expected dataset filename, not null
     */
    protected static String getDefaultExpectedDataSetFileName(Class<?> testClass, Method method) {
        String className = testClass.getName();
        return className.substring(className.lastIndexOf(".") + 1) + "." + method.getName() + "-result.xml";
    }


    /**
     * @return Implementation of DatabaseModule, on which this module is dependent
     */
    protected DatabaseModule getDatabaseModule() {
        return Unitils.getInstance().getModulesRepository().getModuleOfType(DatabaseModule.class);
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
        public void beforeTestMethod(Object testObject, Method testMethod) {
            insertTestData(testMethod);
        }

        @Override
        public void afterTestMethod(Object testObject, Method testMethod) {
            assertDbContentAsExpected(testMethod);
        }

    }

}
