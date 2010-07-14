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
package org.unitils.core.dbsupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbmaintain.dbsupport.DbSupport;
import org.hsqldb.Trigger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.unitils.core.util.SQLTestUtils.*;
import static org.unitils.database.DatabaseUnitils.getDbSupports;
import static org.unitils.database.SQLUnitils.executeUpdate;
import static org.unitils.database.SQLUnitils.getItemAsLong;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;

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

    protected DataSource dataSource;
    protected DbSupport defaultDbSupport;


    /**
     * Sets up the test fixture.
     */
    @Before
    public void setUp() throws Exception {
        defaultDbSupport = getDbSupports().getDefaultDbSupport();
        dataSource = defaultDbSupport.getDataSource();

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
        Set<String> result = defaultDbSupport.getTableNames();
        if ("mysql".equals(defaultDbSupport.getDatabaseInfo().getDialect())) {
            // MySQL quoting behavior: quoted identifiers are not treated as case sensitive.
            assertLenientEquals(asList(defaultDbSupport.toCorrectCaseIdentifier("test_table"), defaultDbSupport.toCorrectCaseIdentifier("Test_CASE_Table")), result);
        } else {
            assertLenientEquals(asList(defaultDbSupport.toCorrectCaseIdentifier("test_table"), "Test_CASE_Table"), result);
        }
    }


    /**
     * Tests getting the column names.
     */
    @Test
    public void testGetColumnNames() throws Exception {
        Set<String> result = defaultDbSupport.getColumnNames(defaultDbSupport.toCorrectCaseIdentifier("test_table"));
        assertLenientEquals(asList(defaultDbSupport.toCorrectCaseIdentifier("col1"), defaultDbSupport.toCorrectCaseIdentifier("col2")), result);
    }


    /**
     * Tests getting the table names but no tables in db.
     */
    @Test
    public void testGetTableNames_noFound() throws Exception {
        cleanupTestDatabase();

        Set<String> result = defaultDbSupport.getTableNames();
        assertTrue(result.isEmpty());
    }


    /**
     * Tests getting the view names.
     */
    @Test
    public void testGetViewNames() throws Exception {
        Set<String> result = defaultDbSupport.getViewNames();
        if ("mysql".equals(defaultDbSupport.getDatabaseInfo().getDialect())) {
            // MySQL quoting behavior: quoted identifiers are not treated as case sensitive.
            assertLenientEquals(asList(defaultDbSupport.toCorrectCaseIdentifier("test_view"), defaultDbSupport.toCorrectCaseIdentifier("Test_CASE_View")), result);
        } else {
            assertLenientEquals(asList(defaultDbSupport.toCorrectCaseIdentifier("test_view"), "Test_CASE_View"), result);
        }
    }


    /**
     * Tests getting the view names but no views in db.
     */
    @Test
    public void testGetViewNames_noFound() throws Exception {
        cleanupTestDatabase();
        Set<String> result = defaultDbSupport.getViewNames();
        assertTrue(result.isEmpty());
    }


    /**
     * Tests getting the synonym names.
     */
    @Test
    public void testGetSynonymNames() throws Exception {
        if (!defaultDbSupport.supportsSynonyms()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        Set<String> result = defaultDbSupport.getSynonymNames();
        assertLenientEquals(asList(defaultDbSupport.toCorrectCaseIdentifier("test_synonym"), "Test_CASE_Synonym"), result);
    }


    /**
     * Tests getting the synonym names but no synonym in db.
     */
    @Test
    public void testGetSynonymNames_noFound() throws Exception {
        if (!defaultDbSupport.supportsSynonyms()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        cleanupTestDatabase();
        Set<String> result = defaultDbSupport.getSynonymNames();
        assertTrue(result.isEmpty());
    }


    /**
     * Tests getting the sequence names.
     */
    @Test
    public void testGetSequenceNames() throws Exception {
        if (!defaultDbSupport.supportsSequences()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        Set<String> result = defaultDbSupport.getSequenceNames();
        assertLenientEquals(asList(defaultDbSupport.toCorrectCaseIdentifier("TEST_SEQUENCE"), "Test_CASE_Sequence"), result);
    }


    /**
     * Tests getting the sequence names but no sequences in db.
     */
    @Test
    public void testGetSequenceNames_noFound() throws Exception {
        if (!defaultDbSupport.supportsSequences()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        cleanupTestDatabase();
        Set<String> result = defaultDbSupport.getSequenceNames();
        assertTrue(result.isEmpty());
    }


    /**
     * Tests getting the trigger names.
     */
    @Test
    public void testGetTriggerNames() throws Exception {
        if (!defaultDbSupport.supportsTriggers()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        Set<String> result = defaultDbSupport.getTriggerNames();
        if ("mysql".equals(defaultDbSupport.getDatabaseInfo().getDialect())) {
            // MySQL trigger behavior: trigger names are case-sensitive
            assertLenientEquals(asList("test_trigger", "Test_CASE_Trigger"), result);
        } else if ("postgresql".equals(defaultDbSupport.getDatabaseInfo().getDialect())) {
            // Postgresql trigger behavior: non-standard drop statement (see PostgreSqlDbSupport.dropTrigger for more info
            // Triggers are returned as  'trigger-name' ON 'table name'
            assertLenientEquals(asList(defaultDbSupport.quoted("test_trigger") + " ON " + defaultDbSupport.qualified("Test_CASE_Table"), defaultDbSupport.quoted("Test_CASE_Trigger") + " ON " + defaultDbSupport.qualified("Test_CASE_Table")), result);
        } else {
            assertLenientEquals(asList(defaultDbSupport.toCorrectCaseIdentifier("test_trigger"), "Test_CASE_Trigger"), result);
        }
    }


    /**
     * Tests getting the trigger names but no triggers in db.
     */
    @Test
    public void testGetTriggerNames_noFound() throws Exception {
        if (!defaultDbSupport.supportsTriggers()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        cleanupTestDatabase();
        Set<String> result = defaultDbSupport.getTriggerNames();
        assertTrue(result.isEmpty());
    }


    /**
     * Tests getting the user-defined type names.
     */
    @Test
    public void testGetTypeNames() throws Exception {
        if (!defaultDbSupport.supportsTypes()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        Set<String> result = defaultDbSupport.getTypeNames();
        assertLenientEquals(asList(defaultDbSupport.toCorrectCaseIdentifier("test_type"), "Test_CASE_Type"), result);
    }


    /**
     * Tests getting the user-defined types names but no user-defined types in db.
     */
    @Test
    public void testGetTypeNames_noFound() throws Exception {
        if (!defaultDbSupport.supportsTypes()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        cleanupTestDatabase();
        Set<String> result = defaultDbSupport.getTypeNames();
        assertTrue(result.isEmpty());
    }


    /**
     * Tests getting the primary column names.
     */
    @Test
    public void testGetIdentityColumnNames() throws Exception {
        if (!defaultDbSupport.supportsIdentityColumns()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        Set<String> result = defaultDbSupport.getIdentityColumnNames(defaultDbSupport.toCorrectCaseIdentifier("test_table"));
        assertLenientEquals(asList(defaultDbSupport.toCorrectCaseIdentifier("col1")), result);
    }


    /**
     * Tests incrementing the current value of the primary key.
     */
    @Test
    public void testIncrementSequenceToValue() throws Exception {
        if (!defaultDbSupport.supportsSequences()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }

        String sequenceName = defaultDbSupport.toCorrectCaseIdentifier("TEST_SEQUENCE");
        defaultDbSupport.incrementSequenceToValue(sequenceName, 30);
        long result = defaultDbSupport.getSequenceValue(sequenceName);
        assertEquals(30, result);
    }

    /**
     * Tests incrementing the current value of the primary key.
     */
    @Test
    public void testIncrementIdentityColumnToValue() throws Exception {
        if (!defaultDbSupport.supportsIdentityColumns()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }

        defaultDbSupport.incrementIdentityColumnToValue(defaultDbSupport.toCorrectCaseIdentifier("TEST_TABLE"), "COL1", 30);
        executeUpdate("insert into test_table (col2) values ('xxxx')", dataSource);

        long result = getItemAsLong("select col1 from test_table", dataSource);
        assertEquals(30, result);
    }

    /**
     * Tests setting a value of an indentity column in an insert statement
     */
    @Test
    public void enableSetSettingIdentityColumnValueEnabled() throws Exception {
        if (!defaultDbSupport.supportsIdentityColumns()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }

        defaultDbSupport.setSettingIdentityColumnValueEnabled(defaultDbSupport.toCorrectCaseIdentifier("TEST_TABLE"), true);
        executeUpdate("insert into test_table (col1, col2) values (99, 'value')", dataSource);

        long result = getItemAsLong("select col1 from test_table", dataSource);
        assertEquals(99, result);
    }


    /**
     * Tests dropping a table.
     */
    @Test
    public void testDropTable() throws Exception {
        // Drop cascade does not work in MySQL and Derby. Therefore we first need to
        // drop the views, next 'Test_CASE_Table' and then test_table.
        if ("mysql".equals(defaultDbSupport.getDatabaseInfo().getDialect()) || "derby".equals(defaultDbSupport.getDatabaseInfo().getDialect())) {
            defaultDbSupport.dropView("Test_CASE_View");
            defaultDbSupport.dropView(defaultDbSupport.toCorrectCaseIdentifier("test_view"));
            defaultDbSupport.dropTable("Test_CASE_Table");
            defaultDbSupport.dropTable(defaultDbSupport.toCorrectCaseIdentifier("test_table"));
        } else {
            Set<String> tableNames = defaultDbSupport.getTableNames();
            for (String tableName : tableNames) {
                defaultDbSupport.dropTable(tableName);
            }
        }
        Set<String> result = defaultDbSupport.getTableNames();
        assertTrue(result.isEmpty());
    }


    /**
     * Tests dropping a view.
     */
    @Test
    public void testDropView() throws Exception {
        Set<String> viewNames = defaultDbSupport.getViewNames();
        for (String viewName : viewNames) {
            defaultDbSupport.dropView(viewName);
        }
        Set<String> result = defaultDbSupport.getViewNames();
        assertTrue(result.isEmpty());
    }


    /**
     * Tests dropping a trigger.
     */
    @Test
    public void testDropTrigger() throws Exception {
        if (!defaultDbSupport.supportsTriggers()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }

        Set<String> triggerNames = defaultDbSupport.getTriggerNames();
        for (String triggerName : triggerNames) {
            defaultDbSupport.dropTrigger(triggerName);
        }
        Set<String> result = defaultDbSupport.getTriggerNames();
        assertTrue(result.isEmpty());
    }


    /**
     * Tests dropping a type.
     */
    @Test
    public void testDropType() throws Exception {
        if (!defaultDbSupport.supportsTypes()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }

        Set<String> typeNames = defaultDbSupport.getTypeNames();
        for (String typeName : typeNames) {
            defaultDbSupport.dropType(typeName);
        }
        Set<String> result = defaultDbSupport.getTypeNames();
        assertTrue(result.isEmpty());
    }


    /**
     * Tests incrementing and getting the current sequence value.
     */
    @Test
    public void testGetCurrentValueOfSequence() throws Exception {
        if (!defaultDbSupport.supportsSequences()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }

        defaultDbSupport.incrementSequenceToValue(defaultDbSupport.toCorrectCaseIdentifier("TEST_SEQUENCE"), 30);
        long result = defaultDbSupport.getSequenceValue(defaultDbSupport.toCorrectCaseIdentifier("TEST_SEQUENCE"));
        assertEquals(30, result);
    }


    /**
     * Tests disabling a foreign key constraint on the test tables.
     */
    @Test
    public void testRemoveReferentialConstraints() throws Exception {
        defaultDbSupport.disableReferentialConstraints();

        // should succeed now
        // drop triggers to avoid side-effects during insert
        Set<String> triggerNames = defaultDbSupport.getTriggerNames();
        for (String triggerName : triggerNames) {
            defaultDbSupport.dropTrigger(triggerName);
        }
        executeUpdate("insert into " + defaultDbSupport.quoted("Test_CASE_Table") + " (col1) values (null)", dataSource);
    }


    /**
     * Tests disabling foreign key constraints but there are no tables.
     * Nothing should happen
     */
    @Test
    public void testRemoveReferentialConstraints_noTablesFound() throws Exception {
        cleanupTestDatabase();
        defaultDbSupport.disableReferentialConstraints();
    }


    /**
     * Tests disabling not null constraints on the test tables.
     */
    @Test
    public void testRemoveValueConstraints() throws Exception {
        defaultDbSupport.disableValueConstraints();

        // should succeed now
        if ("mssql".equals(defaultDbSupport.getDatabaseInfo().getDialect())) {
            // col1 is an identity column, don't insert a value in col1
            executeUpdate("insert into test_table (col2) values (null)", dataSource);
        } else {
            executeUpdate("insert into test_table (col1, col2) values (1, null)", dataSource);
        }
    }


    /**
     * Tests disabling not null constraints but there are no tables.
     * Nothing should happen
     */
    @Test
    public void testRemoveValueConstraints_noTablesFound() throws Exception {
        cleanupTestDatabase();
        defaultDbSupport.disableValueConstraints();
    }


    /**
     * Creates all test database structures (view, tables...)
     */
    private void createTestDatabase() throws Exception {
        String dialect = defaultDbSupport.getDatabaseInfo().getDialect();
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
        String dialect = defaultDbSupport.getDatabaseInfo().getDialect();
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
        dropTestTables(defaultDbSupport, "test_table", "\"Test_CASE_Table\"");
        dropTestViews(defaultDbSupport, "test_view", "\"Test_CASE_View\"");
        dropTestSequences(defaultDbSupport, "test_sequence", "\"Test_CASE_Sequence\"");
        dropTestTriggers(defaultDbSupport, "test_trigger", "\"Test_CASE_Trigger\"");
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
        dropTestTables(defaultDbSupport, "`Test_CASE_Table`", "test_table");
        dropTestViews(defaultDbSupport, "test_view", "`Test_CASE_View`");
        dropTestTriggers(defaultDbSupport, "test_trigger", "`Test_CASE_Trigger`");
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
        dropTestTables(defaultDbSupport, "test_table", "\"Test_CASE_Table\"");
        dropTestViews(defaultDbSupport, "test_view", "\"Test_CASE_View\"");
        dropTestSynonyms(defaultDbSupport, "test_synonym", "\"Test_CASE_Synonym\"");
        dropTestSequences(defaultDbSupport, "test_sequence", "\"Test_CASE_Sequence\"");
        dropTestTriggers(defaultDbSupport, "test_trigger", "\"Test_CASE_Trigger\"");
        dropTestTypes(defaultDbSupport, "test_type", "\"Test_CASE_Type\"");
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
        dropTestTables(defaultDbSupport, "test_table", "\"Test_CASE_Table\"");
        dropTestViews(defaultDbSupport, "test_view", "\"Test_CASE_View\"");
        dropTestSequences(defaultDbSupport, "test_sequence", "\"Test_CASE_Sequence\"");
        dropTestTriggers(defaultDbSupport, "test_trigger ON \"Test_CASE_Sequence\"", "\"Test_CASE_Trigger\" ON \"Test_CASE_Sequence\"");
        dropTestTypes(defaultDbSupport, "test_type", "\"Test_CASE_Type\"");
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
        dropTestTables(defaultDbSupport, "test_table", "\"Test_CASE_Table\"");
        dropTestViews(defaultDbSupport, "test_view", "\"Test_CASE_View\"");
        dropTestSequences(defaultDbSupport, "test_sequence", "\"Test_CASE_Sequence\"");
        dropTestTriggers(defaultDbSupport, "test_trigger", "\"Test_CASE_Trigger\"");
        dropTestTypes(defaultDbSupport, "test_type", "\"Test_CASE_Type\"");
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
        dropTestSynonyms(defaultDbSupport, "test_synonym", "\"Test_CASE_Synonym\"");
        dropTestViews(defaultDbSupport, "test_view", "\"Test_CASE_View\"");
        dropTestTriggers(defaultDbSupport, "test_trigger", "\"Test_CASE_Trigger\"");
        dropTestTables(defaultDbSupport, "\"Test_CASE_Table\"", "test_table");
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
        dropTestSynonyms(defaultDbSupport, "test_synonym", "\"Test_CASE_Synonym\"");
        dropTestViews(defaultDbSupport, "test_view", "\"Test_CASE_View\"");
        dropTestTriggers(defaultDbSupport, "test_trigger", "\"Test_CASE_Trigger\"");
        dropTestTables(defaultDbSupport, "\"Test_CASE_Table\"", "test_table");
        dropTestTypes(defaultDbSupport, "test_type", "\"Test_CASE_Type\"");
    }
}
