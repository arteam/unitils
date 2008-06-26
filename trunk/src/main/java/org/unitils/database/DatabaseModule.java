/*
 * Copyright 2006-2007,  Unitils.org
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
package org.unitils.database;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.Module;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;
import org.unitils.core.dbsupport.DefaultSQLHandler;
import org.unitils.core.dbsupport.SQLHandler;
import org.unitils.core.util.ConfigUtils;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.database.annotations.Transactional;
import org.unitils.database.config.DataSourceFactory;
import org.unitils.database.transaction.UnitilsTransactionManager;
import org.unitils.database.transaction.impl.UnitilsTransactionManagementConfiguration;
import org.unitils.database.util.Flushable;
import org.unitils.database.util.TransactionMode;

import static org.unitils.core.util.ConfigUtils.getInstanceOf;
import static org.unitils.database.util.TransactionMode.*;
import org.unitils.database.util.spring.DatabaseSpringSupport;
import org.unitils.dbmaintainer.DBMaintainer;
import org.unitils.dbmaintainer.clean.DBCleaner;
import org.unitils.dbmaintainer.clean.DBClearer;
import org.unitils.dbmaintainer.structure.ConstraintsDisabler;
import org.unitils.dbmaintainer.structure.DataSetStructureGenerator;
import org.unitils.dbmaintainer.structure.SequenceUpdater;
import org.unitils.dbmaintainer.util.DatabaseModuleConfigUtils;
import org.unitils.dbmaintainer.util.DatabaseAccessing;
import static org.unitils.util.AnnotationUtils.*;
import static org.unitils.util.ModuleUtils.getAnnotationPropertyDefaults;
import static org.unitils.util.ModuleUtils.getEnumValueReplaceDefault;
import org.unitils.util.PropertyUtils;
import static org.unitils.util.ReflectionUtils.createInstanceOfType;
import static org.unitils.util.ReflectionUtils.setFieldAndSetterValue;

import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Module that provides support for database testing: Creation of a datasource that connects to the
 * test database, support for executing tests in a transaction and automatic maintenance of the test database.
 * <p/>
 * A datasource will be created the first time one is requested. Which type of datasource will be created depends on
 * the configured {@link DataSourceFactory}. By default this will be a pooled datasource that gets its connection-url,
 * username and password from the unitils configuration.
 * <p/>
 * The created datasource can be injected into a field of the test by annotating the field with {@link TestDataSource}.
 * It can then be used to install it in your DAO or other class under test.
 * <p/>
 * If the DBMaintainer is enabled (by setting {@link #PROPERTY_UPDATEDATABASESCHEMA_ENABLED} to true), the test database
 * schema will automatically be updated if needed. This check will be performed once during your test-suite run, namely
 * when the data source is created.
 * <p/>
 * If the test class or method is annotated with {@link Transactional} with transaction mode {@link TransactionMode#COMMIT} or
 * {@link TransactionMode#ROLLBACK}, or if the property 'DatabaseModule.Transactional.value.default' was set to 'commit' or
 * 'rollback', every test is executed in a transaction.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 * @see TestDataSource
 * @see DBMaintainer
 * @see Transactional
 */
public class DatabaseModule implements Module {

    /**
     * Property indicating if the database schema should be updated before performing the tests
     */
    public static final String PROPERTY_UPDATEDATABASESCHEMA_ENABLED = "updateDataBaseSchema.enabled";

    /**
     * Property indicating whether the datasource injected onto test fields annotated with @TestDataSource or retrieved using
     * {@link #getTransactionalDataSource} must be wrapped in a transactional proxy
     */
    public static final String PROPERTY_WRAP_DATASOURCE_IN_TRANSACTIONAL_PROXY = "dataSource.wrapInTransactionalProxy";

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DatabaseModule.class);

    /**
     * Map holding the default configuration of the database module annotations
     */
    protected Map<Class<? extends Annotation>, Map<String, String>> defaultAnnotationPropertyValues;

    /**
     * The datasources with the name as key
     */
    protected DataSource dataSource;

    /**
     * The configuration of Unitils
     */
    protected Properties configuration;

    /**
     * Indicates if the DBMaintainer should be invoked to update the database
     */
    protected boolean updateDatabaseSchemaEnabled;

    /**
     * Indicates whether the datasource injected onto test fields annotated with @TestDataSource or retrieved using
     * {@link #getTransactionalDataSource} must be wrapped in a transactional proxy
     */
    protected boolean wrapDataSourceInTransactionalProxy;

    /**
     * The transaction manager
     */
    protected UnitilsTransactionManager transactionManager;

    /**
     * Set of possible providers of a spring <code>PlatformTransactionManager</code>
     */
    protected Set<UnitilsTransactionManagementConfiguration> transactionManagementConfigurations = new HashSet<UnitilsTransactionManagementConfiguration>();

    /**
     * Provides access to <code>PlatformTransactionManager</code>s configured in a spring <code>ApplicationContext</code>,
     * If the spring module is not enabled, this object is null
     */
    protected DatabaseSpringSupport databaseSpringSupport;


    /**
     * Initializes this module using the given <code>Configuration</code>
     *
     * @param configuration The config, not null
     */
    @SuppressWarnings("unchecked")
    public void init(Properties configuration) {
        this.configuration = configuration;

        defaultAnnotationPropertyValues = getAnnotationPropertyDefaults(DatabaseModule.class, configuration, Transactional.class);
        updateDatabaseSchemaEnabled = PropertyUtils.getBoolean(PROPERTY_UPDATEDATABASESCHEMA_ENABLED, configuration);
        wrapDataSourceInTransactionalProxy = PropertyUtils.getBoolean(PROPERTY_WRAP_DATASOURCE_IN_TRANSACTIONAL_PROXY, configuration);
    }


    /**
     * Initializes the spring support object
     */
    public void afterInit() {
        initDatabaseSpringSupport();
    }


    /**
     * Returns the <code>DataSource</code> that provides connection to the unit test database. When invoked the first
     * time, the DBMaintainer is invoked to make sure the test database is up-to-date (if database updating is enabled)
     *
     * @return The <code>DataSource</code>
     */
    public DataSource getDataSource() {
        if (dataSource == null) {
            dataSource = createDataSource();
        }
        return dataSource;
    }


    /**
     * Returns the <code>DataSource</code> that provides connection to the unit test database. When invoked the first
     * time, the DBMaintainer is invoked to make sure the test database is up-to-date (if database updating is enabled)
     * If the property {@link #PROPERTY_WRAP_DATASOURCE_IN_TRANSACTIONAL_PROXY} has been set to true, the <code>DataSource</code>
     * returned will make sure that, for the duration of a transaction, the same <code>java.sql.Connection</code> is returned,
     * and that invocations of the close() method of these connections are suppressed.
     *
     * @param testObject The test instance, not null
     * @return The <code>DataSource</code>
     */
    public DataSource getTransactionalDataSource(Object testObject) {
        if (wrapDataSourceInTransactionalProxy) {
            return getTransactionManager().getTransactionalDataSource(getDataSource());
        }
        return getDataSource();
    }


    /**
     * Returns the transaction manager or creates one if it does not exist yet.
     *
     * @return The transaction manager, not null
     */
    public UnitilsTransactionManager getTransactionManager() {
        if (transactionManager == null) {
            transactionManager = getInstanceOf(UnitilsTransactionManager.class, configuration);
            transactionManager.init(transactionManagementConfigurations, databaseSpringSupport);
        }
        return transactionManager;
    }


    /**
     * Flushes all pending updates to the database. This method is useful when the effect of updates needs to
     * be checked directly on the database.
     * <p/>
     * Will look for modules that implement {@link Flushable} and call {@link Flushable#flushDatabaseUpdates(Object)}
     * on these modules.
     *
     * @param testObject The test object, not null
     */
    public void flushDatabaseUpdates(Object testObject) {
        List<Flushable> flushables = Unitils.getInstance().getModulesRepository().getModulesOfType(Flushable.class);
        for (Flushable flushable : flushables) {
            flushable.flushDatabaseUpdates(testObject);
        }
    }


    /**
     * Determines whether the test database is outdated and, if this is the case, updates the database with the
     * latest changes. See {@link DBMaintainer} for more information.
     */
    public void updateDatabase() {
        updateDatabase(getDefaultSqlHandler());
    }


    /**
     * Determines whether the test database is outdated and, if that is the case, updates the database with the
     * latest changes.
     *
     * @param sqlHandler SQLHandler that needs to be used for the database updates
     * @see {@link DBMaintainer}
     */
    public void updateDatabase(SQLHandler sqlHandler) {
        logger.info("Checking if database has to be updated.");
        DBMaintainer dbMaintainer = new DBMaintainer(configuration, sqlHandler);
        dbMaintainer.updateDatabase();
    }


    /**
     * Updates the database version to the current version, without issuing any other update to the database.
     * This method can be used for example after you've manually brought the database to the latest version, but
     * the database version is not yet set to the current one. This method can also be useful for example for
     * reinitializing the database after having reorganized the scripts folder.
     */
    public void resetDatabaseState() {
        resetDatabaseState(getDefaultSqlHandler());
    }


    /**
     * Updates the database version to the current version, without issuing any other updates to the database.
     * This method can be used for example after you've manually brought the database to the latest version, but
     * the database version is not yet set to the current one. This method can also be useful for example for
     * reinitializing the database after having reorganized the scripts folder.
     *
     * @param sqlHandler The {@link DefaultSQLHandler} to which all commands are issued
     */
    public void resetDatabaseState(SQLHandler sqlHandler) {
        DBMaintainer dbMaintainer = new DBMaintainer(configuration, sqlHandler);
        dbMaintainer.resetDatabaseState();
    }


    /**
     * Assigns the <code>TestDataSource</code> to every field annotated with {@link TestDataSource} and calls all methods
     * annotated with {@link TestDataSource}
     *
     * @param testObject The test instance, not null
     */
    public void injectDataSource(Object testObject) {
        Set<Field> fields = getFieldsAnnotatedWith(testObject.getClass(), TestDataSource.class);
        Set<Method> methods = getMethodsAnnotatedWith(testObject.getClass(), TestDataSource.class);
        if (fields.isEmpty() && methods.isEmpty()) {
            // Nothing to do. Jump out to make sure that we don't try to instantiate the DataSource
            return;
        }
        setFieldAndSetterValue(testObject, fields, methods, getTransactionalDataSource(testObject));
    }


    /**
     * Creates a datasource by using the factory that is defined by the dataSourceFactory.className property
     *
     * @return the datasource
     */
    protected DataSource createDataSource() {
        // Get the factory for the data source and create it
        DataSourceFactory dataSourceFactory = ConfigUtils.getConfiguredInstanceOf(DataSourceFactory.class, configuration);
        dataSourceFactory.init(configuration);
        DataSource dataSource = dataSourceFactory.createDataSource();

        // Call the database maintainer if enabled
        if (updateDatabaseSchemaEnabled) {
            updateDatabase(new DefaultSQLHandler(dataSource));
        }
        return dataSource;
    }


    /**
     * @param testObject The test object, not null
     * @param testMethod The test method, not null
     * @return The {@link TransactionMode} for the given object
     */
    protected TransactionMode getTransactionMode(Object testObject, Method testMethod) {
        TransactionMode transactionMode = getMethodOrClassLevelAnnotationProperty(Transactional.class, "value", DEFAULT, testMethod, testObject.getClass());
        transactionMode = getEnumValueReplaceDefault(Transactional.class, "value", transactionMode, defaultAnnotationPropertyValues);
        return transactionMode;
    }


    /**
     * Starts a transaction. If the Unitils DataSource was not loaded yet, we simply remember that a
     * transaction was started but don't actually start it. If the DataSource is loaded within this
     * test, the transaction will be started immediately after loading the DataSource.
     *
     * @param testObject The test object, not null
     * @param testMethod The test method, not null
     */
    protected void startTransactionForTestMethod(Object testObject, Method testMethod) {
        if (isTransactionsEnabled(testObject, testMethod)) {
            startTransaction(testObject);
        }
    }


    /**
     * Commits or rollbacks the current transaction, if transactions are enabled and a transactionManager is
     * active for the given testObject
     *
     * @param testObject The test object, not null
     * @param testMethod The test method, not null
     */
    protected void endTransactionForTestMethod(Object testObject, Method testMethod) {
        if (isTransactionsEnabled(testObject, testMethod)) {
            if (getTransactionMode(testObject, testMethod) == COMMIT) {
                commitTransaction(testObject);
            } else if (getTransactionMode(testObject, testMethod) == ROLLBACK) {
                rollbackTransaction(testObject);
            }
        }
    }


    /**
     * Starts a new transaction on the transaction manager configured in unitils
     *
     * @param testObject The test object, not null
     */
    public void startTransaction(Object testObject) {
        logger.info("Starting transaction.");
        getTransactionManager().startTransaction(testObject);
    }


    /**
     * Commits the current transaction.
     *
     * @param testObject The test object, not null
     */
    public void commitTransaction(Object testObject) {
        flushDatabaseUpdates(testObject);
        getTransactionManager().commit(testObject);
        logger.info("Committed transaction.");
    }


    /**
     * Performs a rollback of the current transaction
     *
     * @param testObject The test object, not null
     */
    public void rollbackTransaction(Object testObject) {
        flushDatabaseUpdates(testObject);
        getTransactionManager().rollback(testObject);
        logger.info("Rolled back transaction.");
    }


    /**
     * @param testObject The test object, not null
     * @param testMethod The test method, not null
     * @return Whether transactions are enabled for the given test method and test object
     */
    public boolean isTransactionsEnabled(Object testObject, Method testMethod) {
        TransactionMode transactionMode = getTransactionMode(testObject, testMethod);
        return transactionMode != DISABLED;
    }


    /**
     * Clears all configured schema's. I.e. drops all tables, views and other database objects.
     */
    public void clearSchemas() {
        getConfiguredDatabaseTaskInstance(DBClearer.class).clearSchemas();
    }


    /**
     * Cleans all configured schema's. I.e. removes all data from its database tables.
     */
    public void cleanSchemas() {
        getConfiguredDatabaseTaskInstance(DBCleaner.class).cleanSchemas();
    }


    /**
     * Disables all foreigh key and not-null constraints on the configured schema's.
     */
    public void disableConstraints() {
        getConfiguredDatabaseTaskInstance(ConstraintsDisabler.class).removeConstraints();
    }


    /**
     * Updates all sequences that have a value below a certain configurable treshold to become equal
     * to this treshold
     */
    public void updateSequences() {
        getConfiguredDatabaseTaskInstance(SequenceUpdater.class).updateSequences();
    }


    /**
     * Generates a definition file that defines the structure of dataset's, i.e. a XSD of DTD that
     * describes the structure of the database.
     */
    public void generateDatasetDefinition() {
        getConfiguredDatabaseTaskInstance(DataSetStructureGenerator.class).generateDataSetStructure();
    }


    /**
     * @return A configured instance of {@link DatabaseAccessing} of the given type
     *
     * @param databaseTaskType The type of database task, not null
     */
    protected <T extends DatabaseAccessing> T getConfiguredDatabaseTaskInstance(Class<T> databaseTaskType) {
        return DatabaseModuleConfigUtils.getConfiguredDatabaseTaskInstance(databaseTaskType, configuration, getDefaultSqlHandler());
    }


    /**
     * @return The default SQLHandler, which simply executes the sql statements on the unitils-configured
     *         test database
     */
    protected SQLHandler getDefaultSqlHandler() {
        return new DefaultSQLHandler(getDataSource());
    }


    // todo javadoc
    public void registerTransactionManagementConfiguration(UnitilsTransactionManagementConfiguration transactionManagementConfiguration) {
        transactionManagementConfigurations.add(transactionManagementConfiguration);
    }


    /**
     * Creates an instance of {@link org.unitils.database.util.spring.DatabaseSpringSupportImpl}, that
     * implements the dependency to the {@link org.unitils.spring.SpringModule}. If the
     * {@link org.unitils.spring.SpringModule} is not active, the instance is not loaded and the spring
     * support is not enabled
     */
    protected void initDatabaseSpringSupport() {
        if (!isSpringModuleEnabled()) {
            return;
        }
        databaseSpringSupport = createInstanceOfType("org.unitils.database.util.spring.DatabaseSpringSupportImpl", false);
    }


    /**
     * Verifies whether the SpringModule is enabled. If not, this means that either the property unitils.modules doesn't
     * include spring, or unitils.module.spring.enabled = false, or that the module could not be loaded because spring is not
     * in the classpath.
     *
     * @return true if the SpringModule is enabled, false otherwise
     */
    protected boolean isSpringModuleEnabled() {
        return Unitils.getInstance().getModulesRepository().isModuleEnabled("org.unitils.spring.SpringModule");
    }


    /**
     * @return The {@link TestListener} associated with this module
     */
    public TestListener getTestListener() {
        return new DatabaseTestListener();
    }


    /**
     * The {@link TestListener} for this module
     */
    protected class DatabaseTestListener extends TestListener {

        @Override
        public void beforeTestSetUp(Object testObject, Method testMethod) {
            injectDataSource(testObject);
            startTransactionForTestMethod(testObject, testMethod);
        }

        @Override
        public void afterTestTearDown(Object testObject, Method testMethod) {
            endTransactionForTestMethod(testObject, testMethod);
        }
    }
}
