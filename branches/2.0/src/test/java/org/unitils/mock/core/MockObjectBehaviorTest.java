/*
 * Copyright 2006-2007,  Unitils.org
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
package org.unitils.mock.core;

import static junit.framework.Assert.assertEquals;
import static org.easymock.EasyMock.expect;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import static org.unitils.easymock.EasyMockUnitils.*;

import org.unitils.core.UnitilsException;
import org.unitils.easymock.annotation.Mock;
import org.unitils.easymock.util.Calls;
import org.unitils.mock.core.action.ActualImplementationAction;
import org.unitils.mock.core.action.EmptyAction;
import org.unitils.mock.core.action.ExceptionThrowingAction;
import org.unitils.mock.core.action.ValueReturningAction;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class MockObjectBehaviorTest extends UnitilsJUnit4 {

	Scenario scenario;
	
	TestClass testClass;
	
	TestInterface testInterface;
	
	MockObject testClassMock, testInterfaceMock;
	
//	@Mock(calls = Calls.LENIENT)
	MockBehavior defaultMockBehavior;
	
	@Mock
	InvocationMatcher invocationMatcher;

	
	@Before
	public void setup() {
		scenario = new Scenario();
		defaultMockBehavior = new MockBehavior(new InvocationMatcher(), new EmptyAction());
		
		testClassMock = new MockObject(scenario, defaultMockBehavior);
		MockObjectProxyMethodInterceptor testClassMethodInterceptor = new MockObjectProxyMethodInterceptor(testClassMock);
		testClass = ProxyUtils.createProxy(TestClass.class, testClassMethodInterceptor);
		
		testInterfaceMock = new MockObject(scenario, defaultMockBehavior);
		MockObjectProxyMethodInterceptor testInterfaceMethodInterceptor = new MockObjectProxyMethodInterceptor(testInterfaceMock);
		testInterface = ProxyUtils.createProxy(TestInterface.class, testInterfaceMethodInterceptor);
		
		expect(invocationMatcher.matches(null)).andStubReturn(true);
		replay();
	}
	
	
	@Test
	public void testScenarioRecording() throws Exception {
		testClass.doSomething("test");
		testInterface.getSomeString();
		
		List<Invocation> observedInvocations = scenario.getObservedInvocations();
		assertEquals(2, observedInvocations.size());
		
		// todo fix and uncomment
//		assertLenEquals(Arrays.asList(
//				new Invocation(null, TestClass.class.getMethod("doSomething", String.class), Arrays.asList("test"), null),
//				new Invocation(null, TestInterface.class.getMethod("getSomeString"), Arrays.asList(), null)), 
//				observedInvocations);
	}
	
	
	@Test
	public void testValueReturningAction() throws Exception {
		MockBehavior valueReturningBehavior = new MockBehavior(invocationMatcher, new ValueReturningAction("returnedValue"));
		testInterfaceMock.addAlwaysMatchingMockBehavior(valueReturningBehavior);
		
		assertEquals("returnedValue", testInterface.getSomeString());
	}
	
	
	@Test(expected = UnitilsException.class)
	public void testExceptionThrowingAction() throws Exception {
		UnitilsException unitilsException = new UnitilsException("test exception");
		MockBehavior exceptionThrowingBehavior = new MockBehavior(invocationMatcher, new ExceptionThrowingAction(unitilsException));
		testClassMock.addAlwaysMatchingMockBehavior(exceptionThrowingBehavior);
		
		testClass.doSomething(null);
	}
	
	
	@Test
	public void testActualImplementationAction() throws Exception {
		MockBehavior actualImplementationBehavior = new MockBehavior(invocationMatcher, new ActualImplementationAction());
		testClassMock.addAlwaysMatchingMockBehavior(actualImplementationBehavior);
		
		assertEquals(false, testClass.doSomethingHasBeenInvoked);
		testClass.doSomething(null);
		assertEquals(true, testClass.doSomethingHasBeenInvoked);
	}
	
	
	public static class TestClass {
		
		boolean doSomethingHasBeenInvoked = false;
		
		public void doSomething(String param) {
			doSomethingHasBeenInvoked = true;
		}
		
	}
	
	
	public static interface TestInterface {
		
		public String getSomeString();
	}
}
