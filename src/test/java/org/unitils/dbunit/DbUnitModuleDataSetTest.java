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

import org.unitils.UnitilsJUnit3;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.UnitilsException;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.datasetfactory.MultiSchemaXmlDataSetFactory;
import org.unitils.dbunit.datasetloadstrategy.CleanInsertLoadStrategy;

import javax.sql.DataSource;

import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

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

    /* Default dataset factory */
	private MultiSchemaXmlDataSetFactory dataSetFactory;


    /**
     * Initializes the test fixture.
     */
    @Override
	protected void setUp() throws Exception {
        super.setUp();

        Properties configuration = new ConfigurationLoader().loadConfiguration();
        dbUnitModule = new DbUnitModule();
        dbUnitModule.init(configuration);
        
        dataSetFactory = new MultiSchemaXmlDataSetFactory();
		dataSetFactory.init("PUBLIC");

        dropTestTable();
        createTestTable();
    }


    /**
     * Clean-up test database.
     */
    @Override
	protected void tearDown() throws Exception {
        super.tearDown();
        dropTestTable();
    }


    /**
     * Test for custom annotation on method-level overriding default annotation on class-level
     */
    public void testInsertTestData_customMethodDataSet() throws Exception {
        dbUnitModule.insertTestData(DataSetTest.class.getMethod("testMethod2"), new DataSetTest());
        assertLoadedDataSet("CustomDataSet.xml");
    }


    /**
     * Test for default file that is not found
     */
    public void testInsertTestData_notFound() throws Exception {
        try {
            dbUnitModule.insertTestData(DataSetTest.class.getMethod("testNotFound1"), new DataSetTest());
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
            dbUnitModule.insertTestData(DataSetTest.class.getMethod("testNotFound2"), new DataSetTest());
            fail("Expected UnitilsException");
        } catch (UnitilsException e) {
            //expected
        }
    }


    /**
     * Test for custom annotation on method-level and no annotation on class-level
     */
    public void testInsertTestData_noClassDataSetCustomMethodDataSet() throws Exception {
        dbUnitModule.insertTestData(DataSetTestNoClassLevel.class.getMethod("testMethod2"), new DataSetTestNoClassLevel());
        assertLoadedDataSet("CustomDataSet.xml");
    }


    /**
     * Test for no annotation on method-level and no annotation on class-level.
     * No data set should have been loaded.
     */
    public void testInsertTestData_noClassAndMethodDataSet() throws Exception {
        dbUnitModule.insertTestData(DataSetTestNoClassLevel.class.getMethod("testMethod3"), new DataSetTestNoClassLevel());
        Set<String> datasets = getItemsAsStringSet("select dataset from test", dataSource);
        assertTrue(datasets.isEmpty()); // nothing loaded
    }


    /**
     * Test for no annotation on method-level and custom annotation on class-level
     */
    public void testInsertTestData_customClassDataSet() throws Exception {
        dbUnitModule.insertTestData(DataSetTestCustomClassLevel.class.getMethod("testMethod1"), new DataSetTestCustomClassLevel());
        assertLoadedDataSet("CustomDataSet.xml");
    }


    /**
     * Test for a direct call to {@link DbUnitModule#insertTestData(java.io.InputStream,org.unitils.dbunit.datasetloadstrategy.DataSetLoadStrategy)}
     */
    public void testInsertTestData_directCall() throws Exception {
        InputStream dataSetIS = this.getClass().getResourceAsStream("CustomDataSet.xml");
		dbUnitModule.insertTestData(null, dataSetIS, new CleanInsertLoadStrategy(), dataSetFactory);
        assertLoadedDataSet("CustomDataSet.xml");
    }
    
    
    // Tests for behavior when data is loaded for a superclass method, but the test instance is of a
    // subclass type
    
    /**
     * Test for a superclass method when the actual test instance is of a subtype, when the dataset
     * annotation is on the superclass method
     */
    public void testInsertTestData_methodOnSuperClassButInstanceOfSubClass_dataSetAnnotationOnMethod() throws Exception {
    	dbUnitModule.insertTestData(DataSetTestSuperclass.class.getMethod("annotatedTestMethod"), new DataSetTestSubClass_dataSetAnnotationOnSubClass());
    	assertLoadedDataSet("DbUnitModuleDataSetTest$DataSetTestSubClass_dataSetAnnotationOnSubClass.xml");
    }
    
    
    /**
     * Test for a superclass method when the actual test instance is of a subtype, when the dataset
     * annotation is on the superclass method
     */
    public void testInsertTestData_methodOnSuperClassButInstanceOfSubClass_dataSetAnnotationOnSuperclass() throws Exception {
    	dbUnitModule.insertTestData(DataSetTestSuperclass_classLevelAnnotation.class.getMethod("testMethod"), new DataSetTestSubClass_dataSetAnnotationOnSuperClass());
    	assertLoadedDataSet("DbUnitModuleDataSetTest$DataSetTestSubClass_dataSetAnnotationOnSuperClass.xml");
    }
    
    
    /**
     * Test for a superclass method when the actual test instance is of a subtype, when the dataset
     * annotation is on the superclass method
     */
    public void testInsertTestData_methodOnSuperClassButInstanceOfSubClass_dataSetAnnotationOnSubclass() throws Exception {
    	dbUnitModule.insertTestData(DataSetTestSuperclass.class.getMethod("testMethod"), new DataSetTestSubClass_dataSetAnnotationOnSubClass());
    	assertLoadedDataSet("DbUnitModuleDataSetTest$DataSetTestSubClass_dataSetAnnotationOnSubClass.xml");
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

}
