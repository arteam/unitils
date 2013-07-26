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
package org.unitils.easymock.core;

import org.easymock.internal.MocksControl;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import org.unitils.easymock.util.*;
import org.unitils.mock.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.unitils.mock.MockUnitils.assertNoMoreInvocations;

/**
 * @author Tim Ducheyne
 */
public class MockServiceVerifyTest extends UnitilsJUnit4 {

    private MockService mockService;

    private Mock<MocksControlFactory> mocksControlFactoryMock;
    private Mock<MocksControl> mocksControlMock1;
    private Mock<MocksControl> mocksControlMock2;


    @Before
    public void initialize() throws Exception {
        mockService = new MockService(mocksControlFactoryMock.getMock());

        mocksControlFactoryMock.returns(mocksControlMock1).createMocksControl(MyInterface.class, InvocationOrder.DEFAULT, Calls.DEFAULT, Order.DEFAULT, Dates.DEFAULT, Defaults.IGNORE_DEFAULTS);
        mocksControlFactoryMock.returns(mocksControlMock2).createRegularMocksControl(MyInterface.class, InvocationOrder.DEFAULT, Calls.DEFAULT);
    }


    @Test
    public void verify() {
        mockService.createMock(MyInterface.class, InvocationOrder.DEFAULT, Calls.DEFAULT, Order.DEFAULT, Dates.DEFAULT, Defaults.IGNORE_DEFAULTS);
        mockService.createRegularMock(MyInterface.class, InvocationOrder.DEFAULT, Calls.DEFAULT);

        mockService.verify();
        mocksControlMock1.assertInvoked().verify();
        mocksControlMock2.assertInvoked().verify();
    }

    @Test
    public void exceptionWhenMockIsNotInCorrectState() {
        mockService.createMock(MyInterface.class, InvocationOrder.DEFAULT, Calls.DEFAULT, Order.DEFAULT, Dates.DEFAULT, Defaults.IGNORE_DEFAULTS);
        mocksControlMock1.raises(new IllegalStateException("expected")).verify();
        try {
            mockService.verify();
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to verify mocks control. Be sure to call replay before using the mock.\n" +
                    "Reason: IllegalStateException: expected", e.getMessage());
        }
    }

    @Test
    public void ignoreWhenNoMocks() {
        mockService.verify();
        assertNoMoreInvocations();
    }


    private static interface MyInterface {
    }
}
