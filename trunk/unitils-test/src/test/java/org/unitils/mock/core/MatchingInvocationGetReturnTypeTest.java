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

import org.junit.Test;
import org.unitils.mock.core.proxy.Argument;
import org.unitils.mock.core.proxy.ProxyInvocation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * @author Tim Ducheyne
 */
public class MatchingInvocationGetReturnTypeTest {

    private MatchingInvocation matchingInvocation;


    @Test
    public void getReturnType() throws Exception {
        Method method = MyInterface.class.getMethod("method");
        List<Argument<?>> arguments = new ArrayList<Argument<?>>();
        ProxyInvocation proxyInvocation = createProxyInvocation(method);
        matchingInvocation = new MatchingInvocation(proxyInvocation, null);

        Class<?> result = matchingInvocation.getReturnType();
        assertEquals(Properties.class, result);
    }

    @Test
    public void voidReturnType() throws Exception {
        Method method = MyInterface.class.getMethod("voidMethod");
        ProxyInvocation proxyInvocation = createProxyInvocation(method);
        matchingInvocation = new MatchingInvocation(proxyInvocation, null);

        Class<?> result = matchingInvocation.getReturnType();
        assertEquals(Void.class, result);
    }


    private ProxyInvocation createProxyInvocation(Method method) {
        List<Argument<?>> arguments = new ArrayList<Argument<?>>();
        return new ProxyInvocation(null, null, method, arguments, null);
    }


    private static interface MyInterface {

        Properties method();

        Void voidMethod();
    }
}
