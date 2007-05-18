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
import org.unitils.core.dbsupport.DbSupport;
import static org.unitils.core.dbsupport.DbSupportFactory.getDefaultDbSupport;
import org.unitils.core.dbsupport.SQLHandler;
import static org.unitils.core.util.SQLUtils.*;
import org.unitils.database.annotations.TestDataSource;
import static org.unitils.dbmaintainer.clean.impl.DefaultDBCleaner.*;
import static org.unitils.dbmaintainer.util.DatabaseModuleConfigUtils.getConfiguredDatabaseTaskInstance;

import javax.sql.DataSource;
import java.sql.SQLException;
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

    /* The name of the version tabel */
    private String versionTableName;


    /**
     * Test fixture. The DefaultDBCleaner is instantiated and configured. Test tables are created and filled with test
     * data. One of these tables is configured as 'tabletopreserve'.
     */
    protected void setUp() throws Exception {
        super.setUp();

        Properties configuration = new ConfigurationLoader().loadConfiguration();
        SQLHandler sqlHandler = new SQLHandler(dataSource);
        dbSupport = getDefaultDbSupport(configuration, sqlHandler);

        // items to preserve
        configuration.setProperty(PROPKEY_PRESERVE_ONLY_DATA_TABLES, "Test_table_Preserve");
        configuration.setProperty(PROPKEY_PRESERVE_TABLES, dbSupport.quoted("Test_CASE_Table_Preserve"));
        // create cleaner instance
        dbCleaner = getConfiguredDatabaseTaskInstance(DBCleaner.class, configuration, sqlHandler);
        versionTableName = configuration.getProperty(PROPKEY_VERSION_TABLE_NAME);

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
        assertFalse(isEmpty("TEST_TABLE", dbSupport));
        assertFalse(isEmpty(dbSupport.quoted("Test_CASE_Table"), dbSupport));
        dbCleaner.cleanSchemas();
        assertTrue(isEmpty("TEST_TABLE", dbSupport));
        assertTrue(isEmpty(dbSupport.quoted("Test_CASE_Table"), dbSupport));
    }


    /**
     * Tests if the tables that are configured as tables to preserve are left untouched
     */
    public void testCleanDatabase_preserveDbVersionTable() throws Exception {
        assertFalse(isEmpty(versionTableName, dbSupport));
        dbCleaner.cleanSchemas();
        assertFalse(isEmpty(versionTableName, dbSupport));
    }


    /**
     * Tests if the tables to preserve are left untouched
     */
    public void testCleanDatabase_preserveTablesToPreserve() throws Exception {
        assertFalse(isEmpty("TEST_TABLE_PRESERVE", dbSupport));
        assertFalse(isEmpty(dbSupport.quoted("Test_CASE_Table_Preserve"), dbSupport));
        dbCleaner.cleanSchemas();
        assertFalse(isEmpty("TEST_TABLE_PRESERVE", dbSupport));
        assertFalse(isEmpty(dbSupport.quoted("Test_CASE_Table_Preserve"), dbSupport));
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
    private void cleanupTestDatabase() throws SQLException {
        dropTestTables(dbSupport, "TEST_TABLE", "TEST_TABLE_PRESERVE", dbSupport.quoted("Test_CASE_Table"), dbSupport.quoted("Test_CASE_Table_Preserve"), versionTableName);
        dropTestViews(dbSupport, "TEST_VIEW");
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