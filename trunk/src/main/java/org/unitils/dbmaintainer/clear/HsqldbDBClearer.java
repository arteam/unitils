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
package org.unitils.dbmaintainer.clear;

import org.apache.commons.dbutils.DbUtils;
import org.unitils.dbmaintainer.handler.StatementHandlerException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link DBClearer} for hsqldb
 */
public class HsqldbDBClearer extends BaseDBClearer {

    /**
     * Removes the view with the given name from the database
     *
     * @param viewName
     * @throws SQLException
     * @throws StatementHandlerException
     */
    @Override
    protected void dropView(String viewName) throws SQLException, StatementHandlerException {
        String dropTableSQL = "drop view " + viewName + " cascade";
        statementHandler.handle(dropTableSQL);
    }

    /**
     * Removes the table with the given name from the database
     *
     * @param tableName
     * @throws StatementHandlerException
     */
    @Override
    protected void dropTable(String tableName) throws StatementHandlerException {
        String dropTableSQL = "drop table " + tableName + " cascade";
        statementHandler.handle(dropTableSQL);
    }

    /**
     * Drops all sequences in the database
     *
     * @param conn
     * @throws SQLException
     * @throws StatementHandlerException
     */
    @Override
    protected void dropSequences(Connection conn) throws SQLException, StatementHandlerException {
        List<String> sequenceNames = getSequenceNames(conn);
        for (String sequenceName : sequenceNames) {
            statementHandler.handle("drop sequence " + sequenceName);
        }
    }

    /**
     * @param conn
     * @return The names of all sequences in the database
     * @throws SQLException
     */
    private List<String> getSequenceNames(Connection conn) throws SQLException {
        return getDbItemsOfType(conn, "SEQUENCE_NAME", "SYSTEM_SEQUENCES", "SEQUENCE_SCHEMA");
    }

    /**
     * Drops all database triggers
     *
     * @param conn
     * @throws SQLException
     * @throws StatementHandlerException
     */
    @Override
    protected void dropTriggers(Connection conn) throws SQLException, StatementHandlerException {
        List<String> triggerNames = getTriggerNames(conn);
        for (String triggerName : triggerNames) {
            statementHandler.handle("drop trigger " + triggerName);
        }
    }

    private List<String> getTriggerNames(Connection conn) throws SQLException {
        return getDbItemsOfType(conn, "TRIGGER_NAME", "SYSTEM_TRIGGERS", "TRIGGER_SCHEM");
    }

    private List<String> getDbItemsOfType(Connection conn, String dbItemColumnName,
                                          String systemMetadataTableName, String schemaColumnName) throws SQLException {
        ResultSet rset = null;
        Statement st = null;
        try {
            st = conn.createStatement();
            rset = st.executeQuery("select " + dbItemColumnName + " from INFORMATION_SCHEMA."
                    + systemMetadataTableName + " where " + schemaColumnName + " = '" + schemaName
                    + "'");
            List<String> sequenceNames = new ArrayList<String>();
            while (rset.next()) {
                sequenceNames.add(rset.getString(dbItemColumnName));
            }
            return sequenceNames;
        } finally {
            DbUtils.closeQuietly(null, st, rset);
        }
    }

}
