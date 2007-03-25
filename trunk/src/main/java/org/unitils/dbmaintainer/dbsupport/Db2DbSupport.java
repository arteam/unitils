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
import org.unitils.thirdparty.org.apache.commons.dbutils.DbUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

/**
 * todo include schemanames in all statements where applicable
 * <p/>
 * Implementation of {@link DbSupport} for an IBM DB2 database
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Frederick Beernaert
 */
public class Db2DbSupport extends DbSupport {


    /**
     * Creates support for Db2 databases.
     */
    public Db2DbSupport() {
        super("db2");
    }


    /**
     * Retrieves the names of all the sequences in the database schema.
     *
     * @return The names of all sequences in the database
     */
    @Override
    public Set<String> getSequenceNames() {
        return getDb2DbIdentifiers("SEQNAME", "SYSSEQUENCES", "SEQSCHEMA");
    }


    /**
     * Retrieves the names of all the triggers in the database schema.
     *
     * @return The names of all triggers in the database
     */
    @Override
    public Set<String> getTriggerNames() {
        return getDb2DbIdentifiers("NAME", "SYSTRIGGERS", "SCHEMA");
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
            rs = st.executeQuery("VALUES PREVVAL FOR " + qualified(sequenceName));
            rs.next();
            return rs.getLong("1");

        } catch (SQLException e) {
            throw new UnitilsException("Error while looking up primary key column names", e);
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
    @Override
    public void incrementSequenceToValue(String sequenceName, long newSequenceValue) throws StatementHandlerException {
        statementHandler.handle("ALTER SEQUENCE " + qualified(sequenceName) + " RESTART WITH " + newSequenceValue);
        statementHandler.handle("VALUES NEXTVAL FOR " + qualified(sequenceName));
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
     * todo add schemaname to query
     * Returns the foreign key and not null constraint names that are enabled/enforced for the table with the given name
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
            rs = st.executeQuery("select CONSTNAME from SYSCAT.TABCONST where TABNAME = '" + tableName + "' and ENFORCED = 'Y'");
            Set<String> constraintNames = new HashSet<String>();
            while (rs.next()) {
                constraintNames.add(rs.getString("CONSTNAME"));
            }
            return constraintNames;
        } catch (SQLException e) {
            throw new UnitilsException("Error while looking up table constraint names", e);
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
    @Override
    public void disableConstraint(String tableName, String constraintName) throws StatementHandlerException {
        statementHandler.handle("alter table " + qualified(tableName) + " drop constraint " + constraintName);
    }


    /**
     * Returns the the idendtifiers for the given type (sequence names, trigger names)
     *
     * @param identifierName          The type of identifier: SEQNAME or NAME
     * @param systemMetadataTableName The meta data table to retrieve the identifiers from: SYSSEQUENCES or SYSTRIGGERS
     * @param schemaColumnName        The column containing the schema name: SEQSCHEMA or SCHEMA
     * @return The names, not null
     */
    protected Set<String> getDb2DbIdentifiers(String identifierName, String systemMetadataTableName, String schemaColumnName) {
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

        } catch (SQLException e) {
            throw new UnitilsException("Error while looking up db2 identifiers", e);
        } finally {
            DbUtils.closeQuietly(connection, statement, resultSet);
        }
    }

}