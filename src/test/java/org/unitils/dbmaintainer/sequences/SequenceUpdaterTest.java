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
import org.unitils.database.annotations.DatabaseTest;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.dbmaintainer.dbsupport.DbSupport;
import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.util.DatabaseModuleConfigUtils;

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
public class SequenceUpdaterTest extends UnitilsJUnit3 {

    /**
     * DataSource for the test database, is injected
     */
    @TestDataSource
    protected DataSource dataSource;

    /**
     * Tested object
     */
    protected SequenceUpdater sequenceUpdater;

    /* DbSupport instance */
    private DbSupport dbSupport;

    /**
     * Value that sequences should at least have after updating the sequences
     */
    protected static final int LOWEST_ACCEPTACLE_SEQUENCE_VALUE = 1000;


    /**
     * Test fixture. Configures the implementation of the SequenceUpdater that matches the currenlty configured dialect.
     * Creates a test table and test sequence.
     */
    protected void setUp() throws Exception {
        super.setUp();

        Configuration configuration = new ConfigurationLoader().loadConfiguration();
        configuration.setProperty(DefaultSequenceUpdater.PROPKEY_LOWEST_ACCEPTABLE_SEQUENCE_VALUE, LOWEST_ACCEPTACLE_SEQUENCE_VALUE);

        StatementHandler statementHandler = DatabaseModuleConfigUtils.getConfiguredStatementHandlerInstance(configuration, dataSource);
        sequenceUpdater = DatabaseModuleConfigUtils.getConfiguredDatabaseTaskInstance(SequenceUpdater.class, configuration, dataSource, statementHandler);
        dbSupport = DatabaseModuleConfigUtils.getConfiguredDbSupportInstance(configuration, dataSource, statementHandler);

        if (dbSupport.supportsSequences()) {
            dropTestSequence();
            dropTestTable();

            createTestTable();
            insertTestRecord();
            createTestSequence();
        }
    }


    /**
     * Clears the database, to avoid interference with other tests
     */
    protected void tearDown() throws Exception {
        super.tearDown();

        if (dbSupport.supportsSequences()) {
            dropTestSequence();
            dropTestTable();
        }
    }


    /**
     * Inserts a test record
     */
    private void insertTestRecord() {
        Connection conn = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            // Make sure previous setup is cleaned up
            st.execute("insert into testtable values('test')");
        } catch (SQLException e) {
            // No action is taken
        } finally {
            DbUtils.closeQuietly(conn, st, null);
        }
    }


    /**
     * Creates a test table
     */
    private void createTestTable() {
        Connection conn = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            // Make sure previous setup is cleaned up
            st.execute("create table testtable (test varchar(10))");
        } catch (SQLException e) {
            // No action is taken
        } finally {
            DbUtils.closeQuietly(conn, st, null);
        }
    }


    /**
     * Drops the test table
     */
    private void dropTestTable() {
        Connection conn = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            // Make sure previous setup is cleaned up
            st.execute("drop table testtable");
        } catch (SQLException e) {
            // No action is taken
        } finally {
            DbUtils.closeQuietly(conn, st, null);
        }
    }


    /**
     * Creates a test sequence
     */
    private void createTestSequence() {
        Connection conn = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            // Make sure previous setup is cleaned up
            st.execute("create sequence testsequence");
        } catch (SQLException e) {
            // No action is taken
        } finally {
            DbUtils.closeQuietly(conn, st, null);
        }
    }


    /**
     * Drops the test sequence
     */
    private void dropTestSequence() {
        Connection conn = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            // Make sure previous setup is cleaned up
            st.execute("drop sequence testsequence");
        } catch (SQLException e) {
            // No action is taken
        } finally {
            DbUtils.closeQuietly(conn, st, null);
        }
    }


    /**
     * Tests the update sequences behavior
     */
    public void testUpdateSequences() throws Exception {
        if (dbSupport.supportsSequences()) {
            assertTrue(getNextTestSequenceValue() < LOWEST_ACCEPTACLE_SEQUENCE_VALUE);
            sequenceUpdater.updateSequences();
            assertTrue(getNextTestSequenceValue() >= LOWEST_ACCEPTACLE_SEQUENCE_VALUE);
        }
    }


    /**
     * Verifies that if a sequence has a value already high enough, the value is not being set to a lower value
     */
    public void testUpdateSequences_valueAlreadyHighEnough() throws Exception {
        if (dbSupport.supportsSequences()) {
            Connection conn = null;
            try {
                conn = dataSource.getConnection();
                sequenceUpdater.updateSequences();
                long updatedSequenceValue = getNextTestSequenceValue();
                sequenceUpdater.updateSequences();
                assertFalse(getNextTestSequenceValue() <= updatedSequenceValue);
            } finally {
                DbUtils.closeQuietly(conn);
            }
        }
    }


    /**
     * Returns the next value for the test sequence
     *
     * @return the sequence
     */
    private long getNextTestSequenceValue() throws SQLException {
        return dbSupport.getNextValueOfSequence("testsequence");
    }

}
