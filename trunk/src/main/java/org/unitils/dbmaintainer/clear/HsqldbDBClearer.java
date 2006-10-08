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

    protected void dropSequences(Statement st) throws SQLException, StatementHandlerException {
        ResultSet rset = null;
        try {
            rset = st.executeQuery("select SEQUENCE_NAME from INFORMATION_SCHEMA.SYSTEM_SEQUENCES");
            List<String> dropStatements = new ArrayList<String>();
            while (rset.next()) {
                dropStatements.add("drop sequence " + rset.getString("SEQUENCE_NAME"));
            }
            for (String dropStatement : dropStatements) {
                statementHandler.handle(dropStatement);
            }
        } finally {
            DbUtils.closeQuietly(rset);
        }
    }


}
