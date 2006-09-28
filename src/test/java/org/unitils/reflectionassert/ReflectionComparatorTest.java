/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.reflectionassert;

import org.unitils.reflectionassert.ReflectionComparator.Difference;
import junit.framework.TestCase;


/**
 * Test class for {@link ReflectionComparator}.
 */
public class ReflectionComparatorTest extends TestCase {

    /* Test object */
    private Objects objectsA;

    /* Same as A but different instance */
    private Objects objectsB;

    /* Same as A and B but different string value for stringValue2 */
    private Objects objectsDifferentValue;

    /* Test object containing a null value */
    private Objects objectsNullValue;

    /* Test object with inner object */
    private Objects objectsInnerA;

    /* Same as innerA but different instance */
    private Objects objectsInnerB;

    /* Same as innerA and innerB but different int value for inner intValue2 */
    private Objects objectsInnerDifferentValue;

    /* Test object containing a circular dependency to itself */
    private Objects objectsCircularDependencyA;

    /* Same as circularDependencyA but different instance */
    private Objects objectsCircularDependencyB;

    /* Class under test */
    private ReflectionComparator reflectionComparator;

    /**
     * Initializes the test fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();

        objectsA = new Objects("test 1", "test 2", null);
        objectsB = new Objects("test 1", "test 2", null);
        objectsDifferentValue = new Objects("test 1", "XXXXXX", null);
        objectsNullValue = new Objects("test 1", null, null);

        objectsInnerA = new Objects(null, null, objectsA);
        objectsInnerB = new Objects(null, null, objectsB);
        objectsInnerDifferentValue = new Objects(null, null, objectsDifferentValue);

        objectsCircularDependencyA = new Objects(null, null, new Objects(null, null, new Objects(null, null, null)));
        objectsCircularDependencyB = new Objects(null, null, new Objects(null, null, new Objects(null, null, null)));

        //create a circular dependency
        objectsCircularDependencyA.getInner().getInner().setInner(objectsCircularDependencyA);
        objectsCircularDependencyB.getInner().getInner().setInner(objectsCircularDependencyB);

        reflectionComparator = new ReflectionComparator();
    }


    /**
     * Test for two equal objects.
     */
    public void testCheckEquals_equals() {

        Difference result = reflectionComparator.getDifference(objectsA, objectsB);

        assertNull(result);
    }


    /**
     * * Test for two equal objects as an inner field of an object.
     */
    public void testCheckEquals_equalsInner() {

        Difference result = reflectionComparator.getDifference(objectsInnerA, objectsInnerB);

        assertNull(result);
    }


    /**
     * Test case for 2 equal objects that contain a circular reference.
     * This may not cause an infinite loop.
     */
    public void testCheckEquals_equalsCircularDependency() {

        Difference result = reflectionComparator.getDifference(objectsCircularDependencyA, objectsCircularDependencyB);

        assertNull(result);
    }


    /**
     * Test for two objects that contain different values.
     */
    public void testCheckEquals_notEqualsDifferentValues() {

        Difference result = reflectionComparator.getDifference(objectsA, objectsDifferentValue);

        assertNotNull(result);
        assertEquals("string2", result.getFieldStack().get(0));
        assertEquals("test 2", result.getLeftValue());
        assertEquals("XXXXXX", result.getRightValue());
    }


    /**
     * Test case for 2 objects with a right value null.
     */
    public void testCheckEquals_notEqualsRightNull() {

        Difference result = reflectionComparator.getDifference(objectsA, objectsNullValue);

        assertNotNull(result);
        assertEquals("string2", result.getFieldStack().get(0));
        assertEquals("test 2", result.getLeftValue());
        assertEquals(null, result.getRightValue());
    }


    /**
     * Test case for 2 objects with a left value null.
     */
    public void testCheckEquals_notEqualsLeftNull() {

        Difference result = reflectionComparator.getDifference(objectsNullValue, objectsA);

        assertNotNull(result);
        assertEquals("string2", result.getFieldStack().get(0));
        assertEquals(null, result.getLeftValue());
        assertEquals("test 2", result.getRightValue());
    }


    /**
     * Test for objects with inner objects that contain different values.
     */
    public void testCheckEquals_notEqualsInnerDifferentValues() {

        Difference result = reflectionComparator.getDifference(objectsInnerA, objectsInnerDifferentValue);

        assertNotNull(result);
        assertEquals("inner", result.getFieldStack().get(0));
        assertEquals("string2", result.getFieldStack().get(1));
        assertEquals("test 2", result.getLeftValue());
        assertEquals("XXXXXX", result.getRightValue());
    }


    /**
     * Test case for a null left-argument.
     */
    public void testCheckEquals_leftNull() {

        Difference result = reflectionComparator.getDifference(null, objectsA);

        assertNotNull(result);
        assertTrue(result.getFieldStack().isEmpty());
        assertEquals(null, result.getLeftValue());
        assertSame(objectsA, result.getRightValue());
    }


    /**
     * Test case for a null right-argument.
     */
    public void testCheckEquals_rightNull() {

        Difference result = reflectionComparator.getDifference(objectsA, null);

        assertNotNull(result);
        assertTrue(result.getFieldStack().isEmpty());
        assertSame(objectsA, result.getLeftValue());
        assertEquals(null, result.getRightValue());
    }


    /**
     * Test case for both null arguments.
     */
    public void testCheckEquals_null() {

        Difference result = reflectionComparator.getDifference(null, null);

        assertNull(result);
    }


    /**
     * Test case for getting the field stack as a string.
     */
    public void testGetDifferenceFieldStackAsString() {

        Difference result = reflectionComparator.getDifference(objectsInnerA, objectsInnerDifferentValue);
        String differenceFieldString = result.getFieldStackAsString();

        assertEquals("inner.string2", differenceFieldString);
    }


    /**
     * Test class with failing equals.
     */
    private class Objects {

        /* A fist object value */
        private String string1;

        /* A second object value */
        private String string2;

        /* An inner object */
        private Objects inner;


        /**
         * Creates and initializes the objects instance.
         *
         * @param stringValue1 the first object value
         * @param stringValue2 the second object value
         * @param inner        the inner collection
         */
        public Objects(String stringValue1, String stringValue2, Objects inner) {
            this.string1 = stringValue1;
            this.string2 = stringValue2;
            this.inner = inner;
        }

        /**
         * Gets the first object value
         *
         * @return the value
         */
        public String getString1() {
            return string1;
        }

        /**
         * Gets the second object value
         *
         * @return the value
         */
        public String getString2() {
            return string2;
        }

        /**
         * Gets the inner object
         *
         * @return the object
         */
        public Objects getInner() {
            return inner;
        }

        /**
         * Sets the inner object
         *
         * @param inner the object
         */
        public void setInner(Objects inner) {
            this.inner = inner;
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