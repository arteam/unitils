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

import static org.junit.Assert.assertEquals;

/**
 * @author Tim Ducheyne
 */
public class ProxyInvocationGetNrOfNotNullArgumentsTest {

    private ProxyInvocation proxyInvocation;

    private List<Argument<?>> arguments;


    @Before
    public void initialize() {
        arguments = new ArrayList<Argument<?>>();
        proxyInvocation = new ProxyInvocation(null, null, null, arguments, null);
    }


    @Test
    public void getNrOfNotNullArguments() throws Throwable {
        arguments.add(new Argument<String>("1", "1", String.class));
        arguments.add(new Argument<String>(null, null, String.class));
        arguments.add(new Argument<String>("2", "2", String.class));

        int result = proxyInvocation.getNrOfNotNullArguments();
        assertEquals(2, result);
    }

    @Test
    public void zeroWhenAllNullArguments() throws Throwable {
        arguments.add(new Argument<String>(null, null, String.class));
        arguments.add(new Argument<String>(null, null, String.class));

        int result = proxyInvocation.getNrOfNotNullArguments();
        assertEquals(0, result);
    }

    @Test
    public void zeroWhenNoArguments() throws Throwable {
        int result = proxyInvocation.getNrOfNotNullArguments();
        assertEquals(0, result);
    }

    @Test
    public void zeroWhenNullArgumentsAtInvocationTime() throws Throwable {
        proxyInvocation = new ProxyInvocation(null, null, null, null, null);

        int result = proxyInvocation.getNrOfNotNullArguments();
        assertEquals(0, result);
    }
}
