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
import org.unitils.database.transaction.TransactionMode;
import org.unitils.database.util.DynamicThreadLocalDataSourceProxy;
import org.unitils.database.util.Flushable;
import org.unitils.dbmaintainer.DBMaintainer;
import org.unitils.util.AnnotationUtils;
import static org.unitils.util.AnnotationUtils.getFieldsAnnotatedWith;
import static org.unitils.util.AnnotationUtils.getMethodsAnnotatedWith;
import static org.unitils.util.ConfigUtils.getConfiguredInstance;
import static org.unitils.util.ModuleUtils.getAnnotationPropertyDefaults;
import static org.unitils.util.ModuleUtils.getEnumValueReplaceDefault;
import org.unitils.util.PropertyUtils;
import org.unitils.util.ReflectionUtils;
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
    private DynamicThreadLocalDataSourceProxy dataSource;

    /* The configuration of Unitils */
    private Properties configuration;

    /* Indicates if the DBMaintainer should be invoked to update the database */
    private boolean updateDatabaseSchemaEnabled;

    /* The transaction manager */
    private TransactionManager transactionManager;

    /* 
     * ThreadLocal that remembers if a transaction is active for this thread, but this transaction was started
     * while the DataSource was not loaded yet. In this case, the transaction is not really started 
     */
    private ThreadLocal<Object> transactionShouldBeActiveFor = new ThreadLocal<Object>();


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
    public DynamicThreadLocalDataSourceProxy getDataSource() {
        if (dataSource == null) {
            dataSource = new DynamicThreadLocalDataSourceProxy(createDataSource());
            if (updateDatabaseSchemaEnabled) {
                updateDatabase();
            }
            if (transactionShouldBeActiveFor.get() != null) {
                transactionManager.startTransaction(transactionShouldBeActiveFor.get());
            }
        }
        return dataSource;
    }


    /**
     * Flushes all pending updates to the database. This method is useful when the effect of updates needs to
     * be checked directly on the database.
     * <p/>
     * This will look for modules that implement {@link Flushable} and call flushDatabaseUpdates on these module.
     */
    public void flushDatabaseUpdates() {
        logger.info("Flusing database updates.");
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
     * Determines whether the test database is outdated and, if that is the case, updates the database with the
     * latest changes. See {@link DBMaintainer} for more information.
     *
     * @param sqlHandler SQLHandler that needs to be used for the database updates
     *                   todo make configurable using properties
     */
    public void updateDatabase(SQLHandler sqlHandler) {
        try {
            logger.info("Updating database if needed.");
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
        // Get the factory for the data source
        DataSourceFactory dataSourceFactory = getConfiguredInstance(DataSourceFactory.class, configuration);
        dataSourceFactory.init(configuration);
        return dataSourceFactory.createDataSource();
    }


    /**
     * Initializes the Unitils transaction manager
     */
    protected void initTransactionManager() {
        transactionManager = createTransactionManager();
    }


    /**
     * @return An instance of the transactionManager, like configured in the Unitils configuration
     */
    protected TransactionManager createTransactionManager() {
        return getConfiguredInstance(TransactionManager.class, configuration);
    }


    /**
     * @param testObject The test object, not null
     * @return True if transactions are enabled for this test object, false otherwise
     */
    public boolean isTransactionsEnabled(Object testObject) {
        TransactionMode transactionMode = getTransactionMode(testObject);
        return transactionMode != TransactionMode.DISABLED;
    }


    /**
     * @param testObject The test object, not null
     * @return The {@link TransactionMode} for the given object
     */
    protected TransactionMode getTransactionMode(Object testObject) {
        TransactionMode transactionMode = AnnotationUtils.getClassLevelAnnotationProperty(Transactional.class,
                "value", TransactionMode.DEFAULT, testObject.getClass());
        transactionMode = getEnumValueReplaceDefault(Transactional.class, "value", transactionMode,
                defaultAnnotationPropertyValues);
        return transactionMode;
    }


    /**
     * @param testObject The test object, not null
     * @return True if the TransactionManager is 'active', meaning able to manage transactions
     */
    private boolean isTransactionManagerActive(Object testObject) {
        return transactionManager.isActive(testObject);
    }


    /**
     * Starts a transaction if possible, i.e. if transactions are enabled and a transactionManager is
     * active for the given testObject
     *
     * @param testObject The test object, not null
     */
    protected void startTransactionIfPossible(Object testObject) {
        if (isTransactionsEnabled(testObject) && isTransactionManagerActive(testObject)) {
            startTransaction(testObject);
        }
    }

    /**
     * Starts a transaction. If the Unitils DataSource was not loaded yet, we simply remember that a
     * transaction was started but don't actually start it. If the DataSource is loaded within this
     * test, the transaction will be started immediately after loading the DataSource.
     *
     * @param testObject The test object, not null
     */
    public void startTransaction(Object testObject) {
        if (dataSource == null) {
            transactionShouldBeActiveFor.set(testObject);
        } else {
            transactionManager.startTransaction(testObject);
        }
    }


    /**
     * Commits or rollbacks the current transaction, if transactions are enabled and a transactionManager is
     * active for the given testObject
     *
     * @param testObject The test object, not null
     */
    protected void commitOrRollbackTransactionIfPossible(Object testObject) {
        if (isTransactionsEnabled(testObject) && isTransactionManagerActive(testObject)) {
            TransactionMode transactionMode = getTransactionMode(testObject);
            if (transactionMode == TransactionMode.COMMIT) {
                commitTransaction(testObject);
            } else if (getTransactionMode(testObject) == TransactionMode.ROLLBACK) {
                rollbackTransaction(testObject);
            }
        }
    }


    /**
     * Commits the current transaction. This will cause an exception if a transaction was not active
     *
     * @param testObject The test object, not null
     */
    public void commitTransaction(Object testObject) {
        if (dataSource == null) {
            transactionShouldBeActiveFor.remove();
        } else {
            transactionManager.commit(testObject);
        }

    }


    /**
     * Rollbacks the current transaction. This will cause an exception if a transaction was not active
     *
     * @param testObject The test object, not null
     */
    public void rollbackTransaction(Object testObject) {
        if (dataSource == null) {
            transactionShouldBeActiveFor.remove();
        } else {
            transactionManager.rollback(testObject);
        }
    }


    /**
     * Registers the {@link org.unitils.database.util.DataSourceInterceptingBeanPostProcessor} with the
     * {@link org.unitils.spring.SpringModule}. This will make sure that a bean of type {@link UnitilsDataSource}
     * is replaced with the actual untils data source in the spring application context.
     */
    @SuppressWarnings("unchecked")
    protected void registerSpringDataSourceBeanPostProcessor() {
        String springModuleName = "org.unitils.spring.SpringModule";
        if (Unitils.getInstance().getModulesRepository().isModuleEnabled(springModuleName)) {
            try {
                Class springModuleClass = Class.forName(springModuleName);
                Module springModule = Unitils.getInstance().getModulesRepository().getModuleOfType(springModuleClass);
                Method registerBeanPostProcessorTypeMethod = springModuleClass.getMethod("registerBeanPostProcessorType", Class.class);
                Class dataSourceInterceptingBeanPostProcessorClass = ReflectionUtils.getClassWithName(
                        "org.unitils.database.util.DataSourceInterceptingBeanPostProcessor");
                registerBeanPostProcessorTypeMethod.invoke(springModule, dataSourceInterceptingBeanPostProcessorClass);
            } catch (Exception e) {
                throw new UnitilsException("Error while trying to register SpringDataSourceBeanPostProcessor in SpringModule", e);
            }
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
        public void beforeAll() {
            registerSpringDataSourceBeanPostProcessor();
            initTransactionManager();
        }

        @Override
        public void beforeTestSetUp(Object testObject) {
            injectDataSource(testObject);
            startTransactionIfPossible(testObject);
        }

        @Override
        public void afterTestTearDown(Object testObject) {
            commitOrRollbackTransactionIfPossible(testObject);
        }
    }
}
