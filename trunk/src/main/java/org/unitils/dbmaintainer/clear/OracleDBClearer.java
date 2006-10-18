/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.dbmaintainer.clear;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.unitils.dbmaintainer.handler.StatementHandlerException;

/**
 * Implementation of {@link DBClearer} for Oracle
 */
public class OracleDBClearer extends BaseDBClearer {

    /**
     * Removes the view with the given name from the database
     * 
     * @param viewName
     * @throws SQLException
     * @throws StatementHandlerException
     */
    @Override
    protected void dropView(String viewName) throws SQLException, StatementHandlerException {
        String dropTableSQL = "drop view " + viewName + " cascade constraints";
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
        String dropTableSQL = "drop table " + tableName + " cascade constraints";
        statementHandler.handle(dropTableSQL);
    }

    /**
     * Drops all sequences in the database
     * 
     * @param conn
     * @throws SQLException
     * @throws StatementHandlerException
     */
    // TODO check schema name
    @Override
    protected void dropSequences(Connection conn) throws SQLException, StatementHandlerException {
        List<String> sequenceNames = getDbItemsOfType(conn, "SEQUENCE_NAME", "USER_SEQUENCES");
        for (String sequenceName : sequenceNames) {
            statementHandler.handle("drop sequence " + sequenceName);
        }
    }

    /**
     * Drops all database triggers
     * 
     * @param conn
     * @throws SQLException
     * @throws StatementHandlerException
     */
    // TODO check schema name
    @Override
    protected void dropTriggers(Connection conn) throws StatementHandlerException, SQLException {
        List<String> triggerNames = getDbItemsOfType(conn, "TRIGGER_NAME", "USER_TRIGGERS");
        for (String triggerName : triggerNames) {
            statementHandler.handle("drop trigger " + triggerName);
        }
    }

    private List<String> getDbItemsOfType(Connection conn, String dbItemName,
            String systemMetadataTableName) throws SQLException {
        ResultSet rset = null;
        Statement st = null;
        try {
            st = conn.createStatement();
            rset = st.executeQuery("select " + dbItemName + " from " + systemMetadataTableName);
            List<String> sequenceNames = new ArrayList<String>();
            while (rset.next()) {
                sequenceNames.add(rset.getString(dbItemName));
            }
            return sequenceNames;
        } finally {
            DbUtils.closeQuietly(null, st, rset);
        }
    }

}
