package org.unitils.dbmaintainer.clear;

import org.unitils.dbmaintainer.handler.StatementHandlerException;
import org.apache.commons.dbutils.DbUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * todo remove views, triggers, functions, stored procedures
 */
public class HsqldbDBClearer extends BaseDBClearer {

    protected void dropView(String viewName) throws SQLException, StatementHandlerException {
        String dropTableSQL = "drop view " + viewName + " cascade";
        statementHandler.handle(dropTableSQL);
    }

    protected void dropTable(String tableName) throws StatementHandlerException {
        String dropTableSQL = "drop table " + tableName + " cascade";
        statementHandler.handle(dropTableSQL);
    }

    protected void dropSequences(Connection conn) throws SQLException, StatementHandlerException {
        List<String> sequenceNames = getSequenceNames(conn);
        for (String sequenceName : sequenceNames) {
            statementHandler.handle("drop sequence " + sequenceName);
        }
    }

    private List<String> getSequenceNames(Connection conn) throws SQLException {
        return getDbItemsOfType(conn, "SEQUENCE_NAME", "SYSTEM_SEQUENCES");
    }

    protected void dropTriggers(Connection conn) throws SQLException, StatementHandlerException {
        List<String> triggerNames = getTriggerNames(conn);
        for (String triggerName : triggerNames) {
            statementHandler.handle("drop trigger " + triggerName);
        }
    }

    private List<String> getTriggerNames(Connection conn) throws SQLException {
        return getDbItemsOfType(conn, "TRIGGER_NAME", "SYSTEM_TRIGGERS");
    }

    private List<String> getDbItemsOfType(Connection conn, String dbItemName, String systemMetadataTableName) throws SQLException {
        ResultSet rset = null;
        Statement st = null;
        try {
            st = conn.createStatement();
            rset = st.executeQuery("select " + dbItemName + " from INFORMATION_SCHEMA." + systemMetadataTableName);
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
