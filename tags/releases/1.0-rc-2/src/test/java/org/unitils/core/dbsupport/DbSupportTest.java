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
package org.unitils.core.dbsupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.UnitilsJUnit3;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.UnitilsException;
import org.unitils.core.util.SQLUtils;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.dbmaintainer.DBMaintainer;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenEquals;
import org.unitils.thirdparty.org.apache.commons.dbutils.DbUtils;
import org.unitils.util.PropertyUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;

/**
 * Tests for the db support class. Each type of database has to provide a subclass in which it sets-up the
 * database structure during the test setup and cleans the test structure afterwards.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public abstract class DbSupportTest extends UnitilsJUnit3 {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DbSupportTest.class);

    /* DataSource for the test database, is injected */
    @TestDataSource
    protected DataSource dataSource = null;

    /* Instance under test */
    protected DbSupport dbSupport;

    /* True if current test is not for the current dialect */
    protected boolean disabled;


    /**
     * Creates a new test for the given db support instance
     *
     * @param dbSupport The db support to test, not null
     */
    protected DbSupportTest(DbSupport dbSupport) {
        this.dbSupport = dbSupport;
    }


    /**
     * Sets up the test fixture.
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        Properties configuration = new ConfigurationLoader().loadConfiguration();
        String testDatabaseDialect = dbSupport.getDatabaseDialect();
        String databaseDialect = PropertyUtils.getString(DBMaintainer.PROPKEY_DATABASE_DIALECT, configuration);
        disabled = !testDatabaseDialect.equals(databaseDialect);
        if (disabled) {
            return;
        }
        dbSupport.init(configuration, dataSource);

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
     * Tests getting the table names.
     */
    public void testGetTableNames() throws Exception {
        if (disabled) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        Set<String> result = dbSupport.getTableNames();
        assertLenEquals(Arrays.asList(dbSupport.toCorrectCaseIdentifier("test_table"), "Test_CASE_Table"), result);
    }


    /**
     * Tests getting the table names but no tables in db.
     */
    public void testGetTableNames_noFound() throws Exception {
        if (disabled) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        cleanupTestDatabase();
        Set<String> result = dbSupport.getTableNames();
        assertTrue(result.isEmpty());
    }


    /**
     * Tests getting the view names.
     */
    public void testGetViewNames() throws Exception {
        if (disabled) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        Set<String> result = dbSupport.getViewNames();
        assertLenEquals(Arrays.asList(dbSupport.toCorrectCaseIdentifier("test_view"), "Test_CASE_View"), result);
    }


    /**
     * Tests getting the view names but no views in db.
     */
    public void testGetViewNames_noFound() throws Exception {
        if (disabled) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        cleanupTestDatabase();
        Set<String> result = dbSupport.getViewNames();
        assertTrue(result.isEmpty());
    }


    /**
     * Tests getting the synonym names.
     */
    public void testGetSynonymNames() throws Exception {
        if (disabled || !dbSupport.supportsSynonyms()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        Set<String> result = dbSupport.getSynonymNames();
        assertLenEquals(Arrays.asList(dbSupport.toCorrectCaseIdentifier("TEST_SYNONYM"), "Test_CASE_Synonym"), result);
    }


    /**
     * Tests getting the synonym names but no synonym in db.
     */
    public void testGetSynonymNames_noFound() throws Exception {
        if (disabled || !dbSupport.supportsSynonyms()) {
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
        if (disabled || !dbSupport.supportsSequences()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        Set<String> result = dbSupport.getSequenceNames();
        assertLenEquals(Arrays.asList(dbSupport.toCorrectCaseIdentifier("TEST_SEQUENCE"), "Test_CASE_Sequence"), result);
    }


    /**
     * Tests getting the sequence names but no sequences in db.
     */
    public void testGetSequenceNames_noFound() throws Exception {
        if (disabled || !dbSupport.supportsSequences()) {
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
        if (disabled || !dbSupport.supportsTriggers()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        Set<String> result = dbSupport.getTriggerNames();
        assertLenEquals(Arrays.asList(dbSupport.toCorrectCaseIdentifier("test_trigger"), "Test_CASE_Trigger"), result);
    }


    /**
     * Tests getting the trigger names but no triggers in db.
     */
    public void testGetTriggerNames_noFound() throws Exception {
        if (disabled || !dbSupport.supportsTriggers()) {
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
        if (disabled || !dbSupport.supportsTypes()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        Set<String> result = dbSupport.getTypeNames();
        assertLenEquals(Arrays.asList(dbSupport.toCorrectCaseIdentifier("TEST_TYPE"), "Test_CASE_Type"), result);
    }


    /**
     * Tests getting the user-defined types names but no user-defined types in db.
     */
    public void testGetTypeNames_noFound() throws Exception {
        if (disabled || !dbSupport.supportsTypes()) {
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
        if (disabled || !dbSupport.supportsIdentityColumns()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }

        dbSupport.incrementIdentityColumnToValue(dbSupport.toCorrectCaseIdentifier("TEST_TABLE"), "col1", 30);
        SQLUtils.executeUpdate("insert into test_table (col2) values ('xxxx')", dataSource);

        long result = SQLUtils.getItemAsLong("select col1 from test_table", dataSource);
        assertEquals(30, result);
    }


    /**
     * Tests disabling not null constraints on the test table.
     */
    public void testRemoveNotNullConstraint() throws Exception {
        if (disabled || !dbSupport.supportsRemoveNotNullConstraint()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        dbSupport.removeNotNullConstraint(dbSupport.toCorrectCaseIdentifier("TEST_TABLE"), "col2");

        // should succeed now
        SQLUtils.executeUpdate("insert into test_table (col1, col2) values (1, null)", dataSource);
    }


    /**
     * Tests disabling not null constraints on the test table but with an unexisting column
     */
    public void testRemoveNotNullConstraint_columnNotFound() throws Exception {
        if (disabled || !dbSupport.supportsRemoveNotNullConstraint()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        try {
            dbSupport.removeNotNullConstraint(dbSupport.toCorrectCaseIdentifier("TEST_TABLE"), "xxxx");
            fail("UnitilsException expected.");
        } catch (UnitilsException e) {
            // expected
        }
    }


    /**
     * Tests dropping a table.
     */
    public void testDropTable() throws Exception {
        if (disabled) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }

        Set<String> tableNames = dbSupport.getTableNames();
        for (String tableName : tableNames) {
            dbSupport.dropTable(tableName);
        }
        Set<String> result = dbSupport.getTableNames();
        assertTrue(result.isEmpty());
    }


    /**
     * Tests dropping a view.
     */
    public void testDropView() throws Exception {
        if (disabled) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }

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
        if (disabled || !dbSupport.supportsTypes()) {
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
        if (disabled || !dbSupport.supportsSequences()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }

        dbSupport.incrementSequenceToValue(dbSupport.toCorrectCaseIdentifier("TEST_SEQUENCE"), 30);
        long result = dbSupport.getCurrentValueOfSequence(dbSupport.toCorrectCaseIdentifier("TEST_SEQUENCE"));
        assertEquals(30, result);
    }


    /**
     * Tests getting all not null constraints for the test table.
     */
    public void testGetTableConstraintNames_notNull() throws Exception {
        if (disabled || !dbSupport.supportsGetTableConstraintNames()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        Set<String> result = dbSupport.getTableConstraintNames("TEST_TABLE");
        assertEquals(2, result.size());
    }


    /**
     * Tests getting all foreign key constraints for the test table.
     */
    public void testGetTableConstraintNames_foreignKey() throws Exception {
        if (disabled || !dbSupport.supportsGetTableConstraintNames()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        Set<String> result = dbSupport.getTableConstraintNames("Test_CASE_Table");
        assertEquals(1, result.size());
    }


    /**
     * Tests disabling not null constraints on the test table.
     */
    public void testDisableConstraint_notNull() throws Exception {
        if (disabled || !dbSupport.supportsGetTableConstraintNames()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        Set<String> constraintNames = dbSupport.getTableConstraintNames("TEST_TABLE");
        for (String constraintName : constraintNames) {
            dbSupport.disableConstraint("TEST_TABLE", constraintName);
        }
        Set<String> result = dbSupport.getTableConstraintNames("TEST_TABLE");
        assertTrue(result.isEmpty());
    }


    /**
     * Tests disabling a foreign key constraint on the test table.
     */
    public void testDisableConstraint_foreignKey() throws Exception {
        if (disabled || !dbSupport.supportsGetTableConstraintNames()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        Set<String> constraintNames = dbSupport.getTableConstraintNames("Test_CASE_Table");
        for (String constraintName : constraintNames) {
            dbSupport.disableConstraint("Test_CASE_Table", constraintName);
        }
        Set<String> result = dbSupport.getTableConstraintNames("Test_CASE_Table");
        assertTrue(result.isEmpty());
    }


    /**
     * Tests disabling not null constraints on the test table but with an unexisting table name.
     */
    public void testRemoveNotNullConstraint_tableNotFound() throws Exception {
        if (disabled || !dbSupport.supportsRemoveNotNullConstraint()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        try {
            dbSupport.removeNotNullConstraint("xxxx", "col2");
            fail("UnitilsException expected.");
        } catch (UnitilsException e) {
            // expected
        }
    }


    /**
     * Tests disabling a foreign key constraint on the connection.
     */
    public void testDisableForeignKeyConstraintsCheckingOnConnection() throws Exception {
        if (disabled || !dbSupport.supportsDisableForeignKeyConstraintsCheckingOnConnection()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            dbSupport.disableForeignKeyConstraintsCheckingOnConnection(connection);

        } finally {
            DbUtils.closeQuietly(connection, null, null);
        }

        // should succeed now
        SQLUtils.executeUpdate("insert into `Test_CASE_Table` (col1) values (999)", dataSource);
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
