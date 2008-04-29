package org.unitils.mock.core;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.core.argumentmatcher.NotNullArgumentMatcher;

public class InvocationMatcherTest extends UnitilsJUnit4 {
	
	Method invocationMatcherTesterMethodOnNothing;
	Method invocationMatcherTesterMethodOnInt;
	Method invocationMatcherTesterMethodOnObject;
	Method invocationMatcherTesterMethodOnTwoObjects;
	
	InvocationMatcher invocationMatcherOnNothing;
	InvocationMatcher invocationMatcherOnInt;
	InvocationMatcher invocationMatcherOnObject;
	InvocationMatcher invocationMatcherOnTwoObjects;
	
	List<?> noArguments;
	List<?> intArgument;
	List<?> objectArgument;
	List<?> twoObjectArguments;
	
	@Before
	public void setup() {
		final Method[] methods = InvocationMatcherTester.class.getMethods();
		for (Method method: methods) {
			if("doSomethingWithNothing".equals(method.getName())) {
				invocationMatcherTesterMethodOnNothing = method;
			} else if ("doSomethingWithInt".equals(method.getName())){
				invocationMatcherTesterMethodOnInt = method;
			} else if ("doSomethingWithObject".equals(method.getName())){
				invocationMatcherTesterMethodOnObject = method;
			} else if ("doSomethingWithTwoObjects".equals(method.getName())){
				invocationMatcherTesterMethodOnTwoObjects = method;
			}
		}
		invocationMatcherOnNothing = new InvocationMatcher(invocationMatcherTesterMethodOnNothing);
		invocationMatcherOnInt = new InvocationMatcher(invocationMatcherTesterMethodOnInt, new NotNullArgumentMatcher());
		invocationMatcherOnObject = new InvocationMatcher(invocationMatcherTesterMethodOnObject, new NotNullArgumentMatcher());
		invocationMatcherOnTwoObjects = new InvocationMatcher(invocationMatcherTesterMethodOnTwoObjects, new NotNullArgumentMatcher(), new NotNullArgumentMatcher());
		
		noArguments = Collections.emptyList();
		intArgument = Arrays.asList(new int[] { 0 });
		objectArgument = Arrays.asList(new Object[] { new Object() });
		twoObjectArguments = Arrays.asList(new Object[] { new Object(), new Object() });
	}

	@Test
	public void testInvocationMatcherWithDifferentMethod() {
		assertFalse(invocationMatcherOnNothing.matches(new Invocation(null, invocationMatcherTesterMethodOnInt, null, noArguments, null)));
	}
	
	@Test
	public void testInvocationMatcherWithDifferentNumberOfParams() {
		assertFalse(invocationMatcherOnNothing.matches(new Invocation(null, invocationMatcherTesterMethodOnNothing, null, intArgument, null)));
		assertFalse(invocationMatcherOnNothing.matches(new Invocation(null, invocationMatcherTesterMethodOnNothing, null, twoObjectArguments, null)));
	}
	
	@Test
	public void testInvocationMatcherWithCorrectParams() {
		assertTrue(invocationMatcherOnNothing.matches(new Invocation(null, invocationMatcherTesterMethodOnNothing, null, noArguments, null)));
		assertTrue(invocationMatcherOnInt.matches(new Invocation(null, invocationMatcherTesterMethodOnInt, null, objectArgument, null)));
		assertTrue(invocationMatcherOnObject.matches(new Invocation(null, invocationMatcherTesterMethodOnObject, null, objectArgument, null)));
		assertTrue(invocationMatcherOnTwoObjects.matches(new Invocation(null, invocationMatcherTesterMethodOnTwoObjects, null, twoObjectArguments, null)));
	}
	
	static class InvocationMatcherTester {
		public void doSomethingWithNothing() {
		}
		
		public void doSomethingWithInt(int i) {
		}
		
		public void doSomethingWithObject(Object o) {
		}
		
		public void doSomethingWithTwoObjects(Object o1, Object o2) {
		}
	}
}
