/*
 * Copyright 2013,  Unitils.org
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
package org.unitils.database.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.database.dbmaintain.DbMaintainWrapper;

import javax.sql.DataSource;
import java.util.List;

/**
 * @author Tim Ducheyne
 */
public class DataSourceService {

    /* The logger instance for this class */
    protected static Log logger = LogFactory.getLog(DataSourceService.class);

    protected DataSourceProviderManager dataSourceProviderManager;
    protected DbMaintainWrapper dbMaintainWrapper;
    protected TransactionManager transactionManager;


    public DataSourceService(DataSourceProviderManager dataSourceProviderManager, DbMaintainWrapper dbMaintainWrapper, TransactionManager transactionManager) {
        this.dataSourceProviderManager = dataSourceProviderManager;
        this.dbMaintainWrapper = dbMaintainWrapper;
        this.transactionManager = transactionManager;
    }


    public List<String> getDatabaseNames() {
        DataSourceProvider dataSourceProvider = dataSourceProviderManager.getDataSourceProvider();
        return dataSourceProvider.getDatabaseNames();
    }


    public DataSource getDataSource(String databaseName) {
        return getDataSource(databaseName, false);
    }

    public DataSource getDataSource(String databaseName, boolean wrapDataSourceInTransactionalProxy) {
        DataSourceWrapper dataSourceWrapper = getDataSourceWrapper(databaseName);
        return dataSourceWrapper.getDataSource(wrapDataSourceInTransactionalProxy);
    }


    public DataSourceWrapper getDataSourceWrapper(String databaseName) {
        DataSourceProvider dataSourceProvider = dataSourceProviderManager.getDataSourceProvider();
        DataSourceWrapper dataSourceWrapper = dataSourceProvider.getDataSourceWrapper(databaseName);

        DataSource dataSource = dataSourceWrapper.getWrappedDataSource();
        transactionManager.registerDataSource(dataSource);
        dbMaintainWrapper.updateDatabaseIfNeeded();
        return dataSourceWrapper;
    }
}
