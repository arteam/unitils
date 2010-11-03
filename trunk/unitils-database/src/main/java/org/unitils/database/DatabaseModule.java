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

import org.dbmaintain.MainFactory;
import org.dbmaintain.database.Database;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;
import org.unitils.core.Module;
import org.unitils.core.TestExecutionListenerAdapter;
import org.unitils.core.util.ConfigUtils;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.database.annotations.Transactional;
import org.unitils.database.datasource.DataSourceFactory;
import org.unitils.database.transaction.DbMaintainManager;
import org.unitils.database.transaction.UnitilsDataSourceManager;
import org.unitils.database.transaction.UnitilsTransactionManager;
import org.unitils.database.util.DatabaseAnnotationHelper;
import org.unitils.database.util.TransactionMode;
import org.unitils.util.PropertyUtils;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import static org.unitils.database.util.TransactionMode.*;
import static org.unitils.util.AnnotationUtils.getFieldsAnnotatedWith;
import static org.unitils.util.AnnotationUtils.getMethodsAnnotatedWith;
import static org.unitils.util.ReflectionUtils.*;

/**
 * Module that provides support for database testing: Creation of a data source that connects to the
 * test database, support for executing tests in a transaction and automatic maintenance of the test database.
 * <p/>
 * A data source will be created the first time one is requested. Which type of data source will be created depends on
 * the configured {@link DataSourceFactory}. By default this will be a pooled data source that gets its connection-url,
 * user name and password from the unitils configuration.
 * <p/>
 * The created data source can be injected into a field of the test by annotating the field with {@link TestDataSource}.
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

    public static final String DEFAULT_TRANSACTION_MODE_PROPERTY = "database.default.transaction.mode";
    /**
     * Property indicating if the database schema should be updated before performing the tests
     */
    public static final String PROPERTY_UPDATEDATABASESCHEMA_ENABLED = "updateDataBaseSchema.enabled";
    /**
     * Property indicating whether the data source should be wrapped in a {@link org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy}
     */
    public static final String PROPERTY_WRAP_DATASOURCE_IN_TRANSACTIONAL_PROXY = "dataSource.wrapInTransactionalProxy";


    /* The data source and database manager */
    protected DbMaintainManager dbMaintainManager;
    /* The data source and database manager */
    protected UnitilsDataSourceManager unitilsDataSourceManager;
    /* The transaction manager */
    protected UnitilsTransactionManager unitilsTransactionManager;
    /* Utility class for handling annotations */
    protected DatabaseAnnotationHelper databaseAnnotationHelper;

    /* The registered database update listeners that will be called when db-maintain has updated the database */
    protected List<DatabaseUpdateListener> databaseUpdateListeners = new ArrayList<DatabaseUpdateListener>();


    /**
     * Initializes this module using the given <code>Configuration</code>
     *
     * @param configuration The config, not null
     */
    public void init(Properties configuration) {
        this.databaseAnnotationHelper = createDatabaseAnnotationHelper(configuration);
        this.dbMaintainManager = createDbMaintainMainManager(configuration);
        this.unitilsDataSourceManager = createUnitilsDatabaseManager(configuration, dbMaintainManager);
        this.unitilsTransactionManager = new UnitilsTransactionManager();
    }


    public void afterInit() {
    }


    /**
     * Returns the <code>DataSource</code> that provides connection to the unit test database. When invoked the first
     * time, the DBMaintainer is invoked to make sure the test database is up-to-date (if database updating is enabled).
     *
     * By default, the data source will be wrapped in a Spring TransactionAwareDataSourceProxy to make sure that the same
     * connection is always returned within the same transaction and will make sure that the tx synchronization is done correctly.
     * If needed, this can be disabled in the configuration by setting the
     * {@link #PROPERTY_WRAP_DATASOURCE_IN_TRANSACTIONAL_PROXY}.
     * property to false.
     *
     * @param testObject         The test instance, not null
     * @param testMethod         The test method, not null
     * @param databaseName       The name of the database to get a data source for, null for the default database
     * @param applicationContext The spring application context, null if not defined
     * @return The <code>DataSource</code>
     */
    public DataSource getDataSourceAndActivateTransactionIfNeeded(Object testObject, Method testMethod, String databaseName, ApplicationContext applicationContext) {
        DataSource dataSource = getDataSource(databaseName, applicationContext);
        startTransactionForDataSourceIfNoTransactionActive(testObject, testMethod, dataSource);
        return dataSource;
    }

    public DataSource getDataSource(String databaseName, ApplicationContext applicationContext) {
        return unitilsDataSourceManager.getDataSource(databaseName, applicationContext);
    }

    public Database getDatabase(String databaseName) {
        return dbMaintainManager.getDatabase(databaseName);
    }

    public MainFactory getDbMaintainMainFactory() {
        return dbMaintainManager.getDbMaintainMainFactory();
    }


    public boolean updateDatabaseIfNeeded(ApplicationContext applicationContext) {
        boolean databaseUpdated = dbMaintainManager.updateDatabaseIfNeeded(applicationContext);
        if (databaseUpdated) {
            notifyDatabaseUpdateListeners();
        }
        return databaseUpdated;
    }


    /**
     * Assigns the <code>TestDataSource</code> to every field annotated with {@link TestDataSource} and calls all methods
     * annotated with {@link TestDataSource}
     *
     * @param testObject         The test instance, not null
     * @param testMethod         The test method, not null
     * @param applicationContext The spring application context, null if not defined
     */
    protected void injectDataSources(Object testObject, Method testMethod, ApplicationContext applicationContext) {
        Set<Field> fields = getFieldsAnnotatedWith(testObject.getClass(), TestDataSource.class);
        Set<Method> methods = getMethodsAnnotatedWith(testObject.getClass(), TestDataSource.class);

        for (Field field : fields) {
            TestDataSource testDataSourceAnnotation = field.getAnnotation(TestDataSource.class);
            String databaseName = testDataSourceAnnotation.value();

            DataSource dataSource = getDataSourceAndActivateTransactionIfNeeded(testObject, testMethod, databaseName, applicationContext);
            setFieldValue(testObject, field, dataSource);
        }
        for (Method method : methods) {
            TestDataSource testDataSourceAnnotation = method.getAnnotation(TestDataSource.class);
            String databaseName = testDataSourceAnnotation.value();

            DataSource dataSource = getDataSourceAndActivateTransactionIfNeeded(testObject, testMethod, databaseName, applicationContext);
            setSetterValue(testObject, method, dataSource);
        }
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

    protected void startTransactionForTransactionManagersInApplicationContext(Object testObject, Method testMethod, ApplicationContext applicationContext) {
        Transactional transactional = databaseAnnotationHelper.getTransactionalAnnotation(testObject, testMethod);
        if (isTransactionsEnabled(transactional)) {
            List<String> transactionManagerBeanNames = databaseAnnotationHelper.getTransactionManagerBeanNames(transactional);
            unitilsTransactionManager.startTransactionOnTransactionManagersInApplicationContext(testObject, transactionManagerBeanNames, applicationContext);
        }
    }

    /**
     * Starts a new transaction for the given transaction. Ignored if a transaction is already
     * active for this data source.
     *
     * @param testObject The test object, not null
     * @param dataSource The data source, not null
     */
    public void startTransactionForDataSource(Object testObject, DataSource dataSource) {
        unitilsTransactionManager.startTransactionForDataSource(testObject, dataSource);
    }

    /**
     * Commits the current transaction.
     *
     * @param testObject The test object, not null
     */
    protected void commitTransaction(Object testObject) {
        unitilsTransactionManager.commit(testObject);
    }

    /**
     * Performs a rollback of the current transaction
     *
     * @param testObject The test object, not null
     */
    protected void rollbackTransaction(Object testObject) {
        unitilsTransactionManager.rollback(testObject);
    }


    /**
     * Starts a transaction. Ignored if a transaction is already active for this data source or when transaction
     * management is not enabled for this test method.
     *
     * @param testObject The test object, not null
     * @param testMethod The test method, not null
     * @param dataSource The data source, not null
     */
    protected void startTransactionForDataSourceIfNoTransactionActive(Object testObject, Method testMethod, DataSource dataSource) {
        Transactional transactional = databaseAnnotationHelper.getTransactionalAnnotation(testObject, testMethod);
        if (!isTransactionsEnabled(transactional)) {
            return;
        }
        unitilsTransactionManager.startTransactionForDataSource(testObject, dataSource);
    }

    /**
     * Commits or rollbacks the current transactions if transactions are enabled.
     *
     * @param testObject The test object, not null
     * @param testMethod The test method, not null
     */
    protected void endTransactions(Object testObject, Method testMethod) {
        Transactional transactional = databaseAnnotationHelper.getTransactionalAnnotation(testObject, testMethod);
        if (!isTransactionsEnabled(transactional)) {
            return;
        }
        TransactionMode transactionMode = databaseAnnotationHelper.getTransactionMode(transactional);
        if (transactionMode == COMMIT) {
            commitTransaction(testObject);
        } else if (transactionMode == ROLLBACK) {
            rollbackTransaction(testObject);
        }
    }

    /**
     * @param transactional The annotation
     * @return Whether transactions are enabled for the given test method and test object
     */
    protected boolean isTransactionsEnabled(Transactional transactional) {
        if (transactional == null) {
            return false;
        }
        TransactionMode transactionMode = databaseAnnotationHelper.getTransactionMode(transactional);
        return transactionMode != DISABLED;
    }


    protected DatabaseAnnotationHelper createDatabaseAnnotationHelper(Properties configuration) {
        String defaultValue = PropertyUtils.getString(DEFAULT_TRANSACTION_MODE_PROPERTY, configuration);
        TransactionMode defaultTransactionMode = getEnumValue(TransactionMode.class, defaultValue);
        return new DatabaseAnnotationHelper(defaultTransactionMode);
    }

    protected DbMaintainManager createDbMaintainMainManager(Properties configuration) {
        boolean updateDatabaseSchemaEnabled = PropertyUtils.getBoolean(PROPERTY_UPDATEDATABASESCHEMA_ENABLED, configuration);
        DataSourceFactory dataSourceFactory = ConfigUtils.getConfiguredInstanceOf(DataSourceFactory.class, configuration);
        dataSourceFactory.init(configuration);
        return new DbMaintainManager(configuration, updateDatabaseSchemaEnabled, dataSourceFactory);
    }

    protected UnitilsDataSourceManager createUnitilsDatabaseManager(Properties configuration, DbMaintainManager dbMaintainManager) {
        boolean wrapDataSourceInTransactionalProxy = PropertyUtils.getBoolean(PROPERTY_WRAP_DATASOURCE_IN_TRANSACTIONAL_PROXY, configuration);
        return new UnitilsDataSourceManager(wrapDataSourceInTransactionalProxy, dbMaintainManager);
    }

    /**
     * @return The {@link org.unitils.core.TestExecutionListenerAdapter} associated with this module
     */
    public TestExecutionListenerAdapter getTestListener() {
        return new DatabaseTestListener();
    }

    protected class DatabaseTestListener extends TestExecutionListenerAdapter {

        @Override
        public void beforeTestClass(Class<?> testClass, TestContext testContext) throws Exception {
            ApplicationContext applicationContext = getApplicationContext(testContext);
            updateDatabaseIfNeeded(applicationContext);
        }

        @Override
        public void beforeTestMethod(Object testObject, Method testMethod, TestContext testContext) throws Exception {
            ApplicationContext applicationContext = getApplicationContext(testContext);
            startTransactionForTransactionManagersInApplicationContext(testObject, testMethod, applicationContext);
            injectDataSources(testObject, testMethod, applicationContext);
        }

        @Override
        public void afterTestMethod(Object testObject, Method testMethod, Throwable testThrowable, TestContext testContext) throws Exception {
            endTransactions(testObject, testMethod);
        }
    }
}
