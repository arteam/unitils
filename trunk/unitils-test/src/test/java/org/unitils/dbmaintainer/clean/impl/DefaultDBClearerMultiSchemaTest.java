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
import org.dbmaintain.database.Database;
import org.dbmaintain.structure.clear.DBClearer;
import org.hsqldb.Trigger;
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.unitils.database.SQLUnitils.executeUpdate;
import static org.unitils.database.SQLUnitils.executeUpdateQuietly;
import static org.unitils.testutil.TestUnitilsConfiguration.getUnitilsConfiguration;

/**
 * Test class for the clearing the database using multiple database schema's. <p/> This test is currenlty only implemented
 * for HsqlDb
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DefaultDBClearerMultiSchemaTest extends UnitilsJUnit4 {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DefaultDBClearerMultiSchemaTest.class);

    private DBClearer dbClearer;

    @TestDataSource
    protected DataSource dataSource;
    protected Database defaultDatabase;
    protected String versionTableName;
    protected boolean disabled;


    @Before
    public void initialize() throws Exception {
        Properties configuration = getUnitilsConfiguration();
        this.disabled = !"hsqldb".equals(PropertyUtils.getString(PROPERTY_DIALECT, configuration));
        if (disabled) {
            return;
        }
        versionTableName = configuration.getProperty(PROPERTY_EXECUTED_SCRIPTS_TABLE_NAME);

        dropTestDatabase();
        createTestDatabase();

        // configure 3 schemas
        configuration.setProperty(PROPERTY_AUTO_CREATE_DBMAINTAIN_SCRIPTS_TABLE, "true");
        configuration.setProperty(PROPERTY_SCHEMANAMES, "PUBLIC, SCHEMA_A, SCHEMA_B");

        DbMaintainManager dbMaintainManager = new DbMaintainManager(configuration, false, new TestDataSourceFactory(), new UnitilsTransactionManager());
        defaultDatabase = dbMaintainManager.getDatabase(null);

        dbClearer = dbMaintainManager.getDbMaintainMainFactory().createDBClearer();
    }

    @After
    public void cleanUp() throws Exception {
        if (disabled) {
            return;
        }
        dropTestDatabase();
    }


    @Test
    public void clearTables() throws Exception {
        if (disabled) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        assertEquals(1, defaultDatabase.getTableNames("PUBLIC").size());
        assertEquals(1, defaultDatabase.getTableNames("SCHEMA_A").size());
        assertEquals(1, defaultDatabase.getTableNames("SCHEMA_B").size());
        dbClearer.clearDatabase();
        assertEquals(1, defaultDatabase.getTableNames("PUBLIC").size());  // version table was created
        assertEquals(0, defaultDatabase.getTableNames("SCHEMA_A").size());
        assertEquals(0, defaultDatabase.getTableNames("SCHEMA_B").size());
    }

    @Test
    public void clearViews() throws Exception {
        if (disabled) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        assertEquals(1, defaultDatabase.getViewNames("PUBLIC").size());
        assertEquals(1, defaultDatabase.getViewNames("SCHEMA_A").size());
        assertEquals(1, defaultDatabase.getViewNames("SCHEMA_B").size());
        dbClearer.clearDatabase();
        assertTrue(defaultDatabase.getViewNames("PUBLIC").isEmpty());
        assertTrue(defaultDatabase.getViewNames("SCHEMA_A").isEmpty());
        assertTrue(defaultDatabase.getViewNames("SCHEMA_B").isEmpty());
    }

    @Test
    public void clearSequences() throws Exception {
        if (disabled) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        assertEquals(1, defaultDatabase.getSequenceNames("PUBLIC").size());
        assertEquals(1, defaultDatabase.getSequenceNames("SCHEMA_A").size());
        assertEquals(1, defaultDatabase.getSequenceNames("SCHEMA_B").size());
        dbClearer.clearDatabase();
        assertTrue(defaultDatabase.getSequenceNames("PUBLIC").isEmpty());
        assertTrue(defaultDatabase.getSequenceNames("SCHEMA_A").isEmpty());
        assertTrue(defaultDatabase.getSequenceNames("SCHEMA_B").isEmpty());
    }


    private void createTestDatabase() throws Exception {
        // create schemas
        executeUpdate("create schema SCHEMA_A AUTHORIZATION DBA", dataSource);
        executeUpdate("create schema SCHEMA_B AUTHORIZATION DBA", dataSource);
        // create tables
        executeUpdate("create table TEST_TABLE (col1 varchar(100))", dataSource);
        executeUpdate("create table SCHEMA_A.TEST_TABLE (col1 varchar(100))", dataSource);
        executeUpdate("create table SCHEMA_B.TEST_TABLE (col1 varchar(100))", dataSource);
        // create views
        executeUpdate("create view TEST_VIEW as select col1 from TEST_TABLE", dataSource);
        executeUpdate("create view SCHEMA_A.TEST_VIEW as select col1 from SCHEMA_A.TEST_TABLE", dataSource);
        executeUpdate("create view SCHEMA_B.TEST_VIEW as select col1 from SCHEMA_B.TEST_TABLE", dataSource);
        // create sequences
        executeUpdate("create sequence TEST_SEQUENCE", dataSource);
        executeUpdate("create sequence SCHEMA_A.TEST_SEQUENCE", dataSource);
        executeUpdate("create sequence SCHEMA_B.TEST_SEQUENCE", dataSource);
    }

    private void dropTestDatabase() throws Exception {
        // drop sequences
        executeUpdateQuietly("drop sequence TEST_SEQUENCE", dataSource);
        executeUpdateQuietly("drop sequence SCHEMA_A.TEST_SEQUENCE", dataSource);
        executeUpdateQuietly("drop sequence SCHEMA_B.TEST_SEQUENCE", dataSource);
        // drop views
        executeUpdateQuietly("drop view TEST_VIEW", dataSource);
        executeUpdateQuietly("drop view SCHEMA_A.TEST_VIEW", dataSource);
        executeUpdateQuietly("drop view SCHEMA_B.TEST_VIEW", dataSource);
        // drop tables
        executeUpdateQuietly("drop table " + versionTableName, dataSource);
        executeUpdateQuietly("drop table TEST_TABLE", dataSource);
        executeUpdateQuietly("drop table SCHEMA_A.TEST_TABLE", dataSource);
        executeUpdateQuietly("drop table SCHEMA_B.TEST_TABLE", dataSource);
        // drop schemas
        executeUpdateQuietly("drop schema SCHEMA_A", dataSource);
        executeUpdateQuietly("drop schema SCHEMA_B", dataSource);
    }


    public static class TestTrigger implements Trigger {

        public void fire(int i, String string, String string1, Object[] objects, Object[] objects1) {
        }
    }

}
