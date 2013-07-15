/*
 * Copyright 2013,  Unitils.org
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

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Set;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.util.Arrays.asList;
import static org.unitils.reflectionassert.ReflectionAssert.assertPropertyLenientEquals;

/**
 * Test for {@link AnnotationUtils}.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class AnnotationUtilsTest extends TestCase {

    /**
     * Test to get all annotated methods.
     */
    public void testGetMethodsAnnotatedWith() {
        Set<Method> annotatedMethods = AnnotationUtils.getMethodsAnnotatedWith(TestClass.class, TestAnnotation.class);
        assertPropertyLenientEquals("name", asList("annotatedMethod"), annotatedMethods);
    }


    /**
     * Test to get all annotated methods, but no methods are annotated. An empty list should be returned.
     */
    public void testGetMethodsAnnotatedWith_annotationNotFound() {
        Set<Method> annotatedMethods = AnnotationUtils.getMethodsAnnotatedWith(TestClassNoAnnotation.class, TestAnnotation.class);
        assertTrue(annotatedMethods.isEmpty());
    }


    /**
     * Test to get all annotated methods, including the methods of the annotated super class. Both methods should be returned.
     */
    public void testGetMethodsAnnotatedWith_methodFromSuperClass() {
        Set<Method> annotatedMethods = AnnotationUtils.getMethodsAnnotatedWith(TestSubClass.class, TestAnnotation.class);
        assertPropertyLenientEquals("name", asList("annotatedMethod", "annotatedSubMethod"), annotatedMethods);
    }


    /**
     * Test to get all annotated methods, excluding the methods of the annotated super class.
     */
    public void testGetMethodsAnnotatedWith_noMethodsFromSuperClass() {
        Set<Method> annotatedMethods = AnnotationUtils.getMethodsAnnotatedWith(TestSubClass.class, TestAnnotation.class, false);
        assertPropertyLenientEquals("name", asList("annotatedSubMethod"), annotatedMethods);
    }


    /**
     * Test annotation.
     */
    @Target({TYPE, FIELD, METHOD})
    @Retention(RUNTIME)
    private @interface TestAnnotation {

        String level() default "";
    }


    /**
     * Test class that class containing annotated fields and methods.
     */
    @SuppressWarnings("all")
    @TestAnnotation(level = "class")
    private static class TestClass {

        @TestAnnotation
        private String field;

        @TestAnnotation(level = "method")
        private void annotatedMethod() {
        }

        private void unAnnotatedMethod() {
        }
    }

    /**
     * Test class that class containing no annotated fields and methods.
     */
    @SuppressWarnings("all")
    private static class TestClassNoAnnotation {

        private String field;

        private void method() {
        }
    }


    /**
     * Test class that extends the test super class. For testing inherited annotations.
     */
    @SuppressWarnings("all")
    private static class TestSubClass extends TestClass {

        @TestAnnotation
        private String subField;

        @TestAnnotation
        private void annotatedSubMethod() {
        }

        private void unAnnotatedSubMethod() {
        }

    }

    /**
     * Test class that extends the test super class, with class level annotation. For testing inherited annotations.
     */
    @TestAnnotation(level = "subClass")
    private static class TestAnnotatedSubClass extends TestClass {

    }

}
