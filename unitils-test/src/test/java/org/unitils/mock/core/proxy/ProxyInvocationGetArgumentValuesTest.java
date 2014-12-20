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
package org.unitils.mock.core.proxy;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class ProxyInvocationGetArgumentValuesTest {

    private ProxyInvocation proxyInvocation;

    private List<Argument<?>> arguments;


    @Before
    public void initialize() throws Exception {
        arguments = new ArrayList<Argument<?>>();
        proxyInvocation = new ProxyInvocation(null, null, null, null, arguments, null);
    }


    @Test
    public void getArgumentValues() throws Throwable {
        arguments.add(new Argument<Object>("value1", "copy1", String.class));
        arguments.add(new Argument<Object>("value2", "copy2", String.class));

        List<?> result = proxyInvocation.getArgumentValues();
        assertEquals(asList("value1", "value2"), result);
    }

    @Test
    public void emptyListWhenNoArguments() throws Throwable {
        List<?> result = proxyInvocation.getArgumentValues();
        assertTrue(result.isEmpty());
    }
}
