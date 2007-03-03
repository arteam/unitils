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

import junit.framework.AssertionFailedError;
import static org.unitils.thirdparty.org.apache.commons.dbutils.DbUtils.closeQuietly;
import org.unitils.UnitilsJUnit3;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.UnitilsException;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.annotation.ExpectedDataSet;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Test class for using expected data sets in the {@link DbUnitModule}.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DbUnitModuleExpectedDataSetTest extends UnitilsJUnit3 {

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
        Method testMethod = ExpectedDataSetTest.class.getMethod("testMethod1");
        dbUnitModule.insertTestData(testMethod);
        dbUnitModule.assertDbContentAsExpected(testMethod);
    }


    /**
     * Test for custom annotation on method-level overriding default annotation on class-level
     */
    public void testInsertTestData_customMethodDataSet() throws Exception {
        Method testMethod = ExpectedDataSetTest.class.getMethod("testMethod2");
        dbUnitModule.insertTestData(testMethod);
        dbUnitModule.assertDbContentAsExpected(testMethod);
    }


    /**
     * Test for default annotation on method-level and default annotation on class-level
     */
    public void testInsertTestData_classAndMethodDataSet() throws Exception {
        Method testMethod = ExpectedDataSetTest.class.getMethod("testMethod3");
        dbUnitModule.insertTestData(testMethod);
        dbUnitModule.assertDbContentAsExpected(testMethod);
    }


    /**
     * Test for expected dataset that does not correspond to the content of the database. Assertion should
     * have failed.
     */
    public void testInsertTestData_differentContent() throws Exception {
        try {
            Method testMethod = ExpectedDataSetTest.class.getMethod("testMethod4");
            dbUnitModule.assertDbContentAsExpected(testMethod);
            fail("Expected AssertionFailedError");
        } catch (AssertionFailedError e) {
            //expected
        }
    }


    /**
     * Test for default file that is not found
     */
    public void testInsertTestData_notFound() throws Exception {
        try {
            Method testMethod = ExpectedDataSetTest.class.getMethod("testNotFound1");
            dbUnitModule.assertDbContentAsExpected(testMethod);
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
            Method testMethod = ExpectedDataSetTest.class.getMethod("testNotFound2");
            dbUnitModule.assertDbContentAsExpected(testMethod);
            fail("Expected UnitilsException");
        } catch (UnitilsException e) {
            //expected
        }
    }


    /**
     * Test for default annotation on method-level and no annotation on class-level
     */
    public void testInsertTestData_noClassDataSet() throws Exception {
        Method testMethod = ExpectedDataSetTestNoClassLevel.class.getMethod("testMethod1");
        dbUnitModule.insertTestData(testMethod);
        dbUnitModule.assertDbContentAsExpected(testMethod);
    }


    /**
     * Test for custom annotation on method-level and no annotation on class-level
     */
    public void testInsertTestData_noClassDataSetCustomMethodDataSet() throws Exception {
        Method testMethod = ExpectedDataSetTestNoClassLevel.class.getMethod("testMethod2");
        dbUnitModule.insertTestData(testMethod);
        dbUnitModule.assertDbContentAsExpected(testMethod);
    }


    /**
     * Test for custom annotation on method-level and no annotation on class-level
     */
    public void testInsertTestData_noClassAndMethodDataSet() throws Exception {
        Method testMethod = ExpectedDataSetTestNoClassLevel.class.getMethod("testMethod3");
        dbUnitModule.insertTestData(testMethod);
        dbUnitModule.assertDbContentAsExpected(testMethod);
    }


    /**
     * Test for no annotation on method-level and custom annotation on class-level
     */
    public void testInsertTestData_customClassDataSet() throws Exception {
        Method testMethod = ExpectedDataSetTestCustomClassLevel.class.getMethod("testMethod1");
        dbUnitModule.insertTestData(testMethod);
        dbUnitModule.assertDbContentAsExpected(testMethod);
    }


    /**
     * Test for default annotation on method-level overriding a custom annotation on class-level
     */
    public void testInsertTestData_customClassDataSetOverridenByDefault() throws Exception {
        Method testMethod = ExpectedDataSetTestCustomClassLevel.class.getMethod("testMethod2");
        dbUnitModule.insertTestData(testMethod);
        dbUnitModule.assertDbContentAsExpected(testMethod);
    }

    /**
     * Test class with a class level expected dataset.
     * The data set annotations are used to initialize the database content.
     */
    @ExpectedDataSet
    public class ExpectedDataSetTest {

        @DataSet("DbUnitModuleExpectedDataSetTest$ExpectedDataSetTest.testMethod1-result.xml")
        public void testMethod1() {
        }

        @DataSet("CustomDataSet.xml")
        @ExpectedDataSet("CustomDataSet.xml")
        public void testMethod2() {
        }

        @DataSet("DbUnitModuleExpectedDataSetTest$ExpectedDataSetTest.testMethod3-result.xml")
        @ExpectedDataSet
        public void testMethod3() {
        }

        @DataSet("CustomDataSet.xml")
        @ExpectedDataSet
        public void testMethod4() {
        }

        public void testNotFound1() {
        }

        @ExpectedDataSet("xxxxxx.xml")
        public void testNotFound2() {
        }
    }

    /**
     * Test class without a class level expected dataset
     * The data set annotations are used to initialize the database content.
     */
    public class ExpectedDataSetTestNoClassLevel {

        @DataSet("DbUnitModuleExpectedDataSetTest$ExpectedDataSetTestNoClassLevel.testMethod1-result.xml")
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
     * Test class with a custom class level expected dataset
     * The data set annotations are used to initialize the database content.
     */
    @ExpectedDataSet("CustomDataSet.xml")
    public class ExpectedDataSetTestCustomClassLevel {

        @DataSet("CustomDataSet.xml")
        public void testMethod1() {
        }

        @DataSet("DbUnitModuleExpectedDataSetTest$ExpectedDataSetTestCustomClassLevel.testMethod2-result.xml")
        @ExpectedDataSet
        public void testMethod2() {
        }
    }


    /**
     * Utility method to create the test table.
     */
    private void createTestTable() throws SQLException {
        Connection conn = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            st.execute("create table test (dataset varchar(100))");
        } finally {
            closeQuietly(conn, st, null);
        }
    }


    /**
     * Removes the test database table
     */
    private void dropTestTable() throws SQLException {
        Connection conn = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            try {
                st.executeUpdate("drop table test");
            } catch (SQLException e) {
                // Ignored
            }
        } finally {
            closeQuietly(conn, st, null);
        }
    }


}
