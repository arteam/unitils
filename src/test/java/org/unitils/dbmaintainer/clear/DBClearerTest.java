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
package org.unitils.dbmaintainer.clear;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.dbutils.DbUtils;
import org.unitils.UnitilsJUnit3;
import org.unitils.core.ConfigurationLoader;
import org.unitils.db.annotations.DatabaseTest;
import org.unitils.db.annotations.TestDataSource;
import org.unitils.dbmaintainer.dbsupport.DbSupport;
import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.handler.StatementHandlerException;
import org.unitils.dbmaintainer.util.DatabaseModuleConfigUtils;
import org.unitils.util.ConfigUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Base test class for the DBClearer. Contains tests that can be implemented generally for all different database dialects.
 * Extended with implementations for each supported database dialect.
 * <p/>
 * Tests are only executed for the currently activated database dialect. By default, a hsqldb in-memory database is used,
 * to avoid the need for setting up a database instance. If you want to run unit tests for other dbms's, change the
 * configuration in test/resources/unitils.properties
 */
@DatabaseTest
abstract public class DBClearerTest extends UnitilsJUnit3 {

    /* DataSource for the test database, is injected */
    @TestDataSource
    protected javax.sql.DataSource dataSource;

    /* Tested object */
    protected DBClearer dbClearer;

    /* Test database schema name */
    protected String schemaName;

    /* The Configuration object */
    protected Configuration configuration;

    /* The DbSupport object */
    private DbSupport dbSupport;

    /**
     * Configures the tested object. Creates a test table, index, view and sequence
     *
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        if (isTestedDialectActivated()) {
            super.setUp();

            configuration = new ConfigurationLoader().loadConfiguration();
            configuration.addProperty(DefaultDBClearer.PROPKEY_ITEMSTOPRESERVE, "testtablepreserve,testviewpreserve," +
                    "testsequencepreserve,testtriggerpreserve");

            StatementHandler statementHandler = DatabaseModuleConfigUtils.getConfiguredStatementHandlerInstance(configuration, dataSource);
            dbSupport = DatabaseModuleConfigUtils.getConfiguredDbSupportInstance(configuration, dataSource, statementHandler);
            dbClearer = DatabaseModuleConfigUtils.getConfiguredDatabaseTaskInstance(DBClearer.class,
                    configuration, dataSource, statementHandler);

            dropTestView();
            dropTestTables();
            dropTestSequences();
            dropTestTriggers();
            createTestTables();
            createTestView();
            createTestSequences();
            createTestTriggers();
        }
    }

    protected void tearDown() throws Exception {
        if (isTestedDialectActivated()) {
            super.tearDown();

            dropTestView();
            dropTestTables();
            dropTestSequences();
            dropTestTriggers();
            createTestTables();
            createTestView();
            createTestSequences();
            createTestTriggers();
        }
    }

    /**
     * Checks if the tables are correctly dropped.
     *
     * @throws Exception
     */
    public void testClearDatabase_tables() throws Exception {
        if (isTestedDialectActivated()) {
            assertTrue(dbSupport.tableExists("testtable"));
            assertTrue(dbSupport.tableExists("testtablepreserve"));
            dbClearer.clearDatabase();
            assertFalse(dbSupport.tableExists("testtable"));
            assertTrue(dbSupport.tableExists("testtablepreserve"));
        }
    }

    /**
     * Checks if the views are correctly dropped
     *
     * @throws Exception
     */
    public void testClearDatabase_views() throws Exception {
        if (isTestedDialectActivated()) {
            assertTrue(dbSupport.tableExists("testview"));
            assertTrue(dbSupport.tableExists("testviewpreserve"));
            dbClearer.clearDatabase();
            assertFalse(dbSupport.tableExists("testview"));
            assertTrue(dbSupport.tableExists("testviewpreserve"));
        }
    }

    /**
     * Tests if the triggers are correctly dropped
     *
     * @throws Exception
     */
    public void testClearDatabase_sequences() throws Exception {
        if (isTestedDialectActivated()) {
            assertTrue(dbSupport.sequenceExists("testsequence"));
            assertTrue(dbSupport.sequenceExists("testsequencepreserve"));
            dbClearer.clearDatabase();
            assertFalse(dbSupport.sequenceExists("testsequence"));
            assertTrue(dbSupport.sequenceExists("testsequencepreserve"));
        }
    }

    /**
     * Tests if the triggers are correctly dropped
     *
     * @throws Exception
     */
    public void testClearDatabase_triggers() throws Exception {
        if (isTestedDialectActivated()) {
            assertTrue(dbSupport.triggerExists("testtrigger"));
            assertTrue(dbSupport.triggerExists("testtriggerpreserve"));
            dbClearer.clearDatabase();
            assertFalse(dbSupport.triggerExists("testtrigger"));
            assertTrue(dbSupport.triggerExists("testtriggerpreserve"));
        }
    }

    /**
     * Creates the test tables
     *
     * @throws SQLException
     */
    private void createTestTables() throws SQLException {
        Connection conn = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            st.execute("create table testtable (col1 varchar(10))");
            st.execute("create table testtablepreserve (col1 varchar(10))");
        } finally {
            DbUtils.closeQuietly(conn, st, null);
        }
    }

    /**
     * Drops the test tables
     */
    private void dropTestTables() {
        try {
            dbSupport.dropTable("testtable");
        } catch (StatementHandlerException e) {
            // Ignored
        }
        try {
            dbSupport.dropTable("testtablepreserve");
        } catch (StatementHandlerException e) {
            // Ignored
        }
    }

    /**
     * Creates the test views
     *
     * @throws SQLException
     */
    private void createTestView() throws SQLException {
        Connection conn = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            // Make sure previous setup is cleaned up
            st.execute("create view testview as select col1 from testtablepreserve");
            st.execute("create view testviewpreserve as select col1 from testtablepreserve");
        } finally {
            DbUtils.closeQuietly(conn, st, null);
        }
    }

    /**
     * Drops the test views
     */
    private void dropTestView() {
        try {
            dbSupport.dropView("testview");
        } catch (StatementHandlerException e) {
            // Ignored
        }
        try {
            dbSupport.dropView("testviewpreserve");
        } catch (StatementHandlerException e) {
            // Ignored
        }
    }

    /**
     * Creates the test sequences
     */
    private void createTestSequences() {
        Connection conn = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            // Make sure previous setup is cleaned up
            st.execute("create sequence testsequence");
            st.execute("create sequence testsequencepreserve");
        } catch (SQLException e) {
            // No action is taken
        } finally {
            DbUtils.closeQuietly(conn, st, null);
        }
    }

    /**
     * Drops the test sequence
     */
    private void dropTestSequences() {
        try {
            dbSupport.dropSequence("testsequence");
        } catch (StatementHandlerException e) {
            // Ignored
        }
        try {
            dbSupport.dropSequence("testsequencepreserve");
        } catch (StatementHandlerException e) {
            // Ignored
        }
    }

    private void createTestTriggers() throws SQLException {
        createTestTrigger("testtrigger");
        createTestTrigger("testtriggerpreserve");
    }

    abstract protected void createTestTrigger(String triggerName) throws SQLException;

    private void dropTestTriggers() {
        try {
            dropTestTrigger("testtrigger");
        } catch (SQLException e) {
            // Ignored
        }
        try {
            dropTestTrigger("testtriggerpreserve");
        } catch (SQLException e) {
            // Ignored
        }
    }

    abstract protected void dropTestTrigger(String triggerName) throws SQLException;

    /**
     * Checks whether the database dialect that is tested in the current implementation is the currenlty configured
     * database dialect.
     *
     * @return True if the tested dbms is equal to the configured one, false otherwise
     */
    abstract protected boolean isTestedDialectActivated();
}
