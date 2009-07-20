/*
 * Copyright 2006-2009,  Unitils.org
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

import org.unitils.mock.core.matching.MatchingInvocationBuilder;
import static org.unitils.mock.core.proxy.ProxyFactory.createProxy;
import org.unitils.mock.core.proxy.ProxyInvocation;
import org.unitils.mock.core.proxy.ProxyInvocationHandler;
import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.mockbehavior.ValidatableMockBehavior;
import org.unitils.mock.mockbehavior.impl.DefaultValueReturningMockBehavior;

public class MockProxy<T> {

    /* The mock proxy instance */
    protected T proxy;

    protected BehaviorDefiningInvocations oneTimeMatchingBehaviorDefiningInvocations;

    protected BehaviorDefiningInvocations alwaysMatchingBehaviorDefiningInvocations;

    /* The scenario that will record all observed invocations */
    protected Scenario scenario;

    protected MatchingInvocationBuilder matchingInvocationBuilder;


    public MockProxy(String mockName, Class<T> mockedType, BehaviorDefiningInvocations oneTimeMatchingBehaviorDefiningInvocations, BehaviorDefiningInvocations alwaysMatchingBehaviorDefiningInvocations, Scenario scenario, MatchingInvocationBuilder matchingInvocationBuilder) {
        this.oneTimeMatchingBehaviorDefiningInvocations = oneTimeMatchingBehaviorDefiningInvocations;
        this.alwaysMatchingBehaviorDefiningInvocations = alwaysMatchingBehaviorDefiningInvocations;
        this.scenario = scenario;
        this.matchingInvocationBuilder = matchingInvocationBuilder;
        this.proxy = createProxy(mockName, new InvocationHandler(), mockedType, Cloneable.class);
    }


    @SuppressWarnings("unchecked")
    public T getProxy() {
        return proxy;
    }


    protected Object handleMockInvocation(ProxyInvocation proxyInvocation) throws Throwable {
        matchingInvocationBuilder.assertNotExpectingInvocation();

        BehaviorDefiningInvocation behaviorDefiningInvocation = getMatchingBehaviorDefiningInvocation(proxyInvocation);
        MockBehavior mockBehavior = getMockBehavior(proxyInvocation, behaviorDefiningInvocation);

        Throwable throwable = null;
        Object result = null;
        try {
            result = performMockBehavior(proxyInvocation, mockBehavior);
        } catch (Throwable t) {
            throwable = t;
        }

        scenario.addObservedMockInvocation(new ObservedInvocation(result, proxyInvocation, behaviorDefiningInvocation, mockBehavior));
        if (throwable != null) {
            throw throwable;
        }
        return result;
    }


    protected Object performMockBehavior(ProxyInvocation proxyInvocation, MockBehavior mockBehavior) throws Throwable {
        if (mockBehavior == null) {
            return null;
        }
        // check whether the mock behavior can applied for this invocation
        if (mockBehavior instanceof ValidatableMockBehavior) {
            ((ValidatableMockBehavior) mockBehavior).assertCanExecute(proxyInvocation);
        }
        return mockBehavior.execute(proxyInvocation);
    }


    public BehaviorDefiningInvocation getMatchingBehaviorDefiningInvocation(ProxyInvocation proxyInvocation) throws Throwable {
        BehaviorDefiningInvocation behaviorDefiningInvocation = oneTimeMatchingBehaviorDefiningInvocations.getMatchingBehaviorDefiningInvocation(proxyInvocation);
        if (behaviorDefiningInvocation == null) {
            behaviorDefiningInvocation = alwaysMatchingBehaviorDefiningInvocations.getMatchingBehaviorDefiningInvocation(proxyInvocation);
        }
        return behaviorDefiningInvocation;
    }


    protected MockBehavior getMockBehavior(ProxyInvocation proxyInvocation, BehaviorDefiningInvocation behaviorDefiningInvocation) {
        MockBehavior mockBehavior;
        if (behaviorDefiningInvocation != null) {
            mockBehavior = behaviorDefiningInvocation.getMockBehavior();
        } else {
            mockBehavior = getDefaultMockBehavior(proxyInvocation);
        }

        if (mockBehavior instanceof ValidatableMockBehavior) {
            ((ValidatableMockBehavior) mockBehavior).assertCanExecute(proxyInvocation);
        }
        return mockBehavior;
    }


    protected MockBehavior getDefaultMockBehavior(ProxyInvocation proxyInvocation) {
        if (proxyInvocation.getMethod().getReturnType() == Void.TYPE) {
            return null;
        }
        return new DefaultValueReturningMockBehavior();
    }


    protected class InvocationHandler implements ProxyInvocationHandler {

        public Object handleInvocation(ProxyInvocation invocation) throws Throwable {
            return handleMockInvocation(invocation);
        }
    }

}