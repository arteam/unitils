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


    public Set<String> getSequenceNames() throws SQLException {
        return getDbItemsOfType("SEQUENCE_NAME", "SYSTEM_SEQUENCES", "SEQUENCE_SCHEMA");
    }


    public Set<String> getTriggerNames() throws SQLException {
        return getDbItemsOfType("TRIGGER_NAME", "SYSTEM_TRIGGERS", "TRIGGER_SCHEM");
    }


    public void dropView(String viewName) throws StatementHandlerException {
        String dropTableSQL = "drop view " + viewName + " cascade";
        statementHandler.handle(dropTableSQL);
    }


    public void dropTable(String tableName) throws StatementHandlerException {
        String dropTableSQL = "drop table " + tableName + " cascade";
        statementHandler.handle(dropTableSQL);
    }


    public long getCurrentValueOfSequence(String sequenceName) throws SQLException {
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
        } finally {
            DbUtils.closeQuietly(conn, st, rset);
        }
    }


    public void incrementSequenceToValue(String sequenceName, long newSequenceValue) throws StatementHandlerException {
        statementHandler.handle("alter sequence " + sequenceName + " restart with " + newSequenceValue);
    }


    public boolean supportsSequences() {
        return true;
    }


    public boolean supportsTriggers() {
        return true;
    }


    public boolean supportsIdentityColumns() {
        return true;
    }


    public void incrementIdentityColumnToValue(String tableName, String primaryKeyColumnName, long identityValue) {
        try {
            statementHandler.handle("alter table " + tableName + " alter column " + primaryKeyColumnName + " RESTART WITH " + identityValue);
        } catch (StatementHandlerException e) {
            logger.info("Column " + primaryKeyColumnName + " on table " + tableName + " is " + "not an identity column");
        }
    }


    public void disableForeignKeyConstraintsCheckingOnConnection(Connection conn) {
        Statement st = null;
        try {
            st = conn.createStatement();
            st.executeUpdate("set referential_integrity false");

        } catch (SQLException e) {
            throw new UnitilsException("Error while disabling constraints", e);
        } finally {
            DbUtils.closeQuietly(st);
        }
    }


    public void removeNotNullConstraint(String tableName, String columnName) throws StatementHandlerException {
        String makeNullableSql = "alter table " + tableName + " alter column " + columnName + " set null";
        statementHandler.handle(makeNullableSql);
    }


    public Set<String> getTableConstraintNames(String tableName) throws SQLException {
        throw new UnsupportedOperationException("Retrieval of table constraint names is not supported in HSQLDB");
    }


    public void disableConstraint(String tableName, String constraintName) throws StatementHandlerException {
        throw new UnsupportedOperationException("Disabling of individual constraints is not supported in HSQLDB");
    }


    public String getLongDataType() {
        return "BIGINT";
    }


    protected Set<String> getDbItemsOfType(String dbItemColumnName, String systemMetadataTableName, String schemaColumnName) throws SQLException {
        Connection conn = null;
        ResultSet rset = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            rset = st.executeQuery("select " + dbItemColumnName + " from INFORMATION_SCHEMA."
                    + systemMetadataTableName + " where " + schemaColumnName + " = '" + schemaName + "'");
            Set<String> names = new HashSet<String>();
            while (rset.next()) {
                names.add(rset.getString(dbItemColumnName).toUpperCase());
            }
            return names;

        } finally {
            DbUtils.closeQuietly(conn, st, rset);
        }
    }

}