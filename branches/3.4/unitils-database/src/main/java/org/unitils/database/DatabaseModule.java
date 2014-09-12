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

import static org.unitils.core.util.ConfigUtils.getInstanceOf;
import static org.unitils.database.util.TransactionMode.COMMIT;
import static org.unitils.database.util.TransactionMode.DEFAULT;
import static org.unitils.database.util.TransactionMode.DISABLED;
import static org.unitils.database.util.TransactionMode.ROLLBACK;
import static org.unitils.util.AnnotationUtils.getFieldsAnnotatedWith;
import static org.unitils.util.AnnotationUtils.getMethodOrClassLevelAnnotationProperty;
import static org.unitils.util.AnnotationUtils.getMethodsAnnotatedWith;
import static org.unitils.util.ModuleUtils.getAnnotationPropertyDefaults;
import static org.unitils.util.ModuleUtils.getEnumValueReplaceDefault;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.unitils.core.Module;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.unitils.core.config.Configuration;
import org.unitils.core.dbsupport.SQLHandler;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.database.annotations.Transactional;
import org.unitils.database.config.DatabaseConfiguration;
import org.unitils.database.config.DatabaseConfigurations;
import org.unitils.database.config.DatabaseConfigurationsFactory;
import org.unitils.database.transaction.UnitilsTransactionManager;
import org.unitils.database.transaction.impl.UnitilsTransactionManagementConfiguration;
import org.unitils.database.util.Flushable;
import org.unitils.database.util.TransactionMode;
import org.unitils.dbmaintainer.DBMaintainer;
import org.unitils.util.PropertyUtils;
import org.unitils.util.ReflectionUtils;

/**
 * Module that provides support for database testing: Creation of a datasource
 * that connects to the test database, support for executing tests in a
 * transaction and automatic maintenance of the test database.
 * <p/>
 * A datasource will be created the first time one is requested. Which type of
 * datasource will be created depends on the configured
 * {@link DataSourceFactory}. By default this will be a pooled datasource that
 * gets its connection-url, username and password from the unitils
 * configuration.
 * <p/>
 * The created datasource can be injected into a field of the test by annotating
 * the field with {@link TestDataSource}. It can then be used to install it in
 * your DAO or other class under test.
 * <p/>
 * If the DBMaintainer is enabled (by setting
 * {@link #PROPERTY_UPDATEDATABASESCHEMA_ENABLED} to true), the test database
 * schema will automatically be updated if needed. This check will be performed
 * once during your test-suite run, namely when the data source is created.
 * <p/>
 * If the test class or method is annotated with {@link Transactional} with
 * transaction mode {@link TransactionMode#COMMIT} or
 * {@link TransactionMode#ROLLBACK}, or if the property
 * 'DatabaseModule.Transactional.value.default' was set to 'commit' or
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
     * Property indicating if the database schema should be updated before
     * performing the tests
     */
    public static final String PROPERTY_UPDATEDATABASESCHEMA_ENABLED = "updateDataBaseSchema.enabled";

    /**
     * Property indicating whether the datasource injected onto test fields
     * annotated with @TestDataSource or retrieved using
     * {@link #getTransactionalDataSourceAndActivateTransactionIfNeeded(Object)}
     * must be wrapped in a transactional proxy
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
     * Indicates whether the datasource injected onto test fields annotated with
     *
     * @TestDataSource or retrieved using
     * {@link #getTransactionalDataSourceAndActivateTransactionIfNeeded} must be
     * wrapped in a transactional proxy
     */
    protected boolean wrapDataSourceInTransactionalProxy;

    /**
     * The transaction manager
     */
    protected UnitilsTransactionManager transactionManager;

    /**
     * Set of possible providers of a spring
     * <code>PlatformTransactionManager</code>
     */
    protected Set<UnitilsTransactionManagementConfiguration> transactionManagementConfigurations = new HashSet<UnitilsTransactionManagementConfiguration>();

    //protected String dialect;
    private DatabaseConfigurations databaseConfigurations;

    //protected DataSourceWrapper wrapper;
    protected Map<String, DataSourceWrapper> wrappers = new HashMap<String, DataSourceWrapper>();

    /**
     * Initializes this module using the given <code>Configuration</code>
     *
     * @param configuration The config, not null
     */
    @SuppressWarnings("unchecked")
    public void init(Properties configuration) {
        this.configuration = configuration;
        DatabaseConfigurationsFactory configFactory = new DatabaseConfigurationsFactory(new Configuration(configuration));
        databaseConfigurations = configFactory.create();
        defaultAnnotationPropertyValues = getAnnotationPropertyDefaults(DatabaseModule.class, configuration, Transactional.class);
        updateDatabaseSchemaEnabled = PropertyUtils.getBoolean(PROPERTY_UPDATEDATABASESCHEMA_ENABLED, configuration);
        wrapDataSourceInTransactionalProxy = PropertyUtils.getBoolean(PROPERTY_WRAP_DATASOURCE_IN_TRANSACTIONAL_PROXY, configuration);
        PlatformTransactionManager.class.getName();
    }

    /**
     * Initializes the spring support object
     */
    public void afterInit() {
        //do nothing
    }

    public void registerTransactionManagementConfiguration() {
        for (DataSourceWrapper wrapper : wrappers.values()) {
            registerTransactionManagementConfiguration(wrapper);
        }
    }

    public void registerTransactionManagementConfiguration(final DataSourceWrapper wrapper) {

        // Make sure that a spring DataSourceTransactionManager is used for transaction management, if
        // no other transaction management configuration takes preference
        registerTransactionManagementConfiguration(new UnitilsTransactionManagementConfiguration() {

            public boolean isApplicableFor(Object testObject) {
                return true;
            }

            public PlatformTransactionManager getSpringPlatformTransactionManager(Object testObject) {
                return new DataSourceTransactionManager(wrapper.getDataSourceAndActivateTransactionIfNeeded());
            }

            public boolean isTransactionalResourceAvailable(Object testObject) {
                return wrapper.isDataSourceLoaded();
            }

            public Integer getPreference() {
                return 1;
            }

        });
    }

    public void activateTransactionIfNeeded() {
        if (transactionManager != null) {
            transactionManager.activateTransactionIfNeeded(getTestObject());
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

        }
        transactionManager.init(transactionManagementConfigurations);
        return transactionManager;
    }

    /**
     * Flushes all pending updates to the database. This method is useful when
     * the effect of updates needs to be checked directly on the database.
     * <p/>
     * Will look for modules that implement {@link Flushable} and call
     * {@link Flushable#flushDatabaseUpdates(Object)} on these modules.
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
     * Updates the database version to the current version, without issuing any
     * other updates to the database. This method can be used for example after
     * you've manually brought the database to the latest version, but the
     * database version is not yet set to the current one. This method can also
     * be useful for example for reinitializing the database after having
     * reorganized the scripts folder.
     *
     * @param sqlHandler The {@link DefaultSQLHandler} to which all commands are
     * issued
     */
    public void resetDatabaseState(SQLHandler sqlHandler, DataSourceWrapper wrapper) {
        String schema = wrapper.databaseConfiguration.getDefaultSchemaName();

        if (!StringUtils.isEmpty(schema)) {
            DatabaseConfiguration databaseConfiguration = wrapper.getDatabaseConfiguration();
            DBMaintainer dbMaintainer = new DBMaintainer(configuration, sqlHandler, databaseConfiguration.getDialect(), databaseConfiguration.getSchemaNames());
            dbMaintainer.resetDatabaseState(schema, wrapper.getDatabaseConfiguration().isDefaultDatabase());
        } else {
            logger.debug("No schema found! The database is not reset!");
        }

    }

    /**
     * Assigns the <code>TestDataSource</code> to every field annotated with
     * {@link TestDataSource} and calls all methods annotated with
     * {@link TestDataSource}
     *
     * @param testObject The test instance, not null
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public void injectDataSource(Object testObject) {
        Set<Field> fields = getFieldsAnnotatedWith(testObject.getClass(), TestDataSource.class);
        Set<Method> methods = getMethodsAnnotatedWith(testObject.getClass(), TestDataSource.class);
        Map<String, DataSource> mapDatasources = new HashMap<String, DataSource>();
        //update all databases
        for (Entry<String, DataSourceWrapper> wrapper : wrappers.entrySet()) {
            DataSource dataSource2 = getDataSource(wrapper.getKey(), mapDatasources, testObject);
            //look if datasource is needed in test.
            setFieldDataSource(wrapper.getKey(), dataSource2, testObject, fields, methods);
        }
    }

    /**
     * @return
     */
    protected void setFieldDataSource(String databaseName, DataSource dataSource, Object testObject, Set<Field> fields, Set<Method> methods) {
        if (fields.isEmpty() && methods.isEmpty()) {
            // Nothing to do. Jump out to make sure that we don't try to instantiate the DataSource
            return;
        }
        for (Field field : fields) {
            TestDataSource annotation = field.getAnnotation(TestDataSource.class);
            String tempDatabaseName = StringUtils.isEmpty(annotation.value()) ? databaseConfigurations.getDatabaseConfiguration().getDatabaseName() : annotation.value();
            if (annotation != null && tempDatabaseName.equals(databaseName)) {
                ReflectionUtils.setFieldValue(testObject, field, dataSource);
            }
        }
        for (Method method : methods) {
            TestDataSource annotation = method.getAnnotation(TestDataSource.class);
            String tempDatabaseName = StringUtils.isEmpty(annotation.value()) ? databaseConfigurations.getDatabaseConfiguration().getDatabaseName() : annotation.value();
            
            if (annotation != null && tempDatabaseName.equals(databaseName)) {
                try {
                    method.invoke(testObject, dataSource);
                } catch (IllegalAccessException ex) {
                    logger.error(ex.getMessage(), ex);
                } catch (IllegalArgumentException ex) {
                    logger.error(ex.getMessage(), ex);
                } catch (InvocationTargetException ex) {
                    logger.error(ex.getMessage(), ex);
                }
            }
        }
    }

    protected DataSource getDataSource(String databaseName, Map<String, DataSource> mapDatasources, Object testObject) {
        DataSource datasource = null;
        if (mapDatasources.containsKey(databaseName)) {
            datasource = mapDatasources.get(databaseName);
        } else {
            DataSourceWrapper wrapper = getWrapper(databaseName);
            datasource = wrapper.getTransactionalDataSourceAndActivateTransactionIfNeeded(testObject);
            mapDatasources.put(databaseName, datasource);
        }
        return datasource;
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
     * Starts a transaction. If the Unitils DataSource was not loaded yet, we
     * simply remember that a transaction was started but don't actually start
     * it. If the DataSource is loaded within this test, the transaction will be
     * started immediately after loading the DataSource.
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
     * Commits or rollbacks the current transaction, if transactions are enabled
     * and a transactionManager is active for the given testObject
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
        UnitilsTransactionManager transactionManager2 = getTransactionManager();
        transactionManager2.activateTransactionIfNeeded(testObject);
        transactionManager2.commit(testObject);
    }

    /**
     * Performs a rollback of the current transaction
     *
     * @param testObject The test object, not null
     */
    public void rollbackTransaction(Object testObject) {
        flushDatabaseUpdates(testObject);
        UnitilsTransactionManager transactionManager2 = getTransactionManager();
        transactionManager2.activateTransactionIfNeeded(testObject);
        transactionManager2.rollback(testObject);
    }

    /**
     * @param testObject The test object, not null
     * @param testMethod The test method, not null
     * @return Whether transactions are enabled for the given test method and
     * test object
     */
    public boolean isTransactionsEnabled(Object testObject, Method testMethod) {
        TransactionMode transactionMode = getTransactionMode(testObject, testMethod);
        return transactionMode != DISABLED;
    }

    // todo javadoc
    public void registerTransactionManagementConfiguration(UnitilsTransactionManagementConfiguration transactionManagementConfiguration) {
        transactionManagementConfigurations.add(transactionManagementConfiguration);
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
            List<String> databaseNames = databaseConfigurations.getDatabaseNames();
            if (!databaseNames.isEmpty()) {
                for (String databaseName : databaseNames) {
                    DataSourceWrapper wrapper = getWrapper(databaseName);
                    setWrapper(wrapper);

                }

            } else {
                //register default wrapper
                getWrapper("");
            }

            try {
                injectDataSource(testObject);
            } catch (Exception e) {
                throw new UnitilsException(e.getMessage(), e);
            }
            startTransactionForTestMethod(testObject, testMethod);
        }

        @Override
        public void afterTestTearDown(Object testObject, Method testMethod) {
            endTransactionForTestMethod(testObject, testMethod);

        }
    }

    /**
     * @param databaseName
     * @return the wrapper
     */
    public DataSourceWrapper getWrapper(String databaseName) {
        String tempDatabaseName = StringUtils.isEmpty(databaseName) ? databaseConfigurations.getDatabaseConfiguration().getDatabaseName() : databaseName;

        if (wrappers.containsKey(tempDatabaseName)) {
            return wrappers.get(tempDatabaseName);
        }

        DataSourceWrapper wrapper = null;
        if (StringUtils.isEmpty(databaseName)) {
            wrapper = new DataSourceWrapper(databaseConfigurations.getDatabaseConfiguration(), configuration, getTransactionManager());
        } else {
            wrapper = new DataSourceWrapper(databaseConfigurations.getDatabaseConfiguration(databaseName), configuration, getTransactionManager());
        }
        setWrapper(wrapper);
        return wrapper;
    }

    /**
     * @param wrapper the wrapper to set
     */
    public void setWrapper(DataSourceWrapper wrapper) {
        if (!wrappers.keySet().contains(wrapper.getDatabaseName())) {
            wrappers.put(wrapper.getDatabaseName(), wrapper);
            registerTransactionManagementConfiguration(wrapper);
        }

    }

    /**
     * @return the databaseConfigurations
     */
    public DatabaseConfigurations getDatabaseConfigurations() {
        return databaseConfigurations;
    }
}
