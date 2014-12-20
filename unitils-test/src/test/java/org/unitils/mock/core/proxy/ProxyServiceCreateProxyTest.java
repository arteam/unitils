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
import org.unitils.core.UnitilsException;
import org.unitils.core.util.ObjectToFormat;

import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class ProxyServiceCreateProxyTest {

    private ProxyService proxyService;


    @Before
    public void initialize() {
        proxyService = new ProxyService(null);
    }


    @Test
    public void createProxyForInterface() {
        TestInterface result = proxyService.createProxy("1", "name", true, new MyProxyInvocationHandler(), TestInterface.class);
        assertTrue(result instanceof ObjectToFormat);
        assertTrue(result instanceof Cloneable);
        assertEquals("Proxy<name>", ((ObjectToFormat) result).$formatObject());
        assertEquals("Handled by MyProxyInvocationHandler: proxyId: 1, proxyName: name", result.method());
    }

    @Test
    public void customInterfaces() {
        Object result = proxyService.createProxy("1", "name", true, new MyProxyInvocationHandler(), TestClass.class, CustomInterface1.class, CustomInterface2.class);
        assertTrue(result instanceof CustomInterface1);
        assertTrue(result instanceof CustomInterface2);
    }

    @Test
    public void createInitializedProxy() {
        TestClass result = proxyService.createProxy("1", "name", true, new MyProxyInvocationHandler(), TestClass.class);
        assertEquals("test", result.value);
    }

    @Test
    public void createUninitializedProxy() {
        TestClass result = proxyService.createProxy("1", "name", false, new MyProxyInvocationHandler(), TestClass.class);
        assertNull(result.value);
    }

    @Test
    public void exceptionWhenUnableToAccessConstructor() {
        try {
            proxyService.createProxy("1", "name", true, new MyProxyInvocationHandler(), PrivateTestClass.class);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to create proxy with name name for type class org.unitils.mock.core.proxy.ProxyServiceCreateProxyTest$PrivateTestClass\n" +
                    "Reason: IllegalArgumentException: No visible constructors in class org.unitils.mock.core.proxy.ProxyServiceCreateProxyTest$PrivateTestClass", e.getMessage());
        }
    }


    private static interface TestInterface {

        String method();
    }

    public static class TestClass {

        private String value = "test";
    }

    private static class PrivateTestClass {
    }

    private static interface CustomInterface1 {
    }

    private static interface CustomInterface2 {
    }

    private static class MyProxyInvocationHandler implements ProxyInvocationHandler {

        public Object handleInvocation(ProxyInvocation invocation) throws Throwable {
            return "Handled by MyProxyInvocationHandler: proxyId: " + invocation.getProxyId() + ", proxyName: " + invocation.getProxyName();
        }
    }
}
