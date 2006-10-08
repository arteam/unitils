package org.unitils.dbmaintainer.maintainer.clear;

import org.unitils.UnitilsJUnit3;
import org.unitils.util.UnitilsConfiguration;
import org.unitils.util.ReflectionUtils;
import org.unitils.dbmaintainer.clear.DBClearer;
import org.unitils.dbmaintainer.clear.BaseDBClearer;
import org.unitils.dbmaintainer.maintainer.DBMaintainer;
import org.unitils.dbmaintainer.clean.DefaultDBCleaner;
import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.handler.JDBCStatementHandler;
import org.unitils.db.annotations.AfterCreateDataSource;
import org.unitils.dbunit.DatabaseTest;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.dbutils.DbUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

        Configuration config = UnitilsConfiguration.getInstance();
        config.addProperty(DefaultDBCleaner.PROPKEY_TABLESTOPRESERVE, "testtable2");

        schemaName = config.getString(BaseDBClearer.PROPKEY_DATABASE_SCHEMANAME).toUpperCase();

        StatementHandler statementHandler = new JDBCStatementHandler();
        statementHandler.init(dataSource);

        dbClearer = ReflectionUtils.createInstanceOfType(config.getString(DBMaintainer.PROPKEY_DBCLEARER_START + '.' +
                config.getString(DBMaintainer.PROPKEY_DATABASE_DIALECT)));
        dbClearer.init(dataSource, statementHandler);
    }

    public void testClearDatabase_tables() throws Exception {
        createTestTables();
        assertTrue(tableExists("testtable1"));
        assertTrue(tableExists("testtable2"));
        dbClearer.clearDatabase();
        assertFalse(tableExists("testtable1"));
        assertTrue(tableExists("testtable2"));
    }

    public void testClearDatabase_views() throws Exception {
        createTestTables();
        createTestView();
        assertTrue(tableExists("testview"));
        dbClearer.clearDatabase();
        assertFalse(tableExists("testview"));
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
            st.execute("create table testtable1 (col1 varchar(10))");
            st.execute("create table testtable2 (col1 varchar(10))");
        } finally {
            DbUtils.closeQuietly(conn, st, null);
        }
    }

    private void createTestView() throws SQLException {
        Connection conn = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            // Make sure previous setup is cleaned up
            st.execute("drop view testview if exists");
            st.execute("create view testview as select col1 from testtable2");
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
