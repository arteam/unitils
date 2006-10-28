package org.unitils.dbmaintainer.clean;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.dbutils.DbUtils;
import org.unitils.UnitilsJUnit3;
import org.unitils.core.Unitils;
import org.unitils.db.annotations.DatabaseTest;
import org.unitils.db.annotations.TestDataSource;
import org.unitils.dbmaintainer.handler.JDBCStatementHandler;
import org.unitils.dbmaintainer.handler.StatementHandler;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 */
@DatabaseTest
@SuppressWarnings({"UnusedDeclaration"})
public class DBCleanerTest extends UnitilsJUnit3 {

    private DBCleaner dbCleaner;

    @TestDataSource
    private DataSource dataSource;

    
    protected void setUp() throws Exception {
        super.setUp();

        Configuration configuration = Unitils.getInstance().getConfiguration();
        configuration.addProperty(DefaultDBCleaner.PROPKEY_TABLESTOPRESERVE, "tabletopreserve");

        StatementHandler st = new JDBCStatementHandler();
        st.init(configuration, dataSource);

        dbCleaner = new DefaultDBCleaner();
        dbCleaner.init(configuration, dataSource, st);

        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            createTestTables(conn);
            insertTestData(conn);
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    protected void tearDown() throws Exception {
        super.tearDown();

        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            dropTestTables(conn);
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    private void dropTestTables(Connection conn) throws SQLException {
        Statement st = null;
        try {
            st = conn.createStatement();
            st.executeUpdate("drop table tabletoclear");
            st.executeUpdate("drop table db_version");
            st.executeUpdate("drop table tabletopreserve");
        } finally {
            DbUtils.closeQuietly(st);
        }
    }

    public void testCleanDatabase() throws Exception {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            assertTrue(testDataExists(conn));
            dbCleaner.cleanDatabase();
            assertFalse(testDataExists(conn));
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    public void testCleanDatabase_preserveDbVersionTable() throws Exception {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            assertTrue(dbVersionDataExists(conn));
            dbCleaner.cleanDatabase();
            assertTrue(dbVersionDataExists(conn));
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    public void testCleanDatabase_preserveTablesToPreserve() throws Exception {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            assertTrue(dataToPreserveExists(conn));
            dbCleaner.cleanDatabase();
            assertTrue(dataToPreserveExists(conn));
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    private void createTestTables(Connection conn) throws SQLException {
        Statement st = null;
        try {
            st = conn.createStatement();
            st.execute("create table tabletoclear(testcolumn varchar(10))");
            st.execute("create table db_version(testcolumn varchar(10))");
            st.execute("create table tabletopreserve(testcolumn varchar(10))");
        } finally {
            DbUtils.closeQuietly(st);
        }
    }

    private void insertTestData(Connection conn) throws SQLException {
        Statement st = null;
        try {
            st = conn.createStatement();
            st.execute("insert into tabletoclear values('test')");
            st.execute("insert into db_version values('test')");
            st.execute("insert into tabletopreserve values('test')");
        } finally {
            DbUtils.closeQuietly(st);
        }
    }

    private boolean testDataExists(Connection conn) throws SQLException {
        Statement st = null;
        ResultSet rs = null;
        try {
            st = conn.createStatement();
            rs = st.executeQuery("select * from tabletoclear");
            return rs.next();
        } finally {
            DbUtils.closeQuietly(null, st, rs);
        }
    }

    private boolean dbVersionDataExists(Connection conn) throws SQLException {
        Statement st = null;
        ResultSet rs = null;
        try {
            st = conn.createStatement();
            rs = st.executeQuery("select * from db_version");
            return rs.next();
        } finally {
            DbUtils.closeQuietly(null, st, rs);
        }
    }

    private boolean dataToPreserveExists(Connection conn) throws SQLException {
        Statement st = null;
        ResultSet rs = null;
        try {
            st = conn.createStatement();
            rs = st.executeQuery("select * from tabletopreserve");
            return rs.next();
        } finally {
            DbUtils.closeQuietly(null, st, rs);
        }
    }

}
