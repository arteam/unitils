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
import org.unitils.dbmaintainer.script.impl.StatementHandlerException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

/**
 * todo include schemanames in all statements where applicable
 * 
 * Implementation of {@link DbSupport} for an IBM DB2 database
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Frederick Beernaert
 */
public class Db2DbSupport extends DbSupport {


    /**
     * Retrieves the names of all the sequences in the database schema.
     *
     * @return The names of all sequences in the database
     */
    public Set<String> getSequenceNames() throws SQLException {
        return getDb2DbIdentifiers("SEQNAME", "SYSSEQUENCES", "SEQSCHEMA");
    }


    /**
     * Retrieves the names of all the triggers in the database schema.
     *
     * @return The names of all triggers in the database
     */
    public Set<String> getTriggerNames() throws SQLException {
        return getDb2DbIdentifiers("NAME", "SYSTRIGGERS", "SCHEMA");
    }


    /**
     * Not supported
     */
    public Set<String> getTypeNames() {
        throw new UnsupportedOperationException("DB2 doesn't support types");
    }

    /**
     * Not supported
     */
    public void dropType(String typeName) throws StatementHandlerException {
        throw new UnsupportedOperationException("DB2 doesn't support types");
    }


    /**
     * Returns the value of the sequence with the given name
     *
     * @param sequenceName The sequence, not null
     * @return The value of the sequence with the given name
     */
    public long getCurrentValueOfSequence(String sequenceName) throws SQLException {
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            rs = st.executeQuery("VALUES PREVVAL FOR " + sequenceName);
            rs.next();
            return rs.getLong("1");

        } catch (SQLException e) {
            return 0;
        } finally {
            DbUtils.closeQuietly(conn, st, rs);
        }
    }


    /**
     * Sets the next value of the sequence with the given sequence name to the given sequence value.
     *
     * @param sequenceName     The sequence, not null
     * @param newSequenceValue The value to set
     */
    public void incrementSequenceToValue(String sequenceName, long newSequenceValue) throws StatementHandlerException {
        statementHandler.handle("ALTER SEQUENCE " + sequenceName + " RESTART WITH " + newSequenceValue);
        statementHandler.handle("VALUES NEXTVAL FOR " + sequenceName);
    }


    /**
     * Sequences are supported.
     *
     * @return True
     */
    public boolean supportsSequences() {
        return true;
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
     * Setting of identity column values is currently not supported: an UnsupportedOperationException will be raised.
     *
     * @param tableName            The table with the identity column, not null
     * @param primaryKeyColumnName The column, not null
     * @param identityValue        The new value
     */
    public void incrementIdentityColumnToValue(String tableName, String primaryKeyColumnName, long identityValue) {
        // Not possible to manually set the identity column to a specific value in DB2
        // todo implement
        throw new UnsupportedOperationException("Current implementation of Db2DbSupport does not support the setting of an indentiy column value.");
    }


    /**
     * Simple disabling of constraints checking on a connection is not supported: an
     * UnsupportedOperationException will be raised.
     *
     * @param connection The database connection, not null
     */
    public void disableForeignKeyConstraintsCheckingOnConnection(Connection connection) {
        throw new UnsupportedOperationException("DB2 doesn't support simple disabling of constraints checking on a connection");
    }


    /**
     * Removal of not null constraints is not supported: an UnsupportedOperationException will be raised.
     *
     * @param tableName  The table with the column, not null
     * @param columnName The column to remove constraints from, not null
     */
    public void removeNotNullConstraint(String tableName, String columnName) throws StatementHandlerException {
        throw new UnsupportedOperationException("Removal of not null constraints is not supported for DB2");
    }


    /**
     * Returns the foreign key and not null constraint names that are enabled/enforced for the table with the given name
     *
     * @param tableName The table, not null
     * @return The set of constraint names, not null
     */
    public Set<String> getTableConstraintNames(String tableName) throws SQLException {
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            rs = st.executeQuery("select CONSTNAME from SYSCAT.TABCONST where TABNAME = '" + tableName + "' and ENFORCED = 'Y'");
            Set<String> constraintNames = new HashSet<String>();
            while (rs.next()) {
                constraintNames.add(rs.getString("CONSTNAME"));
            }
            return constraintNames;
        } finally {
            DbUtils.closeQuietly(conn, st, rs);
        }
    }


    /**
     * Disables the constraint with the given name on table with the given name.
     *
     * @param tableName      The table with the constraint, not null
     * @param constraintName The constraint, not null
     */
    public void disableConstraint(String tableName, String constraintName) throws StatementHandlerException {
        statementHandler.handle("alter table " + tableName + " drop constraint " + constraintName);
    }

    public String getDbmsName() {
        return "db2";
    }


    /**
     * Returns the the idendtifiers for the given type (sequence names, trigger names)
     *
     * @param identifierName          The type of identifier: SEQNAME or NAME
     * @param systemMetadataTableName The meta data table to retrieve the identifiers from: SYSSEQUENCES or SYSTRIGGERS
     * @param schemaColumnName        The column containing the schema name: SEQSCHEMA or SCHEMA
     * @return The names, not null
     */
    protected Set<String> getDb2DbIdentifiers(String identifierName, String systemMetadataTableName, String schemaColumnName) throws SQLException {
        Connection connection = null;
        ResultSet resultSet = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("select " + identifierName + " from SYSIBM." + systemMetadataTableName + " where " + schemaColumnName + " = '" + schemaName + "'");
            Set<String> names = new HashSet<String>();
            while (resultSet.next()) {
                names.add(resultSet.getString(identifierName));
            }
            return names;

        } finally {
            DbUtils.closeQuietly(connection, statement, resultSet);
        }
    }

}