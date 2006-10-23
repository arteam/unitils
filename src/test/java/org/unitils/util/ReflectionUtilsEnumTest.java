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
     * Test for get enum value.
     */
    public void testGetEnumValue() {

        TestEnum result = ReflectionUtils.getEnumValue(TestEnum.class, "VALUE1");
        assertSame(TestEnum.VALUE1, result);
    }


    /**
     * Test for get enum value with different case.
     */
    public void testGetEnumValue_differentCase() {

        TestEnum result = ReflectionUtils.getEnumValue(TestEnum.class, "Value1");
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

}
