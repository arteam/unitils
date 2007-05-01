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

import org.unitils.UnitilsJUnit3;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.dbsupport.DbSupport;
import org.unitils.core.dbsupport.DbSupportFactory;
import static org.unitils.core.dbsupport.DbSupportFactory.PROPKEY_DATABASE_SCHEMA_NAMES;
import static org.unitils.core.dbsupport.TestSQLUtils.executeUpdateQuietly;
import static org.unitils.core.dbsupport.TestSQLUtils.isEmpty;
import static org.unitils.core.util.SQLUtils.executeUpdate;
import org.unitils.database.annotations.TestDataSource;
import static org.unitils.dbmaintainer.clean.impl.DefaultDBCleaner.PROPKEY_TABLESTOPRESERVE;
import org.unitils.dbmaintainer.util.DatabaseModuleConfigUtils;
import static org.unitils.dbmaintainer.util.DatabaseModuleConfigUtils.PROPKEY_DATABASE_DIALECT;
import org.unitils.util.PropertyUtils;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Test class for the DBCleaner with multiple schemas.
 * <p/>
 * Currently this is only implemented for HsqlDb.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DBCleanerMultiSchemaTest extends UnitilsJUnit3 {

    /* DataSource for the test database, is injected */
    @TestDataSource
    private DataSource dataSource = null;

    /* Tested object */
    private DBCleaner dbCleaner;

    /* The DbSupport object */
    private DbSupport dbSupport;

    /* True if current test is not for the current dialect */
    private boolean disabled;


    /**
     * Initializes the test fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();

        Properties configuration = new ConfigurationLoader().loadConfiguration();
        this.disabled = !"hsqldb".equals(PropertyUtils.getString(PROPKEY_DATABASE_DIALECT, configuration));
        if (disabled) {
            return;
        }

        // configure 3 schemas
        configuration.setProperty(PROPKEY_DATABASE_SCHEMA_NAMES, "PUBLIC, SCHEMA_A, SCHEMA_B");
        dbSupport = DbSupportFactory.getDefaultDbSupport(configuration, dataSource);

        //todo implement
        // case sensitive and insensitive names
        String itemsToPreserve = "Test_table_Preserve, " + dbSupport.quoted("Test_CASE_Table_Preserve");
        configuration.setProperty(PROPKEY_TABLESTOPRESERVE, itemsToPreserve);
        // create cleaner instance
        dbCleaner = DatabaseModuleConfigUtils.getConfiguredDatabaseTaskInstance(DBCleaner.class, configuration, dataSource);

        dropTestTables();
        createTestTables();
    }


    /**
     * Removes the test database tables from the test database, to avoid inference with other tests
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        dropTestTables();
    }


    /**
     * Tests if the tables in all schemas are correctly cleaned.
     */
    public void testCleanDatabase() throws Exception {
        assertFalse(isEmpty("TEST", dataSource));
        assertFalse(isEmpty("SCHEMA_A.TEST", dataSource));
        assertFalse(isEmpty("SCHEMA_B.TEST", dataSource));
        dbCleaner.cleanSchemas();
        assertTrue(isEmpty("TEST", dataSource));
        assertTrue(isEmpty("SCHEMA_A.TEST", dataSource));
        assertTrue(isEmpty("SCHEMA_B.TEST", dataSource));
    }


    /**
     * Creates the test tables.
     */
    private void createTestTables() throws SQLException {
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


    /**
     * Removes the test database tables
     */
    private void dropTestTables() {
        executeUpdateQuietly("drop table TEST", dataSource);
        executeUpdateQuietly("drop table SCHEMA_A.TEST", dataSource);
        executeUpdateQuietly("drop schema SCHEMA_A", dataSource);
        executeUpdateQuietly("drop table SCHEMA_B.TEST", dataSource);
        executeUpdateQuietly("drop schema SCHEMA_B", dataSource);
    }


}
