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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.UnitilsException;
import org.unitils.dbmaintainer.script.impl.StatementHandlerException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of {@link DbSupport} for a hsqldb database
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class HsqldbDbSupport extends DbSupport {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(HsqldbDbSupport.class);

    /**
     * todo implement
     */
    public Set<String> getSynonymNames() {
        throw new UnsupportedOperationException("Synonyms not yet implemented for hsqldb");
    }

    /**
     * Retrieves the names of all the sequences in the database schema.
     *
     * @return The names of all sequences in the database
     */
    public Set<String> getSequenceNames() {
        return getHsqlDbIdentifiers("SEQUENCE_NAME", "SYSTEM_SEQUENCES", "SEQUENCE_SCHEMA");
    }


    /**
     * Retrieves the names of all the triggers in the database schema.
     *
     * @return The names of all triggers in the database
     */
    public Set<String> getTriggerNames() {
        return getHsqlDbIdentifiers("TRIGGER_NAME", "SYSTEM_TRIGGERS", "TRIGGER_SCHEM");
    }


    /**
     * Types are not supported: an UnsupportedOperationException will be raised.
     */
    public Set<String> getTypeNames() {
        throw new UnsupportedOperationException("Hsqldb doesn't support types");
    }

    /**
     * Not supported
     */
    public void dropType(String typeName) throws StatementHandlerException {
        throw new UnsupportedOperationException("Hsqldb doesn't support types");
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
        ResultSet rset = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            rset = st.executeQuery("select START_WITH from INFORMATION_SCHEMA.SYSTEM_SEQUENCES where SEQUENCE_SCHEMA = '" +
                    schemaName + "' and SEQUENCE_NAME = '" + sequenceName + "'");
            rset.next();
            return rset.getLong(1);
        } catch (SQLException e) {
            throw new UnitilsException("Error while looking up current value of sequence", e);
        } finally {
            closeQuietly(conn, st, rset);
        }
    }


    /**
     * Sets the next value of the sequence with the given sequence name to the given sequence value.
     *
     * @param sequenceName     The sequence, not null
     * @param newSequenceValue The value to set
     */
    public void incrementSequenceToValue(String sequenceName, long newSequenceValue) throws StatementHandlerException {
        statementHandler.handle("alter sequence " + qualified(sequenceName) + " restart with " + newSequenceValue);
    }


    /**
     * Synonyms are not supported
     *
     * @return false
     */
    public boolean supportsSynonyms() {
        return false;
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
     * Increments the identity value for the specified primary key on the specified table to the given value. If there
     * is no identity specified on the given primary key, the method silently finishes without effect.
     *
     * @param tableName            The table with the identity column, not null
     * @param primaryKeyColumnName The column, not null
     * @param identityValue        The new value
     */
    public void incrementIdentityColumnToValue(String tableName, String primaryKeyColumnName, long identityValue) {
        try {
            statementHandler.handle("alter table " + qualified(tableName) + " alter column " +
                    primaryKeyColumnName + " RESTART WITH " + identityValue);
        } catch (StatementHandlerException e) {
            logger.info("Column " + primaryKeyColumnName + " on table " + tableName + " is " + "not an identity column");
        }
    }


    /**
     * Disables foreign key checking on all subsequent operations that are performed on the given connection object
     *
     * @param connection The database connection, not null
     */
    public void disableForeignKeyConstraintsCheckingOnConnection(Connection connection) {
        Statement st = null;
        try {
            st = connection.createStatement();
            st.executeUpdate("set referential_integrity false");

        } catch (SQLException e) {
            throw new UnitilsException("Error while disabling constraints", e);
        } finally {
            closeQuietly(st);
        }
    }


    /**
     * Removes the not-null constraint on the specified column and table
     *
     * @param tableName  The table with the column, not null
     * @param columnName The column to remove constraints from, not null
     */
    public void removeNotNullConstraint(String tableName, String columnName) throws StatementHandlerException {
        String makeNullableSql = "alter table " + qualified(tableName) + " alter column " + columnName + " set null";
        statementHandler.handle(makeNullableSql);
    }


    /**
     * Retrieval of table constraint names is not supported : an UnsupportedOperationException will be raised.
     *
     * @param tableName The table, not null
     * @return Nothing
     */
    public Set<String> getTableConstraintNames(String tableName) {
        throw new UnsupportedOperationException("Retrieval of table constraint names is not supported in HSQLDB");
    }


    /**
     * Disabling of individual constraints is not supported: an UnsupportedOperationException will be raised.
     *
     * @param tableName      The table with the constraint, not null
     * @param constraintName The constraint, not null
     */
    public void disableConstraint(String tableName, String constraintName) throws StatementHandlerException {
        throw new UnsupportedOperationException("Disabling of individual constraints is not supported in HSQLDB");
    }

    public String getDbmsName() {
        return "hsqldb";
    }


    /**
     * Returns the the idendtifiers for the given type (sequence names, trigger names)
     *
     * @param identifierName          The type of identifier: SEQUENCE_NAME or TRIGGER_NAME
     * @param systemMetadataTableName The meta data table to retrieve the identifiers from: SYSTEM_SEQUENCES or SYSTEM_TRIGGERS
     * @param schemaColumnName        The column containing the schema name: SEQUENCE_SCHEMA or TRIGGER_SCHEM
     * @return The names, not null
     */
    protected Set<String> getHsqlDbIdentifiers(String identifierName, String systemMetadataTableName, String schemaColumnName) {
        Connection connection = null;
        ResultSet resultSet = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("select " + identifierName + " from INFORMATION_SCHEMA."
                    + systemMetadataTableName + " where " + schemaColumnName + " = '" + schemaName + "'");
            Set<String> names = new HashSet<String>();
            while (resultSet.next()) {
                names.add(resultSet.getString(identifierName));
            }
            return names;

        } catch (SQLException e) {
            throw new UnitilsException("Error while looking up hsqldb identifiers", e);
        } finally {
            closeQuietly(connection, statement, resultSet);
        }
    }

}