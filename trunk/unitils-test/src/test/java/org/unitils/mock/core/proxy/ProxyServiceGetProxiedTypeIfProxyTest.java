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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ProxyServiceGetProxiedTypeIfProxyTest {

    private ProxyService proxyService;


    @Before
    public void initialize() {
        proxyService = new ProxyService(null);
    }


    @Test
    public void proxyInterface() {
        Object proxy = proxyService.createProxy(null, false, new MyProxyInvocationHandler(), TestInterface.class);

        Class<?> result = proxyService.getProxiedTypeIfProxy(proxy);
        assertEquals(TestInterface.class, result);
    }

    @Test
    public void proxyClass() {
        Object proxy = proxyService.createProxy(null, false, new MyProxyInvocationHandler(), TestClass.class);

        Class<?> result = proxyService.getProxiedTypeIfProxy(proxy);
        assertEquals(TestClass.class, result);
    }

    @Test
    public void regularObject() {
        Class<?> result = proxyService.getProxiedTypeIfProxy("notProxy");
        assertNull(result);
    }

    @Test
    public void nullObject() {
        Class<?> result = proxyService.getProxiedTypeIfProxy(null);
        assertNull(result);
    }


    protected static interface TestInterface {
    }

    protected static class TestClass {
    }

    private static class MyProxyInvocationHandler implements ProxyInvocationHandler {

        public Object handleInvocation(ProxyInvocation invocation) throws Throwable {
            return null;
        }
    }
}
