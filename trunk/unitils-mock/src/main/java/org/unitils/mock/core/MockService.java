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
import org.unitils.mock.core.proxy.ProxyService;
import org.unitils.mock.core.util.CloneService;
import org.unitils.mock.core.util.StackTraceService;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.uncapitalize;

/**
 * @author Tim Ducheyne
 */
public class MockService {

    protected Scenario scenario;
    protected MatchingInvocationBuilder matchingInvocationBuilder;
    protected ProxyService proxyService;
    protected StackTraceService stackTraceService;
    protected CloneService cloneService;

    /* Created chained mocks per mock name */
    protected Map<String, Mock<?>> chainedMocksPerName = new HashMap<String, Mock<?>>();


    public MockService(Scenario scenario, MatchingInvocationBuilder matchingInvocationBuilder, ProxyService proxyService, StackTraceService stackTraceService, CloneService cloneService) {
        this.scenario = scenario;
        this.matchingInvocationBuilder = matchingInvocationBuilder;
        this.proxyService = proxyService;
        this.stackTraceService = stackTraceService;
        this.cloneService = cloneService;
    }


    public <T> Mock<T> createMock(String name, Class<T> mockedType, Object testObject) {
        String mockName = getMockName(name, mockedType);
        resetScenarioIfNeeded(testObject);
        return new MockObject<T>(mockName, mockedType, false, scenario, matchingInvocationBuilder, this, proxyService, stackTraceService, cloneService);
    }

    public <T> PartialMock<T> createPartialMock(String name, Class<T> mockedType, Object testObject) {
        String mockName = getMockName(name, mockedType);
        resetScenarioIfNeeded(testObject);
        return new PartialMockObject<T>(mockName, mockedType, false, scenario, matchingInvocationBuilder, this, proxyService, stackTraceService, cloneService);
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
            chainedMock = new MockObject<M>(name, mockedType, true, scenario, matchingInvocationBuilder, this, proxyService, stackTraceService, cloneService);
        } catch (Exception e) {
            // chaining not supported, return type cannot be mocked
            return null;
        }
        chainedMocksPerName.put(name, chainedMock);
        return (Mock<M>) chainedMock;
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
