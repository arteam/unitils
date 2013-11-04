package org.unitils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.TestClass;
import org.junit.runner.Runner;
import org.junit.runners.Parameterized.Parameters;
import org.unitils.easymock.annotation.Mock;
import org.unitils.reflectionassert.ReflectionAssert;
import org.unitils.reflectionassert.ReflectionComparatorMode;


/**
 * UnitilsParameterizedTest.
 * 
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * @author Willemijn Wouters
 * 
 * @since 3.4
 * 
 */
public class UnitilsParameterizedTest {
    private UnitilsParameterized systemUnderTest;
    
    @Mock
    private TestClass mockTestClass;


    /**
     * Test method for {@link org.unitils.UnitilsParameterized#UnitilsParameterized(org.junit.internal.runners.TestClass)}.
     * @throws Exception 
     */
    @Test
    public void testUnitilsParameterized() throws Exception {
        systemUnderTest = new UnitilsParameterized(new TestClass(TestClass1Ok.class));
        List<Runner> actualRunners = systemUnderTest.getRunners();
        List<Runner> expected = getListRunnersTestClass1Ok();
        ReflectionAssert.assertReflectionEquals(expected, actualRunners, ReflectionComparatorMode.LENIENT_ORDER);
    }

    /**
     * Test method for {@link org.unitils.UnitilsParameterized#validateMethod()}.
     * @throws Exception 
     */
    @Test(expected = Exception.class)
    public void testValidateMethodStaticMethod() throws Exception {
        systemUnderTest = new UnitilsParameterized(new TestClass(TestClass2NOK.class));
        systemUnderTest.validateMethod();
    }
    
    @Test(expected = Exception.class)
    public void testValidateMethodInstanceMethod() throws Exception {
        systemUnderTest = new UnitilsParameterized(new TestClass(TestClass3NOK.class));
        systemUnderTest.validateMethod();
    }
    
    @Test
    public void testValidateOk() throws Exception {
        systemUnderTest = new UnitilsParameterized(new TestClass(TestClass1Ok.class));
        systemUnderTest.validateMethod();
    }

    /**
     * Test method for {@link org.unitils.UnitilsParameterized#getParametersList()}.
     * @throws Exception 
     */
    @Test
    public void testGetParametersList() throws Exception {
        systemUnderTest = new UnitilsParameterized(new TestClass(TestClass1Ok.class));
        Collection<?> actual = systemUnderTest.getParametersList();
        List<Object[]> expected = Arrays.asList(new Object[][] { { 1 }, { 2 }, { 3 }, { null } });
        ReflectionAssert.assertLenientEquals(expected, actual);
    }

    @Test
    public void testGetParametersMethodOK() throws Exception {
        systemUnderTest = new UnitilsParameterized(new TestClass(TestClass1Ok.class));
        Method actual = systemUnderTest.getParametersMethod();
        ReflectionAssert.assertLenientEquals(TestClass1Ok.class.getMethod("testMethod"), actual);
    }

    public static class TestClass1Ok {

        @Parameters
        public static Collection<Object[]> testMethod() {
            return Arrays.asList(new Object[][] { { 1 }, { 2 }, { 3 }, { null } });
        }
        
        @Test
        public void test1() {
            Assert.assertTrue(true);
        }
    }

    
    public static class TestClass2NOK {

        @Parameters
        public void testMethod() {
            //do nothing
        }
        
        @Test
        public void test1() {
            Assert.assertTrue(true);
        }
    }
    
    public static class TestClass3NOK {
        @Parameters
        public void testMethod() {
            //do nothing
        }
        
        public void test1() {
            Assert.assertTrue(true);
        }
    }
    
    private List<Runner> getListRunnersTestClass1Ok() throws InitializationError {
        List<Runner> lst = new ArrayList<Runner>();
        lst.add(new UnitilsParameterized.TestClassRunnerForParameters(TestClass.class, new Object[]{1}, 0));
        lst.add(new UnitilsParameterized.TestClassRunnerForParameters(TestClass.class, new Object[]{2}, 1));
        lst.add(new UnitilsParameterized.TestClassRunnerForParameters(TestClass.class, new Object[]{3}, 2));
        lst.add(new UnitilsParameterized.TestClassRunnerForParameters(TestClass.class, new Object[]{null}, 3));
        
        return lst;
    }

}
