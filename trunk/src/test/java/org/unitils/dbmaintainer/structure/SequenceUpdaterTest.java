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
package org.unitils.dbmaintainer.structure;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.UnitilsJUnit3;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.dbsupport.DbSupport;
import static org.unitils.core.dbsupport.DbSupportFactory.getDefaultDbSupport;
import static org.unitils.core.dbsupport.TestSQLUtils.dropTestSequences;
import static org.unitils.core.dbsupport.TestSQLUtils.dropTestTables;
import static org.unitils.core.util.SQLUtils.executeUpdate;
import static org.unitils.core.util.SQLUtils.getItemAsLong;
import org.unitils.database.annotations.TestDataSource;
import static org.unitils.dbmaintainer.structure.impl.DefaultSequenceUpdater.PROPKEY_LOWEST_ACCEPTABLE_SEQUENCE_VALUE;
import static org.unitils.dbmaintainer.util.DatabaseModuleConfigUtils.getConfiguredDatabaseTaskInstance;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Test class for the SequenceUpdater. Contains tests that can be implemented generally for all different database dialects.
 * Extended with implementations for each supported database dialect.
 * <p/>
 * Tests are only executed for the currently activated database dialect. By default, a hsqldb in-memory database is used,
 * to avoid the need for setting up a database instance. If you want to run unit tests for other dbms's, change the
 * configuration in test/resources/unitils.properties
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class SequenceUpdaterTest extends UnitilsJUnit3 {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(SequenceUpdaterTest.class);

    /* DataSource for the test database, is injected */
    @TestDataSource
    private DataSource dataSource = null;

    /* Tested object */
    private SequenceUpdater sequenceUpdater;

    /* DbSupport instance */
    private DbSupport dbSupport;


    /**
     * Test fixture. Configures the implementation of the SequenceUpdater that matches the currenlty configured dialect.
     * Creates a test table and test sequence.
     */
    protected void setUp() throws Exception {
        super.setUp();

        Properties configuration = new ConfigurationLoader().loadConfiguration();
        configuration.setProperty(PROPKEY_LOWEST_ACCEPTABLE_SEQUENCE_VALUE, "1000");

        sequenceUpdater = getConfiguredDatabaseTaskInstance(SequenceUpdater.class, configuration, dataSource);
        dbSupport = getDefaultDbSupport(configuration, dataSource);

        cleanupTestDatabase();
        createTestDatabase();
    }


    /**
     * Clears the database, to avoid interference with other tests
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        cleanupTestDatabase();
    }


    /**
     * Tests the update sequences behavior
     */
    public void testUpdateSequences() throws Exception {
        if (!dbSupport.supportsSequences()) {
            logger.warn("Current dialect does not support sequences. Skipping test.");
            return;
        }
        assertEquals(0, getCurrentSequenceValue());
        sequenceUpdater.updateSequences();
        assertEquals(1000, getCurrentSequenceValue());
    }


    /**
     * Verifies that if a sequence has a value already high enough, the value is not being set to a lower value
     */
    public void testUpdateSequences_valueAlreadyHighEnough() throws Exception {
        if (!dbSupport.supportsSequences()) {
            logger.warn("Current dialect does not support sequences. Skipping test.");
            return;
        }
        sequenceUpdater.updateSequences();
        assertEquals(1000, getCurrentSequenceValue());
        sequenceUpdater.updateSequences();
        assertEquals(1000, getCurrentSequenceValue());
    }


    /**
     * Tests the update identity columns behavior
     */
    public void testUpdateSequences_identityColumns() throws Exception {
        if (!dbSupport.supportsIdentityColumns()) {
            logger.warn("Current dialect does not support identity columns. Skipping test.");
            return;
        }
        assertEquals(0, getCurrentIdentityColumnValue());
        sequenceUpdater.updateSequences();
        assertEquals(1000, getCurrentIdentityColumnValue());
    }


    /**
     * Verifies that if a identity columns has a value already high enough, the value is not being set to a lower value
     */
    public void testUpdateSequences_identityColumnsValueAlreadyHighEnough() throws Exception {
        if (!dbSupport.supportsIdentityColumns()) {
            logger.warn("Current dialect does not support identity columns. Skipping test.");
            return;
        }
        sequenceUpdater.updateSequences();
        assertEquals(1000, getCurrentIdentityColumnValue());
        sequenceUpdater.updateSequences();
        assertEquals(1000, getCurrentIdentityColumnValue());
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
        }
    }

    //
    // Database setup for HsqlDb
    //

    /**
     * Creates all test database structures
     */
    private void createTestDatabaseHsqlDb() throws Exception {
        // create table containing identity
        executeUpdate("create table test_table1 (col1 int not null identity, col2 varchar(12) not null)", dataSource);
        // create table without identity
        executeUpdate("create table test_table2 (col1 int primary key, col2 varchar(12) not null)", dataSource);
        // create sequences
        executeUpdate("create sequence test_sequence", dataSource);
    }


    /**
     * Drops all created test database structures
     */
    private void cleanupTestDatabaseHsqlDb() throws Exception {
        dropTestTables(dbSupport, "test_table1", "test_table2");
        dropTestSequences(dbSupport, "test_sequence");
    }

    //
    // Database setup for MySql
    //

    /**
     * Creates all test database structures
     */
    private void createTestDatabaseMySql() throws Exception {
        // create tables with auto increment column
        executeUpdate("create table test_table1 (col1 int not null primary key AUTO_INCREMENT, col2 varchar(12) not null)", dataSource);
        // create table without increment column
        executeUpdate("create table test_table2 (col1 int not null primary key, col2 varchar(12) not null)", dataSource);
    }


    /**
     * Drops all created test database structures
     */
    private void cleanupTestDatabaseMySql() throws Exception {
        dropTestTables(dbSupport, "test_table1", "test_table2");
    }

    //
    // Database setup for Oracle
    //

    /**
     * Creates all test database structures (view, tables...)
     */
    private void createTestDatabaseOracle() throws Exception {
        // create sequence
        executeUpdate("create sequence test_sequence", dataSource);
    }


    /**
     * Drops all created test database structures (views, tables...)
     */
    private void cleanupTestDatabaseOracle() throws Exception {
        dropTestSequences(dbSupport, "test_sequence");
    }

    //
    // Database setup for PostgreSql
    //

    /**
     * Creates all test database structures
     */
    private void createTestDatabasePostgreSql() throws Exception {
        // create sequence
        executeUpdate("create sequence test_sequence", dataSource);
    }


    /**
     * Drops all created test database structures
     */
    private void cleanupTestDatabasePostgreSql() throws Exception {
        dropTestSequences(dbSupport, "test_sequence");
    }


    /**
     * Returns the current value for the test_sequence
     *
     * @return the sequence value
     */
    private long getCurrentSequenceValue() throws SQLException {
        String correctCaseSequenceName = dbSupport.toCorrectCaseIdentifier("test_sequence");
        return dbSupport.getCurrentValueOfSequence(correctCaseSequenceName);
    }


    /**
     * Returns the current value for the identity column test_table.col1
     *
     * @return The identity column value
     */
    private long getCurrentIdentityColumnValue() throws SQLException {
        executeUpdate("delete from test_table1", dataSource);
        executeUpdate("insert into test_table1(col2) values('test')", dataSource);
        return getItemAsLong("select col1 from test_table1 where col2 = 'test'", dataSource);
    }


}
