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


public class MockInvocationHandler<T> implements ProxyInvocationHandler {

    protected BehaviorDefiningInvocations oneTimeMatchingBehaviorDefiningInvocations;
    protected BehaviorDefiningInvocations alwaysMatchingBehaviorDefiningInvocations;
    protected Scenario scenario;
    protected CloneService cloneService;


    public MockInvocationHandler(BehaviorDefiningInvocations oneTimeMatchingBehaviorDefiningInvocations, BehaviorDefiningInvocations alwaysMatchingBehaviorDefiningInvocations, Scenario scenario, CloneService cloneService) {
        this.oneTimeMatchingBehaviorDefiningInvocations = oneTimeMatchingBehaviorDefiningInvocations;
        this.alwaysMatchingBehaviorDefiningInvocations = alwaysMatchingBehaviorDefiningInvocations;
        this.scenario = scenario;
        this.cloneService = cloneService;
    }


    public Object handleInvocation(ProxyInvocation proxyInvocation) throws Throwable {
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
        MockBehavior mockBehavior;
        if (behaviorDefiningInvocation != null) {
            mockBehavior = behaviorDefiningInvocation.getMockBehavior();
        } else {
            mockBehavior = getDefaultMockBehavior(proxyInvocation);
        }
        if (mockBehavior instanceof ValidatableMockBehavior) {
            ValidatableMockBehavior validatableMockBehavior = (ValidatableMockBehavior) mockBehavior;
            validatableMockBehavior.assertCanExecute(proxyInvocation);
        }
        return mockBehavior;
    }

    protected MockBehavior getDefaultMockBehavior(ProxyInvocation proxyInvocation) {
        if (proxyInvocation.getMethod().getReturnType() == Void.TYPE) {
            return null;
        }
        return new DefaultValueReturningMockBehavior();
    }
}