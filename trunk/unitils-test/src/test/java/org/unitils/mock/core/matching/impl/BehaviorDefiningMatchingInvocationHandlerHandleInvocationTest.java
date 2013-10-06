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
package org.unitils.mock.core.matching.impl;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;
import org.unitils.mock.argumentmatcher.ArgumentMatcher;
import org.unitils.mock.core.BehaviorDefiningInvocation;
import org.unitils.mock.core.BehaviorDefiningInvocations;
import org.unitils.mock.core.MockService;
import org.unitils.mock.core.proxy.ProxyInvocation;
import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.mockbehavior.impl.ChainedMockBehavior;

import java.lang.reflect.Method;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 */
public class BehaviorDefiningMatchingInvocationHandlerHandleInvocationTest extends UnitilsJUnit4 {

    private BehaviorDefiningMatchingInvocationHandler behaviorDefiningMatchingInvocationHandler;

    private Mock<MockBehavior> mockBehaviorMock;
    private Mock<MockService> mockServiceMock;
    private Mock<BehaviorDefiningInvocations> behaviorDefiningInvocationsMock;
    private Mock<Mock<Map>> chainedMock;
    private Mock<ChainedMockBehavior> chainedMockBehavior;
    private ProxyInvocation proxyInvocation;
    private BehaviorDefiningInvocation behaviorDefiningInvocation;
    @Dummy
    private Map chainedProxy;
    @Dummy
    private ArgumentMatcher argumentMatcher;


    @Before
    public void initialize() throws Exception {
        behaviorDefiningMatchingInvocationHandler = new BehaviorDefiningMatchingInvocationHandler(mockBehaviorMock.getMock(), true, behaviorDefiningInvocationsMock.getMock(), mockServiceMock.getMock());

        Method method = MyInterface.class.getMethod("method");
        proxyInvocation = new ProxyInvocation("mock", null, method, emptyList(), emptyList(), null);
        behaviorDefiningInvocation = new BehaviorDefiningInvocation(proxyInvocation, mockBehaviorMock.getMock(), asList(argumentMatcher), true);
    }


    @Test
    public void handleInvocation() {
        mockServiceMock.returns(chainedMock).createChainedMock("mock.method", Map.class);
        chainedMock.returns(chainedProxy).performs(new ChainedMockBehavior(chainedMock.getMock(), behaviorDefiningInvocation));

        Object result = behaviorDefiningMatchingInvocationHandler.handleInvocation(proxyInvocation, asList(argumentMatcher));
        assertSame(chainedProxy, result);
        behaviorDefiningInvocationsMock.assertInvoked().addBehaviorDefiningInvocation(behaviorDefiningInvocation);
    }

    @Test
    public void nullProxyWhenUnableToCreateChainedMock() throws Throwable {
        mockServiceMock.returns(null).createChainedMock("mock.method", Map.class);

        Object result = behaviorDefiningMatchingInvocationHandler.handleInvocation(proxyInvocation, asList(argumentMatcher));
        assertNull(result);
        behaviorDefiningInvocationsMock.assertInvoked().addBehaviorDefiningInvocation(behaviorDefiningInvocation);
    }

    @Test
    public void installChainWhenInvocationOnChainedMockBehavior() {
        behaviorDefiningMatchingInvocationHandler = new BehaviorDefiningMatchingInvocationHandler(chainedMockBehavior.getMock(), true, behaviorDefiningInvocationsMock.getMock(), mockServiceMock.getMock());

        behaviorDefiningMatchingInvocationHandler.handleInvocation(proxyInvocation, asList(argumentMatcher));
        chainedMockBehavior.assertInvoked().installChain();
    }


    private static interface MyInterface {

        Map method();
    }
}
