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
package org.unitils.mock.core.proxy.impl;

import org.unitils.core.UnitilsException;
import org.unitils.mock.core.BehaviorDefiningInvocation;
import org.unitils.mock.core.BehaviorDefiningInvocations;
import org.unitils.mock.core.ObservedInvocation;
import org.unitils.mock.core.Scenario;
import org.unitils.mock.core.proxy.ProxyInvocation;
import org.unitils.mock.core.proxy.ProxyInvocationHandler;
import org.unitils.mock.core.util.CloneService;
import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.mockbehavior.ValidatableMockBehavior;
import org.unitils.mock.mockbehavior.impl.DefaultValueReturningMockBehavior;


public class MockProxyInvocationHandler<T> implements ProxyInvocationHandler {

    protected BehaviorDefiningInvocations behaviorDefiningInvocations;
    protected Scenario scenario;
    protected CloneService cloneService;
    protected MatchingProxyInvocationHandler matchingProxyInvocationHandler;


    public MockProxyInvocationHandler(BehaviorDefiningInvocations behaviorDefiningInvocations, Scenario scenario, CloneService cloneService, MatchingProxyInvocationHandler matchingProxyInvocationHandler) {
        this.behaviorDefiningInvocations = behaviorDefiningInvocations;
        this.scenario = scenario;
        this.cloneService = cloneService;
        this.matchingProxyInvocationHandler = matchingProxyInvocationHandler;
    }


    public Object handleInvocation(ProxyInvocation proxyInvocation) throws Throwable {
        matchingProxyInvocationHandler.assertPreviousMatchingInvocationCompleted();

        BehaviorDefiningInvocation behaviorDefiningInvocation = behaviorDefiningInvocations.getMatchingBehaviorDefiningInvocation(proxyInvocation);
        MockBehavior mockBehavior = getValidMockBehavior(proxyInvocation, behaviorDefiningInvocation);

        ObservedInvocation observedInvocation = new ObservedInvocation(proxyInvocation, behaviorDefiningInvocation, mockBehavior);
        scenario.addObservedInvocation(observedInvocation);

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


    protected MockBehavior getValidMockBehavior(ProxyInvocation proxyInvocation, BehaviorDefiningInvocation behaviorDefiningInvocation) {
        MockBehavior mockBehavior;
        if (behaviorDefiningInvocation != null) {
            mockBehavior = behaviorDefiningInvocation.getMockBehavior();
        } else {
            mockBehavior = getDefaultMockBehavior(proxyInvocation);
        }
        if (mockBehavior instanceof ValidatableMockBehavior) {
            try {
                ValidatableMockBehavior validatableMockBehavior = (ValidatableMockBehavior) mockBehavior;
                validatableMockBehavior.assertCanExecute(proxyInvocation);
            } catch (UnitilsException e) {
                if (behaviorDefiningInvocation != null) {
                    e.setStackTrace(behaviorDefiningInvocation.getInvokedAtTrace());
                }
                throw e;
            }
        }
        return mockBehavior;
    }

    protected MockBehavior getDefaultMockBehavior(ProxyInvocation proxyInvocation) {
        if (proxyInvocation.isVoidMethod()) {
            return null;
        }
        return new DefaultValueReturningMockBehavior();
    }
}