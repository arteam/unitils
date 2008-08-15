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

import static org.unitils.mock.MockUnitils.createMock;
import static org.unitils.mock.MockUnitils.mock;
import static org.unitils.mock.MockUnitils.notNull;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenEquals;

import java.util.Arrays;

import junit.framework.AssertionFailedError;

import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;
import org.unitils.mock.core.action.ExceptionThrowingAction;
import org.unitils.mock.core.action.ValueReturningAction;
import org.unitils.mock.core.argumentmatcher.NotNullArgumentMatcher;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class BehaviorDefinitionTest {

	private TestClass testClassMock;
	
	@Before
	public void setup() {
		testClassMock = createMock("testClassMock", TestClass.class, new Scenario());
		MockBehaviorBuilder.getInstance().reset();
	}
	
	@Test 
	public void defineBehavior_returns() throws Exception {
		mock(testClassMock).returns("test").doSomething(notNull(String.class), notNull(String.class));
		
		assertActionRegistered(new ValueReturningAction("test"), false);
	}
	
	
	@Test
	public void defineBehavior_raises() throws Exception {
		mock(testClassMock).raises(new UnitilsException()).doSomething(notNull(String.class), notNull(String.class));
		
		assertActionRegistered(new ExceptionThrowingAction(new UnitilsException()), false);
	}
	
	
	@Test
	public void defineBehavior_performs() throws Exception {
		Action action = new Action() {
			public Object execute(Invocation invocation) {
				return null;
			}
		};
		mock(testClassMock).performs(action).doSomething(notNull(String.class), notNull(String.class));
		
		assertActionRegistered(action, false);
	}
	
	
	@Test
	public void defineBehavior_AlwaysReturns() throws Exception {
		mock(testClassMock).alwaysReturns("test").doSomething(notNull(String.class), notNull(String.class));
		
		assertActionRegistered(new ValueReturningAction("test"), true);
	}
	
	
	@Test
	public void defineBehavior_AlwaysRaises() throws Exception {
		mock(testClassMock).alwaysRaises(new UnitilsException()).doSomething(notNull(String.class), notNull(String.class));
		
		assertActionRegistered(new ExceptionThrowingAction(new UnitilsException()), true);
	}
	
	
	@Test
	public void defineBehavior_AlwaysPerforms() throws Exception {
		Action action = new Action() {
			public Object execute(Invocation invocation) {
				return null;
			}
		};
		mock(testClassMock).alwaysPerforms(action).doSomething(notNull(String.class), notNull(String.class));
		
		assertActionRegistered(action, true);
	}

	
	@SuppressWarnings("unchecked")
	private void assertActionRegistered(Action action, boolean matchAlways) throws NoSuchMethodException,
			AssertionFailedError {
		MockObject<TestClass> mockObject = ((MockObjectProxy<TestClass>) testClassMock).$_$_getMockObject();
		MockBehavior mockBehavior;
		if (matchAlways) {
			mockBehavior = mockObject.getAlwaysMatchingMockBehaviors().get(0);
		} else {
			mockBehavior = mockObject.getOneTimeMatchingMockBehaviors().get(0);
		}
		MockBehavior expectedMockBehavior = new MockBehavior(
				new InvocationMatcher(TestClass.class.getMethod("doSomething", String.class, String.class), 
						Arrays.<ArgumentMatcher>asList(new NotNullArgumentMatcher(), new NotNullArgumentMatcher())), 
				action);
		assertLenEquals(expectedMockBehavior, mockBehavior);
	}
	
	static class TestClass {
		
		public void doSomething(String arg1, String arg2) {}
	}
}
