package org.unitils.dbmaintainer.maintainer.clear;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.dbutils.DbUtils;
import org.unitils.UnitilsJUnit3;
import org.unitils.core.UnitilsConfigurationLoader;
import org.unitils.db.annotations.AfterCreateDataSource;
import org.unitils.dbmaintainer.clean.DefaultDBCleaner;
import org.unitils.dbmaintainer.clear.BaseDBClearer;
import org.unitils.dbmaintainer.clear.DBClearer;
import org.unitils.dbmaintainer.handler.JDBCStatementHandler;
import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.maintainer.DBMaintainer;
import org.unitils.dbunit.DatabaseTest;
import org.unitils.util.ReflectionUtils;

import javax.sql.DataSource;
import java.sql.*;

/**
 */
@DatabaseTest
public class DBClearerTest extends UnitilsJUnit3 {

    protected DataSource dataSource;

    protected DBClearer dbClearer;

    protected String schemaName;

    @AfterCreateDataSource
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected void setUp() throws Exception {
        super.setUp();

        Configuration configuration = new UnitilsConfigurationLoader().loadConfiguration();
        configuration.addProperty(DefaultDBCleaner.PROPKEY_TABLESTOPRESERVE, "testtable2");

        schemaName = configuration.getString(BaseDBClearer.PROPKEY_DATABASE_SCHEMANAME).toUpperCase();

        StatementHandler statementHandler = new JDBCStatementHandler();
        statementHandler.init(configuration, dataSource);

        dbClearer = ReflectionUtils.createInstanceOfType(configuration.getString(DBMaintainer.PROPKEY_DBCLEARER_START + '.' +
                configuration.getString(DBMaintainer.PROPKEY_DATABASE_DIALECT)));
        dbClearer.init(configuration, dataSource, statementHandler);

        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            createTestTables(conn);
            createTestIndex(conn);
            createTestView(conn);
        } finally {
            DbUtils.closeQuietly(conn);
        }

    }

    protected void tearDown() throws Exception {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            dropTestView(conn);
            dropTestTables(conn);
        } finally {
            DbUtils.closeQuietly(conn);
        }

        super.tearDown();
    }

    public void testClearDatabase_tables() throws Exception {
        assertTrue(tableExists("testtable1"));
        assertTrue(tableExists("db_version"));
        dbClearer.clearDatabase();
        assertFalse(tableExists("testtable1"));
        assertTrue(tableExists("db_version"));
    }

    public void testClearDatabase_views() throws Exception {
        assertTrue(tableExists("testview"));
        dbClearer.clearDatabase();
        assertFalse(tableExists("testview"));
    }

    private void createTestTables(Connection conn) throws SQLException {
        Statement st = null;
        try {
            conn = dataSource.getConnection();
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
            conn = dataSource.getConnection();
            st = conn.createStatement();
            // Make sure previous setup is cleaned up
            st.execute("drop table testtable1 if exists");
            st.execute("drop table db_version if exists");
        } finally {
            DbUtils.closeQuietly(st);
        }
    }

    private void createTestView(Connection conn) throws SQLException {
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            // Make sure previous setup is cleaned up
            st.execute("create view testview as select col1 from db_version");
        } finally {
            DbUtils.closeQuietly(conn, st, null);
        }
    }

    private void dropTestView(Connection conn) throws SQLException {
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            // Make sure previous setup is cleaned up
            st.execute("drop view testview if exists");
        } finally {
            DbUtils.closeQuietly(conn, st, null);
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

}
