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
import org.unitils.mock.mockbehavior.impl.ValueReturningMockBehavior;

import java.util.List;

import static org.unitils.core.util.ObjectFormatter.MOCK_NAME_CHAIN_SEPARATOR;


/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class BehaviorDefiningMatchingInvocationHandler implements MatchingInvocationHandler {

    protected MockBehavior mockBehavior;
    protected MockFactory mockFactory;
    protected BehaviorDefiningInvocations behaviorDefiningInvocations;


    public BehaviorDefiningMatchingInvocationHandler(MockBehavior mockBehavior, BehaviorDefiningInvocations behaviorDefiningInvocations, MockFactory mockFactory) {
        this.mockBehavior = mockBehavior;
        this.behaviorDefiningInvocations = behaviorDefiningInvocations;
        this.mockFactory = mockFactory;
    }


    public Object handleInvocation(ProxyInvocation proxyInvocation, List<ArgumentMatcher> argumentMatchers) throws Throwable {
        if (mockBehavior instanceof ChainedMockBehavior) {
            ((ChainedMockBehavior) mockBehavior).installChain();
        }
        BehaviorDefiningInvocation behaviorDefiningInvocation = new BehaviorDefiningInvocation(proxyInvocation, mockBehavior, argumentMatchers);
        addBehaviorDefiningInvocation(behaviorDefiningInvocation, behaviorDefiningInvocations);
        return createChainedMock(proxyInvocation, behaviorDefiningInvocation);
    }


    protected void addBehaviorDefiningInvocation(BehaviorDefiningInvocation behaviorDefiningInvocation, BehaviorDefiningInvocations behaviorDefinition) {
        behaviorDefinition.addBehaviorDefiningInvocation(behaviorDefiningInvocation);
    }


    protected Object createChainedMock(ProxyInvocation proxyInvocation, BehaviorDefiningInvocation behaviorDefiningInvocation) {
        Class<?> innerMockType = proxyInvocation.getMethod().getReturnType();
        String innerMockName = proxyInvocation.getMockName() + MOCK_NAME_CHAIN_SEPARATOR + proxyInvocation.getMethod().getName();

        Mock<?> mock = mockFactory.createChainedMock(innerMockName, innerMockType);
        if (mock == null) {
            return null;
        }
        return mock.performs(new ChainedMockBehavior(mock, behaviorDefiningInvocation));
    }


    public static class ChainedMockBehavior implements MockBehavior {

        protected Mock<?> mock;
        protected BehaviorDefiningInvocation behaviorDefiningInvocation;
        protected MockBehavior originalMockBehavior;


        public ChainedMockBehavior(Mock<?> mock, BehaviorDefiningInvocation behaviorDefiningInvocation) {
            this.mock = mock;
            this.behaviorDefiningInvocation = behaviorDefiningInvocation;
            this.originalMockBehavior = behaviorDefiningInvocation.getMockBehavior();
        }

        public void installChain() {
            behaviorDefiningInvocation.setMockBehavior(new ValueReturningMockBehavior(mock.getMock()));
        }

        public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
            return originalMockBehavior.execute(proxyInvocation);
        }
    }

}
