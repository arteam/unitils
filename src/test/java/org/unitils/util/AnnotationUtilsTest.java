package org.unitils.util;

import junit.framework.TestCase;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.unitils.core.Unitils;
import org.unitils.inject.InjectionUtils;
import org.unitils.reflectionassert.ReflectionAssert;
import org.unitils.reflectionassert.ReflectionComparatorModes;

import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Test for {@link AnnotationUtils}.
 */
public class AnnotationUtilsTest extends TestCase {

    /* The unitils configuration settings that control the enumeration default settings */
    private Configuration configuration;

    private ReflectionAssert reflectionAssert = new ReflectionAssert(ReflectionComparatorModes.LENIENT_ORDER);

    /**
     * Creates the test instance and initializes the fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();

        configuration = new PropertiesConfiguration();
        configuration.setProperty(TestEnum.class.getName(), "VALUE2");
        configuration.setProperty(TestEnumOtherCase.class.getName(), "Value2");

        InjectionUtils.injectStatic(configuration, Unitils.class, "unitils.configuration");
    }

    public void testGetFieldsAnnotatedWith() {
        List<Field> annotatedFields = AnnotationUtils.getFieldsAnnotatedWith(TestSuperClass.class, TestAnnotation.class);
        reflectionAssert.assertPropertyEquals("name", Arrays.asList("superField"), annotatedFields);
    }

    public void testGetFieldsAnnotatedWith_fieldFromSuperClass() {
        List<Field> annotatedFields = AnnotationUtils.getFieldsAnnotatedWith(TestSubClass.class, TestAnnotation.class);
        reflectionAssert.assertPropertyEquals("name", Arrays.asList("subField", "superField"), annotatedFields);
    }

    public void testGetMethodsAnnotatedWith() {
        List<Method> annotatedMethods = AnnotationUtils.getMethodsAnnotatedWith(TestSuperClass.class, TestAnnotation.class);
        reflectionAssert.assertPropertyEquals("name", Arrays.asList("superMethod"), annotatedMethods);
    }

    public void testGetMethodsAnnotatedWith_methodFromSuperClass() {
        List<Method> annotatedMethods = AnnotationUtils.getMethodsAnnotatedWith(TestSubClass.class, TestAnnotation.class);
        reflectionAssert.assertPropertyEquals("name", Arrays.asList("subMethod", "superMethod"), annotatedMethods);
    }

    public void testGetClassAnnotation() {
        TestClassAnnotation testClassAnnotation = AnnotationUtils.getClassAnnotation(TestSuperClass.class, TestClassAnnotation.class);
        assertNotNull(testClassAnnotation);
    }

    public void testGetClassAnnotation_inherited() {
        TestClassAnnotation testClassAnnotation = AnnotationUtils.getClassAnnotation(TestSubClass.class, TestClassAnnotation.class);
        assertNull(testClassAnnotation);
        TestInheritedClassAnntation testInheritedClassAnntation = AnnotationUtils.getClassAnnotation(TestSubClass.class, TestInheritedClassAnntation.class);
        assertNotNull(testInheritedClassAnntation);
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

    public static class TestSubClass extends TestSuperClass {

        @TestAnnotation
        private String subField;

        @TestAnnotation
        private void subMethod() {
        }

    }

    @TestClassAnnotation
    @TestInheritedClassAnntation
    public static class TestSuperClass {

        @TestAnnotation
        private String superField;

        @TestAnnotation
        private void superMethod() {
        }
    }

    @Target({ElementType.FIELD, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface TestAnnotation {
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface TestClassAnnotation {
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    public @interface TestInheritedClassAnntation {
    }

}
