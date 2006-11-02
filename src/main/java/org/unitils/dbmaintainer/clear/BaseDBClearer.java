/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.dbmaintainer.clear;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.dbutils.DbUtils;
import org.unitils.core.UnitilsException;
import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.handler.StatementHandlerException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Base implementation of {@link DBClearer}. This implementation uses plain JDBC and standard SQL
 * for most of the work, and defers DBMS specific work to its subclasses.
 */
abstract public class BaseDBClearer implements DBClearer {

    /* Property keys of the database schema name */
    public static final String PROPKEY_DATABASE_SCHEMANAME = "dataSource.schemaName";

    /*
     * The key of the property that specifies the name of the datase table in which the DB version
     * is stored. This table should not be deleted
     */
    public static final String PROPKEY_VERSION_TABLE_NAME = "dbMaintainer.dbVersionSource.tableName";

    /* The TestDataSource */
    protected DataSource dataSource;

    /* The StatementHandler */
    protected StatementHandler statementHandler;

    /* The name of the database schema */
    protected String schemaName;

    /**
     * Initializes the connection to the database
     *
     * @param configuration
     * @param ds
     * @param sh
     */
    public void init(Configuration configuration, DataSource ds, StatementHandler sh) {
        this.dataSource = ds;
        this.statementHandler = sh;

        schemaName = configuration.getString(PROPKEY_DATABASE_SCHEMANAME).toUpperCase();
    }

    /**
     * Clears the database schema.
     *
     * @throws StatementHandlerException
     */
    public void clearDatabase() throws StatementHandlerException {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();

            dropViews(conn);
            dropTables(conn);
            dropSequences(conn);
            dropTriggers(conn);
        } catch (SQLException e) {
            throw new UnitilsException("Error while clearing database", e);
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    /**
     * Drops all views.
     *
     * @param conn
     * @throws SQLException
     * @throws StatementHandlerException
     */
    private void dropViews(Connection conn) throws SQLException, StatementHandlerException {
        List<String> viewNames = getViewNames(conn);
        for (String viewName : viewNames) {
            dropView(viewName);
        }
    }

    /**
     * Drops all tables.
     *
     * @param conn
     * @throws SQLException
     * @throws StatementHandlerException
     */
    private void dropTables(Connection conn) throws SQLException, StatementHandlerException {
        List<String> tableNames = getTableNames(conn);
        for (String tableName : tableNames) {
            dropTable(tableName);
        }
    }

    /**
     * Removes the view with the given name from the database
     *
     * @param viewName
     * @throws SQLException
     * @throws StatementHandlerException
     */
    abstract protected void dropView(String viewName) throws SQLException,
            StatementHandlerException;

    /**
     * Removes the table with the given name from the database
     *
     * @param tableName
     * @throws SQLException
     * @throws StatementHandlerException
     */
    abstract protected void dropTable(String tableName) throws SQLException,
            StatementHandlerException;

    /**
     * Retrieves the names of all the views in the database schema.
     *
     * @param conn
     * @return the names of all views
     * @throws SQLException
     */
    private List<String> getViewNames(Connection conn) throws SQLException {
        ResultSet rset = null;
        try {
            List<String> tableNames = new ArrayList<String>();
            DatabaseMetaData databaseMetadata = conn.getMetaData();
            rset = databaseMetadata.getTables(null, schemaName.toUpperCase(), null,
                    new String[]{"VIEW"});
            while (rset.next()) {
                String tableName = rset.getString("TABLE_NAME");
                tableNames.add(tableName);
            }
            return tableNames;
        } finally {
            DbUtils.closeQuietly(rset);
        }
    }

    /**
     * Retrieves the names of all tables in the database schema.
     *
     * @param conn
     * @return
     * @throws SQLException
     */
    private List<String> getTableNames(Connection conn) throws SQLException {
        ResultSet rset = null;
        try {
            List<String> tableNames = new ArrayList<String>();
            DatabaseMetaData databaseMetadata = conn.getMetaData();
            rset = databaseMetadata.getTables(null, schemaName.toUpperCase(), null,
                    new String[]{"TABLE"});
            while (rset.next()) {
                String tableName = rset.getString("TABLE_NAME");
                tableNames.add(tableName);
            }
            return tableNames;
        } finally {
            DbUtils.closeQuietly(rset);
        }
    }

    /**
     * Drops all sequences in the database
     *
     * @param conn
     * @throws StatementHandlerException
     * @throws SQLException
     */
    abstract protected void dropSequences(Connection conn) throws StatementHandlerException,
            SQLException;

    /**
     * Drops all database triggers
     *
     * @param conn
     * @throws StatementHandlerException
     * @throws SQLException
     */
    abstract protected void dropTriggers(Connection conn) throws StatementHandlerException,
            SQLException;

}
