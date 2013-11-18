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

import org.apache.commons.lang.mutable.MutableByte;
import org.junit.Test;
import org.unitils.mock.core.proxy.ProxyInvocation;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DefaultValueReturningMockBehaviorExecuteNumbersTest {

    private DefaultValueReturningMockBehavior defaultValueReturningMockBehavior = new DefaultValueReturningMockBehavior();


    @Test
    public void intPrimitive() throws Exception {
        Method method = TestClass.class.getMethod("intPrimitiveMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, method, null, null);

        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);
        assertTrue(result instanceof Integer);
        assertEquals(0, ((Integer) result).intValue());
    }

    @Test
    public void integerValue() throws Exception {
        Method method = TestClass.class.getMethod("integerMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, method, null, null);

        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);
        assertTrue(result instanceof Integer);
        assertEquals(0, ((Integer) result).intValue());
    }

    @Test
    public void shortPrimitive() throws Exception {
        Method method = TestClass.class.getMethod("shortPrimitiveMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, method, null, null);

        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);
        assertTrue(result instanceof Short);
        assertEquals(0, ((Short) result).intValue());
    }

    @Test
    public void shortValue() throws Exception {
        Method method = TestClass.class.getMethod("shortMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, method, null, null);

        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);
        assertTrue(result instanceof Short);
        assertEquals(0, ((Short) result).intValue());
    }

    @Test
    public void bigIntegerValue() throws Exception {
        Method method = TestClass.class.getMethod("bigIntegerMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, method, null, null);

        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);
        assertTrue(result instanceof BigInteger);
        assertEquals(0, ((BigInteger) result).intValue());
    }

    @Test
    public void longPrimitive() throws Exception {
        Method method = TestClass.class.getMethod("longPrimitiveMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, method, null, null);

        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);
        assertTrue(result instanceof Long);
        assertEquals(0, ((Long) result).intValue());
    }

    @Test
    public void longValue() throws Exception {
        Method method = TestClass.class.getMethod("longMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, method, null, null);

        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);
        assertTrue(result instanceof Long);
        assertEquals(0, ((Long) result).intValue());
    }

    @Test
    public void bigDecimalValue() throws Exception {
        Method method = TestClass.class.getMethod("bigDecimalMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, method, null, null);

        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);
        assertTrue(result instanceof BigDecimal);
        assertEquals(0, ((BigDecimal) result).intValue());
    }

    @Test
    public void doublePrimitive() throws Exception {
        Method method = TestClass.class.getMethod("doublePrimitiveMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, method, null, null);

        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);
        assertTrue(result instanceof Double);
        assertEquals(0, ((Double) result).intValue());
    }

    @Test
    public void doubleValue() throws Exception {
        Method method = TestClass.class.getMethod("doubleMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, method, null, null);

        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);
        assertTrue(result instanceof Double);
        assertEquals(0, ((Double) result).intValue());
    }

    @Test
    public void bytePrimitive() throws Exception {
        Method method = TestClass.class.getMethod("bytePrimitiveMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, method, null, null);

        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);
        assertTrue(result instanceof Byte);
        assertEquals(0, ((Byte) result).intValue());
    }

    @Test
    public void byteValue() throws Exception {
        Method method = TestClass.class.getMethod("byteMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, method, null, null);

        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);
        assertTrue(result instanceof Byte);
        assertEquals(0, ((Byte) result).intValue());
    }

    @Test
    public void floatPrimitive() throws Exception {
        Method method = TestClass.class.getMethod("floatPrimitiveMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, method, null, null);

        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);
        assertTrue(result instanceof Float);
        assertEquals(0, ((Float) result).intValue());
    }

    @Test
    public void floatValue() throws Exception {
        Method method = TestClass.class.getMethod("floatMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, method, null, null);

        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);
        assertTrue(result instanceof Float);
        assertEquals(0, ((Float) result).intValue());
    }

    @Test
    public void atomicIntegerValue() throws Exception {
        Method method = TestClass.class.getMethod("atomicIntegerMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, method, null, null);

        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);
        assertTrue(result instanceof AtomicInteger);
        assertEquals(0, ((AtomicInteger) result).intValue());
    }

    @Test
    public void atomicLongValue() throws Exception {
        Method method = TestClass.class.getMethod("atomicLongMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, method, null, null);

        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);
        assertTrue(result instanceof AtomicLong);
        assertEquals(0, ((AtomicLong) result).intValue());
    }

    @Test
    public void nullWhenUnknownNumberType() throws Exception {
        Method method = TestClass.class.getMethod("customNumberTypeMethod");
        ProxyInvocation proxyInvocation = new ProxyInvocation(null, null, method, null, null);

        Object result = defaultValueReturningMockBehavior.execute(proxyInvocation);
        assertNull(result);
    }


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

        AtomicInteger atomicIntegerMethod();

        AtomicLong atomicLongMethod();

        MutableByte customNumberTypeMethod();

    }
}
