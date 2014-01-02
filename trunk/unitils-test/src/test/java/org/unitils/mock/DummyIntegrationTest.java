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
package org.unitils.mock;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import org.unitils.mock.annotation.Dummy;
import org.unitils.mock.core.util.CloneService;
import org.unitils.mock.core.util.ObjectFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DummyIntegrationTest extends UnitilsJUnit4 {

    @Dummy
    private ArrayList<?> dummyList1;
    @Dummy
    private ArrayList<?> dummyList2;
    @Dummy
    private AbstractList<?> dummyAbstractClass;
    @Dummy
    private List<?> dummyInterface;
    @Dummy
    private TestClass dummyTestClass1;
    @Dummy
    private TestClass dummyTestClass2;


    @Test
    public void createConcreteDummy() {
        assertNotNull(dummyList1);
    }

    @Test
    public void dummyAbstractClass() {
        assertNotNull(dummyAbstractClass);
    }

    @Test
    public void dummyInterface() {
        assertNotNull(dummyInterface);
    }

    @Test
    public void noDefaultConstructor() {
        assertNotNull(dummyTestClass1);
    }

    @Test
    public void exceptionWhenCreatingDummyForFinalClass() {
        try {
            MockUnitils.createDummy(FinalTestClass.class);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to create proxy with name finalTestClass for type class org.unitils.mock.DummyIntegrationTest$FinalTestClass\n" +
                    "Reason: IllegalArgumentException: Cannot subclass final class class org.unitils.mock.DummyIntegrationTest$FinalTestClass", e.getMessage());
        }
    }

    @Test
    public void dummyShouldBeEqualWithItself() {
        assertTrue(dummyList1.equals(dummyList1));
        assertTrue(dummyList1.hashCode() == dummyList1.hashCode());
    }

    @Test
    public void dummyShouldNotBeEqualWithOtherDummy() {
        assertFalse(dummyList1.equals(dummyList2));
        assertFalse(dummyList1.hashCode() == dummyList2.hashCode());
    }

    @Test
    public void dummyShouldReturnSameValueForSameMethod() {
        TestClass result1 = dummyTestClass1.getTestClass();
        TestClass result2 = dummyTestClass1.getTestClass();

        assertSame(result1, result2);
        assertEquals(result1, result2);
    }

    @Test
    public void otherDummyShouldReturnDifferentValueForSameMethod() {
        TestClass result1 = dummyTestClass1.getTestClass();
        TestClass result2 = dummyTestClass2.getTestClass();

        assertNotSame(result1, result2);
        assertFalse(result1.equals(result2));
    }

    @Test
    public void dummyReturnsEmptyList() {
        List<?> result = dummyTestClass1.getList();
        assertTrue(result.isEmpty());
    }

    @Test
    public void dummyReturnsEmptyArray() {
        Object[] result = dummyTestClass1.getArray();
        assertEquals(0, result.length);
    }

    @Test
    public void dummyReturnsEmptyString() {
        String result = dummyTestClass1.getString();
        assertEquals("", result);
    }

    @Test
    public void dummyReturnsZeroInt() {
        int result = dummyTestClass1.getInt();
        assertEquals(0, result);
    }

    @Test
    public void dummyReturnsZeroInteger() {
        Integer result = dummyTestClass1.getInteger();
        assertEquals(new Integer("0"), result);
    }

    @Test
    public void dummyReturnsZeroLong() {
        Long result = dummyTestClass1.getLong();
        assertEquals(new Long("0"), result);
    }

    @Test
    public void dummyReturnsZeroShort() {
        Short result = dummyTestClass1.getShort();
        assertEquals(new Short("0"), result);
    }

    @Test
    public void dummyReturnsZeroByte() {
        Byte result = dummyTestClass1.getByte();
        assertEquals(new Byte((byte) 0), result);
    }

    @Test
    public void dummyReturnsZeroFloat() {
        Float result = dummyTestClass1.getFloat();
        assertEquals(new Float("0"), result);
    }

    @Test
    public void dummyReturnsZeroDouble() {
        Double result = dummyTestClass1.getDouble();
        assertEquals(new Double("0"), result);
    }

    @Test
    public void dummyReturnsZeroBigInteger() {
        BigInteger result = dummyTestClass1.getBigInteger();
        assertEquals(new BigInteger("0"), result);
    }

    @Test
    public void dummyReturnsDummyClass() {
        TestClass result = dummyTestClass1.getTestClass();
        assertTrue(result instanceof TestClass);
    }

    @Test
    public void dummyReturnsNullForFinalClass() {
        FinalTestClass result = dummyTestClass1.getFinalTestClass();
        assertNull(result);
    }

    @Test
    public void dummyIgnoresVoidMethodCalls() {
        dummyTestClass1.voidMethod();
    }

    @Test
    public void toStringMethodReturnsDummyName() {
        String result = dummyTestClass1.toString();
        assertEquals("dummyTestClass1", result);
    }

    @Test
    public void deepCloneEqualToOriginal() {
        CloneService cloneService = new CloneService(new ObjectFactory());
        TestClass clone = cloneService.createDeepClone(dummyTestClass1);
        assertEquals(dummyTestClass1, clone);
    }


    private static abstract class TestClass {

        public TestClass(String someValue) {
        }

        abstract List<?> getList();

        abstract Object[] getArray();

        abstract String getString();

        abstract int getInt();

        abstract Integer getInteger();

        abstract Long getLong();

        abstract BigInteger getBigInteger();

        abstract Float getFloat();

        abstract Double getDouble();

        abstract BigDecimal getBigDecimal();

        abstract Short getShort();

        abstract Byte getByte();

        abstract TestClass getTestClass();

        abstract FinalTestClass getFinalTestClass();

        abstract void voidMethod();
    }

    private static final class FinalTestClass {
    }
}
