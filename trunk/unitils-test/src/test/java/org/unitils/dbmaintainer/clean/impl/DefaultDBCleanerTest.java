/*
 * Copyright Unitils.org
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

import org.dbmaintain.dbsupport.DbSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.util.PropertyUtils;

import javax.sql.DataSource;
import java.util.Properties;

import static org.dbmaintain.config.DbMaintainProperties.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.unitils.core.util.SQLTestUtils.dropTestTables;
import static org.unitils.core.util.SQLTestUtils.dropTestViews;
import static org.unitils.database.DatabaseUnitils.cleanDatabase;
import static org.unitils.database.DatabaseUnitils.getDbSupports;
import static org.unitils.database.SQLUnitils.executeUpdate;
import static org.unitils.database.SQLUnitils.isEmpty;
import static org.unitils.testutil.TestUnitilsConfiguration.*;

/**
 * Test class for the DBCleaner.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DefaultDBCleanerTest {

    private DataSource dataSource;
    private DbSupport defaultDbSupport;
    private String versionTableName;
    private boolean disabled;


    /**
     * Test fixture. The DefaultDBCleaner is instantiated and configured. Test tables are created and filled with test
     * data. One of these tables is configured as 'tabletopreserve'.
     */
    @Before
    public void setUp() throws Exception {
        Properties configuration = getUnitilsConfiguration();
        this.disabled = !"hsqldb".equals(PropertyUtils.getString(PROPERTY_DIALECT, configuration));
        if (disabled) {
            return;
        }

        // items to preserve
        defaultDbSupport = getDbSupports().getDefaultDbSupport();
        configuration.setProperty(PROPERTY_PRESERVE_DATA_TABLES, "Test_table_Preserve");
        configuration.setProperty(PROPERTY_PRESERVE_TABLES, defaultDbSupport.quoted("Test_CASE_Table_Preserve"));
        versionTableName = configuration.getProperty(PROPERTY_EXECUTED_SCRIPTS_TABLE_NAME);
        reinitializeUnitils(configuration);

        defaultDbSupport = getDbSupports().getDefaultDbSupport();
        dataSource = defaultDbSupport.getDataSource();

        cleanupTestDatabase();
        createTestDatabase();
        insertTestData();
    }


    /**
     * Removes the test database tables from the test database, to avoid inference with other tests
     */
    @After
    public void tearDown() throws Exception {
        resetUnitils();
        cleanupTestDatabase();
    }


    /**
     * Tests if the tables that are not configured as tables to preserve are correctly cleaned
     */
    @Test
    public void testCleanDatabase() throws Exception {
        assertFalse(isEmpty("TEST_TABLE", dataSource));
        assertFalse(isEmpty(defaultDbSupport.quoted("Test_CASE_Table"), dataSource));
        cleanDatabase();
        assertTrue(isEmpty("TEST_TABLE", dataSource));
        assertTrue(isEmpty(defaultDbSupport.quoted("Test_CASE_Table"), dataSource));
    }


    /**
     * Tests if the tables that are configured as tables to preserve are left untouched
     */
    @Test
    public void testCleanDatabase_preserveDbVersionTable() throws Exception {
        assertFalse(isEmpty(versionTableName, dataSource));
        cleanDatabase();
        assertFalse(isEmpty(versionTableName, dataSource));
    }


    /**
     * Tests if the tables to preserve are left untouched
     */
    @Test
    public void testCleanDatabase_preserveTablesToPreserve() throws Exception {
        assertFalse(isEmpty("TEST_TABLE_PRESERVE", dataSource));
        assertFalse(isEmpty(defaultDbSupport.quoted("Test_CASE_Table_Preserve"), dataSource));
        cleanDatabase();
        assertFalse(isEmpty("TEST_TABLE_PRESERVE", dataSource));
        assertFalse(isEmpty(defaultDbSupport.quoted("Test_CASE_Table_Preserve"), dataSource));
    }


    /**
     * Creates the test tables
     */
    private void createTestDatabase() throws Exception {
        executeUpdate("create table " + versionTableName + "(testcolumn varchar(10))", dataSource);
        executeUpdate("create table TEST_TABLE(testcolumn varchar(10))", dataSource);
        executeUpdate("create table TEST_TABLE_PRESERVE(testcolumn varchar(10))", dataSource);
        executeUpdate("create table " + defaultDbSupport.quoted("Test_CASE_Table") + " (col1 varchar(10))", dataSource);
        executeUpdate("create table " + defaultDbSupport.quoted("Test_CASE_Table_Preserve") + " (col1 varchar(10))", dataSource);
        // Also create a view, to see if the DBCleaner doesn't crash on views
        executeUpdate("create view TEST_VIEW as (select * from TEST_TABLE_PRESERVE)", dataSource);
    }


    /**
     * Removes the test database tables
     */
    private void cleanupTestDatabase() {
        dropTestViews(defaultDbSupport, "TEST_VIEW");
        dropTestTables(defaultDbSupport, "TEST_TABLE", "TEST_TABLE_PRESERVE", defaultDbSupport.quoted("Test_CASE_Table"), defaultDbSupport.quoted("Test_CASE_Table_Preserve"), versionTableName);
    }


    /**
     * Inserts a test record in each test table
     */
    private void insertTestData() throws Exception {
        executeUpdate("insert into " + versionTableName + " values('test')", dataSource);
        executeUpdate("insert into TEST_TABLE values('test')", dataSource);
        executeUpdate("insert into TEST_TABLE_PRESERVE values('test')", dataSource);
        executeUpdate("insert into " + defaultDbSupport.quoted("Test_CASE_Table") + " values('test')", dataSource);
        executeUpdate("insert into " + defaultDbSupport.quoted("Test_CASE_Table_Preserve") + " values('test')", dataSource);
    }

}