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
import org.dbmaintain.database.DatabaseInfo;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.unitils.core.UnitilsException;
import org.unitils.database.UnitilsDataSource;

import javax.sql.DataSource;
import java.util.*;

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
     * If an application context is given, the data source is looked up in the context.
     * Otherwise a data source is retrieved from properties.
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
            unitilsDataSource = getUnitilsDataSourceFromDbMaintain(databaseName);
        } else {
            unitilsDataSource = getUnitilsDataSourceFromApplicationContext(databaseName, applicationContext);
        }
        wrapDataSourceIfNeeded(unitilsDataSource);
        return unitilsDataSource;
    }

    /**
     * Gets all defined data sources.
     *
     * If an application context is given, the data sources are looked up in the context.
     * Otherwise the data source is retrieved from properties.
     *
     * @param applicationContext The spring application context, null if not defined
     * @return The data sources per database name, not null
     */
    public List<String> getDatabaseNames(ApplicationContext applicationContext) {
        if (applicationContext == null) {
            return getDatabaseNamesFromDbMaintain();
        } else {
            return getDatabaseNamesFromApplicationContext(applicationContext);
        }
    }


    protected UnitilsDataSource getUnitilsDataSourceFromDbMaintain(String databaseName) {
        UnitilsDataSource unitilsDataSource = dbMaintainDataSources.get(databaseName);
        if (unitilsDataSource == null) {
            DatabaseConnection databaseConnection = dbMaintainManager.getDatabaseConnection(databaseName);
            unitilsDataSource = createUnitilsDataSource(databaseConnection);
            dbMaintainDataSources.put(databaseName, unitilsDataSource);
        }
        return unitilsDataSource;
    }

    protected List<String> getDatabaseNamesFromDbMaintain() {
        List<String> databaseNames = new ArrayList<String>();

        List<DatabaseConnection> databaseConnections = dbMaintainManager.getDatabaseConnections();
        for (DatabaseConnection databaseConnection : databaseConnections) {
            String databaseName = databaseConnection.getDatabaseInfo().getName();
            databaseNames.add(databaseName);
        }
        return databaseNames;
    }


    @SuppressWarnings({"unchecked", "RedundantCast"})
    protected UnitilsDataSource getUnitilsDataSourceFromApplicationContext(String databaseName, ApplicationContext applicationContext) {
        if (databaseName == null) {
            // implemented like this to be compatible with spring 2.5.6
            Map<String, UnitilsDataSource> unitilsDataSources = applicationContext.getBeansOfType(UnitilsDataSource.class);
            if (unitilsDataSources.isEmpty()) {
                throw new UnitilsException("Unable to determine default unitils data source: no bean of type UnitilsDataSource found in test application context.");
            }
            if (unitilsDataSources.size() > 1) {
                throw new UnitilsException("Unable to determine default unitils data source: more than one bean of type UnitilsDataSource found in test application context. Please specify the id or name of the bean.");
            }
            return unitilsDataSources.values().iterator().next();
        }
        return (UnitilsDataSource) applicationContext.getBean(databaseName, UnitilsDataSource.class);
    }

    protected List<String> getDatabaseNamesFromApplicationContext(ApplicationContext applicationContext) {
        Map<String, UnitilsDataSource> unitilsDataSourceBeans = applicationContext.getBeansOfType(UnitilsDataSource.class);
        return new ArrayList<String>(unitilsDataSourceBeans.keySet());
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

    protected UnitilsDataSource createUnitilsDataSource(DatabaseConnection databaseConnection) {
        DataSource dataSource = databaseConnection.getDataSource();
        DatabaseInfo databaseInfo = databaseConnection.getDatabaseInfo();
        String dialect = databaseInfo.getDialect();
        Set<String> schemaNames = databaseInfo.getSchemaNames();

        UnitilsDataSource unitilsDataSource = new UnitilsDataSource(dataSource, schemaNames);
        unitilsDataSource.setDialect(dialect);
        return unitilsDataSource;
    }

}
