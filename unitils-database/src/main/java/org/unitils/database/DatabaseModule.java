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
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;
import org.unitils.core.Module;
import org.unitils.core.TestExecutionListenerAdapter;
import org.unitils.core.UnitilsException;
import org.unitils.core.util.ConfigUtils;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.database.annotations.Transactional;
import org.unitils.database.datasource.DataSourceFactory;
import org.unitils.database.manager.DbMaintainManager;
import org.unitils.database.manager.UnitilsDataSourceManager;
import org.unitils.database.transaction.UnitilsTransactionManager;
import org.unitils.database.util.DatabaseAnnotationHelper;
import org.unitils.database.util.TransactionMode;
import org.unitils.util.PropertyUtils;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import static org.unitils.database.util.TransactionMode.*;
import static org.unitils.util.AnnotationUtils.getFieldsAnnotatedWith;
import static org.unitils.util.AnnotationUtils.getMethodsAnnotatedWith;
import static org.unitils.util.ReflectionUtils.*;

/**
 * Module that provides support for database testing: creation of a data source that connects to the
 * test database, support for executing tests in a transaction and automatic maintenance of the test database.
 * <br/>
 * The module listens to the test execution and can be configured by adding annotations to your test class.
 * It should not be used directly. See {@link DatabaseUnitils} and {@link DbMaintainUnitils} if you want to
 * programmatically start transactions, get data sources etc.
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


    public UnitilsDataSourceManager getUnitilsDataSourceManager() {
        return unitilsDataSourceManager;
    }

    public MainFactory getDbMaintainMainFactory() {
        return dbMaintainManager.getDbMaintainMainFactory();
    }

    public DbMaintainManager getDbMaintainManager() {
        return dbMaintainManager;
    }

    public UnitilsTransactionManager getUnitilsTransactionManager() {
        return unitilsTransactionManager;
    }


    /**
     * Assigns the <code>TestDataSource</code> to every field annotated with {@link TestDataSource} and calls all methods
     * annotated with {@link TestDataSource}
     *
     * @param testObject         The test instance, not null
     * @param applicationContext The spring application context, null if not defined
     * @return The data sources that were injected, not null
     */
    protected Set<DataSource> injectDataSources(Object testObject, ApplicationContext applicationContext) {
        Set<DataSource> injectedDataSources = new HashSet<DataSource>();

        Set<Field> fields = getFieldsAnnotatedWith(testObject.getClass(), TestDataSource.class);
        Set<Method> methods = getMethodsAnnotatedWith(testObject.getClass(), TestDataSource.class);

        for (Field field : fields) {
            TestDataSource testDataSourceAnnotation = field.getAnnotation(TestDataSource.class);
            String databaseName = testDataSourceAnnotation.value();

            DataSource dataSource = unitilsDataSourceManager.getDataSource(databaseName, applicationContext);
            setFieldValue(testObject, field, dataSource);
            injectedDataSources.add(dataSource);
        }
        for (Method method : methods) {
            TestDataSource testDataSourceAnnotation = method.getAnnotation(TestDataSource.class);
            String databaseName = testDataSourceAnnotation.value();

            DataSource dataSource = unitilsDataSourceManager.getDataSource(databaseName, applicationContext);
            setSetterValue(testObject, method, dataSource);
            injectedDataSources.add(dataSource);
        }
        return injectedDataSources;
    }

    protected void startTransactionForInjectedDataSources(Object testObject, Method testMethod, Set<DataSource> injectedDataSources) {
        Transactional transactional = databaseAnnotationHelper.getTransactionalAnnotation(testObject, testMethod);
        if (!isTransactionsEnabled(transactional) || injectedDataSources.isEmpty()) {
            // nothing to do
            return;
        }
        if (injectedDataSources.size() > 1) {
            throw new UnitilsException("Starting a transaction for multiple data sources is not supported. You cannot use the @Transactional annotation when there is more than 1 @TestDataSource.\n" +
                    "A transaction can only be started for 1 data source at the same time. If you want a transaction spanning multiple data sources, you will need to set up an XA-transaction manager in a spring context. See the tutorial for more info.");
        }
        DataSource injectedDataSource = injectedDataSources.iterator().next();
        unitilsTransactionManager.startTransactionForDataSource(injectedDataSource);
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
            unitilsTransactionManager.commit();
        } else if (transactionMode == ROLLBACK) {
            unitilsTransactionManager.rollback();
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
            dbMaintainManager.updateDatabaseIfNeeded(applicationContext);
        }

        @Override
        public void beforeTestMethod(Object testObject, Method testMethod, TestContext testContext) throws Exception {
            ApplicationContext applicationContext = getApplicationContext(testContext);
            Set<DataSource> injectedDataSources = injectDataSources(testObject, applicationContext);
            startTransactionForInjectedDataSources(testObject, testMethod, injectedDataSources);
        }

        @Override
        public void afterTestMethod(Object testObject, Method testMethod, Throwable testThrowable, TestContext testContext) throws Exception {
            endTransactions(testObject, testMethod);
        }
    }
}
