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

import java.util.Set;

/**
 * Implementation of {@link DbSupport} for an IBM DB2 database
 *
 * @author Tim Ducheyne
 * @author Tuomas Jormola
 * @author Frederick Beernaert
 * @author Filip Neven
 */
public class Db2DbSupport extends DbSupport {


    /**
     * Creates support for Db2 databases.
     */
    public Db2DbSupport() {
        super("db2");
    }


    /**
     * Returns the names of all tables in the database.
     * <p/>
     * TODO check table types
     * A = Alias G = Global temporary table H = Hierarchy table L = Detached table N = Nickname S = Materialized query table T = Table (untyped) U = Typed table V = View (untyped) W = Typed view
     *
     * @return The names of all tables in the database
     */
    @Override
    public Set<String> getTableNames() {
        return getSQLHandler().getItemsAsStringSet("select TABNAME from SYSCAT.TABLES where TABSCHEMA = '" + getSchemaName() + "' and TYPE = 'T'");
    }


    /**
     * Gets the names of all columns of the given table.
     *
     * @param tableName The table, not null
     * @return The names of the columns of the table with the given name
     */
    @Override
    public Set<String> getColumnNames(String tableName) {
        return getSQLHandler().getItemsAsStringSet("select COLNAME from SYSCAT.COLUMNS where TABNAME = '" + tableName + "' and TABSCHEMA = '" + getSchemaName() + "'");
    }


    /**
     * Gets the names of all primary columns of the given table.
     *
     * @param tableName The table, not null
     * @return The names of the primary key columns of the table with the given name
     */
    @Override
    public Set<String> getPrimaryKeyColumnNames(String tableName) {
        return getSQLHandler().getItemsAsStringSet("select COLNAME from SYSCAT.COLUMNS where KEYSEQ is not null and TABNAME = '" + tableName + "' and TABSCHEMA = '" + getSchemaName() + "'");
    }


    /**
     * Returns the names of all columns that have a 'not-null' constraint on them
     *
     * @param tableName The table, not null
     * @return The set of column names, not null
     */
    @Override
    public Set<String> getNotNullColummnNames(String tableName) {
        return getSQLHandler().getItemsAsStringSet("select COLNAME from SYSCAT.COLUMNS where NULLS = 'N' and TABNAME = '" + tableName + "' and TABSCHEMA = '" + getSchemaName() + "'");
    }


    /**
     * Retrieves the names of all the views in the database schema.
     * <p/>
     * TODO check view types
     * V = View (untyped) W = Typed view
     *
     * @return The names of all views in the database
     */
    @Override
    public Set<String> getViewNames() {
        return getSQLHandler().getItemsAsStringSet("select TABNAME from SYSCAT.TABLES where TABSCHEMA = '" + getSchemaName() + "' and TYPE = 'V'");
    }


    /**
     * Retrieves the names of all the sequences in the database schema.
     *
     * @return The names of all sequences in the database
     */
    @Override
    public Set<String> getSequenceNames() {
        return getSQLHandler().getItemsAsStringSet("select SEQNAME from SYSCAT.SEQUENCES where SEQTYPE = 'S' AND SEQSCHEMA = '" + getSchemaName() + "'");
    }


    /**
     * Retrieves the names of all the triggers in the database schema.
     *
     * @return The names of all triggers in the database
     */
    @Override
    public Set<String> getTriggerNames() {
        return getSQLHandler().getItemsAsStringSet("select TRIGNAME from SYSCAT.TRIGGERS where TRIGSCHEMA = '" + getSchemaName() + "'");
    }


    /**
     * Retrieves the names of all the types in the database schema.
     *
     * @return The names of all types in the database
     */
    @Override
    public Set<String> getTypeNames() {
        return getSQLHandler().getItemsAsStringSet("select TYPENAME from SYSCAT.DATATYPES where TYPESCHEMA = '" + getSchemaName() + "'");
    }


    /**
     * Removes the table with the given name from the database.
     * Note: the table name is surrounded with quotes, making it case-sensitive.
     *
     * @param tableName The table to drop (case-sensitive), not null
     */
    public void dropTable(String tableName) {
        getSQLHandler().executeUpdate("drop table " + qualified(tableName));
    }


    /**
     * Removes the view with the given name from the database
     * Note: the view name is surrounded with quotes, making it case-sensitive.
     *
     * @param viewName The view to drop (case-sensitive), not null
     */
    public void dropView(String viewName) {
        getSQLHandler().executeUpdate("drop view " + qualified(viewName));
    }


    /**
     * Drops the type with the given name from the database
     * Note: the type name is surrounded with quotes, making it case-sensitive.
     *
     * @param typeName The type to drop (case-sensitive), not null
     */
    @Override
    public void dropType(String typeName) {
        getSQLHandler().executeUpdate("drop type " + qualified(typeName));
    }


    /**
     * todo rename + javadoc about currval and nextval  calling this method has the side effect of increasing the nr
     * <p/>
     * <p/>
     * Returns the value of the sequence with the given name
     *
     * @param sequenceName The sequence, not null
     * @return The value of the sequence with the given name
     */
    @Override
    public long getCurrentValueOfSequence(String sequenceName) {
        return getSQLHandler().getItemAsLong("select next value for " + qualified(sequenceName) + " from SYSIBM.SYSDUMMY1");
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
     * Increments the identity value for the specified identity column on the specified table to the given value. If there
     * is no identity specified on the given primary key, the method silently finishes without effect.
     *
     * @param tableName          The table with the identity column, not null
     * @param identityColumnName The column, not null
     * @param identityValue      The new value
     */
    public void incrementIdentityColumnToValue(String tableName, String identityColumnName, long identityValue) {
        getSQLHandler().executeUpdate("alter table " + qualified(tableName) + " alter column " + identityColumnName + " restart with " + identityValue);
    }


    /**
     * Returns the foreign key constraint names that are enabled/enforced for the table with the given name
     *
     * @param tableName The table, not null
     * @return The set of constraint names, not null
     */
    @Override
    public Set<String> getForeignKeyConstraintNames(String tableName) {
        return getSQLHandler().getItemsAsStringSet("select CONSTNAME from SYSCAT.TABCONST where TYPE = 'F' and TABNAME = '" + tableName + "' and TABSCHEMA = '" + getSchemaName() + "'");
    }


    /**
     * Disables the constraint with the given name on table with the given name.
     *
     * @param tableName      The table with the constraint, not null
     * @param constraintName The constraint, not null
     */
    @Override
    public void removeForeignKeyConstraint(String tableName, String constraintName) {
        getSQLHandler().executeUpdate("alter table " + qualified(tableName) + " drop constraint " + constraintName);
    }


    /**
     * Removes the not-null constraint on the specified column and table
     *
     * @param tableName  The table with the column, not null
     * @param columnName The column to remove constraints from, not null
     */
    @Override
    public void removeNotNullConstraint(String tableName, String columnName) {
        getSQLHandler().executeUpdate("alter table " + qualified(tableName) + " alter column " + columnName + " drop not null");
        getSQLHandler().executeUpdate("call SYSPROC.ADMIN_CMD('REORG TABLE " + qualified(tableName) + "')");
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
     * Types are supported
     *
     * @return true
     */
    @Override
    public boolean supportsTypes() {
        return true;
    }
}