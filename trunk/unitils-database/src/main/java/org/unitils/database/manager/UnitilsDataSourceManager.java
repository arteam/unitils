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
package org.unitils.database.manager;

import org.dbmaintain.database.DatabaseConnection;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.unitils.database.UnitilsDataSource;

import javax.sql.DataSource;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class UnitilsDataSourceManager {

    /* Indicates whether the data sources should be wrapped in a TransactionAwareDataSourceProxy */
    protected boolean wrapDataSourceInTransactionalProxy;
    protected DbMaintainManager dbMaintainManager;

    protected Map<String, UnitilsDataSource> dbMaintainDataSources = new IdentityHashMap<String, UnitilsDataSource>();


    public UnitilsDataSourceManager(boolean wrapDataSourceInTransactionalProxy, DbMaintainManager dbMaintainManager) {
        this.wrapDataSourceInTransactionalProxy = wrapDataSourceInTransactionalProxy;
        this.dbMaintainManager = dbMaintainManager;
    }

    public DataSource getDataSource(String databaseName, ApplicationContext applicationContext) {
        UnitilsDataSource unitilsDataSource = getUnitilsDataSource(databaseName, applicationContext);
        return unitilsDataSource.getDataSource();
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
    public UnitilsDataSource getUnitilsDataSource(String databaseName, ApplicationContext applicationContext) {
        if (isBlank(databaseName)) {
            databaseName = null;
        }
        UnitilsDataSource unitilsDataSource;
        if (applicationContext == null) {
            unitilsDataSource = getDataSourceFromDbMaintain(databaseName);
        } else {
            unitilsDataSource = getDataSourceFromApplicationContext(databaseName, applicationContext);
        }
        wrapDataSourceIfNeeded(unitilsDataSource);
        return unitilsDataSource;
    }


    protected void wrapDataSourceIfNeeded(UnitilsDataSource unitilsDataSource) {
        DataSource dataSource = unitilsDataSource.getDataSource();
        if (!wrapDataSourceInTransactionalProxy || dataSource instanceof TransactionAwareDataSourceProxy) {
            // no wrapping requested or needed
            return;
        }
        TransactionAwareDataSourceProxy wrappedDataSource = new TransactionAwareDataSourceProxy(dataSource);
        unitilsDataSource.setDataSource(wrappedDataSource);
    }

    protected UnitilsDataSource getDataSourceFromDbMaintain(String databaseName) {
        UnitilsDataSource unitilsDataSource = dbMaintainDataSources.get(databaseName);
        if (unitilsDataSource == null) {
            DatabaseConnection databaseConnection = dbMaintainManager.getDatabaseConnection(databaseName);
            DataSource dataSource = databaseConnection.getDataSource();
            Set<String> schemaNames = databaseConnection.getDatabaseInfo().getSchemaNames();
            unitilsDataSource = new UnitilsDataSource(dataSource, schemaNames);
            dbMaintainDataSources.put(databaseName, unitilsDataSource);
        }
        return unitilsDataSource;
    }

    protected UnitilsDataSource getDataSourceFromApplicationContext(String databaseName, ApplicationContext applicationContext) {
        if (databaseName == null) {
            return applicationContext.getBean(UnitilsDataSource.class);
        }
        return applicationContext.getBean(databaseName, UnitilsDataSource.class);
    }


}
