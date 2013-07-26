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
import org.unitils.easymock.util.Calls;
import org.unitils.easymock.util.InvocationOrder;

import static org.easymock.internal.MocksControl.MockType;
import static org.unitils.reflectionassert.ReflectionAssert.assertPropertyReflectionEquals;

/**
 * @author Tim Ducheyne
 */
public class MocksControlFactoryCreateRegularMocksControlTest {

    private MocksControlFactory mocksControlFactory;


    @Before
    public void initialize() throws Exception {
        mocksControlFactory = new MocksControlFactory(InvocationOrder.NONE, Calls.STRICT, null, null, null);
    }


    @Test
    public void checkOrderWhenStrictInvocationOrder() {
        MocksControl result = mocksControlFactory.createRegularMocksControl(MyInterface.class, InvocationOrder.STRICT, Calls.STRICT);
        assertPropertyReflectionEquals("type", MockType.DEFAULT, result);
        assertPropertyReflectionEquals("state.behavior.checkOrder", true, result);
    }

    @Test
    public void niceMockWhenLenientCalls() {
        MocksControl result = mocksControlFactory.createRegularMocksControl(MyInterface.class, InvocationOrder.NONE, Calls.LENIENT);
        assertPropertyReflectionEquals("type", MockType.NICE, result);
        assertPropertyReflectionEquals("state.behavior.checkOrder", false, result);
    }

    @Test
    public void defaultValues() {
        MocksControl result = mocksControlFactory.createRegularMocksControl(MyInterface.class, InvocationOrder.DEFAULT, Calls.DEFAULT);
        assertPropertyReflectionEquals("type", MockType.DEFAULT, result);
        assertPropertyReflectionEquals("state.behavior.checkOrder", false, result);
    }


    private static interface MyInterface {
    }
}
