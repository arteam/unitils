/*
 * Copyright 2006-2007,  Unitils.org
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
import org.unitils.reflectionassert.difference.Difference;
import org.unitils.reflectionassert.ReflectionComparator;
import static org.unitils.reflectionassert.ReflectionComparatorFactory.createRefectionComparator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;


/**
 * Test class for {@link ReflectionComparator}.
 * Contains tests with collection types.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ReflectionComparatorCollectionTest extends TestCase {

    /* Test collection */
    private Collection<Element> collectionA;

    /* Same as A but different instance */
    private Collection<Element> collectionB;

    /* Same as A and B but different string value for element 2 */
    private Collection<Element> collectionDifferentValue;

    /* Same as A but in a LinkedList instead of an ArrayList */
    private Collection<Element> collectionDifferentType;

    /* Test collection with inner collection for element 2 */
    private Collection<Element> collectionInnerA;

    /* Same as innerA but different instance  */
    private Collection<Element> collectionInnerB;

    /* Same as innerA and innerB but different string value for inner element 2 */
    private Collection<Element> collectionInnerDifferentValue;

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
        collectionDifferentType = new LinkedList<Element>(collectionA);

        collectionInnerA = createCollection(null, collectionA);
        collectionInnerB = createCollection(null, collectionB);
        collectionInnerDifferentValue = createCollection(null, collectionDifferentValue);

        reflectionComparator = createRefectionComparator();
    }


    /**
     * Test for two equal collections.
     */
    public void testGetDifference_equals() {
        Difference result = reflectionComparator.getDifference(collectionA, collectionB);
        assertNull(result);
    }


    /**
     * Test for two equal collections that are of a different type.
     */
    public void testGetDifference_equalsDifferentType() {
        Difference result = reflectionComparator.getDifference(collectionA, collectionDifferentType);
        assertNull(result);
    }


    /**
     * Test for two equal collections as an inner field of an object.
     */
    public void testGetDifference_equalsInner() {
        Difference result = reflectionComparator.getDifference(collectionInnerA, collectionInnerB);
        assertNull(result);
    }


    /**
     * Test for two collections that contain different values.
     */
    public void testGetDifference_notEqualsDifferentValues() {
        Difference result = reflectionComparator.getDifference(collectionA, collectionDifferentValue);

        Difference difference = result.getInnerDifference("1").getInnerDifference("string");
        assertEquals("test 2", difference.getLeftValue());
        assertEquals("XXXXXX", difference.getRightValue());
    }


    /**
     * Test for two collections that have a different size. The first element was removed from the right list.
     */
    public void testGetDifference_notEqualsFirstRightElementRemoved() {
        Iterator<?> iterator = collectionB.iterator();
        iterator.next();
        iterator.remove();

        Difference result = reflectionComparator.getDifference(collectionA, collectionB);

        Difference difference1 = result.getInnerDifference("0").getInnerDifference("string");
        assertEquals("test 1", difference1.getLeftValue());
        assertEquals("test 2", difference1.getRightValue());

        Difference difference2 = result.getInnerDifference("1").getInnerDifference("string");
        assertEquals("test 2", difference2.getLeftValue());
        assertEquals("test 3", difference2.getRightValue());

        Difference difference3 = result.getInnerDifference("2");
        assertEquals("test 3", ((Element) difference3.getLeftValue()).getString());
        assertEquals(null, difference3.getRightValue());
    }


    /**
     * Test for two collections that have a different size. The first element was removed from the left list.
     */
    public void testGetDifference_notEqualsFirstLeftElementRemoved() {
        Iterator<?> iterator = collectionA.iterator();
        iterator.next();
        iterator.remove();

        Difference result = reflectionComparator.getDifference(collectionA, collectionB);

        Difference difference1 = result.getInnerDifference("0").getInnerDifference("string");
        assertEquals("test 2", difference1.getLeftValue());
        assertEquals("test 1", difference1.getRightValue());

        Difference difference2 = result.getInnerDifference("1").getInnerDifference("string");
        assertEquals("test 3", difference2.getLeftValue());
        assertEquals("test 2", difference2.getRightValue());

        Difference difference3 = result.getInnerDifference("2");
        assertEquals(null, difference3.getLeftValue());
        assertEquals("test 3", ((Element) difference3.getRightValue()).getString());
    }


    /**
     * Test for objects with inner collections that contain different values.
     */
    public void testGetDifference_notEqualsInnerDifferentValues() {
        Difference result = reflectionComparator.getDifference(collectionInnerA, collectionInnerDifferentValue);

        Difference difference = result.getInnerDifference("1").getInnerDifference("inner");
        Difference innerDifference = difference.getInnerDifference("1").getInnerDifference("string");
        assertEquals("test 2", innerDifference.getLeftValue());
        assertEquals("XXXXXX", innerDifference.getRightValue());
    }


    /**
     * Tests for objects with inner collections that have a different size.
     */
    public void testGetDifference_notEqualsInnerDifferentSize() {
        Iterator<?> iterator = collectionB.iterator();
        iterator.next();
        iterator.remove();

        Difference result = reflectionComparator.getDifference(collectionInnerA, collectionInnerB);

        Difference difference = result.getInnerDifference("1").getInnerDifference("inner");
        assertSame(collectionA, difference.getLeftValue());
        assertSame(collectionB, difference.getRightValue());
    }


    /**
     * Tests for collections but right value is not a collection.
     */
    public void testGetDifference_notEqualsRightNotCollection() {
        Difference result = reflectionComparator.getDifference(collectionA, "Test string");

        assertSame(collectionA, result.getLeftValue());
        assertEquals("Test string", result.getRightValue());
    }


    /**
     * Test for two equal collections.
     */
    public void testGetAllDifferences_equals() {
        Difference result = reflectionComparator.getAllDifferences(collectionA, collectionB);
        assertNull(result);
    }


    /**
     * Test for two collections that contain different values.
     */
    //todo implement
    public void testGetAllDifferences_notEqualsDifferentValues() {
        Difference result = reflectionComparator.getAllDifferences(collectionA, collectionDifferentValue);

        //assertEquals(1, result.size());
        //Difference differnce = result.get(0);
        //assertEquals("1", differnce.getFieldStack().get(0));
        //assertEquals("test 2", differnce.getLeftValue());
        //assertEquals("XXXXXX", differnce.getRightValue());
    }


    /**
     * Test for two collections that contain different values.
     */
    //todo implement
    public void testGetAllDifferences_notEqualsMultipleDifferentValues() {
        collectionDifferentValue.iterator().next().string = "YYYYYY";
        Difference result = reflectionComparator.getAllDifferences(collectionA, collectionDifferentValue);

        //assertEquals(2, result.size());
        //Difference differnce1 = result.get(0);
        //assertEquals("0", differnce1.getFieldStack().get(0));
        //assertEquals("test 1", differnce1.getLeftValue());
        //assertEquals("YYYYYY", differnce1.getRightValue());
        //Difference differnce2 = result.get(1);
        //assertEquals("1", differnce2.getFieldStack().get(0));
        //assertEquals("test 2", differnce2.getLeftValue());
        //assertEquals("XXXXXX", differnce2.getRightValue());
    }


    /**
     * Creates a collection.
     *
     * @param stringValueElement2 the value for the 2nd element in the collection
     * @param innerElement2       the value for the inner array of the 2nd element in the collection
     * @return the test collection
     */
    private Collection<Element> createCollection(String stringValueElement2, Collection<Element> innerElement2) {
        Collection<Element> collection = new ArrayList<Element>();
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
        private Collection<?> inner;

        /**
         * Creates and initializes the element.
         *
         * @param string the string value
         * @param inner  the inner collection
         */
        public Element(String string, Collection<?> inner) {
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
        public Collection<?> getInner() {
            return inner;
        }

        /**
         * Always returns false
         *
         * @param o the object to compare to
         */
        @Override
        public boolean equals(Object o) {
            return false;
        }
    }


}