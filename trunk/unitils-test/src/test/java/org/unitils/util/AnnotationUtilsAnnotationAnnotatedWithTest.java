/*
 * Copyright Unitils.org
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
import java.lang.reflect.Method;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.unitils.util.AnnotationUtils.getMethodOrClassLevelAnnotationAnnotatedWith;

/**
 * Test for {@link org.unitils.util.AnnotationUtils}.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class AnnotationUtilsAnnotationAnnotatedWithTest {

    @Test
    public void annotatedMethod() throws Exception {
        Method method = TestClass.class.getDeclaredMethod("annotatedMethod");
        AnnotatedAnnotation result = getMethodOrClassLevelAnnotationAnnotatedWith(TestAnnotation.class, method, TestClass.class);
        assertNotNull(result);
    }

    @Test
    public void classLevelAnnotation() throws Exception {
        Method method = TestClass.class.getDeclaredMethod("notAnnotatedMethod");
        AnnotatedAnnotation result = getMethodOrClassLevelAnnotationAnnotatedWith(TestAnnotation.class, method, TestClass.class);
        assertNotNull(result);
    }

    @Test
    public void noAnnotation() throws Exception {
        Method method = TestClassNoAnnotation.class.getDeclaredMethod("notAnnotatedMethod");
        AnnotatedAnnotation result = getMethodOrClassLevelAnnotationAnnotatedWith(TestAnnotation.class, method, TestClassNoAnnotation.class);
        assertNull(result);
    }

    @Test
    public void notAnnotatedAnnotation() throws Exception {
        Method method = TestClassNoAnnotation.class.getDeclaredMethod("notAnnotatedAnnotationMethod");
        AnnotatedAnnotation result = getMethodOrClassLevelAnnotationAnnotatedWith(TestAnnotation.class, method, TestClassNoAnnotation.class);
        assertNull(result);
    }

    @Test
    public void annotationOnSuperClass() throws Exception {
        Method method = TestSubClass.class.getDeclaredMethod("notAnnotatedSubMethod");
        AnnotatedAnnotation result = getMethodOrClassLevelAnnotationAnnotatedWith(TestAnnotation.class, method, TestSubClass.class);
        assertNotNull(result);
    }


    @Target({TYPE})
    @Retention(RUNTIME)
    private @interface TestAnnotation {
    }

    @Target({TYPE, METHOD})
    @Retention(RUNTIME)
    @TestAnnotation
    private @interface AnnotatedAnnotation {
    }

    @Target({TYPE, METHOD})
    @Retention(RUNTIME)
    private @interface NotAnnotatedAnnotation {
    }


    @AnnotatedAnnotation
    private static class TestClass {

        @AnnotatedAnnotation
        private void annotatedMethod() {
        }

        private void notAnnotatedMethod() {
        }

    }

    private static class TestClassNoAnnotation {

        private void notAnnotatedMethod() {
        }

        @NotAnnotatedAnnotation
        private void notAnnotatedAnnotationMethod() {
        }
    }


    private static class TestSubClass extends TestClass {

        private void notAnnotatedSubMethod() {
        }

    }

}