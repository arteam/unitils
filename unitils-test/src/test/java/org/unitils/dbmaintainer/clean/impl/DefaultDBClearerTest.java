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

import javax.sql.DataSource;
import java.util.Properties;

import static org.dbmaintain.config.DbMaintainProperties.PROPERTY_AUTO_CREATE_DBMAINTAIN_SCRIPTS_TABLE;
import static org.dbmaintain.config.DbMaintainProperties.PROPERTY_EXECUTED_SCRIPTS_TABLE_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.unitils.database.SQLUnitils.executeUpdate;
import static org.unitils.database.SQLUnitils.executeUpdateQuietly;
import static org.unitils.testutil.TestUnitilsConfiguration.getUnitilsConfiguration;

/**
 * Test class for the clearing the database.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Scott Prater
 */
public class DefaultDBClearerTest extends UnitilsJUnit4 {

    private DBClearer dbClearer;

    @TestDataSource
    protected DataSource dataSource;
    protected Database defaultDatabase;
    protected String versionTableName;


    @Before
    public void initialize() throws Exception {
        Properties configuration = getUnitilsConfiguration();
        versionTableName = configuration.getProperty(PROPERTY_EXECUTED_SCRIPTS_TABLE_NAME);

        cleanupTestDatabase();
        createTestDatabase();

        configuration.setProperty(PROPERTY_AUTO_CREATE_DBMAINTAIN_SCRIPTS_TABLE, "true");

        DbMaintainManager dbMaintainManager = new DbMaintainManager(configuration, false, new TestDataSourceFactory(), new UnitilsTransactionManager());
        defaultDatabase = dbMaintainManager.getDatabase(null);

        dbClearer = dbMaintainManager.getDbMaintainMainFactory().createDBClearer();
    }

    @After
    public void cleanUp() throws Exception {
        cleanupTestDatabase();
    }


    @Test
    public void clearTables() throws Exception {
        assertEquals(2, defaultDatabase.getTableNames().size());
        dbClearer.clearDatabase();
        assertEquals(1, defaultDatabase.getTableNames().size()); // version table was created
    }

    @Test
    public void versionTableAutoCreated() throws Exception {
        dbClearer.clearDatabase();
        assertTrue(defaultDatabase.getTableNames().contains(defaultDatabase.toCorrectCaseIdentifier(versionTableName)));
    }

    @Test
    public void doNotClearVersionTable() throws Exception {
        executeUpdate("create table " + versionTableName + "(testcolumn varchar(10))", dataSource);
        assertEquals(3, defaultDatabase.getTableNames().size());
        dbClearer.clearDatabase();
        assertEquals(1, defaultDatabase.getTableNames().size()); // version table
    }

    @Test
    public void clearViews() throws Exception {
        assertEquals(2, defaultDatabase.getViewNames().size());
        dbClearer.clearDatabase();
        assertTrue(defaultDatabase.getViewNames().isEmpty());
    }

    @Test
    public void clearSequences() throws Exception {
        assertEquals(2, defaultDatabase.getSequenceNames().size());
        dbClearer.clearDatabase();
        assertTrue(defaultDatabase.getSequenceNames().isEmpty());
    }


    private void createTestDatabase() throws Exception {
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

    private void cleanupTestDatabase() throws Exception {
        executeUpdateQuietly("drop table " + versionTableName, dataSource);
        executeUpdateQuietly("drop table test_table", dataSource);
        executeUpdateQuietly("drop table \"Test_CASE_Table\"", dataSource);
        executeUpdateQuietly("drop view test_view", dataSource);
        executeUpdateQuietly("drop view \"Test_CASE_View\"", dataSource);
        executeUpdateQuietly("drop sequence test_sequence", dataSource);
        executeUpdateQuietly("drop sequence \"Test_CASE_Sequence\"", dataSource);
        executeUpdateQuietly("drop trigger test_trigger", dataSource);
        executeUpdateQuietly("drop trigger \"Test_CASE_Trigger\"", dataSource);
    }


    @SuppressWarnings({"UnusedDeclaration"})
    public static class TestTrigger implements Trigger {

        public void fire(int i, String string, String string1, Object[] objects, Object[] objects1) {
        }
    }
}