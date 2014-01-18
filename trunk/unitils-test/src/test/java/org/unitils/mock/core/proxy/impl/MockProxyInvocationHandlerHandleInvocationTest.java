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
package org.unitils.mock.core.proxy.impl;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import org.unitils.mock.Mock;
import org.unitils.mock.argumentmatcher.Capture;
import org.unitils.mock.core.BehaviorDefiningInvocation;
import org.unitils.mock.core.BehaviorDefiningInvocations;
import org.unitils.mock.core.ObservedInvocation;
import org.unitils.mock.core.Scenario;
import org.unitils.mock.core.proxy.Argument;
import org.unitils.mock.core.proxy.ProxyInvocation;
import org.unitils.mock.core.util.CloneService;
import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.mockbehavior.ValidatableMockBehavior;
import org.unitils.mock.mockbehavior.impl.DefaultValueReturningMockBehavior;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.unitils.mock.ArgumentMatchers.get;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * @author Tim Ducheyne
 */
public class MockProxyInvocationHandlerHandleInvocationTest extends UnitilsJUnit4 {

    private MockProxyInvocationHandler mockProxyInvocationHandler;

    private Mock<BehaviorDefiningInvocations> behaviorDefiningInvocationsMock;
    private Mock<BehaviorDefiningInvocation> behaviorDefiningInvocationMock;
    private Mock<Scenario> scenarioMock;
    private Mock<CloneService> cloneServiceMock;
    private Mock<MatchingProxyInvocationHandler> matchingProxyInvocationHandlerMock;
    private Mock<MockBehavior> mockBehaviorMock;
    private Mock<ValidatableMockBehavior> validatableMockBehaviorMock;

    private ProxyInvocation proxyInvocation;
    private StackTraceElement[] stackTrace;


    @Before
    public void initialize() throws Throwable {
        mockProxyInvocationHandler = new MockProxyInvocationHandler(behaviorDefiningInvocationsMock.getMock(), scenarioMock.getMock(), cloneServiceMock.getMock(), matchingProxyInvocationHandlerMock.getMock());

        behaviorDefiningInvocationsMock.returns(behaviorDefiningInvocationMock).getMatchingBehaviorDefiningInvocation(proxyInvocation);
        behaviorDefiningInvocationMock.returns(mockBehaviorMock).getMockBehavior();
        mockBehaviorMock.returns("result").execute(proxyInvocation);
        cloneServiceMock.returns("cloned result").createDeepClone("result");
        Method method = MyInterface.class.getMethod("testMethod");
        proxyInvocation = createProxyInvocation(method);

        stackTrace = new StackTraceElement[]{new StackTraceElement("class", "method", "file", 1)};
        behaviorDefiningInvocationMock.returns(stackTrace).getInvokedAtTrace();
    }


    @Test
    public void handleInvocation() throws Throwable {
        Object result = mockProxyInvocationHandler.handleInvocation(proxyInvocation);
        assertEquals("result", result);
        Capture<ObservedInvocation> capture = new Capture<ObservedInvocation>(ObservedInvocation.class);
        scenarioMock.assertInvoked().addObservedInvocation(get(capture));
        ObservedInvocation observedInvocation = capture.getValue();
        assertEquals("result", observedInvocation.getResult());
        assertEquals("cloned result", observedInvocation.getResultAtInvocationTime());
        assertSame(behaviorDefiningInvocationMock.getMock(), observedInvocation.getBehaviorDefiningInvocation());
        assertSame(mockBehaviorMock.getMock(), observedInvocation.getMockBehavior());
    }

    @Test
    public void defaultValueWhenNoMockBehavior() throws Throwable {
        behaviorDefiningInvocationsMock.returns(null).getMatchingBehaviorDefiningInvocation(proxyInvocation);

        Object result = mockProxyInvocationHandler.handleInvocation(proxyInvocation);
        assertNull(result);
        Capture<ObservedInvocation> capture = new Capture<ObservedInvocation>(ObservedInvocation.class);
        scenarioMock.assertInvoked().addObservedInvocation(get(capture));
        ObservedInvocation observedInvocation = capture.getValue();
        assertTrue(observedInvocation.getMockBehavior() instanceof DefaultValueReturningMockBehavior);
    }

    @Test
    public void nullResultWhenNoMockBehaviorAndVoidMethod() throws Throwable {
        Method method = MyInterface.class.getMethod("voidMethod");
        proxyInvocation = createProxyInvocation(method);
        behaviorDefiningInvocationsMock.returns(null).getMatchingBehaviorDefiningInvocation(proxyInvocation);

        Object result = mockProxyInvocationHandler.handleInvocation(proxyInvocation);
        assertNull(result);
        Capture<ObservedInvocation> capture = new Capture<ObservedInvocation>(ObservedInvocation.class);
        scenarioMock.assertInvoked().addObservedInvocation(get(capture));
        ObservedInvocation observedInvocation = capture.getValue();
        assertNull(observedInvocation.getResult());
        assertNull(observedInvocation.getResultAtInvocationTime());
    }

    @Test
    public void exceptionWhenPreviousMatchingInvocationNotCompleted() throws Throwable {
        UnitilsException exception = new UnitilsException("expected");
        matchingProxyInvocationHandlerMock.raises(exception).assertPreviousMatchingInvocationCompleted();
        try {
            mockProxyInvocationHandler.handleInvocation(proxyInvocation);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertSame(exception, e);
        }
    }

    @Test
    public void validatableMockBehaviorIsValidated() throws Throwable {
        behaviorDefiningInvocationMock.returns(validatableMockBehaviorMock).getMockBehavior();

        mockProxyInvocationHandler.handleInvocation(proxyInvocation);
        validatableMockBehaviorMock.assertInvoked().assertCanExecute(proxyInvocation);
    }

    @Test
    public void exceptionWhenMockBehaviorIsNotValid() throws Throwable {
        UnitilsException exception = new UnitilsException("expected");
        behaviorDefiningInvocationMock.returns(validatableMockBehaviorMock).getMockBehavior();
        validatableMockBehaviorMock.raises(exception).assertCanExecute(proxyInvocation);
        try {
            mockProxyInvocationHandler.handleInvocation(proxyInvocation);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertSame(exception, e);
            assertReflectionEquals(stackTrace, e.getStackTrace());
        }
    }


    private ProxyInvocation createProxyInvocation(Method method) throws NoSuchMethodException {
        List<Argument<?>> arguments = new ArrayList<Argument<?>>();
        return new ProxyInvocation(null, null, null, method, arguments, null);
    }

    private static interface MyInterface {

        String testMethod();

        void voidMethod();
    }
}
