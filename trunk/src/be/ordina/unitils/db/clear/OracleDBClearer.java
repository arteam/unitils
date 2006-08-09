package be.ordina.unitils.db.clear;

import be.ordina.unitils.db.handler.StatementHandler;
import be.ordina.unitils.db.handler.StatementHandlerException;
import be.ordina.unitils.util.PropertiesUtils;
import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Filip Neven
 */
public class OracleDBClearer implements DBClearer {

    private static final Logger logger = Logger.getLogger(OracleDBClearer.class);


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

    public void init(Properties properties, DataSource dataSource, StatementHandler statementHandler) {
        this.dataSource = dataSource;
        this.statementHandler = statementHandler;
        schemaName = PropertiesUtils.getPropertyRejectNull(properties, PROPKEY_DATABASE_USERNAME);
        versionTableName = PropertiesUtils.getPropertyRejectNull(properties, PROPKEY_VERSION_TABLE_NAME);
    }

    public void clearDatabase() throws StatementHandlerException {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            Statement st = conn.createStatement();

            dropTables(conn, st);
            dropSequences(conn, st);
        } catch (SQLException e) {
            throw new RuntimeException("Error while clearing database", e);
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    private void dropTables(Connection conn, Statement st) throws SQLException, StatementHandlerException {
        ResultSet rset = null;
        try {
            DatabaseMetaData databaseMetadata = conn.getMetaData();
            rset = databaseMetadata.getTables(null, schemaName.toUpperCase(), null, null);
            while (rset.next())  {
                String tableName = rset.getString("TABLE_NAME");
                if (!tableName.equalsIgnoreCase(versionTableName)) {
                    String dropTableSQL = "drop table " + tableName + " cascade constraints";
                    logger.info(dropTableSQL);
                    statementHandler.handle(dropTableSQL);
                }
            }
        } finally {
            DbUtils.closeQuietly(rset);
        }
    }

    private void dropSequences(Connection conn, Statement st) throws SQLException, StatementHandlerException {
        ResultSet rset = null;
        try {
            rset = st.executeQuery("select SEQUENCE_NAME from USER_SEQUENCES");
            List<String> dropStatements = new ArrayList<String>();
            while (rset.next()) {
                dropStatements.add("drop sequence " + rset.getString("SEQUENCE_NAME"));
            }
            for (String dropStatement : dropStatements) {
                logger.info(dropStatement);
                statementHandler.handle(dropStatement);
            }
        } finally {
            DbUtils.closeQuietly(rset);
        }
    }
}
