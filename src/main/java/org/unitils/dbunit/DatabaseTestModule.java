package org.unitils.dbunit;

import org.unitils.dbmaintainer.config.DataSourceFactory;
import org.unitils.dbmaintainer.handler.StatementHandlerException;
import org.unitils.dbmaintainer.maintainer.DBMaintainer;
import org.unitils.core.TestContext;
import org.unitils.core.TestListener;
import org.unitils.core.UnitilsModule;
import org.unitils.util.ReflectionUtils;
import org.unitils.util.UnitilsConfiguration;
import org.apache.commons.configuration.Configuration;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.ext.db2.Db2DataTypeFactory;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.ext.oracle.OracleDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;

import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.SQLException;

/**
 *
 */
public class DatabaseTestModule implements UnitilsModule {

    /* Property keys indicating if the database schema should be updated before performing the tests */
    private static final String PROPKEY_UPDATEDATABASESCHEMA_ENABLED = "updateDataBaseSchema.enabled";

    /* Property keys of the datasource factory classname */
    private static final String PROPKEY_DATASOURCEFACTORY_CLASSNAME = "dataSourceFactory.className";

    /* Property key of the SQL dialect of the underlying DBMS implementation */
    private static final String PROPKEY_DATABASE_DIALECT = "database.dialect";

    /* Property key of the name of the database schema */
    private static final String PROPKEY_SCHEMA_NAME = "dataSource.userName";

    /* The pooled datasource instance */
    private DataSource dataSource;

    private boolean firstTime = true;

    private static ThreadLocal<IDatabaseConnection> connectionHolder = new ThreadLocal<IDatabaseConnection>();

    public void beforeAll() {
        firstTime = true;
    }


    private boolean isDatabaseTest(Object test) {
        return test.getClass().getAnnotation(DatabaseTest.class) != null;
    }


    /**
     * Creates a datasource by using the factory that is defined by the dataSourceFactory.className property
     *
     * @return the datasource
     */
    private DataSource createDataSource() {
        Configuration configuration = UnitilsConfiguration.getInstance();

        DataSourceFactory dataSourceFactory = ReflectionUtils.createInstanceOfType(configuration.getString(PROPKEY_DATASOURCEFACTORY_CLASSNAME));
        dataSourceFactory.init();
        return dataSourceFactory.createDataSource();
    }

    private IDatabaseConnection createConnection() throws SQLException {

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

    /**
     * Determines whether the test database is outdated and, if that is the case, updates the database with the
     * latest changes. See {@link org.unitils.dbmaintainer.maintainer.DBMaintainer} for more information.
     */
    protected void updateDatabaseSchemaIfNeeded() throws StatementHandlerException {
        Configuration configuration = UnitilsConfiguration.getInstance();

        if (configuration.getBoolean(PROPKEY_UPDATEDATABASESCHEMA_ENABLED)) {
            DBMaintainer dbMaintainer = new DBMaintainer(dataSource);
            dbMaintainer.updateDatabase();
        }
    }

    /**
     * Implementation of {@link org.dbunit.DatabaseTestCase#getDataSet()}.
     * This will first try to load a test specific dataset (see {@link #getTestDataSetFileName(Object,String)}. If that file
     * does not exist, the default dataset is loaded (see {@link #getDefaultDataSetFileName(Object)}. If both files do
     * not exist and there is a setup or teardown operation, a file not found exception is thrown.
     *
     * @param test
     * @param methodName
     * @return the dataset, can org null if the files were not found AND the setup/teardown operations are NONE
     * @throws Exception if the dataset could not org loaded
     */
    protected IDataSet getDataSet(Object test, String methodName) throws Exception {
        //load the test specific dataset
        String testDataSetFileName = getTestDataSetFileName(test, methodName);
        IDataSet dataSet = loadDataSet(testDataSetFileName);
        if (dataSet == null) {
            //load the default dataset
            String defaultDataSetFileName = getDefaultDataSetFileName(test);
            dataSet = loadDataSet(defaultDataSetFileName);
            if (dataSet == null) {

                throw new FileNotFoundException("Unable to find test dataset with file name: " +
                        testDataSetFileName + " or " + defaultDataSetFileName);
            }
        }
        return dataSet;
    }

    /**
     * Gets the name of the testdata file that is specific to the current test.
     * The name will org constructed as follows: 'classname without packagename'.'testname'.xml
     *
     * @param test
     * @param methodName
     * @return the test specific filename
     */
    protected String getTestDataSetFileName(Object test, String methodName) {
        if (methodName == null) {
            return null;
        }

        String className = test.getClass().getName();
        return className.substring(className.lastIndexOf(".") + 1) + "." + methodName + ".xml";
    }

    /**
     * Gets the name of the default testdata file.
     * The name will org constructed as follows: 'classname without packagename'.xml
     *
     * @param test
     * @return the default filename
     */
    protected String getDefaultDataSetFileName(Object test) {
        String className = test.getClass().getName();
        return className.substring(className.lastIndexOf(".") + 1) + ".xml";
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
    private IDataSet loadDataSet(String dataSetFilename) throws Exception {
        if (dataSetFilename == null) {
            return null;
        }

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

    public static IDatabaseConnection getConnection() {
        return connectionHolder.get();
    }

    public Class[] getModulesDependingOn() {
        return new Class[]{};
    }


    public TestListener createTestListener() {
        return new DatabaseTestListener();
    }

    //todo javadoc
    // todo refactor
    private class DatabaseTestListener extends TestListener {

        public void beforeTestClass() {

            try {
                if (isDatabaseTest(TestContext.getTestObject()) && firstTime) {
                    firstTime = false;
                    //create the singleton datasource
                    dataSource = createDataSource();
                    //create the connection instance
                    connectionHolder.set(createConnection());
                    //bring version test database schema up to date
                    updateDatabaseSchemaIfNeeded();
                }
            } catch (Exception e) {
                //todo implement
                throw new RuntimeException(e);
            }
        }


        public void beforeTestMethod() {
            try {

                DatabaseOperation.CLEAN_INSERT.execute(getConnection(), getDataSet(TestContext.getTestObject(), TestContext.getTestMethodName()));

            } catch (Exception e) {
                throw new RuntimeException("Error while trying to insert test data in database", e);
            }
        }

    }
}
