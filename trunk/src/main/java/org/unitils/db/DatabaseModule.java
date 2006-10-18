/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.db;

import org.apache.commons.configuration.Configuration;
import org.unitils.core.TestListener;
import org.unitils.core.UnitilsException;
import org.unitils.core.UnitilsModule;
import org.unitils.db.annotations.TestDataSource;
import org.unitils.db.annotations.DatabaseTest;
import org.unitils.dbmaintainer.config.DataSourceFactory;
import org.unitils.dbmaintainer.constraints.ConstraintsCheckDisablingDataSource;
import org.unitils.dbmaintainer.constraints.ConstraintsDisabler;
import org.unitils.dbmaintainer.handler.JDBCStatementHandler;
import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.handler.StatementHandlerException;
import org.unitils.dbmaintainer.maintainer.DBMaintainer;
import org.unitils.util.AnnotationUtils;
import org.unitils.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Module that provides basic support for database testing. This module provides the following services to unit tests
 * <ul>
 * <li>Connection pooling: A connection pooled TestDataSource is created, and supplied to methods annotated with
 * {@link TestDataSource}</li>
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
    private javax.sql.DataSource dataSource;

    /* The Configuration of Unitils */
    private Configuration configuration;

    /* Indicates if database constraints should be disabled */
    private boolean disableConstraints;

    /* Indicates if the DBMaintainer should be invoked to update the database */
    private boolean updateDatabaseSchemaEnabled;

    /*
    * Database connection holder: ensures that if the method getCurrentConnection is always used for getting
    * a connection to the database, at most one database connection exists per thread
    */
    private ThreadLocal<Connection> connectionHolder = new ThreadLocal<Connection>();

    /**
     * Initializes this module using the given <code>Configuration</code>
     * @param configuration
     */
    public void init(Configuration configuration) {
        this.configuration = configuration;

        disableConstraints = configuration.getBoolean(PROPKEY_DISABLECONSTRAINTS_ENABLED);
        updateDatabaseSchemaEnabled = configuration.getBoolean(PROPKEY_UPDATEDATABASESCHEMA_ENABLED);
    }

    /**
     * Checks whether the given test instance is a database test, i.e. is annotated with the {@link DatabaseTest} annotation.
     *
     * @param testObject the test instance, not null
     * @return true if the test class is a database test false otherwise
     */
    protected boolean isDatabaseTest(Object testObject) {

        return testObject.getClass().getAnnotation(DatabaseTest.class) != null;
    }

    /**
     * Inializes the database setup. I.e., creates a <code>TestDataSource</code> and updates the database schema if needed
     * using the {@link DBMaintainer}
     *
     * @param testObject the test instance, not null
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
    protected javax.sql.DataSource createDataSource() {
        DataSourceFactory dataSourceFactory = createDataSourceFactory();
        dataSourceFactory.init(configuration);
        javax.sql.DataSource dataSource = dataSourceFactory.createDataSource();

        // If contstraints disabling is active, a ConstraintsCheckDisablingDataSource is
        // returned that wrappes the TestDataSource object
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
    protected ConstraintsDisabler createConstraintsDisabler(javax.sql.DataSource dataSource) {

        String databaseDialect = configuration.getString(PROPKEY_DATABASE_DIALECT);
        String constraintsDisablerClassName = configuration.getString(PROPKEY_CONSTRAINTSDISABLER_START + "." + databaseDialect);

        StatementHandler statementHandler = new JDBCStatementHandler();
        statementHandler.init(configuration, dataSource);

        ConstraintsDisabler constraintsDisabler = ReflectionUtils.createInstanceOfType(constraintsDisablerClassName);
        constraintsDisabler.init(configuration, dataSource, statementHandler);
        return constraintsDisabler;
    }


    /**
     * @return The <code>TestDataSource</code>
     */
    public javax.sql.DataSource getDataSource() {
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
     * Assigns the <code>TestDataSource</code> to every field annotated with {@link TestDataSource} and calls all methods
     * annotated with {@link TestDataSource}
     *
     * @param testObject The test instance, not null
     */
    protected void injectDataSource(Object testObject) {
        List<Field> fields = AnnotationUtils.getFieldsAnnotatedWith(testObject.getClass(), TestDataSource.class);
        for (Field field : fields) {
            try {
                ReflectionUtils.setFieldValue(testObject, field, dataSource);

            } catch (UnitilsException e) {

                throw new UnitilsException("Unable to assign the TestDataSource to field annotated with @TestDataSource. " +
                        "Ensure that this field is of type javax.sql.TestDataSource", e);
            }
        }

        List<Method> methods = AnnotationUtils.getMethodsAnnotatedWith(testObject.getClass(), TestDataSource.class);
        for (Method method : methods) {
            try {
                ReflectionUtils.invokeMethod(testObject, method, dataSource);

            } catch (UnitilsException e) {

                throw new UnitilsException("Unable to invoke method annotated with @TestDataSource. Ensure that this method has " +
                        "following signature: void myMethod(javax.sql.TestDataSource dataSource)", e);
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
     * Creates a new instance of the {@link DBMaintainer}
     *
     * @return a new instance of the DBMaintainer
     */
    protected DBMaintainer createDbMaintainer(Configuration configuration) {
        return new DBMaintainer(configuration, dataSource);
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
     * @return The {@link TestListener} associated with this module
     */
    public TestListener createTestListener() {
        return new DatabaseTestListener();
    }

    /**
     * DatabaseTestListener that makes callbacks to methods of this module while running tests.
     */
    private class DatabaseTestListener extends TestListener {

        @Override
        public void beforeTestClass(Object testObject) {

            if (isDatabaseTest(testObject)) {
                initDatabase(testObject);
            }
        }

        // todo these calls must be done each time a new test object is created. For JUnit this is before every test
        // for TestNG this is before every test class.
        @Override
        public void beforeTestMethod(Object testObject, Method testMethod) {
		
		    if (isDatabaseTest(testObject)) {
                //call methods annotated with TestDataSource, if any
                injectDataSource(testObject);
            }
        }

    }
}
