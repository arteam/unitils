/*
 *
 *  * Copyright 2010,  Unitils.org
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package org.unitils.mock.dummy;

import junit.framework.Assert;
import org.junit.Test;
import org.unitils.mock.core.proxy.CloneUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DummyObjectFactoryTest {

    /* Tested object */
    private DummyObjectFactory dummyObjectFactory = new DummyObjectFactory();

    @Test
    public void createDummyConcreteClass() {
        ArrayList<?> list = dummyObjectFactory.createDummy(ArrayList.class);
        assertNotNull(list);
    }

    @Test
    public void createDummyAbstractClass() {
        AbstractList<?> list = dummyObjectFactory.createDummy(AbstractList.class);
        assertNotNull(list);
    }

    @Test
    public void createDummyInterface() {
        List<?> list = dummyObjectFactory.createDummy(List.class);
        assertNotNull(list);
    }

    @Test
    public void dummyEqualsHashCode() {
        ArrayList<?> list1 = dummyObjectFactory.createDummy(ArrayList.class);

        assertTrue(list1.equals(list1));
        assertTrue(list1.hashCode() == list1.hashCode());

        ArrayList<?> list2 = dummyObjectFactory.createDummy(ArrayList.class);
        assertFalse(list1.equals(list2));
        assertFalse(list1.hashCode() == list2.hashCode());
    }

    @Test
    public void dummySameValueReturn() {
        TestClass testClass = dummyObjectFactory.createDummy(TestClass.class);
        TestClass result1 = testClass.getTestClass();
        TestClass result2 = testClass.getTestClass();

        assertNotNull(result1);
        assertNotNull(result2);

        Assert.assertEquals(result1, result2);

        TestClass result3 = result1.getTestClass();
        assertNotNull(result3);

        assertFalse(result1.equals(result3));

        TestClass result4 = result3.getTestClass();
        assertNotNull(result4);
    }

    @Test
    public void noDefaultConstructor() {
        TestClass dummy = dummyObjectFactory.createDummy(TestClass.class);
        assertNotNull(dummy);
    }

    @Test
    public void defaultBehavior() {
        TestClass dummy = dummyObjectFactory.createDummy(TestClass.class);
        assertTrue(dummy.getList().isEmpty());
        assertEquals("", dummy.getString());
        assertTrue(dummy.getTestClass() instanceof TestClass);
        assertEquals(0, dummy.getInt());
        assertEquals(new Integer(0), dummy.getInteger());
        assertEquals(Long.valueOf(0), dummy.getLong());
        assertEquals(new Integer(0), dummy.getInteger());
        assertEquals((float) 0, dummy.getFloat());
        assertEquals(new BigInteger("0"), dummy.getBigInteger());
        assertEquals((double) 0, dummy.getDouble());
        assertEquals(new Short("0"), dummy.getShort());
        assertEquals(new BigDecimal("0"), dummy.getBigDecimal());
        assertEquals(new Byte("0"), dummy.getByte());
        assertTrue(dummy.getArray() instanceof Object[]);
    }

    @Test
    public void instanceOfDummyObject() {
        TestClass dummy = dummyObjectFactory.createDummy(TestClass.class);
        assertTrue(dummy instanceof DummyObject);
        List<?> dummyList = dummyObjectFactory.createDummy(List.class);
        assertTrue(dummyList instanceof DummyObject);
    }

    @Test
    public void toStringMethod() {
        TestClass dummy = dummyObjectFactory.createDummy(TestClass.class);
        assertEquals("TestClass@" + Integer.toHexString(dummy.hashCode()), dummy.toString());
    }

    @Test
    public void deepCloneEqualToOriginal() {
        TestClass dummy = dummyObjectFactory.createDummy(TestClass.class);
        TestClass clone = CloneUtil.createDeepClone(dummy);
        assertEquals(dummy, clone);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    private class TestClass {

        public TestClass(String someValue) {
        }

        public List<?> getList() {
            return null;
        }

        public Object[] getArray() {
            return new TestClass[]{
                    new TestClass("test 123")
            };
        }

        public String getString() {
            return "someString";
        }

        public int getInt() {
            return 20;
        }

        public Integer getInteger() {
            return 20;
        }

        public Long getLong() {
            return 50L;
        }

        public BigInteger getBigInteger() {
            return new BigInteger("5000");
        }

        public Float getFloat() {
            return 50f;
        }

        public Double getDouble() {
            return 50d;
        }

        public BigDecimal getBigDecimal() {
            return new BigDecimal("50");
        }

        public Short getShort() {
            return (short) 0;
        }

        public Byte getByte() {
            return new Byte("1");
        }

        public TestClass getTestClass() {
            return new TestClass("internal test class");
        }
    }
}
