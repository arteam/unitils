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
 * todo Configuration object should be supplied externally
 * todo Use same naming pattern for property name constants
 */
public class DefaultDBCleaner implements DBCleaner {

    /* Property key for the database schema name */
    public static final String PROPKEY_DATABASE_SCHEMANAME = "dataSource.schemaName";

    /* Property key for the tables that should not be cleaned */
    public static final String PROPKEY_TABLESTOPRESERVE = "dbMaintainer.tablesToPreserve";

    /* The key of the property that specifies the name of the datase table in which the
     * DB version is stored. This table should not be deleted */
    public static final String PROPKEY_VERSION_TABLE_NAME = "dbMaintainer.dbVersionSource.tableName";

    /**
     * The DataSource
     */
    private DataSource dataSource;

    /**
     * The StatementHandler
     */
    private StatementHandler statementHandler;

    /* The name of the database schema */
    private String schemaName;

    /* The tables that should not be cleaned */
    private Set<String> tablesToPreserve;

    public void init(Configuration configuration, DataSource dataSource, StatementHandler statementHandler) {
        this.dataSource = dataSource;
        this.statementHandler = statementHandler;

        schemaName = configuration.getString(PROPKEY_DATABASE_SCHEMANAME);

        tablesToPreserve = new HashSet<String>();
        tablesToPreserve.add(configuration.getString(PROPKEY_VERSION_TABLE_NAME));
        tablesToPreserve.addAll(toUpperCaseList(Arrays.asList(configuration.getStringArray(PROPKEY_TABLESTOPRESERVE))));
    }

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

    private void clearTables(Set<String> tableNames) throws StatementHandlerException {
        for (String tableName : tableNames) {
            statementHandler.handle("delete from " + tableName);
        }
    }

    private List<String> toUpperCaseList(List<String> strings) {
        List<String> toUpperCaseList = new ArrayList<String>();
        for (String string : strings) {
            toUpperCaseList.add(string.toUpperCase());
        }
        return toUpperCaseList;
    }
}
