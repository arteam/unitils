package org.unitils.dbmaintainer.clean;

import org.unitils.dbunit.DatabaseTest;
import org.unitils.db.annotations.AfterCreateDataSource;
import org.unitils.UnitilsJUnit3;
import org.unitils.util.UnitilsConfiguration;
import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.handler.JDBCStatementHandler;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.configuration.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 */
@DatabaseTest
public class DBCleanerTest extends UnitilsJUnit3 {

    private DataSource dataSource;

    private DBCleaner dbCleaner;

    @AfterCreateDataSource
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected void setUp() throws Exception {
        super.setUp();

        Configuration config = UnitilsConfiguration.getInstance();
        config.addProperty(DefaultDBCleaner.PROPKEY_TABLESTOPRESERVE, "testtable2,testtable3");

        StatementHandler statementHandler = new JDBCStatementHandler();
        statementHandler.init(dataSource);

        dbCleaner = new DefaultDBCleaner();
        dbCleaner.init(dataSource, statementHandler);

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
