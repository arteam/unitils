/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils.testing.util;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


/**
 * Test class for {@link ReflectionEquals}.
 * Contains tests with collection types.
 */
public class ReflectionEqualsBuilderCollectionTest extends TestCase {

    /* Test collection */
    private Collection collectionA;

    /* Same as A but different instance */
    private Collection collectionB;

    /* Same as A and B but different string value for element 2 */
    private Collection collectionDifferentValue;

    /* Test collection with inner collection for element 2 */
    private Collection collectionInnerA;

    /* Same as innerA but different instance  */
    private Collection collectionInnerB;

    /* Same as innerA and innerB but different string value for inner element 2 */
    private Collection collectionInnerDifferentValue;


    /**
     * Initializes the test fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();

        collectionA = createCollection("test 2", null);
        collectionB = createCollection("test 2", null);
        collectionDifferentValue = createCollection("XXXXXX", null);

        collectionInnerA = createCollection(null, collectionA);
        collectionInnerB = createCollection(null, collectionB);
        collectionInnerDifferentValue = createCollection(null, collectionDifferentValue);
    }


    /**
     * Test for two equal collections.
     */
    public void testCheckEquals_equals() {

        ReflectionEquals reflectionEquals = ReflectionEquals.checkEquals(collectionA, collectionB);

        assertTrue(reflectionEquals.isEquals());
        assertNull(reflectionEquals.getDifferenceFieldStack());
        assertNull(reflectionEquals.getDifferenceLeftValue());
        assertNull(reflectionEquals.getDifferenceRightValue());
    }


    /**
     * Test for two equal collections as an inner field of an object.
     */
    public void testCheckEquals_equalsInner() {

        ReflectionEquals reflectionEquals = ReflectionEquals.checkEquals(collectionInnerA, collectionInnerB);

        assertTrue(reflectionEquals.isEquals());
        assertNull(reflectionEquals.getDifferenceFieldStack());
        assertNull(reflectionEquals.getDifferenceLeftValue());
        assertNull(reflectionEquals.getDifferenceRightValue());
    }


    /**
     * Test for two collections that contain different values.
     */
    public void testCheckEquals_notEqualsDifferentValues() {

        ReflectionEquals reflectionEquals = ReflectionEquals.checkEquals(collectionA, collectionDifferentValue);

        assertFalse(reflectionEquals.isEquals());
        assertEquals("1", reflectionEquals.getDifferenceFieldStack().get(0));
        assertEquals("test 2", reflectionEquals.getDifferenceLeftValue());
        assertEquals("XXXXXX", reflectionEquals.getDifferenceRightValue());
    }


    /**
     * Test for two collections that have a different size.
     */
    public void testCheckEquals_notEqualsDifferentSize() {
        Iterator iterator = collectionB.iterator();
        iterator.next();
        iterator.remove();

        ReflectionEquals reflectionEquals = ReflectionEquals.checkEquals(collectionA, collectionB);

        assertFalse(reflectionEquals.isEquals());
        assertTrue(reflectionEquals.getDifferenceFieldStack().isEmpty());
        assertSame(collectionA, reflectionEquals.getDifferenceLeftValue());
        assertSame(collectionB, reflectionEquals.getDifferenceRightValue());
    }


    /**
     * Test for objects with inner collections that contain different values.
     */
    public void testCheckEquals_notEqualsInnerDifferentValues() {

        ReflectionEquals reflectionEquals = ReflectionEquals.checkEquals(collectionInnerA, collectionInnerDifferentValue);

        assertFalse(reflectionEquals.isEquals());
        assertEquals("1", reflectionEquals.getDifferenceFieldStack().get(0));
        assertEquals("inner", reflectionEquals.getDifferenceFieldStack().get(1));
        assertEquals("test 2", reflectionEquals.getDifferenceLeftValue());
        assertEquals("XXXXXX", reflectionEquals.getDifferenceRightValue());
    }


    /**
     * Tests for objects with inner collections that have a different size.
     */
    public void testCheckEquals_notEqualsInnerDifferentSize() {
        Iterator iterator = collectionB.iterator();
        iterator.next();
        iterator.remove();

        ReflectionEquals reflectionEquals = ReflectionEquals.checkEquals(collectionInnerA, collectionInnerB);

        assertFalse(reflectionEquals.isEquals());
        assertEquals("1", reflectionEquals.getDifferenceFieldStack().get(0));
        assertSame(collectionA, reflectionEquals.getDifferenceLeftValue());
        assertSame(collectionB, reflectionEquals.getDifferenceRightValue());
    }


    /**
     * Creates a collection.
     *
     * @param stringValueElement2 the value for the 2nd element in the collection
     * @param innerElement2       the value for the inner array of the 2nd element in the collection
     * @return the test collection
     */
    private Collection createCollection(String stringValueElement2, Collection innerElement2) {
        Collection collection = new ArrayList();
        collection.add(new Element("test 1", null));
        collection.add(new Element(stringValueElement2, innerElement2));
        collection.add(new Element("test 3", null));
        return collection;
    }


    /**
     * Test class with failing equals.
     */
    private class Element {

        /* A string value */
        private String string;

        /* An inner collection */
        private Collection inner;

        /**
         * Creates and initializes the element.
         *
         * @param string the string value
         * @param inner  the inner collection
         */
        public Element(String string, Collection inner) {
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
         * Gets the inner collection
         *
         * @return the collection
         */
        public Collection getInner() {
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