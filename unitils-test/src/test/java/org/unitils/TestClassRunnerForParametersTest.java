package org.unitils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsParameterized.TestClassRunnerForParameters;
import org.unitils.reflectionassert.ReflectionAssert;


/**
 * TestClassRunnerForParametersTest.
 * 
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * @author Willemijn Wouters
 * 
 * @since 3.4
 * 
 */
public class TestClassRunnerForParametersTest {
    private JustATestClass testClass;
    private TestClassRunnerForParameters classUnderTest;
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        testClass = new JustATestClass(1, "test", 3.3D);
        classUnderTest = new UnitilsParameterized.TestClassRunnerForParameters(testClass.getClass(), getParametersForTest(), 1);

    }

    /**
     * Test method for {@link org.unitils.UnitilsParameterized.TestClassRunnerForParameters#getName()}.
     */
    @Test
    public void testGetName() {
        Assert.assertEquals("[1]", classUnderTest.getName());
    }

    /**
     * Test method for {@link org.unitils.UnitilsParameterized.TestClassRunnerForParameters#createTest()}.
     * @throws Exception 
     */
    @Test
    public void testCreateTest() throws Exception {
        Object obj = classUnderTest.createTest();
        Assert.assertTrue(obj instanceof JustATestClass);
        JustATestClass actual = (JustATestClass) obj;
        JustATestClass expected = new JustATestClass(2, "Test2", 2.2D);
        ReflectionAssert.assertLenientEquals(expected, actual);

    }

    /**
     * Test method for {@link org.unitils.UnitilsParameterized.TestClassRunnerForParameters#testName(java.lang.reflect.Method)}.
     * @throws NoSuchMethodException 
     * @throws SecurityException 
     */
    @Test
    public void testTestName() throws SecurityException, NoSuchMethodException {
        Method method = JustATestClass.class.getMethod("method1");
        Assert.assertEquals("method1[1]", classUnderTest.testName(method));
        
    }
    
    /**
     * Test method for {@link org.unitils.UnitilsParameterized.TestClassRunnerForParameters#getOnlyConstructor()}.
     * @throws Exception 
     */
    @Test(expected = AssertionError.class)
    public void testGetOnlyConstructorFailure() throws Exception {
        JustATestClass3 clazz = new JustATestClass3();
        classUnderTest = new UnitilsParameterized.TestClassRunnerForParameters(clazz.getClass(), getParametersForTest(), 1);
        classUnderTest.getOnlyConstructor();
    }
    
    @Test
    public void testGetOnlyConstructor() throws SecurityException, NoSuchMethodException {
        Constructor<?> actual = classUnderTest.getOnlyConstructor();
        Constructor<JustATestClass> expected = JustATestClass.class.getConstructor(Integer.class, String.class, Double.class);
        ReflectionAssert.assertLenientEquals(expected, actual);
    }


    private Object[][] getParametersForTest() {
        Object[][] temp = new Object[3][];
        temp[0] = new Object[]{1, "Test1", 1.1D};
        temp[1] = new Object[]{2, "Test2", 2.2D};
        temp[2] = new Object[]{3, "Test3", 3.3D};

        return temp;
    }

    @SuppressWarnings("unused")
    private class JustATestClass3 {
       
        private int i;
        private String j;
        private Double k;
        /**
         * @param i
         * @param j
         * @param k
         */
        public JustATestClass3(int i, String j, Double k) {
            super();
            this.i = i;
            this.j = j;
            this.k = k;
        }
        /**
         * 
         */
        public JustATestClass3() {
            super();
        }



    }

}
