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

import org.unitils.mock.core.action.ExceptionThrowingAction;
import org.unitils.mock.core.action.ValueReturningAction;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 *
 */
public class MockBehaviorBuilder {

	private MockObject<?> mockObject;
	
	private Action action;
	
	private Boolean matchAlways;
	
	private InvocationMatcherBuilder invocationMatcherBuilder = InvocationMatcherBuilder.getInstance();
	
	private static MockBehaviorBuilder instance;
	
	public static MockBehaviorBuilder getInstance() {
		if (instance == null) {
			instance = new MockBehaviorBuilder();
		}
		return instance;
	}
	
	
	private MockBehaviorBuilder() {
	}
	
	
	public <T> void registerMockObject(MockObject<T> mockObject) {
		this.mockObject = mockObject;
	}
	
	
	public MockObject<?> getMockObject() {
		return mockObject;
	}
	

	public void registerReturnValue(Object returnValue, Boolean matchAlways) {
		registerPerformedAction(new ValueReturningAction(returnValue), matchAlways);
	}
	
	
	public void registerThrownException(Throwable exception, boolean matchAlways) {
		registerPerformedAction(new ExceptionThrowingAction(exception), matchAlways);
	}
	
	
	public void registerPerformedAction(Action action, boolean matchAlways) {
		this.action = action;
		this.matchAlways = matchAlways;
	}
	
	
	public void registerArgumentMatcher(ArgumentMatcher argumentMatcher) {
		invocationMatcherBuilder.registerArgumentMatcher(argumentMatcher);
	}


	public void registerInvokedMethod(Invocation invocation) {
		invocationMatcherBuilder.registerInvokedMethod(invocation);
		// TODO create list of argument matchers
		InvocationMatcher invocationMatcher = invocationMatcherBuilder.createInvocationMatcher();
		MockBehavior mockBehavior = new MockBehavior(invocationMatcher, action);
		if (matchAlways) {
			mockObject.registerAlwaysMatchingMockBehavior(mockBehavior);
		} else {
			mockObject.registerOneTimeMatchingMockBehavior(mockBehavior);
		}
		reset();
	}


	protected void reset() {
		mockObject = null;
		action = null;
		matchAlways = null;
		invocationMatcherBuilder.reset();
	}


}
