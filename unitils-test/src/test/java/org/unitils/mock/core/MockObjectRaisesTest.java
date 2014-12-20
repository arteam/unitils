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
import org.unitils.mock.core.matching.MatchingInvocationHandler;
import org.unitils.mock.core.matching.MatchingInvocationHandlerFactory;
import org.unitils.mock.core.proxy.impl.MatchingProxyInvocationHandler;
import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.mockbehavior.MockBehaviorFactory;

import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 */
public class MockObjectRaisesTest extends UnitilsJUnit4 {

    private MockObject<Object> mockObject;

    private Mock<MockBehaviorFactory> mockBehaviorFactoryMock;
    private Mock<MatchingInvocationHandlerFactory> matchingInvocationHandlerFactoryMock;
    private Mock<MatchingProxyInvocationHandler> matchingProxyInvocationHandlerMock;
    @Dummy
    private Object matchingProxy;
    @Dummy
    private MockBehavior mockBehavior;
    @Dummy
    private BehaviorDefiningInvocations behaviorDefiningInvocations;
    @Dummy
    private MatchingInvocationHandler matchingInvocationHandler;


    @Before
    public void initialize() {
        mockObject = new MockObject<Object>("name", Object.class, null, matchingProxy, false, behaviorDefiningInvocations,
                matchingProxyInvocationHandlerMock.getMock(), mockBehaviorFactoryMock.getMock(), matchingInvocationHandlerFactoryMock.getMock(), null, null);
    }


    @Test
    public void raises() {
        mockBehaviorFactoryMock.returns(mockBehavior).createExceptionThrowingMockBehavior(NullPointerException.class);
        matchingInvocationHandlerFactoryMock.returns(matchingInvocationHandler).createBehaviorDefiningMatchingInvocationHandler(mockBehavior, false, behaviorDefiningInvocations);

        Object result = mockObject.raises(NullPointerException.class);
        assertSame(matchingProxy, result);
        matchingProxyInvocationHandlerMock.assertInvoked().startMatchingInvocation("name", true, matchingInvocationHandler);
    }

    @Test
    public void raisesInstance() {
        mockBehaviorFactoryMock.returns(mockBehavior).createExceptionThrowingMockBehavior(new NullPointerException("expected"));
        matchingInvocationHandlerFactoryMock.returns(matchingInvocationHandler).createBehaviorDefiningMatchingInvocationHandler(mockBehavior, false, behaviorDefiningInvocations);

        Object result = mockObject.raises(new NullPointerException("expected"));
        assertSame(matchingProxy, result);
        matchingProxyInvocationHandlerMock.assertInvoked().startMatchingInvocation("name", true, matchingInvocationHandler);
    }

    @Test
    public void chainedRaises() {
        mockObject = new MockObject<Object>("name", Object.class, null, matchingProxy, true, behaviorDefiningInvocations,
                matchingProxyInvocationHandlerMock.getMock(), mockBehaviorFactoryMock.getMock(), matchingInvocationHandlerFactoryMock.getMock(), null, null);
        mockBehaviorFactoryMock.returns(mockBehavior).createExceptionThrowingMockBehavior(NullPointerException.class);
        matchingInvocationHandlerFactoryMock.returns(matchingInvocationHandler).createBehaviorDefiningMatchingInvocationHandler(mockBehavior, false, behaviorDefiningInvocations);

        mockObject.raises(NullPointerException.class);
        matchingProxyInvocationHandlerMock.assertInvoked().startMatchingInvocation("name", false, matchingInvocationHandler);
    }

    @Test
    public void chainedRaisesInstance() {
        mockObject = new MockObject<Object>("name", Object.class, null, matchingProxy, true, behaviorDefiningInvocations,
                matchingProxyInvocationHandlerMock.getMock(), mockBehaviorFactoryMock.getMock(), matchingInvocationHandlerFactoryMock.getMock(), null, null);
        mockBehaviorFactoryMock.returns(mockBehavior).createExceptionThrowingMockBehavior(new NullPointerException("expected"));
        matchingInvocationHandlerFactoryMock.returns(matchingInvocationHandler).createBehaviorDefiningMatchingInvocationHandler(mockBehavior, false, behaviorDefiningInvocations);

        mockObject.raises(new NullPointerException("expected"));
        matchingProxyInvocationHandlerMock.assertInvoked().startMatchingInvocation("name", false, matchingInvocationHandler);
    }
}
