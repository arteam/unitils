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

import static org.unitils.thirdparty.org.apache.commons.dbutils.DbUtils.closeQuietly;
import org.unitils.dbmaintainer.script.impl.StatementHandlerException;
import org.unitils.core.UnitilsException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of {@link DbSupport} for an Oracle database.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class OracleDbSupport extends DbSupport {


    /**
     * Retrieves the names of all the sequences in the database schema.
     *
     * @return The names of all sequences in the database
     */
    public Set<String> getSynonymNames() {
        return getOracleIdentifiers("SYNONYM_NAME", "USER_SYNONYMS");
    }

    /**
     * Retrieves the names of all the sequences in the database schema.
     *
     * @return The names of all sequences in the database
     */
    public Set<String> getSequenceNames() {
        return getOracleIdentifiers("SEQUENCE_NAME", "USER_SEQUENCES");
    }


    /**
     * Retrieves the names of all the triggers in the database schema.
     *
     * @return The names of all triggers in the database
     */
    public Set<String> getTriggerNames() {
        return getOracleIdentifiers("TRIGGER_NAME", "USER_TRIGGERS");
    }


    /**
     * Retrieves the names of all the types in the database schema.
     *
     * @return The names of all types in the database
     */
    public Set<String> getTypeNames() {
        return getOracleIdentifiers("TYPE_NAME", "USER_TYPES");
    }


    /**
     * Removes the table with the given name from the database.
     * Note: the table name is surrounded with quotes, making it case-sensitive.
     *
     * @param tableName The table to drop (case-sensitive), not null
     */
    public void dropTable(String tableName) throws StatementHandlerException {
        String dropTableSQL = "drop table " + qualified(tableName) + " cascade constraints";
        statementHandler.handle(dropTableSQL);
    }


    /**
     * Removes the view with the given name from the database
     * Note: the view name is surrounded with quotes, making it case-sensitive.
     *
     * @param viewName The view to drop (case-sensitive), not null
     */
    public void dropView(String viewName) throws StatementHandlerException {
        String dropTableSQL = "drop view " + qualified(viewName) + " cascade constraints";
        statementHandler.handle(dropTableSQL);
    }


    /**
     * Drops the type with the given name from the database
     * Note: the type name is surrounded with quotes, making it case-sensitive.
     *
     * @param typeName The type to drop (case-sensitive), not null
     */
    public void dropType(String typeName) throws StatementHandlerException {
        statementHandler.handle("drop type " + qualified(typeName) + " force");
    }


    /**
     * Returns the value of the sequence with the given name
     *
     * @param sequenceName The sequence, not null
     * @return The value of the sequence with the given name
     */
    public long getCurrentValueOfSequence(String sequenceName) {
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            rs = st.executeQuery("select LAST_NUMBER from USER_SEQUENCES where SEQUENCE_NAME = '" + sequenceName + "'");
            rs.next();
            return rs.getLong("LAST_NUMBER");
        } catch (SQLException e) {
            throw new UnitilsException("Error while looking up current value of sequence", e);
        } finally {
            closeQuietly(conn, st, rs);
        }
    }


    /**
     * Sets the next value of the sequence with the given sequence name to the given sequence value.
     *
     * @param sequenceName     The sequence, not null
     * @param newSequenceValue The value to set
     */
    public void incrementSequenceToValue(String sequenceName, long newSequenceValue) throws StatementHandlerException {
        Connection conn = null;
        ResultSet rs = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            rs = st.executeQuery("select LAST_NUMBER, INCREMENT_BY from USER_SEQUENCES where SEQUENCE_NAME = '" + sequenceName + "'");
            while (rs.next()) {
                long lastNumber = rs.getLong("LAST_NUMBER");
                long incrementBy = rs.getLong("INCREMENT_BY");
                String sqlChangeIncrement = "alter sequence " + qualified(sequenceName) + " increment by " + (newSequenceValue - lastNumber);
                statementHandler.handle(sqlChangeIncrement);
                String sqlNextSequenceValue = "select " + qualified(sequenceName) + ".NEXTVAL from DUAL";
                statementHandler.handle(sqlNextSequenceValue);
                String sqlResetIncrement = "alter sequence " + qualified(sequenceName) + " increment by " + incrementBy;
                statementHandler.handle(sqlResetIncrement);
            }
        } catch (SQLException e) {
            throw new UnitilsException("Error while incrementing sequence to value", e);
        } finally {
            closeQuietly(conn, st, rs);
        }
    }


    /**
     * Synonyms are supported
     * @return True
     */
    public boolean supportsSynonyms() {
        return true;
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
     * Identity columns are not supported.
     *
     * @return False
     */
    public boolean supportsIdentityColumns() {
        return false;
    }


    /**
     * Types are supported
     *
     * @return true
     */
    public boolean supportsTypes() {
        return true;
    }


    /**
     * Identity columns are not supported: an UnsupportedOperationException will be raised.
     *
     * @param tableName            The table with the identity column, not null
     * @param primaryKeyColumnName The column, not null
     * @param identityValue        The new value
     */
    public void incrementIdentityColumnToValue(String tableName, String primaryKeyColumnName, long identityValue) {
        throw new UnsupportedOperationException("Oracle doesn't support identity columns");
    }


    /**
     * Simple disabling of constraints checking on a connection is not supported: an
     * UnsupportedOperationException will be raised.
     *
     * @param connection The database connection, not null
     */
    public void disableForeignKeyConstraintsCheckingOnConnection(Connection connection) {
        throw new UnsupportedOperationException("Oracle doesn't support simple disabling of constraints checking on a connection");
    }


    /**
     * Removal of not null constraints is not supported: an UnsupportedOperationException will be raised.
     *
     * @param tableName  The table with the column, not null
     * @param columnName The column to remove constraints from, not null
     */
    public void removeNotNullConstraint(String tableName, String columnName) throws StatementHandlerException {
        throw new UnsupportedOperationException("Removal of not null constraints is not supported for Oracle");
    }


    /**
     * Returns the foreign key and not null constraint names that are enabled/enforced for the table with the given name
     *
     * @param tableName The table, not null
     * @return The set of constraint names, not null
     */
    public Set<String> getTableConstraintNames(String tableName) {
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            rs = st.executeQuery("select CONSTRAINT_NAME from USER_CONSTRAINTS where TABLE_NAME = '" +
                    tableName + "' and (CONSTRAINT_TYPE = 'R' or CONSTRAINT_TYPE = 'C') and STATUS = 'ENABLED'");
            Set<String> constraintNames = new HashSet<String>();
            while (rs.next()) {
                constraintNames.add(rs.getString("CONSTRAINT_NAME"));
            }
            return constraintNames;
        } catch (SQLException e) {
            throw new UnitilsException("Error while looking up table constraint names", e);
        } finally {
            closeQuietly(conn, st, rs);
        }
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


    /**
     * Gets the column type suitable to store values of the Java <code>java.lang.Long</code> type.
     *
     * @return The column type
     */
    public String getLongDataType() {
        return "INTEGER";
    }


    /**
     * The name of the DBMS implementation that is supported by this implementation of {@link DbSupport}
     *
     * @return oracle
     */
    public String getDbmsName() {
        return "oracle";
    }


    /**
     * Returns the the idendtifiers for the given type (sequence names, trigger names)
     *
     * @param identifierName          The type of identifier: SEQUENCE_NAME or TRIGGER_NAME
     * @param systemMetadataTableName The meta data table to retrieve the identifiers from: USER_SEQUENCES or USER_TRIGGERS
     * @return The names, not null
     */
    protected Set<String> getOracleIdentifiers(String identifierName, String systemMetadataTableName) {
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            rs = st.executeQuery("select " + identifierName + " from " + systemMetadataTableName);
            Set<String> names = new HashSet<String>();
            while (rs.next()) {
                names.add(rs.getString(identifierName));
            }
            return names;
        } catch (SQLException e) {
            throw new UnitilsException("Error while looking up oracle identifiers", e);
        } finally {
            closeQuietly(conn, st, rs);
        }
    }

}