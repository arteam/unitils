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
package org.unitils.mock.core.matching.impl;

import org.unitils.mock.Mock;
import org.unitils.mock.argumentmatcher.ArgumentMatcher;
import org.unitils.mock.core.BehaviorDefiningInvocation;
import org.unitils.mock.core.BehaviorDefiningInvocations;
import org.unitils.mock.core.MockFactory;
import org.unitils.mock.core.matching.MatchingInvocationHandler;
import org.unitils.mock.core.proxy.ProxyInvocation;
import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.mockbehavior.impl.ChainedMockBehavior;

import java.util.List;


/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class BehaviorDefiningMatchingInvocationHandler implements MatchingInvocationHandler {

    protected MockBehavior mockBehavior;
    protected boolean oneTimeMatch;
    protected BehaviorDefiningInvocations behaviorDefiningInvocations;
    protected MockFactory mockFactory;


    public BehaviorDefiningMatchingInvocationHandler(MockBehavior mockBehavior, boolean oneTimeMatch, BehaviorDefiningInvocations behaviorDefiningInvocations, MockFactory mockFactory) {
        this.mockBehavior = mockBehavior;
        this.oneTimeMatch = oneTimeMatch;
        this.behaviorDefiningInvocations = behaviorDefiningInvocations;
        this.mockFactory = mockFactory;
    }


    public Object handleInvocation(ProxyInvocation proxyInvocation, List<ArgumentMatcher> argumentMatchers) {
        if (mockBehavior instanceof ChainedMockBehavior) {
            ((ChainedMockBehavior) mockBehavior).installChain();
        }
        BehaviorDefiningInvocation behaviorDefiningInvocation = new BehaviorDefiningInvocation(proxyInvocation, mockBehavior, argumentMatchers, oneTimeMatch);
        behaviorDefiningInvocations.addBehaviorDefiningInvocation(behaviorDefiningInvocation);
        return createChainedMock(proxyInvocation, behaviorDefiningInvocation);
    }


    protected Object createChainedMock(ProxyInvocation proxyInvocation, BehaviorDefiningInvocation behaviorDefiningInvocation) {
        Class<?> innerMockType = proxyInvocation.getMethod().getReturnType();
        String innerMockName = proxyInvocation.getProxyName() + "." + proxyInvocation.getMethod().getName();

        Mock<?> mock = mockFactory.createChainedMock(innerMockName, innerMockType);
        if (mock == null) {
            return null;
        }
        return mock.performs(new ChainedMockBehavior(mock, behaviorDefiningInvocation));
    }
}
