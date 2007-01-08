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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.UnitilsException;
import org.unitils.dbmaintainer.dbsupport.DatabaseTask;
import org.unitils.dbmaintainer.handler.StatementHandlerException;

import java.sql.*;

/**
 * Implementation of <code>VersionSource</code> that stores the version in the database. The version is stored in the
 * table whose name is defined by the property {@link #PROPKEY_VERSION_TABLE_NAME}. The version index column name is
 * defined by {@link #PROPKEY_VERSION_INDEX_COLUMN_NAME}, the version timestamp colmumn name is defined by
 * {@link #PROPKEY_VERSION_TIMESTAMP_COLUMN_NAME}. The last updated succeeded column name is defined by
 * {@link #PROPKEY_LAST_UPDATE_SUCCEEDED_COLUMN_NAME}.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DBVersionSource extends DatabaseTask implements VersionSource {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DBVersionSource.class);

    /* The key of the property that specifies the name of the datase table in which the DB version is stored */
    public static final String PROPKEY_VERSION_TABLE_NAME = "dbMaintainer.dbVersionSource.tableName";

    /* The key of the property that specifies the name of the column in which the DB version index is stored */
    public static final String PROPKEY_VERSION_INDEX_COLUMN_NAME = "dbMaintainer.dbVersionSource.versionIndexColumnName";

    /* The key of the property that specifies the name of the column in which the DB version index is stored */
    public static final String PROPKEY_VERSION_TIMESTAMP_COLUMN_NAME = "dbMaintainer.dbVersionSource.versionTimeStampColumnName";

    /* The key of the property that specifies the name of the column in which is stored whether the last update succeeded. */
    public static final String PROPKEY_LAST_UPDATE_SUCCEEDED_COLUMN_NAME = "dbMaintainer.dbVersionSource.lastUpdateSucceededColumnName";

    /* The name of the datase table in which the DB version is stored */
    private String versionTableName;

    /* The name of the datase column in which the DB version index is stored */
    private String versionIndexColumnName;

    /* The name of the datase column in which the DB version timestamp is stored */
    private String versionTimestampColumnName;

    /* The name of the database column in which is stored whether the last DB update succeeded */
    private String lastUpdateSucceededColumnName;

    /* True if the existence and structure of the version table was checked */
    private boolean versionTableChecked;


    /**
     * Initializes the name of the version table and its columns using the given <code>Configuration</code> object
     *
     * @param configuration the configuration, not null
     */
    protected void doInit(Configuration configuration) {
        this.versionTableChecked = false;
        this.versionTableName = configuration.getString(PROPKEY_VERSION_TABLE_NAME).toUpperCase();
        this.versionIndexColumnName = configuration.getString(PROPKEY_VERSION_INDEX_COLUMN_NAME).toUpperCase();
        this.versionTimestampColumnName = configuration.getString(PROPKEY_VERSION_TIMESTAMP_COLUMN_NAME).toUpperCase();
        this.lastUpdateSucceededColumnName = configuration.getString(PROPKEY_LAST_UPDATE_SUCCEEDED_COLUMN_NAME).toUpperCase();
    }


    /**
     * @return The current version of the database
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
     * Updates the version of the database to the given value
     *
     * @param version The new version that the database should be updated to
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


    /**
     * Tells us whether the last database version update succeeded or not
     *
     * @return true if the last database version update succeeded, false otherwise
     */
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


    /**
     * Notifies the VersionSource of the fact that the lastest version update has succeeded or not
     */
    public void registerUpdateSucceeded(boolean succeeded) throws StatementHandlerException {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            checkVersionTable(conn);
            statementHandler.handle("update " + versionTableName + " set " + lastUpdateSucceededColumnName + " = " + (succeeded ? "1" : "0"));

        } catch (SQLException e) {
            throw new UnitilsException("Error while registering database update success = " + succeeded, e);
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }


    /**
     * Checks if the version table and columns are available and if a record exists in which the version info is stored.
     * If not, the table, columns and record are created.
     *
     * @param connection The connection to the database, will not be closed, not null
     */
    protected void checkVersionTable(Connection connection) throws StatementHandlerException {

        // only perform check once, skip if already checked
        if (versionTableChecked) {
            return;
        }
        versionTableChecked = true;

        Statement st = null;
        ResultSet rs = null;
        try {
            st = connection.createStatement();
            // Check if the version table exists
            DatabaseMetaData metadata = connection.getMetaData();
            rs = metadata.getTables(null, schemaName, versionTableName, null);
            String longDataType = dbSupport.getLongDataType();
            if (!rs.next()) {
                // The version table does not exist. Create it
                logger.info("The table " + versionTableName + " doesn't exist yet. It is being created");
                statementHandler.handle("create table " + versionTableName + " ( " + versionIndexColumnName + " " + longDataType +
                        ", " + versionTimestampColumnName + " " + longDataType + ", " + lastUpdateSucceededColumnName + " " + longDataType + " )");
            } else {
                // Check if the version table has the expected column
                rs.close();
                rs = metadata.getColumns(null, schemaName, versionTableName, versionIndexColumnName);
                if (!rs.next()) {
                    // The version table exists but the version index column does not. Create it
                    logger.info("Column " + versionIndexColumnName + " is missing on table " + versionTableName + ". It is being created");
                    statementHandler.handle("alter table " + versionTableName + " add " + versionIndexColumnName + " " + longDataType);
                }
                rs.close();
                rs = metadata.getColumns(null, schemaName, versionTableName, versionTimestampColumnName);
                if (!rs.next()) {
                    // The version table exists but the version timestamp column does not. Create it
                    logger.info("Column " + versionTimestampColumnName + " is missing on table " + versionTableName + ". It is being created");
                    statementHandler.handle("alter table " + versionTableName + " add " + versionTimestampColumnName + " " + longDataType);
                }
                rs.close();
                rs = metadata.getColumns(null, schemaName, versionTableName, lastUpdateSucceededColumnName);
                if (!rs.next()) {
                    // The version table exists but the last update succeeded column does not. Create it
                    logger.info("Column " + lastUpdateSucceededColumnName + " is missing on table " + versionTableName + ". It is being created");
                    statementHandler.handle("alter table " + versionTableName + " add " + lastUpdateSucceededColumnName + " " + longDataType);
                }
            }
            // The version table and columns exist. Check if a record with the version is available
            rs.close();
            rs = st.executeQuery("select * from " + versionTableName);
            if (!rs.next()) {
                // The version table is empty. Insert a record with default version numbers.
                statementHandler.handle("insert into " + versionTableName + " (" + versionIndexColumnName + ", " +
                        versionTimestampColumnName + ", " + lastUpdateSucceededColumnName + ") values (0, 0, 0)");
            }
        } catch (SQLException e) {
            throw new UnitilsException("Error while checking version table", e);
        } finally {
            DbUtils.closeQuietly(null, st, rs);
        }
    }
}
