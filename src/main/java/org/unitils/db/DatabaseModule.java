package org.unitils.db;

import org.apache.commons.configuration.Configuration;
import org.unitils.core.TestContext;
import org.unitils.core.TestListener;
import org.unitils.core.UnitilsException;
import org.unitils.core.UnitilsModule;
import org.unitils.db.annotations.AfterCreateConnection;
import org.unitils.db.annotations.AfterCreateDataSource;
import org.unitils.dbmaintainer.config.DataSourceFactory;
import org.unitils.dbmaintainer.constraints.ConstraintsCheckDisablingDataSource;
import org.unitils.dbmaintainer.constraints.ConstraintsDisabler;
import org.unitils.dbmaintainer.handler.JDBCStatementHandler;
import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.handler.StatementHandlerException;
import org.unitils.dbmaintainer.maintainer.DBMaintainer;
import org.unitils.dbunit.DatabaseTest;
import org.unitils.util.AnnotationUtils;
import org.unitils.util.ReflectionUtils;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Module that provides basic support for database testing. This module provides the following services to unit tests
 * <ul>
 * <li>Connection pooling: A connection pooled DataSource is created, and supplied to methods annotated with
 * {@link AfterCreateDataSource}</li>
 * <li>A 'current connection' is associated with each thread from which the method #getCurrentConnection is called</li>
 * <li>If the updateDataBaseSchema.enabled property is set to true, the {@link DBMaintainer} is invoked to update the
 * database and prepare it for unit testing (see {@link DBMaintainer} Javadoc)</li>
 */
public class DatabaseModule implements UnitilsModule {

    /* Property keys indicating if the database schema should be updated before performing the tests */
    static final String PROPKEY_UPDATEDATABASESCHEMA_ENABLED = "updateDataBaseSchema.enabled";

    /* Property keys of the datasource factory classname */
    static final String PROPKEY_DATASOURCEFACTORY_CLASSNAME = "dataSourceFactory.className";

    /* Property key indicating if the database constraints should org disabled after updating the database */
    private static final String PROPKEY_DISABLECONSTRAINTS_ENABLED = "dbMaintainer.disableConstraints.enabled";

    /* Property key of the implementation class of {@link ConstraintsDisabler} */
    private static final String PROPKEY_CONSTRAINTSDISABLER_START = "constraintsDisabler.className";

    /* Property key of the SQL dialect of the underlying DBMS implementation */
    private static final String PROPKEY_DATABASE_DIALECT = "database.dialect";

    /* The pooled datasource instance */
    private DataSource dataSource;

    private Configuration configuration;

    private boolean disableConstraints;

    private boolean updateDatabaseSchemaEnabled;

    /*
    * Database connection holder: ensures that if the method getCurrentConnection is always used for getting
    * a connection to the database, at most one database connection exists per thread
    */
    private ThreadLocal<Connection> connectionHolder = new ThreadLocal<Connection>();

    public void init(Configuration configuration) {
        this.configuration = configuration;

        disableConstraints = configuration.getBoolean(PROPKEY_DISABLECONSTRAINTS_ENABLED);
        updateDatabaseSchemaEnabled = configuration.getBoolean(PROPKEY_UPDATEDATABASESCHEMA_ENABLED);
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
     * Inializes the database setup. I.e., creates a <code>DataSource</code> and updates the database schema if needed
     * using the {@link DBMaintainer}
     */
    protected void initDatabase(Object testObject) {
        try {
            if (dataSource == null) {
                //create the singleton datasource
                dataSource = createDataSource();
                //check if the database must be updated using the DBMaintainer
                updateDatabaseSchemaIfNeeded();
            }
        } catch (Exception e) {
            throw new UnitilsException("Error while intializing database connection", e);
        }
    }

    /**
     * Creates a datasource by using the factory that is defined by the dataSourceFactory.className property
     *
     * @return the datasource
     */
    protected DataSource createDataSource() {
        DataSourceFactory dataSourceFactory = createDataSourceFactory();
        dataSourceFactory.init(configuration);
        DataSource dataSource = dataSourceFactory.createDataSource();

        // If contstraints disabling is active, a ConstraintsCheckDisablingDataSource is
        // returned that wrappes the DataSource object
        if (disableConstraints) {
            ConstraintsDisabler constraintsDisabler = createConstraintsDisabler(dataSource);
            dataSource = new ConstraintsCheckDisablingDataSource(dataSource, constraintsDisabler);
        }
        return dataSource;
    }

    /**
     * Creates the configured instance of the {@link ConstraintsDisabler}
     *
     * @param dataSource
     * @return The configured instance of the {@link ConstraintsDisabler}
     */
    protected ConstraintsDisabler createConstraintsDisabler(DataSource dataSource) {

        String databaseDialect = configuration.getString(PROPKEY_DATABASE_DIALECT);
        String constraintsDisablerClassName = configuration.getString(PROPKEY_CONSTRAINTSDISABLER_START + "." + databaseDialect);

        StatementHandler statementHandler = new JDBCStatementHandler();
        statementHandler.init(configuration, dataSource);

        ConstraintsDisabler constraintsDisabler = ReflectionUtils.createInstanceOfType(constraintsDisablerClassName);
        constraintsDisabler.init(configuration, dataSource, statementHandler);
        return constraintsDisabler;
    }


    /**
     * @return The <code>DataSource</code>
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * @return The database connection that is associated with the current thread.
     */
    public Connection getCurrentConnection() {
        Connection currentConnection = connectionHolder.get();
        if (currentConnection == null) {
            try {
                currentConnection = getDataSource().getConnection();
            } catch (SQLException e) {
                throw new UnitilsException("Error while establishing connection to the database", e);
            }
            connectionHolder.set(currentConnection);
        }
        return currentConnection;
    }

    /**
     * Calls all methods annotated with {@link AfterCreateDataSource}
     *
     * @param testObject
     */
    protected void callAfterCreateDataSourceMethods(Object testObject) {
        List<Method> methods = AnnotationUtils.getMethodsAnnotatedWith(testObject.getClass(), AfterCreateDataSource.class);
        for (Method method : methods) {
            try {
                ReflectionUtils.invokeMethod(testObject, method, dataSource);

            } catch (UnitilsException e) {

                throw new UnitilsException("Unable to invoke after create DataSource method. Ensure that this method has " +
                        "following signature: void myMethod(DataSource dataSource)", e);
            }
        }
    }

    /**
     * Calls all methods annotated with {@link AfterCreateConnection}
     *
     * @param testObject
     */
    protected void callAfterCreateConnectionMethods(Object testObject) {
        List<Method> methods = AnnotationUtils.getMethodsAnnotatedWith(testObject.getClass(), AfterCreateConnection.class);
        for (Method method : methods) {
            try {
                ReflectionUtils.invokeMethod(testObject, method, getCurrentConnection());

            } catch (UnitilsException e) {

                throw new UnitilsException("Unable to invoke after create Connection method. Ensure that this method has " +
                        "following signature: void myMethod(Connection conn)", e);
            }
        }
    }

    /**
     * Determines whether the test database is outdated and, if that is the case, updates the database with the
     * latest changes. See {@link org.unitils.dbmaintainer.maintainer.DBMaintainer} for more information.
     */
    protected void updateDatabaseSchemaIfNeeded() throws StatementHandlerException {

        if (updateDatabaseSchemaEnabled) {
            DBMaintainer dbMaintainer = createDbMaintainer(configuration);
            dbMaintainer.updateDatabase();
        }
    }

    /**
     * Creates a new instance of the DBMaintainer for the given <code>DataSource</code>
     *
     * @return a new instance of the DBMaintainer
     */
    protected DBMaintainer createDbMaintainer(Configuration configuration) {
        return new DBMaintainer(configuration, dataSource);
    }

    /**
     * @return The {@link TestListener} associated with this module
     */
    public TestListener createTestListener() {
        return new DatabaseTestListener();
    }

    /**
     * Returns an instance of the configured {@link DataSourceFactory}
     *
     * @return The configured {@link DataSourceFactory}
     */
    protected DataSourceFactory createDataSourceFactory() {
        String dataSourceFactoryClassName = configuration.getString(PROPKEY_DATASOURCEFACTORY_CLASSNAME);
        return ReflectionUtils.createInstanceOfType(dataSourceFactoryClassName);
    }


    /**
     * DatabaseTestListener that makes callbacks to methods of this module while running tests.
     */
    private class DatabaseTestListener extends TestListener {

        @Override
        public void beforeTestClass(TestContext testContext) {
            if (isDatabaseTest(testContext.getTestClass())) {
                initDatabase(testContext.getTestObject());
            }
        }

        // todo these calls must be done each time a new test object is created. For JUnit this is before every test
        // for TestNG this is before every test class.
        @Override
        public void beforeTestMethod(TestContext testContext) {
            if (isDatabaseTest(testContext.getTestClass())) {
                //call methods annotated with AfterCreateDataSource, if any
                callAfterCreateDataSourceMethods(testContext.getTestObject());
                //call methods annotated with AfterCreateConnection, if any
                callAfterCreateConnectionMethods(testContext.getTestObject());
            }
        }

    }
}
