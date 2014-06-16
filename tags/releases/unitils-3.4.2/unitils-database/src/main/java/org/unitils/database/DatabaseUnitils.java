/*
 * Copyright 2008,  Unitils.org
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

import java.sql.Connection;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.DelegatingConnection;
import org.unitils.core.Unitils;
import org.unitils.core.dbsupport.SQLHandler;

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
    public static DataSource getDataSource(String databaseName) {
    	return getDatabaseModule().getWrapper(databaseName).getTransactionalDataSourceAndActivateTransactionIfNeeded(getTestObject());
    }
    
    /**
     * Returns the DataSource that connects to the test database
     *
     * @return The DataSource that connects to the test database
     */
    public static DataSource getDataSource() {
        return getDataSource("");
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
     *
     */
    public static void rollbackTransaction() {
        getDatabaseModule().rollbackTransaction(getTestObject());
    }


    /**
     * Determines whether the test database is outdated and, if that is the case, updates the database with the
     * latest changes. See {@link DBMaintainer} for more information.
     */
    public static void updateDatabase() {
        updateDatabase("");
    }
    
    /**
     * Determines whether the test database is outdated and, if that is the case, updates the database with the
     * latest changes. See {@link DBMaintainer} for more information.
     */
    public static void updateDatabase(String databaseName) {
        getDatabaseModule().getWrapper(databaseName).updateDatabase();
    }


    /**
     * Updates the database version to the current version, without issuing any other updates to the database.
     * This method can be used for example after you've manually brought the database to the latest version, but
     * the database version is not yet set to the current one. This method can also be useful for example for
     * reinitializing the database after having reorganized the scripts folder.
     */
    public static void resetDatabaseState() {
        resetDatabaseState("");
    }
    
    /**
     * Updates the database version to the current version, without issuing any other updates to the database.
     * This method can be used for example after you've manually brought the database to the latest version, but
     * the database version is not yet set to the current one. This method can also be useful for example for
     * reinitializing the database after having reorganized the scripts folder.
     */
    public static void resetDatabaseState(String databaseName) {
        DatabaseModule databaseModule = getDatabaseModule();
        DataSourceWrapper wrapper = databaseModule.getWrapper(databaseName);
        SQLHandler sqlHandler = wrapper.getDefaultSqlHandler();
        
        databaseModule.resetDatabaseState(sqlHandler, wrapper);
    }


    /**
     * Clears all configured schema's. I.e. drops all tables, views and other database objects.
     */
    public static void clearSchemas() {
        clearSchemas("");
    }
    
    /**
     * Clears all configured schema's. I.e. drops all tables, views and other database objects.
     */
    public static void clearSchemas(String databaseName) {
        getDatabaseModule().getWrapper(databaseName).clearSchemas();
    }


    /**
     * Cleans all configured schema's. I.e. removes all data from its database tables.
     */
    public static void cleanSchemas() {
        cleanSchemas("");
    }
    
    /**
     * Cleans all configured schema's. I.e. removes all data from its database tables.
     */
    public static void cleanSchemas(String databaseName) {
        getDatabaseModule().getWrapper(databaseName).cleanSchemas();
    }


    /**
     * Disables all foreign key and not-null constraints on the configured schema's.
     */
    public static void disableConstraints() {
        disableConstraints("");
    }
    
    /**
     * Disables all foreign key and not-null constraints on the configured schema's.
     */
    public static void disableConstraints(String databaseName) {
        getDatabaseModule().getWrapper(databaseName).disableConstraints();
    }


    /**
     * Updates all sequences that have a value below a certain configurable treshold to become equal
     * to this treshold
     */
    public static void updateSequences() {
        updateSequences("");
    }
    
    /**
     * Updates all sequences that have a value below a certain configurable treshold to become equal
     * to this treshold
     */
    public static void updateSequences(String databaseName) {
        getDatabaseModule().getWrapper(databaseName).updateSequences();
    }


    /**
     * Generates a definition file that defines the structure of dataset's, i.e. a XSD of DTD that
     * describes the structure of the database.
     */
    public static void generateDatasetDefinition() {
        generateDatasetDefinition("");
    }
    
    /**
     * Generates a definition file that defines the structure of dataset's, i.e. a XSD of DTD that
     * describes the structure of the database.
     */
    public static void generateDatasetDefinition(String databaseName) {
        getDatabaseModule().getWrapper(databaseName).generateDatasetDefinition();
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
    
    /**
     * @return The current test object
     */
    private static Object getTestObject() {
        Object testObject = Unitils.getInstance().getTestContext().getTestObject();
        return testObject;
    }
    
    /**
     * This method gets a {@link Connection} from the {@link DataSource} and checks if it is a {@link oracle.jdbc.driver.OracleConnection}.
     * There is a bug with commons-dbcp 1.4: if you want to create a {@link oracle.sql.BLOB} or a {@link java.sql.Clob} than you must get the inner {@link Connection} but you get another {@link Connection}.
     * This is fixed in this method.
     * @param connection
     * @param dataSource
     * @return
     */
    public static Connection getGoodConnection(Connection connection, DataSource dataSource) {
        if (dataSource instanceof BasicDataSource) {
            BasicDataSource tempDataSource = (BasicDataSource) dataSource;
            if (tempDataSource.getDriverClassName().toLowerCase().contains("oracle")  && connection instanceof DelegatingConnection) {
                boolean canAccess = tempDataSource.isAccessToUnderlyingConnectionAllowed();
                if (!canAccess) {
                    tempDataSource.setAccessToUnderlyingConnectionAllowed(true);
                }
                
                DelegatingConnection tempConnection = (DelegatingConnection) connection;
                Connection innermostDelegate = tempConnection.getInnermostDelegate();
                if (!canAccess) {
                    tempDataSource.setAccessToUnderlyingConnectionAllowed(false);
                }
                return innermostDelegate;
            }
        }
        return connection;
    }
}
