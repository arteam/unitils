package be.ordina.unitils.testing.dao;

import be.ordina.unitils.testing.UnitilsModule;
import be.ordina.unitils.util.ReflectionUtils;
import be.ordina.unitils.util.PropertiesUtils;
import be.ordina.unitils.db.config.DataSourceFactory;
import be.ordina.unitils.db.handler.StatementHandlerException;
import be.ordina.unitils.db.maintainer.DBMaintainer;

import javax.sql.DataSource;
import java.util.Properties;
import java.util.List;
import java.util.Collections;
import java.sql.SQLException;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.ext.oracle.OracleDataTypeFactory;
import org.dbunit.ext.db2.Db2DataTypeFactory;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.DatabaseUnitException;

/**
 * @author Filip Neven
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

    /* The configuration (unittest.properties) */
    private Properties properties;

    /* The pooled datasource instance */
    private DataSource dataSource;

    private boolean firstTime = true;

    private static ThreadLocal<IDatabaseConnection> connectionHolder = new ThreadLocal<IDatabaseConnection>();

    public void beforeSuite(Properties unitilsProperties) {
        properties = unitilsProperties;
        firstTime = true;
    }

    public void beforeClass(Object test) throws Exception {
        if (isDatabaseTest(test) && firstTime) {
            firstTime = false;
            //create the singleton datasource
            dataSource = createDataSource();
            //create the connection instance
            connectionHolder.set(createConnection());
            //bring version test database schema up to date
            updateDatabaseSchemaIfNeeded();
        }
    }

    private boolean isDatabaseTest(Object test) {
        return test.getClass().getAnnotation(DatabaseTest.class) != null;
    }

    public void beforeTestMethod(Object test, String methodName) {
        try {
            DatabaseOperation.CLEAN_INSERT.execute(getConnection(), getDataSet(test, methodName));
        } catch (Exception e) {
            throw new RuntimeException("Error while trying to insert test data in database", e);
        }
    }

    /**
     * Creates a datasource by using the factory that is defined by the dataSourceFactory.className property
     *
     * @return the datasource
     */
    private DataSource createDataSource() {
        DataSourceFactory dataSourceFactory = ReflectionUtils.getInstance(PropertiesUtils.getPropertyRejectNull(properties, PROPKEY_DATASOURCEFACTORY_CLASSNAME));
        dataSourceFactory.init(properties);
        return dataSourceFactory.createDataSource();
    }

    private IDatabaseConnection createConnection() throws SQLException {
        IDatabaseConnection connection = new DatabaseConnection(dataSource.getConnection(),
                PropertiesUtils.getPropertyRejectNull(properties, PROPKEY_SCHEMA_NAME).toUpperCase());
        String databaseDialect = PropertiesUtils.getPropertyRejectNull(properties, PROPKEY_DATABASE_DIALECT);
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
     * latest changes. See {@link be.ordina.unitils.db.maintainer.DBMaintainer} for more information.
     */
    protected void updateDatabaseSchemaIfNeeded() throws StatementHandlerException {
        if ("true".equalsIgnoreCase(properties.getProperty(PROPKEY_UPDATEDATABASESCHEMA_ENABLED))) {
            DBMaintainer dbMaintainer = new DBMaintainer(properties, dataSource);
            dbMaintainer.updateDatabase();
        }
    }

    /**
     * Implementation of {@link org.dbunit.DatabaseTestCase#getDataSet()}.
     * This will first try to load a test specific dataset (see {@link #getTestDataSetFileName(Object,String)}. If that file
     * does not exist, the default dataset is loaded (see {@link #getDefaultDataSetFileName(Object)}. If both files do
     * not exist and there is a setup or teardown operation, a file not found exception is thrown.
     *
     * @return the dataset, can org null if the files were not found AND the setup/teardown operations are NONE
     * @throws Exception if the dataset could not org loaded
     * @param test
     * @param methodName
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
     * @return the test specific filename
     * @param test
     * @param methodName
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
     * @return the default filename
     * @param test
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
        return new Class[] {};
    }
}
