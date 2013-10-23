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
import org.unitils.mock.core.proxy.ProxyService;
import org.unitils.mock.core.proxy.impl.DummyProxyInvocationHandler;
import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.mockbehavior.MockBehaviorFactory;

import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 */
public class MockFactoryCreateDummyTest extends UnitilsJUnit4 {

    private MockFactory mockFactory;

    private Mock<ProxyService> proxyServiceMock;
    private Mock<MockBehaviorFactory> mockBehaviorFactoryMock;
    @Dummy
    private MyInterface myInterfaceProxy;
    @Dummy
    private MockBehavior mockBehavior;


    @Before
    public void initialize() {
        mockFactory = new MockFactory(null, null, mockBehaviorFactoryMock.getMock(), proxyServiceMock.getMock(), null, null, null);
    }


    @Test
    public void createDummy() {
        mockBehaviorFactoryMock.returns(mockBehavior).createDummyValueReturningMockBehavior(mockFactory);
        proxyServiceMock.returns(myInterfaceProxy).createProxy("myDummy", false, new DummyProxyInvocationHandler(mockBehavior), MyInterface.class);

        MyInterface result = mockFactory.createDummy("myDummy", MyInterface.class);
        assertSame(myInterfaceProxy, result);
    }

    @Test
    public void defaultName() {
        mockBehaviorFactoryMock.returns(mockBehavior).createDummyValueReturningMockBehavior(mockFactory);
        proxyServiceMock.returns(myInterfaceProxy).createProxy("myInterface", false, new DummyProxyInvocationHandler(mockBehavior), MyInterface.class);

        MyInterface result = mockFactory.createDummy(null, MyInterface.class);
        assertSame(myInterfaceProxy, result);
    }


    public static interface MyInterface {

        int test();
    }
}
