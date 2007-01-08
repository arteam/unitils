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
 * <p/>
 * todo implement + javadoc
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class Db2DbSupport extends DbSupport {

    public Db2DbSupport() {
    }

    public Set<String> getSequenceNames() throws SQLException {
        return null;
    }

    public Set<String> getTriggerNames() throws SQLException {
        return null;
    }

    public boolean triggerExists(String triggerName) throws SQLException {
        return false;
    }

    public boolean sequenceExists(String sequenceName) throws SQLException {
        return false;
    }

    public void dropView(String viewName) throws StatementHandlerException {
    }

    public void dropTable(String tableName) throws StatementHandlerException {
    }

    public long getCurrentValueOfSequence(String sequenceName) throws SQLException {
        return 0;
    }

    public void incrementSequenceToValue(String sequenceName, long newSequenceValue) throws StatementHandlerException {
    }

    public boolean supportsSequences() {
        return false;
    }

    public boolean supportsTriggers() {
        return true;
    }

    public boolean supportsIdentityColumns() {
        return false;
    }

    public void incrementIdentityColumnToValue(String tableName, String primaryKeyColumnName, long identityValue) {
    }

    public void disableForeignKeyConstraintsCheckingOnConnection(Connection conn) {
    }

    public void removeNotNullConstraint(String tableName, String columnName) throws StatementHandlerException {
    }

    public Set<String> getTableConstraintNames(String tableName) throws SQLException {
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            rs = st.executeQuery("select CONSTNAME from SYSCAT.TABCONST where TABNAME = '" + tableName +
                    "' and (TYPE = 'F' or TYPE = 'K') and ENFORCED = 'Y'");
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
        statementHandler.handle("alter table " + tableName + " disable constraint " + constraintName);
    }

    public String getLongDataType() {
        return "BIGINT";
    }
}
