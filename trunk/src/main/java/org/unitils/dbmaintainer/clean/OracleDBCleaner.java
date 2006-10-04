package org.unitils.dbmaintainer.clean;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.dbutils.DbUtils;
import org.unitils.core.UnitilsException;
import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.handler.StatementHandlerException;
import org.unitils.util.UnitilsConfiguration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * todo Configuration object should be supplied externally
 * todo Use same naming pattern for property name constants
 */
public class OracleDBCleaner implements DBCleaner {

    /* Property keys of the database schema name */
    private static final String PROPKEY_DATABASE_USERNAME = "dataSource.userName";

    private static final String PROPKEY_TABLESTOPRESERVE = "dbMaintainer.tablesToPreserve";

    /* The key of the property that specifies the name of the datase table in which the
     * DB version is stored. This table should not be deleted */
    private static final String PROPKEY_VERSION_TABLE_NAME = "dbMaintainer.dbVersionSource.tableName";

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

    public void init(DataSource dataSource, StatementHandler statementHandler) {
        this.dataSource = dataSource;
        this.statementHandler = statementHandler;

        Configuration configuration = UnitilsConfiguration.getInstance();
        schemaName = configuration.getString(PROPKEY_DATABASE_USERNAME);

        tablesToPreserve = new HashSet<String>();
        tablesToPreserve.add(configuration.getString(PROPKEY_VERSION_TABLE_NAME));
        tablesToPreserve.addAll(Arrays.asList(configuration.getStringArray(PROPKEY_TABLESTOPRESERVE)));
    }

    public void cleanDatabase() throws StatementHandlerException {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();

            Set<String> tableNames = getTableNames(conn);
            tableNames.removeAll(tablesToPreserve);
            clearTables(tableNames);
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
                tableNames.add(tableName);
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
}
