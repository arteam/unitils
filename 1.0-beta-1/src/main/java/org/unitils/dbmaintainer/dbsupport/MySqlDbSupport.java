package org.unitils.dbmaintainer.dbsupport;

import org.apache.commons.dbutils.DbUtils;
import org.unitils.core.UnitilsException;
import org.unitils.dbmaintainer.handler.StatementHandlerException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of {@link DbSupport} for a MySql database
 *
 * @author Frederick Beernaert
 */
public class MySqlDbSupport extends DbSupport {

    public MySqlDbSupport() {
    }

    public Set<String> getTableNames() throws SQLException {
        Connection conn = null;
        ResultSet rset = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            rset = st.executeQuery("select TABLE_NAME from INFORMATION_SCHEMA.TABLES where " +
                    " TABLE_SCHEMA " + " = '" + schemaName + "'" +
                    " and TABLE_TYPE = 'BASE TABLE'");
            Set<String> names = new HashSet<String>();
            while (rset.next()) {
                names.add(rset.getString("TABLE_NAME").toUpperCase());
            }
            return names;
        } finally {
            DbUtils.closeQuietly(conn, st, rset);
        }
    }

    public Set<String> getViewNames() throws SQLException {
        Connection conn = null;
        ResultSet rset = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            rset = st.executeQuery("select TABLE_NAME from INFORMATION_SCHEMA.TABLES where " +
                    " TABLE_SCHEMA " + " = '" + schemaName + "'" +
                    " and TABLE_TYPE = 'VIEW'");
            Set<String> names = new HashSet<String>();
            while (rset.next()) {
                names.add(rset.getString("TABLE_NAME").toUpperCase());
            }
            return names;
        } finally {
            DbUtils.closeQuietly(conn, st, rset);
        }
    }

    public Set<String> getSequenceNames() throws SQLException {
        throw new UnsupportedOperationException("Sequences are not supported in MySQL");
    }

    public Set<String> getTriggerNames() throws SQLException {
        return getDbItemsOfType("TRIGGER_NAME", "TRIGGERS", "TRIGGER_SCHEMA");
    }

    public void dropView(String viewName) throws StatementHandlerException {
        String dropViewSQL = "drop view " + viewName + " cascade";
        statementHandler.handle(dropViewSQL);
    }

    public void dropTable(String tableName) throws StatementHandlerException {
        String dropTableSQL = "drop table " + tableName + " cascade";
        statementHandler.handle(dropTableSQL);
    }

    public long getNextValueOfSequence(String sequenceName) throws SQLException {
        throw new UnsupportedOperationException("Sequences are not supported in MySQL");
    }

    public void incrementSequenceToValue(String sequenceName, long newSequenceValue) throws StatementHandlerException {
        throw new UnsupportedOperationException("Sequences are not supported in MySQL");
    }

    public boolean supportsSequences() {
        return false;
    }

    public boolean supportsTriggers() {
        return true;
    }

    public boolean supportsIdentityColumns() {
        return true;
    }

    public void incrementIdentityColumnToValue(String tableName, String primaryKeyColumnName, long identityValue) {
        try {
            statementHandler.handle("alter table " + tableName + " AUTO_INCREMENT = " + identityValue);
        } catch (StatementHandlerException e) {
            throw new UnitilsException(e);
        }
    }

    public void disableForeignKeyConstraintsCheckingOnConnection(Connection conn) {
        Statement st = null;
        try {
            st = conn.createStatement();
            st.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");
        } catch (SQLException e) {
            throw new UnitilsException("Error while disabling constraints", e);
        } finally {
            DbUtils.closeQuietly(st);
        }
    }

    public void removeNotNullConstraint(String tableName, String columnName) throws StatementHandlerException {
        String type = getColumnType(tableName, columnName);
        statementHandler.handle("alter table " + tableName + " change column " + columnName + " " + columnName + " " + type + " NULL ");
    }

    public Set<String> getTableConstraintNames(String tableName) throws SQLException {
        throw new UnsupportedOperationException("Retrieval of table constraint names is not supported in MySQL");
    }

    public void disableConstraint(String tableName, String constraintName) throws StatementHandlerException {
        statementHandler.handle("alter table " + tableName + " disable constraint " + constraintName);
    }

    public String getLongDataType() {
        return "BIGINT";
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
            Set<String> names = new HashSet<String>();
            while (rset.next()) {
                names.add(rset.getString(dbItemColumnName).toUpperCase());
            }
            return names;
        } finally {
            DbUtils.closeQuietly(conn, st, rset);
        }
    }

    private String getColumnType(String tableName, String columnName) {
        String type = null;
        Connection conn = null;
        ResultSet rset = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            rset = st.executeQuery("select COLUMN_TYPE from INFORMATION_SCHEMA.COLUMNS where " +
                    " TABLE_SCHEMA " + " = '" + schemaName + "'" +
                    " and TABLE_NAME " + " = '" + tableName + "'" +
                    " and COLUMN_NAME " + " = '" + columnName + "'");
            if (rset.next()) {
                type = rset.getString("COLUMN_TYPE").toUpperCase();
            }
        } catch (SQLException e) {
            throw new UnitilsException("Error while removing not null constraint", e);
        } finally {
            DbUtils.closeQuietly(conn, st, rset);
        }
        return type;
    }

}