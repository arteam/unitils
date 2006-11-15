package org.unitils.dbmaintainer.dbsupport;

import org.unitils.dbmaintainer.handler.StatementHandlerException;
import org.unitils.dbmaintainer.handler.StatementHandler;
import org.apache.commons.dbutils.DbUtils;

import javax.sql.DataSource;
import java.util.Set;
import java.util.HashSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * 
 */
public class Db2DbSupport extends DbSupport {

    public Db2DbSupport() {
    }

    public Set<String> getSequenceNames() throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Set<String> getTriggerNames() throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean triggerExists(String triggerName) throws SQLException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean sequenceExists(String sequenceName) throws SQLException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void dropView(String viewName) throws StatementHandlerException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void dropTable(String tableName) throws StatementHandlerException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public long getNextValueOfSequence(String sequenceName) throws SQLException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void incrementSequenceToValue(String sequenceName, long newSequenceValue) throws StatementHandlerException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean supportsIdentityColumns() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void incrementIdentityColumnToValue(String tableName, String primaryKeyColumnName, long identityValue) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void disableForeignKeyConstraintsCheckingOnConnection(Connection conn) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void removeNotNullConstraint(String tableName, String columnName) throws StatementHandlerException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Set<String> getTableConstraintNames(String tableName) throws SQLException {
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            rs = st.executeQuery("select CONSTNAME from SYSCAT.TABCONST where TABNAME = '" + tableName + "'");
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
        statementHandler.handle("alter table " + tableName + " disable constraint " + constraintName) ;
    }

    public String getLongDataType() {
        return "BIGINT";
    }
}
