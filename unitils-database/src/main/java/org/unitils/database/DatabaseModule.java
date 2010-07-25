/*
 * Copyright Unitils.org
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
import org.dbmaintain.MainFactory;
import org.dbmaintain.database.DatabaseInfo;
import org.dbmaintain.database.DatabaseInfoFactory;
import org.dbmaintain.database.Databases;
import org.dbmaintain.database.DatabasesFactory;
import org.dbmaintain.database.impl.DefaultSQLHandler;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.unitils.core.Module;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.database.annotations.Transactional;
import org.unitils.database.config.DataSourceFactory;
import org.unitils.database.transaction.UnitilsTransactionManager;
import org.unitils.database.transaction.impl.UnitilsTransactionManagementConfiguration;
import org.unitils.database.util.Flushable;
import org.unitils.database.util.TransactionMode;
import org.unitils.util.PropertyUtils;

import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import static org.unitils.core.util.ConfigUtils.getInstanceOf;
import static org.unitils.database.util.TransactionMode.*;
import static org.unitils.util.AnnotationUtils.*;
import static org.unitils.util.ModuleUtils.getAnnotationPropertyDefaults;
import static org.unitils.util.ModuleUtils.getEnumValueReplaceDefault;
import static org.unitils.util.ReflectionUtils.setFieldAndSetterValue;

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
 * @see org.dbmaintain.DbMaintainer
 * @see Transactional
 */
public class DatabaseModule implements Module {

    /**
     * Property indicating if the database schema should be updated before performing the tests
     */
    public static final String PROPERTY_UPDATEDATABASESCHEMA_ENABLED = "updateDataBaseSchema.enabled";

    /**
     * Property indicating whether the datasource injected onto test fields annotated with @TestDataSource or retrieved using
     * {@link #getTransactionalDataSourceAndActivateTransactionIfNeeded(Object)} must be wrapped in a transactional proxy
     */
    public static final String PROPERTY_WRAP_DATASOURCE_IN_TRANSACTIONAL_PROXY = "dataSource.wrapInTransactionalProxy";

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DatabaseModule.class);

    /* Map holding the default configuration of the database module annotations */
    protected Map<Class<? extends Annotation>, Map<String, String>> defaultAnnotationPropertyValues;

    /* The db support instances */
    protected Databases databases;
    /* The configuration of Unitils */
    protected Properties configuration;
    /* Indicates if the DBMaintain should be invoked to update the database */
    protected boolean updateDatabaseSchemaEnabled;
    /* The main db-maintain factory instance */
    protected MainFactory mainFactory;
    /* Indicates whether the datasource injected onto test fields annotated with @TestDataSource or retrieved using
     * {@link #getTransactionalDataSourceAndActivateTransactionIfNeeded} must be wrapped in a transactional proxy */
    protected boolean wrapDataSourceInTransactionalProxy;
    /* The transaction manager */
    protected UnitilsTransactionManager transactionManager;
    /* Set of possible providers of a spring <code>PlatformTransactionManager</code> */
    protected Set<UnitilsTransactionManagementConfiguration> transactionManagementConfigurations = new HashSet<UnitilsTransactionManagementConfiguration>();

    /* The registered database update listeners that will be called when db-maintain has updated the database */
    protected List<DatabaseUpdateListener> databaseUpdateListeners = new ArrayList<DatabaseUpdateListener>();


    /**
     * Initializes this module using the given <code>Configuration</code>
     *
     * @param configuration The config, not null
     */
    @SuppressWarnings("unchecked")
    public void init(Properties configuration) {
        this.configuration = configuration;
        this.defaultAnnotationPropertyValues = getAnnotationPropertyDefaults(DatabaseModule.class, configuration, Transactional.class);
        this.updateDatabaseSchemaEnabled = PropertyUtils.getBoolean(PROPERTY_UPDATEDATABASESCHEMA_ENABLED, configuration);
        this.wrapDataSourceInTransactionalProxy = PropertyUtils.getBoolean(PROPERTY_WRAP_DATASOURCE_IN_TRANSACTIONAL_PROXY, configuration);

        PlatformTransactionManager.class.getName();
    }


    /**
     * Initializes the spring support object
     */
    public void afterInit() {
        // Make sure that a spring DataSourceTransactionManager is used for transaction management, if
        // no other transaction management configuration takes preference
        registerTransactionManagementConfiguration(new UnitilsTransactionManagementConfiguration() {

            public boolean isApplicableFor(Object testObject) {
                return true;
            }

            public PlatformTransactionManager getSpringPlatformTransactionManager(Object testObject) {
                return new DataSourceTransactionManager(getDataSourceAndActivateTransactionIfNeeded(testObject));
            }

            public boolean isTransactionalResourceAvailable(Object testObject) {
                return isDataSourceLoaded();
            }

            public Integer getPreference() {
                return 1;
            }

        });
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
    public DataSource getTransactionalDataSourceAndActivateTransactionIfNeeded(Object testObject) {
        if (wrapDataSourceInTransactionalProxy) {
            return getTransactionManager().getTransactionalDataSource(getDataSourceAndActivateTransactionIfNeeded(testObject));
        }
        return getDataSourceAndActivateTransactionIfNeeded(testObject);
    }


    /**
     * Returns the <code>DataSource</code> that provides connection to the unit test database. When invoked the first
     * time, the DBMaintainer is invoked to make sure the test database is up-to-date (if database updating is enabled)
     *
     * @param testObject The test instance, not null
     * @return The <code>DataSource</code>
     */
    public DataSource getDataSourceAndActivateTransactionIfNeeded(Object testObject) {
        boolean first = !isDataSourceLoaded();
        DataSource dataSource = getDataSource();
        if (first) {
            activateTransactionIfNeeded(testObject);
        }
        return dataSource;
    }


    public synchronized Databases getDatabases() {
        if (databases == null) {
            DatabaseInfoFactory databaseInfoFactory = new DatabaseInfoFactory(configuration);
            List<DatabaseInfo> databaseInfos = databaseInfoFactory.getDatabaseInfos();
            DatabasesFactory databasesFactory = new DatabasesFactory(configuration, new DefaultSQLHandler());
            databases = databasesFactory.createDatabases(databaseInfos);

            updateDatabaseIfNeeded();
        }
        return databases;
    }

    private void updateDatabaseIfNeeded() {
        // Call the database maintainer if enabled
        if (updateDatabaseSchemaEnabled) {
            boolean databaseUpdated = updateDatabase();
            if (databaseUpdated) {
                notifyDatabaseUpdateListeners();
            }
        }
    }


    public DataSource getDataSource() {
        return getDatabases().getDefaultDatabase().getDataSource();
    }

    public boolean isDataSourceLoaded() {
        return databases != null;
    }


    public void activateTransactionIfNeeded(Object testObject) {
        if (transactionManager != null) {
            transactionManager.activateTransactionIfNeeded(testObject);
        }
    }


    /**
     * Returns the transaction manager or creates one if it does not exist yet.
     *
     * @return The transaction manager, not null
     */
    public UnitilsTransactionManager getTransactionManager() {
        if (transactionManager == null) {
            transactionManager = getInstanceOf(UnitilsTransactionManager.class, configuration);
            transactionManager.init(transactionManagementConfigurations);
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


    public synchronized MainFactory getMainFactory() {
        if (mainFactory == null) {
            mainFactory = new MainFactory(configuration, getDatabases());
        }
        return mainFactory;
    }

    /**
     * Determines whether the test database is outdated and, if that is the case, updates the database with the
     * latest changes.
     *
     * @return True if an update occurred, false if the database was up to date
     * @see {@link org.dbmaintain.DbMaintainer}
     */
    public boolean updateDatabase() {
        logger.info("Checking if database has to be updated.");
        return getMainFactory().createDbMaintainer().updateDatabase(false);
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
        setFieldAndSetterValue(testObject, fields, methods, getTransactionalDataSourceAndActivateTransactionIfNeeded(testObject));
    }


    public void registerDatabaseUpdateListener(DatabaseUpdateListener databaseUpdateListener) {
        databaseUpdateListeners.add(databaseUpdateListener);
    }

    public void unregisterDatabaseUpdateListener(DatabaseUpdateListener databaseUpdateListener) {
        databaseUpdateListeners.remove(databaseUpdateListener);
    }

    private void notifyDatabaseUpdateListeners() {
        for (DatabaseUpdateListener databaseUpdateListener : databaseUpdateListeners) {
            databaseUpdateListener.databaseWasUpdated();
        }
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
    }


    /**
     * Performs a rollback of the current transaction
     *
     * @param testObject The test object, not null
     */
    public void rollbackTransaction(Object testObject) {
        flushDatabaseUpdates(testObject);
        getTransactionManager().rollback(testObject);
    }


    /**
     * @param testObject The test object, not null
     * @param testMethod The test method, not null
     * @return Whether transactions are enabled for the given test method and test object
     */
    protected boolean isTransactionsEnabled(Object testObject, Method testMethod) {
        TransactionMode transactionMode = getTransactionMode(testObject, testMethod);
        return transactionMode != DISABLED;
    }


    public void registerTransactionManagementConfiguration(UnitilsTransactionManagementConfiguration transactionManagementConfiguration) {
        transactionManagementConfigurations.add(transactionManagementConfiguration);
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
