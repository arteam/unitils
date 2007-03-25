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

import org.unitils.UnitilsJUnit3;
import org.unitils.core.ConfigurationLoader;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.dbmaintainer.clean.impl.DefaultDBCleaner;
import org.unitils.dbmaintainer.dbsupport.DbSupport;
import org.unitils.dbmaintainer.script.StatementHandler;
import org.unitils.dbmaintainer.script.impl.StatementHandlerException;
import static org.unitils.dbmaintainer.util.DatabaseModuleConfigUtils.*;
import static org.unitils.thirdparty.org.apache.commons.dbutils.DbUtils.closeQuietly;

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

    /* The sql statement handler */
    private StatementHandler statementHandler;

    /* The DbSupport object */
    private DbSupport dbSupport;

    /* The name of the version tabel */
    private String versionTableName;


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

        statementHandler = getConfiguredStatementHandlerInstance(configuration, dataSource);
        dbCleaner = getConfiguredDatabaseTaskInstance(DBCleaner.class, configuration, dataSource, statementHandler);
        dbSupport = getConfiguredDbSupportInstance(configuration, dataSource, statementHandler);
        versionTableName = dbSupport.qualified(dbSupport.toCorrectCaseIdentifier("db_version"));

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
        assertFalse(isEmpty("TEST_TABLE"));
        assertFalse(isEmpty(dbSupport.quoted("Test_CASE_Table")));
        dbCleaner.cleanSchema();
        assertTrue(isEmpty("TEST_TABLE"));
        assertTrue(isEmpty(dbSupport.quoted("Test_CASE_Table")));
    }


    /**
     * Tests if the tables that are configured as tables to preserve are left untouched
     */
    public void testCleanDatabase_preserveDbVersionTable() throws Exception {
        assertFalse(isEmpty(versionTableName));
        dbCleaner.cleanSchema();
        assertFalse(isEmpty(versionTableName));
    }


    /**
     * Tests if the tables to preserve are left untouched
     */
    public void testCleanDatabase_preserveTablesToPreserve() throws Exception {
        assertFalse(isEmpty("TEST_TABLE_PRESERVE"));
        assertFalse(isEmpty(dbSupport.quoted("Test_CASE_Table_Preserve")));
        dbCleaner.cleanSchema();
        assertFalse(isEmpty("TEST_TABLE_PRESERVE"));
        assertFalse(isEmpty(dbSupport.quoted("Test_CASE_Table_Preserve")));
    }


    /**
     * Creates the test tables
     */
    private void createTestDatabase() throws Exception {
        statementHandler.handle("create table " + versionTableName + "(testcolumn varchar(10))");
        statementHandler.handle("create table TEST_TABLE(testcolumn varchar(10))");
        statementHandler.handle("create table TEST_TABLE_PRESERVE(testcolumn varchar(10))");
        statementHandler.handle("create table " + dbSupport.quoted("Test_CASE_Table") + " (col1 varchar(10))");
        statementHandler.handle("create table " + dbSupport.quoted("Test_CASE_Table_Preserve") + " (col1 varchar(10))");
        // Also create a view, to see if the DBCleaner doesn't crash on views
        statementHandler.handle("create view TEST_VIEW as (select * from TEST_TABLE_PRESERVE)");
    }


    /**
     * Removes the test database tables
     */
    private void cleanupTestDatabase() throws SQLException {
        dropTestTables("TEST_TABLE", "TEST_TABLE_PRESERVE", dbSupport.quoted("Test_CASE_Table"), dbSupport.quoted("Test_CASE_Table_Preserve"));

        try {
            statementHandler.handle("drop table " + versionTableName);
        } catch (StatementHandlerException e) {
            // Ignored
        }
        try {
            dbSupport.dropView("TEST_VIEW");
        } catch (StatementHandlerException e) {
            // Ignored
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
                String correctCaseTableName = dbSupport.toCorrectCaseIdentifier(tableName);
                dbSupport.dropTable(correctCaseTableName);
            } catch (StatementHandlerException e) {
                // Ignored
            }
        }
    }


    /**
     * Inserts a test record in each test table
     */
    private void insertTestData() throws Exception {
        statementHandler.handle("insert into " + versionTableName + " values('test')");
        statementHandler.handle("insert into TEST_TABLE values('test')");
        statementHandler.handle("insert into TEST_TABLE_PRESERVE values('test')");
        statementHandler.handle("insert into " + dbSupport.quoted("Test_CASE_Table") + " values('test')");
        statementHandler.handle("insert into " + dbSupport.quoted("Test_CASE_Table_Preserve") + " values('test')");
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
            closeQuietly(conn, st, rs);
        }
    }

}