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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.unitils.mock.core.action.EmptyAction;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 *
 */
public class MockObject<T> {

	private String name;
	
	private Class<T> mockedClass;
	
	private Scenario scenario;

	private boolean executeActualImplementationByDefault;
	
	private List<MockBehavior> alwaysMatchingMockBehaviors = new ArrayList<MockBehavior>();
	
	private Map<MockBehavior, Boolean> oneTimeMatchingMockBehaviors = new LinkedHashMap<MockBehavior, Boolean>();
	

	public MockObject(String name, Class<T> mockedClass, boolean executeActualImplementationByDefault, Scenario scenario) {
		super();
		this.name = name;
		this.mockedClass = mockedClass;
		this.executeActualImplementationByDefault = executeActualImplementationByDefault;
		this.scenario = scenario;
	}

	
	public void registerInvocation(Invocation invocation) {
		scenario.registerInvocation(invocation);
	}
	
	
	public boolean isExecuteActualImplementation(Invocation invocation) {
		return executeActualImplementationByDefault && getOneTimeMatchingMockBehavior(invocation) == null
			&& getAlwaysMatchingMockBehavior(invocation) == null;
	}

	
	public Object executeBehavior(Invocation invocation) throws Throwable {
		// Check if there is a one-time matching behavior that hasn't been invoked yet
		MockBehavior oneTimeMatchingBehavior = getOneTimeMatchingMockBehavior(invocation);
		if (oneTimeMatchingBehavior != null) {
			oneTimeMatchingMockBehaviors.put(oneTimeMatchingBehavior, true);
			return oneTimeMatchingBehavior.execute(invocation);
		}
		
		// Check if there is an always-matching behavior
		MockBehavior alwaysMatchingBehavior = getAlwaysMatchingMockBehavior(invocation);
		if (alwaysMatchingBehavior != null) {
			return alwaysMatchingBehavior.execute(invocation);
		}
		// There's no matching behavior, simply execute the default one
		return EmptyAction.getInstance().execute(invocation);
	}
	
	
	protected MockBehavior getOneTimeMatchingMockBehavior(Invocation invocation) {
		for (MockBehavior behavior : oneTimeMatchingMockBehaviors.keySet()) {
			if (!oneTimeMatchingMockBehaviors.get(behavior) && behavior.matches(invocation)) {
				return behavior;
			}
		}
		return null;
	}
	
	
	protected MockBehavior getAlwaysMatchingMockBehavior(Invocation invocation) {
		for (MockBehavior behavior : alwaysMatchingMockBehaviors) {
			if (behavior.matches(invocation)) {
				return behavior;
			}
		}
		return null;
	}
	
	
	public void registerAlwaysMatchingMockBehavior(MockBehavior mockBehavior) {
		alwaysMatchingMockBehaviors.add(mockBehavior);
		scenario.registerAlwaysMatchingMockBehaviorInvocationMatcher(mockBehavior.getInvocationMatcher());
	}
	
	
	public void registerOneTimeMatchingMockBehavior(MockBehavior mockBehavior) {
		oneTimeMatchingMockBehaviors.put(mockBehavior, false);
		scenario.registerOneTimeMatchingMockBehaviorInvocationMatcher(mockBehavior.getInvocationMatcher());
	}
	
	
	public List<MockBehavior> getAlwaysMatchingMockBehaviors() {
		return alwaysMatchingMockBehaviors;
	}


	public List<MockBehavior> getOneTimeMatchingMockBehaviors() {
		return new ArrayList<MockBehavior>(oneTimeMatchingMockBehaviors.keySet());
	}


	public String getName() {
		return name;
	}


	public Class<T> getMockedClass() {
		return mockedClass;
	}

}
