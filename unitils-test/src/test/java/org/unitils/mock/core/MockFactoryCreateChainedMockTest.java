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
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;
import org.unitils.mock.argumentmatcher.ArgumentMatcherRepository;
import org.unitils.mock.core.proxy.Argument;
import org.unitils.mock.core.proxy.ProxyInvocation;
import org.unitils.mock.core.proxy.ProxyService;
import org.unitils.mock.core.proxy.impl.MatchingProxyInvocationHandler;
import org.unitils.mock.core.proxy.impl.MockProxyInvocationHandler;
import org.unitils.mock.core.util.CloneService;
import org.unitils.mock.core.util.StackTraceService;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class MockFactoryCreateChainedMockTest extends UnitilsJUnit4 {

    private MockFactory mockFactory;

    private Mock<ProxyService> proxyServiceMock;
    @Dummy
    private StackTraceService stackTraceService;
    @Dummy
    private CloneService cloneService;
    @Dummy
    private Properties proxy;
    @Dummy
    private Properties matchingProxy;
    @Dummy
    private Scenario scenario;
    @Dummy
    private ArgumentMatcherRepository argumentMatcherRepository;
    private MockProxyInvocationHandler mockProxyInvocationHandler;
    private MatchingProxyInvocationHandler matchingProxyInvocationHandler;


    @Before
    public void initialize() throws Exception {
        mockFactory = new MockFactory(scenario, argumentMatcherRepository, null, proxyServiceMock.getMock(), stackTraceService, cloneService, null, null, null);

        BehaviorDefiningInvocations behaviorDefiningInvocations = new BehaviorDefiningInvocations();
        matchingProxyInvocationHandler = new MatchingProxyInvocationHandler(argumentMatcherRepository, stackTraceService);
        mockProxyInvocationHandler = new MockProxyInvocationHandler<Properties>(behaviorDefiningInvocations, scenario, cloneService, matchingProxyInvocationHandler);
    }


    @Test
    public void createChainedMock() throws Exception {
        MatchingInvocation matchingInvocation = createMatchingInvocation("name", matchingProxy, "testMethod1");
        proxyServiceMock.returns(proxy).createProxy("name.testMethod1", false, mockProxyInvocationHandler, Properties.class);
        proxyServiceMock.returns(matchingProxy).createProxy("name.testMethod1", false, matchingProxyInvocationHandler, Properties.class);

        Mock<?> result = mockFactory.createChainedMock(matchingInvocation);
        MockObject<?> mockObject = (MockObject<?>) result;
        assertEquals("name.testMethod1", mockObject.name);
        assertEquals(Properties.class, mockObject.type);
        assertSame(proxy, mockObject.proxy);
        assertSame(matchingProxy, mockObject.matchingProxy);
        assertTrue(mockObject.chained);
    }

    @Test
    public void nullForVoidMethod() throws Exception {
        MatchingInvocation matchingInvocation = createMatchingInvocation("name", matchingProxy, "voidMethod");

        Mock<?> result = mockFactory.createChainedMock(matchingInvocation);
        assertNull(result);
    }

    @Test
    public void nullForPrimitiveMethod() throws Exception {
        MatchingInvocation matchingInvocation = createMatchingInvocation("name", matchingProxy, "primitiveMethod");

        Mock<?> result = mockFactory.createChainedMock(matchingInvocation);
        assertNull(result);
    }

    @Test
    public void nullForArrayMethod() throws Exception {
        MatchingInvocation matchingInvocation = createMatchingInvocation("name", matchingProxy, "arrayMethod");

        Mock<?> result = mockFactory.createChainedMock(matchingInvocation);
        assertNull(result);
    }

    @Test
    public void nullForFinalMethod() throws Exception {
        MatchingInvocation matchingInvocation = createMatchingInvocation("name", matchingProxy, "finalMethod");

        Mock<?> result = mockFactory.createChainedMock(matchingInvocation);
        assertNull(result);
    }

    @Test
    public void nullWhenUnableToCreateMock() throws Exception {
        MatchingInvocation matchingInvocation = createMatchingInvocation("name", matchingProxy, "testMethod1");
        proxyServiceMock.raises(NullPointerException.class).createProxy("name.testMethod1", false, mockProxyInvocationHandler, Properties.class);

        Mock<?> result = mockFactory.createChainedMock(matchingInvocation);
        assertNull(result);
    }

    @Test
    public void sameMockReturnedForSameProxyAndMethod() throws Exception {
        MatchingInvocation matchingInvocation1 = createMatchingInvocation("name", matchingProxy, "testMethod1");
        MatchingInvocation matchingInvocation2 = createMatchingInvocation("name", matchingProxy, "testMethod1");

        Mock<?> result1 = mockFactory.createChainedMock(matchingInvocation1);
        Mock<?> result2 = mockFactory.createChainedMock(matchingInvocation2);
        assertSame(result1, result2);
    }

    @Test
    public void differentMockReturnedForDifferentProxy() throws Exception {
        MatchingInvocation matchingInvocation1 = createMatchingInvocation("name", new Properties(), "testMethod1");
        MatchingInvocation matchingInvocation2 = createMatchingInvocation("name", new Properties(), "testMethod1");

        Mock<?> result1 = mockFactory.createChainedMock(matchingInvocation1);
        Mock<?> result2 = mockFactory.createChainedMock(matchingInvocation2);
        assertNotSame(result1, result2);
    }

    @Test
    public void differentMockReturnedForSameProxyButDifferentMethod() throws Exception {
        MatchingInvocation matchingInvocation1 = createMatchingInvocation("name", matchingProxy, "testMethod1");
        MatchingInvocation matchingInvocation2 = createMatchingInvocation("name", matchingProxy, "testMethod2");

        Mock<?> result1 = mockFactory.createChainedMock(matchingInvocation1);
        Mock<?> result2 = mockFactory.createChainedMock(matchingInvocation2);
        assertNotSame(result1, result2);
    }


    private MatchingInvocation createMatchingInvocation(String proxyName, Object proxy, String methodName) throws Exception {
        Method method = MyClass.class.getMethod(methodName);
        List<Argument<?>> arguments = new ArrayList<Argument<?>>();
        ProxyInvocation proxyInvocation = new ProxyInvocation(proxyName, proxy, method, arguments, null);
        return new MatchingInvocation(proxyInvocation, null);
    }

    private static abstract class MyClass {

        public void voidMethod() {
        }

        public abstract int primitiveMethod();

        public Object[] arrayMethod() {
            return null;
        }

        public final Class<?> finalMethod() {
            return null;
        }

        public abstract Properties testMethod1();

        public abstract Properties testMethod2();
    }
}
