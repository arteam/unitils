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
 */
public class ReflectionEqualsBuilderTest extends TestCase {

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
    }


    /**
     * Test for two equal objects.
     */
    public void testCheckEquals_equals() {

        ReflectionEquals reflectionEquals = ReflectionEquals.checkEquals(objectsA, objectsB);

        assertTrue(reflectionEquals.isEquals());
        assertNull(reflectionEquals.getDifferenceFieldStack());
        assertNull(reflectionEquals.getDifferenceLeftValue());
        assertNull(reflectionEquals.getDifferenceRightValue());
    }


    /**
     * * Test for two equal objects as an inner field of an object.
     */
    public void testCheckEquals_equalsInner() {

        ReflectionEquals reflectionEquals = ReflectionEquals.checkEquals(objectsInnerA, objectsInnerB);

        assertTrue(reflectionEquals.isEquals());
        assertNull(reflectionEquals.getDifferenceFieldStack());
        assertNull(reflectionEquals.getDifferenceLeftValue());
        assertNull(reflectionEquals.getDifferenceRightValue());
    }


    /**
     * Test case for 2 equal objects that contain a circular reference.
     * This may not cause an infinite loop.
     */
    public void testCheckEquals_equalsCircularDependency() {

        ReflectionEquals reflectionEquals = ReflectionEquals.checkEquals(objectsCircularDependencyA, objectsCircularDependencyB);

        assertTrue(reflectionEquals.isEquals());
        assertNull(reflectionEquals.getDifferenceFieldStack());
        assertNull(reflectionEquals.getDifferenceLeftValue());
        assertNull(reflectionEquals.getDifferenceRightValue());
    }


    /**
     * Test for two objects that contain different values.
     */
    public void testCheckEquals_notEqualsDifferentValues() {

        ReflectionEquals reflectionEquals = ReflectionEquals.checkEquals(objectsA, objectsDifferentValue);

        assertFalse(reflectionEquals.isEquals());
        assertEquals("string2", reflectionEquals.getDifferenceFieldStack().get(0));
        assertEquals("test 2", reflectionEquals.getDifferenceLeftValue());
        assertEquals("XXXXXX", reflectionEquals.getDifferenceRightValue());
    }


    /**
     * Test case for 2 objects with a right value null.
     */
    public void testCheckEquals_notEqualsRightNull() {

        ReflectionEquals reflectionEquals = ReflectionEquals.checkEquals(objectsA, objectsNullValue);

        assertFalse(reflectionEquals.isEquals());
        assertEquals("string2", reflectionEquals.getDifferenceFieldStack().get(0));
        assertEquals("test 2", reflectionEquals.getDifferenceLeftValue());
        assertEquals(null, reflectionEquals.getDifferenceRightValue());
    }


    /**
     * Test case for 2 objects with a left value null.
     */
    public void testCheckEquals_notEqualsLeftNull() {

        ReflectionEquals reflectionEquals = ReflectionEquals.checkEquals(objectsNullValue, objectsA);

        assertFalse(reflectionEquals.isEquals());
        assertEquals("string2", reflectionEquals.getDifferenceFieldStack().get(0));
        assertEquals(null, reflectionEquals.getDifferenceLeftValue());
        assertEquals("test 2", reflectionEquals.getDifferenceRightValue());
    }


    /**
     * Test for objects with inner objects that contain different values.
     */
    public void testCheckEquals_notEqualsInnerDifferentValues() {

        ReflectionEquals reflectionEquals = ReflectionEquals.checkEquals(objectsInnerA, objectsInnerDifferentValue);

        assertFalse(reflectionEquals.isEquals());
        assertEquals("inner", reflectionEquals.getDifferenceFieldStack().get(0));
        assertEquals("string2", reflectionEquals.getDifferenceFieldStack().get(1));
        assertEquals("test 2", reflectionEquals.getDifferenceLeftValue());
        assertEquals("XXXXXX", reflectionEquals.getDifferenceRightValue());
    }


    /**
     * Test case for a null left-argument.
     */
    public void testCheckEquals_leftNull() {

        ReflectionEquals reflectionEquals = ReflectionEquals.checkEquals(null, objectsA);

        assertFalse(reflectionEquals.isEquals());
        assertTrue(reflectionEquals.getDifferenceFieldStack().isEmpty());
        assertEquals(null, reflectionEquals.getDifferenceLeftValue());
        assertSame(objectsA, reflectionEquals.getDifferenceRightValue());
    }


    /**
     * Test case for a null right-argument.
     */
    public void testCheckEquals_rightNull() {

        ReflectionEquals reflectionEquals = ReflectionEquals.checkEquals(objectsA, null);

        assertFalse(reflectionEquals.isEquals());
        assertTrue(reflectionEquals.getDifferenceFieldStack().isEmpty());
        assertSame(objectsA, reflectionEquals.getDifferenceLeftValue());
        assertEquals(null, reflectionEquals.getDifferenceRightValue());
    }


    /**
     * Test case for both null arguments.
     */
    public void testCheckEquals_null() {

        ReflectionEquals reflectionEquals = ReflectionEquals.checkEquals(null, null);

        assertTrue(reflectionEquals.isEquals());
        assertNull(reflectionEquals.getDifferenceFieldStack());
        assertNull(reflectionEquals.getDifferenceLeftValue());
        assertNull(reflectionEquals.getDifferenceRightValue());
    }


    /**
     * Test case for getting the field stack as a string.
     */
    public void testGetDifferenceFieldStackAsString() {

        ReflectionEquals reflectionEquals = ReflectionEquals.checkEquals(objectsInnerA, objectsInnerDifferentValue);
        String differenceFieldString = reflectionEquals.getDifferenceFieldStackAsString();

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