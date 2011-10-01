/*
 * Copyright 2008,  Unitils.org
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
import org.junit.After;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.dbsupport.DbSupport;
import org.unitils.core.dbsupport.SQLHandler;

import static org.unitils.core.dbsupport.DbSupportFactory.getDefaultDbSupport;
import org.unitils.core.dbsupport.DefaultSQLHandler;
import static org.unitils.core.util.SQLTestUtils.dropTestSequences;
import static org.unitils.core.util.SQLTestUtils.dropTestTables;
import static org.unitils.database.SQLUnitils.executeUpdate;
import static org.unitils.database.SQLUnitils.getItemAsLong;
import org.unitils.database.annotations.TestDataSource;
import static org.unitils.dbmaintainer.structure.impl.DefaultSequenceUpdater.PROPKEY_LOWEST_ACCEPTABLE_SEQUENCE_VALUE;
import static org.unitils.dbmaintainer.util.DatabaseModuleConfigUtils.getConfiguredDatabaseTaskInstance;

import javax.sql.DataSource;
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
 * @author Scott Prater
 */
public class SequenceUpdaterTest extends UnitilsJUnit4 {

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
    @Before
    public void setUp() throws Exception {
        Properties configuration = new ConfigurationLoader().loadConfiguration();
        configuration.setProperty(PROPKEY_LOWEST_ACCEPTABLE_SEQUENCE_VALUE, "1000");

        SQLHandler sqlHandler = new DefaultSQLHandler(dataSource);
        sequenceUpdater = getConfiguredDatabaseTaskInstance(SequenceUpdater.class, configuration, sqlHandler);
        dbSupport = getDefaultDbSupport(configuration, sqlHandler);

        cleanupTestDatabase();
        createTestDatabase();
    }


    /**
     * Clears the database, to avoid interference with other tests
     */
    @After
    public void tearDown() throws Exception {
        cleanupTestDatabase();
    }


    /**
     * Tests the update sequences behavior
     */
    @Test
    public void testUpdateSequences() throws Exception {
        if (!dbSupport.supportsSequences()) {
            logger.warn("Current dialect does not support sequences. Skipping test.");
            return;
        }
        assertCurrentSequenceValueBetween(0, 10);
        sequenceUpdater.updateSequences();
        assertCurrentSequenceValueBetween(1000, 1010);
    }


    /**
     * Verifies that if a sequence has a value already high enough, the value is not being set to a lower value
     */
    @Test
    public void testUpdateSequences_valueAlreadyHighEnough() throws Exception {
        if (!dbSupport.supportsSequences()) {
            logger.warn("Current dialect does not support sequences. Skipping test.");
            return;
        }
        assertCurrentSequenceValueBetween(0, 10);
        sequenceUpdater.updateSequences();
        assertCurrentSequenceValueBetween(1000, 1010);
        sequenceUpdater.updateSequences();
        assertCurrentSequenceValueBetween(1000, 1010);
    }


    /**
     * Tests the update identity columns behavior
     */
    @Test
    public void testUpdateSequences_identityColumns() throws Exception {
        if (!dbSupport.supportsIdentityColumns()) {
            logger.warn("Current dialect does not support identity columns. Skipping test.");
            return;
        }
        assertCurrentIdentityColumnValueBetween(0, 10);
        sequenceUpdater.updateSequences();
        assertCurrentIdentityColumnValueBetween(1000, 1010);
    }


    /**
     * Verifies that if a identity columns has a value already high enough, the value is not being set to a lower value
     */
    @Test
    public void testUpdateSequences_identityColumnsValueAlreadyHighEnough() throws Exception {
        if (!dbSupport.supportsIdentityColumns()) {
            logger.warn("Current dialect does not support identity columns. Skipping test.");
            return;
        }
        assertCurrentIdentityColumnValueBetween(0, 10);
        sequenceUpdater.updateSequences();
        assertCurrentIdentityColumnValueBetween(1000, 1010);
        sequenceUpdater.updateSequences();
        assertCurrentIdentityColumnValueBetween(1000, 1010);
    }


    /**
     * Asserts that the current value for the test_sequence is between the given values
     *
     * @param minValue The minimum value (included)
     * @param maxValue The maximum value (included)
     */
    private void assertCurrentSequenceValueBetween(long minValue, long maxValue) {
        String correctCaseSequenceName = dbSupport.toCorrectCaseIdentifier("test_sequence");
        long currentValue = dbSupport.getSequenceValue(correctCaseSequenceName);
        assertTrue("Current sequence value is not between " + minValue + " and " + maxValue, (currentValue >= minValue && currentValue <= maxValue));
    }


    /**
     * Asserts that the current value for the identity column test_table.col1 is between the given values.
     *
     * @param minValue The minimum value (included)
     * @param maxValue The maximum value (included)
     */
    private void assertCurrentIdentityColumnValueBetween(long minValue, long maxValue) {
        executeUpdate("delete from test_table1", dataSource);
        executeUpdate("insert into test_table1(col2) values('test')", dataSource);
        long currentValue = getItemAsLong("select col1 from test_table1 where col2 = 'test'", dataSource);
        assertTrue("Current sequence value is not between " + minValue + " and " + maxValue, (currentValue >= minValue && currentValue <= maxValue));
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

    //
    // Database setup for Db2
    //

    /**
     * Creates all test database structures (view, tables...)
     */
    private void createTestDatabaseDb2() throws Exception {
        // create tables with auto increment column
        executeUpdate("create table test_table1 (col1 int not null primary key generated by default as identity, col2 varchar(12) not null)", dataSource);
        // create table without increment column
        executeUpdate("create table test_table2 (col1 int not null primary key, col2 varchar(12) not null)", dataSource);
        // create sequences
        executeUpdate("create sequence test_sequence", dataSource);
    }


    /**
     * Drops all created test database structures (views, tables...)
     */
    private void cleanupTestDatabaseDb2() throws Exception {
        dropTestTables(dbSupport, "test_table1", "test_table2");
        dropTestSequences(dbSupport, "test_sequence");
    }

    //
    // Database setup for Derby
    //

    /**
     * Creates all test database structures
     */
    private void createTestDatabaseDerby() throws Exception {
        // create table containing identity
        executeUpdate("create table test_table1 (col1 int not null primary key generated always as identity, col2 varchar(12) not null)", dataSource);
        // create table without identity
        executeUpdate("create table test_table2 (col1 int not null primary key, col2 varchar(12) not null)", dataSource);
    }


    /**
     * Drops all created test database structures
     */
    private void cleanupTestDatabaseDerby() throws Exception {
        dropTestTables(dbSupport, "test_table1", "test_table2");
    }

    //
    // Database setup for MS-Sql
    //

    /**
     * Creates all test database structures
     */
    private void createTestDatabaseMsSql() throws Exception {
        // create table containing identity
        executeUpdate("create table test_table1 (col1 int not null primary key identity, col2 varchar(12) not null)", dataSource);
        // create table without identity
        executeUpdate("create table test_table2 (col1 int not null primary key, col2 varchar(12) not null)", dataSource);
    }


    /**
     * Drops all created test database structures
     */
    private void cleanupTestDatabaseMsSql() throws Exception {
        dropTestTables(dbSupport, "test_table1", "test_table2");
    }
}
