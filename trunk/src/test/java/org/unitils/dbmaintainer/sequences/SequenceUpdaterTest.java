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
package org.unitils.dbmaintainer.sequences;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.dbutils.DbUtils;
import org.unitils.UnitilsJUnit3;
import org.unitils.core.ConfigurationLoader;
import org.unitils.db.annotations.DatabaseTest;
import org.unitils.db.annotations.TestDataSource;
import org.unitils.dbmaintainer.handler.JDBCStatementHandler;
import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.maintainer.DBMaintainer;
import org.unitils.util.ReflectionUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Test class for the SequenceUpdater. Contains tests that can be implemented generally for all different database dialects.
 * Extended with implementations for each supported database dialect.
 * <p/>
 * Tests are only executed for the currently activated database dialect. By default, a hsqldb in-memory database is used,
 * to avoid the need for setting up a database instance. If you want to run unit tests for other dbms's, change the
 * configuration in test/resources/unitils.properties
 */
@DatabaseTest
public abstract class SequenceUpdaterTest extends UnitilsJUnit3 {

    @TestDataSource
    private DataSource dataSource;

    /**
     * Tested object
     */
    private SequenceUpdater sequenceUpdater;

    /**
     * Value that sequences should at least have after updating the sequences
     */
    private static final int LOWEST_ACCEPTACLE_SEQUENCE_VALUE = 1000;

    /**
     * Test fixture. Configures the implementation of the SequenceUpdater that matches the currenlty configured dialect.
     * Creates a test table and test sequence.
     *
     * @throws Exception
     */
    protected void setUp() throws Exception {
        super.setUp();

        Configuration configuration = new ConfigurationLoader().loadConfiguration();
        configuration.setProperty(BaseSequenceUpdater.PROPKEY_LOWEST_ACCEPTABLE_SEQUENCE_VALUE, LOWEST_ACCEPTACLE_SEQUENCE_VALUE);

        String databaseDialect = configuration.getString(DBMaintainer.PROPKEY_DATABASE_DIALECT);
        StatementHandler statementHandler = new JDBCStatementHandler();
        statementHandler.init(configuration, dataSource);
        sequenceUpdater = ReflectionUtils.createInstanceOfType(configuration.getString(DBMaintainer.PROPKEY_SEQUENCEUPDATER_START + "." + databaseDialect));
        sequenceUpdater.init(configuration, dataSource, statementHandler);

        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            createTestTable(conn);
            insertTestRecord(conn);
            createTestSequence(conn);
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    /**
     * Clears the database, to avoid interference with other tests
     *
     * @throws Exception
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            dropTestSequence(conn);
            dropTestTable(conn);
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    /**
     * Inserts a test record
     *
     * @param conn
     */
    private void insertTestRecord(Connection conn) {
        Statement st = null;
        try {
            st = conn.createStatement();
            // Make sure previous setup is cleaned up
            st.execute("insert into testtable values('test')");
        } catch (SQLException e) {
            // No action is taken
        } finally {
            DbUtils.closeQuietly(st);
        }
    }

    /**
     * Creates a test table
     *
     * @param conn
     */
    private void createTestTable(Connection conn) {
        Statement st = null;
        try {
            st = conn.createStatement();
            // Make sure previous setup is cleaned up
            st.execute("create table testtable (test varchar(10))");
        } catch (SQLException e) {
            // No action is taken
        } finally {
            DbUtils.closeQuietly(st);
        }
    }

    /**
     * Drops the test table
     *
     * @param conn
     */
    private void dropTestTable(Connection conn) {
        Statement st = null;
        try {
            st = conn.createStatement();
            // Make sure previous setup is cleaned up
            st.execute("drop table testtable");
        } catch (SQLException e) {
            // No action is taken
        } finally {
            DbUtils.closeQuietly(st);
        }
    }

    /**
     * Creates a test sequence
     *
     * @param conn
     */
    private void createTestSequence(Connection conn) {
        Statement st = null;
        try {
            st = conn.createStatement();
            // Make sure previous setup is cleaned up
            st.execute("create sequence testsequence");
        } catch (SQLException e) {
            // No action is taken
        } finally {
            DbUtils.closeQuietly(st);
        }
    }

    /**
     * Drops the test sequence
     *
     * @param conn
     */
    private void dropTestSequence(Connection conn) {
        Statement st = null;
        try {
            st = conn.createStatement();
            // Make sure previous setup is cleaned up
            st.execute("drop sequence testsequence");
        } catch (SQLException e) {
            // No action is taken
        } finally {
            DbUtils.closeQuietly(st);
        }
    }

    /**
     * Tests the update sequences behavior
     *
     * @throws Exception
     */
    public void testUpdateSequences() throws Exception {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            assertTrue(getNextSequenceValue(conn) < LOWEST_ACCEPTACLE_SEQUENCE_VALUE);
            sequenceUpdater.updateSequences();
            assertTrue(getNextSequenceValue(conn) >= LOWEST_ACCEPTACLE_SEQUENCE_VALUE);
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    /**
     * Verifies that if a sequence has a value already high enough, the value is not being set to a lower value
     *
     * @throws Exception
     */
    public void testUpdateSequences_valueAlreadyHighEnough() throws Exception {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            sequenceUpdater.updateSequences();
            long updatedSequenceValue = getNextSequenceValue(conn);
            sequenceUpdater.updateSequences();
            assertFalse(getNextSequenceValue(conn) <= updatedSequenceValue);
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    /**
     * Abstract method, since it is dbms dependent
     *
     * @param conn
     * @return The next value of the test sequence
     * @throws SQLException
     */
    protected abstract long getNextSequenceValue(Connection conn) throws SQLException;

}
