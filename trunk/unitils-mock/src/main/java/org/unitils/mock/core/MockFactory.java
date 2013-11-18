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
import org.unitils.mock.argumentmatcher.ArgumentMatcherRepository;
import org.unitils.mock.core.matching.MatchingInvocationHandlerFactory;
import org.unitils.mock.core.proxy.ProxyService;
import org.unitils.mock.core.proxy.impl.DummyProxyInvocationHandler;
import org.unitils.mock.core.proxy.impl.MatchingProxyInvocationHandler;
import org.unitils.mock.core.proxy.impl.MockProxyInvocationHandler;
import org.unitils.mock.core.proxy.impl.PartialMockProxyInvocationHandler;
import org.unitils.mock.core.util.CloneService;
import org.unitils.mock.core.util.StackTraceService;
import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.mockbehavior.MockBehaviorFactory;
import org.unitils.mock.report.ScenarioReport;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.uncapitalize;
import static org.unitils.util.ReflectionUtils.copyFields;

/**
 * @author Tim Ducheyne
 */
public class MockFactory {

    protected Scenario scenario;
    protected ArgumentMatcherRepository argumentMatcherRepository;
    protected ProxyService proxyService;
    protected StackTraceService stackTraceService;
    protected CloneService cloneService;
    protected MockBehaviorFactory mockBehaviorFactory;
    protected ScenarioReport scenarioReport;

    /* Created chained mocks per proxy and per method */
    protected Map<Object, Map<Method, Mock<?>>> chainedMocks = new IdentityHashMap<Object, Map<Method, Mock<?>>>();


    public MockFactory(Scenario scenario, ArgumentMatcherRepository argumentMatcherRepository, MockBehaviorFactory mockBehaviorFactory, ProxyService proxyService, StackTraceService stackTraceService, CloneService cloneService, ScenarioReport scenarioReport) {
        this.scenario = scenario;
        this.argumentMatcherRepository = argumentMatcherRepository;
        this.proxyService = proxyService;
        this.stackTraceService = stackTraceService;
        this.cloneService = cloneService;
        this.mockBehaviorFactory = mockBehaviorFactory;
        this.scenarioReport = scenarioReport;
    }


    public <T> Mock<T> createMock(String name, Class<T> mockedType, Object testObject) {
        String mockName = getName(name, mockedType);
        resetIfNewTestObject(testObject);
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
        String mockName = getName(name, mockedType);
        resetIfNewTestObject(testObject);
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

    public Mock<?> createChainedMock(MatchingInvocation matchingInvocation) {
        Object matchingProxy = matchingInvocation.getProxy();
        Map<Method, Mock<?>> chainedMocksForProxy = chainedMocks.get(matchingProxy);
        if (chainedMocksForProxy == null) {
            chainedMocksForProxy = new HashMap<Method, Mock<?>>();
            chainedMocks.put(matchingProxy, chainedMocksForProxy);
        }
        Method method = matchingInvocation.getMethod();
        Mock<?> chainedMock = chainedMocksForProxy.get(method);
        if (chainedMock != null) {
            return chainedMock;
        }
        Class<?> mockedType = matchingInvocation.getReturnType();
        if (Void.class.equals(mockedType) || mockedType.isPrimitive() || mockedType.isArray() || Modifier.isFinal(mockedType.getModifiers())) {
            return null;
        }
        try {
            String name = matchingInvocation.getInnerMockName();
            chainedMock = createMockObject(name, mockedType, true);
        } catch (Exception e) {
            // chaining not supported, return type cannot be mocked
            return null;
        }
        chainedMocksForProxy.put(method, chainedMock);
        return chainedMock;
    }

    /**
     * Creates a dummy of the given type. A dummy object is a proxy that will return default values for every method. This can be
     * used to quickly create test objects without having to worry about correctly filling in every field.
     *
     * @param name      The name for the dummy, use null for the default typeDummy name
     * @param dummyType The type for the proxy, not null
     * @return The proxy, not null
     */
    public <T> T createDummy(String name, Class<T> dummyType) {
        String dummyName = getName(name, dummyType);
        MockBehavior mockBehaviour = mockBehaviorFactory.createDummyValueReturningMockBehavior(this);
        DummyProxyInvocationHandler dummyProxyInvocationHandler = createDummyProxyInvocationHandler(mockBehaviour);
        return proxyService.createProxy(dummyName, false, dummyProxyInvocationHandler, dummyType);
    }


    protected <T> MockObject<T> createMockObject(String name, Class<T> mockedType, boolean chained) {
        BehaviorDefiningInvocations behaviorDefiningInvocations = createBehaviorDefiningInvocations();
        MatchingProxyInvocationHandler matchingProxyInvocationHandler = createMatchingProxyInvocationHandler();
        MockProxyInvocationHandler<T> mockProxyInvocationHandler = createMockProxyInvocationHandler(behaviorDefiningInvocations, matchingProxyInvocationHandler);
        T proxy = proxyService.createProxy(name, false, mockProxyInvocationHandler, mockedType);
        T matchingProxy = proxyService.createProxy(name, false, matchingProxyInvocationHandler, mockedType);
        MatchingInvocationHandlerFactory matchingInvocationHandlerFactory = createMatchingInvocationHandlerFactory();
        return new MockObject<T>(name, mockedType, proxy, matchingProxy, chained, behaviorDefiningInvocations, matchingProxyInvocationHandler, mockBehaviorFactory, matchingInvocationHandlerFactory);
    }

    protected <T> PartialMockObject<T> createPartialMockObject(String name, Class<T> mockedType, boolean chained) {
        BehaviorDefiningInvocations behaviorDefiningInvocations = createBehaviorDefiningInvocations();
        MatchingProxyInvocationHandler matchingProxyInvocationHandler = createMatchingProxyInvocationHandler();
        MockProxyInvocationHandler<T> mockProxyInvocationHandler = createPartialMockProxyInvocationHandler(behaviorDefiningInvocations, matchingProxyInvocationHandler);
        T proxy = proxyService.createProxy(name, true, mockProxyInvocationHandler, mockedType);
        T matchingProxy = proxyService.createProxy(name, false, matchingProxyInvocationHandler, mockedType);
        MatchingInvocationHandlerFactory matchingInvocationHandlerFactory = createMatchingInvocationHandlerFactory();
        return new PartialMockObject<T>(name, mockedType, proxy, matchingProxy, chained, behaviorDefiningInvocations, matchingProxyInvocationHandler, mockBehaviorFactory, matchingInvocationHandlerFactory);
    }

    protected void resetIfNewTestObject(Object testObject) {
        if (scenario.getTestObject() == testObject) {
            return;
        }
        scenario.reset();
        scenario.setTestObject(testObject);
        argumentMatcherRepository.reset();
        chainedMocks.clear();
    }

    protected <T> String getName(String name, Class<T> type) {
        if (isBlank(name)) {
            return uncapitalize(type.getSimpleName());
        }
        return name;
    }


    protected BehaviorDefiningInvocations createBehaviorDefiningInvocations() {
        return new BehaviorDefiningInvocations();
    }

    protected <T> MockProxyInvocationHandler<T> createMockProxyInvocationHandler(BehaviorDefiningInvocations behaviorDefiningInvocations, MatchingProxyInvocationHandler matchingProxyInvocationHandler) {
        return new MockProxyInvocationHandler<T>(behaviorDefiningInvocations, scenario, cloneService, matchingProxyInvocationHandler);
    }

    protected <T> PartialMockProxyInvocationHandler<T> createPartialMockProxyInvocationHandler(BehaviorDefiningInvocations behaviorDefiningInvocations, MatchingProxyInvocationHandler matchingProxyInvocationHandler) {
        return new PartialMockProxyInvocationHandler<T>(behaviorDefiningInvocations, scenario, cloneService, matchingProxyInvocationHandler);
    }

    protected <T> DummyProxyInvocationHandler createDummyProxyInvocationHandler(MockBehavior mockBehaviour) {
        return new DummyProxyInvocationHandler(mockBehaviour);
    }

    protected <T> MatchingProxyInvocationHandler createMatchingProxyInvocationHandler() {
        return new MatchingProxyInvocationHandler(argumentMatcherRepository, proxyService, stackTraceService);
    }

    protected MatchingInvocationHandlerFactory createMatchingInvocationHandlerFactory() {
        return new MatchingInvocationHandlerFactory(scenario, this, scenarioReport);
    }
}
