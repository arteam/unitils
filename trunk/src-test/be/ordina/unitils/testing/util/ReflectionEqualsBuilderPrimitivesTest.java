/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils.testing.util;

import junit.framework.TestCase;


/**
 * Test class for {@link ReflectionEquals}.
 * Contains tests with primitive types.
 */
public class ReflectionEqualsBuilderPrimitivesTest extends TestCase {

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
    }


    /**
     * Test for two equal primitives.
     */
    public void testCheckEquals_equals() {

        ReflectionEquals reflectionEquals = ReflectionEquals.checkEquals(primitivesA, primitivesB);

        assertTrue(reflectionEquals.isEquals());
        assertNull(reflectionEquals.getDifferenceFieldStack());
        assertNull(reflectionEquals.getDifferenceLeftValue());
        assertNull(reflectionEquals.getDifferenceRightValue());
    }


    /**
     * Test for two equal primitives as an inner field of an object.
     */
    public void testCheckEquals_equalsInner() {

        ReflectionEquals reflectionEquals = ReflectionEquals.checkEquals(primitivesInnerA, primitivesInnerB);

        assertTrue(reflectionEquals.isEquals());
        assertNull(reflectionEquals.getDifferenceFieldStack());
        assertNull(reflectionEquals.getDifferenceLeftValue());
        assertNull(reflectionEquals.getDifferenceRightValue());
    }


    /**
     * Test for two primitives that contain different values.
     */
    public void testCheckEquals_notEqualsDifferentValues() {

        ReflectionEquals reflectionEquals = ReflectionEquals.checkEquals(primitivesA, primitiveDifferentValue);

        assertFalse(reflectionEquals.isEquals());
        assertEquals("intValue2", reflectionEquals.getDifferenceFieldStack().get(0));
        assertEquals(new Integer(2), reflectionEquals.getDifferenceLeftValue());
        assertEquals(new Integer(9999), reflectionEquals.getDifferenceRightValue());
    }


    /**
     * Test for two primitives with right value 0.
     */
    public void testCheckEquals_notEqualsRight0() {

        ReflectionEquals reflectionEquals = ReflectionEquals.checkEquals(primitivesA, primitives0Value);

        assertFalse(reflectionEquals.isEquals());
        assertEquals("intValue2", reflectionEquals.getDifferenceFieldStack().get(0));
        assertEquals(new Integer(2), reflectionEquals.getDifferenceLeftValue());
        assertEquals(new Integer(0), reflectionEquals.getDifferenceRightValue());
    }


    /**
     * Test for two primitives with left value 0.
     */
    public void testCheckEquals_notEqualsLeft0() {

        ReflectionEquals reflectionEquals = ReflectionEquals.checkEquals(primitives0Value, primitivesA);

        assertFalse(reflectionEquals.isEquals());
        assertEquals("intValue2", reflectionEquals.getDifferenceFieldStack().get(0));
        assertEquals(new Integer(0), reflectionEquals.getDifferenceLeftValue());
        assertEquals(new Integer(2), reflectionEquals.getDifferenceRightValue());
    }


    /**
     * Test for objects with inner primitives that contain different values.
     */
    public void testCheckEquals_notEqualsInnerDifferentValues() {

        ReflectionEquals reflectionEquals = ReflectionEquals.checkEquals(primitivesInnerA, primitivesInnerDifferentValue);

        assertFalse(reflectionEquals.isEquals());
        assertEquals("inner", reflectionEquals.getDifferenceFieldStack().get(0));
        assertEquals("intValue2", reflectionEquals.getDifferenceFieldStack().get(1));
        assertEquals(new Integer(2), reflectionEquals.getDifferenceLeftValue());
        assertEquals(new Integer(9999), reflectionEquals.getDifferenceRightValue());
    }


    /**
     * Test class with failing equals.
     */
    private class Primitives {

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
         * Gets the first int value
         *
         * @return the value
         */
        public int getIntValue1() {
            return intValue1;
        }

        /**
         * Gets the second int value
         *
         * @return the value
         */
        public int getIntValue2() {
            return intValue2;
        }

        /**
         * Gets the inner object
         *
         * @return the object
         */
        public Primitives getInner() {
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

