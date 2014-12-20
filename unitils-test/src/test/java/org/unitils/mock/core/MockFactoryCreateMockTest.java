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

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.CreateMockListener;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;
import org.unitils.mock.argumentmatcher.ArgumentMatcherRepository;
import org.unitils.mock.core.matching.MatchingInvocationHandlerFactory;
import org.unitils.mock.core.proxy.ProxyService;
import org.unitils.mock.core.proxy.impl.MatchingProxyInvocationHandler;
import org.unitils.mock.core.proxy.impl.MockProxyInvocationHandler;
import org.unitils.mock.core.util.CloneService;
import org.unitils.mock.core.util.StackTraceService;
import org.unitils.mock.mockbehavior.MockBehaviorFactory;
import org.unitils.mock.report.ScenarioReport;

import java.util.Properties;

import static org.junit.Assert.*;
import static org.unitils.mock.ArgumentMatchers.notNull;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * @author Tim Ducheyne
 */
public class MockFactoryCreateMockTest extends UnitilsJUnit4 {

    private MockFactory mockFactory;

    private Mock<Scenario> scenarioMock;
    private Mock<ArgumentMatcherRepository> argumentMatcherRepositoryMock;
    private Mock<ProxyService> proxyServiceMock;
    private Mock<MockBehaviorFactory> mockBehaviorFactoryMock;
    @Dummy
    private StackTraceService stackTraceService;
    @Dummy
    private CloneService cloneService;
    @Dummy
    private ScenarioReport scenarioReport;
    @Dummy
    private Properties proxy;
    @Dummy
    private Properties matchingProxy;
    @Dummy
    private Object testObject;
    @Dummy
    private Object otherTestObject;
    @Dummy
    private MockService mockService;
    @Dummy
    private DummyFactory dummyFactory;
    private Mock<CreateMockListener> createMockListenerMock;
    private BehaviorDefiningInvocations behaviorDefiningInvocations;
    private MatchingProxyInvocationHandler matchingProxyInvocationHandler;
    private MockProxyInvocationHandler mockProxyInvocationHandler;
    private MatchingInvocationHandlerFactory matchingInvocationHandlerFactory;


    @Before
    public void initialize() {
        mockFactory = new MockFactory(scenarioMock.getMock(), argumentMatcherRepositoryMock.getMock(), mockBehaviorFactoryMock.getMock(), proxyServiceMock.getMock(), stackTraceService, cloneService, scenarioReport, mockService, dummyFactory);

        scenarioMock.returns(testObject).getTestObject();

        behaviorDefiningInvocations = new BehaviorDefiningInvocations();
        matchingProxyInvocationHandler = new MatchingProxyInvocationHandler(argumentMatcherRepositoryMock.getMock(), stackTraceService);
        mockProxyInvocationHandler = new MockProxyInvocationHandler<Properties>(behaviorDefiningInvocations, scenarioMock.getMock(), cloneService, matchingProxyInvocationHandler);
        matchingInvocationHandlerFactory = new MatchingInvocationHandlerFactory(scenarioMock.getMock(), mockFactory, scenarioReport);
    }


    @Test
    public void createMock() {
        proxyServiceMock.returns(proxy).createProxy(notNull(String.class), "name", false, mockProxyInvocationHandler, Properties.class);
        proxyServiceMock.returns(matchingProxy).createProxy(notNull(String.class), "name", false, matchingProxyInvocationHandler, Properties.class);

        Mock<Properties> result = mockFactory.createMock("name", Properties.class, testObject);
        MockObject<Properties> mockObject = (MockObject<Properties>) result;
        assertEquals("name", mockObject.name);
        assertSame(proxy, mockObject.proxy);
        assertSame(matchingProxy, mockObject.matchingProxy);
        assertFalse(mockObject.chained);
        assertReflectionEquals(behaviorDefiningInvocations, mockObject.behaviorDefiningInvocations);
        assertSame(mockBehaviorFactoryMock.getMock(), mockObject.mockBehaviorFactory);
        assertReflectionEquals(matchingProxyInvocationHandler, mockObject.matchingProxyInvocationHandler);
        assertReflectionEquals(matchingInvocationHandlerFactory, mockObject.matchingInvocationHandlerFactory);
        assertReflectionEquals(mockService, mockObject.mockService);
        assertReflectionEquals(dummyFactory, mockObject.dummyFactory);
    }

    @Test
    public void defaultNameWhenNameIsNull() {
        Mock<Properties> result = mockFactory.createMock(null, Properties.class, testObject);
        MockObject<Properties> mockObject = (MockObject<Properties>) result;
        assertEquals("properties", mockObject.name);
    }

    @Test
    public void resetWhenNewTestObject() {
        mockFactory.createMock("name", Properties.class, otherTestObject);
        scenarioMock.assertInvoked().reset();
        scenarioMock.assertInvoked().setTestObject(otherTestObject);
        argumentMatcherRepositoryMock.assertInvoked().reset();
    }

    @Test
    public void noResetWhenSameTestObject() {
        mockFactory.createMock("name", Properties.class, testObject);
        scenarioMock.assertNotInvoked().reset();
        scenarioMock.assertNotInvoked().setTestObject(testObject);
        argumentMatcherRepositoryMock.assertNotInvoked().reset();
    }

    @Test
    public void mockCreatedCalledWhenTestObjectIsCreateMockListener() {
        Mock<Properties> mock = mockFactory.createMock("name", Properties.class, createMockListenerMock.getMock());
        createMockListenerMock.assertInvoked().mockCreated(mock, "name", Properties.class);
    }
}
