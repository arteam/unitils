package org.unitils.dbmaintainer.dbsupport;

import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;
import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.handler.StatementHandlerException;
import org.unitils.core.UnitilsException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 */
public class HsqldbDbSupport extends DbSupport {

    /**
     * Logger for this class
     */
    Logger logger = Logger.getLogger(HsqldbDbSupport.class);

    public HsqldbDbSupport() {
    }

    public void dropView(String viewName) throws SQLException, StatementHandlerException {
        String dropTableSQL = "drop view " + viewName + " cascade";
        statementHandler.handle(dropTableSQL);
    }

    public void dropTable(String tableName) throws SQLException, StatementHandlerException {
        String dropTableSQL = "drop table " + tableName + " cascade";
        statementHandler.handle(dropTableSQL);
    }

    public Set<String> getSequenceNames() throws SQLException {
        return getDbItemsOfType("SEQUENCE_NAME", "SYSTEM_SEQUENCES", "SEQUENCE_SCHEMA");
    }



    public Set<String> getTriggerNames() throws SQLException {
        return getDbItemsOfType("TRIGGER_NAME", "SYSTEM_TRIGGERS", "TRIGGER_SCHEM");
    }

    private Set<String> getDbItemsOfType(String dbItemColumnName,
                                         String systemMetadataTableName, String schemaColumnName) throws SQLException {
        Connection conn = null;
        ResultSet rset = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            rset = st.executeQuery("select " + dbItemColumnName + " from INFORMATION_SCHEMA."
                    + systemMetadataTableName + " where " + schemaColumnName + " = '" + schemaName
                    + "'");
            Set<String> sequenceNames = new HashSet<String>();
            while (rset.next()) {
                sequenceNames.add(rset.getString(dbItemColumnName));
            }
            return sequenceNames;
        } finally {
            DbUtils.closeQuietly(conn, st, rset);
        }
    }

    public long getNextValueOfSequence(String sequenceName) throws SQLException {
        Connection conn = null;
        Statement st = null;
        ResultSet rset = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            rset = st.executeQuery("select next value for " + sequenceName + " from INFORMATION_SCHEMA.SYSTEM_SEQUENCES");
            rset.next();
            long sequenceValue = rset.getLong(1);
            return sequenceValue;
        } finally {
            DbUtils.closeQuietly(conn, st, rset);
        }
    }

    public void incrementSequenceToValue(String sequenceName, long newSequenceValue) throws StatementHandlerException {
        statementHandler.handle("alter sequence " + sequenceName + " restart with " + newSequenceValue);
    }

    public boolean supportsIdentityColumns() {
        return true;
    }

    public void incrementIdentityColumnToValue(String tableName, String primaryKeyColumnName, long identityValue) {
        try {
            statementHandler.handle("alter table " + tableName + " alter column " + primaryKeyColumnName +
                    " RESTART WITH " + identityValue);
        } catch (StatementHandlerException e) {
            logger.info("Column " + primaryKeyColumnName + " on table " + tableName + " is " +
                    "not an identity column");
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
}
