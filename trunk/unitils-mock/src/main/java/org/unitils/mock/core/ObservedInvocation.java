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

import org.unitils.mock.core.proxy.ProxyInvocation;
import org.unitils.mock.mockbehavior.MockBehavior;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class ObservedInvocation extends ProxyInvocation {

    private Object resultAtInvocationTime;

    private BehaviorDefiningInvocation behaviorDefiningInvocation;

    private MockBehavior mockBehavior;


    /**
     * Creates a observed invocation for the given prosy invocation.
     *
     * The argumentsAtInvocationTime should be copies (deep clones) of the arguments at the time of
     * the invocation. This way the original values can still be used later-on even when changes
     * occur to the original values (pass-by-value vs pass-by-reference).
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


    public void setResultAtInvocationTime(Object result) {
        this.resultAtInvocationTime = result;
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