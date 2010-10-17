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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.util.PropertyUtils;

import javax.sql.DataSource;
import java.util.Properties;

import static org.dbmaintain.config.DbMaintainProperties.PROPERTY_DIALECT;
import static org.dbmaintain.config.DbMaintainProperties.PROPERTY_SCHEMANAMES;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.unitils.database.DatabaseUnitils.cleanDatabase;
import static org.unitils.database.DatabaseUnitils.getDataSource;
import static org.unitils.database.SQLUnitils.*;
import static org.unitils.testutil.TestUnitilsConfiguration.*;

/**
 * Test class for the cleaning all tables in multiple schema's.
 * <p/>
 * Currently this is only implemented for HsqlDb.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DefaultDBCleanerMultiSchemaTest {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DefaultDBCleanerMultiSchemaTest.class);

    /* DataSource for the test database, is injected */
    private DataSource dataSource;

    /* True if current test is not for the current dialect */
    private boolean disabled;


    @Before
    public void initialize() throws Exception {
        Properties configuration = getUnitilsConfiguration();
        this.disabled = !"hsqldb".equals(PropertyUtils.getString(PROPERTY_DIALECT, configuration));
        if (disabled) {
            return;
        }

        // configure 3 schema"s
        configuration.setProperty(PROPERTY_SCHEMANAMES, "PUBLIC, SCHEMA_A, SCHEMA_B");
        reinitializeUnitils(configuration);

        dataSource = getDataSource(this);
        dropTestTables();
        createTestTables();
    }

    @After
    public void cleanUp() throws Exception {
        if (disabled) {
            return;
        }
        resetUnitils();
        dropTestTables();
    }


    @Test
    public void cleanDatabaseInMultipleSchemas() throws Exception {
        if (disabled) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        assertFalse(isEmpty("TEST", dataSource));
        assertFalse(isEmpty("SCHEMA_A.TEST", dataSource));
        assertFalse(isEmpty("SCHEMA_B.TEST", dataSource));
        cleanDatabase();
        assertTrue(isEmpty("TEST", dataSource));
        assertTrue(isEmpty("SCHEMA_A.TEST", dataSource));
        assertTrue(isEmpty("SCHEMA_B.TEST", dataSource));
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
    }

    private void dropTestTables() {
        executeUpdateQuietly("drop table TEST", dataSource);
        executeUpdateQuietly("drop table SCHEMA_A.TEST", dataSource);
        executeUpdateQuietly("drop schema SCHEMA_A", dataSource);
        executeUpdateQuietly("drop table SCHEMA_B.TEST", dataSource);
        executeUpdateQuietly("drop schema SCHEMA_B", dataSource);
    }


}
