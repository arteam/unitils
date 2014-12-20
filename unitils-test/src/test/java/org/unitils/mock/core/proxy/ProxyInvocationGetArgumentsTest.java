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

import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 */
public class ProxyInvocationGetArgumentsTest {

    private ProxyInvocation proxyInvocation;

    private List<Argument<?>> arguments;


    @Before
    public void initialize() throws Exception {
        arguments = new ArrayList<Argument<?>>();
        proxyInvocation = new ProxyInvocation(null, null, null, null, arguments, null);
    }


    @Test
    public void getArguments() throws Throwable {
        List<?> result = proxyInvocation.getArguments();
        assertSame(arguments, result);
    }
}
