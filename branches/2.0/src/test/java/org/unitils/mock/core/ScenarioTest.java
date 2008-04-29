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
		scenario.registerInvocation(new Invocation(testObject, testMethodDoSomething, null, Collections.emptyList(), Thread.currentThread().getStackTrace()));
		scenario.assertInvoked(doSomethingInvocationMatcher);
		try {
			scenario.assertNoMoreInvocations();
			throw new RuntimeException();
		} catch(AssertionError error) {
			// expected.
		}
		
		scenario.assertInvoked(doSomethingInvocationMatcher);
		scenario.assertNoMoreInvocations();
		
		scenario.registerInvocation(new Invocation(testObject, testMethodDoSomething, null, Collections.emptyList(), Thread.currentThread().getStackTrace()));
		scenario.registerOneTimeMatchingMockBehaviorInvocationMatcher(doSomethingInvocationMatcher);
		scenario.assertNoMoreInvocations();
	}
	
	static class TestObject {
		public void doSomething() {
		}
		
		public void doSomething(Object o) {
		}
	}
}
