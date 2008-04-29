package org.unitils.mock.core;

import java.lang.reflect.Method;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.core.argumentmatcher.NotNullArgumentMatcher;

public class ScenarioTest extends UnitilsJUnit4 {
	private Scenario scenario;
	private Method testMethodDoSomething;
	private Method testMethodDoSomethingWithParam;
	private Object testObject = new TestObject();
	private InvocationMatcher doSomethingInvocationMatcher;
	private InvocationMatcher doSomethingWithParamInvocationMatcher;
	
	
	@Before
	public void setup() throws Exception {
		testMethodDoSomething = TestObject.class.getMethod("doSomething");
		testMethodDoSomethingWithParam = TestObject.class.getMethod("doSomething", Object.class);
		doSomethingInvocationMatcher = new InvocationMatcher(testMethodDoSomething);
		doSomethingWithParamInvocationMatcher = new InvocationMatcher(testMethodDoSomethingWithParam, new NotNullArgumentMatcher());
	}
	
	@Test
	public void testAssertInvoked() {
		scenario = new Scenario();
		scenario.registerInvocation(new Invocation(testObject, testMethodDoSomething, null, Collections.emptyList(), Thread.currentThread().getStackTrace()));
		scenario.assertInvoked(doSomethingInvocationMatcher);
		try {
			scenario.assertInvoked(doSomethingWithParamInvocationMatcher);
			throw new RuntimeException();
		} catch(AssertionError error) {
			// expected.
		}
		try {
			scenario.assertInvoked(doSomethingInvocationMatcher);
			throw new RuntimeException();
		} catch(AssertionError error) {
			// expected.
		}		
	}
	
	@Test
	public void testAssertNotInvoked() {
		scenario = new Scenario();
		scenario.registerInvocation(new Invocation(testObject, testMethodDoSomething, null, Collections.emptyList(), Thread.currentThread().getStackTrace()));
		scenario.assertNotInvoked(doSomethingWithParamInvocationMatcher);
		try {
			scenario.assertNotInvoked(doSomethingInvocationMatcher);
			throw new RuntimeException();
		} catch(AssertionError error) {
			// expected.
		}
		scenario.assertInvoked(doSomethingInvocationMatcher);
		scenario.assertNotInvoked(doSomethingInvocationMatcher);
	}
	
	@Test
	public void testAssertNoMoreInvocations() {
		scenario = new Scenario();
		scenario.registerInvocation(new Invocation(testObject, testMethodDoSomething, null, Collections.emptyList(), Thread.currentThread().getStackTrace()));
		scenario.assertNoMoreInvocation();
		try {
			scenario.registerInvocation(new Invocation(testObject, testMethodDoSomething, null, Collections.emptyList(), Thread.currentThread().getStackTrace()));
			throw new RuntimeException();
		} catch(AssertionError error) {
			// expected.
		}
	}
	
	static class TestObject {
		public void doSomething() {
		}
		
		public void doSomething(Object o) {
		}
	}
}
