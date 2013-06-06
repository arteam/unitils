/*
 * Copyright 2013,  Unitils.org
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

import org.unitilsnew.UnitilsJUnit4;

/**
 * Tests DbUnitModule's feature for using different DataSetOperations
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DbUnitModuleDataSetOperationTest extends UnitilsJUnit4 {

//    private DbUnitModule dbUnitModule;
//
//    @TestDataSource
//    private DataSource dataSource = null;
//
//
//    @Before
//    public void setUp() throws Exception {
//        Properties configuration = new ConfigurationLoader().loadConfiguration();
//        dbUnitModule = new DbUnitModule();
//        dbUnitModule.init(configuration);
//
//        dropTestTables();
//        createTestTables();
//
//        MockDataSetLoadStrategy.operationExecuted = false;
//    }
//
//
//    @After
//    public void tearDown() throws Exception {
//        dropTestTables();
//    }
//
//
//    @Test
//    public void testLoadDataSet_defaultDataSetOperation() throws Exception {
//        dbUnitModule.insertDataSet(DataSetTest.class.getMethod("testMethod1"), new DataSetTest());
//        assertLoadedDataSet("DbUnitModuleDataSetOperationTest$DataSetTest.xml");
//    }
//
//
//    @Test
//    public void testLoadDataSet_customDataSetOperation() throws Exception {
//        dbUnitModule.insertDataSet(DataSetTest.class.getMethod("testMethodCustomDataSetOperation"), new DataSetTest());
//        assertTrue(MockDataSetLoadStrategy.operationExecuted);
//    }
//
//
//    /**
//     * Utility method to assert that the correct data set was loaded.
//     *
//     * @param expectedDataSetName the name of the data set, not null
//     */
//    private void assertLoadedDataSet(String expectedDataSetName) {
//        String dataSet = getString("select dataset from test");
//        assertEquals(expectedDataSetName, dataSet);
//    }
//
//
//    /**
//     * Creates the test tables.
//     */
//    private void createTestTables() {
//        executeUpdate("create table test(dataset varchar(100))");
//    }
//
//
//    /**
//     * Removes the test database tables
//     */
//    private void dropTestTables() {
//        executeUpdateQuietly("drop table test");
//    }
//
//
//    /**
//     * Test class with a class level dataset
//     */
//    @DataSet
//    public class DataSetTest {
//
//        public void testMethod1() {
//        }
//
//        @DataSet(loadStrategy = MockDataSetLoadStrategy.class)
//        public void testMethodCustomDataSetOperation() {
//        }
//    }
//
//
//    public static class MockDataSetLoadStrategy implements DataSetLoadStrategy {
//
//        private static boolean operationExecuted;
//
//        public void execute(DbUnitDatabaseConnection dbUnitDatabaseConnection, IDataSet dataSet) {
//            operationExecuted = true;
//        }
//    }
}
