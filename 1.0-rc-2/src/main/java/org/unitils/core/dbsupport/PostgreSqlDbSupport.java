/*
 * Copyright 2006 the original author or authors.
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

import static org.unitils.core.util.SQLUtils.*;

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
        return getItemsAsStringSet("select table_name from information_schema.tables where table_type = 'BASE TABLE' and table_schema = '" + getSchemaName() + "'", getDataSource());
    }


    /**
     * Retrieves the names of all the views in the database schema.
     *
     * @return The names of all views in the database
     */
    @Override
    public Set<String> getViewNames() {
        return getItemsAsStringSet("select table_name from information_schema.tables where table_type = 'VIEW' and table_schema = '" + getSchemaName() + "'", getDataSource());
    }


    /**
     * Retrieves the names of all the sequences in the database schema.
     *
     * @return The names of all sequences in the database
     */
    @Override
    public Set<String> getSequenceNames() {
        // Patch from Dan Carleton submitted in forum post http://sourceforge.net/forum/forum.php?thread_id=1708520&forum_id=570578
        // Should be replaced by the original query on information_schema.sequences in future, since this is a more elegant solution
        // This is the original query:
        // getItemsAsStringSet("select sequence_name from information_schema.sequences where sequence_schema = '" + getSchemaName() + "'", getDataSource());
        return getItemsAsStringSet("select c.relname from pg_class c join pg_namespace n on (c.relnamespace = n.oid) where c.relkind = 'S' and n.nspname = '" + getSchemaName() + "'", getDataSource());
    }


    /**
     * Retrieves the names of all the triggers in the database schema.
     *
     * @return The names of all triggers in the database
     */
    @Override
    public Set<String> getTriggerNames() {
        return getItemsAsStringSet("select trigger_name from information_schema.triggers where trigger_schema = '" + getSchemaName() + "'", getDataSource());
    }


    /**
     * Retrieves the names of all user-defined types in the database schema.
     *
     * @return The names of all types in the database
     */
    @Override
    public Set<String> getTypeNames() {
        return getItemsAsStringSet("select object_name from information_schema.data_type_privileges where object_type = 'USER-DEFINED TYPE' and object_schema = '" + getSchemaName() + "'", getDataSource());
    }


    /**
     * Returns the foreign key constraint names that are enabled/enforced for the table with the given name
     *
     * @param tableName The table, not null
     * @return The set of constraint names, not null
     */
    @Override
    public Set<String> getTableConstraintNames(String tableName) {
        return getItemsAsStringSet("select constraint_name from information_schema.table_constraints where table_name = '" + tableName + "' and constraint_type = 'FOREIGN KEY' and constraint_schema = '" + getSchemaName() + "'", getDataSource());
    }


    /**
     * Drops the type with the given name from the database
     * Note: the type name is surrounded with quotes, making it case-sensitive.
     *
     * @param typeName The type to drop (case-sensitive), not null
     */
    @Override
    public void dropType(String typeName) {
        executeUpdate("drop type " + qualified(typeName) + " cascade", getDataSource());
    }


    /**
     * Returns the value of the sequence with the given name
     *
     * @param sequenceName The sequence, not null
     * @return The value of the sequence with the given name
     */
    @Override
    public long getCurrentValueOfSequence(String sequenceName) {
        return getItemAsLong("select last_value from " + qualified(sequenceName), getDataSource());
    }


    /**
     * Sets the next value of the sequence with the given sequence name to the given sequence value.
     *
     * @param sequenceName     The sequence, not null
     * @param newSequenceValue The value to set
     */
    @Override
    public void incrementSequenceToValue(String sequenceName, long newSequenceValue) {
        getItemAsLong("select setval('" + qualified(sequenceName) + "', " + newSequenceValue + ")", getDataSource());
    }


    /**
     * Removes the not-null constraint on the specified column and table
     *
     * @param tableName  The table with the column, not null
     * @param columnName The column to remove constraints from, not null
     */
    @Override
    public void removeNotNullConstraint(String tableName, String columnName) {
        executeUpdate("alter table " + qualified(tableName) + " alter column " + columnName + " drop not null", getDataSource());
    }


    /**
     * Disables the constraint with the given name on table with the given name.
     *
     * @param tableName      The table with the constraint, not null
     * @param constraintName The constraint, not null
     */
    @Override
    public void disableConstraint(String tableName, String constraintName) {
        executeUpdate("alter table " + qualified(tableName) + " drop constraint \"" + constraintName + "\"", getDataSource());
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
}
