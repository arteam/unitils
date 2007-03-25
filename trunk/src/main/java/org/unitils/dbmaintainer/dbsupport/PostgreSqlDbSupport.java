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

import org.unitils.core.UnitilsException;
import org.unitils.dbmaintainer.script.impl.StatementHandlerException;
import static org.unitils.thirdparty.org.apache.commons.dbutils.DbUtils.closeQuietly;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of {@link DbSupport} for an PostgreSql database.
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
     * Retrieves the names of all the sequences in the database schema.
     *
     * @return The names of all sequences in the database
     */
    @Override
    public Set<String> getSequenceNames() {
        return getPostgreSqlIdentifiers("SEQUENCE_NAME", "SEQUENCES");
    }


    /**
     * Retrieves the names of all the triggers in the database schema.
     *
     * @return The names of all triggers in the database
     */
    @Override
    public Set<String> getTriggerNames() {
        return getPostgreSqlIdentifiers("TRIGGER_NAME", "TRIGGERS");
    }


    /**
     * Retrieves the names of all user-defined types in the database schema.
     *
     * @return The names of all types in the database
     */
    @Override
    public Set<String> getTypeNames() {
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            rs = st.executeQuery("select OBJECT_NAME from INFORMATION_SCHEMA.DATA_TYPE_PRIVILEGES where OBJECT_TYPE = 'USER-DEFINED TYPE'");
            Set<String> constraintNames = new HashSet<String>();
            while (rs.next()) {
                constraintNames.add(rs.getString("OBJECT_NAME"));
            }
            return constraintNames;

        } catch (SQLException e) {
            throw new UnitilsException("Error while looking up type names", e);
        } finally {
            closeQuietly(conn, st, rs);
        }
    }


    /**
     * Drops the type with the given name from the database
     * Note: the type name is surrounded with quotes, making it case-sensitive.
     *
     * @param typeName The type to drop (case-sensitive), not null
     */
    @Override
    public void dropType(String typeName) throws StatementHandlerException {
        statementHandler.handle("drop type " + qualified(typeName) + " cascade");
    }


    /**
     * Returns the value of the sequence with the given name
     *
     * @param sequenceName The sequence, not null
     * @return The value of the sequence with the given name
     */
    @Override
    public long getCurrentValueOfSequence(String sequenceName) {
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            rs = st.executeQuery("select last_value from " + qualified(sequenceName));
            rs.next();
            return rs.getLong(1);

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
    @Override
    public void incrementSequenceToValue(String sequenceName, long newSequenceValue) throws StatementHandlerException {
        Connection conn = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            st.executeQuery("select setval('" + qualified(sequenceName) + "', " + newSequenceValue + ")");

        } catch (SQLException e) {
            throw new UnitilsException("Error while incrementing sequence to value", e);
        } finally {
            closeQuietly(conn, st, null);
        }
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
     * Removes the not-null constraint on the specified column and table
     *
     * @param tableName  The table with the column, not null
     * @param columnName The column to remove constraints from, not null
     */
    @Override
    public void removeNotNullConstraint(String tableName, String columnName) throws StatementHandlerException {
        statementHandler.handle("alter table " + qualified(tableName) + " alter column " + columnName + " drop not null");
    }


    /**
     * Returns the foreign key constraint names that are enabled/enforced for the table with the given name
     *
     * @param tableName The table, not null
     * @return The set of constraint names, not null
     */
    @Override
    public Set<String> getTableConstraintNames(String tableName) {
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            rs = st.executeQuery("select CONSTRAINT_NAME from INFORMATION_SCHEMA.TABLE_CONSTRAINTS where TABLE_NAME = '" + tableName + "' and CONSTRAINT_TYPE = 'FOREIGN KEY'");
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
    @Override
    public void disableConstraint(String tableName, String constraintName) throws StatementHandlerException {
        statementHandler.handle("alter table " + qualified(tableName) + " drop constraint \"" + constraintName + "\"");
    }


    /**
     * Returns the the idendtifiers for the given type (sequence names, trigger names)
     *
     * @param identifierName          The type of identifier: SEQUENCE_NAME or TRIGGER_NAME
     * @param systemMetadataTableName The meta data table to retrieve the identifiers from: USER_SEQUENCES or USER_TRIGGERS
     * @return The names, not null
     */
    protected Set<String> getPostgreSqlIdentifiers(String identifierName, String systemMetadataTableName) {
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            rs = st.executeQuery("select " + identifierName + " from INFORMATION_SCHEMA." + systemMetadataTableName);
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
