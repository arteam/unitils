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
package org.unitils.dbmaintainer.structure;

import org.dbmaintain.database.Database;
import org.dbmaintain.structure.sequence.SequenceUpdater;
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

import static org.dbmaintain.config.DbMaintainProperties.PROPERTY_LOWEST_ACCEPTABLE_SEQUENCE_VALUE;
import static org.junit.Assert.assertTrue;
import static org.unitils.database.SQLUnitils.*;
import static org.unitils.testutil.TestUnitilsConfiguration.getUnitilsConfiguration;

/**
 * Test class for the SequenceUpdater.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Scott Prater
 */
public class SequenceUpdaterTest extends UnitilsJUnit4 {

    private SequenceUpdater sequenceUpdater;

    @TestDataSource
    protected DataSource dataSource;
    protected Database defaultDatabase;


    @Before
    public void setUp() throws Exception {
        Properties configuration = getUnitilsConfiguration();
        configuration.setProperty(PROPERTY_LOWEST_ACCEPTABLE_SEQUENCE_VALUE, "1000");

        cleanupTestDatabase();
        createTestDatabase();

        DbMaintainManager dbMaintainManager = new DbMaintainManager(configuration, false, new TestDataSourceFactory(), new UnitilsTransactionManager());
        defaultDatabase = dbMaintainManager.getDatabase(null);

        sequenceUpdater = dbMaintainManager.getDbMaintainMainFactory().createSequenceUpdater();
    }

    @After
    public void tearDown() throws Exception {
        cleanupTestDatabase();
    }


    @Test
    public void testUpdateSequences() throws Exception {
        assertCurrentSequenceValueBetween(0, 10);
        sequenceUpdater.updateSequences();
        assertCurrentSequenceValueBetween(1000, 1010);
    }

    @Test
    public void testUpdateSequences_valueAlreadyHighEnough() throws Exception {
        assertCurrentSequenceValueBetween(0, 10);
        sequenceUpdater.updateSequences();
        assertCurrentSequenceValueBetween(1000, 1010);
        sequenceUpdater.updateSequences();
        assertCurrentSequenceValueBetween(1000, 1010);
    }

    @Test
    public void testUpdateSequences_identityColumns() throws Exception {
        assertCurrentIdentityColumnValueBetween(0, 10);
        sequenceUpdater.updateSequences();
        assertCurrentIdentityColumnValueBetween(1000, 1010);
    }

    @Test
    public void testUpdateSequences_identityColumnsValueAlreadyHighEnough() throws Exception {
        assertCurrentIdentityColumnValueBetween(0, 10);
        sequenceUpdater.updateSequences();
        assertCurrentIdentityColumnValueBetween(1000, 1010);
        sequenceUpdater.updateSequences();
        assertCurrentIdentityColumnValueBetween(1000, 1010);
    }


    private void assertCurrentSequenceValueBetween(long minValue, long maxValue) {
        String correctCaseSequenceName = defaultDatabase.toCorrectCaseIdentifier("test_sequence");
        long currentValue = defaultDatabase.getSequenceValue(correctCaseSequenceName);
        assertTrue("Current sequence value is not between " + minValue + " and " + maxValue, (currentValue >= minValue && currentValue <= maxValue));
    }

    private void assertCurrentIdentityColumnValueBetween(long minValue, long maxValue) {
        executeUpdate("delete from test_table1", dataSource);
        executeUpdate("insert into test_table1(col2) values('test')", dataSource);
        long currentValue = getItemAsLong("select col1 from test_table1 where col2 = 'test'", dataSource);
        assertTrue("Current sequence value is not between " + minValue + " and " + maxValue, (currentValue >= minValue && currentValue <= maxValue));
    }


    private void createTestDatabase() throws Exception {
        // create table containing identity
        executeUpdate("create table test_table1 (col1 int not null identity, col2 varchar(12) not null)", dataSource);
        // create table without identity
        executeUpdate("create table test_table2 (col1 int primary key, col2 varchar(12) not null)", dataSource);
        // create sequences
        executeUpdate("create sequence test_sequence", dataSource);
    }

    private void cleanupTestDatabase() throws Exception {
        executeUpdateQuietly("drop table test_table1", dataSource);
        executeUpdateQuietly("drop table test_table2", dataSource);
        executeUpdateQuietly("drop sequence test_sequence", dataSource);
    }
}
