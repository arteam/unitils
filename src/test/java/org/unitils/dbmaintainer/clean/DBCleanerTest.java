package org.unitils.dbmaintainer.clean;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.dbutils.DbUtils;
import org.unitils.UnitilsJUnit3;
import org.unitils.core.UnitilsConfigurationLoader;
import org.unitils.db.annotations.TestDataSource;
import org.unitils.db.annotations.DatabaseTest;
import org.unitils.dbmaintainer.handler.JDBCStatementHandler;
import org.unitils.dbmaintainer.handler.StatementHandler;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 */
@DatabaseTest
public class DBCleanerTest extends UnitilsJUnit3 {

    private javax.sql.DataSource dataSource;

    private DBCleaner dbCleaner;

    @TestDataSource
    public void setDataSource(javax.sql.DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected void setUp() throws Exception {
        super.setUp();

        Configuration configuration = new UnitilsConfigurationLoader().loadConfiguration();
        configuration.addProperty(DefaultDBCleaner.PROPKEY_TABLESTOPRESERVE, "testtable2,testtable3");

        StatementHandler statementHandler = new JDBCStatementHandler();
        statementHandler.init(configuration, dataSource);

        dbCleaner = new DefaultDBCleaner();
        dbCleaner.init(configuration, dataSource, statementHandler);

        createTestTables();
        insertTestRecords();
    }

    private void createTestTables() throws SQLException {
        Connection conn = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            // Make sure previous setup is cleaned up
            st.execute("drop table testtable1 if exists");
            st.execute("drop table testtable2 if exists");
            st.execute("drop table testtable3 if exists");
            st.execute("create table testtable1 (col1 varchar(10))");
            st.execute("create table testtable2 (col1 varchar(10))");
            st.execute("create table testtable3 (col1 varchar(10))");
        } finally {
            DbUtils.closeQuietly(conn, st, null);
        }
    }

    public void testCleanDatabase() throws Exception {
        assertFalse(isTestTableEmpty("testtable1"));
        dbCleaner.cleanDatabase();
        assertTrue(isTestTableEmpty("testtable1"));
    }

    public void testCleanDatabase_tablesToPreserve() throws Exception {
        assertFalse(isTestTableEmpty("testtable1"));
        assertFalse(isTestTableEmpty("testtable2"));
        assertFalse(isTestTableEmpty("testtable3"));
        dbCleaner.cleanDatabase();
        assertTrue(isTestTableEmpty("testtable1"));
        assertFalse(isTestTableEmpty("testtable2"));
        assertFalse(isTestTableEmpty("testtable3"));
    }

    private void insertTestRecords() throws SQLException {
        Connection conn = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            st.execute("insert into testtable1 values('value1')");
            st.execute("insert into testtable2 values('value1')");
            st.execute("insert into testtable3 values('value1')");
        } finally {
            DbUtils.closeQuietly(conn, st, null);
        }
    }

    private boolean isTestTableEmpty(String tableName) throws SQLException {
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            rs = st.executeQuery("select * from " + tableName);
            return !rs.next();
        } finally {
            DbUtils.closeQuietly(conn, st, rs);
        }
    }

}
