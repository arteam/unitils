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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbmaintain.structure.clean.DBCleaner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.database.TestDataSourceFactory;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.database.manager.DbMaintainManager;
import org.unitils.database.manager.UnitilsTransactionManager;
import org.unitils.util.PropertyUtils;

import javax.sql.DataSource;
import java.util.Properties;

import static org.dbmaintain.config.DbMaintainProperties.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.unitils.database.SQLUnitils.*;
import static org.unitils.testutil.TestUnitilsConfiguration.getUnitilsConfiguration;

/**
 * Test class for the DBCleaner.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DefaultDBCleanerTest extends UnitilsJUnit4 {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DefaultDBCleanerTest.class);

    private DBCleaner dbCleaner;

    @TestDataSource
    protected DataSource dataSource;

    private String versionTableName;
    private boolean disabled;


    @Before
    public void setUp() throws Exception {
        Properties configuration = getUnitilsConfiguration();
        this.disabled = !"hsqldb".equals(PropertyUtils.getString(PROPERTY_DIALECT, configuration));
        if (disabled) {
            return;
        }
        versionTableName = configuration.getProperty(PROPERTY_EXECUTED_SCRIPTS_TABLE_NAME);

        cleanupTestDatabase();
        createTestDatabase();
        insertTestData();

        // items to preserve
        configuration.setProperty(PROPERTY_PRESERVE_DATA_TABLES, "Test_table_Preserve");
        configuration.setProperty(PROPERTY_PRESERVE_TABLES, "\"Test_CASE_Table_Preserve\"");

        DbMaintainManager dbMaintainManager = new DbMaintainManager(configuration, false, new TestDataSourceFactory(), new UnitilsTransactionManager());

        dbCleaner = dbMaintainManager.getDbMaintainMainFactory().createDBCleaner();
    }

    @After
    public void tearDown() throws Exception {
        if (disabled) {
            return;
        }
        cleanupTestDatabase();
    }


    @Test
    public void testCleanDatabase() throws Exception {
        if (disabled) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        assertFalse(isEmpty("TEST_TABLE", dataSource));
        assertFalse(isEmpty("\"Test_CASE_Table\"", dataSource));
        dbCleaner.cleanDatabase();
        assertTrue(isEmpty("TEST_TABLE", dataSource));
        assertTrue(isEmpty("\"Test_CASE_Table\"", dataSource));
    }

    @Test
    public void testCleanDatabase_preserveDbVersionTable() throws Exception {
        if (disabled) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        assertFalse(isEmpty(versionTableName, dataSource));
        dbCleaner.cleanDatabase();
        assertFalse(isEmpty(versionTableName, dataSource));
    }

    @Test
    public void testCleanDatabase_preserveTablesToPreserve() throws Exception {
        if (disabled) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        assertFalse(isEmpty("TEST_TABLE_PRESERVE", dataSource));
        assertFalse(isEmpty("\"Test_CASE_Table_Preserve\"", dataSource));
        dbCleaner.cleanDatabase();
        assertFalse(isEmpty("TEST_TABLE_PRESERVE", dataSource));
        assertFalse(isEmpty("\"Test_CASE_Table_Preserve\"", dataSource));
    }


    private void createTestDatabase() throws Exception {
        executeUpdate("create table " + versionTableName + "(testcolumn varchar(10))", dataSource);
        executeUpdate("create table TEST_TABLE(testcolumn varchar(10))", dataSource);
        executeUpdate("create table TEST_TABLE_PRESERVE(testcolumn varchar(10))", dataSource);
        executeUpdate("create table \"Test_CASE_Table\" (col1 varchar(10))", dataSource);
        executeUpdate("create table \"Test_CASE_Table_Preserve\" (col1 varchar(10))", dataSource);
        // Also create a view, to see if the DBCleaner doesn't crash on views
        executeUpdate("create view TEST_VIEW as (select * from TEST_TABLE_PRESERVE)", dataSource);
    }

    private void cleanupTestDatabase() {
        executeUpdateQuietly("drop view TEST_VIEW", dataSource);
        executeUpdateQuietly("drop table " + versionTableName, dataSource);
        executeUpdateQuietly("drop table TEST_TABLE", dataSource);
        executeUpdateQuietly("drop table TEST_TABLE_PRESERVE", dataSource);
        executeUpdateQuietly("drop table \"Test_CASE_Table\"", dataSource);
        executeUpdateQuietly("drop table \"Test_CASE_Table_Preserve\"", dataSource);
    }

    private void insertTestData() throws Exception {
        executeUpdate("insert into " + versionTableName + " values('test')", dataSource);
        executeUpdate("insert into TEST_TABLE values('test')", dataSource);
        executeUpdate("insert into TEST_TABLE_PRESERVE values('test')", dataSource);
        executeUpdate("insert into \"Test_CASE_Table\" values('test')", dataSource);
        executeUpdate("insert into \"Test_CASE_Table_Preserve\" values('test')", dataSource);
    }
}