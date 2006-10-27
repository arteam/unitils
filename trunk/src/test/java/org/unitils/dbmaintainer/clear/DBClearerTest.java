package org.unitils.dbmaintainer.clear;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.dbutils.DbUtils;
import org.unitils.UnitilsJUnit3;
import org.unitils.core.ConfigurationLoader;
import org.unitils.db.annotations.TestDataSource;
import org.unitils.db.annotations.DatabaseTest;
import org.unitils.dbmaintainer.clear.DBClearer;
import org.unitils.dbmaintainer.handler.JDBCStatementHandler;
import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.maintainer.DBMaintainer;
import org.unitils.util.ReflectionUtils;

/**
 */
@DatabaseTest
abstract public class DBClearerTest extends UnitilsJUnit3 {

    @TestDataSource
    protected javax.sql.DataSource dataSource;

    protected DBClearer dbClearer;

    protected String schemaName;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        Configuration configuration = new ConfigurationLoader().loadConfiguration();

        StatementHandler statementHandler = new JDBCStatementHandler();
        statementHandler.init(configuration, dataSource);

        dbClearer = ReflectionUtils.createInstanceOfType(configuration.getString(DBMaintainer.PROPKEY_DBCLEARER_START + '.' +
                configuration.getString(DBMaintainer.PROPKEY_DATABASE_DIALECT)));
        dbClearer.init(configuration, dataSource, statementHandler);

        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            dropTestView(conn);
            dropTestTables(conn);
            dropTestSequence(conn);
            createTestTables(conn);
            createTestIndex(conn);
            createTestView(conn);
            createTestSequence(conn);
        } finally {
            DbUtils.closeQuietly(conn);
        }

    }

    @Override
    protected void tearDown() throws Exception {
       /*Connection conn = null;
        try {
            conn = dataSource.getConnection();
            dropTestView(conn);
            dropTestTables(conn);
        } finally {
            DbUtils.closeQuietly(conn);
        }*/

        super.tearDown();
    }

    public void testClearDatabase_tables() throws Exception {
        if (isTestedDialectActivated()) {
            assertTrue(tableExists("testtable1"));
            assertTrue(tableExists("db_version"));
            dbClearer.clearDatabase();
            assertFalse(tableExists("testtable1"));
        }
    }

    public void testClearDatabase_views() throws Exception {
        if (isTestedDialectActivated()) {
            assertTrue(tableExists("testview"));
            dbClearer.clearDatabase();
            assertFalse(tableExists("testview"));
        }
    }
    
    private void createTestTables(Connection conn) throws SQLException {
        Statement st = null;
        try {
            st = conn.createStatement();
            st.execute("create table testtable1 (col1 varchar(10))");
            st.execute("create table db_version (col1 varchar(10))");
        } finally {
            DbUtils.closeQuietly(st);
        }
    }

    private void createTestIndex(Connection conn) throws SQLException {
        Statement st = null;
        try {
            st = conn.createStatement();
            st.execute("create index testindex on testtable1(col1)");
        } finally {
            DbUtils.closeQuietly(st);
        }
    }

    private void dropTestTables(Connection conn) throws SQLException {
        Statement st = null;
        try {
            st = conn.createStatement();
            // Make sure previous setup is cleaned up
            try {
                st.execute("drop table testtable1");
            } catch (SQLException e) {
                // no action taken
            }
            try {
                st.execute("drop table db_version");
            } catch (SQLException e) {
                // no action taken
            }
        }  finally {
            DbUtils.closeQuietly(st);
        }
    }

    private void createTestView(Connection conn) throws SQLException {
        Statement st = null;
        try {
            st = conn.createStatement();
            // Make sure previous setup is cleaned up
            st.execute("create view testview as select col1 from db_version");
        } finally {
            DbUtils.closeQuietly(st);
        }
    }

    private void dropTestView(Connection conn) {
        Statement st = null;
        try {
            st = conn.createStatement();
            // Make sure previous setup is cleaned up
            st.execute("drop view testview");
        } catch (SQLException e) {
            // No action is taken
        } finally {
            DbUtils.closeQuietly(st);
        }
    }

    private void createTestSequence(Connection conn) {
        Statement st = null;
        try {
            st = conn.createStatement();
            // Make sure previous setup is cleaned up
            st.execute("create sequence testsequence");
        } catch (SQLException e) {
            // No action is taken
        } finally {
            DbUtils.closeQuietly(st);
        }
    }

    private void dropTestSequence(Connection conn) {
        Statement st = null;
        try {
            st = conn.createStatement();
            // Make sure previous setup is cleaned up
            st.execute("drop sequence testsequence");
        } catch (SQLException e) {
            // No action is taken
        } finally {
            DbUtils.closeQuietly(st);
        }
    }

    private boolean tableExists(String tableName) throws SQLException {
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            DatabaseMetaData metaData = conn.getMetaData();
            rs = metaData.getTables(null, schemaName, tableName.toUpperCase(), null);
            while (rs.next()) {
                if (tableName.equalsIgnoreCase(rs.getString("TABLE_NAME"))) {
                    return true;
                }
            }
            return false;
        } finally {
            DbUtils.closeQuietly(conn, null, rs);
        }
    }

    abstract protected boolean isTestedDialectActivated();
}
