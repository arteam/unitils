package org.unitils.util;

import junit.framework.TestCase;
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

    private ReflectionAssert reflectionAssert = new ReflectionAssert(ReflectionComparatorModes.LENIENT_ORDER);

    /**
     * Creates the test instance and initializes the fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();
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


    public static class TestSubClass extends TestSuperClass {

        @SuppressWarnings({"UNUSED_SYMBOL"})
        @TestAnnotation
        private String subField;

        @SuppressWarnings({"UNUSED_SYMBOL"})
        @TestAnnotation
        private void subMethod() {
        }

    }

    @TestClassAnnotation
    @TestInheritedClassAnntation
    public static class TestSuperClass {

        @SuppressWarnings({"UNUSED_SYMBOL"})
        @TestAnnotation
        private String superField;

        @SuppressWarnings({"UNUSED_SYMBOL"})
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
