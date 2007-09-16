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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hsqldb.Trigger;
import org.unitils.UnitilsJUnit3;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.UnitilsException;
import static org.unitils.core.dbsupport.DbSupportFactory.getDefaultDbSupport;
import static org.unitils.core.util.SQLTestUtils.*;
import static org.unitils.database.SQLUnitils.executeUpdate;
import static org.unitils.database.SQLUnitils.getItemAsLong;
import org.unitils.database.annotations.TestDataSource;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenEquals;

import javax.sql.DataSource;
import java.util.Arrays;
import static java.util.Arrays.asList;
import java.util.Properties;
import java.util.Set;

/**
 * Tests for the db support class. Each type of database has to provide a subclass in which it sets-up the
 * database structure during the test setup and cleans the test structure afterwards.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 * @author Scott Prater
 */
public class DbSupportTest extends UnitilsJUnit3 {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DbSupportTest.class);

    /* DataSource for the test database, is injected */
    @TestDataSource
    protected DataSource dataSource = null;

    /* Instance under test */
    protected DbSupport dbSupport;


    /**
     * Sets up the test fixture.
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // todo multiple schema names
        Properties configuration = new ConfigurationLoader().loadConfiguration();
        SQLHandler sqlHandler = new SQLHandler(dataSource);
        dbSupport = getDefaultDbSupport(configuration, sqlHandler);

        cleanupTestDatabase();
        createTestDatabase();
    }


    /**
     * Removes all test tables.
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        cleanupTestDatabase();
    }


    /**
     * Tests getting the table names.
     */
    public void testGetTableNames() throws Exception {
        Set<String> result = dbSupport.getTableNames();
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
    public void testGetColumnNames() throws Exception {
        Set<String> result = dbSupport.getColumnNames(dbSupport.toCorrectCaseIdentifier("test_table"));
        assertLenEquals(asList(dbSupport.toCorrectCaseIdentifier("col1"), dbSupport.toCorrectCaseIdentifier("col2")), result);
    }


    /**
     * Tests getting the primary column names.
     */
    public void testGetPrimaryKeyColumnNames() throws Exception {
        Set<String> result = dbSupport.getPrimaryKeyColumnNames(dbSupport.toCorrectCaseIdentifier("test_table"));
        assertLenEquals(asList(dbSupport.toCorrectCaseIdentifier("col1")), result);
    }


    /**
     * Tests getting the not null column names.
     */
    public void testGetNotNullColummnNames() throws Exception {
        Set<String> result = dbSupport.getNotNullColummnNames(dbSupport.toCorrectCaseIdentifier("test_table"));
        assertLenEquals(Arrays.asList(dbSupport.toCorrectCaseIdentifier("col1"), dbSupport.toCorrectCaseIdentifier("col2")), result);
    }


    /**
     * Tests getting the table names but no tables in db.
     */
    public void testGetTableNames_noFound() throws Exception {
        cleanupTestDatabase();

        Set<String> result = dbSupport.getTableNames();
        assertTrue(result.isEmpty());
    }


    /**
     * Tests getting the view names.
     */
    public void testGetViewNames() throws Exception {
        Set<String> result = dbSupport.getViewNames();
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
    public void testGetViewNames_noFound() throws Exception {
        cleanupTestDatabase();
        Set<String> result = dbSupport.getViewNames();
        assertTrue(result.isEmpty());
    }


    /**
     * Tests getting the synonym names.
     */
    public void testGetSynonymNames() throws Exception {
        if (!dbSupport.supportsSynonyms()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        Set<String> result = dbSupport.getSynonymNames();
        assertLenEquals(asList(dbSupport.toCorrectCaseIdentifier("TEST_SYNONYM"), "Test_CASE_Synonym"), result);
    }


    /**
     * Tests getting the synonym names but no synonym in db.
     */
    public void testGetSynonymNames_noFound() throws Exception {
        if (!dbSupport.supportsSynonyms()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        cleanupTestDatabase();
        Set<String> result = dbSupport.getSynonymNames();
        assertTrue(result.isEmpty());
    }


    /**
     * Tests getting the sequence names.
     */
    public void testGetSequenceNames() throws Exception {
        if (!dbSupport.supportsSequences()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        Set<String> result = dbSupport.getSequenceNames();
        assertLenEquals(asList(dbSupport.toCorrectCaseIdentifier("TEST_SEQUENCE"), "Test_CASE_Sequence"), result);
    }


    /**
     * Tests getting the sequence names but no sequences in db.
     */
    public void testGetSequenceNames_noFound() throws Exception {
        if (!dbSupport.supportsSequences()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        cleanupTestDatabase();
        Set<String> result = dbSupport.getSequenceNames();
        assertTrue(result.isEmpty());
    }


    /**
     * Tests getting the trigger names.
     */
    public void testGetTriggerNames() throws Exception {
        if (!dbSupport.supportsTriggers()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        Set<String> result = dbSupport.getTriggerNames();
        if ("mysql".equals(dbSupport.getDatabaseDialect())) {
            //MySQL trigger behavior: trigger names are case-sensitive
            assertLenEquals(Arrays.asList("test_trigger", "Test_CASE_Trigger"), result);
        } else {
            assertLenEquals(asList(dbSupport.toCorrectCaseIdentifier("test_trigger"), "Test_CASE_Trigger"), result);
        }
    }


    /**
     * Tests getting the trigger names but no triggers in db.
     */
    public void testGetTriggerNames_noFound() throws Exception {
        if (!dbSupport.supportsTriggers()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        cleanupTestDatabase();
        Set<String> result = dbSupport.getTriggerNames();
        assertTrue(result.isEmpty());
    }


    /**
     * Tests getting the user-defined type names.
     */
    public void testGetTypeNames() throws Exception {
        if (!dbSupport.supportsTypes()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        Set<String> result = dbSupport.getTypeNames();
        assertLenEquals(asList(dbSupport.toCorrectCaseIdentifier("TEST_TYPE"), "Test_CASE_Type"), result);
    }


    /**
     * Tests getting the user-defined types names but no user-defined types in db.
     */
    public void testGetTypeNames_noFound() throws Exception {
        if (!dbSupport.supportsTypes()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        cleanupTestDatabase();
        Set<String> result = dbSupport.getTypeNames();
        assertTrue(result.isEmpty());
    }


    /**
     * Tests incrementing the current value of the primary key.
     */
    public void testIncrementIdentityColumnToValue() throws Exception {
        if (!dbSupport.supportsIdentityColumns()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }

        dbSupport.incrementIdentityColumnToValue(dbSupport.toCorrectCaseIdentifier("TEST_TABLE"), "col1", 30);
        executeUpdate("insert into test_table (col2) values ('xxxx')", dataSource);

        long result = getItemAsLong("select col1 from test_table", dataSource);
        assertEquals(30, result);
    }


    /**
     * Tests dropping a table.
     */
    public void testDropTable() throws Exception {
        //Drop cascade does not work in MySQL and Derby. Therefore we first need to
        // drop the views, next 'Test_CASE_Table' and then test_table.
        if ("mysql".equals(dbSupport.getDatabaseDialect()) || "derby".equals(dbSupport.getDatabaseDialect())) {
            dbSupport.dropView("Test_CASE_View");
            dbSupport.dropView(dbSupport.toCorrectCaseIdentifier("test_view"));
            dbSupport.dropTable("Test_CASE_Table");
            dbSupport.dropTable(dbSupport.toCorrectCaseIdentifier("test_table"));
        } else {
            Set<String> tableNames = dbSupport.getTableNames();
            for (String tableName : tableNames) {
                dbSupport.dropTable(tableName);
            }
        }
        Set<String> result = dbSupport.getTableNames();
        assertTrue(result.isEmpty());
    }


    /**
     * Tests dropping a view.
     */
    public void testDropView() throws Exception {
        Set<String> viewNames = dbSupport.getViewNames();
        for (String viewName : viewNames) {
            dbSupport.dropView(viewName);
        }
        Set<String> result = dbSupport.getViewNames();
        assertTrue(result.isEmpty());
    }


    /**
     * Tests dropping a type.
     */
    public void testDropType() throws Exception {
        if (!dbSupport.supportsTypes()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }

        Set<String> typeNames = dbSupport.getTypeNames();
        for (String typeName : typeNames) {
            dbSupport.dropType(typeName);
        }
        Set<String> result = dbSupport.getTypeNames();
        assertTrue(result.isEmpty());
    }


    /**
     * Tests incrementing and getting the current sequence value.
     */
    public void testGetCurrentValueOfSequence() throws Exception {
        if (!dbSupport.supportsSequences()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }

        dbSupport.incrementSequenceToValue(dbSupport.toCorrectCaseIdentifier("TEST_SEQUENCE"), 30);
        long result = dbSupport.getSequenceValue(dbSupport.toCorrectCaseIdentifier("TEST_SEQUENCE"));
        assertEquals(30, result);
    }


    /**
     * Tests getting all foreign key constraints for the test table.
     */
    public void testGetTableConstraintNames_foreignKey() throws Exception {
        Set<String> result = dbSupport.getForeignKeyConstraintNames("Test_CASE_Table");
        assertEquals(1, result.size());
    }


    /**
     * Tests disabling a foreign key constraint on the test table.
     */
    public void testRemoveForeignKeyConstraint() throws Exception {
        Set<String> constraintNames = dbSupport.getForeignKeyConstraintNames("Test_CASE_Table");
        for (String constraintName : constraintNames) {
            dbSupport.removeForeignKeyConstraint("Test_CASE_Table", constraintName);
        }
        Set<String> result = dbSupport.getForeignKeyConstraintNames("Test_CASE_Table");
        assertTrue(result.isEmpty());
    }


    /**
     * Tests disabling not null constraints on the test table.
     */
    public void testRemoveNotNullConstraint() throws Exception {
        dbSupport.removeNotNullConstraint(dbSupport.toCorrectCaseIdentifier("TEST_TABLE"), dbSupport.toCorrectCaseIdentifier("col2"));

        // should succeed now
        executeUpdate("insert into test_table (col1, col2) values (1, null)", dataSource);
    }


    /**
     * Tests disabling not null constraints on the test table but with an unexisting column
     */
    public void testRemoveNotNullConstraint_columnNotFound() throws Exception {
        try {
            dbSupport.removeNotNullConstraint(dbSupport.toCorrectCaseIdentifier("TEST_TABLE"), "xxxx");
            fail("UnitilsException expected.");
        } catch (UnitilsException e) {
            // expected
        }
    }


    /**
     * Tests disabling not null constraints on the test table but with an unexisting table name.
     */
    public void testRemoveNotNullConstraint_tableNotFound() throws Exception {
        try {
            dbSupport.removeNotNullConstraint("xxxx", "col2");
            fail("UnitilsException expected.");
        } catch (UnitilsException e) {
            // expected
        }
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
     * Creates all test database structures (view, tables...)
     * <p/>
     * NO FOREIGN KEY USED: drop cascade does not work in MySQL
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
        dropTestTriggers(dbSupport, "test_trigger", "\"Test_CASE_Trigger\"");
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
        executeUpdate("create table \"TEST_TABLE\" (col1 int not null primary key generated by default as identity, col2 varchar(12) not null)", dataSource);
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
     * Drops all created test database structures (views, tables...)
     * First drop the views, since Derby doesn't support "drop table ... cascade" (yet, as of Derby 10.3)
     */
    private void cleanupTestDatabaseDerby() throws Exception {
        dropTestSynonyms(dbSupport, "test_synonym", "\"Test_CASE_Synonym\"");
        dropTestViews(dbSupport, "test_view", "\"Test_CASE_View\"");
        dropTestTriggers(dbSupport, "test_trigger", "\"Test_CASE_Trigger\"");
        dropTestTables(dbSupport, "\"Test_CASE_Table\"", "test_table");
    }
}
