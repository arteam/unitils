/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.util;

import junit.framework.TestCase;
import org.unitils.reflectionassert.ReflectionAssert;
import org.unitils.reflectionassert.ReflectionComparatorModes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Test for {@link AnnotationUtils}.
 */
public class AnnotationUtilsTest extends TestCase {

    //todo assertPropertyRefEquals

    /* Assertion through reflection */
    private ReflectionAssert reflectionAssert = new ReflectionAssert(ReflectionComparatorModes.LENIENT_ORDER);


    /**
     * Test to get all annotated fields.
     */
    public void testGetFieldsAnnotatedWith() {
        List<Field> annotatedFields = AnnotationUtils.getFieldsAnnotatedWith(TestClass.class, TestAnnotation.class);
        reflectionAssert.assertPropertyEquals("name", Arrays.asList("field"), annotatedFields);
    }


    /**
     * Test to get all annotated fields, but no fields are annotated. An empty list should be returned.
     */
    public void testGetFieldsAnnotatedWith_annotationNotFound() {
        List<Field> annotatedFields = AnnotationUtils.getFieldsAnnotatedWith(TestClassNoAnnotation.class, TestAnnotation.class);
        assertTrue(annotatedFields.isEmpty());
    }

    /**
     * Test to get all annotated fields, including the fields of the annotated super class. Both fields should be returned.
     */
    public void testGetFieldsAnnotatedWith_fieldFromSuperClass() {
        List<Field> annotatedFields = AnnotationUtils.getFieldsAnnotatedWith(TestSubClass.class, TestAnnotation.class);
        reflectionAssert.assertPropertyEquals("name", Arrays.asList("field", "subField"), annotatedFields);
    }


    /**
     * Test to get all annotated methods.
     */
    public void testGetMethodsAnnotatedWith() {
        List<Method> annotatedMethods = AnnotationUtils.getMethodsAnnotatedWith(TestClass.class, TestAnnotation.class);
        reflectionAssert.assertPropertyEquals("name", Arrays.asList("method"), annotatedMethods);
    }

    /**
     * Test to get all annotated methods, but no methods are annotated. An empty list should be returned.
     */
    public void testGetMethodsAnnotatedWith_annotationNotFound() {
        List<Method> annotatedMethods = AnnotationUtils.getMethodsAnnotatedWith(TestClassNoAnnotation.class, TestAnnotation.class);
        assertTrue(annotatedMethods.isEmpty());
    }


    /**
     * Test to get all annotated methods, including the methods of the annotated super class. Both methods should be returned.
     */
    public void testGetMethodsAnnotatedWith_methodFromSuperClass() {
        List<Method> annotatedMethods = AnnotationUtils.getMethodsAnnotatedWith(TestSubClass.class, TestAnnotation.class);
        reflectionAssert.assertPropertyEquals("name", Arrays.asList("method", "subMethod"), annotatedMethods);
    }


    /**
     * Test annotation.
     */
    @Target({ElementType.FIELD, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    private @interface TestAnnotation {
    }


    /**
     * Test class that class containing annotated fields and methods.
     */
    @SuppressWarnings({"UnusedDeclaration"})
    private static class TestClass {

        @TestAnnotation
        private String field;

        @TestAnnotation
        private void method() {
        }
    }

    /**
     * Test class that class containing no annotated fields and methods.
     */
    @SuppressWarnings({"UnusedDeclaration"})
    private static class TestClassNoAnnotation {

        private String field;

        private void method() {
        }
    }


    /**
     * Test class that extends the test super class. For testing inherited annotations.
     */
    @SuppressWarnings({"UnusedDeclaration"})
    private static class TestSubClass extends TestClass {

        @TestAnnotation
        private String subField;

        @TestAnnotation
        private void subMethod() {
        }

    }


}
