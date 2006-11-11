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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;


/**
 * Test class for {@link ReflectionComparator}.
 * Contains tests with collection types.
 */
public class ReflectionComparatorCollectionTest extends TestCase {

    /* Test collection */
    private Collection collectionA;

    /* Same as A but different instance */
    private Collection collectionB;

    /* Same as A and B but different string value for element 2 */
    private Collection collectionDifferentValue;

    /* Same as A but in a LinkedList instead of an ArrayList */
    private Collection collectionDifferentType;

    /* Test collection with inner collection for element 2 */
    private Collection collectionInnerA;

    /* Same as innerA but different instance  */
    private Collection collectionInnerB;

    /* Same as innerA and innerB but different string value for inner element 2 */
    private Collection collectionInnerDifferentValue;

    /* Class under test */
    private ReflectionComparator reflectionComparator;

    /**
     * Initializes the test fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();

        collectionA = createCollection("test 2", null);
        collectionB = createCollection("test 2", null);
        collectionDifferentValue = createCollection("XXXXXX", null);
        collectionDifferentType = new LinkedList(collectionA);

        collectionInnerA = createCollection(null, collectionA);
        collectionInnerB = createCollection(null, collectionB);
        collectionInnerDifferentValue = createCollection(null, collectionDifferentValue);

        reflectionComparator = new ReflectionComparator();
    }


    /**
     * Test for two equal collections.
     */
    public void testCheckEquals_equals() {

        Difference result = reflectionComparator.getDifference(collectionA, collectionB);

        assertNull(result);
    }

    /**
     * Test for two equal collections that are of a different type.
     */
    public void testCheckEquals_equalsDifferentType() {

        Difference result = reflectionComparator.getDifference(collectionA, collectionDifferentType);

        assertNull(result);
    }

    /**
     * Test for two equal collections as an inner field of an object.
     */
    public void testCheckEquals_equalsInner() {

        Difference result = reflectionComparator.getDifference(collectionInnerA, collectionInnerB);

        assertNull(result);
    }


    /**
     * Test for two collections that contain different values.
     */
    public void testCheckEquals_notEqualsDifferentValues() {

        Difference result = reflectionComparator.getDifference(collectionA, collectionDifferentValue);

        assertNotNull(result);
        assertEquals("1", result.getFieldStack().get(0));
        assertEquals("test 2", result.getLeftValue());
        assertEquals("XXXXXX", result.getRightValue());
    }


    /**
     * Test for two collections that have a different size.
     */
    public void testCheckEquals_notEqualsDifferentSize() {
        Iterator iterator = collectionB.iterator();
        iterator.next();
        iterator.remove();

        Difference result = reflectionComparator.getDifference(collectionA, collectionB);

        assertNotNull(result);
        assertTrue(result.getFieldStack().isEmpty());
        assertSame(collectionA, result.getLeftValue());
        assertSame(collectionB, result.getRightValue());
    }


    /**
     * Test for objects with inner collections that contain different values.
     */
    public void testCheckEquals_notEqualsInnerDifferentValues() {

        Difference result = reflectionComparator.getDifference(collectionInnerA, collectionInnerDifferentValue);

        assertNotNull(result);
        assertEquals("1", result.getFieldStack().get(0));
        assertEquals("inner", result.getFieldStack().get(1));
        assertEquals("test 2", result.getLeftValue());
        assertEquals("XXXXXX", result.getRightValue());
    }


    /**
     * Tests for objects with inner collections that have a different size.
     */
    public void testCheckEquals_notEqualsInnerDifferentSize() {
        Iterator iterator = collectionB.iterator();
        iterator.next();
        iterator.remove();

        Difference result = reflectionComparator.getDifference(collectionInnerA, collectionInnerB);

        assertNotNull(result);
        assertEquals("1", result.getFieldStack().get(0));
        assertSame(collectionA, result.getLeftValue());
        assertSame(collectionB, result.getRightValue());
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