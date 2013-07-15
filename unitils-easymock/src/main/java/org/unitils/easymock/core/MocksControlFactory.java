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

import org.easymock.classextension.internal.MocksClassControl;
import org.easymock.internal.MocksControl;
import org.unitils.easymock.util.*;
import org.unitils.reflectionassert.ReflectionComparatorMode;
import org.unitilsnew.core.annotation.Property;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.internal.MocksControl.MockType.DEFAULT;
import static org.easymock.internal.MocksControl.MockType.NICE;
import static org.unitils.easymock.util.InvocationOrder.STRICT;
import static org.unitils.reflectionassert.ReflectionComparatorMode.*;

/**
 * @author Tim Ducheyne
 */
public class MocksControlFactory {

    protected InvocationOrder defaultInvocationOrder;
    protected Calls defaultCalls;
    protected Order defaultOrder;
    protected Dates defaultDates;
    protected Defaults defaultsDefaults;


    public MocksControlFactory(@Property("easymock.defaultInvocationOrder") InvocationOrder defaultInvocationOrder, @Property("easymock.defaultCalls") Calls defaultCalls,
                               @Property("easymock.defaultOrder") Order defaultOrder, @Property("easymock.defaultDates") Dates defaultDates,
                               @Property("easymock.defaultDefaults") Defaults defaultsDefaults) {
        this.defaultInvocationOrder = defaultInvocationOrder;
        this.defaultCalls = defaultCalls;
        this.defaultOrder = defaultOrder;
        this.defaultDates = defaultDates;
        this.defaultsDefaults = defaultsDefaults;
    }


    /**
     * todo javadoc
     * <p/>
     * Creates an EasyMock mock instance of the given type (class/interface). The type of mock is determined
     * as follows:
     * <p/>
     * If returns is set to LENIENT, a nice mock is created, else a default mock is created
     * If arguments is lenient a lenient control is create, else an EasyMock control is created
     * If order is set to strict, invocation order checking is enabled
     *
     * @param mockType        the type of the mock, not null
     * @param invocationOrder the order setting, not null
     * @param calls           the calls setting, not null
     * @param order           todo
     * @param dates           todo
     * @param defaults        todo
     * @return a mock control for the given class or interface, not null
     */
    public MocksControl createMocksControl(Class<?> mockType, InvocationOrder invocationOrder, Calls calls, Order order, Dates dates, Defaults defaults) {
        if (invocationOrder == InvocationOrder.DEFAULT) {
            invocationOrder = defaultInvocationOrder;
        }
        if (calls == Calls.DEFAULT) {
            calls = defaultCalls;
        }
        if (order == Order.DEFAULT) {
            order = defaultOrder;
        }
        if (dates == Dates.DEFAULT) {
            dates = defaultDates;
        }
        if (defaults == Defaults.DEFAULT) {
            defaults = defaultsDefaults;
        }

        List<ReflectionComparatorMode> comparatorModes = new ArrayList<ReflectionComparatorMode>();
        if (Order.LENIENT == order) {
            comparatorModes.add(LENIENT_ORDER);
        }
        if (Dates.LENIENT == dates) {
            comparatorModes.add(LENIENT_DATES);
        }
        if (Defaults.IGNORE_DEFAULTS == defaults) {
            comparatorModes.add(IGNORE_DEFAULTS);
        }
        MocksControl.MockType type;
        if (Calls.LENIENT == calls) {
            type = NICE;
        } else {
            type = DEFAULT;
        }

        LenientMocksControl mocksControl = new LenientMocksControl(type, comparatorModes.toArray(new ReflectionComparatorMode[comparatorModes.size()]));
        if (InvocationOrder.STRICT == invocationOrder) {
            mocksControl.checkOrder(true);
        }
        return mocksControl;
    }

    /**
     * Creates an EasyMock mock object of the given type.
     *
     * @param mockType        the class type for the mock, not null
     * @param invocationOrder the order setting, not null
     * @param calls           the calls setting, not null
     * @return a mock for the given class or interface, not null
     */
    public MocksControl createRegularMocksControl(Class<?> mockType, InvocationOrder invocationOrder, Calls calls) {
        if (invocationOrder == InvocationOrder.DEFAULT) {
            invocationOrder = defaultInvocationOrder;
        }
        if (calls == Calls.DEFAULT) {
            calls = defaultCalls;
        }

        MocksControl.MockType type;
        if (Calls.LENIENT == calls) {
            type = NICE;
        } else {
            type = DEFAULT;
        }

        MocksControl mocksControl = new MocksClassControl(type);
        if (STRICT == invocationOrder) {
            mocksControl.checkOrder(true);
        }
        return mocksControl;
    }
}
