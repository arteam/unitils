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
import org.unitils.UnitilsJUnit3;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.dbsupport.DbSupport;
import org.unitils.database.annotations.TestDataSource;
import static org.unitils.dbmaintainer.DBMaintainer.PROPKEY_DATABASE_DIALECT;
import org.unitils.dbmaintainer.clean.impl.DefaultDBClearer;
import static org.unitils.dbmaintainer.util.DatabaseModuleConfigUtils.getConfiguredDatabaseTaskInstance;
import static org.unitils.dbmaintainer.util.DatabaseModuleConfigUtils.getConfiguredDbSupportInstance;
import org.unitils.util.PropertyUtils;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Base test class for the DBClearer. Contains tests that can be implemented generally for all different database dialects.
 * Extended with implementations for each supported database dialect.
 * <p/>
 * Tests are only executed for the currently activated database dialect. By default, a hsqldb in-memory database is used,
 * to avoid the need for setting up a database instance. If you want to run unit tests for other dbms's, change the
 * configuration in test/resources/unitils.properties
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
abstract public class DBClearerTest extends UnitilsJUnit3 {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DBClearerTest.class);

    /* DataSource for the test database, is injected */
    @TestDataSource
    protected DataSource dataSource = null;

    /* Tested object */
    protected DBClearer dbClearer;

    /* The DbSupport object */
    protected DbSupport dbSupport;

    /* The database dialect that is tested in this test */
    protected String testDatabaseDialect;

    /* True if current test is not for the current dialect */
    protected boolean disabled;


    /**
     * Creates a new clearer test
     *
     * @param testDatabaseDialect The database dialect that is tested in this test, not null
     */
    public DBClearerTest(String testDatabaseDialect) {
        this.testDatabaseDialect = testDatabaseDialect;
    }


    /**
     * Configures the tested object. Creates a test table, index, view and sequence
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        Properties configuration = new ConfigurationLoader().loadConfiguration();
        this.disabled = !testDatabaseDialect.equals(PropertyUtils.getString(PROPKEY_DATABASE_DIALECT, configuration));
        if (disabled) {
            return;
        }

        dbSupport = getConfiguredDbSupportInstance(configuration, dataSource);

        // case insensitive names
        String itemsToPreserve = "Test_table_Preserve, Test_view_Preserve, Test_sequence_Preserve, Test_trigger_Preserve, ";
        // case sensitive names
        itemsToPreserve += dbSupport.quoted("Test_CASE_Table_Preserve") + ", " + dbSupport.quoted("Test_CASE_View_Preserve") + ", " + dbSupport.quoted("Test_CASE_Sequence_Preserve") + ", " + dbSupport.quoted("Test_CASE_Trigger_Preserve");
        configuration.setProperty(DefaultDBClearer.PROPKEY_ITEMSTOPRESERVE, itemsToPreserve);
        // create clearer instance
        dbClearer = getConfiguredDatabaseTaskInstance(DBClearer.class, configuration, dataSource);

        cleanupTestDatabase();
        createTestDatabase();
    }


    /**
     * Removes all test tables.
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        if (disabled) {
            return;
        }
        cleanupTestDatabase();
    }


    /**
     * Checks if the tables are correctly dropped.
     */
    public void testClearDatabase_tables() throws Exception {
        if (disabled) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        assertFalse(dbSupport.getTableNames().isEmpty());
        dbClearer.clearSchema();
        assertTrue(dbSupport.getTableNames().isEmpty());
    }


    /**
     * Checks if the views are correctly dropped
     */
    public void testClearDatabase_views() throws Exception {
        if (disabled) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        assertFalse(dbSupport.getViewNames().isEmpty());
        dbClearer.clearSchema();
        assertTrue(dbSupport.getViewNames().isEmpty());
    }

    /**
     * Checks if the synonyms are correctly dropped
     */
    public void testClearDatabase_synonyms() throws Exception {
        if (disabled || !dbSupport.supportsSynonyms()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        assertFalse(dbSupport.getSynonymNames().isEmpty());
        dbClearer.clearSchema();
        assertTrue(dbSupport.getSynonymNames().isEmpty());
    }


    /**
     * Tests if the triggers are correctly dropped
     */
    public void testClearDatabase_sequences() throws Exception {
        if (disabled || !dbSupport.supportsSequences()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        assertFalse(dbSupport.getSequenceNames().isEmpty());
        dbClearer.clearSchema();
        assertTrue(dbSupport.getSequenceNames().isEmpty());
    }


    /**
     * Creates all test database structures (view, tables...)
     */
    protected abstract void createTestDatabase() throws Exception;


    /**
     * Drops all created test database structures (views, tables...)
     */
    protected abstract void cleanupTestDatabase() throws Exception;

}