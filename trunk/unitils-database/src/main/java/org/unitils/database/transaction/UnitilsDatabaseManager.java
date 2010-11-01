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

import org.dbmaintain.database.DatabaseConnection;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;
import java.util.IdentityHashMap;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class UnitilsDatabaseManager {

    /* Indicates whether the data sources should be wrapped in a TransactionAwareDataSourceProxy */
    protected boolean wrapDataSourceInTransactionalProxy;
    protected DbMaintainManager dbMaintainManager;

    protected Map<DataSource, TransactionAwareDataSourceProxy> wrappedDataSources = new IdentityHashMap<DataSource, TransactionAwareDataSourceProxy>();


    public UnitilsDatabaseManager(boolean wrapDataSourceInTransactionalProxy, DbMaintainManager dbMaintainManager) {
        this.wrapDataSourceInTransactionalProxy = wrapDataSourceInTransactionalProxy;
        this.dbMaintainManager = dbMaintainManager;
    }

    /**
     * Gets the data source for the given database.
     *
     * If the database is found in the application context (if a context is given) then this data source is returned.
     * Otherwise a data source is created using the data source factory.
     *
     * This will make sure that the same connection is always returned within the same transaction and will make
     * sure that the tx synchronization is done correctly.
     *
     * @param databaseName       The name of the database to get a data source for, null or blank for the default database
     * @param applicationContext The spring application context, null if not defined
     * @return The data source, not null
     */
    public DataSource getDataSource(String databaseName, ApplicationContext applicationContext) {
        if (isBlank(databaseName)) {
            databaseName = null;
        }
        DataSource dataSource;
        if (applicationContext == null) {
            dataSource = getDataSourceFromDbMaintain(databaseName);
        } else {
            dataSource = getDataSourceFromApplicationContext(databaseName, applicationContext);
        }
        return wrapDataSourceIfNeeded(dataSource);
    }


    protected DataSource wrapDataSourceIfNeeded(DataSource dataSource) {
        if (!wrapDataSourceInTransactionalProxy || dataSource instanceof TransactionAwareDataSourceProxy) {
            // no wrapping requested or needed
            return dataSource;
        }
        TransactionAwareDataSourceProxy wrappedDataSource = wrappedDataSources.get(dataSource);
        if (wrappedDataSource == null) {
            wrappedDataSource = new TransactionAwareDataSourceProxy(dataSource);
            wrappedDataSources.put(dataSource, wrappedDataSource);
        }
        return wrappedDataSource;
    }

    protected DataSource getDataSourceFromDbMaintain(String databaseName) {
        DatabaseConnection databaseConnection = dbMaintainManager.getDatabaseConnection(databaseName);
        return databaseConnection.getDataSource();
    }

    protected DataSource getDataSourceFromApplicationContext(String databaseName, ApplicationContext applicationContext) {
        if (databaseName == null) {
            return applicationContext.getBean(DataSource.class);
        }
        return applicationContext.getBean(databaseName, DataSource.class);
    }


}
