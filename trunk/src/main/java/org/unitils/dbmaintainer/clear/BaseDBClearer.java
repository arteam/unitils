package org.unitils.dbmaintainer.clear;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.dbutils.DbUtils;
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
 */
abstract public class BaseDBClearer implements DBClearer {

    /**
     * Property keys of the database schema name
     */
    public static final String PROPKEY_DATABASE_SCHEMANAME = "dataSource.schemaName";

    /**
     * The key of the property that specifies the name of the datase table in which the
     * DB version is stored. This table should not be deleted
     */
    public static final String PROPKEY_VERSION_TABLE_NAME = "dbMaintainer.dbVersionSource.tableName";

    /**
     * The DataSource
     */
    protected DataSource dataSource;

    /**
     * The StatementHandler
     */
    protected StatementHandler statementHandler;

    /* The name of the database schema */
    protected String schemaName;

    /* The name of the version table. This table will not be removed */
    protected String versionTableName;

    public void init(Configuration configuration, DataSource dataSource, StatementHandler statementHandler) {
        this.dataSource = dataSource;
        this.statementHandler = statementHandler;

        schemaName = configuration.getString(PROPKEY_DATABASE_SCHEMANAME);
        versionTableName = configuration.getString(PROPKEY_VERSION_TABLE_NAME);
    }

    public void clearDatabase() throws StatementHandlerException {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();

            dropViews(conn);
            dropTables(conn);
            dropSequences(conn);
            dropTriggers(conn);
        } catch (SQLException e) {
            throw new RuntimeException("Error while clearing database", e);
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    private void dropViews(Connection conn) throws SQLException, StatementHandlerException {
        List<String> viewNames = getViewNames(conn);
        for (String viewName : viewNames) {
            dropView(viewName);
        }
    }

    private void dropTables(Connection conn) throws SQLException, StatementHandlerException {
        List<String> tableNames = getTableNames(conn);
        for (String tableName : tableNames) {
            if (!tableName.equalsIgnoreCase(versionTableName)) {
                dropTable(tableName);
            }
        }
    }

    abstract protected void dropView(String viewName) throws SQLException, StatementHandlerException;

    abstract protected void dropTable(String tableName) throws SQLException, StatementHandlerException;

    private List<String> getViewNames(Connection conn) throws SQLException {
        ResultSet rset = null;
        try {
            List<String> tableNames = new ArrayList<String>();
            DatabaseMetaData databaseMetadata = conn.getMetaData();
            rset = databaseMetadata.getTables(null, schemaName.toUpperCase(), null, new String[]{"VIEW"});
            while (rset.next()) {
                String tableName = rset.getString("TABLE_NAME");
                tableNames.add(tableName);
            }
            return tableNames;
        } finally {
            DbUtils.closeQuietly(rset);
        }
    }

    private List<String> getTableNames(Connection conn) throws SQLException {
        ResultSet rset = null;
        try {
            List<String> tableNames = new ArrayList<String>();
            DatabaseMetaData databaseMetadata = conn.getMetaData();
            rset = databaseMetadata.getTables(null, schemaName.toUpperCase(), null, new String[]{"TABLE"});
            while (rset.next()) {
                String tableName = rset.getString("TABLE_NAME");
                tableNames.add(tableName);
            }
            return tableNames;
        } finally {
            DbUtils.closeQuietly(rset);
        }
    }

    abstract protected void dropSequences(Connection conn) throws StatementHandlerException, SQLException;

    abstract protected void dropTriggers(Connection conn) throws StatementHandlerException, SQLException;

}
