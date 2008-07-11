/*
 * Copyright 2006-2007,  Unitils.org
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
package org.unitils.core.dbsupport;

import org.unitils.core.util.StoredIdentifierCase;
import static org.unitils.core.util.StoredIdentifierCase.LOWER_CASE;
import static org.unitils.core.util.StoredIdentifierCase.UPPER_CASE;

import java.util.Set;

/**
 * Implementation of {@link DbSupport} for a MySql database.
 * <p/>
 * Note: by default MySql uses '`' (back-quote) for quoting identifiers. '"' (double quotes) is only supported in MySql
 * if ANSI_QUOTES sql mode is enabled. Quoting identifiers does not make them case-sensitive. Case-sensitivity is
 * platform dependent. E.g. on UNIX systems identifiers will typically be case-sensitive, on Windows platforms they
 * will be converted to lower-case.
 * <p/>
 * Trigger names are an exception to this: they are always case-sensitive.
 *
 * @author Frederick Beernaert
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class MySqlDbSupport extends DbSupport {


    /**
     * Creates support for MySql databases.
     */
    public MySqlDbSupport() {
        super("mysql");
    }


    /**
     * Returns the names of all tables in the database.
     *
     * @return The names of all tables in the database
     */
    @Override
    public Set<String> getTableNames(String schemaName) {
        return getSQLHandler().getItemsAsStringSet("select table_name from information_schema.tables where table_schema = '" + schemaName + "' and table_type = 'BASE TABLE'", getDataSource());
    }


    /**
     * Gets the names of all columns of the given table.
     * @param tableName The table, not null
     *
     * @return The names of the columns of the table with the given name
     */
    @Override
    public Set<String> getColumnNames(String schemaName, String tableName) {
        return getSQLHandler().getItemsAsStringSet("select column_name from information_schema.columns where table_name = '" + tableName + "' and table_schema = '" + schemaName + "'", getDataSource());
    }


    /**
     * Retrieves the names of all the views in the database schema.
     *
     * @return The names of all views in the database
     */
    @Override
    public Set<String> getViewNames(String schemaName) {
        return getSQLHandler().getItemsAsStringSet("select table_name from information_schema.tables where table_schema = '" + schemaName + "' and table_type = 'VIEW'", getDataSource());
    }


    /**
     * Retrieves the names of all the triggers in the database schema.
     *
     * @return The names of all triggers in the database
     */
    @Override
    public Set<String> getTriggerNames(String schemaName) {
        return getSQLHandler().getItemsAsStringSet("select trigger_name from information_schema.triggers where trigger_schema = '" + schemaName + "'", getDataSource());
    }


    /**
     * Removes all referential constraints (e.g. foreign keys) on the specified table
     * @param tableName The table, not null
     */
    @Override
    public void removeReferentialConstraints(String schemaName, String tableName) {
        SQLHandler sqlHandler = getSQLHandler();
        Set<String> constraintNames = sqlHandler.getItemsAsStringSet("select constraint_name from information_schema.table_constraints where constraint_type = 'FOREIGN KEY' AND table_name = '" + tableName + "' and constraint_schema = '" + schemaName + "'", getDataSource());
        for (String constraintName : constraintNames) {
            sqlHandler.executeUpdate("alter table " + qualified(schemaName, tableName) + " drop foreign key " + quoted(constraintName), getDataSource());
        }
    }


    /**
     * Disables all value constraints (e.g. not null) on the specified table
     * @param tableName The table, not null
     */
    @Override
    public void removeValueConstraints(String schemaName, String tableName) {
        SQLHandler sqlHandler = getSQLHandler();

        // disable all unique constraints (check constraints are not implemented)
        Set<String> constraintNames = sqlHandler.getItemsAsStringSet("select constraint_name from information_schema.table_constraints where constraint_type in ('UNIQUE') AND table_name = '" + tableName + "' and constraint_schema = '" + schemaName + "'", getDataSource());
        for (String constraintName : constraintNames) {
            sqlHandler.executeUpdate("alter table " + qualified(schemaName, tableName) + " drop key " + quoted(constraintName), getDataSource());
        }

        // disable all not null constraints
        Set<String> notNullColumnNames = sqlHandler.getItemsAsStringSet("select column_name from information_schema.columns where is_nullable = 'NO' and column_key <> 'PRI' and table_name = '" + tableName + "' and table_schema = '" + schemaName + "'", getDataSource());
        for (String notNullColumnName : notNullColumnNames) {
            // todo test length etc
            String columnType = sqlHandler.getItemAsString("select column_type from information_schema.columns where table_schema = '" + schemaName + "' and table_name = '" + tableName + "' and column_name = '" + notNullColumnName + "'", getDataSource());
            sqlHandler.executeUpdate("alter table " + qualified(schemaName, tableName) + " change column " + quoted(notNullColumnName) + " " + quoted(notNullColumnName) + " " + columnType + " NULL ", getDataSource());
        }
    }


    /**
     * Gets the names of all identity columns of the given table.
     * @param tableName The table, not null
     *
     * @return The names of the identity columns of the table with the given name
     */
    @Override
    public Set<String> getIdentityColumnNames(String schemaName, String tableName) {
        //  todo check, at this moment the PK columns are returned
        return getSQLHandler().getItemsAsStringSet("select column_name from information_schema.columns where table_name = '" + tableName + "' and column_key = 'PRI' and table_schema = '" + schemaName + "'", getDataSource());
    }


    /**
     * Increments the identity value for the specified primary key on the specified table to the given value.
     * @param tableName            The table with the identity column, not null
     * @param identityValue        The new value
     * @param primaryKeyColumnName The column, not null
     */
    @Override
    public void incrementIdentityColumnToValue(String schemaName, String tableName, String primaryKeyColumnName, long identityValue) {
        getSQLHandler().executeUpdate("alter table " + qualified(schemaName, tableName) + " AUTO_INCREMENT = " + identityValue, getDataSource());
    }


    /**
     * Converts the given identifier to uppercase/lowercase
     * <p/>
     * MySql does not treat quoted identifiers as case sensitive. These will also be converted to the correct case.
     * <p/>
     * KNOWN ISSUE: MySql trigger names are case-sensitive (even if not quoted). This will incorrectly be converted to
     * the stored identifier case
     *
     * @param identifier The identifier, not null
     * @return The name converted to correct case if needed, not null
     */
    @Override
    public String toCorrectCaseIdentifier(String identifier) {
        identifier = identifier.trim();
        String identifierQuoteString = getIdentifierQuoteString();
        if (identifier.startsWith(identifierQuoteString) && identifier.endsWith(identifierQuoteString)) {
            identifier = identifier.substring(1, identifier.length() - 1);
        }

        StoredIdentifierCase storedIdentifierCase = getStoredIdentifierCase();
        if (storedIdentifierCase == UPPER_CASE) {
            return identifier.toUpperCase();
        } else if (storedIdentifierCase == LOWER_CASE) {
            return identifier.toLowerCase();
        } else {
            return identifier;
        }
    }


    /**
     * Triggers are supported.
     *
     * @return True
     */
    @Override
    public boolean supportsTriggers() {
        return true;
    }


    /**
     * Identity columns are supported.
     *
     * @return True
     */
    @Override
    public boolean supportsIdentityColumns() {
        return true;
    }


    /**
     * Cascade are supported.
     *
     * @return True
     */
    @Override
    public boolean supportsCascade() {
        return true;
    }

}