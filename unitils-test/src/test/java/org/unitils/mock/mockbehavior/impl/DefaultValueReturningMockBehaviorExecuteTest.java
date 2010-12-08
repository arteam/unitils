/*
 * Copyright Unitils.org
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

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DefaultValueReturningMockBehaviorExecuteTest {

    /* Tested object */
    private DefaultValueReturningMockBehavior defaultValueReturningMockBehavior = new DefaultValueReturningMockBehavior();

    @Test
    public void list() throws Exception {
        ProxyInvocation proxyInvocation = createProxyInvocation(TestClass.class.getMethod("listMethod"));
        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);

        assertTrue(result instanceof List);
    }

    @Test
    public void set() throws Exception {
        ProxyInvocation proxyInvocation = createProxyInvocation(TestClass.class.getMethod("setMethod"));
        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);

        assertTrue(result instanceof Set);
    }

    @Test
    public void map() throws Exception {
        ProxyInvocation proxyInvocation = createProxyInvocation(TestClass.class.getMethod("mapMethod"));
        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);

        assertTrue(result instanceof Map);
    }

    @Test
    public void collection() throws Exception {
        ProxyInvocation proxyInvocation = createProxyInvocation(TestClass.class.getMethod("collectionMethod"));
        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);

        assertTrue(result instanceof Collection);
    }

    @Test
    public void array() throws Exception {
        ProxyInvocation proxyInvocation = createProxyInvocation(TestClass.class.getMethod("arrayMethod"));
        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);

        assertTrue(result instanceof String[]);
    }

    @Test
    public void booleanPrimitiveValue() throws Exception {
        ProxyInvocation proxyInvocation = createProxyInvocation(TestClass.class.getMethod("booleanPrimitiveMethod"));
        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);

        assertTrue(result instanceof Boolean);
    }

    @Test
    public void booleanValue() throws Exception {
        ProxyInvocation proxyInvocation = createProxyInvocation(TestClass.class.getMethod("booleanMethod"));
        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);

        assertTrue(result instanceof Boolean);
    }

    @Test
    public void nullForObject() throws Exception {
        ProxyInvocation proxyInvocation = createProxyInvocation(TestClass.class.getMethod("dataSourceMethod"));
        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);

        assertNull(result);
    }


    private ProxyInvocation createProxyInvocation(Method method) {
        return new ProxyInvocation(null, null, method, null, null);
    }


    @SuppressWarnings({"UnusedDeclaration"})
    private static interface TestClass {

        List listMethod();

        Set setMethod();

        Map mapMethod();

        Collection collectionMethod();

        String[] arrayMethod();

        boolean booleanPrimitiveMethod();

        Boolean booleanMethod();

        DataSource dataSourceMethod();

    }
}
