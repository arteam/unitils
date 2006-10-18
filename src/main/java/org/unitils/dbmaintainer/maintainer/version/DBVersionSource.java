/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.dbmaintainer.maintainer.version;

import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.handler.StatementHandlerException;
import org.unitils.core.UnitilsException;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.dbutils.DbUtils;

import javax.sql.DataSource;
import java.sql.*;

/**
 * Implementation of <code>VersionSource</code> that stores the version in the database
 */
public class DBVersionSource implements VersionSource {

    /**
     * The <code>TestDataSource</code> that provides the connection to the database
     */
    private DataSource dataSource;

    /**
     * The StatementHandler
     */
    private StatementHandler statementHandler;

    /**
     * The key of the property that specifies the name of the datase table in which the
     * DB version is stored
     */
    public static final String PROPKEY_VERSION_TABLE_NAME = "dbMaintainer.dbVersionSource.tableName";

    /**
     * The key of the property that specifies the name of the column in which the DB version index is stored
     */
    public static final String PROPKEY_VERSION_INDEX_COLUMN_NAME = "dbMaintainer.dbVersionSource.versionIndexColumnName";

    /**
     * The key of the property that specifies the name of the column in which the DB version index is stored
     */
    public static final String PROPKEY_VERSION_TIMESTAMP_COLUMN_NAME = "dbMaintainer.dbVersionSource.versionTimeStampColumnName";

    /**
     * The start of the key of the property that specifies the type of the column in which the database version is stored.
     */
    public static final String PROPKEY_VERSION_COLUMN_DATATYPE_START = "dbMaintainer.dbVersionSource.versionColumnDataType";

    /**
     * The key of the property that specifies the schema name of the database
     */
    public static final String PROPKEY_SCHEMANAME = "dataSource.schemaName";

    /**
     *  Property key of the SQL dialect of the underlying DBMS implementation
     */
    private static final String PROPKEY_DATABASE_DIALECT = "database.dialect";

    /**
     * The name of the database schema
     */
    private String schemaName;

    /**
     * The name of the datase table in which the DB version is stored
     */
    private String tableName;

    /**
     * The name of the datase column in which the DB version index is stored
     */
    private String versionIndexColumnName;

    /**
     * The name of the datase column in which the DB version timestamp is stored
     */
    private String versionTimestampColumnName;

    /**
     * The type of the database columns in which the version index and version timestamps are stored. This data type
     * should be sufficiently large to be able to store values of the <code>long</code> Java type.
     */
    private String versionColumnType;

    /**
     * Initializes with the given <code>Properties</code> and <code>TestDataSource</code>.
     *
     * @param dataSource
     */
    public void init(Configuration configuration, DataSource dataSource, StatementHandler statementHandler) {

        this.schemaName = configuration.getString(PROPKEY_SCHEMANAME).toUpperCase();
        this.tableName = configuration.getString(PROPKEY_VERSION_TABLE_NAME).toUpperCase();
        this.versionIndexColumnName = configuration.getString(PROPKEY_VERSION_INDEX_COLUMN_NAME).toUpperCase();
        this.versionTimestampColumnName = configuration.getString(PROPKEY_VERSION_TIMESTAMP_COLUMN_NAME).toUpperCase();
        this.versionColumnType = configuration.getString(PROPKEY_VERSION_COLUMN_DATATYPE_START + '.' +
                configuration.getString(PROPKEY_DATABASE_DIALECT));
        this.dataSource = dataSource;
        this.statementHandler = statementHandler;
    }

    /**
     * @see org.unitils.dbmaintainer.maintainer.version.DBVersionSource#getDbVersion()
     */
    public Version getDbVersion() throws StatementHandlerException {
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            checkVersionTable(conn);
            st = conn.createStatement();
            rs = st.executeQuery("select " + versionIndexColumnName + ", " + versionTimestampColumnName + " from " + tableName);
            rs.next();
            return new Version(rs.getLong(versionIndexColumnName), rs.getLong(versionTimestampColumnName));
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving database version", e);
        } finally {
            DbUtils.closeQuietly(conn, st, rs);
        }
    }

    /**
     * Checks if the version table a column are available and if a record exists with the version number. If
     * not, create the table, column and/or record.
     *
     * @param conn
     */
    private void checkVersionTable(Connection conn) throws StatementHandlerException {
        Statement st = null;
        ResultSet rs = null;
        try {
            st = conn.createStatement();
            // Check if the version table exists
            DatabaseMetaData metadata = conn.getMetaData();
            rs = metadata.getTables(null, schemaName, tableName, null);
            if (!rs.next()) {
                // The version table does not exist. Create it
                statementHandler.handle("create table " + tableName + " ( " + versionIndexColumnName + " " + versionColumnType +
                        ", " + versionTimestampColumnName + " " + versionColumnType + " )");
            } else {
                // Check if the version table has the expected column
                rs = metadata.getColumns(null, schemaName, tableName, versionIndexColumnName);
                if (!rs.next()) {
                    // The version table exists but the column does not. Create it
                    statementHandler.handle("alter table " + tableName + " add " + versionIndexColumnName + " " + versionColumnType);
                }
                rs = metadata.getColumns(null, schemaName, tableName, versionTimestampColumnName);
                if (!rs.next()) {
                    // The version table exists but the column does not. Create it
                    statementHandler.handle("alter table " + tableName + " add " + versionTimestampColumnName + " " + versionColumnType);
                }
            }
            // The version table and column exist. Check if a record with the version is available
            rs = st.executeQuery("select * from " + tableName);
            if (!rs.next()) {
                // The version table is empty. Insert a record with version number 0.
                statementHandler.handle("insert into " + tableName + " (" + versionIndexColumnName + ", " + versionTimestampColumnName +
                        ") values (0, 0)");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while checking version table", e);
        } finally {
            DbUtils.closeQuietly(null, st, rs);
        }
    }

    /**
     * @see VersionSource#setDbVersion(Version)
     */
    public void setDbVersion(Version version) throws StatementHandlerException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            checkVersionTable(conn);
            ps = conn.prepareStatement("update " + tableName + " set " + versionIndexColumnName + " = ?, " +
                    versionTimestampColumnName + " = ?");
            ps.setLong(1, version.getIndex());
            ps.setLong(2, version.getTimeStamp());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new UnitilsException("Error while incrementing database version", e);
        } finally {
            DbUtils.closeQuietly(conn, ps, null);
        }
    }

}
