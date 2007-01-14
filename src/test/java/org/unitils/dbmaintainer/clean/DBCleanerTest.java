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
import org.unitils.database.annotations.TestDataSource;
import org.unitils.dbmaintainer.dbsupport.DbSupport;
import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.handler.StatementHandlerException;
import org.unitils.dbmaintainer.util.DatabaseModuleConfigUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

        ConfigurationLoader configurationLoader = new ConfigurationLoader();
        Configuration configuration = configurationLoader.loadConfiguration();
        configuration.addProperty(DefaultDBCleaner.PROPKEY_TABLESTOPRESERVE, "tabletopreserve");

        StatementHandler statementHandler = DatabaseModuleConfigUtils.getConfiguredStatementHandlerInstance(configuration, dataSource);
        dbCleaner = DatabaseModuleConfigUtils.getConfiguredDatabaseTaskInstance(DBCleaner.class, configuration, dataSource, statementHandler);
        dbSupport = DatabaseModuleConfigUtils.getConfiguredDbSupportInstance(configuration, dataSource, statementHandler);

        dropTestTables();
        createTestTables();
        insertTestData();
    }


    /**
     * Removes the test database tables from the test database, to avoid inference with other tests
     */
    protected void tearDown() throws Exception {
        super.tearDown();

        dropTestTables();
    }


    /**
     * Creates the test tables
     */
    private void createTestTables() throws SQLException {
        Connection conn = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            st.execute("create table tabletoclean(testcolumn varchar(10))");
            st.execute("create table db_version(testcolumn varchar(10))");
            st.execute("create table tabletopreserve(testcolumn varchar(10))");
            // Also create a view, to see if the DBCleaner doesn't crash on views
            st.execute("create view testview as (select * from tabletopreserve)");
        } finally {
            DbUtils.closeQuietly(conn, st, null);
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
            st.execute("insert into tabletoclean values('test')");
            st.execute("insert into db_version values('test')");
            st.execute("insert into tabletopreserve values('test')");
        } finally {
            DbUtils.closeQuietly(conn, st, null);
        }
    }


    /**
     * Removes the test database tables
     */
    private void dropTestTables() throws SQLException {
        Connection conn = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            try {
                dbSupport.dropView("testview");
            } catch (StatementHandlerException e) {
                // Ignored
            }
            try {
                dbSupport.dropTable("tabletoclean");
            } catch (StatementHandlerException e) {
                // Ignored
            }
            try {
                dbSupport.dropTable("db_version");
            } catch (StatementHandlerException e) {
                // Ignored
            }
            try {
                dbSupport.dropTable("tabletopreserve");
            } catch (StatementHandlerException e) {
                // Ignored
            }
        } finally {
            DbUtils.closeQuietly(conn, st, null);
        }
    }


    /**
     * Tests if the tables that are not configured as tables to preserve are correctly cleaned
     */
    public void testCleanDatabase() throws Exception {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            assertFalse(isEmpty("tabletoclean"));
            dbCleaner.cleanDatabase();
            assertTrue(isEmpty("tabletoclean"));
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
            assertFalse(isEmpty("db_version"));
            dbCleaner.cleanDatabase();
            assertFalse(isEmpty("db_version"));
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }


    /**
     * Tests if db_version table is left untouched
     */
    public void testCleanDatabase_preserveTablesToPreserve() throws Exception {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            assertFalse(isEmpty("tabletopreserve"));
            dbCleaner.cleanDatabase();
            assertFalse(isEmpty("tabletopreserve"));
        } finally {
            DbUtils.closeQuietly(conn);
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