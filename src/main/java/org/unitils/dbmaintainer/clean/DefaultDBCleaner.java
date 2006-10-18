/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.dbmaintainer.clean;

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
import java.util.*;

/**
 * Implementation of {@link DBCleaner}. This implementation doesn't use any DBMS specific features, so it should work
 * for every database.
 */
public class DefaultDBCleaner implements DBCleaner {

    /* Property key for the database schema name */
    public static final String PROPKEY_DATABASE_SCHEMANAME = "dataSource.schemaName";

    /* Property key for the tables that should not be cleaned */
    public static final String PROPKEY_TABLESTOPRESERVE = "dbMaintainer.tablesToPreserve";

    /* The key of the property that specifies the name of the datase table in which the
     * DB version is stored. This table should not be deleted */
    public static final String PROPKEY_VERSION_TABLE_NAME = "dbMaintainer.dbVersionSource.tableName";

    /* The TestDataSource */
    private DataSource dataSource;

    /* The StatementHandler */
    private StatementHandler statementHandler;

    /* The name of the database schema */
    private String schemaName;

    /* The tables that should not be cleaned */
    private Set<String> tablesToPreserve;

    /**
     * Configures this object
     * @param configuration
     * @param dataSource
     * @param statementHandler
     */
    public void init(Configuration configuration, DataSource dataSource, StatementHandler statementHandler) {
        this.dataSource = dataSource;
        this.statementHandler = statementHandler;

        schemaName = configuration.getString(PROPKEY_DATABASE_SCHEMANAME);

        tablesToPreserve = new HashSet<String>();
        tablesToPreserve.add(configuration.getString(PROPKEY_VERSION_TABLE_NAME));
        tablesToPreserve.addAll(toUpperCaseList(Arrays.asList(configuration.getStringArray(PROPKEY_TABLESTOPRESERVE))));
    }

    /**
     * Deletes all data from all tables in the database, except
     * @throws StatementHandlerException
     */
    public void cleanDatabase() throws StatementHandlerException {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();

            Set<String> tables = getTableNames(conn);
            tables.removeAll(tablesToPreserve);
            clearTables(tables);
        } catch (SQLException e) {
            throw new UnitilsException("Error while cleaning database", e);
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    /**
     * Returns the names of all tables in the database.
     * @param conn
     * @return the names of all tables in the database.
     * @throws SQLException
     */
    private Set<String> getTableNames(Connection conn) throws SQLException {
        ResultSet rset = null;
        try {
            Set<String> tableNames = new HashSet<String>();
            DatabaseMetaData databaseMetadata = conn.getMetaData();
            rset = databaseMetadata.getTables(null, schemaName.toUpperCase(), null, null);
            while (rset.next()) {
                String tableName = rset.getString("TABLE_NAME");
                tableNames.add(tableName.toUpperCase());
            }
            return tableNames;
        } finally {
            DbUtils.closeQuietly(rset);
        }
    }

    /**
     * Deletes the data in the database tables with the given table names.
     * @param tableNames
     * @throws StatementHandlerException
     */
    private void clearTables(Set<String> tableNames) throws StatementHandlerException {
        for (String tableName : tableNames) {
            statementHandler.handle("delete from " + tableName);
        }
    }

    /**
     * Converts the given list of strings to uppercase.
     * @param strings
     * @return the given string list, converted to uppercase
     */
    private List<String> toUpperCaseList(List<String> strings) {
        List<String> toUpperCaseList = new ArrayList<String>();
        for (String string : strings) {
            toUpperCaseList.add(string.toUpperCase());
        }
        return toUpperCaseList;
    }
}
