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
import static org.unitils.reflectionassert.ReflectionComparatorFactory.createRefectionComparator;
import static org.unitils.reflectionassert.ReflectionComparatorMode.LENIENT_ORDER;
import org.unitils.reflectionassert.difference.Difference;
import static org.unitils.reflectionassert.formatter.util.InnerDifferenceFinder.getInnerDifference;


/**
 * Test class for {@link ReflectionComparator}.
 * Contains tests with primitive array types.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ReflectionComparatorPrimitivesArrayTest extends TestCase {

    /* Test array */
    private int[] arrayA;

    /* Same as A but different instance */
    private int[] arrayB;

    /* Same as A but different order of values*/
    private int[] arrayDifferentOrder;

    /* Same as A and B but different int value for element 2 */
    private int[] arrayDifferentValue;

    /* Same as A and B but no 3rd element */
    private int[] arrayDifferentSize;

    /* Same as A and B but of type long */
    private long[] arrayDifferentType;

    /* Test element with inner array for element 2 */
    private Element arrayInnerA;

    /* Same as innerA but different instance */
    private Element arrayInnerB;

    /* Same as innerA and innerB but different int value for inner element 2 */
    private Element arrayInnerDifferentValue;

    /* Same as innerA and innerB but no 3rd inner element */
    private Element arrayInnerDifferentSize;

    /* Class under test */
    private ReflectionComparator reflectionComparator;

    /* Class under test lenient order version */
    private ReflectionComparator reflectionComparatorLenientOrder;


    /**
     * Initializes the test fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();

        arrayA = new int[]{1, 2, 3};
        arrayB = new int[]{1, 2, 3};
        arrayDifferentOrder = new int[]{3, 1, 2};
        arrayDifferentValue = new int[]{1, 9999, 3};
        arrayDifferentSize = new int[]{1, 2};
        arrayDifferentType = new long[]{1, 2, 3};

        arrayInnerA = new Element(arrayA);
        arrayInnerB = new Element(arrayB);
        arrayInnerDifferentValue = new Element(arrayDifferentValue);
        arrayInnerDifferentSize = new Element(arrayDifferentSize);

        reflectionComparator = createRefectionComparator();
        reflectionComparatorLenientOrder = createRefectionComparator(LENIENT_ORDER);
    }


    /**
     * Test for two equal arrays.
     */
    public void testGetDifference_equals() {
        Difference result = reflectionComparator.getDifference(arrayA, arrayB);
        assertNull(result);
    }


    /**
     * Test for two equal arrays as an inner field of an object.
     */
    public void testGetDifference_equalsInner() {
        Difference result = reflectionComparator.getDifference(arrayInnerA, arrayInnerB);
        assertNull(result);
    }


    /**
     * Test for two equal arrays with different order and no lenient order.
     */
    public void testGetDifference_notEqualsDifferentOrder() {
        Difference result = reflectionComparator.getDifference(arrayA, arrayDifferentOrder);

        Difference elementDifference = getInnerDifference("0", result);
        assertEquals(1, elementDifference.getLeftValue());
        assertEquals(3, elementDifference.getRightValue());
    }

    /**
     * Test for two equal arrays with different order but with lenient order.
     */
    public void testGetDifference_equalsLenientOrder() {
        Difference result = reflectionComparatorLenientOrder.getDifference(arrayA, arrayDifferentOrder);
        assertNull(result);
    }


    /**
     * Test for two equal primitives arrays but of different type (int vs long).
     */
    public void testGetDifference_differentTypes() {
        Difference result = reflectionComparator.getDifference(arrayA, arrayDifferentType);
        assertNull(result);
    }


    /**
     * Test for two arrays that contain different values.
     */
    public void testGetDifference_notEqualsDifferentValues() {
        Difference result = reflectionComparator.getDifference(arrayA, arrayDifferentValue);

        Difference difference = getInnerDifference("1", result);
        assertEquals(2, difference.getLeftValue());
        assertEquals(9999, difference.getRightValue());
    }


    /**
     * Test for two arrays that have a different size.
     */
    public void testGetDifference_notEqualsDifferentSize() {
        Difference result = reflectionComparator.getDifference(arrayA, arrayDifferentSize);

        assertSame(arrayA, result.getLeftValue());
        assertSame(arrayDifferentSize, result.getRightValue());
    }


    /**
     * Test for objects with inner arrays that contain different values.
     */
    public void testGetDifference_notEqualsInnerDifferentValues() {
        Difference result = reflectionComparator.getDifference(arrayInnerA, arrayInnerDifferentValue);

        Difference difference = getInnerDifference("1", getInnerDifference("inner", result));
        assertEquals(2, difference.getLeftValue());
        assertEquals(9999, difference.getRightValue());
    }


    /**
     * Tests for objects with inner arrays that have a different size.
     */
    public void testGetDifference_notEqualsInnerDifferentSize() {
        Difference result = reflectionComparator.getDifference(arrayInnerA, arrayInnerDifferentSize);

        Difference difference = getInnerDifference("inner", result);
        assertSame(arrayA, difference.getLeftValue());
        assertSame(arrayDifferentSize, difference.getRightValue());
    }


    /**
     * Test class with failing equals.
     */
    private class Element {

        /* An inner array */
        private int[] inner;

        /**
         * Creates and initializes the element.
         *
         * @param inner the inner array
         */
        public Element(int[] inner) {
            this.inner = inner;
        }

        /**
         * Gets the inner array
         *
         * @return the array
         */
        public int[] getInner() {
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