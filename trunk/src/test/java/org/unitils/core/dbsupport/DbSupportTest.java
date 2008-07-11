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
package org.unitils.core.dbsupport;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.unitils.core.util.SQLTestUtils.dropTestSequences;
import static org.unitils.core.util.SQLTestUtils.dropTestSynonyms;
import static org.unitils.core.util.SQLTestUtils.dropTestTables;
import static org.unitils.core.util.SQLTestUtils.dropTestTriggers;
import static org.unitils.core.util.SQLTestUtils.dropTestTypes;
import static org.unitils.core.util.SQLTestUtils.dropTestViews;
import static org.unitils.database.SQLUnitils.executeUpdate;
import static org.unitils.database.SQLUnitils.getItemAsLong;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenEquals;

import java.util.Properties;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hsqldb.Trigger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.util.TestUtils;

/**
 * Tests for the db support class. Each type of database has to provide a subclass in which it sets-up the database
 * structure during the test setup and cleans the test structure afterwards.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 * @author Scott Prater
 */
public class DbSupportTest {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DbSupportTest.class);

    /* DataSource for the test database */
    protected DataSource dataSource;

    /* Instance under test */
    protected DbSupport dbSupport;


    /**
     * Sets up the test fixture.
     */
    @Before
    public void setUp() throws Exception {
        Properties configuration = new ConfigurationLoader().loadConfiguration();
        dbSupport = TestUtils.getDefaultDbSupport(configuration);
        dataSource = dbSupport.getDataSource();

        cleanupTestDatabase();
        createTestDatabase();
    }


    /**
     * Removes all test tables.
     */
    @After
    public void tearDown() throws Exception {
        cleanupTestDatabase();
    }


    /**
     * Tests getting the table names.
     */
    @Test
    public void testGetTableNames() throws Exception {
        Set<String> result = dbSupport.getTableNames(dbSupport.getDefaultSchemaName());
        if ("mysql".equals(dbSupport.getDatabaseDialect())) {
            // MySQL quoting behavior: quoted identifiers are not treated as case sensitive.
            assertLenEquals(asList(dbSupport.toCorrectCaseIdentifier("test_table"), dbSupport.toCorrectCaseIdentifier("Test_CASE_Table")), result);
        } else {
            assertLenEquals(asList(dbSupport.toCorrectCaseIdentifier("test_table"), "Test_CASE_Table"), result);
        }
    }


    /**
     * Tests getting the column names.
     */
    @Test
    public void testGetColumnNames() throws Exception {
        Set<String> result = dbSupport.getColumnNames(dbSupport.getDefaultSchemaName(), dbSupport.toCorrectCaseIdentifier("test_table"));
        assertLenEquals(asList(dbSupport.toCorrectCaseIdentifier("col1"), dbSupport.toCorrectCaseIdentifier("col2")), result);
    }


    /**
     * Tests getting the table names but no tables in db.
     */
    @Test
    public void testGetTableNames_noFound() throws Exception {
        cleanupTestDatabase();

        Set<String> result = dbSupport.getTableNames(dbSupport.getDefaultSchemaName());
        assertTrue(result.isEmpty());
    }


    /**
     * Tests getting the view names.
     */
    @Test
    public void testGetViewNames() throws Exception {
        Set<String> result = dbSupport.getViewNames(dbSupport.getDefaultSchemaName());
        if ("mysql".equals(dbSupport.getDatabaseDialect())) {
            // MySQL quoting behavior: quoted identifiers are not treated as case sensitive.
            assertLenEquals(asList(dbSupport.toCorrectCaseIdentifier("test_view"), dbSupport.toCorrectCaseIdentifier("Test_CASE_View")), result);
        } else {
            assertLenEquals(asList(dbSupport.toCorrectCaseIdentifier("test_view"), "Test_CASE_View"), result);
        }
    }


    /**
     * Tests getting the view names but no views in db.
     */
    @Test
    public void testGetViewNames_noFound() throws Exception {
        cleanupTestDatabase();
        Set<String> result = dbSupport.getViewNames(dbSupport.getDefaultSchemaName());
        assertTrue(result.isEmpty());
    }


    /**
     * Tests getting the synonym names.
     */
    @Test
    public void testGetSynonymNames() throws Exception {
        if (!dbSupport.supportsSynonyms()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        Set<String> result = dbSupport.getSynonymNames(dbSupport.getDefaultSchemaName());
        assertLenEquals(asList(dbSupport.toCorrectCaseIdentifier("test_synonym"), "Test_CASE_Synonym"), result);
    }


    /**
     * Tests getting the synonym names but no synonym in db.
     */
    @Test
    public void testGetSynonymNames_noFound() throws Exception {
        if (!dbSupport.supportsSynonyms()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        cleanupTestDatabase();
        Set<String> result = dbSupport.getSynonymNames(dbSupport.getDefaultSchemaName());
        assertTrue(result.isEmpty());
    }


    /**
     * Tests getting the sequence names.
     */
    @Test
    public void testGetSequenceNames() throws Exception {
        if (!dbSupport.supportsSequences()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        Set<String> result = dbSupport.getSequenceNames(dbSupport.getDefaultSchemaName());
        assertLenEquals(asList(dbSupport.toCorrectCaseIdentifier("TEST_SEQUENCE"), "Test_CASE_Sequence"), result);
    }


    /**
     * Tests getting the sequence names but no sequences in db.
     */
    @Test
    public void testGetSequenceNames_noFound() throws Exception {
        if (!dbSupport.supportsSequences()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        cleanupTestDatabase();
        Set<String> result = dbSupport.getSequenceNames(dbSupport.getDefaultSchemaName());
        assertTrue(result.isEmpty());
    }


    /**
     * Tests getting the trigger names.
     */
    @Test
    public void testGetTriggerNames() throws Exception {
        if (!dbSupport.supportsTriggers()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        Set<String> result = dbSupport.getTriggerNames(dbSupport.getDefaultSchemaName());
        if ("mysql".equals(dbSupport.getDatabaseDialect())) {
            // MySQL trigger behavior: trigger names are case-sensitive
            assertLenEquals(asList("test_trigger", "Test_CASE_Trigger"), result);
        } else if ("postgresql".equals(dbSupport.getDatabaseDialect())) {
            // Postgresql trigger behavior: non-standard drop statement (see PostgreSqlDbSupport.dropTrigger for more info
            // Triggers are returned as  'trigger-name' ON 'table name'
            assertLenEquals(asList(dbSupport.quoted("test_trigger") + " ON " + dbSupport.qualified(dbSupport.getDefaultSchemaName(), "Test_CASE_Table"), dbSupport.quoted("Test_CASE_Trigger") + " ON " + dbSupport.qualified(dbSupport.getDefaultSchemaName(), "Test_CASE_Table")), result);
        } else {
            assertLenEquals(asList(dbSupport.toCorrectCaseIdentifier("test_trigger"), "Test_CASE_Trigger"), result);
        }
    }


    /**
     * Tests getting the trigger names but no triggers in db.
     */
    @Test
    public void testGetTriggerNames_noFound() throws Exception {
        if (!dbSupport.supportsTriggers()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        cleanupTestDatabase();
        Set<String> result = dbSupport.getTriggerNames(dbSupport.getDefaultSchemaName());
        assertTrue(result.isEmpty());
    }


    /**
     * Tests getting the user-defined type names.
     */
    @Test
    public void testGetTypeNames() throws Exception {
        if (!dbSupport.supportsTypes()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        Set<String> result = dbSupport.getTypeNames(dbSupport.getDefaultSchemaName());
        assertLenEquals(asList(dbSupport.toCorrectCaseIdentifier("test_type"), "Test_CASE_Type"), result);
    }


    /**
     * Tests getting the user-defined types names but no user-defined types in db.
     */
    @Test
    public void testGetTypeNames_noFound() throws Exception {
        if (!dbSupport.supportsTypes()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        cleanupTestDatabase();
        Set<String> result = dbSupport.getTypeNames(dbSupport.getDefaultSchemaName());
        assertTrue(result.isEmpty());
    }


    /**
     * Tests getting the primary column names.
     */
    @Test
    public void testGetIdentityColumnNames() throws Exception {
        if (!dbSupport.supportsIdentityColumns()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        Set<String> result = dbSupport.getIdentityColumnNames(dbSupport.getDefaultSchemaName(), dbSupport.toCorrectCaseIdentifier("test_table"));
        assertLenEquals(asList(dbSupport.toCorrectCaseIdentifier("col1")), result);
    }


    /**
     * Tests incrementing the current value of the primary key.
     */
    @Test
    public void testIncrementIdentityColumnToValue() throws Exception {
        if (!dbSupport.supportsIdentityColumns()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }

        dbSupport.incrementIdentityColumnToValue(dbSupport.getDefaultSchemaName(), dbSupport.toCorrectCaseIdentifier("TEST_TABLE"), "COL1", 30);
        executeUpdate("insert into test_table (col2) values ('xxxx')", dataSource);

        long result = getItemAsLong("select col1 from test_table", dataSource);
        assertEquals(30, result);
    }


    /**
     * Tests dropping a table.
     */
    @Test
    public void testDropTable() throws Exception {
        // Drop cascade does not work in MySQL and Derby. Therefore we first need to
        // drop the views, next 'Test_CASE_Table' and then test_table.
        if ("mysql".equals(dbSupport.getDatabaseDialect()) || "derby".equals(dbSupport.getDatabaseDialect())) {
            dbSupport.dropView(dbSupport.getDefaultSchemaName(), "Test_CASE_View");
            dbSupport.dropView(dbSupport.getDefaultSchemaName(), dbSupport.toCorrectCaseIdentifier("test_view"));
            dbSupport.dropTable(dbSupport.getDefaultSchemaName(), "Test_CASE_Table");
            dbSupport.dropTable(dbSupport.getDefaultSchemaName(), dbSupport.toCorrectCaseIdentifier("test_table"));
        } else {
            Set<String> tableNames = dbSupport.getTableNames(dbSupport.getDefaultSchemaName());
            for (String tableName : tableNames) {
                dbSupport.dropTable(dbSupport.getDefaultSchemaName(), tableName);
            }
        }
        Set<String> result = dbSupport.getTableNames(dbSupport.getDefaultSchemaName());
        assertTrue(result.isEmpty());
    }


    /**
     * Tests dropping a view.
     */
    @Test
    public void testDropView() throws Exception {
        Set<String> viewNames = dbSupport.getViewNames(dbSupport.getDefaultSchemaName());
        for (String viewName : viewNames) {
            dbSupport.dropView(dbSupport.getDefaultSchemaName(), viewName);
        }
        Set<String> result = dbSupport.getViewNames(dbSupport.getDefaultSchemaName());
        assertTrue(result.isEmpty());
    }


    /**
     * Tests dropping a trigger.
     */
    @Test
    public void testDropTrigger() throws Exception {
        if (!dbSupport.supportsTriggers()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }

        Set<String> triggerNames = dbSupport.getTriggerNames(dbSupport.getDefaultSchemaName());
        for (String triggerName : triggerNames) {
            dbSupport.dropTrigger(dbSupport.getDefaultSchemaName(), triggerName);
        }
        Set<String> result = dbSupport.getTriggerNames(dbSupport.getDefaultSchemaName());
        assertTrue(result.isEmpty());
    }


    /**
     * Tests dropping a type.
     */
    @Test
    public void testDropType() throws Exception {
        if (!dbSupport.supportsTypes()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }

        Set<String> typeNames = dbSupport.getTypeNames(dbSupport.getDefaultSchemaName());
        for (String typeName : typeNames) {
            dbSupport.dropType(dbSupport.getDefaultSchemaName(), typeName);
        }
        Set<String> result = dbSupport.getTypeNames(dbSupport.getDefaultSchemaName());
        assertTrue(result.isEmpty());
    }


    /**
     * Tests incrementing and getting the current sequence value.
     */
    @Test
    public void testGetCurrentValueOfSequence() throws Exception {
        if (!dbSupport.supportsSequences()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }

        dbSupport.incrementSequenceToValue(dbSupport.getDefaultSchemaName(), dbSupport.toCorrectCaseIdentifier("TEST_SEQUENCE"), 30);
        long result = dbSupport.getSequenceValue(dbSupport.getDefaultSchemaName(), dbSupport.toCorrectCaseIdentifier("TEST_SEQUENCE"));
        assertEquals(30, result);
    }


    /**
     * Tests disabling a foreign key constraint on the test table.
     */
    @Test
    public void testRemoveReferentialConstraints() throws Exception {
        dbSupport.removeReferentialConstraints(dbSupport.getDefaultSchemaName(), "Test_CASE_Table");

        // should succeed now
        // drop triggers to avoid side-effects during insert
        Set<String> triggerNames = dbSupport.getTriggerNames(dbSupport.getDefaultSchemaName());
        for (String triggerName : triggerNames) {
            dbSupport.dropTrigger(dbSupport.getDefaultSchemaName(), triggerName);
        }
        executeUpdate("insert into " + dbSupport.quoted("Test_CASE_Table") + " (col1) values (null)", dataSource);
    }


    /**
     * Tests disabling foreign key constraints on the test table but with an unexisting table name.
     * Nothing should happen
     */
    @Test
    public void testRemoveReferentialConstraints_tableNotFound() throws Exception {
        dbSupport.removeReferentialConstraints(dbSupport.getDefaultSchemaName(), "xxxx");
    }


    /**
     * Tests disabling not null constraints on the test table.
     */
    @Test
    public void testRemoveValueConstraints() throws Exception {
        dbSupport.removeValueConstraints(dbSupport.getDefaultSchemaName(), dbSupport.toCorrectCaseIdentifier("TEST_TABLE"));

        // should succeed now
        if ("mssql".equals(dbSupport.getDatabaseDialect())) {
            // col1 is an identity column, don't insert a value in col1
            executeUpdate("insert into test_table (col2) values (null)", dataSource);
        } else {
            executeUpdate("insert into test_table (col1, col2) values (1, null)", dataSource);
        }
    }


    /**
     * Tests disabling not null constraints on the test table but with an unexisting table name.
     * Nothing should happen
     */
    @Test
    public void testRemoveValueConstraints_tableNotFound() throws Exception {
        dbSupport.removeValueConstraints(dbSupport.getDefaultSchemaName(), "xxxx");
    }


    /**
     * Creates all test database structures (view, tables...)
     */
    private void createTestDatabase() throws Exception {
        String dialect = dbSupport.getDatabaseDialect();
        if ("hsqldb".equals(dialect)) {
            createTestDatabaseHsqlDb();
        } else if ("mysql".equals(dialect)) {
            createTestDatabaseMySql();
        } else if ("oracle".equals(dialect)) {
            createTestDatabaseOracle();
        } else if ("postgresql".equals(dialect)) {
            createTestDatabasePostgreSql();
        } else if ("db2".equals(dialect)) {
            createTestDatabaseDb2();
        } else if ("derby".equals(dialect)) {
            createTestDatabaseDerby();
        } else if ("mssql".equals(dialect)) {
            createTestDatabaseMsSql();
        } else {
            fail("This test is not implemented for current dialect: " + dialect);
        }
    }


    /**
     * Drops all created test database structures (views, tables...)
     */
    private void cleanupTestDatabase() throws Exception {
        String dialect = dbSupport.getDatabaseDialect();
        if ("hsqldb".equals(dialect)) {
            cleanupTestDatabaseHsqlDb();
        } else if ("mysql".equals(dialect)) {
            cleanupTestDatabaseMySql();
        } else if ("oracle".equals(dialect)) {
            cleanupTestDatabaseOracle();
        } else if ("postgresql".equals(dialect)) {
            cleanupTestDatabasePostgreSql();
        } else if ("db2".equals(dialect)) {
            cleanupTestDatabaseDb2();
        } else if ("derby".equals(dialect)) {
            cleanupTestDatabaseDerby();
        } else if ("mssql".equals(dialect)) {
            cleanupTestDatabaseMsSql();
        }
    }

    //
    // Database setup for HsqlDb
    //

    /**
     * Creates all test database structures (view, tables...)
     */
    private void createTestDatabaseHsqlDb() throws Exception {
        // create tables
        executeUpdate("create table test_table (col1 int not null identity, col2 varchar(12) not null)", dataSource);
        executeUpdate("create table \"Test_CASE_Table\" (col1 int, foreign key (col1) references test_table(col1))", dataSource);
        // create views
        executeUpdate("create view test_view as select col1 from test_table", dataSource);
        executeUpdate("create view \"Test_CASE_View\" as select col1 from \"Test_CASE_Table\"", dataSource);
        // create sequences
        executeUpdate("create sequence test_sequence", dataSource);
        executeUpdate("create sequence \"Test_CASE_Sequence\"", dataSource);
        // create triggers
        executeUpdate("create trigger test_trigger before insert on \"Test_CASE_Table\" call \"org.unitils.core.dbsupport.HsqldbDbSupportTest.TestTrigger\"", dataSource);
        executeUpdate("create trigger \"Test_CASE_Trigger\" before insert on \"Test_CASE_Table\" call \"org.unitils.core.dbsupport.HsqldbDbSupportTest.TestTrigger\"", dataSource);
    }


    /**
     * Drops all created test database structures (views, tables...)
     */
    private void cleanupTestDatabaseHsqlDb() throws Exception {
        dropTestTables(dbSupport, "test_table", "\"Test_CASE_Table\"");
        dropTestViews(dbSupport, "test_view", "\"Test_CASE_View\"");
        dropTestSequences(dbSupport, "test_sequence", "\"Test_CASE_Sequence\"");
        dropTestTriggers(dbSupport, "test_trigger", "\"Test_CASE_Trigger\"");
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

    //
    // Database setup for MySql
    //

    /**
     * Creates all test database structures (view, tables...) <p/> NO FOREIGN KEY USED: drop cascade does not work in
     * MySQL
     */
    private void createTestDatabaseMySql() throws Exception {
        // create tables
        executeUpdate("create table test_table (col1 int not null primary key AUTO_INCREMENT, col2 varchar(12) not null)", dataSource);
        executeUpdate("create table `Test_CASE_Table` (col1 int, foreign key (col1) references test_table(col1))", dataSource);
        // create views
        executeUpdate("create view test_view as select col1 from test_table", dataSource);
        executeUpdate("create view `Test_CASE_View` as select col1 from `Test_CASE_Table`", dataSource);
        // create triggers
        executeUpdate("create trigger test_trigger before insert on `Test_CASE_Table` FOR EACH ROW begin end", dataSource);
        executeUpdate("create trigger `Test_CASE_Trigger` after insert on `Test_CASE_Table` FOR EACH ROW begin end", dataSource);
    }


    /**
     * Drops all created test database structures (views, tables...)
     */
    private void cleanupTestDatabaseMySql() throws Exception {
        dropTestTables(dbSupport, "`Test_CASE_Table`", "test_table");
        dropTestViews(dbSupport, "test_view", "`Test_CASE_View`");
        dropTestTriggers(dbSupport, "test_trigger", "`Test_CASE_Trigger`");
    }

    //
    // Database setup for Oracle
    //

    /**
     * Creates all test database structures (view, tables...)
     */
    private void createTestDatabaseOracle() throws Exception {
        // create tables
        executeUpdate("create table test_table (col1 varchar(10) not null primary key, col2 varchar(12) not null)", dataSource);
        executeUpdate("create table \"Test_CASE_Table\" (col1 varchar(10), foreign key (col1) references test_table(col1))", dataSource);
        // create views
        executeUpdate("create view test_view as select col1 from test_table", dataSource);
        executeUpdate("create view \"Test_CASE_View\" as select col1 from \"Test_CASE_Table\"", dataSource);
        // create synonyms
        executeUpdate("create synonym test_synonym for test_table", dataSource);
        executeUpdate("create synonym \"Test_CASE_Synonym\" for \"Test_CASE_Table\"", dataSource);
        // create sequences
        executeUpdate("create sequence test_sequence", dataSource);
        executeUpdate("create sequence \"Test_CASE_Sequence\"", dataSource);
        // create triggers
        executeUpdate("create or replace trigger test_trigger before insert on \"Test_CASE_Table\" begin dbms_output.put_line('test'); end test_trigger", dataSource);
        executeUpdate("create or replace trigger \"Test_CASE_Trigger\" before insert on \"Test_CASE_Table\" begin dbms_output.put_line('test'); end \"Test_CASE_Trigger\"", dataSource);
        // create types
        executeUpdate("create type test_type AS (col1 int)", dataSource);
        executeUpdate("create type \"Test_CASE_Type\" AS (col1 int)", dataSource);
    }


    /**
     * Drops all created test database structures (views, tables...)
     */
    private void cleanupTestDatabaseOracle() throws Exception {
        dropTestTables(dbSupport, "test_table", "\"Test_CASE_Table\"");
        dropTestViews(dbSupport, "test_view", "\"Test_CASE_View\"");
        dropTestSynonyms(dbSupport, "test_synonym", "\"Test_CASE_Synonym\"");
        dropTestSequences(dbSupport, "test_sequence", "\"Test_CASE_Sequence\"");
        dropTestTriggers(dbSupport, "test_trigger", "\"Test_CASE_Trigger\"");
        dropTestTypes(dbSupport, "test_type", "\"Test_CASE_Type\"");
    }

    //
    // Database setup for PostgreSql
    //

    /**
     * Creates all test database structures (view, tables...)
     */
    private void createTestDatabasePostgreSql() throws Exception {
        // create tables
        executeUpdate("create table test_table (col1 varchar(10) not null primary key, col2 varchar(12) not null)", dataSource);
        executeUpdate("create table \"Test_CASE_Table\" (col1 varchar(10), foreign key (col1) references test_table(col1))", dataSource);
        // create views
        executeUpdate("create view test_view as select col1 from test_table", dataSource);
        executeUpdate("create view \"Test_CASE_View\" as select col1 from \"Test_CASE_Table\"", dataSource);
        // create sequences
        executeUpdate("create sequence test_sequence", dataSource);
        executeUpdate("create sequence \"Test_CASE_Sequence\"", dataSource);
        // create triggers
        try {
            executeUpdate("create language plpgsql", dataSource);
        } catch (Exception e) {
            // ignore language already exists
        }
        executeUpdate("create or replace function test() returns trigger as $$ declare begin end; $$ language plpgsql", dataSource);
        executeUpdate("create trigger test_trigger before insert on \"Test_CASE_Table\" FOR EACH ROW EXECUTE PROCEDURE test()", dataSource);
        executeUpdate("create trigger \"Test_CASE_Trigger\" before insert on \"Test_CASE_Table\" FOR EACH ROW EXECUTE PROCEDURE test()", dataSource);
        // create types
        executeUpdate("create type test_type AS (col1 int)", dataSource);
        executeUpdate("create type \"Test_CASE_Type\" AS (col1 int)", dataSource);
    }


    /**
     * Drops all created test database structures (views, tables...)
     */
    private void cleanupTestDatabasePostgreSql() throws Exception {
        dropTestTables(dbSupport, "test_table", "\"Test_CASE_Table\"");
        dropTestViews(dbSupport, "test_view", "\"Test_CASE_View\"");
        dropTestSequences(dbSupport, "test_sequence", "\"Test_CASE_Sequence\"");
        dropTestTriggers(dbSupport, "test_trigger ON \"Test_CASE_Sequence\"", "\"Test_CASE_Trigger\" ON \"Test_CASE_Sequence\"");
        dropTestTypes(dbSupport, "test_type", "\"Test_CASE_Type\"");
    }

    //
    // Database setup for Db2
    //

    /**
     * Creates all test database structures (view, tables...)
     */
    private void createTestDatabaseDb2() throws Exception {
        // create tables
        executeUpdate("create table test_table (col1 int not null primary key generated by default as identity, col2 varchar(12) not null)", dataSource);
        executeUpdate("create table \"Test_CASE_Table\" (col1 int, foreign key (col1) references test_table(col1))", dataSource);
        // create views
        executeUpdate("create view test_view as select col1 from test_table", dataSource);
        executeUpdate("create view \"Test_CASE_View\" as select col1 from \"Test_CASE_Table\"", dataSource);
        // create sequences
        executeUpdate("create sequence test_sequence", dataSource);
        executeUpdate("create sequence \"Test_CASE_Sequence\"", dataSource);
        // create triggers
        executeUpdate("create trigger test_trigger before insert on \"Test_CASE_Table\" FOR EACH ROW when (1 < 0) SIGNAL SQLSTATE '0'", dataSource);
        executeUpdate("create trigger \"Test_CASE_Trigger\" before insert on \"Test_CASE_Table\" FOR EACH ROW when (1 < 0) SIGNAL SQLSTATE '0'", dataSource);
        // create types
        executeUpdate("create type test_type AS (col1 int) MODE DB2SQL", dataSource);
        executeUpdate("create type \"Test_CASE_Type\" AS (col1 int) MODE DB2SQL", dataSource);
    }


    /**
     * Drops all created test database structures (views, tables...)
     */
    private void cleanupTestDatabaseDb2() throws Exception {
        dropTestTables(dbSupport, "test_table", "\"Test_CASE_Table\"");
        dropTestViews(dbSupport, "test_view", "\"Test_CASE_View\"");
        dropTestSequences(dbSupport, "test_sequence", "\"Test_CASE_Sequence\"");
        dropTestTriggers(dbSupport, "test_trigger", "\"Test_CASE_Trigger\"");
        dropTestTypes(dbSupport, "test_type", "\"Test_CASE_Type\"");
    }

    //
    // Database setup for Derby
    //

    /**
     * Creates all test database structures (view, tables...)
     */
    private void createTestDatabaseDerby() throws Exception {
        // create tables
        executeUpdate("create table test_table (col1 int not null primary key generated by default as identity, col2 varchar(12) not null)", dataSource);
        executeUpdate("create table \"Test_CASE_Table\" (col1 int, foreign key (col1) references test_table(col1))", dataSource);
        // create views
        executeUpdate("create view test_view as select col1 from test_table", dataSource);
        executeUpdate("create view \"Test_CASE_View\" as select col1 from \"Test_CASE_Table\"", dataSource);
        // create synonyms
        executeUpdate("create synonym test_synonym for test_table", dataSource);
        executeUpdate("create synonym \"Test_CASE_Synonym\" for \"Test_CASE_Table\"", dataSource);
        // create triggers
        executeUpdate("call SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('testKey', 'test')", dataSource);
        executeUpdate("create trigger test_trigger no cascade before insert on \"Test_CASE_Table\" FOR EACH ROW MODE DB2SQL VALUES SYSCS_UTIL.SYSCS_GET_DATABASE_PROPERTY('testKey')", dataSource);
        executeUpdate("create trigger \"Test_CASE_Trigger\" no cascade before insert on \"Test_CASE_Table\" FOR EACH ROW MODE DB2SQL VALUES SYSCS_UTIL.SYSCS_GET_DATABASE_PROPERTY('testKey')", dataSource);
    }


    /**
     * Drops all created test database structures (views, tables...) First drop the views, since Derby doesn't support
     * "drop table ... cascade" (yet, as of Derby 10.3)
     */
    private void cleanupTestDatabaseDerby() throws Exception {
        dropTestSynonyms(dbSupport, "test_synonym", "\"Test_CASE_Synonym\"");
        dropTestViews(dbSupport, "test_view", "\"Test_CASE_View\"");
        dropTestTriggers(dbSupport, "test_trigger", "\"Test_CASE_Trigger\"");
        dropTestTables(dbSupport, "\"Test_CASE_Table\"", "test_table");
    }

    //
    // Database setup for MS-Sql
    //

    /**
     * Creates all test database structures (view, tables...)
     */
    private void createTestDatabaseMsSql() throws Exception {
        // create tables
        executeUpdate("create table test_table (col1 int not null primary key identity, col2 varchar(12) not null)", dataSource);
        executeUpdate("create table \"Test_CASE_Table\" (col1 int, foreign key (col1) references test_table(col1))", dataSource);
        // create views
        executeUpdate("create view test_view as select col1 from test_table", dataSource);
        executeUpdate("create view \"Test_CASE_View\" as select col1 from \"Test_CASE_Table\"", dataSource);
        // create synonyms
        executeUpdate("create synonym test_synonym for test_table", dataSource);
        executeUpdate("create synonym \"Test_CASE_Synonym\" for \"Test_CASE_Table\"", dataSource);
        // create triggers
        executeUpdate("create trigger test_trigger on \"Test_CASE_Table\" after insert AS select * from test_table", dataSource);
        executeUpdate("create trigger \"Test_CASE_Trigger\" on \"Test_CASE_Table\" after insert AS select * from test_table", dataSource);
        // create types
        executeUpdate("create type test_type from int", dataSource);
        executeUpdate("create type \"Test_CASE_Type\" from int", dataSource);
    }


    /**
     * Drops all created test database structures (views, tables...) First drop the views, since Derby doesn't support
     * "drop table ... cascade" (yet, as of Derby 10.3)
     */
    private void cleanupTestDatabaseMsSql() throws Exception {
        dropTestSynonyms(dbSupport, "test_synonym", "\"Test_CASE_Synonym\"");
        dropTestViews(dbSupport, "test_view", "\"Test_CASE_View\"");
        dropTestTriggers(dbSupport, "test_trigger", "\"Test_CASE_Trigger\"");
        dropTestTables(dbSupport, "\"Test_CASE_Table\"", "test_table");
        dropTestTypes(dbSupport, "test_type", "\"Test_CASE_Type\"");
    }
}
