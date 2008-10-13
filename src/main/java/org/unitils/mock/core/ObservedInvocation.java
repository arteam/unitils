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

import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.proxy.ProxyInvocation;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class ObservedInvocation extends ProxyInvocation {

    private String mockName;

    private Object result;

    private BehaviorDefiningInvocation behaviorDefiningInvocation;

    private MockBehavior mockBehavior;


    /**
     * Creates an invocation.
     *
     * The argumentsAtInvocationTime should be copies (deep clones) of the arguments at the time of
     * the invocation. This way the original values can still be used later-on even when changes
     * occur to the original values (pass-by-value vs pass-by-reference).
     *
     * @param proxy                      The proxy on which the method was called, not null
     * @param mockName                   The name of the mock, e.g. the field name, not null
     * @param method                     The method that was called, not null
     * @param arguments                  The arguments that were used by reference, not null
     * @param argumentsAtInvocationTime  The copies of the arguments at the time that they were used, not null
     * @param invokedAt                  The location of the invocation, not null
     * @param behaviorDefiningInvocation The invocation that defined the behavior, null if there is no behavior
     * @param mockBehavior               The executed behavior, not null
     */
    public ObservedInvocation(Object proxy, String mockName, Method method, List<Object> arguments, List<Object> argumentsAtInvocationTime,
                              StackTraceElement invokedAt, BehaviorDefiningInvocation behaviorDefiningInvocation, MockBehavior mockBehavior) {
        super(proxy, method, arguments, argumentsAtInvocationTime, invokedAt);
        this.mockName = mockName;
        this.behaviorDefiningInvocation = behaviorDefiningInvocation;
        this.mockBehavior = mockBehavior;
    }


    public void setResult(Object result) {
        this.result = result;
    }


    public String getMockName() {
        return mockName;
    }


    public Object getResult() {
        return result;
    }


    public BehaviorDefiningInvocation getBehaviorDefiningInvocation() {
        return behaviorDefiningInvocation;
    }


    public MockBehavior getMockBehavior() {
        return mockBehavior;
    }


    public boolean hasMockBehavior() {
        return mockBehavior != null;
    }
}