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
import org.unitils.mock.core.proxy.Argument;
import org.unitils.mock.core.proxy.ProxyInvocation;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Tim Ducheyne
 */
public class ObservedInvocationGetResultAtInvocationTimeTest extends UnitilsJUnit4 {

    private ObservedInvocation observedInvocation;


    @Before
    public void initialize() {
        List<Argument<?>> arguments = new ArrayList<Argument<?>>();
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, null, null, arguments, null);
        observedInvocation = new ObservedInvocation(proxyInvocation, null, null);
    }


    @Test
    public void getResultAtInvocationTime() {
        observedInvocation.setResult("a", "b");

        Object result = observedInvocation.getResultAtInvocationTime();
        assertEquals("b", result);
    }

    @Test
    public void nullWhenNoResultAtInvocationTimeSet() {
        Object result = observedInvocation.getResultAtInvocationTime();
        assertNull(result);
    }
}
