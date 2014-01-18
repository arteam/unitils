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
import org.unitils.core.UnitilsException;
import org.unitils.mock.CreateMockListener;
import org.unitils.mock.Mock;
import org.unitils.mock.PartialMock;
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
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * @author Tim Ducheyne
 */
public class MockFactoryCreatePartialMockTest extends UnitilsJUnit4 {

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
    private Mock<CreateMockListener> createMockListenerMock;
    @Dummy
    private Object otherTestObject;
    @Dummy
    private MockService mockService;
    @Dummy
    private DummyFactory dummyFactory;
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
    public void createPartialMock() {
        proxyServiceMock.returns(proxy).createProxy(null, "name", true, mockProxyInvocationHandler, Properties.class);
        proxyServiceMock.returns(matchingProxy).createProxy(null, "name", false, matchingProxyInvocationHandler, Properties.class);

        PartialMock<Properties> result = mockFactory.createPartialMock("name", Properties.class, testObject);
        PartialMockObject<Properties> mockObject = (PartialMockObject<Properties>) result;
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
    public void fieldsAreCopiedWhenUsingMockPrototype() {
        Properties prototype = new Properties();
        Properties prototypeProxy = new Properties();
        prototype.put("test", "123");
        proxyServiceMock.returns(prototypeProxy).createProxy(null, "name", true, mockProxyInvocationHandler, Properties.class);
        proxyServiceMock.returns(matchingProxy).createProxy(null, "name", false, matchingProxyInvocationHandler, Properties.class);

        PartialMock<Properties> result = mockFactory.createPartialMock("name", prototype, testObject);
        PartialMockObject<Properties> mockObject = (PartialMockObject<Properties>) result;
        assertEquals(prototypeProxy, prototype);
    }

    @Test
    public void defaultNameWhenNameIsNull() {
        PartialMock<Properties> result = mockFactory.createPartialMock(null, Properties.class, testObject);
        PartialMockObject<Properties> mockObject = (PartialMockObject<Properties>) result;
        assertEquals("properties", mockObject.name);
    }

    @Test
    public void resetWhenNewTestObject() {
        mockFactory.createPartialMock("name", Properties.class, otherTestObject);
        scenarioMock.assertInvoked().reset();
        scenarioMock.assertInvoked().setTestObject(otherTestObject);
        argumentMatcherRepositoryMock.assertInvoked().reset();
    }

    @Test
    public void noResetWhenSameTestObject() {
        mockFactory.createPartialMock("name", Properties.class, testObject);
        scenarioMock.assertNotInvoked().reset();
        scenarioMock.assertNotInvoked().setTestObject(testObject);
        argumentMatcherRepositoryMock.assertNotInvoked().reset();
    }

    @Test
    public void mockCreatedCalledWhenTestObjectIsCreateMockListener() {
        Mock<Properties> mock = mockFactory.createPartialMock("name", Properties.class, createMockListenerMock.getMock());
        createMockListenerMock.assertInvoked().mockCreated(mock, "name", Properties.class);
    }

    @Test
    public void mockCreatedCalledForPrototypeWhenTestObjectIsCreateMockListener() {
        Properties prototype = new Properties();
        Properties prototypeProxy = new Properties();
        proxyServiceMock.returns(prototypeProxy).createProxy(null, "name", true, mockProxyInvocationHandler, Properties.class);

        Mock<Properties> mock = mockFactory.createPartialMock("name", prototype, createMockListenerMock.getMock());
        createMockListenerMock.assertInvoked().mockCreated(mock, "name", Properties.class);
    }

    @Test
    public void exceptionWhenCopyFieldsFails() {
        proxyServiceMock.returns(proxy).createProxy(null, "name", true, mockProxyInvocationHandler, Properties.class);
        proxyServiceMock.returns(matchingProxy).createProxy(null, "name", false, matchingProxyInvocationHandler, Properties.class);
        try {
            // forces a copy from the wrong type  (StringBuilder to Properties)
            mockFactory.createPartialMock("name", new StringBuilder(), testObject);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to create partial mock from prototype. Unable to copy fields values from prototype to mock instance for class java.lang.StringBuilder.", e.getMessage());
        }
    }
}
