package org.unitils.dbmaintainer.clear;

import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.handler.StatementHandlerException;
import org.unitils.util.UnitilsConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.dbutils.DbUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * todo remove views, triggers, functions, stored procedures
 */
public class OracleDBClearer implements DBClearer {

    /* Property keys of the database schema name */
    private static final String PROPKEY_DATABASE_USERNAME = "dataSource.userName";

    /**
     * The key of the property that specifies the name of the datase table in which the
     * DB version is stored. This table should not be deleted
     */
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

    /* The name of the version table */
    private String versionTableName;

    public void init(DataSource dataSource, StatementHandler statementHandler) {
        this.dataSource = dataSource;
        this.statementHandler = statementHandler;

        Configuration configuration = UnitilsConfiguration.getInstance();
        schemaName = configuration.getString(PROPKEY_DATABASE_USERNAME);
        versionTableName = configuration.getString(PROPKEY_VERSION_TABLE_NAME);
    }

    public void clearDatabase() throws StatementHandlerException {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            Statement st = conn.createStatement();

            dropTables(conn);
            dropSequences(st);
        } catch (SQLException e) {
            throw new RuntimeException("Error while clearing database", e);
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    private void dropTables(Connection conn) throws SQLException, StatementHandlerException {
        ResultSet rset = null;
        try {
            DatabaseMetaData databaseMetadata = conn.getMetaData();
            rset = databaseMetadata.getTables(null, schemaName.toUpperCase(), null, null);
            while (rset.next()) {
                String tableName = rset.getString("TABLE_NAME");
                if (!tableName.equalsIgnoreCase(versionTableName)) {
                    String dropTableSQL = "drop table " + tableName + " cascade constraints";
                    statementHandler.handle(dropTableSQL);
                }
            }
        } finally {
            DbUtils.closeQuietly(rset);
        }
    }

    private void dropSequences(Statement st) throws SQLException, StatementHandlerException {
        ResultSet rset = null;
        try {
            rset = st.executeQuery("select SEQUENCE_NAME from USER_SEQUENCES");
            List<String> dropStatements = new ArrayList<String>();
            while (rset.next()) {
                dropStatements.add("drop sequence " + rset.getString("SEQUENCE_NAME"));
            }
            for (String dropStatement : dropStatements) {
                statementHandler.handle(dropStatement);
            }
        } finally {
            DbUtils.closeQuietly(rset);
        }
    }
}
