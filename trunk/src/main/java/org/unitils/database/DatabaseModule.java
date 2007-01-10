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
import org.unitils.core.UnitilsException;
import org.unitils.database.annotations.DatabaseTest;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.database.config.DataSourceFactory;
import org.unitils.dbmaintainer.constraints.ConstraintsCheckDisablingDataSource;
import org.unitils.dbmaintainer.constraints.ConstraintsDisabler;
import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.handler.StatementHandlerException;
import org.unitils.dbmaintainer.maintainer.DBMaintainer;
import org.unitils.dbmaintainer.util.DatabaseModuleConfigUtils;
import org.unitils.util.AnnotationUtils;
import org.unitils.util.ConfigUtils;
import org.unitils.util.ReflectionUtils;

import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Module that provides basic support for database testing.
 * <p/>
 * This module only provides services to unit test classes that are annotated with an annotation that identifies it as a
 * database test. By default, the annotation {@link DatabaseTest} is supported, but other annotations can be added as
 * well by invoking {@link #registerDatabaseTestAnnotation(Class<? extends java.lang.annotation.Annotation>)}
 * <p/>
 * Following services are provided:
 * <ul>
 * <li>Connection pooling: A connection pooled DataSource is created, and supplied to methods annotated with
 * {@link TestDataSource}</li>
 * <li>A 'current connection' is associated with each thread from which the method #getCurrentConnection is called</li>
 * <li>If the updateDataBaseSchema.enabled property is set to true, the {@link DBMaintainer} is invoked to update the
 * database and prepare it for unit testing (see {@link DBMaintainer} Javadoc)</li>
 * <p/>
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DatabaseModule implements Module {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DatabaseModule.class);

    /* Property keys indicating if the database schema should be updated before performing the tests */
    private static final String PROPKEY_UPDATEDATABASESCHEMA_ENABLED = "updateDataBaseSchema.enabled";

    /* Property key indicating if the database constraints should org disabled after updating the database */
    private static final String PROPKEY_DISABLECONSTRAINTS_ENABLED = "dbMaintainer.disableConstraints.enabled";

    /* The pooled datasource instance */
    private DataSource dataSource;

    /* The Configuration of Unitils */
    private Configuration configuration;

    /* Indicates if database constraints should be disabled */
    private boolean disableConstraints;

    /* Indicates if the DBMaintainer should be invoked to update the database */
    private boolean updateDatabaseSchemaEnabled;

    /* Set of annotations that identify a test as a DatabaseTest */
    private Set<Class<? extends Annotation>> databaseTestAnnotations = new HashSet<Class<? extends Annotation>>();


    /**
     * Creates a new instance of the module, and registers the {@link DatabaseTest} annotation as an annotation that
     * identifies a test class as a database test.
     */
    public DatabaseModule() {
        registerDatabaseTestAnnotation(DatabaseTest.class);
    }


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
     * Registers the given annotation as an annotation that identifies a test class as being a database test.
     *
     * @param databaseTestAnnotation the annotation to register, not null
     */
    public void registerDatabaseTestAnnotation(Class<? extends Annotation> databaseTestAnnotation) {

        databaseTestAnnotations.add(databaseTestAnnotation);
    }


    /**
     * Checks whether the given test instance is a database test, i.e. is annotated with the {@link DatabaseTest} annotation.
     *
     * @param testClass the test class, not null
     * @return true if the test class is a database test false otherwise
     */
    public boolean isDatabaseTest(Class<?> testClass) {

        for (Class<? extends Annotation> databaseTestAnnotation : databaseTestAnnotations) {
            if (testClass.getAnnotation(databaseTestAnnotation) != null) {
                return true;
            }
        }
        return false;
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
     * Determines whether the test database is outdated and, if that is the case, updates the database with the
     * latest changes. See {@link DBMaintainer} for more information.
     */
    public void updateDatabaseSchema() {
        try {
            DBMaintainer dbMaintainer = createDbMaintainer(configuration);
            dbMaintainer.updateDatabase();

        } catch (StatementHandlerException e) {
            throw new UnitilsException("Error while updating database", e);
        }
    }


    /**
     * Creates a datasource by using the factory that is defined by the dataSourceFactory.className property
     *
     * @return the datasource
     */
    protected DataSource createDataSource() {

        logger.info("Creating DataSource");
        DataSourceFactory dataSourceFactory = createDataSourceFactory();
        dataSourceFactory.init(configuration);
        DataSource dataSource = dataSourceFactory.createDataSource();

        // If contstraints disabling is active, a ConstraintsCheckDisablingDataSource is
        // returned that wrappes the TestDataSource object
        if (disableConstraints) {
            ConstraintsDisabler constraintsDisabler = createConstraintsDisabler(dataSource);
            dataSource = new ConstraintsCheckDisablingDataSource(dataSource, constraintsDisabler);
        }
        return dataSource;
    }


    /**
     * Creates the configured instance of the {@link ConstraintsDisabler}
     *
     * @param dataSource the datasource, not null
     * @return The configured instance of the {@link ConstraintsDisabler}
     */
    protected ConstraintsDisabler createConstraintsDisabler(DataSource dataSource) {
        StatementHandler statementHandler = DatabaseModuleConfigUtils.getConfiguredStatementHandlerInstance(configuration, dataSource);
        return DatabaseModuleConfigUtils.getConfiguredDatabaseTaskInstance(ConstraintsDisabler.class, configuration, dataSource, statementHandler);
    }


    /**
     * Assigns the <code>TestDataSource</code> to every field annotated with {@link TestDataSource} and calls all methods
     * annotated with {@link TestDataSource}
     *
     * @param testObject The test instance, not null
     */
    protected void injectDataSource(Object testObject) {
        List<Field> fields = AnnotationUtils.getFieldsAnnotatedWith(testObject.getClass(), TestDataSource.class);
        for (Field field : fields) {
            try {
                ReflectionUtils.setFieldValue(testObject, field, getDataSource());

            } catch (UnitilsException e) {
                throw new UnitilsException("Unable to assign the DataSource to field annotated with @" + TestDataSource.class.getSimpleName() +
                        "Ensure that this field is of type " + DataSource.class.getName(), e);
            }
        }

        List<Method> methods = AnnotationUtils.getMethodsAnnotatedWith(testObject.getClass(), TestDataSource.class);
        for (Method method : methods) {
            try {
                ReflectionUtils.invokeMethod(testObject, method, getDataSource());

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
     * Creates a new instance of the {@link DBMaintainer}
     *
     * @param configuration the config, not null
     * @return a new instance of the DBMaintainer
     */
    protected DBMaintainer createDbMaintainer(Configuration configuration) {
        return new DBMaintainer(configuration, getDataSource());
    }


    /**
     * Returns an instance of the configured {@link DataSourceFactory}
     *
     * @return The configured {@link DataSourceFactory}
     */
    protected DataSourceFactory createDataSourceFactory() {
        return ConfigUtils.getConfiguredInstance(DataSourceFactory.class, configuration);
    }


    /**
     * @return The {@link TestListener} associated with this module
     */
    public TestListener createTestListener() {
        return new DatabaseTestListener();
    }


    /**
     * TestListener that makes callbacks to methods of this module while running tests. This TestListener makes
     * sure that before running the first DatabaseTest, the database connection is initialized, and that before doing
     * the setup of every test, the DataSource is injected to fields and methods annotated with the TestDataSource
     * annotation.
     */
    private class DatabaseTestListener extends TestListener {

        @Override
        public void beforeTestSetUp(Object testObject) {

            injectDataSource(testObject);
        }
    }
}
