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

import org.unitils.mock.argumentmatcher.ArgumentMatcher;
import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.mockbehavior.ValidatableMockBehavior;
import org.unitils.mock.proxy.ProxyInvocation;

import java.lang.reflect.Method;
import java.util.List;

/**
 * todo javadoc
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class BehaviorDefiningInvocation extends ProxyInvocation {

    /* The name of the mock, e.g. the field name */
    private String mockName;

    /* The argument matchers to use when matching the invocation */
    private List<ArgumentMatcher> argumentMatchers;

    /* The behavior to execute */
    private MockBehavior mockBehavior;

    /* True if this invocation was already matched and used before and should no longer be used */
    private boolean used;


    /**
     * Creates an invocation.
     *
     * The argumentsAtInvocationTime should be copies (deep clones) of the arguments at the time of
     * the invocation. This way the original values can still be used later-on even when changes
     * occur to the original values (pass-by-value vs pass-by-reference).
     *
     * @param proxy                     The proxy on which the method was called, not null
     * @param mockName                  The name of the mock, e.g. the field name, not null
     * @param method                    The method that was called, not null
     * @param arguments                 The arguments that were used by reference, not null
     * @param argumentsAtInvocationTime The copies of the arguments at the time that they were used, not null
     * @param invokedAt                 The location of the invocation, not null
     * @param argumentMatchers          The argument matchers to use when matching the invocation, not null
     * @param mockBehavior              The behavior to execute, not null
     */
    public BehaviorDefiningInvocation(Object proxy, String mockName, Method method, List<Object> arguments, List<Object> argumentsAtInvocationTime,
                                      StackTraceElement invokedAt, List<ArgumentMatcher> argumentMatchers, MockBehavior mockBehavior) {
        super(proxy, method, arguments, argumentsAtInvocationTime, invokedAt);
        this.mockName = mockName;
        this.argumentMatchers = argumentMatchers;
        this.mockBehavior = mockBehavior;
        this.used = false;

        // check whether the mock behavior can applied for this invocation
        if (mockBehavior instanceof ValidatableMockBehavior) {
            ((ValidatableMockBehavior) mockBehavior).assertCanExecute(this);
        }
    }


    /**
     * @return The name of the mock, e.g. the field name, not null
     */
    public String getMockName() {
        return mockName;
    }


    /**
     * @return The argument matchers to use when matching the invocation, not null
     */
    public List<ArgumentMatcher> getArgumentMatchers() {
        return argumentMatchers;
    }


    /**
     * @return The behavior to execute, not null
     */
    public MockBehavior getMockBehavior() {
        return mockBehavior;
    }


    /**
     * @return True if this invocation was already matched and used before and should no longer be used
     */
    public boolean isUsed() {
        return used;
    }


    public void markAsUsed() {
        this.used = true;
    }


    /**
     * Returns whether or not the given {@link ProxyInvocation} matches this object's predefined <code>Method</code> and arguments.
     *
     * @param proxyInvocation the {@link ProxyInvocation} to match.
     * @return true when given {@link org.unitils.mock.proxy.ProxyInvocation} matches, false otherwise.
     */
    public boolean matches(ProxyInvocation proxyInvocation) {
        if (!getMethod().equals(proxyInvocation.getMethod())) {
            return false;
        }
        List<?> arguments = proxyInvocation.getArguments();
        List<?> argumentsAtInvocationTime = proxyInvocation.getArgumentsAtInvocationTime();

        if (arguments.size() != argumentMatchers.size()) {
            return false;
        }

        for (int i = 0; i < arguments.size(); ++i) {
            Object argument = arguments.get(i);
            Object argumentAtInvocationTime = argumentsAtInvocationTime.get(i);
            if (!argumentMatchers.get(i).matches(argument, argumentAtInvocationTime)) {
                return false;
            }
        }
        return true;
    }


}