/*
 * Copyright 2006-2007,  Unitils.org
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
package org.unitils.dbmaintainer.clean.impl;

import org.junit.After;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.dbsupport.DbSupport;
import org.unitils.core.dbsupport.SQLHandler;

import static org.unitils.core.dbsupport.DbSupportFactory.getDefaultDbSupport;
import org.unitils.core.dbsupport.DefaultSQLHandler;
import static org.unitils.core.util.SQLTestUtils.dropTestTables;
import static org.unitils.core.util.SQLTestUtils.dropTestViews;
import static org.unitils.database.SQLUnitils.executeUpdate;
import static org.unitils.database.SQLUnitils.isEmpty;
import org.unitils.database.annotations.TestDataSource;
import static org.unitils.dbmaintainer.clean.impl.DefaultDBCleaner.*;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Test class for the DBCleaner.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DefaultDBCleanerTest extends UnitilsJUnit4 {

    /* DataSource for the test database, is injected */
    @TestDataSource
    private DataSource dataSource = null;

    /* Tested object */
    private DefaultDBCleaner defaultDbCleaner;

    /* The DbSupport object */
    private DbSupport dbSupport;

    /* The name of the version tabel */
    private String versionTableName;


    /**
     * Test fixture. The DefaultDBCleaner is instantiated and configured. Test tables are created and filled with test
     * data. One of these tables is configured as 'tabletopreserve'.
     */
    @Before
    public void setUp() throws Exception {
        Properties configuration = new ConfigurationLoader().loadConfiguration();
        SQLHandler sqlHandler = new DefaultSQLHandler(dataSource);
        dbSupport = getDefaultDbSupport(configuration, sqlHandler);

        // items to preserve
        configuration.setProperty(PROPKEY_PRESERVE_DATA_TABLES, "Test_table_Preserve");
        configuration.setProperty(PROPKEY_PRESERVE_TABLES, dbSupport.quoted("Test_CASE_Table_Preserve"));
        // create cleaner instance
        defaultDbCleaner = new DefaultDBCleaner();
        defaultDbCleaner.init(configuration, sqlHandler);
        versionTableName = configuration.getProperty(PROPKEY_VERSION_TABLE_NAME);

        cleanupTestDatabase();
        createTestDatabase();
        insertTestData();
    }


    /**
     * Removes the test database tables from the test database, to avoid inference with other tests
     */
    @After
    public void tearDown() throws Exception {
        cleanupTestDatabase();
    }


    /**
     * Tests if the tables that are not configured as tables to preserve are correctly cleaned
     */
    @Test
    public void testCleanDatabase() throws Exception {
        assertFalse(isEmpty("TEST_TABLE", dataSource));
        assertFalse(isEmpty(dbSupport.quoted("Test_CASE_Table"), dataSource));
        defaultDbCleaner.cleanSchemas();
        assertTrue(isEmpty("TEST_TABLE", dataSource));
        assertTrue(isEmpty(dbSupport.quoted("Test_CASE_Table"), dataSource));
    }


    /**
     * Tests if the tables that are configured as tables to preserve are left untouched
     */
    @Test
    public void testCleanDatabase_preserveDbVersionTable() throws Exception {
        assertFalse(isEmpty(versionTableName, dataSource));
        defaultDbCleaner.cleanSchemas();
        assertFalse(isEmpty(versionTableName, dataSource));
    }


    /**
     * Tests if the tables to preserve are left untouched
     */
    @Test
    public void testCleanDatabase_preserveTablesToPreserve() throws Exception {
        assertFalse(isEmpty("TEST_TABLE_PRESERVE", dataSource));
        assertFalse(isEmpty(dbSupport.quoted("Test_CASE_Table_Preserve"), dataSource));
        defaultDbCleaner.cleanSchemas();
        assertFalse(isEmpty("TEST_TABLE_PRESERVE", dataSource));
        assertFalse(isEmpty(dbSupport.quoted("Test_CASE_Table_Preserve"), dataSource));
    }


    /**
     * Creates the test tables
     */
    private void createTestDatabase() throws Exception {
        executeUpdate("create table " + versionTableName + "(testcolumn varchar(10))", dataSource);
        executeUpdate("create table TEST_TABLE(testcolumn varchar(10))", dataSource);
        executeUpdate("create table TEST_TABLE_PRESERVE(testcolumn varchar(10))", dataSource);
        executeUpdate("create table " + dbSupport.quoted("Test_CASE_Table") + " (col1 varchar(10))", dataSource);
        executeUpdate("create table " + dbSupport.quoted("Test_CASE_Table_Preserve") + " (col1 varchar(10))", dataSource);
        // Also create a view, to see if the DBCleaner doesn't crash on views
        executeUpdate("create view TEST_VIEW as (select * from TEST_TABLE_PRESERVE)", dataSource);
    }


    /**
     * Removes the test database tables
     */
    private void cleanupTestDatabase() {
        dropTestViews(dbSupport, "TEST_VIEW");
        dropTestTables(dbSupport, "TEST_TABLE", "TEST_TABLE_PRESERVE", dbSupport.quoted("Test_CASE_Table"), dbSupport.quoted("Test_CASE_Table_Preserve"), versionTableName);
    }


    /**
     * Inserts a test record in each test table
     */
    private void insertTestData() throws Exception {
        executeUpdate("insert into " + versionTableName + " values('test')", dataSource);
        executeUpdate("insert into TEST_TABLE values('test')", dataSource);
        executeUpdate("insert into TEST_TABLE_PRESERVE values('test')", dataSource);
        executeUpdate("insert into " + dbSupport.quoted("Test_CASE_Table") + " values('test')", dataSource);
        executeUpdate("insert into " + dbSupport.quoted("Test_CASE_Table_Preserve") + " values('test')", dataSource);
    }

}