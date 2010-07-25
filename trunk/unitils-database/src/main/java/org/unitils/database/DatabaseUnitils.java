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
import org.dbmaintain.database.Databases;
import org.unitils.core.Unitils;

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
     * Returns the DataSource that connects to the test database
     *
     * @return The DataSource that connects to the test database
     */
    public static DataSource getDataSource() {
        return getDatabaseModule().getTransactionalDataSourceAndActivateTransactionIfNeeded(getTestObject());
    }

    /**
     * @return The utility classes for working with databases. For example for getting all table names within a schema.
     */
    public static Databases getDatabases() {
        return getDatabaseModule().getDatabases();
    }


    /**
     * Flushes all pending updates to the database. This method is useful when the effect of updates
     * needs to be checked directly on the database.
     * <p/>
     * A typical usage of this method is, when updates were issues to the database using hibernate,
     * making sure that these updates are flushed, to be able to check the effect of these updates
     * using plain old JDBC.
     */
    public static void flushDatabaseUpdates() {
        getDatabaseModule().flushDatabaseUpdates(getTestObject());
    }


    /**
     * Starts a new transaction on the transaction manager configured in unitils
     */
    public static void startTransaction() {
        getDatabaseModule().startTransaction(getTestObject());
    }

    /**
     * Commits the current unitils transaction
     */
    public static void commitTransaction() {
        getDatabaseModule().commitTransaction(getTestObject());
    }

    /**
     * Performs a rollback of the current unitils transaction
     */
    public static void rollbackTransaction() {
        getDatabaseModule().rollbackTransaction(getTestObject());
    }


    /**
     * Determines whether the test database is outdated and, if that is the case, updates the database with the
     * latest changes. See {@link org.dbmaintain.DbMaintainer} for more information.
     */
    public static void updateDatabase() {
        getDatabaseModule().updateDatabase();
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

    private static MainFactory getMainFactory() {
        return getDatabaseModule().getMainFactory();
    }

    /**
     * @return The current test object
     */
    private static Object getTestObject() {
        return Unitils.getInstance().getTestContext().getTestObject();
    }
}
