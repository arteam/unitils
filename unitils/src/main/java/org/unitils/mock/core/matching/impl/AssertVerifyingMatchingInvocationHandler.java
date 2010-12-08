/*
 * Copyright Unitils.org
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
import org.unitils.mock.core.MockFactory;
import org.unitils.mock.core.Scenario;
import org.unitils.mock.core.matching.MatchingInvocationHandler;
import org.unitils.mock.core.proxy.ProxyInvocation;

import java.util.List;

import static org.unitils.core.util.ObjectFormatter.MOCK_NAME_CHAIN_SEPARATOR;


/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public abstract class AssertVerifyingMatchingInvocationHandler implements MatchingInvocationHandler {

    protected Scenario scenario;
    protected MockFactory mockFactory;


    public AssertVerifyingMatchingInvocationHandler(Scenario scenario, MockFactory mockFactory) {
        this.scenario = scenario;
        this.mockFactory = mockFactory;
    }


    public Object handleInvocation(ProxyInvocation proxyInvocation, List<ArgumentMatcher> argumentMatchers) throws Throwable {
        BehaviorDefiningInvocation behaviorDefiningInvocation = new BehaviorDefiningInvocation(proxyInvocation, null, argumentMatchers);
        performAssertion(scenario, behaviorDefiningInvocation);
        return createChainedMock(proxyInvocation);
    }


    protected Object createChainedMock(ProxyInvocation proxyInvocation) {
        Class<?> innerMockType = proxyInvocation.getMethod().getReturnType();
        String innerMockName = proxyInvocation.getMockName() + MOCK_NAME_CHAIN_SEPARATOR + proxyInvocation.getMethod().getName();

        Mock<?> mock = mockFactory.createChainedMock(innerMockName, innerMockType);
        if (mock == null) {
            return null;
        }
        return performChainedAssertion(mock);
    }

    protected abstract void performAssertion(Scenario scenario, BehaviorDefiningInvocation behaviorDefiningInvocation);

    protected abstract Object performChainedAssertion(Mock<?> mock);
}
