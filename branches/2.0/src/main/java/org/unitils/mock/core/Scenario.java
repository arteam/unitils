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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 *
 */
public class Scenario {
	
	/**
	 * Insertion ordered map that keeps track of the registered invocations and whether or not they have already been checked for invocation with the various assertX() methods in this class.
	 */
	private final Map<Invocation, Boolean> observedInvocations = new LinkedHashMap<Invocation, Boolean>();
	
	private boolean assertNoMoreInvocations = false;
	
	public void registerInvocation(Invocation invocation) {
		if (assertNoMoreInvocations) {
			throw new AssertionError(getNoMoreInvocationsErrorMessage(invocation));
		}
		observedInvocations.put(invocation, Boolean.FALSE);
	}

	
	public List<Invocation> getObservedInvocations() {
		return new ArrayList<Invocation>(observedInvocations.keySet());
	}
	
	
	public void assertInvoked(InvocationMatcher invocationMatcher) {
		for (Entry<Invocation, Boolean> registeredInvocationEntry: this.observedInvocations.entrySet()) {
			if(!registeredInvocationEntry.getValue() && invocationMatcher.matches(registeredInvocationEntry.getKey())) {
				registeredInvocationEntry.setValue(Boolean.TRUE);
				return;
			}
		}
		throw new AssertionError(getAssertInvokedErrorMessage(findMatchingMethodName(invocationMatcher.getMethod()), invocationMatcher));
	}
	
	
	public void assertNotInvoked(InvocationMatcher invocationMatcher) {
		for (Entry<Invocation, Boolean> registeredInvocationEntry: this.observedInvocations.entrySet()) {
			if(!registeredInvocationEntry.getValue() && invocationMatcher.matches(registeredInvocationEntry.getKey())) {
				throw new AssertionError(getAssertNotInvokedErrorMessage(registeredInvocationEntry.getKey(), invocationMatcher));
			}
		}
	}
	
	public void assertNoMoreInvocation() {
		assertNoMoreInvocations = true;
	}
	
	
	protected String getAssertNotInvokedErrorMessage(Invocation invocation, InvocationMatcher invocationMatcher) {
		final StringBuffer message = new StringBuffer();
		final Method method = invocationMatcher.getMethod();
		message.append("Prohibited invocation of ")
			.append(MethodUtils.getCompleteRepresentation(method))
			.append(" at ")
			.append(invocation.getStackTrace().length > 1 ? invocation.getStackTrace()[1] : "(unknown source)");
		return message.toString();
	}
	
	
	protected String getAssertInvokedErrorMessage(Invocation key, InvocationMatcher invocationMatcher) {
		final StringBuffer message = new StringBuffer();
		final Method method = invocationMatcher.getMethod();
		message.append("Expected invocation of ")
			.append(MethodUtils.getCompleteRepresentation(method))
			.append(", but ");
		final Invocation matchedInvocation = findMatchingMethodName(method);
		if(matchedInvocation != null) {
			Method matchedMethod = matchedInvocation.getMethod();
			message.append(MethodUtils.getCompleteRepresentation(matchedMethod))
			.append(" was called (probably with different or non-matching arguments).");
		} else {
			message.append("the invocation didn't occur.");
		} 
		return message.toString();
	}
	
	
	protected String getNoMoreInvocationsErrorMessage(Invocation invocation) {
		final StringBuffer message = new StringBuffer();
		final Method method = invocation.getMethod();
		message.append("No more invocations expected, but ")
			.append(MethodUtils.getCompleteRepresentation(method))
			.append(" was called from ")
			.append(invocation.getStackTrace().length > 1 ? invocation.getStackTrace()[1] : "(unknown source)");
		return message.toString();
	}

	
	protected Invocation findMatchingMethodName(Method method) {
		for (Entry<Invocation, Boolean> registeredInvocationEntry: this.observedInvocations.entrySet()) {
			if(registeredInvocationEntry.getKey().getMethod().getName().equals(method.getName())) {
				return registeredInvocationEntry.getKey();
			}
		}
		return null;
	}
}
