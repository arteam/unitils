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
     * The name of the datase table in which the  DB version is stored
     */
    private String tableName;

    /**
     * The name of the datase column in which the  DB version is stored
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
        this.tableName = PropertiesUtils.getPropertyRejectNull(properties, PROPKEY_VERSION_TABLE_NAME);
        this.columnName = PropertiesUtils.getPropertyRejectNull(properties, PROPKEY_VERSION_COLUMN_NAME);
        this.dataSource = dataSource;
    }

    /**
     * @see be.ordina.unitils.db.maintainer.version.DBVersionSource#getDbVersion()
     */
    public long getDbVersion() {
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
            DbUtils.closeQuietly(conn, st, null);
        }
    }

    /**
     * Checks if the version table is empty. If so, a new record is inserted
     *
     * @param conn
     */
    private void checkVersionTable(Connection conn) {
        Statement st = null;
        ResultSet rs = null;
        try {
            st = conn.createStatement();
            rs = st.executeQuery("select " + columnName + " from " + tableName);
            if (!rs.next()) {
                // The version table is empty, so this is the first time it is accessed. Set the version number to 1.
                st.execute("insert into " + tableName + " (" + columnName + ") values (0)");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while checking version table", e);
        } finally {
            DbUtils.closeQuietly(null, st, rs);
        }
    }

    /**
     * @see VersionSource#setDbVersion(long)
     */
    public void setDbVersion(long version) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            checkVersionTable(conn);
            ps = conn.prepareStatement("update " + tableName + " set " + columnName + " = ?");
            ps.setLong(1, version);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error while incrementing database version", e);
        } finally {
            DbUtils.closeQuietly(conn, ps, null);
        }
    }

}
