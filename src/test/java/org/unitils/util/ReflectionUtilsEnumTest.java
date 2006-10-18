/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.util;

import junit.framework.TestCase;
import org.unitils.core.UnitilsException;

/**
 * Test for {@link ReflectionUtils} that use enumeration values.
 */
public class ReflectionUtilsEnumTest extends TestCase {


    /**
     * Test for get value with normal (no default) value.
     */
    public void testGetValueReplaceDefault() {

        TestEnum value = ReflectionUtils.getValueReplaceDefault(TestEnum.VALUE1, TestEnum.VALUE2);
        assertEquals(TestEnum.VALUE1, value);
    }


    /**
     * Test for get value for a default value (DEFAULT).
     */
    public void testGetValueReplaceDefault_default() {

        TestEnum value = ReflectionUtils.getValueReplaceDefault(TestEnum.DEFAULT, TestEnum.VALUE2);
        assertEquals(TestEnum.VALUE2, value);
    }


    /**
     * Test for get value with a default value (VALUE1).
     */
    public void testGetValueReplaceDefault_value1AsDefault() {

        TestEnum value = ReflectionUtils.getValueReplaceDefault(TestEnum.VALUE1, "VALUE1", TestEnum.VALUE2);
        assertEquals(TestEnum.VALUE2, value);
    }


    /**
     * Test for get value for a default value that is not all upper case (DEFAULT).
     */
    public void testGetValueReplaceDefault_defaultButOtherCase() {

        TestEnumOtherCase value = ReflectionUtils.getValueReplaceDefault(TestEnumOtherCase.Default, TestEnumOtherCase.Value2);
        assertEquals(TestEnumOtherCase.Value2, value);
    }


    /**
     * Test for get enum value.
     */
    public void testGetEnumValue() {

        TestEnum result = ReflectionUtils.getEnumValue(TestEnum.class, "VALUE1");
        assertSame(TestEnum.VALUE1, result);
    }


    /**
     * Test for get enum value, but an unknown value name.
     * Should fail with a runtime exception.
     */
    public void testGetEnumValue_unexisting() {

        try {
            ReflectionUtils.getEnumValue(TestEnum.class, "xxxxxxxxx");
            fail("Expected UnitilsException");

        } catch (UnitilsException e) {
            //expected
        }
    }


    /**
     * Test enumeration with a default value.
     */
    private enum TestEnum {

        DEFAULT, VALUE1, VALUE2

    }


    /**
     * Test enumeration with a default value and not all upper case values.
     */
    private enum TestEnumOtherCase {

        Default, Value1, Value2

    }
}
