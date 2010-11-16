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
import org.hsqldb.Trigger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.util.Properties;

import static org.dbmaintain.config.DbMaintainProperties.PROPERTY_AUTO_CREATE_DBMAINTAIN_SCRIPTS_TABLE;
import static org.dbmaintain.config.DbMaintainProperties.PROPERTY_EXECUTED_SCRIPTS_TABLE_NAME;
import static org.junit.Assert.*;
import static org.unitils.core.util.SQLTestUtils.*;
import static org.unitils.database.DatabaseUnitils.getDefaultDatabase;
import static org.unitils.database.DbMaintainUnitils.clearDatabase;
import static org.unitils.database.SQLUnitils.executeUpdate;
import static org.unitils.testutil.TestUnitilsConfiguration.*;

/**
 * Test class for the clearing the database.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Scott Prater
 */
public class DefaultDBClearerTest {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DefaultDBClearerTest.class);

    private DataSource dataSource;
    private Database defaultDatabase;
    private String versionTableName;


    @Before
    public void initialize() throws Exception {
        Properties configuration = getUnitilsConfiguration();
        versionTableName = configuration.getProperty(PROPERTY_EXECUTED_SCRIPTS_TABLE_NAME);

        configuration.setProperty(PROPERTY_AUTO_CREATE_DBMAINTAIN_SCRIPTS_TABLE, "true");
        reinitializeUnitils(configuration);

        defaultDatabase = getDefaultDatabase();
        dataSource = defaultDatabase.getDataSource();

        cleanupTestDatabase();
        createTestDatabase();
    }

    @After
    public void cleanUp() throws Exception {
        resetUnitils();
        cleanupTestDatabase();
    }


    @Test
    public void clearTables() throws Exception {
        assertEquals(2, defaultDatabase.getTableNames().size());
        clearDatabase();
        assertEquals(1, defaultDatabase.getTableNames().size()); // version table was created
    }

    @Test
    public void versionTableAutoCreated() throws Exception {
        clearDatabase();
        assertTrue(defaultDatabase.getTableNames().contains(defaultDatabase.toCorrectCaseIdentifier(versionTableName)));
    }

    @Test
    public void doNotClearVersionTable() throws Exception {
        executeUpdate("create table " + versionTableName + "(testcolumn varchar(10))", dataSource);
        assertEquals(3, defaultDatabase.getTableNames().size());
        clearDatabase();
        assertEquals(1, defaultDatabase.getTableNames().size()); // version table
    }

    @Test
    public void clearViews() throws Exception {
        assertEquals(2, defaultDatabase.getViewNames().size());
        clearDatabase();
        assertTrue(defaultDatabase.getViewNames().isEmpty());
    }

    @Test
    public void clearMaterializedViews() throws Exception {
        if (!defaultDatabase.supportsMaterializedViews()) {
            logger.warn("Current dialect does not support materialized views. Skipping test.");
            return;
        }
        assertEquals(2, defaultDatabase.getMaterializedViewNames().size());
        clearDatabase();
        assertTrue(defaultDatabase.getMaterializedViewNames().isEmpty());
    }

    @Test
    public void clearSynonyms() throws Exception {
        if (!defaultDatabase.supportsSynonyms()) {
            logger.warn("Current dialect does not support synonyms. Skipping test.");
            return;
        }
        assertEquals(2, defaultDatabase.getSynonymNames().size());
        clearDatabase();
        assertTrue(defaultDatabase.getSynonymNames().isEmpty());
    }

    @Test
    public void clearSequences() throws Exception {
        if (!defaultDatabase.supportsSequences()) {
            logger.warn("Current dialect does not support sequences. Skipping test.");
            return;
        }
        assertEquals(2, defaultDatabase.getSequenceNames().size());
        clearDatabase();
        assertTrue(defaultDatabase.getSequenceNames().isEmpty());
    }


    private void createTestDatabase() throws Exception {
        String dialect = defaultDatabase.getDatabaseInfo().getDialect();
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

    private void cleanupTestDatabase() throws Exception {
        String dialect = defaultDatabase.getDatabaseInfo().getDialect();
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

    private void cleanupTestDatabaseHsqlDb() throws Exception {
        dropTestTables(defaultDatabase, "test_table", "\"Test_CASE_Table\"", versionTableName);
        dropTestViews(defaultDatabase, "test_view", "\"Test_CASE_View\"");
        dropTestSequences(defaultDatabase, "test_sequence", "\"Test_CASE_Sequence\"");
        dropTestTriggers(defaultDatabase, "test_trigger", "\"Test_CASE_Trigger\"");
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
     * NO FOREIGN KEY USED: drop cascade does not work in MySQL
     */
    private void createTestDatabaseMySql() throws Exception {
        // create tables
        executeUpdate("create table test_table (col1 int not null primary key AUTO_INCREMENT, col2 varchar(12) not null)", dataSource);
        executeUpdate("create table `Test_CASE_Table` (col1 int)", dataSource);
        // create views
        executeUpdate("create view test_view as select col1 from test_table", dataSource);
        executeUpdate("create view `Test_CASE_View` as select col1 from `Test_CASE_Table`", dataSource);
        // create triggers
        executeUpdate("create trigger test_trigger before insert on `Test_CASE_Table` FOR EACH ROW begin end", dataSource);
        executeUpdate("create trigger `Test_CASE_Trigger` after insert on `Test_CASE_Table` FOR EACH ROW begin end", dataSource);
    }

    private void cleanupTestDatabaseMySql() throws Exception {
        dropTestTables(defaultDatabase, "test_table", "`Test_CASE_Table`", versionTableName);
        dropTestViews(defaultDatabase, "test_view", "`Test_CASE_View`");
        dropTestTriggers(defaultDatabase, "test_trigger", "`Test_CASE_Trigger`");
    }

    //
    // Database setup for Oracle
    //

    private void createTestDatabaseOracle() throws Exception {
        // create tables
        executeUpdate("create table test_table (col1 varchar(10) not null primary key, col2 varchar(12) not null)", dataSource);
        executeUpdate("create table \"Test_CASE_Table\" (col1 varchar(10), foreign key (col1) references test_table(col1))", dataSource);
        // create views
        executeUpdate("create view test_view as select col1 from test_table", dataSource);
        executeUpdate("create view \"Test_CASE_View\" as select col1 from \"Test_CASE_Table\"", dataSource);
        // create materialized views
        executeUpdate("create materialized view test_mview as select col1 from test_table", dataSource);
        executeUpdate("create materialized view \"Test_CASE_MView\" as select col1 from test_table", dataSource);
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

    private void cleanupTestDatabaseOracle() throws Exception {
        dropTestTables(defaultDatabase, "test_table", "\"Test_CASE_Table\"", versionTableName);
        dropTestViews(defaultDatabase, "test_view", "\"Test_CASE_View\"");
        dropTestMaterializedViews(defaultDatabase, "test_mview", "\"Test_CASE_MView\"");
        dropTestSynonyms(defaultDatabase, "test_synonym", "\"Test_CASE_Synonym\"");
        dropTestSequences(defaultDatabase, "test_sequence", "\"Test_CASE_Sequence\"");
        dropTestTriggers(defaultDatabase, "test_trigger", "\"Test_CASE_Trigger\"");
        dropTestTypes(defaultDatabase, "test_type", "\"Test_CASE_Type\"");
    }

    //
    // Database setup for PostgreSql
    //

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

    private void cleanupTestDatabasePostgreSql() throws Exception {
        dropTestTables(defaultDatabase, "test_table", "\"Test_CASE_Table\"", versionTableName);
        dropTestViews(defaultDatabase, "test_view", "\"Test_CASE_View\"");
        dropTestSequences(defaultDatabase, "test_sequence", "\"Test_CASE_Sequence\"");
        dropTestTriggers(defaultDatabase, "test_trigger", "\"Test_CASE_Trigger\"");
        dropTestTypes(defaultDatabase, "test_type", "\"Test_CASE_Type\"");
    }

    //
    // Database setup for Db2
    //

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

    private void cleanupTestDatabaseDb2() throws Exception {
        dropTestTables(defaultDatabase, "test_table", "\"Test_CASE_Table\"", versionTableName);
        dropTestViews(defaultDatabase, "test_view", "\"Test_CASE_View\"");
        dropTestSynonyms(defaultDatabase, "test_synonym", "\"Test_CASE_Synonym\"");
        dropTestSequences(defaultDatabase, "test_sequence", "\"Test_CASE_Sequence\"");
        dropTestTriggers(defaultDatabase, "test_trigger", "\"Test_CASE_Trigger\"");
        dropTestTypes(defaultDatabase, "test_type", "\"Test_CASE_Type\"");
    }

    //
    // Database setup for Derby
    //

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
     * First drop the views, since Derby doesn't support "drop table ... cascade" (yet, as of Derby 10.3)
     */
    private void cleanupTestDatabaseDerby() throws Exception {
        dropTestSynonyms(defaultDatabase, "test_synonym", "\"Test_CASE_Synonym\"");
        dropTestViews(defaultDatabase, "test_view", "\"Test_CASE_View\"");
        dropTestTriggers(defaultDatabase, "test_trigger", "\"Test_CASE_Trigger\"");
        dropTestTables(defaultDatabase, "\"Test_CASE_Table\"", "TEST_TABLE", versionTableName);
    }

    //
    // Database setup for MS-Sql
    //

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

    private void cleanupTestDatabaseMsSql() throws Exception {
        dropTestSynonyms(defaultDatabase, "test_synonym", "\"Test_CASE_Synonym\"");
        dropTestViews(defaultDatabase, "test_view", "\"Test_CASE_View\"");
        dropTestTriggers(defaultDatabase, "test_trigger", "\"Test_CASE_Trigger\"");
        dropTestTables(defaultDatabase, "\"Test_CASE_Table\"", "test_table", versionTableName);
        dropTestTypes(defaultDatabase, "test_type", "\"Test_CASE_Type\"");
    }
}