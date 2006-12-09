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
import static org.unitils.reflectionassert.ReflectionComparatorMode.IGNORE_DEFAULTS;
import static org.unitils.reflectionassert.ReflectionComparatorMode.LENIENT_DATES;

import java.util.Date;


/**
 * Test class for {@link ReflectionComparator}.
 * Contains tests for ignore defaults and lenient dates.
 */
public class ReflectionComparatorLenientTest extends TestCase {

    /* Test object with no java defaults */
    private Element elementNoDefaultsA;

    /* Same as A but different instance */
    private Element elementNoDefaultsB;

    /* Test object with only defaults */
    private Element elementAllDefaults;

    /* Same as A but with null date */
    private Element elementNoDefaultsNullDateA;

    /* Same as null date  A but different instance */
    private Element elementNoDefaultsNullDateB;

    /* Same as A but different date */
    private Element elementNoDefaultsDifferentDate;

    /* Class under test */
    private ReflectionComparator reflectionComparator;


    /**
     * Initializes the test fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();

        Date date = new Date();
        elementNoDefaultsA = new Element(true, 'c', (byte) 1, (short) 2, 3, 4l, 5.0f, 6.0, date, "object");
        elementNoDefaultsB = new Element(true, 'c', (byte) 1, (short) 2, 3, 4l, 5.0f, 6.0, date, "object");
        elementNoDefaultsNullDateA = new Element(true, 'c', (byte) 1, (short) 2, 3, 4l, 5.0f, 6.0, null, "object");
        elementNoDefaultsNullDateB = new Element(true, 'c', (byte) 1, (short) 2, 3, 4l, 5.0f, 6.0, null, "object");
        elementNoDefaultsDifferentDate = new Element(true, 'c', (byte) 1, (short) 2, 3, 4l, 5.0f, 6.0, new Date(), "object");
        elementAllDefaults = new Element(false, (char) 0, (byte) 0, (short) 0, 0, 0l, 0.0f, 0.0, null, null);

        reflectionComparator = new ReflectionComparator();
    }


    /**
     * Test for two equal objects without java defaults.
     */
    public void testGetDifference_equals() {

        Difference result = reflectionComparator.getDifference(elementNoDefaultsA, elementNoDefaultsB);

        assertNull(result);
    }


    /**
     * Test with left object containing only java defaults.
     */
    public void testGetDifference_equalsIgnoreDefaults() {

        Difference result = new ReflectionComparator(IGNORE_DEFAULTS).getDifference(elementAllDefaults, elementNoDefaultsA);

        assertNull(result);
    }


    /**
     * Test with ignore defaults and left object null.
     */
    public void testGetDifference_equalsIgnoreDefaultsLeftNull() {

        Difference result = new ReflectionComparator(IGNORE_DEFAULTS).getDifference(null, elementNoDefaultsA);

        assertNull(result);
    }


    /**
     * Test with ignore defaults and right object null
     */
    public void testGetDifference_notEqualsIgnoreDefaultsRightNull() {

        Difference result = new ReflectionComparator(IGNORE_DEFAULTS).getDifference(elementNoDefaultsA, null);

        assertNotNull(result);
        assertTrue(result.getFieldStack().isEmpty());
        assertSame(elementNoDefaultsA, result.getLeftValue());
        assertNull(result.getRightValue());
    }


    /**
     * Test with ignore defaults and left value 0.
     */
    public void testGetDifference_equalsIgnoreDefaultsLeft0() {

        Difference result = new ReflectionComparator(IGNORE_DEFAULTS).getDifference(0, 999);

        assertNull(result);
    }

    /**
     * Test with ignore defaults and right value 0.
     */
    public void testGetDifference_equalsIgnoreDefaultsRight0() {

        Difference result = new ReflectionComparator(IGNORE_DEFAULTS).getDifference(999, 0);

        assertNotNull(result);
        assertTrue(result.getFieldStack().isEmpty());
        assertEquals(999, result.getLeftValue());
        assertEquals(0, result.getRightValue());
    }


    /**
     * Test for lenient dates with 2 null dates.
     */
    public void testGetDifference_equalsLenientDatesBothNull() {

        Difference result = new ReflectionComparator(LENIENT_DATES).getDifference(elementNoDefaultsNullDateA, elementNoDefaultsNullDateB);

        assertNull(result);
    }


    /**
     * Test for lenient dates with 2 not null dates.
     */
    public void testGetDifference_equalsLenientDatesBothNotNull() {

        Difference result = new ReflectionComparator(LENIENT_DATES).getDifference(elementNoDefaultsA, elementNoDefaultsDifferentDate);

        assertNull(result);
    }


    /**
     * Test with left object containing only java defaults but no ignore defaults.
     */
    public void testGetDifference_notEqualsNoIgnoreDefaults() {

        Difference result = reflectionComparator.getDifference(elementAllDefaults, elementNoDefaultsB);

        assertNotNull(result);
        assertEquals("booleanValue", result.getFieldStack().get(0));
        assertEquals(Boolean.FALSE, result.getLeftValue());
        assertEquals(Boolean.TRUE, result.getRightValue());
    }


    /**
     * Test with right instead of left object containing only java defaults.
     */
    public void testGetDifference_notEqualsIgnoreDefaultsButDefaultsRight() {

        Difference result = new ReflectionComparator(IGNORE_DEFAULTS).getDifference(elementNoDefaultsB, elementAllDefaults);

        assertNotNull(result);
        assertEquals("booleanValue", result.getFieldStack().get(0));
        assertEquals(Boolean.TRUE, result.getLeftValue());
        assertEquals(Boolean.FALSE, result.getRightValue());
    }


    /**
     * Test for lenient dates but with only right date null.
     */
    public void testGetDifference_notEqualsLenientDatesRightDateNull() {

        Difference result = new ReflectionComparator(LENIENT_DATES).getDifference(elementNoDefaultsDifferentDate, elementNoDefaultsNullDateA);

        assertNotNull(result);
        assertEquals("dateValue", result.getFieldStack().get(0));
        assertEquals(elementNoDefaultsDifferentDate.getDateValue(), result.getLeftValue());
        assertNull(result.getRightValue());
    }


    /**
     * Test for lenient dates but with only left date null.
     */
    public void testGetDifference_notEqualsLenientDatesLeftDateNull() {

        Difference result = new ReflectionComparator(LENIENT_DATES).getDifference(elementNoDefaultsNullDateA, elementNoDefaultsDifferentDate);

        assertNotNull(result);
        assertEquals("dateValue", result.getFieldStack().get(0));
        assertNull(result.getLeftValue());
        assertEquals(elementNoDefaultsDifferentDate.getDateValue(), result.getRightValue());
    }


    /**
     * Test for lenient dates while ignore defaults but with only left date null (= not treated as default).
     */
    public void testGetDifference_notEqualsLenientDatesAndIgnoreDefaultsWithLeftDateNull() {

        Difference result = new ReflectionComparator(LENIENT_DATES, IGNORE_DEFAULTS).getDifference(elementNoDefaultsNullDateA, elementNoDefaultsDifferentDate);

        assertNotNull(result);
        assertEquals("dateValue", result.getFieldStack().get(0));
        assertNull(result.getLeftValue());
        assertEquals(elementNoDefaultsDifferentDate.getDateValue(), result.getRightValue());
    }


    /**
     * Test class with failing equals.
     */
    private class Element {

        /* A boolean value */
        private boolean booleanValue;

        /* A char value */
        private char charValue;

        /* A byte value */
        private byte byteValue;

        /* A short value */
        private short shortValue;

        /* An int value */
        private int intValue;

        /* A long value */
        private long longValue;

        /* A float value */
        private float floatValue;

        /* A double value */
        private double doubleValue;

        /* A date value */
        private Date dateValue;

        /* An object value */
        private Object objectValue;

        /**
         * Creates and initializes the element.
         *
         * @param booleanValue a boolean value
         * @param charValue    a char value
         * @param byteValue    a byte value
         * @param shortValue   a short value
         * @param intValue     an int value
         * @param longValue    a long value
         * @param floatValue   a float value
         * @param doubleValue  a double value
         * @param dateValue    a date value
         * @param objectValue  an object value
         */
        public Element(boolean booleanValue, char charValue, byte byteValue, short shortValue, int intValue, long longValue, float floatValue, double doubleValue, Date dateValue, Object objectValue) {
            this.booleanValue = booleanValue;
            this.charValue = charValue;
            this.byteValue = byteValue;
            this.shortValue = shortValue;
            this.intValue = intValue;
            this.longValue = longValue;
            this.floatValue = floatValue;
            this.doubleValue = doubleValue;
            this.dateValue = dateValue;
            this.objectValue = objectValue;
        }

        /**
         * Gets the boolean value.
         *
         * @return the boolean value
         */
        public boolean isBooleanValue() {
            return booleanValue;
        }

        /**
         * Gets the char value.
         *
         * @return the char value
         */
        public char getCharValue() {
            return charValue;
        }

        /**
         * Gets the byte value.
         *
         * @return the byte value
         */
        public byte getByteValue() {
            return byteValue;
        }

        /**
         * Gets the short value.
         *
         * @return the short value
         */
        public short getShortValue() {
            return shortValue;
        }

        /**
         * Gets the int value.
         *
         * @return the int value
         */
        public int getIntValue() {
            return intValue;
        }

        /**
         * Gets the long value.
         *
         * @return the long value
         */
        public long getLongValue() {
            return longValue;
        }

        /**
         * Gets the float value.
         *
         * @return the float value
         */
        public float getFloatValue() {
            return floatValue;
        }

        /**
         * Gets the double value.
         *
         * @return the double value
         */
        public double getDoubleValue() {
            return doubleValue;
        }

        /**
         * Gets the date value.
         *
         * @return the date value
         */
        public Date getDateValue() {
            return dateValue;
        }

        /**
         * Gets the object value.
         *
         * @return the object value
         */
        public Object getObjectValue() {
            return objectValue;
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