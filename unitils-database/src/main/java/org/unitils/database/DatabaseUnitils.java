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

package org.unitils.database;

import org.dbmaintain.database.Database;
import org.dbmaintain.database.Databases;
import org.unitils.core.Unitils;
import org.unitils.database.core.DataSourceService;
import org.unitils.database.core.DataSourceWrapper;
import org.unitils.database.core.TransactionManager;
import org.unitils.database.dbmaintain.DbMaintainWrapper;

import javax.sql.DataSource;


/**
 * @author Tim Ducheyne
 */
public class DatabaseUnitils {

    /**
     * @return The data source for the default database, not null
     */
    public static DataSource getDataSource() {
        return getDataSource(null);
    }

    /**
     * @param databaseName The database name, null for the default database
     * @return The data source, not null
     */
    public static DataSource getDataSource(String databaseName) {
        DataSourceWrapper dataSourceWrapper = getDataSourceService().getDataSourceWrapper(databaseName);
        return dataSourceWrapper.getWrappedDataSource();
    }


    public static DataSourceWrapper getDataSourceWrapper() {
        return getDataSourceWrapper(null);
    }

    public static DataSourceWrapper getDataSourceWrapper(String databaseName) {
        return getDataSourceService().getDataSourceWrapper(databaseName);
    }


    public static Database getDatabase() {
        return getDatabase(null);
    }

    public static Database getDatabase(String databaseName) {
        return getDbMaintainWrapper().getDatabase(databaseName);
    }

    public static Databases getDatabases() {
        return getDbMaintainWrapper().getDatabases();
    }


    /**
     * Starts a new transaction.
     */
    public static void startTransaction() {
        startTransaction(null);
    }

    // todo unit test
    public static void startTransaction(String transactionManagerName) {
        getTransactionManager().startTransaction(transactionManagerName);
    }

    /**
     * Commits the current transaction.
     */
    public static void commitTransaction() {
        getTransactionManager().commit(true);
    }

    /**
     * Performs a rollback of the current transaction.
     */
    public static void rollbackTransaction() {
        getTransactionManager().rollback(true);
    }


    protected static DataSourceService getDataSourceService() {
        return Unitils.getInstanceOfType(DataSourceService.class);
    }

    protected static TransactionManager getTransactionManager() {
        return Unitils.getInstanceOfType(TransactionManager.class);
    }

    protected static DbMaintainWrapper getDbMaintainWrapper() {
        return Unitils.getInstanceOfType(DbMaintainWrapper.class);
    }
}
