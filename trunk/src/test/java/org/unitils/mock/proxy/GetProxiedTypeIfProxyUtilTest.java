/*
 * Copyright 2006-2009,  Unitils.org
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
package org.unitils.mock.proxy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import static org.unitils.mock.proxy.ProxyUtils.createProxy;
import static org.unitils.mock.proxy.ProxyUtils.getProxiedTypeIfProxy;

/**
 * Tests the get proxied type behavior
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class GetProxiedTypeIfProxyUtilTest {


    @Test
    public void proxyInterface() {
        Object proxy = createProxy("mock", TestInterface.class, new DummyProxyInvocationHandler());
        Class<?> result = getProxiedTypeIfProxy(proxy);

        assertEquals(TestInterface.class, result);
    }


    @Test
    public void proxyClass() {
        Object proxy = createProxy("mock", TestClass.class, new DummyProxyInvocationHandler());
        Class<?> result = getProxiedTypeIfProxy(proxy);

        assertEquals(TestClass.class, result);
    }


    @Test
    public void regularObject() {
        Class<?> result = getProxiedTypeIfProxy("notproxy");
        assertNull(result);
    }


    @Test
    public void nullObject() {
        Class<?> result = getProxiedTypeIfProxy(null);
        assertNull(result);
    }


    protected static interface TestInterface {
    }


    protected static class TestClass {
    }


    private static class DummyProxyInvocationHandler implements ProxyInvocationHandler {

        public Object handleInvocation(ProxyInvocation invocation) throws Throwable {
            return null;
        }
    }

}
