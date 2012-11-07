/*
 * Copyright 2012,  Unitils.org
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
import org.junit.Before;
import org.junit.Test;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.UnitilsException;
import org.unitils.database.annotation.TestDataSource;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.annotation.ExpectedDataSet;
import org.unitilsnew.UnitilsJUnit4;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Properties;

import static org.unitils.database.SqlUnitils.executeUpdate;


/**
 * Tests the expected data set behavior when the annotation is not put on class level.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ExpectedDataSetNoAnnotationOnClassLevelTest extends UnitilsJUnit4 {

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


    /**
     * Test for default annotation on method-level and no annotation on class-level
     */
    @Test
    public void testEqual() throws Exception {
        Object testObject = new TestClass();
        Method testMethod = TestClass.class.getMethod("testMethod1");
        dbUnitModule.insertDataSet(testMethod, testObject);
        dbUnitModule.assertDbContentAsExpected(testMethod, testObject);
    }


    /**
     * Test for default annotation on method-level and no annotation on class-level
     */
    @Test
    public void testEqualWithCustomName() throws Exception {
        Object testObject = new TestClass();
        Method testMethod = TestClass.class.getMethod("testMethod2");
        dbUnitModule.insertDataSet(testMethod, testObject);
        dbUnitModule.assertDbContentAsExpected(testMethod, testObject);
    }


    /**
     * Test for custom annotation on method-level and no annotation on class-level
     */
    @Test
    public void testNoClassAndMethodDataSet() throws Exception {
        Object testObject = new TestClass();
        Method testMethod = TestClass.class.getMethod("testMethod3");
        dbUnitModule.insertDataSet(testMethod, testObject);
        dbUnitModule.assertDbContentAsExpected(testMethod, testObject);
    }


    /**
     * Test class without a class level expected dataset
     * The data set annotations are used to initialize the database content.
     */
    public class TestClass {

        @DataSet("ExpectedDataSetNoAnnotationOnClassLevelTest$TestClass.testMethod1-result.xml")
        @ExpectedDataSet
        public void testMethod1() {
        }

        @DataSet("CustomDataSet.xml")
        @ExpectedDataSet("CustomDataSet.xml")
        public void testMethod2() {
        }

        public void testMethod3() {
        }
    }


    /**
     * Utility method to create the test table.
     */
    private void createTestTable() throws SQLException {
        executeUpdate("create table TEST(dataset varchar(100))");
    }


    /**
     * Removes the test database table
     */
    private void dropTestTable() throws SQLException {
        try {
            executeUpdate("drop table TEST");
        } catch (UnitilsException e) {
            // Ignored
        }
    }


}