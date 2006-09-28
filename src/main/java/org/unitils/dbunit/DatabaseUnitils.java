/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.dbunit;

import org.unitils.dbmaintainer.config.DataSourceFactory;
import org.unitils.dbmaintainer.handler.StatementHandlerException;
import org.unitils.dbmaintainer.maintainer.DBMaintainer;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.annotation.ExpectedDataSet;
import org.unitils.util.ReflectionUtils;
import org.unitils.util.UnitilsConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.dbutils.DbUtils;
import org.dbunit.Assertion;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.*;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.ext.db2.Db2DataTypeFactory;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.ext.oracle.OracleDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;

import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.sql.SQLException;

/**
 * todo javadoc
 */
public class DatabaseUnitils {


    /* The annotated test instance */
    private Object testInstance;

    /* The pooled datasource instance */
    private static DataSource dataSource;

    /* The cached DBUnit dbUnitConnection instance */
    private static IDatabaseConnection dbUnitConnection;

    /* Property keys indicating if the database schema should be updated before performing the tests */
    private static final String PROPKEY_UPDATEDATABASESCHEMA_ENABLED = "updateDataBaseSchema.enabled";

    /* Property keys of the datasource factory classname */
    private static final String PROPKEY_DATASOURCEFACTORY_CLASSNAME = "dataSourceFactory.className";

    /* Property key of the SQL dialect of the underlying DBMS implementation */
    private static final String PROPKEY_DATABASE_DIALECT = "database.dialect";

    /* Property key of the name of the database schema */
    private static final String PROPKEY_SCHEMA_NAME = "dataSource.userName";


    // todo javadoc
    //todo exceptions
    public DatabaseUnitils(Object testInstance) throws Exception, SQLException {
        this.testInstance = testInstance;

        //create the singleton datasource
        dataSource = createDataSource();

        //create the dbUnitConnection instance
        dbUnitConnection = createDbUnitConnection(dataSource);

        //bring version test database schema up to date
        updateDatabaseSchemaIfNeeded();

        DatabaseOperation.CLEAN_INSERT.execute(dbUnitConnection, getDataSet());
    }


    /**
     * Compares the contents of the expected DataSet with the contents of the database. Only the tables that occur in
     * the expected DataSet are compared with the database content.
     * <p/>
     * todo javadoc
     */
    public void assertDBContentAsExpected() throws Exception {
        try {
            IDataSet expectedDataSet = getExpectedDataSet();
            IDataSet actualDataSet = dbUnitConnection.createDataSet(expectedDataSet.getTableNames());
            ITableIterator tables = expectedDataSet.iterator();

            while (tables.next()) {
                ITable expectedTable = tables.getTable();
                ITableMetaData metaData = expectedTable.getTableMetaData();
                ITable actualTable = actualDataSet.getTable(expectedTable.getTableMetaData().getTableName());
                ITable filteredActualTable = DefaultColumnFilter.includedColumnsTable(actualTable, metaData.getColumns());

                Assertion.assertEquals(new SortedTable(expectedTable), new SortedTable(filteredActualTable, expectedTable.getTableMetaData()));
            }
        } finally {
            if (dbUnitConnection != null) {
                DbUtils.closeQuietly(dbUnitConnection.getConnection());
            }
        }
    }


    /**
     * Determines whether the test database is outdated and, if that is the case, updates the database with the
     * latest changes. See {@link DBMaintainer} for more information.
     */
    protected void updateDatabaseSchemaIfNeeded() throws StatementHandlerException {

        Configuration configuration = UnitilsConfiguration.getInstance();

        if (configuration.getBoolean(PROPKEY_UPDATEDATABASESCHEMA_ENABLED)) {
            DBMaintainer dbMaintainer = new DBMaintainer(dataSource);
            dbMaintainer.updateDatabase();
        }
    }


    /**
     * This will first try to load a test specific dataset (see {@link #getTestDataSetFileName()}. If that file
     * does not exist, the default dataset is loaded (see {@link #getDefaultDataSetFileName()}. If both files do
     * not exist and there is a setup or teardown operation, a file not found exception is thrown.
     *
     * @return the dataset, can org null if the files were not found AND the setup/teardown operations are NONE
     * @throws Exception if the dataset could not org loaded
     */
    protected IDataSet getDataSet() throws Exception {
        //load the test specific dataset
        String testDataSetFileName = getTestDataSetFileName();
        IDataSet dataSet = loadDataSet(testDataSetFileName);
        if (dataSet == null) {
            //load the default dataset
            String defaultDataSetFileName = getDefaultDataSetFileName();
            dataSet = loadDataSet(defaultDataSetFileName);
            if (dataSet == null) {
                throw new FileNotFoundException("Unable to find test dataset with file name: " +
                        testDataSetFileName + " or " + defaultDataSetFileName);
            }
        }
        return dataSet;
    }

    /**
     * Gets the result dataset with a filename specified by {@link #getExpectedDataSetFileName()}.
     * If the file does not exist, a file not found exception is thrown.
     *
     * @return the dataset, not null
     * @throws Exception if the dataset could not org loaded
     */
    private IDataSet getExpectedDataSet() throws Exception {
        String dataSetFileName = getExpectedDataSetFileName();
        IDataSet dataSet = loadDataSet(dataSetFileName);
        if (dataSet == null) {
            throw new FileNotFoundException("Unable to find test dataset with file name: " + dataSetFileName);
        }
        return dataSet;
    }


    /**
     * Gets the name of the testdata file that is specific to the current test.
     * The name will org constructed as follows: 'classname without packagename'.'testname'.xml
     *
     * @return the test specific filename
     */
    private String getTestDataSetFileName() {
        Class<? extends Object> testInstanceClazz = testInstance.getClass();
        DataSet dataSet = testInstanceClazz.getAnnotation(DataSet.class);
        if (dataSet != null) {
            return dataSet.fileName();
        }

        String className = testInstanceClazz.getName();
        return className.substring(className.lastIndexOf(".") + 1) + "." + getTestName(testInstance) + ".xml";
    }

    /**
     * Gets the name of the default testdata file.
     * The name will org constructed as follows: 'classname without packagename'.xml
     *
     * @return the default filename
     */
    private String getDefaultDataSetFileName() {
        Class<? extends Object> testInstanceClazz = testInstance.getClass();
        DataSet dataSet = testInstanceClazz.getAnnotation(DataSet.class);
        if (dataSet != null) {
            return dataSet.fileName();
        }

        String className = testInstanceClazz.getName();
        return className.substring(className.lastIndexOf(".") + 1) + ".xml";
    }

    /**
     * Gets the name of the result testdata file.
     * The name will org constructed as follows: 'classname without packagename'.'testname'-result.xml
     *
     * @return the result filename
     */
    private String getExpectedDataSetFileName() {
        Method testMethod = getTestMethod(testInstance);
        if (testMethod != null) {
            testMethod.getAnnotation(ExpectedDataSet.class);
            //todo implement
        }

        String className = testInstance.getClass().getName();
        return className.substring(className.lastIndexOf(".") + 1) + "." + testInstance.getClass().getName() + "-result.xml";
    }


    /**
     * Loads a dataset from the file with the given name.
     * Filenames that start with '/' are treated absolute. Filenames that do not start with '/', are relative
     * to the current class.
     *
     * @param dataSetFilename the name, (start with '/' for absolute names)
     * @return the data set, or null if the file did not exist
     * @throws Exception if the dataset could not org loaded
     */
    protected IDataSet loadDataSet(String dataSetFilename) throws Exception {
        InputStream in = null;
        try {
            in = getClass().getResourceAsStream(dataSetFilename);
            if (in == null) {
                return null;
            }

            IDataSet dataSet = new GroupableFlatXmlDataSet(in);
            ReplacementDataSet replacementDataSet = new ReplacementDataSet(dataSet);
            replacementDataSet.addReplacementObject("[null]", null);
            return replacementDataSet;

        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    /**
     * Creates a datasource by using the factory that is defined by the dataSourceFactory.className property
     *
     * @return the datasource
     * @throws Exception if the factory or the datasource could not org created
     */
    protected DataSource createDataSource() throws Exception {

        Configuration configuration = UnitilsConfiguration.getInstance();

        DataSourceFactory dataSourceFactory = ReflectionUtils.createInstanceOfType(configuration.getString(PROPKEY_DATASOURCEFACTORY_CLASSNAME));
        dataSourceFactory.init();
        return dataSourceFactory.createDataSource();
    }


    //todo refactor
    protected IDatabaseConnection createDbUnitConnection(DataSource dataSource) throws SQLException {

        Configuration configuration = UnitilsConfiguration.getInstance();

        IDatabaseConnection connection = new DatabaseConnection(dataSource.getConnection(), configuration.getString(PROPKEY_SCHEMA_NAME).toUpperCase());

        String databaseDialect = configuration.getString(PROPKEY_DATABASE_DIALECT);
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
        return connection;
    }


    //todo override testname method
    protected String getTestName(Object testInstance) {
        try {
            Method method = testInstance.getClass().getMethod("getName");
            return (String) method.invoke(testInstance);
        } catch (Exception e) {
            //Todo implement
            e.printStackTrace();
            return null;
        }
    }

    protected Method getTestMethod(Object testInstance) {
        try {
            String testMethodName = getTestName(testInstance);
            return testInstance.getClass().getMethod(testMethodName);

        } catch (Exception e) {
            //Todo implement
            e.printStackTrace();
            return null;
        }
    }
}



