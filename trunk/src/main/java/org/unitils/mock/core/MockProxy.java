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

import static org.unitils.core.util.CloneUtil.createDeepClone;
import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.mockbehavior.ValidatableMockBehavior;
import org.unitils.mock.mockbehavior.impl.DefaultValueReturningMockBehavior;
import org.unitils.mock.proxy.ProxyInvocation;
import org.unitils.mock.proxy.ProxyInvocationHandler;
import static org.unitils.mock.proxy.ProxyUtils.createProxy;
import static org.unitils.util.MethodUtils.*;

public class MockProxy<T> {


    /* Mock behaviors that are removed once they have been matched */
    protected BehaviorDefiner<T> oneTimeMatchingBehaviorDefiner;

    /* Mock behaviors that can be matched and re-used for several invocation */
    protected BehaviorDefiner<T> alwaysMatchingBehaviorDefiner;

    /* The scenario that will record all observed invocations */
    protected Scenario scenario;

    protected SyntaxMonitor syntaxMonitor;


    public MockProxy(BehaviorDefiner<T> oneTimeMatchingBehaviorDefiner, BehaviorDefiner<T> alwaysMatchingBehaviorDefiner, Scenario scenario, SyntaxMonitor syntaxMonitor) {
        this.oneTimeMatchingBehaviorDefiner = oneTimeMatchingBehaviorDefiner;
        this.alwaysMatchingBehaviorDefiner = alwaysMatchingBehaviorDefiner;
        this.scenario = scenario;
        this.syntaxMonitor = syntaxMonitor;
    }


    @SuppressWarnings("unchecked")
    public T getProxyInstance(String mockName, Class<T> mockedType) {
        return createProxy(mockedType, new Class<?>[]{Cloneable.class}, new InvocationHandler(mockName, mockedType));
    }


    protected Object handleMockInvocation(String mockName, ProxyInvocation proxyInvocation) throws Throwable {
        if (Object.class.equals(proxyInvocation.getMethod().getDeclaringClass())) {
            return handleObjectClassMethodInvocation(proxyInvocation);
        }

        syntaxMonitor.assertNotExpectingInvocation();

        BehaviorDefiningInvocation behaviorDefiningInvocation = getMatchingBehaviorDefiningInvocation(proxyInvocation);
        MockBehavior mockBehavior = getMockBehavior(proxyInvocation, behaviorDefiningInvocation);
        if (mockBehavior instanceof ValidatableMockBehavior) {
            ((ValidatableMockBehavior) mockBehavior).assertCanExecute(proxyInvocation);
        }

        ObservedInvocation mockInvocation = createObservedInvocation(mockName, proxyInvocation, behaviorDefiningInvocation, mockBehavior);
        scenario.addObservedMockInvocation(mockInvocation);

        Object result = null;
        if (mockBehavior != null) {
            // check whether the mock behavior can applied for this invocation
            if (mockBehavior instanceof ValidatableMockBehavior) {
                ((ValidatableMockBehavior) mockBehavior).assertCanExecute(proxyInvocation);
            }
            result = mockBehavior.execute(proxyInvocation);
        }

        mockInvocation.setResultAtInvocationTime(createDeepClone(result));
        return result;
    }


    protected Object handleObjectClassMethodInvocation(ProxyInvocation proxyInvocation) throws Throwable {
        if (isEqualsMethod(proxyInvocation.getMethod())) {
            Object other = proxyInvocation.getArguments().get(0);
            return proxyInvocation.getProxy() == other;
        }
        if (isHashCodeMethod(proxyInvocation.getMethod())) {
            return super.hashCode();
        }
        if (isCloneMethod(proxyInvocation.getMethod())) {
            return proxyInvocation.getProxy();
        }
        return proxyInvocation.getMethod().invoke(proxyInvocation.getProxy(), proxyInvocation.getArguments().toArray());
    }


    protected BehaviorDefiningInvocation getMatchingBehaviorDefiningInvocation(ProxyInvocation proxyInvocation) throws Throwable {
        // Check if there is a one-time matching behavior that hasn't been invoked yet
        for (BehaviorDefiningInvocation behaviorDefiningInvocation : oneTimeMatchingBehaviorDefiner.getBehaviorDefiningInvocations()) {
            if (behaviorDefiningInvocation.isUsed()) {
                continue;
            }
            if (behaviorDefiningInvocation.matches(proxyInvocation)) {
                behaviorDefiningInvocation.markAsUsed();
                return behaviorDefiningInvocation;
            }
        }

        // Check if there is an always-matching behavior
        for (BehaviorDefiningInvocation behaviorDefiningInvocation : alwaysMatchingBehaviorDefiner.getBehaviorDefiningInvocations()) {
            if (behaviorDefiningInvocation.matches(proxyInvocation)) {
                behaviorDefiningInvocation.markAsUsed();
                return behaviorDefiningInvocation;
            }
        }
        return null;
    }

    protected MockBehavior getMockBehavior(ProxyInvocation proxyInvocation, BehaviorDefiningInvocation behaviorDefiningInvocation) {
        if (behaviorDefiningInvocation != null) {
            return behaviorDefiningInvocation.getMockBehavior();
        }
        return getDefaultMockBehavior(proxyInvocation);
    }


    protected MockBehavior getDefaultMockBehavior(ProxyInvocation proxyInvocation) {
        if (proxyInvocation.getMethod().getReturnType() == Void.TYPE) {
            return null;
        }
        return new DefaultValueReturningMockBehavior();
    }


    protected ObservedInvocation createObservedInvocation(String mockName, ProxyInvocation proxyInvocation, BehaviorDefiningInvocation behaviorDefiningInvocation, MockBehavior mockBehavior) {
        return new ObservedInvocation(proxyInvocation, mockName, behaviorDefiningInvocation, mockBehavior);
    }


    protected class InvocationHandler implements ProxyInvocationHandler {

        /* The name of the mock (e.g. the name of the field) */
        protected String mockName;

        /* The class type that is mocked */
        protected Class<T> mockedType;


        public InvocationHandler(String mockName, Class<T> mockedType) {
            this.mockName = mockName;
            this.mockedType = mockedType;
        }

        public Object handleInvocation(ProxyInvocation invocation) throws Throwable {
            return handleMockInvocation(mockName, invocation);
        }
    }

}