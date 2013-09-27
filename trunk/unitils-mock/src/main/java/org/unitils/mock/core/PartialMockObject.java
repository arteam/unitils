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

import org.unitils.mock.PartialMock;
import org.unitils.mock.annotation.MatchStatement;
import org.unitils.mock.core.matching.MatchingInvocationBuilder;
import org.unitils.mock.core.matching.MatchingInvocationHandler;
import org.unitils.mock.core.proxy.ProxyService;
import org.unitils.mock.core.proxy.impl.MockInvocationHandler;
import org.unitils.mock.core.proxy.impl.MockProxyInvocationHandler;
import org.unitils.mock.core.proxy.impl.PartialMockInvocationHandler;
import org.unitils.mock.core.util.CloneService;
import org.unitils.mock.core.util.StackTraceService;
import org.unitils.mock.mockbehavior.impl.StubMockBehavior;

import static org.unitils.util.ReflectionUtils.copyFields;

/**
 * Implementation of a PartialMock.
 * For a partial mock, if a method is called that is not mocked, the original behavior will be called.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 * @author Kenny Claes
 */
public class PartialMockObject<T> extends MockObject<T> implements PartialMock<T> {


    /**
     * Creates a mock of the same type as the given mock prototype with the given name.
     * All instance fields of the given prototype will then be copied to the mock instance. This way you can have a
     * pre-initialized instance of the mock (e.g. when there is no default constructor).
     * <p/>
     * If the type mocked instance does not correspond to the declared type, a ClassCastException will occur when the mock
     * is used.
     * <p/>
     * If no name is given the un-capitalized type name + Mock is used, e.g. myServiceMock
     *
     * @param name          The name of the mock, e.g. the field-name, null for the default
     * @param mockPrototype The instance that will be wrapped with a proxy, use the raw type when mocking generic types, not null
     */
    @SuppressWarnings("unchecked")
    public PartialMockObject(String name, T mockPrototype, boolean chainedMock, Scenario scenario, MatchingInvocationBuilder matchingInvocationBuilder, MockService mockService, ProxyService proxyService, StackTraceService stackTraceService, CloneService cloneService) {
        super(name, (Class<T>) mockPrototype.getClass(), chainedMock, scenario, matchingInvocationBuilder, mockService, proxyService, stackTraceService, cloneService);
        copyFields(mockPrototype, getMock());
    }

    /**
     * Creates a mock of the given type for the given scenario.
     * <p/>
     * There is no .class literal for generic types. Therefore you need to pass the raw type when mocking generic types.
     * E.g. Mock&lt;List&lt;String&gt;&gt; myMock = new MockObject("myMock", List.class, this);
     * <p/>
     * If the mocked type does not correspond to the declared type, a ClassCastException will occur when the mock
     * is used.
     * <p/>
     * If no name is given the un-capitalized type name + Mock is used, e.g. myServiceMock
     *
     * @param name       The name of the mock, e.g. the field-name, null for the default
     * @param mockedType The mock type that will be proxied, use the raw type when mocking generic types, not null
     */
    public PartialMockObject(String name, Class<T> mockedType, boolean chainedMock, Scenario scenario, MatchingInvocationBuilder matchingInvocationBuilder, MockService mockService, ProxyService proxyService, StackTraceService stackTraceService, CloneService cloneService) {
        super(name, mockedType, chainedMock, scenario, matchingInvocationBuilder, mockService, proxyService, stackTraceService, cloneService);
    }


    /**
     * Stubs out (removes) the behavior of the method when the invocation following
     * this call matches the observed behavior. E.g.
     * <p/>
     * mock.stub().method1();
     * <p/>
     * will not invoke the actual behavior of method1.
     * <p/>
     * If the method has a return type, a default value will be returned.
     * <p/>
     * Note: stubbed methods can still be asserted afterwards: e.g.
     * <p/>
     * mock.assertInvoked().method1();
     *
     * @return The proxy instance that will record the method call, not null
     */
    @MatchStatement
    public T stub() {
        MatchingInvocationHandler matchingInvocationHandler = createAlwaysMatchingBehaviorDefiningMatchingInvocationHandler(new StubMockBehavior());
        return startMatchingInvocation(matchingInvocationHandler);
    }

    @Override
    protected T createProxy(MockProxyInvocationHandler mockProxyInvocationHandler) {
        return proxyService.createProxy(name, mockProxyInvocationHandler, mockedType);
    }

    @Override
    protected MockInvocationHandler<T> createMockInvocationHandler() {
        return new PartialMockInvocationHandler<T>(oneTimeMatchingBehaviorDefiningInvocations, alwaysMatchingBehaviorDefiningInvocations, scenario, cloneService);
    }
}