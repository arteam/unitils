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
import static org.junit.Assert.fail;
import static org.unitils.database.SQLUnitils.executeUpdate;
import static org.unitils.database.SQLUnitils.executeUpdateQuietly;
import static org.unitils.database.SQLUnitils.getItemAsLong;
import static org.unitils.database.SQLUnitils.getItemAsString;
import static org.unitils.database.SQLUnitils.getItemsAsStringSet;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;

import java.io.File;
import java.util.Properties;
import java.util.Set;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.unitils.database.DataSourceWrapper;
import org.unitils.database.DatabaseModule;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.datasetfactory.impl.MultiSchemaXmlDataSetFactory;
import org.unitils.dbunit.datasetloadstrategy.impl.CleanInsertLoadStrategy;

/**
 * Test class for loading of data sets using the {@link DbUnitModule}.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DbUnitModuleDataSetTest extends UnitilsJUnit4 {

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
        
        dbUnitModule.databaseName = "";

        dropTestTable();
        createTestTables();
    }


    /**
     * Clean-up test database.
     */
    @After
    public void tearDown() throws Exception {
        dropTestTable();
    }


    /**
     * Test for loading the default data set for a given class.
     */
    @Test
    public void testInsertDefaultDataSet() throws Exception {
        dbUnitModule.insertDefaultDataSet(DataSetTestSubClass_dataSetAnnotationOnSubClass.class);
        assertLoadedDataSet("DbUnitModuleDataSetTest$DataSetTestSubClass_dataSetAnnotationOnSubClass.xml");
    }


    /**
     * Test for custom annotation on method-level overriding default annotation on class-level
     */
    @Test
    public void testInsertDataSet_customMethodDataSet() throws Exception {
        dbUnitModule.insertDataSet(DataSetTest.class.getMethod("testMethod2"), new DataSetTest());
        assertLoadedDataSet("CustomDataSet.xml");
    }


    /**
     * Test for default file that is not found
     */
    @Test
    public void testInsertDataSet_notFound() throws Exception {
        try {
            dbUnitModule.insertDataSet(DataSetTest.class.getMethod("testNotFound1"), new DataSetTest());
            fail("Expected UnitilsException");
        } catch (UnitilsException e) {
            //expected
        }
    }


    /**
     * Test for custom file that is not found
     */
    @Test
    public void testInsertDataSet_customNotFound() throws Exception {
        try {
            dbUnitModule.insertDataSet(DataSetTest.class.getMethod("testNotFound2"), new DataSetTest());
            fail("Expected UnitilsException");
        } catch (UnitilsException e) {
            //expected
        }
    }


    /**
     * Test for custom annotation on method-level and no annotation on class-level
     */
    @Test
    public void testInsertDataSet_noClassDataSetCustomMethodDataSet() throws Exception {
        dbUnitModule.insertDataSet(DataSetTestNoClassLevel.class.getMethod("testMethod2"), new DataSetTestNoClassLevel());
        assertLoadedDataSet("CustomDataSet.xml");
    }


    /**
     * Test for no annotation on method-level and no annotation on class-level.
     * No data set should have been loaded.
     */
    @Test
    public void testInsertDataSet_noClassAndMethodDataSet() throws Exception {
        dbUnitModule.insertDataSet(DataSetTestNoClassLevel.class.getMethod("testMethod3"), new DataSetTestNoClassLevel());
        Set<String> datasets = getItemsAsStringSet("select dataset from test", dataSource);
        assertTrue(datasets.isEmpty()); // nothing loaded
    }


    /**
     * Test for no annotation on method-level and custom annotation on class-level
     */
    @Test
    public void testInsertDataSet_customClassDataSet() throws Exception {
        dbUnitModule.insertDataSet(DataSetTestCustomClassLevel.class.getMethod("testMethod1"), new DataSetTestCustomClassLevel());
        assertLoadedDataSet("CustomDataSet.xml");
    }

    @Test
    public void testInsertDataSet_defaultPackageDataSet() throws Exception {
        Class<?> defaultPackageDataSetClass = Class.forName("DefaultPackageDataSet");
        Object testInstance = defaultPackageDataSetClass.newInstance();
        dbUnitModule.insertDataSet(defaultPackageDataSetClass.getMethod("testMethod1"), testInstance);
        assertLoadedDataSet("DefaultPackageDataSet.xml");
    }


    /**
     * Test for a direct call to insertDataSet.
     */
    @Test
    public void testInsertDataSet_directCall() throws Exception {
        File dataSetFile = new File(this.getClass().getResource("CustomDataSet.xml").getPath());
        dbUnitModule.insertDataSet(dataSetFile, MultiSchemaXmlDataSetFactory.class, CleanInsertLoadStrategy.class);
        assertLoadedDataSet("CustomDataSet.xml");
    }


    /**
     * Test loading a data set containing 2 elements both specifying different columns.
     * This is a test for a bug in DbUnit that forces you to repeat all column names over and over again, even if
     * they should just be left null.
     */
    @Test
    public void testInsertDataSet_elementsWithDifferentColumns() throws Exception {
        File dataSetFile = new File(this.getClass().getResource("DifferentColumnsDataSet.xml").getPath());
        dbUnitModule.insertDataSet(dataSetFile, MultiSchemaXmlDataSetFactory.class, CleanInsertLoadStrategy.class);
        assertLenientEquals(3, getItemAsLong("select count(1) from test", dataSource));
    }

    // Tests for behavior when data is loaded for a superclass method, but the test instance is of a
    // subclass type

    /**
     * Test for a superclass method when the actual test instance is of a subtype, when the dataset
     * annotation is on the superclass method
     */
    @Test
    public void testInsertDataSet_methodOnSuperClassButInstanceOfSubClass_dataSetAnnotationOnMethod() throws Exception {
        dbUnitModule.insertDataSet(DataSetTestSuperclass.class.getMethod("annotatedTestMethod"), new DataSetTestSubClass_dataSetAnnotationOnSubClass());
        assertLoadedDataSet("DbUnitModuleDataSetTest$DataSetTestSubClass_dataSetAnnotationOnSubClass.xml");
    }


    /**
     * Test for a superclass method when the actual test instance is of a subtype, when the dataset
     * annotation is on the superclass method
     */
    @Test
    public void testInsertDataSet_methodOnSuperClassButInstanceOfSubClass_dataSetAnnotationOnSuperclass() throws Exception {
        dbUnitModule.insertDataSet(DataSetTestSuperclass_classLevelAnnotation.class.getMethod("testMethod"), new DataSetTestSubClass_dataSetAnnotationOnSuperClass());
        assertLoadedDataSet("DbUnitModuleDataSetTest$DataSetTestSubClass_dataSetAnnotationOnSuperClass.xml");
    }


    /**
     * Test for a superclass method when the actual test instance is of a subtype, when the dataset
     * annotation is on the superclass method
     */
    @Test
    public void testInsertDataSet_methodOnSuperClassButInstanceOfSubClass_dataSetAnnotationOnSubclass() throws Exception {
        dbUnitModule.insertDataSet(DataSetTestSuperclass.class.getMethod("testMethod"), new DataSetTestSubClass_dataSetAnnotationOnSubClass());
        assertLoadedDataSet("DbUnitModuleDataSetTest$DataSetTestSubClass_dataSetAnnotationOnSubClass.xml");
    }


    /**
     * Test for loading a dataset consisting of multiple dataset files
     * <p/>
     * todo: make possible to define records in the same table in the two datasets
     */
    @Test
    public void testInsertDataSet_multipleDataSets() throws Exception {
        dbUnitModule.insertDataSet(DataSetTest.class.getMethod("testMethodMultipleDataSets"), new DataSetTest());
        assertLoadedDataSet("dataSet1.xml");
        assertLenientEquals("dataSet2.xml", getItemAsString("select dataset from test1", dataSource));
    }


    /**
     * Utility method to assert that the correct data set was loaded.
     *
     * @param expectedDataSetName the name of the data set, not null
     */
    private void assertLoadedDataSet(String expectedDataSetName) throws Exception {
        String dataSet = getItemAsString("select dataset from test", dataSource);
        assertEquals(expectedDataSetName, dataSet);
    }


    /**
     * Utility method to create the test table.
     */
    private void createTestTables() {
        executeUpdate("create table test (dataset varchar(100), anotherColumn varchar(100))", dataSource);
        executeUpdate("create table test1 (dataset varchar(100))", dataSource);
    }


    /**
     * Removes the test database table
     */
    private void dropTestTable() {
        executeUpdateQuietly("drop table test", dataSource);
        executeUpdateQuietly("drop table test1", dataSource);
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

        @DataSet({"dataSet1.xml", "dataSet2.xml"})
        public void testMethodMultipleDataSets() {
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


    @DataSet("CustomDataSet.xml")
    public class DataSetTestCustomClassLevel {

        public void testMethod1() {
        }

        @DataSet
        public void testMethod2() {
        }
    }


    public class DataSetTestSuperclass {

        public void testMethod() {
        }

        @DataSet
        public void annotatedTestMethod() {
        }
    }

    @DataSet
    public class DataSetTestSuperclass_classLevelAnnotation {

        public void testMethod() {
        }
    }


    @DataSet
    public class DataSetTestSubClass_dataSetAnnotationOnSubClass extends DataSetTestSuperclass {
    }


    public class DataSetTestSubClass_dataSetAnnotationOnSuperClass extends DataSetTestSuperclass_classLevelAnnotation {
    }
    
    private String getDatabaseName(Properties config) {
        
        DatabaseModule databaseModule = Unitils.getInstance().getModulesRepository().getModuleOfType(DatabaseModule.class);
        DataSourceWrapper wrapper = databaseModule.getWrapper("");
        return wrapper.getDatabaseName();
        
        
    }

}
