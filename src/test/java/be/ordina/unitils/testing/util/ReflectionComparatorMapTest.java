/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils.testing.util;

import be.ordina.unitils.testing.util.ReflectionComparator.Difference;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Test class for {@link ReflectionComparator}.
 * Contains tests with map types.
 */
public class ReflectionComparatorMapTest extends TestCase {

    /* Test map */
    private Map mapA;

    /* Same as A but different instance */
    private Map mapB;

    /* Same as A and B but different string value for element 2 */
    private Map mapDifferentValue;

    /* Same as A and B but different key value for element 2 */
    private Map mapDifferentKey;

    /* Test collection with inner map for element 2 */
    private Map mapInnerA;

    /* Same as innerA but different instance  */
    private Map mapInnerB;

    /* Same as innerA and innerB but different string value for inner element 2 */
    private Map mapInnerDifferentValue;

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

        reflectionComparator = new ReflectionComparator();
    }


    /**
     * Test for two equal maps.
     */
    public void testCheckEquals_equals() {

        Difference result = reflectionComparator.getDifference(mapA, mapB);

        assertNull(result);
    }


    /**
     * Test for two equal maps as an inner field of an object.
     */
    public void testCheckEquals_equalsInner() {

        Difference result = reflectionComparator.getDifference(mapInnerA, mapInnerB);

        assertNull(result);
    }


    /**
     * Test for two maps that contain different values.
     */
    public void testCheckEquals_notEqualsDifferentValues() {

        Difference result = reflectionComparator.getDifference(mapA, mapDifferentValue);

        assertNotNull(result);
        assertEquals("key 2", result.getFieldStack().get(0));
        assertEquals("test 2", result.getLeftValue());
        assertEquals("XXXXXX", result.getRightValue());
    }


    /**
     * Test for two maps that have a different size.
     */
    public void testCheckEquals_notEqualsDifferentSize() {
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
    public void testCheckEquals_notEqualsInnerDifferentValues() {

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
    public void testCheckEquals_notEqualsDifferentKeys() {

        Difference result = reflectionComparator.getDifference(mapA, mapDifferentKey);

        assertNotNull(result);
        assertEquals("key 2", result.getFieldStack().get(0));
        assertSame(mapA.get("key 2"), result.getLeftValue());
        assertEquals(null, result.getRightValue());
    }


    /**
     * Tests for objects with inner maps that have a different size.
     */
    public void testCheckEquals_notEqualsInnerDifferentSize() {
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
     * Creates a map.
     *
     * @param keyElement2         the key for the 2nd element in the collection
     * @param stringValueElement2 the value for the 2nd element in the collection
     * @param innerElement2       the value for the inner array of the 2nd element in the collection
     * @return the test collection
     */
    private Map createMap(String keyElement2, String stringValueElement2, Map innerElement2) {
        Map map = new HashMap();
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