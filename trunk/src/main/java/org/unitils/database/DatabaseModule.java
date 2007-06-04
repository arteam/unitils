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
package org.unitils.database;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.Module;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.unitils.core.dbsupport.SQLHandler;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.database.annotations.Transactional;
import org.unitils.database.config.DataSourceFactory;
import org.unitils.database.transaction.TransactionManager;
import org.unitils.database.transaction.TransactionManagerFactory;
import org.unitils.database.util.Flushable;
import org.unitils.database.util.TransactionMode;
import static org.unitils.database.util.TransactionMode.*;
import org.unitils.dbmaintainer.DBMaintainer;
import static org.unitils.util.AnnotationUtils.*;
import static org.unitils.util.ConfigUtils.getConfiguredInstance;
import static org.unitils.util.ModuleUtils.getAnnotationPropertyDefaults;
import static org.unitils.util.ModuleUtils.getEnumValueReplaceDefault;
import org.unitils.util.PropertyUtils;
import static org.unitils.util.ReflectionUtils.setFieldAndSetterValue;

import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * todo add javadoc explaining transaction behavior.
 * <p/>
 * Module that provides basic support for database testing such as the creation of a datasource that connectes to the
 * test database and the maintaince of the test database structure.
 * <p/>
 * A datasource will be created the first time one is requested. Which type of datasource will be created depends on
 * the configured {@link DataSourceFactory}. By default this will be a pooled datasource that gets its connection-url
 * and username and password from the unitils configuration.
 * <p/>
 * The created datasource can be injected into a field of the test by annotating the field with {@link TestDataSource}.
 * It can then be used to install it in your DAO or other class under test. See the javadoc of the annotation for more info
 * on how you can use it.
 * <p/>
 * If the DbMaintainer is enabled (by setting {@link #PROPKEY_UPDATEDATABASESCHEMA_ENABLED} to true), the test database
 * schema will automatically be updated if needed. This check will be performed once during your test-suite run, namely
 * when the data source is created. See {@link DBMaintainer} javadoc for more information on how this update is performed.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DatabaseModule implements Module {

    /* Property indicating if the database schema should be updated before performing the tests */
    public static final String PROPKEY_UPDATEDATABASESCHEMA_ENABLED = "updateDataBaseSchema.enabled";

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DatabaseModule.class);

    /* Map holding the default configuration of the database module annotations */
    private Map<Class<? extends Annotation>, Map<Method, String>> defaultAnnotationPropertyValues;

    /* The datasource instance */
    private DataSource dataSource;

    /* The configuration of Unitils */
    private Properties configuration;

    /* Indicates if the DBMaintainer should be invoked to update the database */
    private boolean updateDatabaseSchemaEnabled;

    /* The transaction manager */
    private TransactionManager transactionManager;


    /**
     * Initializes this module using the given <code>Configuration</code>
     *
     * @param configuration the config, not null
     */
    public void init(Properties configuration) {
        this.configuration = configuration;

        defaultAnnotationPropertyValues = getAnnotationPropertyDefaults(DatabaseModule.class, configuration, Transactional.class);
        updateDatabaseSchemaEnabled = PropertyUtils.getBoolean(PROPKEY_UPDATEDATABASESCHEMA_ENABLED, configuration);
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
     * Gets the transaction manager or creates one if it does not exist yet.
     *
     * @return The transaction manager, not null
     */
    public TransactionManager getTransactionManager() {
        if (transactionManager == null) {
            transactionManager = createTransactionManager();
        }
        return transactionManager;
    }


    /**
     * Flushes all pending updates to the database. This method is useful when the effect of updates needs to
     * be checked directly on the database.
     * <p/>
     * This will look for modules that implement {@link Flushable} and call flushDatabaseUpdates on these module.
     */
    public void flushDatabaseUpdates() {
        logger.info("Flushing database updates.");
        List<Flushable> flushables = Unitils.getInstance().getModulesRepository().getModulesOfType(Flushable.class);
        for (Flushable flushable : flushables) {
            flushable.flushDatabaseUpdates();
        }
    }


    /**
     * Determines whether the test database is outdated and, if that is the case, updates the database with the
     * latest changes. See {@link DBMaintainer} for more information.
     */
    public void updateDatabase() {
        updateDatabase(new SQLHandler(getDataSource()));
    }


    /**
     * todo make configurable using properties
     * <p/>
     * Determines whether the test database is outdated and, if that is the case, updates the database with the
     * latest changes. See {@link DBMaintainer} for more information.
     *
     * @param sqlHandler SQLHandler that needs to be used for the database updates
     */
    public void updateDatabase(SQLHandler sqlHandler) {
        try {
            logger.info("Checking if database has to be updated.");
            DBMaintainer dbMaintainer = new DBMaintainer(configuration, sqlHandler);
            dbMaintainer.updateDatabase();

        } catch (UnitilsException e) {
            throw new UnitilsException("Error while updating database", e);
        }
    }


    /**
     * Updates the database version to the current version, without issuing any other updates to the database.
     * This method can be used for example after you've manually brought the database to the latest version, but
     * the database version is not yet set to the current one. This method can also be useful for example for
     * reinitializing the database after having reorganized the scripts folder.
     */
    public void setDatabaseToCurrentVersion() {
        DBMaintainer dbMaintainer = new DBMaintainer(configuration, new SQLHandler(getDataSource()));
        dbMaintainer.setDatabaseToCurrentVersion();
    }


    /**
     * Assigns the <code>TestDataSource</code> to every field annotated with {@link TestDataSource} and calls all methods
     * annotated with {@link TestDataSource}
     *
     * @param testObject The test instance, not null
     */
    public void injectDataSource(Object testObject) {
        List<Field> fields = getFieldsAnnotatedWith(testObject.getClass(), TestDataSource.class);
        List<Method> methods = getMethodsAnnotatedWith(testObject.getClass(), TestDataSource.class);
        if (fields.isEmpty() && methods.isEmpty()) {
            // Nothing to do. Jump out to make sure that we don't try to instantiate the DataSource
            return;
        }
        setFieldAndSetterValue(testObject, fields, methods, getDataSource());
    }


    /**
     * Creates a datasource by using the factory that is defined by the dataSourceFactory.className property
     *
     * @return the datasource
     */
    protected DataSource createDataSource() {
        logger.debug("Creating data source.");
        // Get the factory for the data source and create it
        DataSourceFactory dataSourceFactory = getConfiguredInstance(DataSourceFactory.class, configuration);
        dataSourceFactory.init(configuration);
        DataSource dataSource = dataSourceFactory.createDataSource();
        // Make data source transactional
        dataSource = getTransactionManager().createTransactionalDataSource(dataSource);

        // Call the database maintainer if enabled
        if (updateDatabaseSchemaEnabled) {
            updateDatabase(new SQLHandler(dataSource));
        }
        return dataSource;
    }


    /**
     * @return An instance of the transactionManager, as configured in the Unitils configuration
     */
    protected TransactionManager createTransactionManager() {
        logger.debug("Creating transaction manager");
        // Get the factory for the transaction manager
        TransactionManagerFactory transactionManagerFactory = getConfiguredInstance(TransactionManagerFactory.class, configuration);
        transactionManagerFactory.init(configuration);
        return transactionManagerFactory.createTransactionManager();
    }


    /**
     * @param testObject The test object, not null
     * @return The {@link TransactionMode} for the given object
     */
    protected TransactionMode getTransactionMode(Object testObject) {
        TransactionMode transactionMode = getClassLevelAnnotationProperty(Transactional.class, "value", DEFAULT, testObject.getClass());
        transactionMode = getEnumValueReplaceDefault(Transactional.class, "value", transactionMode, defaultAnnotationPropertyValues);
        return transactionMode;
    }


    /**
     * Starts a transaction. If the Unitils DataSource was not loaded yet, we simply remember that a
     * transaction was started but don't actually start it. If the DataSource is loaded within this
     * test, the transaction will be started immediately after loading the DataSource.
     *
     * @param testObject The test object, not null
     */
    public void startTransaction(Object testObject) {
        TransactionMode transactionMode = getTransactionMode(testObject);
        if (transactionMode == DISABLED) {
            return;
        }
        getTransactionManager().startTransaction(testObject);
    }


    /**
     * Commits or rollbacks the current transaction, if transactions are enabled and a transactionManager is
     * active for the given testObject
     *
     * @param testObject The test object, not null
     */
    protected void commitOrRollbackTransaction(Object testObject) {
        TransactionMode transactionMode = getTransactionMode(testObject);
        if (transactionMode == DISABLED) {
            return;
        }
        TransactionManager transactionManager = getTransactionManager();
        if (transactionMode == COMMIT) {
            logger.debug("Commiting transaction");
            transactionManager.commit(testObject);
        } else if (getTransactionMode(testObject) == ROLLBACK) {
            logger.debug("Rolling back transaction");
            transactionManager.rollback(testObject);
        }
    }


    /**
     * @return The {@link TestListener} associated with this module
     */
    public TestListener createTestListener() {
        return new DatabaseTestListener();
    }


    /**
     * The {@link TestListener} for this module
     */
    protected class DatabaseTestListener extends TestListener {

        @Override
        public void beforeTestSetUp(Object testObject) {
            injectDataSource(testObject);
            startTransaction(testObject);
        }

        @Override
        public void afterTestTearDown(Object testObject) {
            commitOrRollbackTransaction(testObject);
        }
    }
}
