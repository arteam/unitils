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
package org.unitils.database;

import org.dbmaintain.MainFactory;
import org.dbmaintain.database.Database;
import org.unitils.core.Unitils;
import org.unitils.database.transaction.UnitilsDataSourceManager;
import org.unitils.database.transaction.UnitilsTransactionManager;

import javax.sql.DataSource;

/**
 * Class providing access to the functionality of the database module using static methods. Meant
 * to be used directly in unit tests.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DatabaseUnitils {


    /**
     * Returns the DataSource that connects to the default test database
     *
     * NOTE: this will not retrieve a data source from a Spring context.
     * Use injection or applicationContext.getBean() instead.
     *
     * @return The DataSource that connects to the test database
     */
    public static DataSource getDataSource() {
        return getDataSource(null);
    }

    public static DataSource getDataSourceAndStartTransaction() {
        return getDataSourceAndStartTransaction(null);
    }

    /**
     * Returns the DataSource that connects to the test database
     *
     * @param databaseName The name of the database to get a data source for, null for the default database
     * @return The DataSource that connects to the test database
     */
    public static DataSource getDataSource(String databaseName) {
        return getUnitilsDataSourceManager().getDataSource(databaseName, null);
    }

    public static DataSource getDataSourceAndStartTransaction(String databaseName) {
        DataSource dataSource = getDataSource(databaseName);
        getUnitilsTransactionManager().startTransactionForDataSource(dataSource);
        return dataSource;
    }


    /**
     * @return The utility class for working with the default database. For example for getting all table names within a schema.
     */
    public static Database getDefaultDatabase() {
        return getDatabase(null);
    }

    /**
     * @param databaseName The name of the database to get the database support for, null for the default database
     * @return The utility classes for working with the database. For example for getting all table names within a schema.
     */
    public static Database getDatabase(String databaseName) {
        return getDatabaseModule().getDatabase(databaseName);
    }


    /**
     * Starts a transaction for the give data source using a {@link org.springframework.jdbc.datasource.DataSourceTransactionManager}.
     * If a transaction is already started for the given data source, the start will be ignored.
     * If a transaction is already started for another data source, an expception will be raised.
     *
     * @param dataSource The data source, not null
     */
    public static void startTransaction(DataSource dataSource) {
        getUnitilsTransactionManager().startTransactionForDataSource(dataSource);
    }

    /**
     * Commits the current transaction.
     * An exception will be raised if no transaction is currently active.
     */
    public static void commitTransaction() {
        getUnitilsTransactionManager().commit();
    }

    /**
     * Rolls back the current transaction.
     * An exception will be raised if no transaction is currently active.
     */
    public static void rollbackTransaction() {
        getUnitilsTransactionManager().rollback();
    }


    /**
     * Determines whether the test database is outdated and, if that is the case, updates the database with the
     * latest changes. See {@link org.dbmaintain.DbMaintainer} for more information.
     */
    public static void updateDatabaseIfNeeded() {
        getDatabaseModule().updateDatabaseIfNeeded(null);
    }

    /**
     * Updates the database version to the current version, without issuing any other updates to the database.
     * This method can be used for example after you've manually brought the database to the latest version, but
     * the database version is not yet set to the current one. This method can also be useful for example for
     * reinitializing the database after having reorganized the scripts folder.
     */
    public static void markDatabaseAsUpToDate() {
        getMainFactory().createDbMaintainer().markDatabaseAsUpToDate();
    }

    /**
     * Clears all configured schema's. I.e. drops all tables, views and other database objects.
     */
    public static void clearDatabase() {
        getMainFactory().createDBClearer().clearDatabase();
    }

    /**
     * Cleans all configured schema's. I.e. removes all data from its database tables.
     */
    public static void cleanDatabase() {
        getMainFactory().createDBCleaner().cleanDatabase();
    }

    /**
     * Disables all foreign key and not-null constraints on the configured schema's.
     */
    public static void disableConstraints() {
        getMainFactory().createConstraintsDisabler().disableConstraints();
    }

    /**
     * Updates all sequences that have a value below a certain configurable treshold to become equal
     * to this treshold
     */
    public static void updateSequences() {
        getMainFactory().createSequenceUpdater().updateSequences();
    }


    /**
     * Gets the instance DatabaseModule that is registered in the modules repository.
     * This instance implements the actual behavior of the static methods in this class.
     * This way, other implementations can be plugged in, while keeping the simplicity of using static methods.
     *
     * @return the instance, not null
     */
    private static DatabaseModule getDatabaseModule() {
        return Unitils.getInstance().getModulesRepository().getModuleOfType(DatabaseModule.class);
    }

    private static UnitilsTransactionManager getUnitilsTransactionManager() {
        return getDatabaseModule().getUnitilsTransactionManager();
    }

    private static UnitilsDataSourceManager getUnitilsDataSourceManager() {
        return getDatabaseModule().getUnitilsDataSourceManager();
    }

    private static MainFactory getMainFactory() {
        return getDatabaseModule().getDbMaintainMainFactory();
    }
}
