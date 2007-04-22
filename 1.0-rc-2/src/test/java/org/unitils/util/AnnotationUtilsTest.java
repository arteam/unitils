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
package org.unitils.util;

import junit.framework.TestCase;
import static org.unitils.reflectionassert.ReflectionAssert.assertPropertyLenEquals;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import static java.util.Arrays.asList;
import java.util.List;

/**
 * Test for {@link AnnotationUtils}.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class AnnotationUtilsTest extends TestCase {


    /**
     * Test to get all annotated fields.
     */
    public void testGetFieldsAnnotatedWith() {
        List<Field> annotatedFields = AnnotationUtils.getFieldsAnnotatedWith(TestClass.class, TestAnnotation.class);
        assertPropertyLenEquals("name", asList("field"), annotatedFields);
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
        assertPropertyLenEquals("name", asList("field", "subField"), annotatedFields);
    }


    /**
     * Test to get all annotated methods.
     */
    public void testGetMethodsAnnotatedWith() {
        List<Method> annotatedMethods = AnnotationUtils.getMethodsAnnotatedWith(TestClass.class, TestAnnotation.class);
        assertPropertyLenEquals("name", asList("method"), annotatedMethods);
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
        assertPropertyLenEquals("name", asList("method", "subMethod"), annotatedMethods);
    }


    /**
     * Test to get all annotated methods, excluding the methods of the annotated super class.
     */
    public void testGetMethodsAnnotatedWith_noMethodsFromSuperClass() {
        List<Method> annotatedMethods = AnnotationUtils.getMethodsAnnotatedWith(TestSubClass.class, TestAnnotation.class, false);
        assertPropertyLenEquals("name", asList("subMethod"), annotatedMethods);
    }


    /**
     * Test annotation.
     */
    @Target({FIELD, METHOD})
    @Retention(RUNTIME)
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
