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
import org.unitils.dbmaintainer.handler.StatementHandlerException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of {@link DbSupport} for an IBM DB2 database
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Frederick Beernaert
 */
public class Db2DbSupport extends DbSupport {

    public Db2DbSupport() {
    }

    public Set<String> getSequenceNames() throws SQLException {
        return getDbItemsOfType("SEQNAME", "SYSSEQUENCES", "SEQSCHEMA");
    }

    public Set<String> getTriggerNames() throws SQLException {
        return getDbItemsOfType("NAME", "SYSTRIGGERS", "SCHEMA");
    }

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

    public void incrementSequenceToValue(String sequenceName, long newSequenceValue) throws StatementHandlerException {
        statementHandler.handle("ALTER SEQUENCE " + sequenceName + " RESTART WITH " + newSequenceValue);
        statementHandler.handle("VALUES NEXTVAL FOR " + sequenceName);
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
        // Not possible to manually set the identity column to a specific value in DB2
    }

    public void disableForeignKeyConstraintsCheckingOnConnection(Connection conn) {
        throw new UnsupportedOperationException("DB2 doesn't simple disabling of constraints checking on a connection");
    }

    public void removeNotNullConstraint(String tableName, String columnName) throws StatementHandlerException {
        throw new UnsupportedOperationException("Removal of not null constraints is not supported for DB2");
    }

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

    public void disableConstraint(String tableName, String constraintName) throws StatementHandlerException {
        statementHandler.handle("alter table " + tableName + " drop constraint " + constraintName);
    }

    public String getLongDataType() {
        return "BIGINT";
    }

    private Set<String> getDbItemsOfType(String dbItemColumnName, String systemMetadataTableName, String schemaColumnName) throws SQLException {
        Connection conn = null;
        ResultSet rset = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            rset = st.executeQuery("select " + dbItemColumnName + " from SYSIBM."
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