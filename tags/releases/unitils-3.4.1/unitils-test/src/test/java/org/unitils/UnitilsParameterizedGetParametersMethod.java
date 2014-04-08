package org.unitils;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;
import org.unitils.UnitilsParameterized.TestClassRunnerForParameters;
import org.unitils.parameterized.UnitilsParametersNullParametersTest;

public class UnitilsParameterizedGetParametersMethod {

	private UnitilsParameterized unitilsParameterized;

	private TestClassRunnerForParameters sut;
	private List<Object[]> parameters;

	@Before
	public void init() throws Throwable {
		parameters = new ArrayList<Object[]>();
		//unitilsParameterized = new UnitilsParameterized(ParameterizedIntegrationTest.class);
	}

	@Test(expected = Exception.class)
	public void testNoParameters() throws Throwable {
		unitilsParameterized = new UnitilsParameterized(TestclassNoParameters.class);
		unitilsParameterized.getParametersMethod(new TestClass(TestclassNoParameters.class));
	}
	@Test(expected = Exception.class)
	public void testNoStaticNotPublic() throws Throwable {
		unitilsParameterized = new UnitilsParameterized(TestclassNotStaticNotPublic.class);
		unitilsParameterized.getParametersMethod(new TestClass(TestclassNotStaticNotPublic.class));
	}
	
	@Test(expected = Exception.class)
	public void testNotPublic() throws Throwable {
		unitilsParameterized = new UnitilsParameterized(TestclassNotPublic.class);
		unitilsParameterized.getParametersMethod(new TestClass(TestclassNotPublic.class));
	}
	
	@Test(expected = Exception.class)
	public void testNotStatic() throws Throwable {
		unitilsParameterized = new UnitilsParameterized(TestclassNotStatic.class);
		unitilsParameterized.getParametersMethod(new TestClass(TestclassNotStatic.class));
	}
	
	@Test
	public void testOk() throws Throwable {
		unitilsParameterized = new UnitilsParameterized(UnitilsParametersNullParametersTest.class);
		FrameworkMethod method = unitilsParameterized.getParametersMethod(new TestClass(UnitilsParametersNullParametersTest.class));
		Assert.assertNotNull(method);
	}
	
	

	private static class TestclassNoParameters {

		private void method1(){
			//do nothing
		}
	}

	private static class TestclassNotStaticNotPublic {

		@Parameters
		private void method1(){
			//do nothing
		}
	}
	
	private static class TestclassNotPublic {

		@Parameters
		private static void method1(){
			//do nothing
		}
	}
	
	private static class TestclassNotStatic {

		@Parameters
		public void method1(){
			//do nothing
		}
	}
}
