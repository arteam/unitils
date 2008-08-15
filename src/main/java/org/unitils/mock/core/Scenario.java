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

import org.unitils.mock.util.MethodFormatUtil;

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
 */
public class Scenario {

    /**
     * Insertion ordered map that keeps track of the registered invocations and whether or not they have already been checked for invocation with the various assertX() methods in this class.
     */
    private final Map<Invocation, Boolean> observedInvocations = new LinkedHashMap<Invocation, Boolean>();

    private List<InvocationMatcher> alwaysMatchingMockBehaviorInvocationMatchers = new ArrayList<InvocationMatcher>();
    private List<InvocationMatcher> oneTimeMatchingMockBehaviorInvocationMatchers = new ArrayList<InvocationMatcher>();

    public void registerInvocation(Invocation invocation) {
        observedInvocations.put(invocation, Boolean.FALSE);
    }


    public void registerAlwaysMatchingMockBehaviorInvocationMatcher(InvocationMatcher invocationMatcher) {
        alwaysMatchingMockBehaviorInvocationMatchers.add(invocationMatcher);
    }


    public void registerOneTimeMatchingMockBehaviorInvocationMatcher(InvocationMatcher invocationMatcher) {
        oneTimeMatchingMockBehaviorInvocationMatchers.add(invocationMatcher);
    }


    public List<Invocation> getObservedInvocations() {
        return new ArrayList<Invocation>(observedInvocations.keySet());
    }


    public void assertInvoked(InvocationMatcher invocationMatcher) {
        for (Entry<Invocation, Boolean> registeredInvocationEntry : this.observedInvocations.entrySet()) {
            if (!registeredInvocationEntry.getValue() && invocationMatcher.matches(registeredInvocationEntry.getKey())) {
                registeredInvocationEntry.setValue(Boolean.TRUE);
                return;
            }
        }
        throw new AssertionError(getAssertInvokedErrorMessage(findMatchingMethodName(invocationMatcher.getMethod()), invocationMatcher));
    }


    public void assertNotInvoked(InvocationMatcher invocationMatcher) {
        for (Entry<Invocation, Boolean> registeredInvocationEntry : this.observedInvocations.entrySet()) {
            if (!registeredInvocationEntry.getValue() && invocationMatcher.matches(registeredInvocationEntry.getKey())) {
                throw new AssertionError(getAssertNotInvokedErrorMessage(registeredInvocationEntry.getKey(), invocationMatcher));
            }
        }
    }

    public void assertNoMoreInvocations() {
        // create a copy of the oneTimeMatchingMockBehaviorInvocationMatchers so that the method can be called repeatedly.
        List<InvocationMatcher> onceMatchingInvocationMatchers = new ArrayList<InvocationMatcher>(oneTimeMatchingMockBehaviorInvocationMatchers);
        List<InvocationMatcher> alwaysMatchingInvocationMatchers = alwaysMatchingMockBehaviorInvocationMatchers;
        for (Entry<Invocation, Boolean> registeredInvocationEntry : this.observedInvocations.entrySet()) {
            Invocation invocation = registeredInvocationEntry.getKey();
            if (!registeredInvocationEntry.getValue() && !isMatchForAlwaysMatchingMockBehavior(invocation, alwaysMatchingInvocationMatchers) && !isMatchForOnceMatchingMockBehavior(invocation, onceMatchingInvocationMatchers)) {
                throw new AssertionError(getNoMoreInvocationsErrorMessage(invocation));
            }
        }
    }

    protected boolean isMatchForOnceMatchingMockBehavior(Invocation invocation, List<InvocationMatcher> invocationMatchers) {
        for (InvocationMatcher invocationMatcher : invocationMatchers) {
            if (invocationMatcher.matches(invocation)) {
                oneTimeMatchingMockBehaviorInvocationMatchers.remove(invocation);
                return true;
            }
        }
        return false;
    }


    protected boolean isMatchForAlwaysMatchingMockBehavior(Invocation invocation, List<InvocationMatcher> invocationMatchers) {
        for (InvocationMatcher invocationMatcher : invocationMatchers) {
            if (invocationMatcher.matches(invocation)) {
                return true;
            }
        }
        return false;
    }

    protected String getAssertNotInvokedErrorMessage(Invocation invocation, InvocationMatcher invocationMatcher) {
        StringBuilder message = new StringBuilder();
        Method method = invocationMatcher.getMethod();
        message.append("Prohibited invocation of ");
        message.append(MethodFormatUtil.getCompleteRepresentation(method));
        message.append(" at ");
        message.append(invocation.getInvokedAt());
        return message.toString();
    }


    protected String getAssertInvokedErrorMessage(Invocation matchedInvocation, InvocationMatcher invocationMatcher) {
        StringBuilder message = new StringBuilder();
        Method method = invocationMatcher.getMethod();
        message.append("Expected invocation of ");
        message.append(MethodFormatUtil.getCompleteRepresentation(method));
        message.append(", but ");
        if (matchedInvocation != null) {
            message.append("it was called with different or non-matching arguments.");
        } else {
            message.append("the invocation didn't occur.");
        }
        return message.toString();
    }


    protected String getNoMoreInvocationsErrorMessage(Invocation invocation) {
        StringBuilder message = new StringBuilder();
        Method method = invocation.getMethod();
        message.append("No more invocations expected, but ");
        message.append(MethodFormatUtil.getCompleteRepresentation(method));
        message.append(" was called from ");
        message.append(invocation.getInvokedAt());
        return message.toString();
    }


    protected Invocation findMatchingMethodName(Method method) {
        for (Entry<Invocation, Boolean> registeredInvocationEntry : this.observedInvocations.entrySet()) {
            if (!registeredInvocationEntry.getValue() && registeredInvocationEntry.getKey().getMethod().getName().equals(method.getName())) {
                return registeredInvocationEntry.getKey();
            }
        }
        return null;
    }
}
