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

import static org.unitils.reflectionassert.ReflectionAssert.assertLenEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.easymock.EasyMockUnitils;
import org.unitils.easymock.annotation.Mock;
import org.unitils.easymock.util.Calls;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class MockObjectBehaviorTest extends UnitilsJUnit4 {

	Scenario scenario;
	
	TestClass testClass;
	
	TestInterface testInterface;
	
	@Mock(calls = Calls.LENIENT)
	MockBehavior defaultMockBehavior;
	
	@Before
	public void setup() {
		scenario = new Scenario();
		MockObject testClassMock = new MockObject(scenario, defaultMockBehavior);
		MockObjectProxyMethodInterceptor testClassMethodInterceptor = new MockObjectProxyMethodInterceptor(testClassMock);
		testClass = ProxyUtils.createProxy(TestClass.class, testClassMethodInterceptor);
		
		MockObject testInterfaceMock = new MockObject(scenario, defaultMockBehavior);
		MockObjectProxyMethodInterceptor testInterfaceMethodInterceptor = new MockObjectProxyMethodInterceptor(testInterfaceMock);
		testInterface = ProxyUtils.createProxy(TestInterface.class, testInterfaceMethodInterceptor);
		
		EasyMockUnitils.replay();
	}
	
	@Test
	public void testMockObjectBehavior() throws Exception {
		testClass.doSomething("test");
		testInterface.getSomeString();
		
		List<Invocation> observedInvocations = scenario.getObservedInvocations();
		assertLenEquals(Arrays.asList(
				new Invocation(TestClass.class.getMethod("doSomething", String.class), Arrays.asList("test"), null),
				new Invocation(TestInterface.class.getMethod("getSomeString"), Arrays.asList(), null)), 
				observedInvocations);
	}
	
	public static class TestClass {
		
		public void doSomething(String param) {}
	}
	
	public static interface TestInterface {
		
		public String getSomeString();
	}
}
