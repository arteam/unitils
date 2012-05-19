/*
 * Copyright 2012,  Unitils.org
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

package org.unitilsnew.database;

import org.unitilsnew.core.Unitils;
import org.unitilsnew.database.core.DataSourceService;
import org.unitilsnew.database.core.DataSourceWrapper;
import org.unitilsnew.database.core.TransactionManager;

import javax.sql.DataSource;


/**
 * @author Tim Ducheyne
 */
public class DatabaseUnitils {

    protected static DataSourceService dataSourceService = Unitils.getInstanceOfType(DataSourceService.class);
    protected static TransactionManager transactionManager = Unitils.getInstanceOfType(TransactionManager.class);


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
        DataSourceWrapper dataSourceWrapper = dataSourceService.getDataSourceWrapper(databaseName);
        return dataSourceWrapper.getWrappedDataSource();
    }


    public static DataSourceWrapper getDataSourceWrapper() {
        return getDataSourceWrapper(null);
    }

    public static DataSourceWrapper getDataSourceWrapper(String databaseName) {
        return dataSourceService.getDataSourceWrapper(databaseName);
    }


    /**
     * Starts a new transaction.
     */
    public static void startTransaction() {
        transactionManager.startTransaction();
    }

    /**
     * Commits the current transaction.
     */
    public static void commitTransaction() {
        transactionManager.commit();
    }

    /**
     * Performs a rollback of the current transaction.
     */
    public static void rollbackTransaction() {
        transactionManager.rollback();
    }
}
