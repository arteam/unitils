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
import static org.unitils.reflectionassert.ReflectionComparatorMode.LENIENT_ORDER;


/**
 * Test class for {@link ReflectionComparator}.
 * Contains tests with primitive array types.
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

        arrayInnerA = new Element(arrayA);
        arrayInnerB = new Element(arrayB);
        arrayInnerDifferentValue = new Element(arrayDifferentValue);
        arrayInnerDifferentSize = new Element(arrayDifferentSize);

        reflectionComparator = new ReflectionComparator();
        reflectionComparatorLenientOrder = new ReflectionComparator(LENIENT_ORDER);
    }


    /**
     * Test for two equal arrays.
     */
    public void testCheckEquals_equals() {

        Difference result = reflectionComparator.getDifference(arrayA, arrayB);

        assertNull(result);
    }


    /**
     * Test for two equal arrays as an inner field of an object.
     */
    public void testCheckEquals_equalsInner() {

        Difference result = reflectionComparator.getDifference(arrayInnerA, arrayInnerB);

        assertNull(result);
    }


    /**
     * Test for two equal arrays with different order and no lenient order.
     */
    public void testCheckEquals_notEqualsDifferentOrder() {

        Difference result = reflectionComparator.getDifference(arrayA, arrayDifferentOrder);

        assertNotNull(result);
        assertEquals("0", result.getFieldStack().get(0));
        assertEquals(1, result.getLeftValue());
        assertEquals(3, result.getRightValue());
    }

    /**
     * Test for two equal arrays with different order but with lenient order.
     */
    public void testCheckEquals_equalsLenientOrder() {

        Difference result = reflectionComparatorLenientOrder.getDifference(arrayA, arrayDifferentOrder);

        assertNull(result);
    }


    /**
     * Test for two arrays that contain different values.
     */
    public void testCheckEquals_notEqualsDifferentValues() {

        Difference result = reflectionComparator.getDifference(arrayA, arrayDifferentValue);

        assertNotNull(result);
        assertEquals("1", result.getFieldStack().get(0));
        assertEquals(2, result.getLeftValue());
        assertEquals(9999, result.getRightValue());
    }


    /**
     * Test for two arrays that have a different size.
     */
    public void testCheckEquals_notEqualsDifferentSize() {

        Difference result = reflectionComparator.getDifference(arrayA, arrayDifferentSize);

        assertNotNull(result);
        assertTrue(result.getFieldStack().isEmpty());
        assertSame(arrayA, result.getLeftValue());
        assertSame(arrayDifferentSize, result.getRightValue());
    }


    /**
     * Test for objects with inner arrays that contain different values.
     */
    public void testCheckEquals_notEqualsInnerDifferentValues() {

        Difference result = reflectionComparator.getDifference(arrayInnerA, arrayInnerDifferentValue);

        assertNotNull(result);
        assertEquals("inner", result.getFieldStack().get(0));
        assertEquals("1", result.getFieldStack().get(1));
        assertEquals(2, result.getLeftValue());
        assertEquals(9999, result.getRightValue());
    }


    /**
     * Tests for objects with inner arrays that have a different size.
     */
    public void testCheckEquals_notEqualsInnerDifferentSize() {

        Difference result = reflectionComparator.getDifference(arrayInnerA, arrayInnerDifferentSize);

        assertNotNull(result);
        assertEquals("inner", result.getFieldStack().get(0));
        assertSame(arrayA, result.getLeftValue());
        assertSame(arrayDifferentSize, result.getRightValue());
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
        public boolean equals(Object o) {
            return false;
        }
    }
}