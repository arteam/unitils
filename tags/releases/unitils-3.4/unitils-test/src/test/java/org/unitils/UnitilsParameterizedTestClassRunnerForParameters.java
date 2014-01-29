package org.unitils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsParameterized.TestClassRunnerForParameters;
import org.unitils.parameterized.ParameterizedIntegrationTest;
import org.unitils.reflectionassert.ReflectionAssert;

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

public class UnitilsParameterizedTestClassRunnerForParameters {
	
	private UnitilsParameterized unitilsParameterized;
	
	private TestClassRunnerForParameters sut;
	
	@Before
	public void init() throws Throwable {
		unitilsParameterized = new UnitilsParameterized(ParameterizedIntegrationTest.class);
	}

	@Test
	public void testOneParameter() throws Exception {
		List<Object[]> parameters = new ArrayList<Object[]>();
		parameters.add(new Object[]{null});
		sut = unitilsParameterized.new TestClassRunnerForParameters(Testclass1.class, parameters, 0);
	
		String actual = sut.getName();
		String expected = "dataset [null]";
		Assert.assertEquals(expected, actual);
	}
	
	
	@Test
	public void testMultipleParametersArrays() throws Exception {
		List<Object[]> parameters = new ArrayList<Object[]>();
		parameters.add(new Object[]{1,2});
		parameters.add(new Object[]{3,4});
		sut = unitilsParameterized.new TestClassRunnerForParameters(Testclass1.class, parameters, 1);
	
		String actual = sut.getName();
		String expected = "dataset [3,4]";
		Assert.assertEquals(expected, actual);
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public void testIndexOutOfBounds() throws Exception {
		List<Object[]> parameters = new ArrayList<Object[]>();
		sut = unitilsParameterized.new TestClassRunnerForParameters(Testclass1.class, parameters, 1);
	
		sut.getName();
	}
	
	@Test
	public void testTestName() throws Exception {
		Method method = Testclass1.class.getMethod("testMethod1");
		List<Object[]> parameters = new ArrayList<Object[]>();
		parameters.add(new Object[]{1,2});
		sut = unitilsParameterized.new TestClassRunnerForParameters(Testclass1.class, parameters, 0);
		
		String actual = sut.testName(method);
		String expected = "testMethod1[0]";
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void testComputeParams() throws Exception {
		List<Object[]> parameters = new ArrayList<Object[]>();
		parameters.add(new Object[]{1,2});
		sut = unitilsParameterized.new TestClassRunnerForParameters(Testclass1.class, parameters, 0);
		
		Object[] actual = sut.computeParams();
		Object[] expected = new Object[]{1,2};
		ReflectionAssert.assertLenientEquals(expected, actual);
	}
	
	
	private class Testclass1 {
		
		public void testMethod1() {
			//do nothing
		}
	}

}
