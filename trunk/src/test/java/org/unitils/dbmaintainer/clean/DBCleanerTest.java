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

import org.apache.commons.dbutils.DbUtils;
import org.unitils.UnitilsJUnit3;
import org.unitils.core.ConfigurationLoader;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.dbmaintainer.clean.impl.DefaultDBCleaner;
import org.unitils.dbmaintainer.dbsupport.DbSupport;
import org.unitils.dbmaintainer.script.StatementHandler;
import org.unitils.dbmaintainer.script.impl.StatementHandlerException;
import static org.unitils.dbmaintainer.util.DatabaseModuleConfigUtils.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Test class for the DBCleaner.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DBCleanerTest extends UnitilsJUnit3 {

    /* DataSource for the test database, is injected */
    @TestDataSource
    private DataSource dataSource = null;

    /* Tested object */
    private DBCleaner dbCleaner;

    /* The DbSupport object */
    private DbSupport dbSupport;


    /**
     * Test fixture. The DefaultDBCleaner is instantiated and configured. Test tables are created and filled with test
     * data. One of these tables is configured as 'tabletopreserve'.
     */
    protected void setUp() throws Exception {
        super.setUp();

        // case sensitive and insensitive names
        String itemsToPreserve = "Test_table_Preserve, \"Test_CASE_Table_Preserve\"";

        Properties configuration = new ConfigurationLoader().loadConfiguration();
        configuration.setProperty(DefaultDBCleaner.PROPKEY_TABLESTOPRESERVE, itemsToPreserve);

        StatementHandler statementHandler = getConfiguredStatementHandlerInstance(configuration, dataSource);
        dbCleaner = getConfiguredDatabaseTaskInstance(DBCleaner.class, configuration, dataSource, statementHandler);
        dbSupport = getConfiguredDbSupportInstance(configuration, dataSource, statementHandler);

        cleanupTestDatabase();
        createTestDatabase();
        insertTestData();
    }


    /**
     * Removes the test database tables from the test database, to avoid inference with other tests
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        cleanupTestDatabase();
    }


    /**
     * Tests if the tables that are not configured as tables to preserve are correctly cleaned
     */
    public void testCleanDatabase() throws Exception {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            assertFalse(isEmpty("TEST_TABLE"));
            assertFalse(isEmpty("TEST_TABLE"));
            assertFalse(isEmpty(dbSupport.quoted("Test_CASE_Table")));
            dbCleaner.cleanSchema();
            assertTrue(isEmpty("TEST_TABLE"));
            assertTrue(isEmpty(dbSupport.quoted("Test_CASE_Table")));
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }


    /**
     * Tests if the tables that are configured as tables to preserve are left untouched
     */
    public void testCleanDatabase_preserveDbVersionTable() throws Exception {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            assertFalse(isEmpty("DB_VERSION"));
            dbCleaner.cleanSchema();
            assertFalse(isEmpty("DB_VERSION"));
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }


    /**
     * Tests if the tables to preserve are left untouched
     */
    public void testCleanDatabase_preserveTablesToPreserve() throws Exception {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            assertFalse(isEmpty("TEST_TABLE_PRESERVE"));
            assertFalse(isEmpty(dbSupport.quoted("Test_CASE_Table_Preserve")));
            dbCleaner.cleanSchema();
            assertFalse(isEmpty("TEST_TABLE_PRESERVE"));
            assertFalse(isEmpty(dbSupport.quoted("Test_CASE_Table_Preserve")));
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }


    /**
     * Creates the test tables
     */
    private void createTestDatabase() throws SQLException {
        Connection conn = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            st.execute("create table DB_VERSION(testcolumn varchar(10))");
            st.execute("create table TEST_TABLE(testcolumn varchar(10))");
            st.execute("create table TEST_TABLE_PRESERVE(testcolumn varchar(10))");
            st.execute("create table " + dbSupport.quoted("Test_CASE_Table") + " (col1 varchar(10))");
            st.execute("create table " + dbSupport.quoted("Test_CASE_Table_Preserve") + " (col1 varchar(10))");
            // Also create a view, to see if the DBCleaner doesn't crash on views
            st.execute("create view TEST_VIEW as (select * from TEST_TABLE_PRESERVE)");
        } finally {
            DbUtils.closeQuietly(conn, st, null);
        }
    }


    /**
     * Removes the test database tables
     */
    private void cleanupTestDatabase() throws SQLException {
        dropTestTables("TEST_TABLE", "TEST_TABLE_PRESERVE", "Test_CASE_Table", "Test_CASE_Table_Preserve", "DB_VERSION");

        Connection conn = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            try {
                dbSupport.dropView("TEST_VIEW");
            } catch (StatementHandlerException e) {
                // Ignored
            }
        } finally {
            DbUtils.closeQuietly(conn, st, null);
        }
    }


    /**
     * Drops the test tables
     *
     * @param tableNames The tables to drop
     */
    private void dropTestTables(String... tableNames) {
        for (String tableName : tableNames) {
            try {
                dbSupport.dropTable(tableName);
            } catch (StatementHandlerException e) {
                // Ignored
            }
        }
    }


    /**
     * Inserts a test record in each test table
     */
    private void insertTestData() throws SQLException {
        Connection conn = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            st.execute("insert into DB_VERSION values('test')");
            st.execute("insert into TEST_TABLE values('test')");
            st.execute("insert into TEST_TABLE_PRESERVE values('test')");
            st.execute("insert into " + dbSupport.quoted("Test_CASE_Table") + " values('test')");
            st.execute("insert into " + dbSupport.quoted("Test_CASE_Table_Preserve") + " values('test')");
        } finally {
            DbUtils.closeQuietly(conn, st, null);
        }
    }


    /**
     * Utility method to check whether the given table is empty.
     *
     * @param tableName the table, not null
     * @return true if empty
     */
    private boolean isEmpty(String tableName) throws SQLException {
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