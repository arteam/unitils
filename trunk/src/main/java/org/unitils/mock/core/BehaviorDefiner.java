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

import org.unitils.mock.Mock;
import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.mockbehavior.impl.ValueReturningMockBehavior;
import org.unitils.mock.proxy.ProxyInvocation;
import org.unitils.mock.proxy.ProxyInvocationHandler;

import java.util.ArrayList;
import java.util.List;

public abstract class BehaviorDefiner<T> {

    /* The name of the mock (e.g. the name of the field) */
    protected String mockName;

    /* Mock behaviors that are removed once they have been matched */
    protected List<BehaviorDefiningInvocation> behaviorDefiningInvocations = new ArrayList<BehaviorDefiningInvocation>();


    public BehaviorDefiner(String mockName) {
        this.mockName = mockName;
    }


    public List<BehaviorDefiningInvocation> getBehaviorDefiningInvocations() {
        return behaviorDefiningInvocations;
    }


    public void reset() {
        behaviorDefiningInvocations.clear();
    }


    protected Object handleBehaviorDefiningInvocation(ProxyInvocation proxyInvocation, MockBehavior mockBehavior) {
        if (mockBehavior instanceof ChainedMockBehavior) {
            ((ChainedMockBehavior) mockBehavior).installChain();
        }
        BehaviorDefiningInvocation behaviorDefiningInvocation = new BehaviorDefiningInvocation(proxyInvocation, mockName, mockBehavior);
        behaviorDefiningInvocations.add(behaviorDefiningInvocation);
        return createChainedMock(proxyInvocation, behaviorDefiningInvocation);
    }


    protected Object createChainedMock(ProxyInvocation proxyInvocation, BehaviorDefiningInvocation behaviorDefiningInvocation) {
        Class<?> innerMockType = proxyInvocation.getMethod().getReturnType();
        String innerMockName = mockName + "." + proxyInvocation.getMethod().getName();

        Mock<?> mock = createInnerMock(innerMockName, innerMockType);
        if (mock == null) {
            return null;
        }
        return mock.performs(new ChainedMockBehavior(mock, behaviorDefiningInvocation));
    }


    @SuppressWarnings({"unchecked"})
    protected abstract Mock<?> createInnerMock(String name, Class<?> mockedType);


    public ProxyInvocationHandler createProxyInvocationHandler(MockBehavior mockBehavior) {
        return new InvocationHandler(mockBehavior);
    }


    protected class InvocationHandler implements ProxyInvocationHandler {

        private MockBehavior mockBehavior;

        public InvocationHandler(MockBehavior mockBehavior) {
            this.mockBehavior = mockBehavior;
        }

        public Object handleInvocation(ProxyInvocation proxyInvocation) throws Throwable {
            return handleBehaviorDefiningInvocation(proxyInvocation, mockBehavior);
        }
    }


    public static class ChainedMockBehavior implements MockBehavior {

        private Mock<?> mock;

        private BehaviorDefiningInvocation behaviorDefiningInvocation;

        private MockBehavior originalMockBehavior;


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
