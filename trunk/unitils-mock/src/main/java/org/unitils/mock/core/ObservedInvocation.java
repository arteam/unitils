/*
 * Copyright 2013,  Unitils.org
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

import org.unitils.mock.core.proxy.ProxyInvocation;
import org.unitils.mock.mockbehavior.MockBehavior;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 * @author Kenny Claes
 */
public class ObservedInvocation extends ProxyInvocation {

    protected Object result;
    protected Object resultAtInvocationTime;
    protected BehaviorDefiningInvocation behaviorDefiningInvocation;
    protected MockBehavior mockBehavior;

    /**
     * Creates a observed invocation for the given prosy invocation.
     *
     * @param proxyInvocation            The proxy invocation, not null
     * @param behaviorDefiningInvocation The invocation that defined the behavior, null if there is no behavior
     * @param mockBehavior               The executed behavior, not null
     */
    public ObservedInvocation(ProxyInvocation proxyInvocation, BehaviorDefiningInvocation behaviorDefiningInvocation, MockBehavior mockBehavior) {
        super(proxyInvocation);
        this.behaviorDefiningInvocation = behaviorDefiningInvocation;
        this.mockBehavior = mockBehavior;
    }

    /**
     * Sets the result of the invocation.
     * This is set afterwards to make it possible to get the correct sequence in the report when there are nested mock
     * invocations.
     *
     * @param result                 The result of the invocation (pass by reference)
     * @param resultAtInvocationTime A copy of the result at the time of invocation (pass by value)
     */
    public void setResult(Object result, Object resultAtInvocationTime) {
        this.result = result;
        this.resultAtInvocationTime = resultAtInvocationTime;
    }

    public Object getResult() {
        return result;
    }

    public Object getResultAtInvocationTime() {
        return resultAtInvocationTime;
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