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

import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 */
public class MockObjectAssertNotInvokedTest extends UnitilsJUnit4 {

    private MockObject<Object> mockObject;

    private Mock<MatchingInvocationHandlerFactory> matchingInvocationHandlerFactoryMock;
    private Mock<MatchingProxyInvocationHandler> matchingProxyInvocationHandlerMock;
    @Dummy
    private Object matchingProxy;
    @Dummy
    private MatchingInvocationHandler matchingInvocationHandler;


    @Before
    public void initialize() {
        mockObject = new MockObject<Object>("name", Object.class, null, matchingProxy, false, null,
                matchingProxyInvocationHandlerMock.getMock(), null, matchingInvocationHandlerFactoryMock.getMock());
    }


    @Test
    public void assertNotInvoked() {
        matchingInvocationHandlerFactoryMock.returns(matchingInvocationHandler).createAssertNotInvokedVerifyingMatchingInvocationHandler();

        Object result = mockObject.assertNotInvoked();
        assertSame(matchingProxy, result);
        matchingProxyInvocationHandlerMock.assertInvoked().startMatchingInvocation("name", true, matchingInvocationHandler);
    }

    @Test
    public void chainedAssertNotInvoked() {
        mockObject = new MockObject<Object>("name", Object.class, null, matchingProxy, true, null,
                matchingProxyInvocationHandlerMock.getMock(), null, matchingInvocationHandlerFactoryMock.getMock());
        matchingInvocationHandlerFactoryMock.returns(matchingInvocationHandler).createAssertNotInvokedVerifyingMatchingInvocationHandler();

        Object result = mockObject.assertNotInvoked();
        assertSame(matchingProxy, result);
        matchingProxyInvocationHandlerMock.assertInvoked().startMatchingInvocation("name", false, matchingInvocationHandler);
    }
}
