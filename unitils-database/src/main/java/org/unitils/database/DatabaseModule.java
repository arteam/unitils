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
import org.dbmaintain.database.Databases;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.test.context.TestContext;
import org.unitils.core.Module;
import org.unitils.core.TestExecutionListenerAdapter;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.database.annotations.Transactional;
import org.unitils.database.config.DataSourceFactory;
import org.unitils.database.transaction.UnitilsDataSourceManager;
import org.unitils.database.transaction.UnitilsTransactionManager;
import org.unitils.database.util.TransactionMode;
import org.unitils.util.PropertyUtils;

import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import static java.lang.Boolean.TRUE;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.unitils.database.util.TransactionMode.*;
import static org.unitils.util.AnnotationUtils.*;
import static org.unitils.util.ModuleUtils.getAnnotationPropertyDefaults;
import static org.unitils.util.ModuleUtils.getEnumValueReplaceDefault;
import static org.unitils.util.ReflectionUtils.setFieldValue;
import static org.unitils.util.ReflectionUtils.setSetterValue;

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
     * Property indicating whether the data sources should be wrapped in a {@link TransactionAwareDataSourceProxy}
     */
    public static final String PROPERTY_WRAP_DATASOURCE_IN_TRANSACTIONAL_PROXY = "dataSource.wrapInTransactionalProxy";

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DatabaseModule.class);

    /* Map holding the default configuration of the database module annotations */
    protected Map<Class<? extends Annotation>, Map<String, String>> defaultAnnotationPropertyValues;

    /* The configuration of Unitils */
    protected Properties configuration;
    /* Indicates if the DBMaintain should be invoked to update the database */
    protected boolean updateDatabaseSchemaEnabled;
    /* The main db-maintain factory instance */
    protected MainFactory mainFactory;
    /* Indicates whether the data sources should be wrapped in a TransactionAwareDataSourceProxy */
    protected boolean wrapDataSourceInTransactionalProxy;
    /* The data source manager */
    protected UnitilsDataSourceManager unitilsDataSourceManager;
    /* The transaction manager */
    protected UnitilsTransactionManager unitilsTransactionManager;

    /* The registered database update listeners that will be called when db-maintain has updated the database */
    protected List<DatabaseUpdateListener> databaseUpdateListeners = new ArrayList<DatabaseUpdateListener>();

    protected Map<DataSource, Boolean> updateDatabaseCalledForDataSource = new IdentityHashMap<DataSource, Boolean>();

    /**
     * Initializes this module using the given <code>Configuration</code>
     *
     * @param configuration The config, not null
     */
    public void init(Properties configuration) {
        this.configuration = configuration;
        this.defaultAnnotationPropertyValues = getAnnotationPropertyDefaults(DatabaseModule.class, configuration, Transactional.class);
        this.updateDatabaseSchemaEnabled = PropertyUtils.getBoolean(PROPERTY_UPDATEDATABASESCHEMA_ENABLED, configuration);
        this.wrapDataSourceInTransactionalProxy = PropertyUtils.getBoolean(PROPERTY_WRAP_DATASOURCE_IN_TRANSACTIONAL_PROXY, configuration);

        this.unitilsDataSourceManager = new UnitilsDataSourceManager(configuration);
        this.unitilsTransactionManager = new UnitilsTransactionManager();
    }

    public void afterInit() {
    }


    /**
     * Returns the <code>DataSource</code> that provides connection to the unit test database. When invoked the first
     * time, the DBMaintainer is invoked to make sure the test database is up-to-date (if database updating is enabled)
     * If the property {@link #PROPERTY_WRAP_DATASOURCE_IN_TRANSACTIONAL_PROXY} has been set to true, the <code>DataSource</code>
     * returned will make sure that, for the duration of a transaction, the same <code>java.sql.Connection</code> is returned,
     * and that invocations of the close() method of these connections are suppressed.
     *
     * @param databaseName       The name of the database to get a data source for, null for the default database
     * @param applicationContext The spring application context, null if not defined
     * @return The <code>DataSource</code>
     */
    public DataSource getDataSource(String databaseName, ApplicationContext applicationContext) {
        if (isBlank(databaseName)) {
            databaseName = null;
        }
        DataSource dataSource = unitilsDataSourceManager.getDataSource(databaseName, applicationContext);
        updateDatabaseIfNeeded(dataSource);
        return dataSource;
    }

    public Databases getDatabases() {
        return unitilsDataSourceManager.getDatabases();
    }


    protected void updateDatabaseIfNeeded(DataSource dataSource) {
        if (!updateDatabaseSchemaEnabled) {
            return;
        }
        Boolean updateDatabaseCalled = updateDatabaseCalledForDataSource.get(dataSource);
        if (updateDatabaseCalled != null && updateDatabaseCalled) {
            return;
        }
        updateDatabaseCalledForDataSource.put(dataSource, TRUE);

        boolean databaseUpdated = updateDatabase();
        if (databaseUpdated) {
            notifyDatabaseUpdateListeners();
        }
    }


    /**
     * Determines whether the test database is outdated and, if that is the case, updates the database with the
     * latest changes.
     *
     * @return True if an update occurred, false if the database was up to date
     * @see {@link org.dbmaintain.DbMaintainer}
     */
    public synchronized boolean updateDatabase() {
        logger.info("Checking if database has to be updated.");
        return getMainFactory().createDbMaintainer().updateDatabase(false);
    }


    /**
     * Assigns the <code>TestDataSource</code> to every field annotated with {@link TestDataSource} and calls all methods
     * annotated with {@link TestDataSource}
     *
     * @param testObject         The test instance, not null
     * @param applicationContext The spring application context, null if not defined
     */
    public void injectDataSource(Object testObject, ApplicationContext applicationContext) {
        Set<Field> fields = getFieldsAnnotatedWith(testObject.getClass(), TestDataSource.class);
        Set<Method> methods = getMethodsAnnotatedWith(testObject.getClass(), TestDataSource.class);

        for (Field field : fields) {
            TestDataSource testDataSourceAnnotation = field.getAnnotation(TestDataSource.class);
            String databaseName = testDataSourceAnnotation.value();

            DataSource dataSource = getDataSource(databaseName, applicationContext);
            setFieldValue(testObject, field, dataSource);
        }
        for (Method method : methods) {
            TestDataSource testDataSourceAnnotation = method.getAnnotation(TestDataSource.class);
            String databaseName = testDataSourceAnnotation.value();

            DataSource dataSource = getDataSource(databaseName, applicationContext);
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
     * Starts a new transaction.
     *
     * @param testObject         The test object, not null
     * @param applicationContext The spring application context context, null if not defined
     */
    public void startTransaction(Object testObject, ApplicationContext applicationContext) {
        DataSource dataSource = getDataSource(null, applicationContext);
        unitilsTransactionManager.startTransaction(testObject, applicationContext, dataSource);
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
     * Starts a transaction. If the Unitils DataSource was not loaded yet, we simply remember that a
     * transaction was started but don't actually start it. If the DataSource is loaded within this
     * test, the transaction will be started immediately after loading the DataSource.
     *
     * @param testObject         The test object, not null
     * @param testMethod         The test method, not null
     * @param applicationContext The spring application context, null if not defined
     */
    protected void startTransactionForTestMethod(Object testObject, Method testMethod, ApplicationContext applicationContext) {
        if (isTransactionsEnabled(testObject, testMethod)) {
            startTransaction(testObject, applicationContext);
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
     * @param testObject The test object, not null
     * @param testMethod The test method, not null
     * @return Whether transactions are enabled for the given test method and test object
     */
    protected boolean isTransactionsEnabled(Object testObject, Method testMethod) {
        TransactionMode transactionMode = getTransactionMode(testObject, testMethod);
        return transactionMode != DISABLED;
    }


    protected MainFactory getMainFactory() {
        if (mainFactory == null) {
            mainFactory = new MainFactory(configuration, getDatabases());
        }
        return mainFactory;
    }


    /**
     * @return The {@link org.unitils.core.TestExecutionListenerAdapter} associated with this module
     */
    public TestExecutionListenerAdapter getTestListener() {
        return new DatabaseTestListener();
    }


    protected class DatabaseTestListener extends TestExecutionListenerAdapter {

        @Override
        public void prepareTestInstance(Object testObject, TestContext testContext) throws Exception {
            ApplicationContext applicationContext = getApplicationContext(testContext);
            injectDataSource(testObject, applicationContext);
        }

        @Override
        public void beforeTestMethod(Object testObject, Method testMethod, TestContext testContext) throws Exception {
            ApplicationContext applicationContext = getApplicationContext(testContext);
            startTransactionForTestMethod(testObject, testMethod, applicationContext);
        }

        @Override
        public void afterTestMethod(Object testObject, Method testMethod, Throwable testThrowable, TestContext testContext) throws Exception {
            endTransactionForTestMethod(testObject, testMethod);
        }
    }
}
