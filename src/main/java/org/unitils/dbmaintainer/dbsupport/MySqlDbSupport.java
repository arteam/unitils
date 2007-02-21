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
package org.unitils.dbmaintainer.dbsupport;

import org.apache.commons.dbutils.DbUtils;
import org.unitils.core.UnitilsException;
import org.unitils.dbmaintainer.script.impl.StatementHandlerException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of {@link DbSupport} for a MySql database
 *
 * @author Frederick Beernaert
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class MySqlDbSupport extends DbSupport {


    /**
     * Returns the names of all tables in the database.
     *
     * @return The names of all tables in the database
     */
    public Set<String> getTableNames() {
        Connection conn = null;
        ResultSet rset = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            rset = st.executeQuery("select TABLE_NAME from INFORMATION_SCHEMA.TABLES where TABLE_SCHEMA = '" + schemaName + "' and TABLE_TYPE = 'BASE TABLE'");
            Set<String> names = new HashSet<String>();
            while (rset.next()) {
                names.add(rset.getString("TABLE_NAME"));
            }
            return names;

        } catch (SQLException e) {
            throw new UnitilsException("Error while looking up table names", e);
        } finally {
            DbUtils.closeQuietly(conn, st, rset);
        }
    }


    /**
     * Retrieves the names of all the views in the database schema.
     *
     * @return The names of all views in the database
     */
    public Set<String> getViewNames() {
        Connection conn = null;
        ResultSet rset = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            rset = st.executeQuery("select TABLE_NAME from INFORMATION_SCHEMA.TABLES where TABLE_SCHEMA = '" + schemaName + "' and TABLE_TYPE = 'VIEW'");
            Set<String> names = new HashSet<String>();
            while (rset.next()) {
                names.add(rset.getString("TABLE_NAME"));
            }
            return names;

        } catch (SQLException e) {
            throw new UnitilsException("Error while looking up view names", e);
        } finally {
            DbUtils.closeQuietly(conn, st, rset);
        }
    }

    /**
     * todo implement
     */
    public Set<String> getSynonymNames() {
        throw new UnsupportedOperationException("Synonyms not yet implemented for mysql");
    }


    /**
     * Sequences are not supported, an UnsupportedOperationException will be raised.
     *
     * @return Nothing
     */
    public Set<String> getSequenceNames() {
        throw new UnsupportedOperationException("Sequences are not supported in MySQL");
    }


    /**
     * Retrieves the names of all the triggers in the database schema.
     *
     * @return The names of all triggers in the database
     */
    public Set<String> getTriggerNames() {
        Connection conn = null;
        ResultSet rset = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            rset = st.executeQuery("select TRIGGER_NAME from INFORMATION_SCHEMA.TRIGGERS where TRIGGER_SCHEMA = '" + schemaName + "'");
            Set<String> names = new HashSet<String>();
            while (rset.next()) {
                names.add(rset.getString("TRIGGER_NAME"));
            }
            return names;

        } catch (SQLException e) {
            throw new UnitilsException("Error while looking trigger names", e);
        } finally {
            DbUtils.closeQuietly(conn, st, rset);
        }
    }


    /**
     * Types are not supported: an UnsupportedOperationException will be raised.
     */
    public Set<String> getTypeNames() {
        throw new UnsupportedOperationException("Mysql doesn't support types");
    }

    public Set<String> getDbLinkNames() {
        throw new UnsupportedOperationException("Mysql doesn't support db links");
    }


    /**
     * Types are not supported: an UnsupportedOperationException will be raised.
     *
     * @param typeName The type to drop (case-sensitive), not null
     */
    public void dropType(String typeName) throws StatementHandlerException {
        throw new UnsupportedOperationException("Mysql doesn't support types");
    }


    /**
     * Types are not supported: an UnsupportedOperationException will be raised.
     *
     * @param dbLinkName The db link to drop (case-sensitive), not null
     */
    public void dropDbLink(String dbLinkName) {
        throw new UnsupportedOperationException("Mysql doesn't support types");
    }


    /**
     * Sequences are not supported, an UnsupportedOperationException will be raised.
     *
     * @return Nothing
     */
    public long getCurrentValueOfSequence(String sequenceName) {
        throw new UnsupportedOperationException("Sequences are not supported in MySQL");
    }


    /**
     * Sequences are not supported, an UnsupportedOperationException will be raised.
     *
     * @param sequenceName     The sequence, not null
     * @param newSequenceValue The value to set
     */
    public void incrementSequenceToValue(String sequenceName, long newSequenceValue) throws StatementHandlerException {
        throw new UnsupportedOperationException("Sequences are not supported in MySQL");
    }

    /**
     * Synonyms are not supported
     *
     * @return False
     */
    public boolean supportsSynonyms() {
        return false;
    }


    /**
     * Sequences are not supported.
     *
     * @return False
     */
    public boolean supportsSequences() {
        return false;
    }


    /**
     * Triggers are supported.
     *
     * @return True
     */
    public boolean supportsTriggers() {
        return true;
    }


    /**
     * Identity columns are supported.
     *
     * @return True
     */
    public boolean supportsIdentityColumns() {
        return true;
    }


    /**
     * Types are not supported
     *
     * @return false
     */
    public boolean supportsTypes() {
        return false;
    }


    /**
     * Support for db links is currently not implemented for mysql
     *
     * @return false
     */
    public boolean supportsDbLinks() {
        return false;
    }


    /**
     * Increments the identity value for the specified primary key on the specified table to the given value. If there
     * is no identity specified on the given primary key, the method silently finishes without effect.
     *
     * @param tableName            The table with the identity column, not null
     * @param primaryKeyColumnName The column, not null
     * @param identityValue        The new value
     */
    public void incrementIdentityColumnToValue(String tableName, String primaryKeyColumnName, long identityValue) {
        try {
            statementHandler.handle("alter table " + qualified(tableName) + " AUTO_INCREMENT = " + identityValue);

        } catch (StatementHandlerException e) {
            throw new UnitilsException(e);
        }
    }


    /**
     * Disables foreign key checking on all subsequent operations that are performed on the given connection object
     *
     * @param connection The database connection, not null
     */
    public void disableForeignKeyConstraintsCheckingOnConnection(Connection connection) {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");

        } catch (SQLException e) {
            throw new UnitilsException("Error while disabling constraints", e);
        } finally {
            DbUtils.closeQuietly(statement);
        }
    }


    /**
     * Removes the not-null constraint on the specified column and table
     *
     * @param tableName  The table with the column, not null
     * @param columnName The column to remove constraints from, not null
     */
    public void removeNotNullConstraint(String tableName, String columnName) throws StatementHandlerException {
        String type = getColumnType(tableName, columnName);
        statementHandler.handle("alter table " + qualified(tableName) + " change column " + columnName + " " + columnName + " " + type + " NULL ");
    }


    /**
     * Retrieval of table constraint names is not supported: an UnsupportedOperationException will be raised.
     *
     * @param tableName The table, not null
     * @return Nothing
     */
    public Set<String> getTableConstraintNames(String tableName) {
        throw new UnsupportedOperationException("Retrieval of table constraint names is not supported in MySQL");
    }


    /**
     * Disables the constraint with the given name on table with the given name.
     *
     * @param tableName      The table with the constraint, not null
     * @param constraintName The constraint, not null
     */
    public void disableConstraint(String tableName, String constraintName) throws StatementHandlerException {
        statementHandler.handle("alter table " + qualified(tableName) + " disable constraint " + constraintName);
    }

    public String getDbmsName() {
        return "mysql";
    }

    /**
     * This method is overwritten, since Mysql doesn't support quoted database object names
     * @return false
     */
    public boolean supportsQuotedDatabaseObjectNames() {
        return false;
    }

    /**
     * This method is overwritten, since Mysql doesn't support schema qualification
     * @return false
     */
    public boolean supportsSchemaQualification() {
        return false;
    }

    /**
     * Gets the type of the column with the given name in the given table. An exception is thrown if
     * the column could not be found
     *
     * @param tableName  The table, not null
     * @param columnName The column, not null
     * @return The type, not null
     */
    protected String getColumnType(String tableName, String columnName) {
        Connection connection = null;
        ResultSet resultSet = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("select COLUMN_TYPE from INFORMATION_SCHEMA.COLUMNS where TABLE_SCHEMA " + " = '" + schemaName + "'" +
                    " and TABLE_NAME = '" + tableName + "' and COLUMN_NAME = '" + columnName + "'");

            if (resultSet.next()) {
                return resultSet.getString("COLUMN_TYPE");
            }
            throw new UnitilsException("Could not get type of column " + columnName + " in table " + tableName + ": column not found.");

        } catch (SQLException e) {
            throw new UnitilsException("Error while getting type of column " + columnName + " in table " + tableName, e);
        } finally {
            DbUtils.closeQuietly(connection, statement, resultSet);
        }
    }

}