/*
 * Copyright 2008,  Unitils.org
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
import static org.unitils.easymock.EasyMockUnitils.replay;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import org.unitils.easymock.annotation.Mock;
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
	
	MockObject<TestClass> testClassMock; 
	
	MockObject<TestInterface> testInterfaceMock;
	
	Action defaultAction;
	
	@Mock
	InvocationMatcher invocationMatcher;

	
	@Before
	public void setup() {
		scenario = new Scenario();
		
		testClassMock = new MockObject<TestClass>("testClassMock", TestClass.class, false, scenario);
		MockObjectProxyMethodInterceptor<TestClass> testClassMethodInterceptor = new MockObjectProxyMethodInterceptor<TestClass>(testClassMock);
		testClass = ProxyUtils.createProxy(testClassMethodInterceptor, TestClass.class);
		
		testInterfaceMock = new MockObject<TestInterface>("testInterfaceMock", TestInterface.class, false, scenario);
		MockObjectProxyMethodInterceptor<TestInterface> testInterfaceMethodInterceptor = new MockObjectProxyMethodInterceptor<TestInterface>(testInterfaceMock);
		testInterface = ProxyUtils.createProxy(testInterfaceMethodInterceptor, TestInterface.class);
		
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
		testInterfaceMock.registerAlwaysMatchingMockBehavior(valueReturningBehavior);
		
		assertEquals("returnedValue", testInterface.getSomeString());
	}
	
	
	@Test(expected = UnitilsException.class)
	public void testExceptionThrowingAction() throws Exception {
		UnitilsException unitilsException = new UnitilsException("test exception");
		MockBehavior exceptionThrowingBehavior = new MockBehavior(invocationMatcher, new ExceptionThrowingAction(unitilsException));
		testClassMock.registerAlwaysMatchingMockBehavior(exceptionThrowingBehavior);
		
		testClass.doSomething(null);
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
