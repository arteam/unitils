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
import org.unitils.core.*;
import org.unitils.db.DatabaseModule;
import org.unitils.dbunit.annotation.DbUnitDataSet;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * todo javadoc
 * todo implement following:
 * - overriding class-level dataset filename using annotation on class
 * - overriding of dataset filename using annotation on method
 * - expected dataset
 * - (if possible) clear database completely before every test
 */
public class DbUnitModule implements UnitilsModule {

    /* Property key of the name of the database schema */
    private static final String PROPKEY_SCHEMA_NAME = "dataSource.schemaName";

    /* Property key of the SQL dialect of the underlying DBMS implementation */
    private static final String PROPKEY_DATABASE_DIALECT = "database.dialect";

    /* Object that DbUnit uses to connect to the database and to cache some database metadata. Since DBUnit's data
       caching is time-consuming, this object is created only once and used througout the test run. */
    private IDatabaseConnection dbUnitDatabaseConnection;


    private String databaseSchemaName;

    private String databaseDialect;


    public void init(Configuration configuration) {
        databaseSchemaName = configuration.getString(PROPKEY_SCHEMA_NAME).toUpperCase();
        databaseDialect = configuration.getString(PROPKEY_DATABASE_DIALECT);
    }

    /**
     * @return The TestListener object that implements Unitils' DbUnit support
     */
    public TestListener createTestListener() {
        return new DbUnitListener();
    }

    /**
     * @param testClass
     * @return True if the test class is a database test, i.e. is annotated with the {@link DatabaseTest} annotation,
     *         false otherwise
     */
    protected boolean isDatabaseTest(Class<?> testClass) {
        return testClass.getAnnotation(DatabaseTest.class) != null;
    }

    /**
     * If this is the first time that we encounter a test class annotated with {@link DatabaseTest}, a new instance
     * of the dbUnit's <code>IDatabaseConnection</code> is created, that is used througout the whole test run.
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
        DatabaseModule databaseModule = getDatabaseTestModule();
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
     * Closes the DbUnit connection, and also the wrapped Jdbc Connection object
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
     * Uses dbUnit to insert test data in the database. The dbUnit data file that is loaded is defined by the following
     * rules:
     * First, we try to load a test specific dataset (see {@link #getTestDataSetFileName(Class,Method)}. If that file
     * does not exist, the default dataset is loaded (see {@link #getDefaultDataSetFileName(Class)}. If neither of
     * these files exists, a <code>UnitilsException</code> is thrown.
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
            throw new UnitilsException("Error when trying to insert test data from DBUnit xml file", e);
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
     * Loads a dataset from the file with the given name.
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
    public void assertDBContentAsExpected() {

        TestContext testContext = Unitils.getInstance().getTestContext();
        Class testClass = testContext.getTestClass();
        Method testMethod = testContext.getTestMethod();

        assertDBContentAsExpected(testClass, testMethod.getName());
    }

    /**
     * Compares the contents of the expected DbUnitDataSet with the contents of the database. Only the tables and columns
     * that occur in the expected DbUnitDataSet are compared with the database contents.
     */
    private void assertDBContentAsExpected(Class testClass, String testMethodName) {
        try {
            IDatabaseConnection databaseConnection = getDbUnitDatabaseConnection();

            IDataSet expectedDataSet = getExpectedDataSet(testClass, testMethodName);
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
     * Gets the result dataset with a filename specified by {@link #getExpectedDataSetFileName(Class, String)}.
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
     * Gets the name of the result testdata file.
     * The name will org constructed as follows: 'classname without packagename'.'testname'-result.xml
     *
     * @return the result filename
     */
    protected static String getExpectedDataSetFileName(Class testClass, String methodName) {
        String className = testClass.getName();
        return className.substring(className.lastIndexOf(".") + 1) + "." + methodName + "-result.xml";
    }

    /**
     * @return Implementation of DatabaseModule, on which this module is dependent
     */
    private DatabaseModule getDatabaseTestModule() {
        return Unitils.getModulesRepository().getModule(DatabaseModule.class);
    }

    /**
     * Test listener that is called while the test framework is running tests
     */
    private class DbUnitListener extends TestListener {

        @Override
        public void beforeAll(TestContext testContext) {
            if (getDatabaseTestModule() == null) {
                throw new UnitilsException("Invalid configuration: DatabaseModule should be enabled and DbUnitModule " +
                        "should be configured to run after DatabaseModule");
            }
        }

        @Override
        public void beforeTestClass(TestContext testContext) {
            if (isDatabaseTest(testContext.getTestClass())) {
                initDbUnitConnection();
            }
        }

        @Override
        public void beforeTestMethod(TestContext testContext) {
            if (isDatabaseTest(testContext.getTestClass())) {
                insertTestData(testContext.getTestClass(), testContext.getTestMethod());
            }
        }

        @Override
        public void afterAll(TestContext testContext) {
            closeDbUnitConnection();
        }

    }

}
