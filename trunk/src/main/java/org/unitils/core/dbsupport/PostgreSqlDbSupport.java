/*
 * Copyright 2006-2007,  Unitils.org Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable
 * law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 * for the specific language governing permissions and limitations under the License.
 */
package org.unitils.core.dbsupport;

import java.util.Set;

/**
 * Implementation of {@link DbSupport} for a PostgreSql database.
 *
 * @author Tim Ducheyne
 * @author Sunteya
 * @author Filip Neven
 */
public class PostgreSqlDbSupport extends DbSupport {

    /**
     * Creates support for PostgreSql databases.
     */
    public PostgreSqlDbSupport() {
        super("postgresql");
    }


    /**
     * Returns the names of all tables in the database.
     *
     * @return The names of all tables in the database
     */
    @Override
    public Set<String> getTableNames() {
        return getSQLHandler().getItemsAsStringSet("select table_name from information_schema.tables where table_type = 'BASE TABLE' and table_schema = '" + getSchemaName() + "'");
    }


    /**
     * Gets the names of all columns of the given table.
     *
     * @param tableName The table, not null
     * @return The names of the columns of the table with the given name
     */
    @Override
    public Set<String> getColumnNames(String tableName) {
        return getSQLHandler().getItemsAsStringSet("select column_name from information_schema.columns where table_name = '" + tableName + "' and table_schema = '" + getSchemaName() + "'");
    }


    /**
     * Retrieves the names of all the views in the database schema.
     *
     * @return The names of all views in the database
     */
    @Override
    public Set<String> getViewNames() {
        return getSQLHandler().getItemsAsStringSet("select table_name from information_schema.tables where table_type = 'VIEW' and table_schema = '" + getSchemaName() + "'");
    }


    /**
     * Retrieves the names of all the sequences in the database schema.
     *
     * @return The names of all sequences in the database
     */
    @Override
    public Set<String> getSequenceNames() {
        // Patch from Dan Carleton submitted in forum post
        // http://sourceforge.net/forum/forum.php?thread_id=1708520&forum_id=570578
        // Should be replaced by the original query on information_schema.sequences in future, since this is a more elegant solution
        // This is the original query: getItemsAsStringSet("select sequence_name from information_schema.sequences where sequence_schema = '" + getSchemaName() + "'", getDataSource());
        return getSQLHandler().getItemsAsStringSet("select c.relname from pg_class c join pg_namespace n on (c.relnamespace = n.oid) where c.relkind = 'S' and n.nspname = '" + getSchemaName() + "'");
    }


    /**
     * Retrieves the names of all the triggers in the database schema.
     *
     * @return The names of all triggers in the database
     */
    @Override
    public Set<String> getTriggerNames() {
        return getSQLHandler().getItemsAsStringSet("select trigger_name from information_schema.triggers where trigger_schema = '" + getSchemaName() + "'");
    }


    /**
     * Retrieves the names of all user-defined types in the database schema.
     *
     * @return The names of all types in the database
     */
    @Override
    public Set<String> getTypeNames() {
        return getSQLHandler().getItemsAsStringSet("select object_name from information_schema.data_type_privileges where object_type = 'USER-DEFINED TYPE' and object_schema = '" + getSchemaName() + "'");
    }


    /**
     * Drops the type with the given name from the database Note: the type name is surrounded with
     * quotes, making it case-sensitive.
     *
     * @param typeName The type to drop (case-sensitive), not null
     */
    @Override
    public void dropType(String typeName) {
        getSQLHandler().executeCodeUpdate("drop type " + qualified(typeName) + " cascade");
    }


    /**
     * Removes all constraints on the specified table
     *
     * @param tableName The table with the column, not null
     */
    @Override
    public void disableConstraints(String tableName) {
        removeForeignKeyConstraints(tableName);
        removeNotNullConstraints(tableName);
    }


    /**
     * Returns the value of the sequence with the given name. <p/> Note: this can have the
     * side-effect of increasing the sequence value.
     *
     * @param sequenceName The sequence, not null
     * @return The value of the sequence with the given name
     */
    @Override
    public long getSequenceValue(String sequenceName) {
        return getSQLHandler().getItemAsLong("select last_value from " + qualified(sequenceName));
    }


    /**
     * Sets the next value of the sequence with the given sequence name to the given sequence value.
     *
     * @param sequenceName     The sequence, not null
     * @param newSequenceValue The value to set
     */
    @Override
    public void incrementSequenceToValue(String sequenceName, long newSequenceValue) {
        getSQLHandler().getItemAsLong("select setval('" + qualified(sequenceName) + "', " + newSequenceValue + ")");
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
     * Types are supported
     *
     * @return true
     */
    @Override
    public boolean supportsTypes() {
        return true;
    }

    // todo rewrite constraint disabling

    /**
     * Disables all foreign key constraints
     *
     * @param tableName The table, not null
     */
    protected void removeForeignKeyConstraints(String tableName) {
        Set<String> constraintNames = getForeignKeyConstraintNames(tableName);
        for (String constraintName : constraintNames) {
            removeForeignKeyConstraint(tableName, constraintName);
        }
    }


    /**
     * Disables all not-null constraints that are not of primary keys.
     *
     * @param tableName The table, not null
     */
    protected void removeNotNullConstraints(String tableName) {
        // Retrieve the name of the primary key, since we cannot remove the not-null constraint on this column
        Set<String> primaryKeyColumnNames = getPrimaryKeyColumnNames(tableName);

        Set<String> notNullColumnNames = getNotNullColummnNames(tableName);
        for (String notNullColumnName : notNullColumnNames) {
            if (primaryKeyColumnNames.contains(notNullColumnName)) {
                // Do not remove PK constraints
                continue;
            }
            removeNotNullConstraint(tableName, notNullColumnName);
        }
    }

    /**
     * Gets the names of all primary columns of the given table.
     *
     * @param tableName The table, not null
     * @return The names of the primary key columns of the table with the given name
     */
    protected Set<String> getPrimaryKeyColumnNames(String tableName) {
        return getSQLHandler().getItemsAsStringSet("select column_name from information_schema.table_constraints con, information_schema.key_column_usage key where con.table_name = '" + tableName + "' and con.table_schema = '"
                + getSchemaName() + "' and key.table_name = '" + tableName + "' and key.table_schema = '" + getSchemaName() + "' and con.constraint_type = 'PRIMARY KEY'");
    }


    /**
     * Returns the names of all columns that have a 'not-null' constraint on them
     *
     * @param tableName The table, not null
     * @return The set of column names, not null
     */
    protected Set<String> getNotNullColummnNames(String tableName) {
        return getSQLHandler().getItemsAsStringSet("select column_name from information_schema.columns where is_nullable = 'NO' and table_name = '" + tableName + "' and table_schema = '" + getSchemaName() + "'");
    }


    /**
     * Returns the foreign key constraint names that are enabled/enforced for the table with the
     * given name
     *
     * @param tableName The table, not null
     * @return The set of constraint names, not null
     */
    protected Set<String> getForeignKeyConstraintNames(String tableName) {
        return getSQLHandler().getItemsAsStringSet("select constraint_name from information_schema.table_constraints where table_name = '" + tableName + "' and constraint_type = 'FOREIGN KEY' and constraint_schema = '" + getSchemaName() + "'");
    }


    /**
     * Disables the constraint with the given name on table with the given name.
     *
     * @param tableName      The table with the constraint, not null
     * @param constraintName The constraint, not null
     */
    protected void removeForeignKeyConstraint(String tableName, String constraintName) {
        getSQLHandler().executeUpdate("alter table " + qualified(tableName) + " drop constraint \"" + constraintName + "\"");
    }


    /**
     * Removes the not-null constraint on the specified column and table
     *
     * @param tableName  The table with the column, not null
     * @param columnName The column to remove constraints from, not null
     */
    protected void removeNotNullConstraint(String tableName, String columnName) {
        getSQLHandler().executeUpdate("alter table " + qualified(tableName) + " alter column " + columnName + " drop not null");
    }
}
