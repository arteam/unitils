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

import java.util.List;
import java.util.Properties;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.unitils.core.util.CollectionUtils.asSet;

/**
 * @author Tim Ducheyne
 */
public class MockReturnsAllIntegrationTest extends UnitilsJUnit4 {

    private Mock<TestInterface> mockObject;


    @Test
    public void list() {
        mockObject.returnsAll("1", "2", "3").listMethod();

        List<String> result1 = mockObject.getMock().listMethod();
        List<String> result2 = mockObject.getMock().listMethod();
        assertEquals(asList("1", "2", "3"), result1);
        assertEquals(asList("1", "2", "3"), result2);
    }

    @Test
    public void listWithSubType() {
        SubClass value = new SubClass();
        mockObject.returnsAll(value).myClassListMethod();

        List<MyClass> result = mockObject.getMock().myClassListMethod();
        assertEquals(asList(value), result);
    }

    @Test
    public void wildCardList() {
        Properties value = new Properties();
        mockObject.returnsAll(value, "string").wildCardMethod();

        List<?> result = mockObject.getMock().wildCardMethod();
        assertEquals(asList(value, "string"), result);
    }

    @Test
    public void rawList() {
        Properties value = new Properties();
        mockObject.returnsAll(value, "string").rawListMethod();

        List<?> result = mockObject.getMock().rawListMethod();
        assertEquals(asList(value, "string"), result);
    }

    @Test
    public void wildCardExtendsList() {
        SubClass value = new SubClass();
        mockObject.returnsAll(value).wildCardExtendsMethod();

        List<?> result = mockObject.getMock().wildCardExtendsMethod();
        assertEquals(asList(value), result);
    }

    @Test
    public void exceptionWhenWildCardExtendsListAndNotAssignable() {
        Properties value = new Properties();
        mockObject.returnsAll(value).wildCardExtendsMethod();
        try {
            mockObject.getMock().wildCardExtendsMethod();
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to return a list, set or array value. The given value does not have a valid type for the list, set or array. Expected type: ? extends org.unitils.mock.MockReturnsAllIntegrationTest$MyClass, actual type: class java.util.Properties", e.getMessage());
        }
    }

    @Test
    public void emptyListWhenNoValues() {
        mockObject.returnsAll().listMethod();

        List<String> result = mockObject.getMock().listMethod();
        assertTrue(result.isEmpty());
    }

    @Test
    public void exceptionWhenNoAssignableToList() {
        mockObject.returnsAll(5).listMethod();
        try {
            mockObject.getMock().listMethod();
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to return a list, set or array value. The given value does not have a valid type for the list, set or array. Expected type: class java.lang.String, actual type: class java.lang.Integer", e.getMessage());
        }
    }

    @Test
    public void set() {
        mockObject.returnsAll("1", "2", "2", "3").setMethod();

        Set<String> result1 = mockObject.getMock().setMethod();
        Set<String> result2 = mockObject.getMock().setMethod();
        assertEquals(asSet("1", "2", "3"), result1);
        assertEquals(asSet("1", "2", "3"), result2);
    }

    @Test
    public void setWithSubType() {
        SubClass value = new SubClass();
        mockObject.returnsAll(value).myClassSetMethod();

        Set<MyClass> result = mockObject.getMock().myClassSetMethod();
        assertEquals(asSet(value), result);
    }

    @Test
    public void emptySetWhenNoValues() {
        mockObject.returnsAll().setMethod();

        Set<String> result = mockObject.getMock().setMethod();
        assertTrue(result.isEmpty());
    }

    @Test
    public void exceptionWhenNoAssignableToSet() {
        mockObject.returnsAll(5).setMethod();
        try {
            mockObject.getMock().setMethod();
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to return a list, set or array value. The given value does not have a valid type for the list, set or array. Expected type: class java.lang.String, actual type: class java.lang.Integer", e.getMessage());
        }
    }

    @Test
    public void array() {
        mockObject.returnsAll("1", "2", "3").arrayMethod();

        String[] result1 = mockObject.getMock().arrayMethod();
        String[] result2 = mockObject.getMock().arrayMethod();
        assertArrayEquals(new String[]{"1", "2", "3"}, result1);
        assertArrayEquals(new String[]{"1", "2", "3"}, result2);
    }

    @Test
    public void arrayWithSubType() {
        SubClass value = new SubClass();
        mockObject.returnsAll(value).myClassArrayMethod();

        MyClass[] result = mockObject.getMock().myClassArrayMethod();
        assertArrayEquals(new MyClass[]{value}, result);
    }

    @Test
    public void emptyArrayWhenNoValues() {
        mockObject.returnsAll().arrayMethod();

        String[] result = mockObject.getMock().arrayMethod();
        assertEquals(0, result.length);
    }

    @Test
    public void exceptionWhenNoAssignableToArray() {
        mockObject.returnsAll(5).arrayMethod();
        try {
            mockObject.getMock().arrayMethod();
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to return a list, set or array value. The given value does not have a valid type for the list, set or array. Expected type: class java.lang.String, actual type: class java.lang.Integer", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenVoidMethod() {
        mockObject.returnsAll("1").voidMethod();
        try {
            mockObject.getMock().voidMethod();
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Trying to define mock behavior that returns a value for a void method.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenNotACollectionValue() {
        mockObject.returnsAll("1").stringMethod();
        try {
            mockObject.getMock().stringMethod();
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to return a list, set or array value. The method does not have a list, set or array return type.", e.getMessage());
        }
    }


    private static interface TestInterface {

        List<String> listMethod();

        List<?> wildCardMethod();

        List<? extends MyClass> wildCardExtendsMethod();

        List rawListMethod();

        Set<String> setMethod();

        String[] arrayMethod();

        List<MyClass> myClassListMethod();

        Set<MyClass> myClassSetMethod();

        MyClass[] myClassArrayMethod();

        void voidMethod();

        String stringMethod();
    }


    private static class MyClass {
    }

    private static class SubClass extends MyClass {
    }
}