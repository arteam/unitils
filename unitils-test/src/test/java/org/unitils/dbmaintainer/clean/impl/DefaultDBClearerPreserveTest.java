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
import org.unitils.database.annotations.TestDataSource;
import org.unitils.database.datasource.DataSourceFactory;
import org.unitils.database.datasource.impl.DefaultDataSourceFactory;
import org.unitils.database.manager.DbMaintainManager;

import javax.sql.DataSource;
import java.util.Properties;

import static org.dbmaintain.config.DbMaintainProperties.*;
import static org.junit.Assert.assertEquals;
import static org.unitils.database.DatabaseUnitils.getDefaultDatabase;
import static org.unitils.database.SQLUnitils.executeUpdate;
import static org.unitils.database.SQLUnitils.executeUpdateQuietly;
import static org.unitils.testutil.TestUnitilsConfiguration.getUnitilsConfiguration;

/**
 * Test class for clearing the database with configuration to preserve all items.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 * @author Scott Prater
 */
public class DefaultDBClearerPreserveTest extends UnitilsJUnit4 {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DefaultDBClearerPreserveTest.class);

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

        defaultDatabase = getDefaultDatabase();
        configuration.setProperty(PROPERTY_AUTO_CREATE_DBMAINTAIN_SCRIPTS_TABLE, "true");
        configuration.setProperty(PROPERTY_PRESERVE_TABLES, "Test_Table, " + defaultDatabase.quoted("Test_CASE_Table"));
        configuration.setProperty(PROPERTY_PRESERVE_VIEWS, "Test_View, " + defaultDatabase.quoted("Test_CASE_View"));
        if (defaultDatabase.supportsMaterializedViews()) {
            configuration.setProperty(PROPERTY_PRESERVE_MATERIALIZED_VIEWS, "Test_MView, " + defaultDatabase.quoted("Test_CASE_MView"));
        }
        if (defaultDatabase.supportsSequences()) {
            configuration.setProperty(PROPERTY_PRESERVE_SEQUENCES, "Test_Sequence, " + defaultDatabase.quoted("Test_CASE_Sequence"));
        }
        if (defaultDatabase.supportsSynonyms()) {
            configuration.setProperty(PROPERTY_PRESERVE_SYNONYMS, "Test_Synonym, " + defaultDatabase.quoted("Test_CASE_Synonym"));
        }

        DataSourceFactory dataSourceFactory = new DefaultDataSourceFactory();
        dataSourceFactory.init(configuration);
        DbMaintainManager dbMaintainManager = new DbMaintainManager(configuration, false, dataSourceFactory);
        defaultDatabase = dbMaintainManager.getDatabase(null);

        dbClearer = dbMaintainManager.getDbMaintainMainFactory().createDBClearer();
    }

    @After
    public void cleanUp() throws Exception {
        cleanupTestDatabase();
    }


    @Test
    public void preserveTables() throws Exception {
        assertEquals(2, defaultDatabase.getTableNames().size());
        dbClearer.clearDatabase();
        assertEquals(3, defaultDatabase.getTableNames().size()); // executed scripts table was created
    }

    @Test
    public void preserveViews() throws Exception {
        assertEquals(2, defaultDatabase.getViewNames().size());
        dbClearer.clearDatabase();
        assertEquals(2, defaultDatabase.getViewNames().size());
    }

    @Test
    public void preserveSequences() throws Exception {
        assertEquals(2, defaultDatabase.getSequenceNames().size());
        dbClearer.clearDatabase();
        assertEquals(2, defaultDatabase.getSequenceNames().size());
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
        executeUpdate("create trigger test_trigger before insert on \"Test_CASE_Table\" call \"org.unitils.core.database.HsqldbDbSupportTest.TestTrigger\"", dataSource);
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

    /**
     * Test trigger for hypersonic.
     *
     * @author Filip Neven
     * @author Tim Ducheyne
     */
    @SuppressWarnings({"UnusedDeclaration"})
    public static class TestTrigger implements Trigger {

        public void fire(int i, String string, String string1, Object[] objects, Object[] objects1) {
        }
    }
}
