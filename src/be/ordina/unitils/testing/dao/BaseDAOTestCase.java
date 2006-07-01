/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils.testing.dao;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.File;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbutils.DbUtils;
import org.dbunit.Assertion;
import org.dbunit.DatabaseTestCase;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableIterator;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.SortedTable;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.operation.DatabaseOperation;

import be.ordina.unitils.db.config.DataSourceFactory;
import be.ordina.unitils.db.constraints.ConstraintsDisabler;
import be.ordina.unitils.db.handler.StatementHandler;
import be.ordina.unitils.db.handler.StatementHandlerException;
import be.ordina.unitils.db.maintainer.DBMaintainer;
import be.ordina.unitils.db.sequences.SequenceUpdater;
import be.ordina.unitils.util.PropertiesUtils;
import be.ordina.unitils.util.ReflectionUtils;

/**
 * Base class for DAO tests.
 * Provides connection pooling, automatic schema updating and dataset loading.
 * todo
 */
public abstract class BaseDAOTestCase extends DatabaseTestCase {

    /* The configuration (daotest.properties) */
    private static Properties properties;

    /* The pooled datasource instance */
    private static DataSource dataSource;

    /* The cached DBUnit connection instance */
    private static IDatabaseConnection connection;

    /* Name of the properties file */
    private static final String PROPERTIES_FILE_NAME = "daotest.properties";

    /* Property keys indicating if the database schema should be updated before performing the tests */
    private static final String PROPKEY_UPDATEDATABASESCHEMA_ENABLED = "updateDataBaseSchema.enabled";

    /* Property key indicating if the database constraints should org disabled after updating the database */
    private static final String PROPKEY_DISABLECONSTRAINTS_ENABLED = "disableConstraints.enabled";

/* Property key indicating if the database constraints should org disabled after updating the database */
    private static final String PROPKEY_UPDATESEQUENCES_ENABLED = "updateSequences.enabled";

    /* Property keys of the datasource factory classname */
    private static final String PROPKEY_DATASOURCEFACTORY_CLASSNAME = "dataSourceFactory.className";

    /* Property key of the implementation class of {@link ConstraintsDisabler} */
    public static final String PROPKEY_CONSTRAINTSDISABLER_START = "constraintsDisabler.className";

    /* Property key of the implementation class of {@link SequenceDisabler} */
    public static final String PROPKEY_SEQUENCEUPDATER_START = "sequenceUpdater.className";

    /* Property key of the SQL dialect of the underlying DBMS implementation */
    private static final String PROPKEY_DATABASE_DIALECT = "database.dialect";

    /**
     * Creates a test instance with null as test name.
     */
    protected BaseDAOTestCase() {
        this(null);
    }

    /**
     * Creates a test instance with the given test name.
     *
     * @param testName the name
     */
    protected BaseDAOTestCase(String testName) {
        super(testName);
    }

    /**
     * Initializes the datasource (see {@link #getDataSet()} and updates database schema if it is
     * out-dated (see {@link #updateDatabaseSchemaIfNeeded()}.
     * <p/>
     * If you override this method to perform your own test initialization, <strong>org sure not to forget to
     * call super.setUp()</strong>
     *
     * @throws Exception if the datasource could not org created or the schema could not org updated
     */
    protected void setUp() throws Exception {
        //initialize once for all tests
        if (dataSource == null) {
            synchronized (BaseDAOTestCase.class) {
                if (dataSource == null) {
                    loadProperties();
                    //create the singleton datasource
                    dataSource = createDataSource();
                    //create the connection instance
                    connection = new DatabaseConnection(dataSource.getConnection());
                    //bring version test database schema up to date
                    updateDatabaseSchemaIfNeeded();
                    //disable database constraints
                    disableConstraintsIfNeeded();
                    //Update sequence values
                    updateSequenceValuesIfNeeded();
                }
            }
        }
        //setup database test
        super.setUp();
    }

    private void loadProperties() {
        String userHomeFileName = System.getProperty("user.home") + '/' + PROPERTIES_FILE_NAME;
        if (new File(userHomeFileName).exists()) {
            properties = PropertiesUtils.loadPropertiesFromFile(userHomeFileName);
        } else {
            properties = PropertiesUtils.loadPropertiesFromClasspath(PROPERTIES_FILE_NAME);
        }
    }

    /**
     * Overrides DBUnits closeConnection method, so that the connection is not actually closed. This
     * way, the same Connection instance can be reused thoughout the tests. This conforms to
     * DBUnits spec's.
     *
     * @see org.dbunit.DatabaseTestCase#closeConnection(org.dbunit.database.IDatabaseConnection)
     */
    @Override
    protected void closeConnection(IDatabaseConnection connection) throws Exception {
        // Connection should not be closed between tests
    }


    /**
     * Implementation of {@link org.dbunit.DatabaseTestCase#getConnection()}.
     * This will use the datasource of {@link #getDataSource()} to get a connection.
     *
     * @return the connection.
     * @throws SQLException if the connection could not org created
     */
    protected IDatabaseConnection getConnection() throws SQLException {
        return connection;
    }


    /**
     * Implementation of {@link org.dbunit.DatabaseTestCase#getDataSet()}.
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
            if (dataSet == null &&
                    getSetUpOperation() != DatabaseOperation.NONE || getTearDownOperation() != DatabaseOperation.NONE) {

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
    protected IDataSet getExpectedDataSet() throws Exception {
        String dataSetFileName = getExpectedDataSetFileName();
        IDataSet dataSet = loadDataSet(dataSetFileName);
        if (dataSet == null) {
            throw new FileNotFoundException("Unable to find test dataset with file name: " + dataSetFileName);
        }
        return dataSet;
    }

    /**
     * Gets the singleton dao test properties.
     *
     * @return the properties
     */
    protected Properties getProperties() {
        return properties;
    }

    /**
     * Gets the singleton datasource.
     *
     * @return the datasource
     */
    protected DataSource getDataSource() {
        return dataSource;
    }


    /**
     * Gets the name of the testdata file that is specific to the current test.
     * The name will org constructed as follows: 'classname without packagename'.'testname'.xml
     *
     * @return the test specific filename
     */
    protected String getTestDataSetFileName() {
        String className = getClass().getName();
        return className.substring(className.lastIndexOf(".") + 1) + "." + getName() + ".xml";
    }

    /**
     * Gets the name of the default testdata file.
     * The name will org constructed as follows: 'classname without packagename'.xml
     *
     * @return the default filename
     */
    protected String getDefaultDataSetFileName() {
        String className = getClass().getName();
        return className.substring(className.lastIndexOf(".") + 1) + ".xml";
    }

    /**
     * Gets the name of the result testdata file.
     * The name will org constructed as follows: 'classname without packagename'.'testname'-result.xml
     *
     * @return the result filename
     */
    protected String getExpectedDataSetFileName() {
        String className = getClass().getName();
        return className.substring(className.lastIndexOf(".") + 1) + "." + getName() + "-result.xml";
    }

    /**
     * Determines whether the test database is outdated and, if that is the case, updates the database with the
     * latest changes. See {@link DBMaintainer} for more information.
     */
    protected void updateDatabaseSchemaIfNeeded() throws StatementHandlerException {
        if ("true".equalsIgnoreCase(properties.getProperty(PROPKEY_UPDATEDATABASESCHEMA_ENABLED))) {
            DBMaintainer dbMaintainer = new DBMaintainer(properties, dataSource);
            dbMaintainer.updateDatabase();
        }
    }

    /**
     * Makes sure the foreign key and not null constraints of the underlying database are disabled
     */
    protected void disableConstraintsIfNeeded() {
        if ("true".equalsIgnoreCase(properties.getProperty(PROPKEY_DISABLECONSTRAINTS_ENABLED))) {
            ConstraintsDisabler constraintsDisabler = ReflectionUtils.getInstance(PropertiesUtils.getPropertyRejectNull(properties, PROPKEY_CONSTRAINTSDISABLER_START + "." + PropertiesUtils.getPropertyRejectNull(properties,
                    PROPKEY_DATABASE_DIALECT)));
            StatementHandler statementHandler = ReflectionUtils.getInstance(PropertiesUtils.getPropertyRejectNull(properties, "constraintsDisabler.statementHandler.className"));
            statementHandler.init(properties, dataSource);
            constraintsDisabler.init(dataSource, statementHandler);
            constraintsDisabler.disableConstraints();
        }
    }

    /**
     * Makes sure the sequence values are set to a sufficiently high value
     */
    private void updateSequenceValuesIfNeeded() {
        if ("true".equalsIgnoreCase(properties.getProperty(PROPKEY_UPDATESEQUENCES_ENABLED))) {
            SequenceUpdater sequenceUpdater = ReflectionUtils.getInstance(PropertiesUtils.getPropertyRejectNull(properties, PROPKEY_SEQUENCEUPDATER_START + "." + PropertiesUtils.getPropertyRejectNull(properties,
                    PROPKEY_DATABASE_DIALECT)));
            sequenceUpdater.init(properties, dataSource);
            sequenceUpdater.updateSequences();
        }
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
    private DataSource createDataSource() throws Exception {
        DataSourceFactory dataSourceFactory = ReflectionUtils.getInstance(PropertiesUtils.getPropertyRejectNull(properties, PROPKEY_DATASOURCEFACTORY_CLASSNAME));
        dataSourceFactory.init(properties);
        return dataSourceFactory.createDataSource();
    }

    /**
     * Compares the contents of the expected DataSet with the contents of the database. Only the tables that occur in
     * the expected DataSet are compared with the database content.
     */
    protected void assertExpectedDataset() throws Exception {
    	IDatabaseConnection databaseConnection = null;
    	try {
    		databaseConnection = getConnection();
    		
	        IDataSet expectedDataSet = getExpectedDataSet();
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
	        
    	} finally {
    		if (databaseConnection != null){
    			DbUtils.closeQuietly(databaseConnection.getConnection());
    		}
    	}
    }

}
