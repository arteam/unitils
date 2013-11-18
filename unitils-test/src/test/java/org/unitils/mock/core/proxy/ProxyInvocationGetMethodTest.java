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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 */
public class ProxyInvocationGetMethodTest {

    private ProxyInvocation proxyInvocation;

    private Method method;


    @Before
    public void initialize() throws Exception {
        method = MyInterface.class.getMethod("method");
        List<Argument<?>> arguments = new ArrayList<Argument<?>>();
        proxyInvocation = new ProxyInvocation(null, null, method, arguments, null);
    }


    @Test
    public void getMethod() throws Throwable {
        Method result = proxyInvocation.getMethod();
        assertSame(method, result);
    }


    private static interface MyInterface {

        void method();
    }
}
