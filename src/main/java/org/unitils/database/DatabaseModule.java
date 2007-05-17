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
import org.unitils.database.config.DataSourceFactory;
import org.unitils.database.util.Flushable;
import org.unitils.dbmaintainer.DBMaintainer;
import static org.unitils.util.AnnotationUtils.getFieldsAnnotatedWith;
import static org.unitils.util.AnnotationUtils.getMethodsAnnotatedWith;
import static org.unitils.util.ConfigUtils.getConfiguredInstance;
import org.unitils.util.PropertyUtils;
import static org.unitils.util.ReflectionUtils.setFieldAndSetterValue;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Properties;

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

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DatabaseModule.class);

    /* The datasource instance */
    private DataSource dataSource;

    /* The configuration of Unitils */
    private Properties configuration;

    /* Indicates if the DBMaintainer should be invoked to update the database */
    private boolean updateDatabaseSchemaEnabled;


    /**
     * Initializes this module using the given <code>Configuration</code>
     *
     * @param configuration the config, not null
     */
    public void init(Properties configuration) {
        this.configuration = configuration;

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
            if (updateDatabaseSchemaEnabled) {
                updateDatabase();
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
     * todo make configurable using properties
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
        }
    }
}
