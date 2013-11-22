package org.unitils.dbunit;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.unitils.core.Unitils;
import org.unitils.dbunit.dataset.ColumnComparisonTest;


/**
 * DbunitModuleNaming.
 * 
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * @author Willemijn Wouters
 * 
 * @since 3.4
 * 
 */
public class DbunitModuleNaming {
    
    private DbUnitModule sut;
    
    @Before
    public void setUp() {
        sut = new DbUnitModule();
        sut.init((Properties) Unitils.getInstance().getConfiguration().clone());
    }

    /**
     * Test method for {@link org.unitils.dbunit.DbUnitModule#getCorrectFileName(java.lang.Class, java.lang.reflect.Method, java.lang.String)}.
     */
    @Test
    public void testGetCorrectFileNameOnMethodLevel() throws SecurityException, NoSuchMethodException {
        JustAClass obj = new JustAClass();
        
        String actual = sut.getCorrectFileName(obj.getClass(), obj.getClass().getMethod("method1"), "xml");
        String expected = "JustAClass-method1.xml";
        
        Assert.assertEquals(expected, actual);
    }
    
    /**
     * Test method for {@link org.unitils.dbunit.DbUnitModule#getCorrectFileName(java.lang.Class, java.lang.reflect.Method, java.lang.String)}.
     */
    @Test
    public void testGetCorrectFileNameOnClassLevel() throws SecurityException, NoSuchMethodException {
        ExpectedDataSetWithPrimaryKeysTest obj = new ExpectedDataSetWithPrimaryKeysTest();
        
        String actual = sut.getCorrectFileName(obj.getClass(), obj.getClass().getMethod("setUp"), "xml");
        String expected = "ExpectedDataSetWithPrimaryKeysTest.xml";
        
        Assert.assertEquals(expected, actual);
    }

    /**
     * Test method for {@link org.unitils.dbunit.DbUnitModule#getDefaultDataSetFileNameClassLevel(java.lang.Class, java.lang.String)}.
     */
    @Test
    public void testGetDefaultDataSetFileNameClassLevel_Innerclass() {
        TestClass1 obj = new TestClass1();
        String actual = sut.getDefaultDataSetFileNameClassLevel(obj.getClass(), "xml");
        String expected = "DbunitModuleNaming$TestClass1.xml";
        Assert.assertEquals(expected, actual);
    }
    
    @Test
    public void testGetDefaultDataSetFileNameClassLevel() {
        ColumnComparisonTest obj = new ColumnComparisonTest();
        String actual = sut.getDefaultDataSetFileNameClassLevel(obj.getClass(), "xml");
        String expected = "ColumnComparisonTest.xml";
        Assert.assertEquals(expected, actual);
    }

    /**
     * Test method for {@link org.unitils.dbunit.DbUnitModule#getDefaultDataSetFileNameMethodLevel(java.lang.Class, java.lang.reflect.Method, java.lang.String)}.
     */
    @Test
    public void testGetDefaultDataSetFileNameMethodLevel_InnerClass() throws SecurityException, NoSuchMethodException {
        TestClass1 obj = new TestClass1();
        String actual = sut.getDefaultDataSetFileNameMethodLevel(obj.getClass(), obj.getClass().getMethod("testMethod"), "xml");
        String expected = "DbunitModuleNaming$TestClass1-testMethod.xml";
        Assert.assertEquals(expected, actual);
    }
    
    /**
     * Test method for {@link org.unitils.dbunit.DbUnitModule#getDefaultDataSetFileNameMethodLevel(java.lang.Class, java.lang.reflect.Method, java.lang.String)}.
     */
    @Test
    public void testGetDefaultDataSetFileNameMethodLevel() throws SecurityException, NoSuchMethodException {
        ColumnComparisonTest obj = new ColumnComparisonTest();
        String actual = sut.getDefaultDataSetFileNameMethodLevel(obj.getClass(), obj.getClass().getMethod("equalStringValue"), "xml");
        String expected = "ColumnComparisonTest-equalStringValue.xml";
        Assert.assertEquals(expected, actual);
    }
    
    private class TestClass1 {
        
        public void testMethod() {
            
        }
    }
    
    

}
