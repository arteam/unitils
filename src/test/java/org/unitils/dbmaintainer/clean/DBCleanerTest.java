/*
 * Copyright 2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unitils.dbmaintainer.clean;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.dbutils.DbUtils;
import org.unitils.UnitilsJUnit3;
import org.unitils.core.ConfigurationLoader;
import org.unitils.db.annotations.DatabaseTest;
import org.unitils.db.annotations.TestDataSource;
import org.unitils.dbmaintainer.handler.JDBCStatementHandler;
import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.util.DatabaseModuleConfigUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Test class for the DBCleaner
 */
@DatabaseTest
@SuppressWarnings({"UnusedDeclaration"})
public class DBCleanerTest extends UnitilsJUnit3 {

    /**
     * Tested object
     */
    private DBCleaner dbCleaner;

    /**
     * DataSource for the test database, is injected
     */
    @TestDataSource
    private DataSource dataSource;

    /**
     * Test fixture. The DefaultDBCleaner is instantiated and configured. Test tables are created and filled with test
     * data. One of these tables is configured as 'tabletopreserve'.
     *
     * @throws Exception
     */
    protected void setUp() throws Exception {
        super.setUp();

        ConfigurationLoader configurationLoader = new ConfigurationLoader();
        Configuration configuration = configurationLoader.loadConfiguration();
        configuration.addProperty(DefaultDBCleaner.PROPKEY_TABLESTOPRESERVE, "tabletopreserve");

        StatementHandler st = new JDBCStatementHandler();
        st.init(configuration, dataSource);

        dbCleaner = DatabaseModuleConfigUtils.getConfiguredDatabaseTaskInstance(DBCleaner.class,
                configuration, dataSource, st);

        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            createTestTables(conn);
            insertTestData(conn);
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    /**
     * Removes the test database tables from the test database, to avoid inference with other tests
     *
     * @throws Exception
     */
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

    /**
     * Removes the test database tables
     *
     * @param conn
     * @throws SQLException
     */
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

    /**
     * Tests if the tables that are not configured as tables to preserve are correctly cleaned
     *
     * @throws Exception
     */
    public void testCleanDatabase() throws Exception {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            assertFalse(isEmpty(conn, "tabletoclear"));
            dbCleaner.cleanDatabase();
            assertTrue(isEmpty(conn, "tabletoclear"));
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    /**
     * Tests if the tables that are configured as tables to preserve are left untouched
     *
     * @throws Exception
     */
    public void testCleanDatabase_preserveDbVersionTable() throws Exception {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            assertFalse(isEmpty(conn, "db_version"));
            dbCleaner.cleanDatabase();
            assertFalse(isEmpty(conn, "db_version"));
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    /**
     * Tests if db_version table is left untouched
     *
     * @throws Exception
     */
    public void testCleanDatabase_preserveTablesToPreserve() throws Exception {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            assertFalse(isEmpty(conn, "tabletopreserve"));
            dbCleaner.cleanDatabase();
            assertFalse(isEmpty(conn, "tabletopreserve"));
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    /**
     * Creates the test tables
     *
     * @param conn
     * @throws SQLException
     */
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

    /**
     * Inserts a test record in each test table
     *
     * @param conn
     * @throws SQLException
     */
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

    /**
     * @param conn
     * @return
     * @throws SQLException
     */
    private boolean isEmpty(Connection conn, String tableName) throws SQLException {
        Statement st = null;
        ResultSet rs = null;
        try {
            st = conn.createStatement();
            rs = st.executeQuery("select * from " + tableName);
            return !rs.next();
        } finally {
            DbUtils.closeQuietly(null, st, rs);
        }
    }

}
