/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils.db.maintainer.version;

import be.ordina.unitils.util.PropertiesUtils;
import org.apache.commons.dbutils.DbUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Properties;

/**
 * Implementation of <code>VersionSource</code> that stores the version in the database
 */
public class DBVersionSource implements VersionSource {

    /**
     * The <code>DataSource</code> that provides the connection to the database
     */
    private DataSource dataSource;

    /**
     * The key of the property that specifies the name of the datase table in which the
     * DB version is stored
     */
    private static final String PROPKEY_VERSION_TABLE_NAME = "dbMaintainer.dbVersionSource.tableName";

    /**
     * The key of the property that specifies the name of the column in which the
     * DB version is stored
     */
    private static final String PROPKEY_VERSION_COLUMN_NAME = "dbMaintainer.dbVersionSource.columnName";

    /**
     * The key of the property that specifies the schema name of the database
     */
    private static final String PROPKEY_DATABASE_USERNAME = "dataSource.userName";

    /**
     * The name of the database schema
     */
    private String schemaName;

    /**
     * The name of the datase table in which the DB version is stored
     */
    private String tableName;

    /**
     * The name of the datase column in which the DB version is stored
     */
    private String columnName;

    /**
     * Initializes with the given <code>Properties</code> and <code>DataSource</code>. The <code>Properties</code>
     * object should at least contain the properties {@link PROPKEY_VERSION_TABLE_NAME} and
     * {@link PROPKEY_VERSION_COLUMN_NAME}
     *
     * @param properties
     * @param dataSource
     */
    public void init(Properties properties, DataSource dataSource) {
        this.schemaName = PropertiesUtils.getPropertyRejectNull(properties, PROPKEY_DATABASE_USERNAME).toUpperCase();
        this.tableName = PropertiesUtils.getPropertyRejectNull(properties, PROPKEY_VERSION_TABLE_NAME).toUpperCase();
        this.columnName = PropertiesUtils.getPropertyRejectNull(properties, PROPKEY_VERSION_COLUMN_NAME).toUpperCase();
        this.dataSource = dataSource;
    }

    /**
     * @see be.ordina.unitils.db.maintainer.version.DBVersionSource#getDbVersion()
     */
    public Long getDbVersion() {
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            checkVersionTable(conn);
            st = conn.createStatement();
            rs = st.executeQuery("select " + columnName + " from " + tableName);
            rs.next();
            return rs.getLong(columnName);
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
    private void checkVersionTable(Connection conn) {
        Statement st = null;
        ResultSet rs = null;
        try {
            st = conn.createStatement();
            // Check if the version table exists
            DatabaseMetaData metadata = conn.getMetaData();
            rs = metadata.getTables(null, schemaName, tableName, null);
            if (!rs.next()) {
                // The version table does not exist. Create it
                 st.execute("create table " + tableName + " ( " + columnName + " number(20) )");
            } else {
                // Check if the version table has the expected column
                rs = metadata.getColumns(null, schemaName, tableName, columnName);
                if (!rs.next()) {
                    // The version table exists but the column does not. Create it
                    st.execute("alter table " + tableName + " add " + columnName + " number(20)");
                }
            }
            // The version table and column exist. Check if a record with the version is available
            rs = st.executeQuery("select " + columnName + " from " + tableName);
            if (!rs.next()) {
                // The version table is empty. Insert a record with version number 0.
                st.execute("insert into " + tableName + " (" + columnName + ") values ('')");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while checking version table", e);
        } finally {
            DbUtils.closeQuietly(null, st, rs);
        }
    }

    /**
     * @see VersionSource#setDbVersion(Long)
     */
    public void setDbVersion(Long version) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            checkVersionTable(conn);
            ps = conn.prepareStatement("update " + tableName + " set " + columnName + " = ?");
            ps.setObject(1, version);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error while incrementing database version", e);
        } finally {
            DbUtils.closeQuietly(conn, ps, null);
        }
    }

}
