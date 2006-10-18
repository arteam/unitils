/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.dbmaintainer.constraints;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.dbutils.DbUtils;
import org.unitils.core.UnitilsException;
import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.handler.StatementHandlerException;

/**
 * TODO test me
 * TODO javadoc me
 *
 * Implementation of {@link ConstraintsDisabler} for an MySQL database.
 */
public class MySQLConstraintsDisabler implements ConstraintsDisabler {
    
    public static final String PROPKEY_SCHEMANAME = "dataSource.schemaName";

    /**
     * The TestDataSource
     */
    private DataSource dataSource;

    /**
     * The StatementHandler
     */
    private StatementHandler statementHandler;
    
    private String schemaName;


    /**
     * @see ConstraintsDisabler#init(org.apache.commons.configuration.Configuration,javax.sql.DataSource,org.unitils.dbmaintainer.handler.StatementHandler)
     */
    public void init(Configuration configuration, DataSource dataSource, StatementHandler statementHandler) {
        this.dataSource = dataSource;
        this.statementHandler = statementHandler;
        
        schemaName = configuration.getString(PROPKEY_SCHEMANAME).toUpperCase();
    }

    /**
     * @see org.unitils.dbmaintainer.constraints.ConstraintsDisabler#disableConstraints()
     */
    public void disableConstraints() {
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            removeNotNullConstraints(conn);
        } catch (SQLException e) {
            throw new UnitilsException("Error while disabling constraints", e);
        } catch (StatementHandlerException e) {
            throw new UnitilsException("Error while disabling constraints", e);
        } finally {
            DbUtils.closeQuietly(conn, null, rs);
        }
    }
    
    /**
     * Sends statements to the StatementHandler that make sure all not-null constraints are disabled.
     * @param conn
     * @throws SQLException
     * @throws StatementHandlerException
     */
    private void removeNotNullConstraints(Connection conn) throws SQLException, StatementHandlerException {
        // Iterate of all table names
        List<String> tableNames = getTableNames(conn);
        for (String tableName : tableNames) {
            removeNotNullConstraints(conn, tableName);
        }
    }
    
    /**
     * Sends statements to the StatementHandler that make sure all not-null constraints for the table with the given
     * name are disabled.
     * @param conn
     * @param tableName 
     * @throws SQLException
     * @throws StatementHandlerException
     */
    private void removeNotNullConstraints(Connection conn, String tableName) throws SQLException, StatementHandlerException {
        ResultSet rs = null;
        try {
            // Retrieve the name of the primary key, since we cannot remove the not-null constraint on this column
            List<String> primaryKeyColumnNames = getPrimaryKeyColumnNames(conn, tableName);
            // Iterate over all column names
            DatabaseMetaData metaData = conn.getMetaData();
            rs = metaData.getColumns(null, schemaName, tableName, null);
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME");
                // Check if the column is not the primary key column
                if (!primaryKeyColumnNames.contains(columnName)) {
                    // Check if the column has a not-null constraint
                    boolean nullable = rs.getBoolean("NULLABLE");
                    if (!nullable) {
                        // Remove the not-null constraint. Disabling is not possible in MySQL
                        String columnType = rs.getString("COLUMN_TYPE");
                        // In MySQL, there is no convenient way to drop a not null constraint on a column definition.
                        // Therefore, we use the alter table modfiy syntax to redefine the column, and leave off the 
                        // not null part. This has as a side effect that if a column default has been set, this is 
                        // also left out.
                        String makeNullableSql = "alter table " + tableName + " modify column " + columnName 
                            + " " + columnType;
                        statementHandler.handle(makeNullableSql);
                    }
                }
            }
        } finally {
            DbUtils.closeQuietly(rs);
        }
    }
    
    
    
    /**
     * Returns the names of the primary key columns of the table with the given tableName
     * @param conn
     * @param tableName
     * @return the names of the primary key columns of the table with the given tableName
     * @throws SQLException
     */
    private List<String> getPrimaryKeyColumnNames(Connection conn, String tableName) throws SQLException {
        ResultSet rs = null;
        try {
            DatabaseMetaData metaData = conn.getMetaData();
            List<String> primaryKeyColumnNames = new ArrayList<String>(1);
            rs = metaData.getPrimaryKeys(null, schemaName, tableName);
            while (rs.next()) {
                primaryKeyColumnNames.add(rs.getString("COLUMN_NAME"));
            }
            return primaryKeyColumnNames;
        } finally {
            DbUtils.closeQuietly(rs);
        }
    }
    
    /**
     * Returns the names of all tables as a <code>List</code>
     * @param conn
     * The database connection
     * @return
     * The names of all database tables
     * @throws SQLException
     */
    private List<String> getTableNames(Connection conn) throws SQLException {
        ResultSet rs = null;
        try {
            List<String> tableNames = new ArrayList<String>();
            DatabaseMetaData metaData = conn.getMetaData();
            rs = metaData.getTables(null, schemaName, null, null);
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                tableNames.add(tableName);
            }
            return tableNames;
        } finally {
            DbUtils.closeQuietly(rs);
        }
    }

    /**
     * @see ConstraintsDisabler#disableConstraintsOnConnection(java.sql.Connection)
     */
    public void disableConstraintsOnConnection(Connection conn) {
        try {
            Statement st = conn.createStatement();
            st.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");
        } catch (SQLException e) {
            throw new UnitilsException("Error while disabling contstraints", e);
        }
    }
}
