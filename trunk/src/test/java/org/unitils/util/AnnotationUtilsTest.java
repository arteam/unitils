package org.unitils.util;

import junit.framework.TestCase;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * Test for {@link AnnotationUtils}.
 */
public class AnnotationUtilsTest extends TestCase {

    /* The unitils configuration settings that control the enumeration default settings */
    private Configuration configuration;


    /**
     * Creates the test instance and initializes the fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();

        configuration = new PropertiesConfiguration();
        configuration.setProperty(TestEnum.class.getName(), "VALUE2");
        configuration.setProperty(TestEnumOtherCase.class.getName(), "Value2");

        UnitilsConfiguration.setInstance(configuration);
    }


    /**
     * Test for get value with normal (no default) value.
     */
    public void testGetValueReplaceDefault() {

        TestEnum value = AnnotationUtils.getValueReplaceDefault(TestEnum.VALUE1);

        assertEquals(TestEnum.VALUE1, value);
    }


    /**
     * Test for get value for a default value (DEFAULT).
     */
    public void testGetValueReplaceDefault_default() {

        TestEnum value = AnnotationUtils.getValueReplaceDefault(TestEnum.DEFAULT);

        assertEquals(TestEnum.VALUE2, value);
    }

    /**
     * Test for get value for a default value that is not all upper case (DEFAULT).
     */
    public void testGetValueReplaceDefault_defaultButOtherCase() {

        TestEnumOtherCase value = AnnotationUtils.getValueReplaceDefault(TestEnumOtherCase.Default);

        assertEquals(TestEnumOtherCase.Value2, value);
    }

    /**
     * Test for get value for a default value, but an unknown default value in the configuration.
     * Should fail with a runtime exception.
     */
    public void testGetValueReplaceDefault_defaultButWrongConfiguration() {

        try {
            configuration.setProperty(TestEnum.class.getName(), "xxxxxxxxx");

            AnnotationUtils.getValueReplaceDefault(TestEnum.DEFAULT);
            fail();

        } catch (RuntimeException e) {
            //expected
        }
    }


    /**
     * Test for get value for a non-default value, but missing configuration for a default value.
     * Should be no problem.
     */
    public void testGetValueReplaceDefault_noDefaultAndNoConfiguration() {

        configuration.setProperty(TestEnum.class.getName(), null);

        TestEnum value = AnnotationUtils.getValueReplaceDefault(TestEnum.VALUE1);

        assertEquals(TestEnum.VALUE1, value);
    }

    /**
     * Test for get value for a default value, but missing configuration for a default value.
     * Should fail with a runtime exception.
     */
    public void testGetValueReplaceDefault_defaultButNoConfiguration() {

        try {
            configuration.setProperty(TestEnum.class.getName(), null);

            AnnotationUtils.getValueReplaceDefault(TestEnum.DEFAULT);
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
