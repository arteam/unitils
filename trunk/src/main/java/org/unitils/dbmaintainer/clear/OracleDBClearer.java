package org.unitils.dbmaintainer.clear;

import org.unitils.dbmaintainer.handler.StatementHandlerException;
import org.apache.commons.dbutils.DbUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * todo remove materialized views, triggers, functions, stored procedures
 */
public class OracleDBClearer extends BaseDBClearer {

    protected void dropView(String viewName) throws SQLException, StatementHandlerException {
        String dropTableSQL = "drop table " + viewName + " cascade constraints";
        statementHandler.handle(dropTableSQL);
    }

    protected void dropTable(String tableName) throws StatementHandlerException {
        String dropTableSQL = "drop table " + tableName + " cascade constraints";
        statementHandler.handle(dropTableSQL);
    }

    protected void dropSequences(Connection conn) throws SQLException, StatementHandlerException {
        ResultSet rset = null;
        Statement st = null;
        try {
            st = conn.createStatement();
            rset = st.executeQuery("select SEQUENCE_NAME from USER_SEQUENCES");
            List<String> dropStatements = new ArrayList<String>();
            while (rset.next()) {
                dropStatements.add("drop sequence " + rset.getString("SEQUENCE_NAME"));
            }
            for (String dropStatement : dropStatements) {
                statementHandler.handle(dropStatement);
            }
        } finally {
            DbUtils.closeQuietly(null, st, rset);
        }
    }

    // todo: implement
    protected void dropTriggers(Connection conn) throws StatementHandlerException, SQLException {
        throw new UnsupportedOperationException("todo: implement");
    }

}
