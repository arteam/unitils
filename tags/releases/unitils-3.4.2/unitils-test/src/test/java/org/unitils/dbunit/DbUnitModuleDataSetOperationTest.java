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
package org.unitils.dbunit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.unitils.database.SQLUnitils.executeUpdate;
import static org.unitils.database.SQLUnitils.executeUpdateQuietly;
import static org.unitils.database.SQLUnitils.getItemAsString;

import java.util.Properties;

import javax.sql.DataSource;

import org.dbunit.dataset.IDataSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.ConfigurationLoader;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.datasetloadstrategy.DataSetLoadStrategy;
import org.unitils.dbunit.util.DbUnitDatabaseConnection;

/**
 * Tests DbUnitModule's feature for using different DataSetOperations
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DbUnitModuleDataSetOperationTest extends UnitilsJUnit4 {

    private DbUnitModule dbUnitModule;

    @TestDataSource
    private DataSource dataSource;


    @Before
    public void setUp() throws Exception {
        Properties configuration = new ConfigurationLoader().loadConfiguration();
        dbUnitModule = new DbUnitModule();
        dbUnitModule.init(configuration);

        dropTestTables();
        createTestTables();

        MockDataSetLoadStrategy.operationExecuted = false;
    }


    @After
    public void tearDown() throws Exception {
        dropTestTables();
    }


    @Test
    public void testLoadDataSet_defaultDataSetOperation() throws Exception {
        dbUnitModule.insertDataSet(DataSetTest.class.getMethod("testMethod1"), new DataSetTest());
        assertLoadedDataSet("DbUnitModuleDataSetOperationTest$DataSetTest.xml");
    }


    @Test
    public void testLoadDataSet_customDataSetOperation() throws Exception {
        dbUnitModule.insertDataSet(DataSetTest.class.getMethod("testMethodCustomDataSetOperation"), new DataSetTest());
        assertTrue(MockDataSetLoadStrategy.operationExecuted);
    }
    


    /**
     * Utility method to assert that the correct data set was loaded.
     *
     * @param expectedDataSetName the name of the data set, not null
     */
    private void assertLoadedDataSet(String expectedDataSetName) {
        String dataSet = getItemAsString("select dataset from test", dataSource);
        assertEquals(expectedDataSetName, dataSet);
    }


    /**
     * Creates the test tables.
     */
    private void createTestTables() {
        executeUpdate("create table test(dataset varchar(100))", dataSource);
    }


    /**
     * Removes the test database tables
     */
    private void dropTestTables() {
        executeUpdateQuietly("drop table test", dataSource);
    }


    /**
     * Test class with a class level dataset
     */
    @DataSet
    public class DataSetTest {

        public void testMethod1() {
        }

        @DataSet(loadStrategy = MockDataSetLoadStrategy.class)
        public void testMethodCustomDataSetOperation() {
        }
    }


    public static class MockDataSetLoadStrategy implements DataSetLoadStrategy {

        private static boolean operationExecuted;

        public void execute(DbUnitDatabaseConnection dbUnitDatabaseConnection, IDataSet dataSet) {
            operationExecuted = true;
        }
    }
}
