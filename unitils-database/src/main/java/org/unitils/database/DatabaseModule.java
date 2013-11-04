/*
 * Copyright 2008,  Unitils.org
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

import static org.unitils.util.AnnotationUtils.getFieldsAnnotatedWith;
import static org.unitils.util.AnnotationUtils.getMethodsAnnotatedWith;
import static org.unitils.util.ModuleUtils.getAnnotationPropertyDefaults;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.unitils.core.Module;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;
import org.unitils.core.config.Configuration;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.database.annotations.Transactional;
import org.unitils.database.config.DatabaseConfiguration;
import org.unitils.database.config.DatabaseConfigurations;
import org.unitils.database.config.DatabaseConfigurationsFactory;
import org.unitils.database.transaction.TransactionHandler;
import org.unitils.database.transaction.UnitilsTransactionManager;
import org.unitils.database.transaction.impl.UnitilsTransactionManagementConfiguration;
import org.unitils.database.util.Flushable;
import org.unitils.util.PropertyUtils;

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
     * {@link #getTransactionalDataSourceAndActivateTransactionIfNeeded(Object)} must be wrapped in a transactional proxy
     */
    public static final String PROPERTY_WRAP_DATASOURCE_IN_TRANSACTIONAL_PROXY = "dataSource.wrapInTransactionalProxy";

    /* The logger instance for this class */
    private static final Log LOGGER = LogFactory.getLog(DatabaseModule.class);

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
     * {@link #getTransactionalDataSourceAndActivateTransactionIfNeeded} must be wrapped in a transactional proxy
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

    private TransactionHandler transactionHandler;

    private DatabaseConfigurations databaseConfigurations;

    /**
     * Initializes this module using the given <code>Configuration</code>
     *
     * @param configuration The config, not null
     */
    public void init(Properties configuration) {
        setConfig(configuration, new TransactionHandler());
    }

    @SuppressWarnings("unchecked")
    public void setConfig(Properties configuration, TransactionHandler transactionHandler2) {
        this.configuration = configuration;
        DatabaseConfigurationsFactory databaseConfigurationsFactory = new DatabaseConfigurationsFactory(new Configuration(configuration));
        databaseConfigurations = databaseConfigurationsFactory.create();

        defaultAnnotationPropertyValues = getAnnotationPropertyDefaults(DatabaseModule.class, configuration, Transactional.class);
        updateDatabaseSchemaEnabled = PropertyUtils.getBoolean(PROPERTY_UPDATEDATABASESCHEMA_ENABLED, configuration);
        wrapDataSourceInTransactionalProxy = PropertyUtils.getBoolean(PROPERTY_WRAP_DATASOURCE_IN_TRANSACTIONAL_PROXY, configuration);

        PlatformTransactionManager.class.getName();
        this.transactionHandler = transactionHandler2;
    }



    /**
     * Initializes the spring support object
     */
    public void afterInit() {
        LOGGER.info("DatabaseModule is loaded");
        for (DatabaseConfiguration conf : databaseConfigurations.getDatabaseConfigurations()) {
            getTransactionHandler().registerTransactionManagementConfiguration(new DataSourceWrapper(conf).getDataSource());
        }
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
            LOGGER.info("Nothing to do. Jump out to make sure that we don't try to instantiate the DataSource");
            return;
        }
        LOGGER.info("Try to instantiate the DataSource.");
        while (!fields.isEmpty() || !methods.isEmpty()) {
            String firstDatabase = null;
            if (!fields.isEmpty()) {
                firstDatabase = fields.iterator().next().getAnnotation(TestDataSource.class).value();
            } else {
                firstDatabase = methods.iterator().next().getAnnotation(TestDataSource.class).value();
            }
            Set<Field> tempFields = new HashSet<Field>();
            Set<Method> tempMethods = new HashSet<Method>();

            DataSourceWrapper wrapper = getDataSourceWrapper(firstDatabase);
           

            getAccessibleObjectsAnnotatedWithChosenDatabase(firstDatabase, fields, tempFields);
            getAccessibleObjectsAnnotatedWithChosenDatabase(firstDatabase, methods, tempMethods);

            wrapper.injectDataSource(tempFields, tempMethods, testObject);

        }
    }

    protected void getAccessibleObjectsAnnotatedWithChosenDatabase(String databaseName, Set<? extends AccessibleObject> firstSet, Set returnSet) {
        //check fields
        for (AccessibleObject field : firstSet) {
            if (field.getAnnotation(TestDataSource.class).value().equals(databaseName)) {
                returnSet.add(field);

                firstSet.remove(field);
            }
        }
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

    protected Object getTestObject() {
        return Unitils.getInstance().getTestContext().getTestObject();
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
            transactionHandler.startTransactionForTestMethod(testObject, testMethod);
        }

        @Override
        public void afterTestTearDown(Object testObject, Method testMethod) {
            transactionHandler.endTransactionForTestMethod(testObject, testMethod);
        }
    }
    public DataSourceWrapper getDataSourceWrapper(String databaseName) {
        DatabaseConfiguration databaseConfig = null;
        if (StringUtils.isEmpty(databaseName)) {
            databaseConfig = databaseConfigurations.getDatabaseConfiguration();
        } else {
            databaseConfig = databaseConfigurations.getDatabaseConfiguration(databaseName);
        }

        DataSourceWrapper wrapper = new DataSourceWrapper(databaseConfig);
        transactionHandler.registerTransactionManagementConfiguration(wrapper.getDataSource());
        wrapper.updateDatabase();
        return wrapper;
    }

    public DataSourceWrapper getDefaultDataSourceWrapper() {
        return getDataSourceWrapper("");
    }



    /**
     * @return the databaseConfigurations
     */
    public DatabaseConfigurations getDatabaseConfigurations() {
        return databaseConfigurations;
    }

    /**
     * @param databaseConfigurations the databaseConfigurations to set
     */
    public void setDatabaseConfigurations(DatabaseConfigurations databaseConfigurations) {
        this.databaseConfigurations = databaseConfigurations;
    }


    /**
     * @return the transactionHandler
     */
    public TransactionHandler getTransactionHandler() {
        return transactionHandler;
    }
}
