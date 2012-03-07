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
 * Test class for the DBCleaner with multiple schemas with configuration to preserve all tables. <p/> Currently this is
 * only implemented for HsqlDb.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DefaultDBCleanerMultiSchemaPreserveTest extends UnitilsJUnit4 {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DefaultDBCleanerMultiSchemaPreserveTest.class);

    private DBCleaner dbCleaner;

    @TestDataSource
    protected DataSource dataSource;
    protected boolean disabled;


    @Before
    public void setUp() throws Exception {
        Properties configuration = getUnitilsConfiguration();
        this.disabled = !"hsqldb".equals(PropertyUtils.getString(PROPERTY_DIALECT, configuration));
        if (disabled) {
            return;
        }

        dropTestTables();
        createTestTables();

        // configure 3 schemas
        configuration.setProperty(PROPERTY_SCHEMANAMES, "PUBLIC, SCHEMA_A, \"SCHEMA_B\", schema_c");
        // items to preserve
        configuration.setProperty(PROPERTY_PRESERVE_DATA_SCHEMAS, "schema_c");
        configuration.setProperty(PROPERTY_PRESERVE_DATA_TABLES, "test, \"SCHEMA_A\".\"TEST\"");

        DbMaintainManager dbMaintainManager = new DbMaintainManager(configuration, false, new TestDataSourceFactory(), new UnitilsTransactionManager());
        dbCleaner = dbMaintainManager.getDbMaintainMainFactory().createDBCleaner();
    }

    @After
    public void tearDown() throws Exception {
        if (disabled) {
            return;
        }
        dropTestTables();
    }


    @Test
    public void testCleanDatabase() throws Exception {
        if (disabled) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        assertFalse(isEmpty("TEST", dataSource));
        assertFalse(isEmpty("SCHEMA_A.TEST", dataSource));
        assertFalse(isEmpty("SCHEMA_B.TEST", dataSource));
        assertFalse(isEmpty("SCHEMA_C.TEST", dataSource));
        dbCleaner.cleanDatabase();
        assertFalse(isEmpty("TEST", dataSource));
        assertFalse(isEmpty("SCHEMA_A.TEST", dataSource));
        assertTrue(isEmpty("SCHEMA_B.TEST", dataSource));
        assertFalse(isEmpty("SCHEMA_C.TEST", dataSource));
    }


    private void createTestTables() {
        // PUBLIC SCHEMA
        executeUpdate("create table TEST (dataset varchar(100))", dataSource);
        executeUpdate("insert into TEST values('test')", dataSource);
        // SCHEMA_A
        executeUpdate("create schema SCHEMA_A AUTHORIZATION DBA", dataSource);
        executeUpdate("create table SCHEMA_A.TEST (dataset varchar(100))", dataSource);
        executeUpdate("insert into SCHEMA_A.TEST values('test')", dataSource);
        // SCHEMA_B
        executeUpdate("create schema SCHEMA_B AUTHORIZATION DBA", dataSource);
        executeUpdate("create table SCHEMA_B.TEST (dataset varchar(100))", dataSource);
        executeUpdate("insert into SCHEMA_B.TEST values('test')", dataSource);
        // SCHEMA_C
        executeUpdate("create schema SCHEMA_C AUTHORIZATION DBA", dataSource);
        executeUpdate("create table SCHEMA_C.TEST (dataset varchar(100))", dataSource);
        executeUpdate("insert into SCHEMA_C.TEST values('test')", dataSource);
    }

    private void dropTestTables() {
        executeUpdateQuietly("drop table TEST", dataSource);
        executeUpdateQuietly("drop table SCHEMA_A.TEST", dataSource);
        executeUpdateQuietly("drop schema SCHEMA_A", dataSource);
        executeUpdateQuietly("drop table SCHEMA_B.TEST", dataSource);
        executeUpdateQuietly("drop schema SCHEMA_B", dataSource);
        executeUpdateQuietly("drop table SCHEMA_C.TEST", dataSource);
        executeUpdateQuietly("drop schema SCHEMA_C", dataSource);
    }


}