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

import org.junit.After;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.UnitilsException;
import static org.unitils.database.SQLUnitils.executeUpdate;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.annotation.ExpectedDataSet;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Test the expected data set behavior when there are primary keys.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ExpectedDataSetWithPrimaryKeysTest extends UnitilsJUnit4 {

    /* Tested object */
    private DbUnitModule dbUnitModule;

    /* The dataSource */
    @TestDataSource
    private DataSource dataSource = null;


    /**
     * Initializes the test fixture.
     */
    @Before
    public void setUp() throws Exception {
        Properties configuration = new ConfigurationLoader().loadConfiguration();
        dbUnitModule = new DbUnitModule();
        dbUnitModule.init(configuration);

        dropTestTable();
        createTestTable();
    }


    /**
     * Clean-up test database.
     */
    @After
    public void tearDown() throws Exception {
        dropTestTable();
    }


    @Test
    public void testEqual() throws Exception {
        assertEqualDataSet("equalDataSet");
    }


    @Test
    public void testDifferentValues() throws Exception {
        assertDifferentDataSet("differentValues");
    }


    @Test
    public void testMissingValue() throws Exception {
        assertDifferentDataSet("missingRow");
    }


    @Test
    public void testEmptyTable() throws Exception {
        assertDifferentDataSet("emptyTable");
    }


    private void assertDifferentDataSet(String methodName) throws Exception {
        try {
            assertEqualDataSet(methodName);
        } catch (AssertionError e) {
            return;
        }
        fail("No differences found for dataset. Method name: " + methodName);
    }


    private void assertEqualDataSet(String methodName) throws Exception {
        Object testObject = new TestClass();
        Method testMethod = TestClass.class.getMethod(methodName);
        dbUnitModule.insertDataSet(testMethod, testObject);
        dbUnitModule.assertDbContentAsExpected(testMethod, testObject);
    }


    /**
     * Test class defining the data sets that need to be loaded/checked
     */
    @DataSet("ExpectedDataSetWithPrimaryKeysTest.xml")
    public class TestClass {

        @ExpectedDataSet("ExpectedDataSetWithPrimaryKeysTest.xml")
        public void equalDataSet() {
        }

        @ExpectedDataSet("ExpectedDataSetWithPrimaryKeysTest-differentValues.xml")
        public void differentValues() {
        }

        @ExpectedDataSet("ExpectedDataSetWithPrimaryKeysTest-missingRow.xml")
        public void missingRow() {
        }

        @ExpectedDataSet("ExpectedDataSetWithPrimaryKeysTest-emptyTable.xml")
        public void emptyTable() {
        }
    }


    /**
     * Utility method to create the test table.
     */
    private void createTestTable() throws SQLException {
        executeUpdate("create table TEST(pk varchar(2) primary key, column1 varchar(10), column2 varchar(10))", dataSource);
    }


    /**
     * Removes the test database table
     */
    private void dropTestTable() throws SQLException {
        try {
            executeUpdate("drop table TEST", dataSource);
        } catch (UnitilsException e) {
            // Ignored
        }
    }


}