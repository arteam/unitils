/*
 * Copyright 2008,  Unitils.org
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

import org.junit.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.unitils.reflectionassert.ReflectionAssert.assertPropertyLenientEquals;
import static org.unitils.util.AnnotationUtils.getMethodOrClassLevelAnnotation;
import static org.unitils.util.AnnotationUtils.getMethodOrClassLevelAnnotationProperty;

/**
 * Test for {@link AnnotationUtils}.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class AnnotationUtilsTest {

    /**
     * Test to get all annotated fields.
     */
    @Test
    public void getFieldsAnnotatedWith() {
    	Set<Field> annotatedFields = AnnotationUtils.getFieldsAnnotatedWith(TestClass.class, TestAnnotation.class);
        assertPropertyLenientEquals("name", asList("field"), annotatedFields);
    }


    /**
     * Test to get all annotated fields, but no fields are annotated. An empty list should be returned.
     */
    @Test
    public void getFieldsAnnotatedWith_annotationNotFound() {
    	Set<Field> annotatedFields = AnnotationUtils.getFieldsAnnotatedWith(TestClassNoAnnotation.class, TestAnnotation.class);
        assertTrue(annotatedFields.isEmpty());
    }


    /**
     * Test to get all annotated fields, including the fields of the annotated super class. Both fields should be returned.
     */
    @Test
    public void getFieldsAnnotatedWith_fieldFromSuperClass() {
    	Set<Field> annotatedFields = AnnotationUtils.getFieldsAnnotatedWith(TestSubClass.class, TestAnnotation.class);
        assertPropertyLenientEquals("name", asList("field", "subField"), annotatedFields);
    }


    /**
     * Test to get all annotated methods.
     */
    @Test
    public void getMethodsAnnotatedWith() {
    	Set<Method> annotatedMethods = AnnotationUtils.getMethodsAnnotatedWith(TestClass.class, TestAnnotation.class);
        assertPropertyLenientEquals("name", asList("annotatedMethod"), annotatedMethods);
    }


    /**
     * Test to get all annotated methods, but no methods are annotated. An empty list should be returned.
     */
    @Test
    public void getMethodsAnnotatedWith_annotationNotFound() {
    	Set<Method> annotatedMethods = AnnotationUtils.getMethodsAnnotatedWith(TestClassNoAnnotation.class, TestAnnotation.class);
        assertTrue(annotatedMethods.isEmpty());
    }


    /**
     * Test to get all annotated methods, including the methods of the annotated super class. Both methods should be returned.
     */
    @Test
    public void getMethodsAnnotatedWith_methodFromSuperClass() {
    	Set<Method> annotatedMethods = AnnotationUtils.getMethodsAnnotatedWith(TestSubClass.class, TestAnnotation.class);
        assertPropertyLenientEquals("name", asList("annotatedMethod", "annotatedSubMethod"), annotatedMethods);
    }


    /**
     * Test to get all annotated methods, excluding the methods of the annotated super class.
     */
    @Test
    public void getMethodsAnnotatedWith_noMethodsFromSuperClass() {
    	Set<Method> annotatedMethods = AnnotationUtils.getMethodsAnnotatedWith(TestSubClass.class, TestAnnotation.class, false);
        assertPropertyLenientEquals("name", asList("annotatedSubMethod"), annotatedMethods);
    }

    /**
     * Tests getting a method level annotation
     */
    @Test
    public void getMethodOrClassLevelAnnotation_methodLevel() throws Exception {
        TestAnnotation testAnnotation = getMethodOrClassLevelAnnotation(TestAnnotation.class, TestClass.class.getDeclaredMethod("annotatedMethod"), TestClass.class);
        assertEquals("method", testAnnotation.level());
    }

    /**
     * Tests getting a class level annotation
     */
    @Test
    public void getMethodOrClassLevelAnnotation_classLevel() throws Exception {
        TestAnnotation testAnnotation = getMethodOrClassLevelAnnotation(TestAnnotation.class, TestClass.class.getDeclaredMethod("unAnnotatedMethod"), TestClass.class);
        assertEquals("class", testAnnotation.level());
    }

    /**
     * Tests getting an annotation from the superclass
     */
    @Test
    public void getMethodOrClassLevelAnnotation_superClassLevel() throws Exception {
        TestAnnotation testAnnotation = getMethodOrClassLevelAnnotation(TestAnnotation.class, TestSubClass.class.getDeclaredMethod("unAnnotatedSubMethod"), TestSubClass.class);
        assertEquals("class", testAnnotation.level());
    }
    
    /**
     * Tests getting an annotation from the subclass
     */
    @Test
    public void getMethodOrClassLevelAnnotation_subClassLevel() throws Exception {
        TestAnnotation testAnnotation = getMethodOrClassLevelAnnotation(TestAnnotation.class, TestClass.class.getDeclaredMethod("unAnnotatedMethod"), TestSubClass.class);
        assertEquals("class", testAnnotation.level());
    }

    /**
     * Tests getting a method level annotation property
     */
    @Test
    public void getMethodOrClassLevelAnnotationProperty_methodLevel() throws Exception {
        String level = getMethodOrClassLevelAnnotationProperty(TestAnnotation.class, "level",
                "", TestClass.class.getDeclaredMethod("annotatedMethod"), TestClass.class);
        assertEquals("method", level);
    }

    /**
     * Tests getting a class level annotation property
     */
    @Test
    public void getMethodOrClassLevelAnnotationProperty_classLevel() throws Exception {
        String level = getMethodOrClassLevelAnnotationProperty(TestAnnotation.class, "level",
                "", TestClass.class.getDeclaredMethod("unAnnotatedMethod"), TestClass.class);
        assertEquals("class", level);
    }

    /**
     * Tests getting a super class level annotation property
     */
    @Test
    public void getMethodOrClassLevelAnnotationProperty_superClassLevel() throws Exception {
        String level = getMethodOrClassLevelAnnotationProperty(TestAnnotation.class, "level",
                "", TestSubClass.class.getDeclaredMethod("unAnnotatedSubMethod"), TestSubClass.class);
        assertEquals("class", level);
    }

    /**
     * Tests getting an annotation property when the method level annotation uses the default
     */
    @Test
    public void getMethodOrClassLevelAnnotationProperty_methodLevelWithDefaultProperty() throws Exception {
        String level = getMethodOrClassLevelAnnotationProperty(TestAnnotation.class, "level",
                "", TestSubClass.class.getDeclaredMethod("annotatedSubMethod"), TestSubClass.class);
        assertEquals("class", level);
    }
    
    /**
     * Tests getting an annotation property when a subclass exists that overrides the class-level annotation,
     * and no annotation exists on the method
     */
    @Test
    public void getMethodOrClassLevelAnnotationProperty_subClassWithMethodOnSuperClass() throws Exception {
    	String level = getMethodOrClassLevelAnnotationProperty(TestAnnotation.class, "level",
                "", TestClass.class.getDeclaredMethod("unAnnotatedMethod"), TestAnnotatedSubClass.class);
        assertEquals("subClass", level);
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
    @TestAnnotation(level="subClass")
    private static class TestAnnotatedSubClass extends TestClass {

    }

}
