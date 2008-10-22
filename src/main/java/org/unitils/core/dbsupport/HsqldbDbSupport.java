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
package org.unitils.core.dbsupport;

import java.util.Set;

/**
 * Implementation of {@link DbSupport} for a hsqldb database
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class HsqldbDbSupport extends DbSupport {

    /**
     * Creates support for HsqlDb databases.
     */
    public HsqldbDbSupport() {
        super("hsqldb");
    }


    /**
     * Returns the names of all tables in the database.
     *
     * @return The names of all tables in the database
     */
    @Override
    public Set<String> getTableNames() {
        return getSQLHandler().getItemsAsStringSet("select TABLE_NAME from INFORMATION_SCHEMA.SYSTEM_TABLES where TABLE_TYPE = 'TABLE' AND TABLE_SCHEM = '" + getSchemaName() + "'");
    }


    /**
     * Gets the names of all columns of the given table.
     *
     * @param tableName The table, not null
     * @return The names of the columns of the table with the given name
     */
    @Override
    public Set<String> getColumnNames(String tableName) {
        return getSQLHandler().getItemsAsStringSet("select COLUMN_NAME from INFORMATION_SCHEMA.SYSTEM_COLUMNS where TABLE_NAME = '" + tableName + "' AND TABLE_SCHEM = '" + getSchemaName() + "'");
    }


    /**
     * Retrieves the names of all the views in the database schema.
     *
     * @return The names of all views in the database
     */
    @Override
    public Set<String> getViewNames() {
        return getSQLHandler().getItemsAsStringSet("select TABLE_NAME from INFORMATION_SCHEMA.SYSTEM_TABLES where TABLE_TYPE = 'VIEW' AND TABLE_SCHEM = '" + getSchemaName() + "'");
    }


    /**
     * Retrieves the names of all the sequences in the database schema.
     *
     * @return The names of all sequences in the database
     */
    @Override
    public Set<String> getSequenceNames() {
        return getSQLHandler().getItemsAsStringSet("select SEQUENCE_NAME from INFORMATION_SCHEMA.SYSTEM_SEQUENCES where SEQUENCE_SCHEMA = '" + getSchemaName() + "'");
    }


    /**
     * Retrieves the names of all the triggers in the database schema.
     *
     * @return The names of all triggers in the database
     */
    @Override
    public Set<String> getTriggerNames() {
        return getSQLHandler().getItemsAsStringSet("select TRIGGER_NAME from INFORMATION_SCHEMA.SYSTEM_TRIGGERS where TRIGGER_SCHEM = '" + getSchemaName() + "'");
    }


    /**
     * Disables all referential constraints (e.g. foreign keys) on all table in the schema
     */
    @Override
    public void disableReferentialConstraints() {
        SQLHandler sqlHandler = getSQLHandler();
        Set<String[]> tableAndConstraintNames = sqlHandler.getAllItemsAsStringSet("select TABLE_NAME, CONSTRAINT_NAME from INFORMATION_SCHEMA.SYSTEM_TABLE_CONSTRAINTS where CONSTRAINT_TYPE = 'FOREIGN KEY' AND CONSTRAINT_SCHEMA = '" + getSchemaName() + "'");
        for (String[] tableAndConstraintName : tableAndConstraintNames) {
            sqlHandler.executeUpdate("alter table " + qualified(tableAndConstraintName[0]) + " drop constraint " + quoted(tableAndConstraintName[1]));
        }
    }


    /**
     * Disables all value constraints (e.g. not null) on all tables in the schema
     */
    @Override
    public void disableValueConstraints() {
        SQLHandler sqlHandler = getSQLHandler();
        Set<String[]> tableAndConstraintNames = sqlHandler.getAllItemsAsStringSet("select TABLE_NAME, CONSTRAINT_NAME from INFORMATION_SCHEMA.SYSTEM_TABLE_CONSTRAINTS where CONSTRAINT_TYPE IN ('CHECK', 'UNIQUE') AND CONSTRAINT_SCHEMA = '" + getSchemaName() + "'");
        for (String[] tableAndConstraintName : tableAndConstraintNames) {
            sqlHandler.executeUpdate("alter table " + qualified(tableAndConstraintName[0]) + " drop constraint " + quoted(tableAndConstraintName[1]));
        }

        Set<String[]> tableAndNotNullColumnNames = sqlHandler.getAllItemsAsStringSet("select TABLE_NAME, COLUMN_NAME from INFORMATION_SCHEMA.SYSTEM_COLUMNS where IS_NULLABLE = 'NO' AND TABLE_SCHEM = '" + getSchemaName() + "'");
        for (String[] tableAndNotNullColumnName : tableAndNotNullColumnNames) {
            String tableName = tableAndNotNullColumnName[0];
            String columnName = tableAndNotNullColumnName[1];
            Set<String> primaryKeyColumnNames = sqlHandler.getItemsAsStringSet("select COLUMN_NAME from INFORMATION_SCHEMA.SYSTEM_PRIMARYKEYS where TABLE_NAME = '" + tableName + "' AND TABLE_SCHEM = '" + getSchemaName() + "'");
            if (primaryKeyColumnNames.contains(columnName)) {
                // Do not remove PK constraints
                continue;
            }
            sqlHandler.executeUpdate("alter table " + qualified(tableName) + " alter column " + quoted(columnName) + " set null");
        }
    }


    /**
     * Returns the value of the sequence with the given name.
     * <p/>
     * Note: this can have the side-effect of increasing the sequence value.
     *
     * @param sequenceName The sequence, not null
     * @return The value of the sequence with the given name
     */
    @Override
    public long getSequenceValue(String sequenceName) {
        return getSQLHandler().getItemAsLong("select START_WITH from INFORMATION_SCHEMA.SYSTEM_SEQUENCES where SEQUENCE_SCHEMA = '" + getSchemaName() + "' and SEQUENCE_NAME = '" + sequenceName + "'");
    }


    /**
     * Sets the next value of the sequence with the given sequence name to the given sequence value.
     *
     * @param sequenceName     The sequence, not null
     * @param newSequenceValue The value to set
     */
    @Override
    public void incrementSequenceToValue(String sequenceName, long newSequenceValue) {
        getSQLHandler().executeUpdate("alter sequence " + qualified(sequenceName) + " restart with " + newSequenceValue);
    }


    /**
     * Gets the names of all identity columns of the given table.
     * <p/>
     * todo check, at this moment the PK columns are returned
     *
     * @param tableName The table, not null
     * @return The names of the identity columns of the table with the given name
     */
    @Override
    public Set<String> getIdentityColumnNames(String tableName) {
        return getSQLHandler().getItemsAsStringSet("select COLUMN_NAME from INFORMATION_SCHEMA.SYSTEM_PRIMARYKEYS where TABLE_NAME = '" + tableName + "' AND TABLE_SCHEM = '" + getSchemaName() + "'");
    }


    /**
     * Increments the identity value for the specified identity column on the specified table to the given value.
     *
     * @param tableName          The table with the identity column, not null
     * @param identityColumnName The column, not null
     * @param identityValue      The new value
     */
    @Override
    public void incrementIdentityColumnToValue(String tableName, String identityColumnName, long identityValue) {
        getSQLHandler().executeUpdate("alter table " + qualified(tableName) + " alter column " + quoted(identityColumnName) + " RESTART WITH " + identityValue);
    }


    /**
     * Sequences are supported.
     *
     * @return True
     */
    @Override
    public boolean supportsSequences() {
        return true;
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