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
package org.unitils.database.transaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbmaintain.DbMaintainer;
import org.dbmaintain.MainFactory;
import org.dbmaintain.database.*;
import org.dbmaintain.database.impl.DefaultSQLHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.unitils.core.UnitilsException;
import org.unitils.database.UnitilsDataSource;
import org.unitils.database.datasource.DataSourceFactory;
import org.unitils.util.PropertyUtils;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class UnitilsDatabaseManager {

    /**
     * Property indicating if the database schema should be updated before performing the tests
     */
    public static final String PROPERTY_UPDATEDATABASESCHEMA_ENABLED = "updateDataBaseSchema.enabled";
    /**
     * Property indicating whether the data source should be wrapped in a {@link org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy}
     */
    public static final String PROPERTY_WRAP_DATASOURCE_IN_TRANSACTIONAL_PROXY = "dataSource.wrapInTransactionalProxy";

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(UnitilsDatabaseManager.class);

    /* The configuration of Unitils */
    protected Properties configuration;
    /* Indicates whether the data sources should be wrapped in a TransactionAwareDataSourceProxy */
    protected boolean wrapDataSourceInTransactionalProxy;
    protected DataSourceFactory dataSourceFactory;
    /* Indicates if the DBMaintain should be invoked to update the database */
    protected boolean updateDatabaseSchemaEnabled;
    /* True if update database has already been called */
    protected boolean updateDatabaseCalled = false;

    protected Databases databases;
    protected Map<String, DataSource> dataSourcesPerDatabaseName = new HashMap<String, DataSource>();


    public UnitilsDatabaseManager(Properties configuration, DataSourceFactory dataSourceFactory) {
        this.configuration = configuration;
        this.dataSourceFactory = dataSourceFactory;
        this.updateDatabaseSchemaEnabled = PropertyUtils.getBoolean(PROPERTY_UPDATEDATABASESCHEMA_ENABLED, configuration);
        this.wrapDataSourceInTransactionalProxy = PropertyUtils.getBoolean(PROPERTY_WRAP_DATASOURCE_IN_TRANSACTIONAL_PROXY, configuration);
    }


    public Databases getDatabases() {
        if (databases == null) {
            DatabaseInfoFactory databaseInfoFactory = new DatabaseInfoFactory(configuration);
            List<DatabaseInfo> databaseInfos = databaseInfoFactory.getDatabaseInfos();
            DatabasesFactory databasesFactory = new DatabasesFactory(configuration, new DefaultSQLHandler(), dataSourceFactory);
            databases = databasesFactory.createDatabases(databaseInfos);
        }
        return databases;
    }

    public Database getDatabase(String databaseName) {
        Databases databases = getDatabases();
        if (databaseName == null) {
            return databases.getDefaultDatabase();
        }
        return databases.getDatabase(databaseName);
    }

    /**
     * Determines whether the test database is outdated and, if that is the case, updates the database with the
     * latest changes.
     *
     * @return True if an update occurred, false if the database was up to date
     * @see {@link org.dbmaintain.DbMaintainer}
     */
    public boolean updateDatabaseIfNeeded() {
        if (!updateDatabaseSchemaEnabled || updateDatabaseCalled) {
            return false;
        }
        updateDatabaseCalled = true;

        logger.info("Checking if database has to be updated.");

        DbMaintainer dbMaintainer = createDbMaintainMainFactory().createDbMaintainer();
        return dbMaintainer.updateDatabase(false);
    }

    public MainFactory createDbMaintainMainFactory() {
        return new MainFactory(configuration, getDatabases());
    }

    /**
     * Gets the data source for the given database.
     *
     * If the database is found in the application context (if a context is given) then this data source is returned.
     * Otherwise a data source is created using the data source factory.
     *
     * The data source will be wrapped in a {@link org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy}
     * when the {@link #PROPERTY_WRAP_DATASOURCE_IN_TRANSACTIONAL_PROXY} is set to true (default).
     *
     * This will make sure that the same connection is always returned within the same transaction and will make
     * sure that the tx synchronization is done correctly.
     *
     * @param databaseName       The name of the database to get a data source for, null for the default database
     * @param applicationContext The spring application context, null if not defined
     * @return The data source, not null
     */
    public DataSource getDataSource(String databaseName, ApplicationContext applicationContext) {
        DataSource dataSource = dataSourcesPerDatabaseName.get(databaseName);
        if (dataSource == null) {
            if (applicationContext == null) {
                dataSource = createDataSource(databaseName, applicationContext);
            } else {
                dataSource = getDataSourceFromApplicationContext(databaseName, applicationContext);
            }
            dataSource = wrapDataSourceIfNeeded(dataSource);
            dataSourcesPerDatabaseName.put(databaseName, dataSource);
        }
        return dataSource;
    }

    protected DataSource wrapDataSourceIfNeeded(DataSource dataSource) {
        if (wrapDataSourceInTransactionalProxy && !(dataSource instanceof TransactionAwareDataSourceProxy)) {
            return new TransactionAwareDataSourceProxy(dataSource);
        }
        return dataSource;
    }


    protected DataSource createDataSource(String databaseName, ApplicationContext applicationContext) {
        try {
            Database database = getDatabase(databaseName);
            return database.getDataSource();

        } catch (DatabaseException e) {
            String message;
            if (databaseName == null) {
                message = "Unable to create data source for the default database: ";
            } else {
                message = "Unable to create data source for database name " + databaseName + ": ";
            }
            message += "\nIf you are using a Spring application context, make sure that a correct UnitilsDataSource bean for this data source is configured in the application context. " +
                    "\nIf you are configuring Unitils without Spring, make sure that the data source is configured correctly in the unitils properties.";
            throw new UnitilsException(message, e);
        }
    }

    protected DataSource getDataSourceFromApplicationContext(String databaseName, ApplicationContext applicationContext) {
        String foundBeanName = null;
        DataSource dataSource = null;

        Map<String, UnitilsDataSource> unitilsDataSourceBeans = applicationContext.getBeansOfType(UnitilsDataSource.class);
        for (Map.Entry<String, UnitilsDataSource> entry : unitilsDataSourceBeans.entrySet()) {
            String beanName = entry.getKey();
            UnitilsDataSource unitilsDataSource = entry.getValue();

            if (unitilsDataSource.hasDatabaseName(databaseName)) {
                if (dataSource != null) {
                    if (beanName == null) {
                        beanName = "<no-name>";
                    }
                    if (databaseName == null) {
                        throw new UnitilsException("Unable to get default unitils data source from test application context. More than one default data source was configured in UnitilsDataSource beans with names " + beanName + " and " + foundBeanName);
                    }
                    throw new UnitilsException("Unable to get unitils data source for database name " + databaseName + " from test application context. More than one data source was configured with database name " + databaseName + " in UnitilsDataSource beans with names " + beanName + " and " + foundBeanName);
                }
                foundBeanName = beanName;
                dataSource = unitilsDataSource.getDataSource();
            }
        }
        if (dataSource == null) {
            if (databaseName == null) {
                throw new UnitilsException("Unable to get default unitils data source from test application context. No UnitilsDataSource bean found with an empty database name.");
            }
            throw new UnitilsException("Unable to get unitils data source for database name " + databaseName + " from test application context. No UnitilsDataSource bean found with this database name.");
        }
        return dataSource;
    }
}
