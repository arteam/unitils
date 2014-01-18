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
package org.unitils.mock.mockbehavior.impl;

import org.junit.Test;
import org.unitils.mock.core.proxy.ProxyInvocation;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DefaultValueReturningMockBehaviorExecuteTest {

    private DefaultValueReturningMockBehavior defaultValueReturningMockBehavior = new DefaultValueReturningMockBehavior();


    @Test
    public void proxyNameForToStringMethod() throws Exception {
        Method method = Object.class.getMethod("toString");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, "proxy name", null, method, null, null);

        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);
        assertEquals("proxy name", result);
    }

    @Test
    public void list() throws Exception {
        Method method = TestClass.class.getMethod("listMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, null, method, null, null);

        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);
        assertTrue(result instanceof List);
    }

    @Test
    public void set() throws Exception {
        Method method = TestClass.class.getMethod("setMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, null, method, null, null);

        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);
        assertTrue(result instanceof Set);
    }

    @Test
    public void map() throws Exception {
        Method method = TestClass.class.getMethod("mapMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, null, method, null, null);

        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);
        assertTrue(result instanceof Map);
    }

    @Test
    public void collection() throws Exception {
        Method method = TestClass.class.getMethod("collectionMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, null, method, null, null);

        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);
        assertTrue(result instanceof Collection);
    }

    @Test
    public void array() throws Exception {
        Method method = TestClass.class.getMethod("arrayMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, null, method, null, null);

        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);
        assertTrue(result instanceof String[]);
    }

    @Test
    public void booleanPrimitiveValue() throws Exception {
        Method method = TestClass.class.getMethod("booleanPrimitiveMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, null, method, null, null);

        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);
        assertTrue(result instanceof Boolean);
    }

    @Test
    public void booleanValue() throws Exception {
        Method method = TestClass.class.getMethod("booleanMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, null, method, null, null);

        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);
        assertTrue(result instanceof Boolean);
    }

    @Test
    public void nullForObject() throws Exception {
        Method method = TestClass.class.getMethod("dataSourceMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, null, method, null, null);

        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);
        assertNull(result);
    }

    @Test
    public void nullForVoid() throws Exception {
        Method method = TestClass.class.getMethod("voidMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, null, method, null, null);

        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);
        assertNull(result);
    }


    private static interface TestClass {

        List listMethod();

        Set setMethod();

        Map mapMethod();

        Collection collectionMethod();

        String[] arrayMethod();

        boolean booleanPrimitiveMethod();

        Boolean booleanMethod();

        DataSource dataSourceMethod();

        void voidMethod();
    }
}
