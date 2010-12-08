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

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DefaultValueReturningMockBehaviorExecuteNumbersTest {

    /* Tested object */
    private DefaultValueReturningMockBehavior defaultValueReturningMockBehavior = new DefaultValueReturningMockBehavior();

    @Test
    public void intPrimitive() throws Exception {
        ProxyInvocation proxyInvocation = createProxyInvocation(TestClass.class.getMethod("intPrimitiveMethod"));
        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);

        assertTrue(result instanceof Integer);
    }

    @Test
    public void integerValue() throws Exception {
        ProxyInvocation proxyInvocation = createProxyInvocation(TestClass.class.getMethod("integerMethod"));
        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);

        assertTrue(result instanceof Integer);
    }

    @Test
    public void shortPrimitive() throws Exception {
        ProxyInvocation proxyInvocation = createProxyInvocation(TestClass.class.getMethod("shortPrimitiveMethod"));
        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);

        assertTrue(result instanceof Short);
    }

    @Test
    public void shortValue() throws Exception {
        ProxyInvocation proxyInvocation = createProxyInvocation(TestClass.class.getMethod("shortMethod"));
        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);

        assertTrue(result instanceof Short);
    }

    @Test
    public void bigIntegerValue() throws Exception {
        ProxyInvocation proxyInvocation = createProxyInvocation(TestClass.class.getMethod("bigIntegerMethod"));
        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);

        assertTrue(result instanceof BigInteger);
    }

    @Test
    public void longPrimitive() throws Exception {
        ProxyInvocation proxyInvocation = createProxyInvocation(TestClass.class.getMethod("longPrimitiveMethod"));
        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);

        assertTrue(result instanceof Long);
    }

    @Test
    public void longValue() throws Exception {
        ProxyInvocation proxyInvocation = createProxyInvocation(TestClass.class.getMethod("longMethod"));
        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);

        assertTrue(result instanceof Long);
    }

    @Test
    public void bigDecimalValue() throws Exception {
        ProxyInvocation proxyInvocation = createProxyInvocation(TestClass.class.getMethod("bigDecimalMethod"));
        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);

        assertTrue(result instanceof BigDecimal);
    }

    @Test
    public void doublePrimitive() throws Exception {
        ProxyInvocation proxyInvocation = createProxyInvocation(TestClass.class.getMethod("doublePrimitiveMethod"));
        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);

        assertTrue(result instanceof Double);
    }

    @Test
    public void doubleValue() throws Exception {
        ProxyInvocation proxyInvocation = createProxyInvocation(TestClass.class.getMethod("doubleMethod"));
        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);

        assertTrue(result instanceof Double);
    }

    @Test
    public void bytePrimitive() throws Exception {
        ProxyInvocation proxyInvocation = createProxyInvocation(TestClass.class.getMethod("bytePrimitiveMethod"));
        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);

        assertTrue(result instanceof Byte);
    }

    @Test
    public void byteValue() throws Exception {
        ProxyInvocation proxyInvocation = createProxyInvocation(TestClass.class.getMethod("byteMethod"));
        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);

        assertTrue(result instanceof Byte);
    }

    @Test
    public void floatPrimitive() throws Exception {
        ProxyInvocation proxyInvocation = createProxyInvocation(TestClass.class.getMethod("floatPrimitiveMethod"));
        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);

        assertTrue(result instanceof Float);
    }

    @Test
    public void floatValue() throws Exception {
        ProxyInvocation proxyInvocation = createProxyInvocation(TestClass.class.getMethod("floatMethod"));
        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);

        assertTrue(result instanceof Float);
    }

    private ProxyInvocation createProxyInvocation(Method method) {
        return new ProxyInvocation(null, null, method, null, null);
    }


    @SuppressWarnings({"UnusedDeclaration"})
    private static interface TestClass {

        int intPrimitiveMethod();

        Integer integerMethod();

        short shortPrimitiveMethod();

        Short shortMethod();

        BigInteger bigIntegerMethod();

        long longPrimitiveMethod();

        Long longMethod();

        BigDecimal bigDecimalMethod();

        double doublePrimitiveMethod();

        Double doubleMethod();

        byte bytePrimitiveMethod();

        Byte byteMethod();

        float floatPrimitiveMethod();

        Float floatMethod();
    }
}
