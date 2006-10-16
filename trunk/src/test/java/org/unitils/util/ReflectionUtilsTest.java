package org.unitils.util;

import junit.framework.TestCase;

/**
 * Test for {@link ReflectionUtils}.
 */
public class ReflectionUtilsTest extends TestCase {


    /**
     * Creates the test instance and initializes the fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();
    }


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
            fail();

        } catch (RuntimeException e) {
            //expected
        }
    }


    /**
     * Test for get enum value, but an unknown value name.
     * Should fail with a runtime exception.
     */
    public void testGetEnumValue_null() {

        try {
            ReflectionUtils.getEnumValue(TestEnum.class, null);
            fail();

        } catch (RuntimeException e) {
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
