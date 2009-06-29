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
import org.unitils.mock.argumentmatcher.ArgumentMatcherRepository;
import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.mockbehavior.impl.ValueReturningMockBehavior;
import org.unitils.mock.proxy.ProxyInvocation;
import org.unitils.mock.proxy.ProxyInvocationHandler;
import static org.unitils.mock.proxy.ProxyUtils.createProxy;
import static org.unitils.util.StackTraceUtils.getInvocationStackTrace;

import java.util.ArrayList;
import java.util.List;

public abstract class BehaviorDefiner<T> {

    /* The scenario that will record all observed invocations */
    protected Scenario scenario;

    /* The name of the mock (e.g. the name of the field) */
    protected String mockName;

    /* The class type that is mocked */
    protected Class<T> mockedType;

    protected SyntaxMonitor syntaxMonitor;

    /* Mock behaviors that are removed once they have been matched */
    protected List<BehaviorDefiningInvocation> behaviorDefiningInvocations = new ArrayList<BehaviorDefiningInvocation>();


    public BehaviorDefiner(String mockName, Class<T> mockedType, Scenario scenario, SyntaxMonitor syntaxMonitor) {
        this.mockName = mockName;
        this.mockedType = mockedType;
        this.scenario = scenario;
        this.syntaxMonitor = syntaxMonitor;
    }


    public T getProxyInstance(String definingMethodName, MockBehavior mockBehavior) {
        if (!(mockBehavior instanceof ChainedMockBehavior)) {
            StackTraceElement[] invocationStackTrace = getInvocationStackTrace(MockObject.class);
            syntaxMonitor.startBehaviorDefinition(mockName, definingMethodName, invocationStackTrace);
        }
        return createProxy(mockedType, new InvocationHandler(mockBehavior));
    }


    public List<BehaviorDefiningInvocation> getBehaviorDefiningInvocations() {
        return behaviorDefiningInvocations;
    }


    public void reset() {
        behaviorDefiningInvocations.clear();
    }


    protected Object handleBehaviorDefiningInvocation(ProxyInvocation proxyInvocation, MockBehavior mockBehavior) {
        syntaxMonitor.endBehaviorDefinition(proxyInvocation);
        BehaviorDefiningInvocation behaviorDefiningInvocation = new BehaviorDefiningInvocation(proxyInvocation, mockName, mockBehavior);
        behaviorDefiningInvocations.add(behaviorDefiningInvocation);
        return createChainedMock(proxyInvocation, behaviorDefiningInvocation);
    }


    public abstract Mock<?> createInnerMock(String name, Class<?> mockedType, Scenario scenario);


    public Object createChainedMock(ProxyInvocation proxyInvocation, BehaviorDefiningInvocation behaviorDefiningInvocation) {
        try {
            ArgumentMatcherRepository.getInstance().registerStartOfMatchingInvocation(proxyInvocation.getLineNumber());

            Class<?> innerMockType = proxyInvocation.getMethod().getReturnType();
            String innerMockName = mockName + "." + proxyInvocation.getMethod().getName();
            Mock<?> mock = createInnerMock(innerMockName, innerMockType, scenario);
            return mock.performs(new ChainedMockBehavior(mock, behaviorDefiningInvocation));

        } catch (Exception e) {
            return null;
        }
    }


    protected class InvocationHandler implements ProxyInvocationHandler {

        private MockBehavior mockBehavior;

        public InvocationHandler(MockBehavior mockBehavior) {
            this.mockBehavior = mockBehavior;
        }

        @SuppressWarnings({"unchecked"})
        public Object handleInvocation(ProxyInvocation proxyInvocation) throws Throwable {
            if (mockBehavior instanceof ChainedMockBehavior) {
                ((ChainedMockBehavior) mockBehavior).installChain();
            }
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
