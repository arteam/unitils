/*
 * Copyright 2006 the original author or authors.
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
package org.unitils.reflectionassert;

import junit.framework.TestCase;
import org.unitils.reflectionassert.ReflectionComparator.Difference;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Test class for {@link ReflectionComparator}.
 * Contains tests with map types.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ReflectionComparatorMapTest extends TestCase {

    /* Test map */
    private Map<String, Element> mapA;

    /* Same as A but different instance */
    private Map<String, Element> mapB;

    /* Same as A and B but different string value for element 2 */
    private Map<String, Element> mapDifferentValue;

    /* Same as A and B but different key value for element 2 */
    private Map<String, Element> mapDifferentKey;

    /* Test collection with inner map for element 2 */
    private Map<String, Element> mapInnerA;

    /* Same as innerA but different instance  */
    private Map<String, Element> mapInnerB;

    /* Same as innerA and innerB but different string value for inner element 2 */
    private Map<String, Element> mapInnerDifferentValue;

    /* Class under test */
    private ReflectionComparator reflectionComparator;


    /**
     * Initializes the test fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();

        mapA = createMap("key 2", "test 2", null);
        mapB = createMap("key 2", "test 2", null);
        mapDifferentValue = createMap("key 2", "XXXXXX", null);
        mapDifferentKey = createMap("XXXXX", "test 2", null);

        mapInnerA = createMap("key 2", null, mapA);
        mapInnerB = createMap("key 2", null, mapB);
        mapInnerDifferentValue = createMap("key 2", null, mapDifferentValue);

        reflectionComparator = ReflectionComparatorChainFactory.STRICT_COMPARATOR;
    }


    /**
     * Test for two equal maps.
     */
    public void testGetDifference_equals() {
        Difference result = reflectionComparator.getDifference(mapA, mapB);
        assertNull(result);
    }


    /**
     * Test for two equal maps as an inner field of an object.
     */
    public void testGetDifference_equalsInner() {
        Difference result = reflectionComparator.getDifference(mapInnerA, mapInnerB);
        assertNull(result);
    }


    /**
     * Test for two maps that contain different values.
     */
    public void testGetDifference_notEqualsDifferentValues() {
        Difference result = reflectionComparator.getDifference(mapA, mapDifferentValue);

        assertNotNull(result);
        assertEquals("key 2", result.getFieldStack().get(0));
        assertEquals("test 2", result.getLeftValue());
        assertEquals("XXXXXX", result.getRightValue());
    }


    /**
     * Test for two maps that have a different size.
     */
    public void testGetDifference_notEqualsDifferentSize() {
        Iterator iterator = mapB.entrySet().iterator();
        iterator.next();
        iterator.remove();

        Difference result = reflectionComparator.getDifference(mapA, mapB);

        assertNotNull(result);
        assertTrue(result.getFieldStack().isEmpty());
        assertSame(mapA, result.getLeftValue());
        assertSame(mapB, result.getRightValue());
    }


    /**
     * Test for objects with inner maps that contain different values.
     */
    public void testGetDifference_notEqualsInnerDifferentValues() {
        Difference result = reflectionComparator.getDifference(mapInnerA, mapInnerDifferentValue);

        assertNotNull(result);
        assertEquals("key 2", result.getFieldStack().get(0));
        assertEquals("inner", result.getFieldStack().get(1));
        assertEquals("test 2", result.getLeftValue());
        assertEquals("XXXXXX", result.getRightValue());
    }


    /**
     * Test for objects with inner maps that contain different keys.
     */
    public void testGetDifference_notEqualsDifferentKeys() {
        Difference result = reflectionComparator.getDifference(mapA, mapDifferentKey);

        assertNotNull(result);
        assertEquals("key 2", result.getFieldStack().get(0));
        assertSame(mapA.get("key 2"), result.getLeftValue());
        assertEquals(null, result.getRightValue());
    }


    /**
     * Tests for objects with inner maps that have a different size.
     */
    public void testGetDifference_notEqualsInnerDifferentSize() {
        Iterator iterator = mapB.entrySet().iterator();
        iterator.next();
        iterator.remove();

        Difference result = reflectionComparator.getDifference(mapInnerA, mapInnerB);

        assertNotNull(result);
        assertEquals("key 2", result.getFieldStack().get(0));
        assertSame(mapA, result.getLeftValue());
        assertSame(mapB, result.getRightValue());
    }


    /**
     * Tests for maps but right value is not a map.
     */
    public void testGetDifference_notEqualsRightNotMap() {
        Difference result = reflectionComparator.getDifference(mapA, "Test string");

        assertNotNull(result);
        assertTrue(result.getFieldStack().empty());
        assertSame(mapA, result.getLeftValue());
        assertEquals("Test string", result.getRightValue());
    }


    /**
     * Creates a map.
     *
     * @param keyElement2         the key for the 2nd element in the collection
     * @param stringValueElement2 the value for the 2nd element in the collection
     * @param innerElement2       the value for the inner array of the 2nd element in the collection
     * @return the test collection
     */
    private Map<String, Element> createMap(String keyElement2, String stringValueElement2, Map innerElement2) {
        Map<String, Element> map = new HashMap<String, Element>();
        map.put("key 1", new Element("test 1", null));
        map.put(keyElement2, new Element(stringValueElement2, innerElement2));
        map.put("key 3", new Element("test 3", null));
        return map;
    }


    /**
     * Test class with failing equals.
     */
    private class Element {

        /* A string value */
        private String string;

        /* An inner map */
        private Map inner;


        /**
         * Creates and initializes the element.
         *
         * @param string the string value
         * @param inner  the inner map
         */
        public Element(String string, Map inner) {
            this.string = string;
            this.inner = inner;
        }

        /**
         * Gets the string value
         *
         * @return the value
         */
        public String getString() {
            return string;
        }

        /**
         * Gets the inner map
         *
         * @return the map
         */
        public Map getInner() {
            return inner;
        }

        /**
         * Always returns false
         *
         * @param o the object to compare to
         */
        public boolean equals(Object o) {
            return false;
        }
    }


}