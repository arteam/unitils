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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hsqldb.Trigger;
import org.unitils.UnitilsJUnit3;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.dbsupport.DbSupport;
import static org.unitils.core.dbsupport.DbSupportFactory.PROPKEY_DATABASE_SCHEMA_NAMES;
import static org.unitils.core.dbsupport.DbSupportFactory.getDbSupport;
import static org.unitils.core.dbsupport.TestSQLUtils.executeUpdateQuietly;
import static org.unitils.core.util.SQLUtils.executeUpdate;
import org.unitils.database.annotations.TestDataSource;
import static org.unitils.dbmaintainer.util.DatabaseModuleConfigUtils.PROPKEY_DATABASE_DIALECT;
import static org.unitils.dbmaintainer.util.DatabaseModuleConfigUtils.getConfiguredDatabaseTaskInstance;
import org.unitils.util.PropertyUtils;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Test class for the {@link DBClearer} using multiple database schemas.
 * <p/>
 * This test is currenlty only implemented for HsqlDb
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DBClearerMultiSchemaTest extends UnitilsJUnit3 {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DBClearerMultiSchemaTest.class);

    /* DataSource for the test database, is injected */
    @TestDataSource
    private DataSource dataSource = null;

    /* Tested object */
    private DBClearer dbClearer;

    /* The db support for the default PUBLIC schema */
    private DbSupport dbSupportPublic;

    /* The db support for the SCHEMA_A schema */
    private DbSupport dbSupportSchemaA;

    /* The db support for the SCHEMA_B schema */
    private DbSupport dbSupportSchemaB;

    /* True if current test is not for the current dialect */
    private boolean disabled;


    /**
     * Configures the tested object. Creates a test table, index, view and sequence
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        Properties configuration = new ConfigurationLoader().loadConfiguration();
        this.disabled = !"hsqldb".equals(PropertyUtils.getString(PROPKEY_DATABASE_DIALECT, configuration));
        if (disabled) {
            return;
        }

        // configure 3 schemas
        configuration.setProperty(PROPKEY_DATABASE_SCHEMA_NAMES, "PUBLIC, SCHEMA_A, SCHEMA_B");
        dbSupportPublic = getDbSupport(configuration, dataSource, "PUBLIC");
        dbSupportSchemaA = getDbSupport(configuration, dataSource, "SCHEMA_A");
        dbSupportSchemaB = getDbSupport(configuration, dataSource, "SCHEMA_B");
        // create clearer instance
        dbClearer = getConfiguredDatabaseTaskInstance(DBClearer.class, configuration, dataSource);

        dropTestDatabase();
        createTestDatabase();
    }


    /**
     * Removes all test tables.
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        dropTestDatabase();
    }


    /**
     * Checks if the tables are correctly dropped.
     */
    public void testClearDatabase_tables() throws Exception {
        assertEquals(1, dbSupportPublic.getTableNames().size());
        assertEquals(1, dbSupportSchemaA.getTableNames().size());
        assertEquals(1, dbSupportSchemaB.getTableNames().size());
        dbClearer.clearSchemas();
        assertTrue(dbSupportPublic.getTableNames().isEmpty());
        assertTrue(dbSupportSchemaA.getTableNames().isEmpty());
        assertTrue(dbSupportSchemaB.getTableNames().isEmpty());
    }


    /**
     * Checks if the views are correctly dropped
     */
    public void testClearDatabase_views() throws Exception {
        assertEquals(1, dbSupportPublic.getViewNames().size());
        assertEquals(1, dbSupportSchemaA.getViewNames().size());
        assertEquals(1, dbSupportSchemaB.getViewNames().size());
        dbClearer.clearSchemas();
        assertTrue(dbSupportPublic.getViewNames().isEmpty());
        assertTrue(dbSupportSchemaA.getViewNames().isEmpty());
        assertTrue(dbSupportSchemaB.getViewNames().isEmpty());
    }


    /**
     * Tests if the triggers are correctly dropped
     */
    public void testClearDatabase_sequences() throws Exception {
        assertEquals(1, dbSupportPublic.getSequenceNames().size());
        assertEquals(1, dbSupportSchemaA.getSequenceNames().size());
        assertEquals(1, dbSupportSchemaB.getSequenceNames().size());
        dbClearer.clearSchemas();
        assertTrue(dbSupportPublic.getSequenceNames().isEmpty());
        assertTrue(dbSupportSchemaA.getSequenceNames().isEmpty());
        assertTrue(dbSupportSchemaB.getSequenceNames().isEmpty());
    }


    /**
     * Creates all test database structures (view, tables...)
     */
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


    /**
     * Drops all created test database structures (views, tables...)
     */
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
        executeUpdateQuietly("drop table TEST_TABLE", dataSource);
        executeUpdateQuietly("drop table SCHEMA_A.TEST_TABLE", dataSource);
        executeUpdateQuietly("drop table SCHEMA_B.TEST_TABLE", dataSource);
        // drop schemas
        executeUpdateQuietly("drop schema SCHEMA_A", dataSource);
        executeUpdateQuietly("drop schema SCHEMA_B", dataSource);
    }


    /**
     * Test trigger for hypersonic.
     *
     * @author Filip Neven
     * @author Tim Ducheyne
     */
    public static class TestTrigger implements Trigger {

        public void fire(int i, String string, String string1, Object[] objects, Object[] objects1) {
        }
    }

}
