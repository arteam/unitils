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
package org.unitils.dbunit;

import static org.unitils.core.util.SQLUtils.executeUpdate;
import static org.unitils.core.util.SQLUtils.executeUpdateQuietly;
import static org.unitils.core.util.SQLUtils.getItemAsString;
import static org.unitils.core.util.SQLUtils.getItemsAsStringSet;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Set;

import javax.sql.DataSource;

import org.dbunit.operation.DatabaseOperation;
import org.unitils.UnitilsJUnit3;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.UnitilsException;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.datasetoperation.CleanInsertOperation;

/**
 * Test class for loading of data sets using the {@link DbUnitModule}.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DbUnitModuleDataSetTest extends UnitilsJUnit3 {

    /* Tested object */
    private DbUnitModule dbUnitModule;

    /* The dataSource */
    @TestDataSource
    private DataSource dataSource = null;


    /**
     * Initializes the test fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();

        Properties configuration = new ConfigurationLoader().loadConfiguration();
        dbUnitModule = new DbUnitModule();
        dbUnitModule.init(configuration);

        dropTestTable();
        createTestTable();
    }


    /**
     * Clean-up test database.
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        dropTestTable();
    }


    /**
     * Test for no annotation on method-level and default annotation on class-level
     */
    public void testDataSet() throws Exception {
        dbUnitModule.insertTestData(DataSetTest.class.getMethod("testMethod1"));
        assertLoadedDataSet("DbUnitModuleDataSetTest$DataSetTest.testMethod1.xml");
    }


    /**
     * Test for custom annotation on method-level overriding default annotation on class-level
     */
    public void testInsertTestData_customMethodDataSet() throws Exception {
        dbUnitModule.insertTestData(DataSetTest.class.getMethod("testMethod2"));
        assertLoadedDataSet("CustomDataSet.xml");
    }


    /**
     * Test for default annotation on method-level and default annotation on class-level
     */
    public void testInsertTestData_classAndMethodDataSet() throws Exception {
        dbUnitModule.insertTestData(DataSetTest.class.getMethod("testMethod3"));
        assertLoadedDataSet("DbUnitModuleDataSetTest$DataSetTest.testMethod3.xml");
    }


    /**
     * Test for default file that is not found
     */
    public void testInsertTestData_notFound() throws Exception {
        try {
            dbUnitModule.insertTestData(DataSetTest.class.getMethod("testNotFound1"));
            fail("Expected UnitilsException");
        } catch (UnitilsException e) {
            //expected
        }
    }


    /**
     * Test for custom file that is not found
     */
    public void testInsertTestData_customNotFound() throws Exception {
        try {
            dbUnitModule.insertTestData(DataSetTest.class.getMethod("testNotFound2"));
            fail("Expected UnitilsException");
        } catch (UnitilsException e) {
            //expected
        }
    }


    /**
     * Test for default annotation on method-level and no annotation on class-level
     */
    public void testInsertTestData_noClassDataSet() throws Exception {
        dbUnitModule.insertTestData(DataSetTestNoClassLevel.class.getMethod("testMethod1"));
        assertLoadedDataSet("DbUnitModuleDataSetTest$DataSetTestNoClassLevel.testMethod1.xml");
    }


    /**
     * Test for custom annotation on method-level and no annotation on class-level
     */
    public void testInsertTestData_noClassDataSetCustomMethodDataSet() throws Exception {
        dbUnitModule.insertTestData(DataSetTestNoClassLevel.class.getMethod("testMethod2"));
        assertLoadedDataSet("CustomDataSet.xml");
    }


    /**
     * Test for no annotation on method-level and no annotation on class-level.
     * No data set should have been loaded.
     */
    public void testInsertTestData_noClassAndMethodDataSet() throws Exception {
        dbUnitModule.insertTestData(DataSetTestNoClassLevel.class.getMethod("testMethod3"));
        Set<String> datasets = getItemsAsStringSet("select dataset from test", dataSource);
        assertTrue(datasets.isEmpty()); // nothing loaded
    }


    /**
     * Test for no annotation on method-level and custom annotation on class-level
     */
    public void testInsertTestData_customClassDataSet() throws Exception {
        dbUnitModule.insertTestData(DataSetTestCustomClassLevel.class.getMethod("testMethod1"));
        assertLoadedDataSet("CustomDataSet.xml");
    }


    /**
     * Test for default annotation on method-level overriding a custom annotation on class-level
     */
    public void testInsertTestData_customClassDataSetOverridenByDefault() throws Exception {
        dbUnitModule.insertTestData(DataSetTestCustomClassLevel.class.getMethod("testMethod2"));
        assertLoadedDataSet("DbUnitModuleDataSetTest$DataSetTestCustomClassLevel.testMethod2.xml");
    }


    /**
     * Test for a direct call to {@link DbUnitModule#insertTestData(java.io.InputStream,DatabaseOperation)}
     */
    public void testInsertTestData_directCall() throws Exception {
        InputStream dataSetIS = this.getClass().getResourceAsStream("CustomDataSet.xml");
        dbUnitModule.insertTestData(dataSetIS, new CleanInsertOperation());
        assertLoadedDataSet("CustomDataSet.xml");
    }


    /**
     * Utility method to assert that the correct data set was loaded.
     *
     * @param expectedDataSetName the name of the data set, not null
     */
    private void assertLoadedDataSet(String expectedDataSetName) throws SQLException {
        String dataSet = getItemAsString("select dataset from test", dataSource);
        assertEquals(expectedDataSetName, dataSet);
    }


    /**
     * Utility method to create the test table.
     */
    private void createTestTable() {
        executeUpdate("create table test (dataset varchar(100))", dataSource);
    }


    /**
     * Removes the test database table
     */
    private void dropTestTable() {
        executeUpdateQuietly("drop table test", dataSource);
    }


    /**
     * Test class with a class level dataset
     */
    @DataSet
    public class DataSetTest {

        public void testMethod1() {
        }

        @DataSet("CustomDataSet.xml")
        public void testMethod2() {
        }

        @DataSet
        public void testMethod3() {
        }

        public void testNotFound1() {
        }

        @DataSet("xxxxxx.xml")
        public void testNotFound2() {
        }
    }


    /**
     * Test class without a class level dataset
     */
    public class DataSetTestNoClassLevel {

        @DataSet
        public void testMethod1() {
        }

        @DataSet("CustomDataSet.xml")
        public void testMethod2() {
        }

        public void testMethod3() {
        }
    }


    /**
     * Test class with a custom class level dataset
     */
    @DataSet("CustomDataSet.xml")
    public class DataSetTestCustomClassLevel {

        public void testMethod1() {
        }

        @DataSet
        public void testMethod2() {
        }
    }

}
