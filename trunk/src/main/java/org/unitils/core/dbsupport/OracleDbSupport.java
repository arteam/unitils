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

import org.unitils.core.UnitilsException;
import static org.unitils.thirdparty.org.apache.commons.dbutils.DbUtils.closeQuietly;

import java.sql.*;
import java.util.Set;

/**
 * Implementation of {@link DbSupport} for an Oracle database.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class OracleDbSupport extends DbSupport {

    /* The major version number of the Oracle database */
    private Integer oracleMajorVersionNumber;


    /**
     * Creates support for Oracle databases.
     */
    public OracleDbSupport() {
        super("oracle");
    }


    /**
     * Returns the names of all tables in the database.
     *
     * @return The names of all tables in the database
     */
    @Override
    public Set<String> getTableNames() {
        // all_tables also contains the materialized views: don't return these
        return getSQLHandler().getItemsAsStringSet("select TABLE_NAME from ALL_TABLES where OWNER = '" + getSchemaName() + "' minus select MVIEW_NAME from ALL_MVIEWS where OWNER = '" + getSchemaName() + "'");
    }


    /**
     * Gets the names of all columns of the given table.
     *
     * @param tableName The table, not null
     * @return The names of the columns of the table with the given name
     */
    @Override
    public Set<String> getColumnNames(String tableName) {
        return getSQLHandler().getItemsAsStringSet("select COLUMN_NAME from ALL_TAB_COLUMNS where TABLE_NAME = '" + tableName + "' and OWNER = '" + getSchemaName() + "'");
    }


    /**
     * Retrieves the names of all views in the database schema.
     *
     * @return The names of all views in the database
     */
    @Override
    public Set<String> getViewNames() {
        return getSQLHandler().getItemsAsStringSet("select VIEW_NAME from ALL_VIEWS where OWNER = '" + getSchemaName() + "'");
    }


    /**
     * Retrieves the names of all materialized views in the database schema.
     *
     * @return The names of all materialized views in the database
     */
    @Override
    public Set<String> getMaterializedViewNames() {
        return getSQLHandler().getItemsAsStringSet("select MVIEW_NAME from ALL_MVIEWS where OWNER = '" + getSchemaName() + "'");
    }


    /**
     * Retrieves the names of all synonyms in the database schema.
     *
     * @return The names of all synonyms in the database
     */
    @Override
    public Set<String> getSynonymNames() {
        return getSQLHandler().getItemsAsStringSet("select SYNONYM_NAME from ALL_SYNONYMS where OWNER = '" + getSchemaName() + "'");
    }


    /**
     * Retrieves the names of all sequences in the database schema.
     *
     * @return The names of all sequences in the database
     */
    @Override
    public Set<String> getSequenceNames() {
        return getSQLHandler().getItemsAsStringSet("select SEQUENCE_NAME from ALL_SEQUENCES where SEQUENCE_OWNER = '" + getSchemaName() + "'");
    }


    /**
     * Retrieves the names of all triggers in the database schema.
     *
     * @return The names of all triggers in the database
     */
    @Override
    public Set<String> getTriggerNames() {
        return getSQLHandler().getItemsAsStringSet("select TRIGGER_NAME from ALL_TRIGGERS where OWNER = '" + getSchemaName() + "'");
    }


    /**
     * Retrieves the names of all the types in the database schema.
     *
     * @return The names of all types in the database
     */
    @Override
    public Set<String> getTypeNames() {
        return getSQLHandler().getItemsAsStringSet("select TYPE_NAME from ALL_TYPES where OWNER = '" + getSchemaName() + "'");
    }


    /**
     * Removes the table with the given name from the database.
     * Note: the table name is surrounded with quotes, making it case-sensitive.
     *
     * @param tableName The table to drop (case-sensitive), not null
     */
    @Override
    public void dropTable(String tableName) {
        getSQLHandler().executeUpdate("drop table " + qualified(tableName) + " cascade constraints" + (supportsPurge() ? " purge" : ""));
    }


    /**
     * Removes the view with the given name from the database
     * Note: the view name is surrounded with quotes, making it case-sensitive.
     *
     * @param viewName The view to drop (case-sensitive), not null
     */
    @Override
    public void dropView(String viewName) {
        getSQLHandler().executeUpdate("drop view " + qualified(viewName) + " cascade constraints");
    }


    /**
     * Removes the materialized view with the given name from the database
     * Note: the view name is surrounded with quotes, making it case-sensitive.
     *
     * @param materializedViewName The view to drop (case-sensitive), not null
     */
    @Override
    public void dropMaterializedView(String materializedViewName) {
        getSQLHandler().executeUpdate("drop materialized view " + qualified(materializedViewName));
    }


    /**
     * Drops the type with the given name from the database
     * Note: the type name is surrounded with quotes, making it case-sensitive.
     * <p/>
     * Overriden to add the force option. This will make sure that super-types can also be dropped.
     *
     * @param typeName The type to drop (case-sensitive), not null
     */
    @Override
    public void dropType(String typeName) {
        getSQLHandler().executeCodeUpdate("drop type " + qualified(typeName) + " force");
    }


    /**
     * Removes all referential constraints (e.g. foreign keys) on the specified table
     *
     * @param tableName The table, not null
     */
    @Override
    public void removeReferentialConstraints(String tableName) {
        SQLHandler sqlHandler = getSQLHandler();
        Set<String> constraintNames = sqlHandler.getItemsAsStringSet("select CONSTRAINT_NAME from ALL_CONSTRAINTS where CONSTRAINT_TYPE = 'R' and TABLE_NAME = '" + tableName + "' and OWNER = '" + getSchemaName() + "'");
        for (String constraintName : constraintNames) {
            sqlHandler.executeUpdate("alter table " + qualified(tableName) + " drop constraint " + quoted(constraintName));
        }
    }


    /**
     * Disables all value constraints (e.g. not null) on the specified table
     *
     * @param tableName The table, not null
     */
    @Override
    public void removeValueConstraints(String tableName) {
        SQLHandler sqlHandler = getSQLHandler();
        Set<String> constraintNames = sqlHandler.getItemsAsStringSet("select CONSTRAINT_NAME from ALL_CONSTRAINTS where CONSTRAINT_TYPE in ('U', 'C', 'V', 'O') and TABLE_NAME = '" + tableName + "' and OWNER = '" + getSchemaName() + "'");
        for (String constraintName : constraintNames) {
            sqlHandler.executeUpdate("alter table " + qualified(tableName) + " drop constraint " + quoted(constraintName));
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
        return getSQLHandler().getItemAsLong("select LAST_NUMBER from ALL_SEQUENCES where SEQUENCE_NAME = '" + sequenceName + "' and SEQUENCE_OWNER = '" + getSchemaName() + "'");
    }


    /**
     * Sets the next value of the sequence with the given sequence name to the given sequence value.
     *
     * @param sequenceName     The sequence, not null
     * @param newSequenceValue The value to set
     */
    @Override
    public void incrementSequenceToValue(String sequenceName, long newSequenceValue) {
        Connection connection = null;
        ResultSet resultSet = null;
        Statement statement = null;
        try {
            connection = getSQLHandler().getDataSource().getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("select LAST_NUMBER, INCREMENT_BY from ALL_SEQUENCES where SEQUENCE_NAME = '" + sequenceName + "' and SEQUENCE_OWNER = '" + getSchemaName() + "'");
            while (resultSet.next()) {
                long lastNumber = resultSet.getLong("LAST_NUMBER");
                long incrementBy = resultSet.getLong("INCREMENT_BY");
                // change the increment
                getSQLHandler().executeUpdate("alter sequence " + qualified(sequenceName) + " increment by " + (newSequenceValue - lastNumber));
                // select the increment
                getSQLHandler().executeUpdate("select " + qualified(sequenceName) + ".NEXTVAL from DUAL");
                // set back old increment
                getSQLHandler().executeUpdate("alter sequence " + qualified(sequenceName) + " increment by " + incrementBy);
            }
        } catch (SQLException e) {
            throw new UnitilsException("Error while incrementing sequence to value", e);
        } finally {
            closeQuietly(connection, statement, resultSet);
        }
    }


    /**
     * Gets the column type suitable to store values of the Java <code>java.lang.Long</code> type.
     *
     * @return The column type
     */
    @Override
    public String getLongDataType() {
        return "INTEGER";
    }


    /**
     * Gets the column type suitable to store text values.
     *
     * @param length The nr of characters.
     * @return The column type, not null
     */
    @Override
    public String getTextDataType(int length) {
        return "VARCHAR2(" + length + ")";
    }


    /**
     * Synonyms are supported
     *
     * @return True
     */
    @Override
    public boolean supportsSynonyms() {
        return true;
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


    /**
     * Materialized views are supported
     *
     * @return true
     */
    @Override
    public boolean supportsMaterializedViews() {
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


    /**
     * @return Whether or not this version of the Oracle database that is used supports the purge keyword. This is,
     *         whether or not an Oracle database of version 10 or higher is used.
     */
    protected boolean supportsPurge() {
        return getOracleMajorVersionNumber() >= 10;
    }


    /**
     * @return The major version number of the Oracle database server that is used (e.g. for Oracle version 9.2.0.1, 9 is returned
     */
    protected Integer getOracleMajorVersionNumber() {
        if (oracleMajorVersionNumber == null) {
            Connection connection = null;
            try {
                connection = getSQLHandler().getDataSource().getConnection();
                DatabaseMetaData metaData = connection.getMetaData();
                oracleMajorVersionNumber = metaData.getDatabaseMajorVersion();
            } catch (SQLException e) {
                throw new UnitilsException("Unable to determine database major version", e);
            } finally {
                closeQuietly(connection);
            }
        }
        return oracleMajorVersionNumber;
    }
}