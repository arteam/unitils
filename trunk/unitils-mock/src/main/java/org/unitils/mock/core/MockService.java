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

import org.unitils.mock.Mock;
import org.unitils.mock.PartialMock;
import org.unitils.mock.core.matching.MatchingInvocationBuilder;
import org.unitils.mock.core.matching.MatchingInvocationHandlerFactory;
import org.unitils.mock.core.proxy.ProxyService;
import org.unitils.mock.core.proxy.impl.MatchingProxyInvocationHandler;
import org.unitils.mock.core.proxy.impl.MockInvocationHandler;
import org.unitils.mock.core.util.CloneService;
import org.unitils.mock.core.util.StackTraceService;
import org.unitils.mock.mockbehavior.MockBehaviorFactory;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.uncapitalize;
import static org.unitils.util.ReflectionUtils.copyFields;

/**
 * @author Tim Ducheyne
 */
public class MockService {

    protected Scenario scenario;
    protected MatchingInvocationBuilder matchingInvocationBuilder;
    protected ProxyService proxyService;
    protected StackTraceService stackTraceService;
    protected CloneService cloneService;
    protected MockBehaviorFactory mockBehaviorFactory;

    /* Created chained mocks per mock name */
    protected Map<String, Mock<?>> chainedMocksPerName = new HashMap<String, Mock<?>>();


    public MockService(Scenario scenario, MatchingInvocationBuilder matchingInvocationBuilder, ProxyService proxyService, StackTraceService stackTraceService, CloneService cloneService, MockBehaviorFactory mockBehaviorFactory) {
        this.scenario = scenario;
        this.matchingInvocationBuilder = matchingInvocationBuilder;
        this.proxyService = proxyService;
        this.stackTraceService = stackTraceService;
        this.cloneService = cloneService;
        this.mockBehaviorFactory = mockBehaviorFactory;
    }


    public <T> Mock<T> createMock(String name, Class<T> mockedType, Object testObject) {
        String mockName = getMockName(name, mockedType);
        resetScenarioIfNeeded(testObject);
        return createMockObject(mockName, mockedType, false);
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
     */
    public <T> PartialMock<T> createPartialMock(String name, Class<T> mockedType, Object testObject) {
        String mockName = getMockName(name, mockedType);
        resetScenarioIfNeeded(testObject);
        return createPartialMockObject(mockName, mockedType, false);
    }

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
    public <T> PartialMock<T> createPartialMock(String name, T mockPrototype, Object testObject) {
        Class<T> mockedType = (Class<T>) mockPrototype.getClass();
        PartialMock<T> partialMock = createPartialMock(name, mockedType, testObject);
        copyFields(mockPrototype, partialMock.getMock());
        return partialMock;
    }


    @SuppressWarnings({"unchecked"})
    public <M> Mock<M> createChainedMock(String name, Class<M> mockedType) {
        Mock<?> chainedMock = chainedMocksPerName.get(name);  // todo   not per name !!
        if (chainedMock != null) {
            return (Mock<M>) chainedMock;
        }
        if (Void.class.equals(mockedType) || mockedType.isPrimitive() || mockedType.isArray() || Modifier.isFinal(mockedType.getModifiers())) {
            return null;
        }
        try {
            chainedMock = createMockObject(name, mockedType, true);
        } catch (Exception e) {
            // chaining not supported, return type cannot be mocked
            return null;
        }
        chainedMocksPerName.put(name, chainedMock);
        return (Mock<M>) chainedMock;
    }


    public <T> MockObject<T> createMockObject(String name, Class<T> mockedType, boolean chained) {
        BehaviorDefiningInvocations behaviorDefiningInvocations = createBehaviorDefiningInvocations();
        MockProxy<T> mockProxy = createMockProxy(name, mockedType, false, behaviorDefiningInvocations);
        MatchingInvocationHandlerFactory matchingInvocationHandlerFactory = createMatchingInvocationHandlerFactory();
        return new MockObject<T>(mockProxy, chained, behaviorDefiningInvocations, mockBehaviorFactory, matchingInvocationHandlerFactory);
    }

    public <T> PartialMockObject<T> createPartialMockObject(String name, Class<T> mockedType, boolean chained) {
        BehaviorDefiningInvocations behaviorDefiningInvocations = createBehaviorDefiningInvocations();
        MockProxy<T> mockProxy = createMockProxy(name, mockedType, true, behaviorDefiningInvocations);
        MatchingInvocationHandlerFactory matchingInvocationHandlerFactory = createMatchingInvocationHandlerFactory();
        return new PartialMockObject<T>(mockProxy, chained, behaviorDefiningInvocations, mockBehaviorFactory, matchingInvocationHandlerFactory);
    }


    protected <T> MockProxy<T> createMockProxy(String name, Class<T> mockedType, boolean initialize, BehaviorDefiningInvocations behaviorDefiningInvocations) {
        MockInvocationHandler<T> mockProxyInvocationHandler = createMockProxyInvocationHandler(behaviorDefiningInvocations);
        MatchingProxyInvocationHandler matchingProxyInvocationHandler = createMatchingProxyInvocationHandler();
        T proxy = proxyService.createProxy(name, initialize, mockProxyInvocationHandler, mockedType);
        T matchingProxy = proxyService.createProxy(name, initialize, matchingProxyInvocationHandler, mockedType);
        return new MockProxy<T>(name, mockedType, proxy, matchingProxy, matchingProxyInvocationHandler);
    }

    protected BehaviorDefiningInvocations createBehaviorDefiningInvocations() {
        return new BehaviorDefiningInvocations();
    }

    protected <T> MockInvocationHandler<T> createMockProxyInvocationHandler(BehaviorDefiningInvocations behaviorDefiningInvocations) {
        return new MockInvocationHandler<T>(behaviorDefiningInvocations, scenario, cloneService, matchingInvocationBuilder);
    }

    protected <T> MatchingProxyInvocationHandler createMatchingProxyInvocationHandler() {
        return new MatchingProxyInvocationHandler(matchingInvocationBuilder);
    }

    protected MatchingInvocationHandlerFactory createMatchingInvocationHandlerFactory() {
        return new MatchingInvocationHandlerFactory(scenario, this);
    }

    protected void resetScenarioIfNeeded(Object testObject) {
        if (scenario.getTestObject() == testObject) {
            return;
        }
        scenario.reset();
        scenario.setTestObject(testObject);
        matchingInvocationBuilder.reset();
    }

    protected <T> String getMockName(String name, Class<T> mockedType) {
        if (isBlank(name)) {
            return uncapitalize(mockedType.getSimpleName()) + "Mock";
        }
        return name;
    }
}
