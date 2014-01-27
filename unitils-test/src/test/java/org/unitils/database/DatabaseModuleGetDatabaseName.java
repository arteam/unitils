package org.unitils.database;

import java.util.List;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.unitils.database.annotations.TestDataSource;


/**
 * Test {@link DatabaseModule}: method getDatabaseName.
 * 
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * @author Willemijn Wouters
 * 
 * @since 3.4
 * 
 */
public class DatabaseModuleGetDatabaseName {

    private DatabaseModule sut;

    @Before
    public void setUp() {
        sut = new DatabaseModule();
    }

    @Test
    public void testNameOnMethodNameNotEmpty() throws SecurityException, NoSuchMethodException {
        TestclassOnMethodTestDataSourceNotEmpty obj = new TestclassOnMethodTestDataSourceNotEmpty();

        List<String> actual = sut.getDatabaseName(obj, obj.getClass().getMethod("testMethod"));
        String expected = "database1";

        Assert.assertEquals(expected, actual.get(0));
    }

    @Test
    public void testNameOnMethodNameEmpty() throws SecurityException, NoSuchMethodException {
        TestclassOnMethodTestDataSourceEmpty obj = new TestclassOnMethodTestDataSourceEmpty();

        List<String> actual = sut.getDatabaseName(obj, obj.getClass().getMethod("testMethod"));

        Assert.assertTrue(actual.get(0).isEmpty());
    }
    
    @Test
    public void testNameOnFieldNotEmpty() throws SecurityException, NoSuchMethodException {
        TestclassOnFieldTestDataSourceNotEmpty obj = new TestclassOnFieldTestDataSourceNotEmpty();
        List<String> actual = sut.getDatabaseName(obj, obj.getClass().getMethod("testMethod"));
        String expected = "database1";

        Assert.assertEquals(expected, actual.get(0));
    }
    
    @Test
    public void testNameOnFieldEmpty() throws SecurityException, NoSuchMethodException {
        TestclassOnFieldTestDataSourceEmpty obj = new TestclassOnFieldTestDataSourceEmpty();
        List<String> actual = sut.getDatabaseName(obj, obj.getClass().getMethod("testMethod"));

        Assert.assertTrue(actual.get(0).isEmpty());
    }
    
    @Test
    public void testNameOnFieldAndOnMethod() throws SecurityException, NoSuchMethodException {
        TestclassOnMethodAndOnFieldTestDataSourceNotEmpty obj = new TestclassOnMethodAndOnFieldTestDataSourceNotEmpty();
        
        List<String> actual = sut.getDatabaseName(obj, obj.getClass().getMethod("testMethod"));
        String expected = "database2";

        Assert.assertEquals(expected, actual.get(0));
    }
    
    @Test
    public void testNameNoTestDatasource() throws SecurityException, NoSuchMethodException {
        TestclassWithoutTestDatasource obj = new TestclassWithoutTestDatasource();
        List<String> actual = sut.getDatabaseName(obj, obj.getClass().getMethod("testMethod"));

        Assert.assertNull(actual);
    
    }



    private class TestclassOnMethodTestDataSourceNotEmpty {

        @TestDataSource("database1")
        public void testMethod() {
            //do nothing
        }
    }

    private class TestclassOnMethodTestDataSourceEmpty {

        @TestDataSource
        public void testMethod() {
            //do nothing
        }
    }
    
    private class TestclassOnFieldTestDataSourceNotEmpty {
        @TestDataSource("database1")
        private DataSource dataSource;
        
        public void testMethod() {
            //do nothing
        }
    }
    
    private class TestclassOnFieldTestDataSourceEmpty {
        @TestDataSource()
        private DataSource dataSource;
        
        public void testMethod() {
            //do nothing
        }
    }
    
    private class TestclassOnMethodAndOnFieldTestDataSourceNotEmpty {
        @TestDataSource("database1")
        private DataSource dataSource;
        
        @TestDataSource("database2")
        public void testMethod() {
            //do nothing
        }
    }
    
    private class TestclassWithoutTestDatasource {
        private DataSource dataSource;
        
        public void testMethod() {
            //do nothing
        }
    }

}
