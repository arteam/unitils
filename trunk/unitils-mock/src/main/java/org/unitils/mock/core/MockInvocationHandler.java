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

import org.unitils.core.UnitilsException;
import org.unitils.mock.core.matching.MatchingInvocationBuilder;
import org.unitils.mock.core.proxy.CloneService;
import org.unitils.mock.core.proxy.ProxyInvocation;
import org.unitils.mock.core.proxy.ProxyInvocationHandler;
import org.unitils.mock.core.proxy.ProxyService;
import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.mockbehavior.ValidatableMockBehavior;
import org.unitils.mock.mockbehavior.impl.DefaultValueReturningMockBehavior;


public class MockInvocationHandler<T> implements ProxyInvocationHandler {

    protected BehaviorDefiningInvocations oneTimeMatchingBehaviorDefiningInvocations;
    protected BehaviorDefiningInvocations alwaysMatchingBehaviorDefiningInvocations;
    protected Scenario scenario;
    protected MatchingInvocationBuilder matchingInvocationBuilder; // todo td move to getMock()
    protected CloneService cloneService;


    public MockInvocationHandler(String mockName, Class<T> mockedType, BehaviorDefiningInvocations oneTimeMatchingBehaviorDefiningInvocations, BehaviorDefiningInvocations alwaysMatchingBehaviorDefiningInvocations, Scenario scenario, MatchingInvocationBuilder matchingInvocationBuilder, ProxyService proxyService, CloneService cloneService) {
        this.oneTimeMatchingBehaviorDefiningInvocations = oneTimeMatchingBehaviorDefiningInvocations;
        this.alwaysMatchingBehaviorDefiningInvocations = alwaysMatchingBehaviorDefiningInvocations;
        this.scenario = scenario;
        this.matchingInvocationBuilder = matchingInvocationBuilder;
        this.cloneService = cloneService;
    }


    public Object handleInvocation(ProxyInvocation proxyInvocation) throws Throwable {
        matchingInvocationBuilder.assertPreviousMatchingInvocationCompleted();

        BehaviorDefiningInvocation behaviorDefiningInvocation = getMatchingBehaviorDefiningInvocation(proxyInvocation);
        MockBehavior mockBehavior = getValidMockBehavior(proxyInvocation, behaviorDefiningInvocation);

        ObservedInvocation observedInvocation = new ObservedInvocation(proxyInvocation, behaviorDefiningInvocation, mockBehavior);
        scenario.addObservedMockInvocation(observedInvocation);

        Throwable throwable = null;
        Object result = null;
        if (mockBehavior != null) {
            try {
                result = mockBehavior.execute(proxyInvocation);
            } catch (Throwable t) {
                throwable = t;
            }
        }
        Object clonedResult = cloneService.createDeepClone(result);
        observedInvocation.setResult(result, clonedResult);

        if (throwable != null) {
            throw throwable;
        }
        return result;
    }


    protected BehaviorDefiningInvocation getMatchingBehaviorDefiningInvocation(ProxyInvocation proxyInvocation) throws Throwable {
        BehaviorDefiningInvocation behaviorDefiningInvocation = oneTimeMatchingBehaviorDefiningInvocations.getMatchingBehaviorDefiningInvocation(proxyInvocation);
        if (behaviorDefiningInvocation == null) {
            behaviorDefiningInvocation = alwaysMatchingBehaviorDefiningInvocations.getMatchingBehaviorDefiningInvocation(proxyInvocation);
        }
        return behaviorDefiningInvocation;
    }

    protected MockBehavior getValidMockBehavior(ProxyInvocation proxyInvocation, BehaviorDefiningInvocation behaviorDefiningInvocation) {
        if (behaviorDefiningInvocation != null) {
            MockBehavior mockBehavior = behaviorDefiningInvocation.getMockBehavior();
            assertCanExecute(mockBehavior, proxyInvocation, behaviorDefiningInvocation);
            return mockBehavior;
        }
        return getDefaultMockBehavior(proxyInvocation);
    }

    /**
     * Check whether the mock behavior can applied for this invocation
     *
     * @param mockBehavior               The behavior to verify, not null
     * @param proxyInvocation            The invocation, not null
     * @param behaviorDefiningInvocation The invocation that defined the behavior, not null
     */
    protected void assertCanExecute(MockBehavior mockBehavior, ProxyInvocation proxyInvocation, BehaviorDefiningInvocation behaviorDefiningInvocation) {
        if (!(mockBehavior instanceof ValidatableMockBehavior)) {
            return;
        }
        ValidatableMockBehavior validatableMockBehavior = (ValidatableMockBehavior) mockBehavior;
        try {
            validatableMockBehavior.assertCanExecute(proxyInvocation);
        } catch (UnitilsException e) {
            e.setStackTrace(behaviorDefiningInvocation.getInvokedAtTrace());
            throw e;
        }
    }

    protected MockBehavior getDefaultMockBehavior(ProxyInvocation proxyInvocation) {
        if (proxyInvocation.getMethod().getReturnType() == Void.TYPE) {
            return null;
        }
        return new DefaultValueReturningMockBehavior();
    }
}