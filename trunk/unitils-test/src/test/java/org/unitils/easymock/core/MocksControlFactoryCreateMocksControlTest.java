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
import org.unitils.easymock.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.easymock.internal.MocksControl.MockType;
import static org.unitils.reflectionassert.ReflectionAssert.assertPropertyReflectionEquals;
import static org.unitils.reflectionassert.ReflectionComparatorMode.*;

/**
 * @author Tim Ducheyne
 */
public class MocksControlFactoryCreateMocksControlTest {

    private MocksControlFactory mocksControlFactory;


    @Before
    public void initialize() throws Exception {
        mocksControlFactory = new MocksControlFactory(InvocationOrder.NONE, Calls.STRICT, Order.STRICT, Dates.STRICT, Defaults.STRICT);
    }


    @Test
    public void checkOrderWhenStrictInvocationOrder() {
        MocksControl result = mocksControlFactory.createMocksControl(MyInterface.class, InvocationOrder.STRICT, Calls.STRICT, Order.STRICT, Dates.STRICT, Defaults.STRICT);
        assertPropertyReflectionEquals("type", MockType.DEFAULT, result);
        assertPropertyReflectionEquals("state.recordState.behavior.checkOrder", true, result);
        assertPropertyReflectionEquals("invocationInterceptor.modes", emptyList(), result);
    }

    @Test
    public void niceMockWhenLenientCalls() {
        MocksControl result = mocksControlFactory.createMocksControl(MyInterface.class, InvocationOrder.NONE, Calls.LENIENT, Order.STRICT, Dates.STRICT, Defaults.STRICT);
        assertPropertyReflectionEquals("type", MockType.NICE, result);
        assertPropertyReflectionEquals("state.recordState.behavior.checkOrder", false, result);
        assertPropertyReflectionEquals("invocationInterceptor.modes", emptyList(), result);
    }

    @Test
    public void lenientOrderArgumentMatcherWhenLenientOrder() {
        MocksControl result = mocksControlFactory.createMocksControl(MyInterface.class, InvocationOrder.NONE, Calls.STRICT, Order.LENIENT, Dates.STRICT, Defaults.STRICT);
        assertPropertyReflectionEquals("type", MockType.DEFAULT, result);
        assertPropertyReflectionEquals("state.recordState.behavior.checkOrder", false, result);
        assertPropertyReflectionEquals("invocationInterceptor.modes", asList(LENIENT_ORDER), result);
    }

    @Test
    public void lenientDatesArgumentMatcherWhenLenientDates() {
        MocksControl result = mocksControlFactory.createMocksControl(MyInterface.class, InvocationOrder.NONE, Calls.STRICT, Order.STRICT, Dates.LENIENT, Defaults.STRICT);
        assertPropertyReflectionEquals("type", MockType.DEFAULT, result);
        assertPropertyReflectionEquals("state.recordState.behavior.checkOrder", false, result);
        assertPropertyReflectionEquals("invocationInterceptor.modes", asList(LENIENT_DATES), result);
    }

    @Test
    public void ignoreDefaultsArgumentMatcherWhenIgnoreDefaults() {
        MocksControl result = mocksControlFactory.createMocksControl(MyInterface.class, InvocationOrder.NONE, Calls.STRICT, Order.STRICT, Dates.STRICT, Defaults.IGNORE_DEFAULTS);
        assertPropertyReflectionEquals("type", MockType.DEFAULT, result);
        assertPropertyReflectionEquals("state.recordState.behavior.checkOrder", false, result);
        assertPropertyReflectionEquals("invocationInterceptor.modes", asList(IGNORE_DEFAULTS), result);
    }

    @Test
    public void defaultValues() {
        MocksControl result = mocksControlFactory.createMocksControl(MyInterface.class, InvocationOrder.DEFAULT, Calls.DEFAULT, Order.DEFAULT, Dates.DEFAULT, Defaults.DEFAULT);
        assertPropertyReflectionEquals("type", MockType.DEFAULT, result);
        assertPropertyReflectionEquals("state.recordState.behavior.checkOrder", false, result);
        assertPropertyReflectionEquals("invocationInterceptor.modes", emptyList(), result);
    }


    private static interface MyInterface {
    }
}
