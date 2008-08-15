/*
 * Copyright 2008,  Unitils.org
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
import org.unitils.reflectionassert.difference.Difference;
import static org.unitils.reflectionassert.util.InnerDifferenceFinder.getInnerDifference;


/**
 * Test class for {@link ReflectionComparator}. Contains tests with primitive types.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ReflectionComparatorPrimitivesTest extends TestCase {

    /* Test object */
    private Primitives primitivesA;

    /* Same as A but different instance */
    private Primitives primitivesB;

    /* Same as A and B but different int value for intValue2 */
    private Primitives primitiveDifferentValue;

    /* Same as A and B but with 0 value for intValue2 */
    private Primitives primitives0Value;

    /* Test object with inner object */
    private Primitives primitivesInnerA;

    /* Same as innerA but different instance */
    private Primitives primitivesInnerB;

    /* Same as innerA and innerB but different int value for inner intValue2 */
    private Primitives primitivesInnerDifferentValue;

    /* Class under test */
    private ReflectionComparator reflectionComparator;


    /**
     * Initializes the test fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();

        primitivesA = new Primitives(1, 2, null);
        primitivesB = new Primitives(1, 2, null);
        primitiveDifferentValue = new Primitives(1, 9999, null);
        primitives0Value = new Primitives(1, 0, null);

        primitivesInnerA = new Primitives(0, 0, primitivesA);
        primitivesInnerB = new Primitives(0, 0, primitivesB);
        primitivesInnerDifferentValue = new Primitives(0, 0, primitiveDifferentValue);

        reflectionComparator = createRefectionComparator();
    }


    /**
     * Test for two equal primitives.
     */
    public void testGetDifference_equals() {
        Difference result = reflectionComparator.getDifference(primitivesA, primitivesB);
        assertNull(result);
    }


    /**
     * Test for two equal autoboxing. An autoboxed primitive should be considered equals to the object version.
     */
    @SuppressWarnings({"UnnecessaryBoxing"})
    public void testGetDifference_equalsAutoboxing() {
        Difference result = reflectionComparator.getDifference(5L, new Long(5));
        assertNull(result);
    }


    /**
     * Test for two equal primitives as an inner field of an object.
     */
    public void testGetDifference_equalsInner() {
        Difference result = reflectionComparator.getDifference(primitivesInnerA, primitivesInnerB);
        assertNull(result);
    }


    /**
     * Test for two equal primitives but of different type (int vs long).
     */
    public void testGetDifference_differentTypes() {
        Difference result = reflectionComparator.getDifference(5L, 5);
        assertNull(result);
    }


    /**
     * Test for two primitives that contain different values.
     */
    public void testGetDifference_notEqualsDifferentValues() {
        Difference result = reflectionComparator.getDifference(primitivesA, primitiveDifferentValue);

        Difference difference = getInnerDifference("intValue2", result);
        assertEquals(2, difference.getLeftValue());
        assertEquals(9999, difference.getRightValue());
    }


    /**
     * Test for two primitives with right value 0.
     */
    public void testGetDifference_notEqualsRight0() {
        Difference result = reflectionComparator.getDifference(primitivesA, primitives0Value);

        Difference difference = getInnerDifference("intValue2", result);
        assertEquals(2, difference.getLeftValue());
        assertEquals(0, difference.getRightValue());
    }


    /**
     * Test for two primitives with left value 0.
     */
    public void testGetDifference_notEqualsLeft0() {
        Difference result = reflectionComparator.getDifference(primitives0Value, primitivesA);

        Difference difference = getInnerDifference("intValue2", result);
        assertEquals(0, difference.getLeftValue());
        assertEquals(2, difference.getRightValue());
    }


    /**
     * Test for objects with inner primitives that contain different values.
     */
    public void testGetDifference_notEqualsInnerDifferentValues() {
        Difference result = reflectionComparator.getDifference(primitivesInnerA, primitivesInnerDifferentValue);

        Difference difference2 = getInnerDifference("intValue2", getInnerDifference("inner", result));
        assertEquals(2, difference2.getLeftValue());
        assertEquals(9999, difference2.getRightValue());
    }


    /**
     * Tests for equality of two NaN values
     */
    public void testNaN() {
        Difference result = reflectionComparator.getDifference(Double.NaN, Float.NaN);
        assertNull(result);
    }


    /**
     * Tests for equality of a NaN with a 0 value
     */
    public void testNaN_notEqual() {
        Difference result = reflectionComparator.getDifference(Double.NaN, 0);
        assertEquals(Double.NaN, result.getLeftValue());
        assertEquals(0, result.getRightValue());
    }


    /**
     * Tests for equality of two NEGATIVE_INFINITY values
     */
    public void testInfinity() {
        Difference result = reflectionComparator.getDifference(Double.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
        assertNull(result);
    }


    /**
     * Tests for equality of a NEGATIVE_INFINITY with a POSITIVE_INFINITY value
     */
    public void testInfinity_notEqual() {
        Difference result = reflectionComparator.getDifference(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        assertEquals(Double.NEGATIVE_INFINITY, result.getLeftValue());
        assertEquals(Double.POSITIVE_INFINITY, result.getRightValue());
    }


    /**
     * Test class with failing equals.
     */
    @SuppressWarnings({"unused", "UnusedDeclaration", "FieldCanBeLocal"})
    private static class Primitives {

        /* A fist int value */
        private int intValue1;

        /* A second int value */
        private int intValue2;

        /* An inner object */
        private Primitives inner;


        /**
         * Creates and initializes the element.
         *
         * @param intValue1 the first int value
         * @param intValue2 the second int value
         * @param inner     the inner collection
         */
        public Primitives(int intValue1, int intValue2, Primitives inner) {
            this.intValue1 = intValue1;
            this.intValue2 = intValue2;
            this.inner = inner;
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
