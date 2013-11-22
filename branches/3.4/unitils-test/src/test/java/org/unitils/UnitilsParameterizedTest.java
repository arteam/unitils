package org.unitils;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.internal.runners.TestClass;
import org.junit.runner.RunWith;
import org.unitils.UnitilsParameterized.TestClassRunnerForParameters;
import org.unitils.UnitilsParameterized.UnitilsMethodValidator;
import org.unitils.parameterized.JustATestClass;
import org.unitils.parameterized.UnitilsParametersNullParametersStveParametersTest;
import org.unitils.util.ReflectionUtils;

/**
 * Parameterized runner.
 * 
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * @author Willemijn Wouters
 * 
 * @since 3.4
 * 
 */

@SuppressWarnings("deprecation")
@RunWith(UnitilsJUnit4TestClassRunner.class)
public class UnitilsParameterizedTest {

    @Test
    public void testValidateTestMethods() throws Throwable {

        UnitilsMethodValidator methodValidator = new UnitilsMethodValidator(new TestClass(JustATestClass.class));
        methodValidator.validateTestMethods(Test.class, false);
        Assert.assertEquals(getErrors().get(0).getMessage(), methodValidator.getErrors().get(0).getMessage());
        Assert.assertEquals(getErrors().get(1).getMessage(), methodValidator.getErrors().get(1).getMessage());
        Assert.assertEquals(getErrors().get(2).getMessage(), methodValidator.getErrors().get(2).getMessage());
        Assert.assertEquals(getErrors().get(3).getMessage(), methodValidator.getErrors().get(3).getMessage());
    }

    @Test
    public void testGetParametersMethod() {
        UnitilsMethodValidator methodValidator = new UnitilsMethodValidator(new TestClass(Testclass2.class));
        methodValidator.validateInstanceMethods();
        Assert.assertTrue(methodValidator.getErrors() != null && !methodValidator.getErrors().isEmpty());
        Assert.assertEquals("No runnable methods", methodValidator.getErrors().get(0).getMessage());
    }

    @Test(expected = Exception.class)
    public void testComputeParams() throws Throwable {
        List<Object[]> data = new ArrayList<Object[]>();
        List<int[]> data2 = new ArrayList<int[]>();
        data2.add(new int[]{1});
        data2.add(new int[]{1, 2});
        data2.add(new int[]{1, 2, 3});
        TestClassRunnerForParameters runner = new UnitilsParameterized(UnitilsParametersNullParametersStveParametersTest.class).new TestClassRunnerForParameters(UnitilsParametersNullParametersStveParametersTest.class, data, 1);   
        ReflectionUtils.setFieldValue(runner, "fParameterList", data2);
        runner.computeParams();
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetName() throws Throwable {
        List<Object[]> data = new ArrayList<Object[]>();
        List<int[]> data2 = new ArrayList<int[]>();
        data2.add(new int[]{1});
        data2.add(new int[]{1, 2});
        data2.add(new int[]{1, 2, 3});
        TestClassRunnerForParameters runner = new UnitilsParameterized(UnitilsParametersNullParametersStveParametersTest.class).new TestClassRunnerForParameters(UnitilsParametersNullParametersStveParametersTest.class, data, 6);   
        ReflectionUtils.setFieldValue(runner, "fParameterList", data2);
        runner.getName();
    }
    
    @Test
	public void testValidateArgConstructorNoParameters() throws Exception {
		UnitilsMethodValidator validator = new UnitilsMethodValidator(new TestClass(JustATestClass.class));
		validator.validateArgConstructor();
		Assert.assertFalse(validator.getErrors().isEmpty());
	}

    @Test
   	public void testValidateArgConstructorWithParameters() throws Exception {
   		UnitilsMethodValidator validator = new UnitilsMethodValidator(new TestClass(Testclass3.class));
   		validator.validateArgConstructor();
   		Assert.assertTrue(validator.getErrors().isEmpty());
   	}
    
    private List<Throwable> getErrors() {
        List<Throwable> lst = new ArrayList<Throwable>();
        lst.add(new Exception("Method test1() should not be static"));
        lst.add(new Exception("Method test2 should be public"));
        lst.add(new Exception("Method test3 should be void"));
        lst.add(new Exception("Method test4 should have no parameters"));
        return lst;
    }

    private class Testclass2 {
        //just an empty testclass
    }
    
    private class Testclass3 {
    	
    	public Testclass3() {
    		//do nothing
    	}
    }
    
    private class TestClass4 {
    	public TestClass4(int i) {
    		//do nothing
    	}
    }

}
