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

import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.Module;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.database.config.DataSourceFactory;
import org.unitils.database.util.Flushable;
import org.unitils.dbmaintainer.DBMaintainer;
import org.unitils.dbmaintainer.script.StatementHandler;
import org.unitils.dbmaintainer.script.impl.StatementHandlerException;
import org.unitils.dbmaintainer.structure.ConstraintsCheckDisablingDataSource;
import org.unitils.dbmaintainer.structure.ConstraintsDisabler;
import static org.unitils.dbmaintainer.util.DatabaseModuleConfigUtils.getConfiguredDatabaseTaskInstance;
import static org.unitils.dbmaintainer.util.DatabaseModuleConfigUtils.getConfiguredStatementHandlerInstance;
import static org.unitils.util.AnnotationUtils.getFieldsAnnotatedWith;
import static org.unitils.util.AnnotationUtils.getMethodsAnnotatedWith;
import static org.unitils.util.ConfigUtils.getConfiguredInstance;
import static org.unitils.util.ReflectionUtils.invokeMethod;
import static org.unitils.util.ReflectionUtils.setFieldValue;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
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

    /* Property keys indicating if the database schema should be updated before performing the tests */
    public static final String PROPKEY_UPDATEDATABASESCHEMA_ENABLED = "updateDataBaseSchema.enabled";

    /* Property key indicating if the database constraints should org disabled after updating the database */
    public static final String PROPKEY_DISABLECONSTRAINTS_ENABLED = "dbMaintainer.disableConstraints.enabled";

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DatabaseModule.class);

    /* The datasource instance */
    private DataSource dataSource;

    /* The Configuration of Unitils */
    private Configuration configuration;

    /* Indicates if database constraints should be disabled */
    private boolean disableConstraints;

    /* Indicates if the DBMaintainer should be invoked to update the database */
    private boolean updateDatabaseSchemaEnabled;


    /**
     * Initializes this module using the given <code>Configuration</code>
     *
     * @param configuration the config, not null
     */
    public void init(Configuration configuration) {
        this.configuration = configuration;

        disableConstraints = configuration.getBoolean(PROPKEY_DISABLECONSTRAINTS_ENABLED);
        updateDatabaseSchemaEnabled = configuration.getBoolean(PROPKEY_UPDATEDATABASESCHEMA_ENABLED);
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
            if (updateDatabaseSchemaEnabled) {
                updateDatabaseSchema();
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
    public void updateDatabaseSchema() {
        try {
            logger.info("Updating database schema if needed.");
            DBMaintainer dbMaintainer = new DBMaintainer(configuration, getDataSource());
            dbMaintainer.updateDatabase();

        } catch (StatementHandlerException e) {
            throw new UnitilsException("Error while updating database", e);
        }
    }


    /**
     * Assigns the <code>TestDataSource</code> to every field annotated with {@link TestDataSource} and calls all methods
     * annotated with {@link TestDataSource}
     *
     * @param testObject The test instance, not null
     */
    public void injectDataSource(Object testObject) {
        List<Field> fields = getFieldsAnnotatedWith(testObject.getClass(), TestDataSource.class);
        DataSource dataSource = getDataSource();
        for (Field field : fields) {
            try {
                setFieldValue(testObject, field, dataSource);

            } catch (UnitilsException e) {
                throw new UnitilsException("Unable to assign the DataSource to field annotated with @" + TestDataSource.class.getSimpleName() +
                        ". Ensure that this field is of type " + DataSource.class.getName(), e);
            }
        }

        List<Method> methods = getMethodsAnnotatedWith(testObject.getClass(), TestDataSource.class);
        for (Method method : methods) {
            try {
                invokeMethod(testObject, method, getDataSource());

            } catch (UnitilsException e) {
                throw new UnitilsException("Unable to invoke method " + testObject.getClass().getSimpleName() + "." + methods.get(0).getName() +
                        " annotated with @" + TestDataSource.class.getSimpleName() + " Ensure that this method has following signature: void myMethod(" +
                        DataSource.class.getName() + " dataSource)", e);

            } catch (InvocationTargetException e) {
                throw new UnitilsException("Method " + testObject.getClass().getSimpleName() + "." + methods.get(0).getName() + " annotated with "
                        + TestDataSource.class.getSimpleName() + " has thrown an exception", e.getCause());
            }
        }
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
        DataSource dataSource = dataSourceFactory.createDataSource();

        // If contstraints disabling is active, a ConstraintsCheckDisablingDataSource is
        // returned that wrappes the TestDataSource object
        if (disableConstraints) {
            StatementHandler statementHandler = getConfiguredStatementHandlerInstance(configuration, dataSource);
            ConstraintsDisabler constraintsDisabler = getConfiguredDatabaseTaskInstance(ConstraintsDisabler.class, configuration, dataSource, statementHandler);
            dataSource = new ConstraintsCheckDisablingDataSource(dataSource, constraintsDisabler);
        }
        return dataSource;
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
    private class DatabaseTestListener extends TestListener {

        @Override
        public void beforeTestSetUp(Object testObject) {
            injectDataSource(testObject);
        }
    }
}
