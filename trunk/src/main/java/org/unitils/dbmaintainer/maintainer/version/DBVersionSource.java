/*
 * Copyright 2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unitils.dbmaintainer.maintainer.version;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.dbutils.DbUtils;
import org.unitils.core.UnitilsException;
import org.unitils.dbmaintainer.dbsupport.DatabaseTask;
import org.unitils.dbmaintainer.handler.StatementHandlerException;

import java.sql.*;

/**
 * Implementation of <code>VersionSource</code> that stores the version in the database
 */
public class DBVersionSource extends DatabaseTask implements VersionSource {

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
     * The key of the property that specifies the name of the column in which is stored whether the last update succeeded.
     */
    public static final String PROPKEY_LAST_UPDATE_SUCCEEDED_COLUMN_NAME = "dbMaintainer.dbVersionSource.lastUpdateSucceededColumnName";

    /**
     * The key of the property that specifies the schema name of the database
     */
    public static final String PROPKEY_SCHEMANAME = "dataSource.schemaName";

    /**
     * The name of the datase table in which the DB version is stored
     */
    private String versionTableName;

    /**
     * The name of the datase column in which the DB version index is stored
     */
    private String versionIndexColumnName;

    /**
     * The name of the datase column in which the DB version timestamp is stored
     */
    private String versionTimestampColumnName;

    /**
     * The name of the database column in which is stored whether the last DB update succeeded
     */
    private String lastUpdateSucceededColumnName;

    protected void doInit(Configuration configuration) {
        this.versionTableName = configuration.getString(PROPKEY_VERSION_TABLE_NAME).toUpperCase();
        this.versionIndexColumnName = configuration.getString(PROPKEY_VERSION_INDEX_COLUMN_NAME).toUpperCase();
        this.versionTimestampColumnName = configuration.getString(PROPKEY_VERSION_TIMESTAMP_COLUMN_NAME).toUpperCase();
        this.lastUpdateSucceededColumnName = configuration.getString(PROPKEY_LAST_UPDATE_SUCCEEDED_COLUMN_NAME).toUpperCase();
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
            rs = st.executeQuery("select " + versionIndexColumnName + ", " + versionTimestampColumnName + " from " + versionTableName);
            rs.next();
            return new Version(rs.getLong(versionIndexColumnName), rs.getLong(versionTimestampColumnName));
        } catch (SQLException e) {
            throw new UnitilsException("Error while retrieving database version", e);
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
            rs = metadata.getTables(null, schemaName, versionTableName, null);
            String longDataType = dbSupport.getLongDataType();
            if (!rs.next()) {
                // The version table does not exist. Create it
                statementHandler.handle("create table " + versionTableName + " ( " + versionIndexColumnName + " " + longDataType +
                        ", " + versionTimestampColumnName + " " + longDataType + ", " + lastUpdateSucceededColumnName +
                        " " + longDataType + " )");
            } else {
                // Check if the version table has the expected column
                rs = metadata.getColumns(null, schemaName, versionTableName, versionIndexColumnName);
                if (!rs.next()) {
                    // The version table exists but the column does not. Create it
                    statementHandler.handle("alter table " + versionTableName + " add " + versionIndexColumnName + " " + longDataType);
                }
                rs = metadata.getColumns(null, schemaName, versionTableName, versionTimestampColumnName);
                if (!rs.next()) {
                    // The version table exists but the column does not. Create it
                    statementHandler.handle("alter table " + versionTableName + " add " + versionTimestampColumnName + " " + longDataType);
                }
                rs = metadata.getColumns(null, schemaName, versionTableName, lastUpdateSucceededColumnName);
                if (!rs.next()) {
                    // The version table exists but the column does not. Create it
                    statementHandler.handle("alter table " + versionTableName + " add " + lastUpdateSucceededColumnName + " " + longDataType);
                }
            }
            // The version table and column exist. Check if a record with the version is available
            rs = st.executeQuery("select * from " + versionTableName);
            if (!rs.next()) {
                // The version table is empty. Insert a record with version number 0.
                statementHandler.handle("insert into " + versionTableName + " (" + versionIndexColumnName + ", " + versionTimestampColumnName +
                        ") values (0, 0)");
            }
        } catch (SQLException e) {
            throw new UnitilsException("Error while checking version table", e);
        } finally {
            DbUtils.closeQuietly(null, st, rs);
        }
    }

    /**
     * @see VersionSource#setDbVersion(Version)
     */
    public void setDbVersion(Version version) throws StatementHandlerException {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            checkVersionTable(conn);
            statementHandler.handle("update " + versionTableName + " set " + versionIndexColumnName + " = " + version.getIndex() + ", " +
                    versionTimestampColumnName + " = " + version.getTimeStamp());
        } catch (SQLException e) {
            throw new UnitilsException("Error while incrementing database version", e);
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    public boolean lastUpdateSucceeded() {
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            rs = st.executeQuery("select " + lastUpdateSucceededColumnName + " from " + versionTableName);
            if (rs.next()) {
                return (rs.getInt(lastUpdateSucceededColumnName) == 1);
            } else {
                return false;
            }
        } catch (SQLException e) {
            throw new UnitilsException("Error while checking whether last update succeeded", e);
        } finally {
            DbUtils.closeQuietly(conn, st, rs);
        }
    }

    public void registerUpdateSucceeded(boolean succeeded) throws StatementHandlerException {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            checkVersionTable(conn);
            statementHandler.handle("update " + versionTableName + " set " + lastUpdateSucceededColumnName + " = " +
                    (succeeded ? "1" : "0"));
        } catch (SQLException e) {
            throw new UnitilsException("Error while registering database update success = " + succeeded, e);
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

}
