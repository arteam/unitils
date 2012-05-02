/*
 * Copyright 2012,  Unitils.org
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

package org.unitilsnew.core;

import org.junit.Before;
import org.junit.Test;
import org.unitilsnew.core.reflect.ClassWrapper;

import java.lang.annotation.Retention;
import java.lang.reflect.Method;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Tim Ducheyne
 */
public class TestInstanceGetMethodAnnotationTest {

    /* Tested object */
    private TestInstance testInstance;

    private ClassWrapper classWrapper;
    private Method annotationMethod;
    private Method noAnnotationMethod;


    @Before
    public void initialize() throws Exception {
        annotationMethod = MyClass.class.getMethod("annotation");
        noAnnotationMethod = MyClass.class.getMethod("noAnnotation");
        classWrapper = new ClassWrapper(MyClass.class);
    }


    @Test
    public void annotation() {
        testInstance = new TestInstance(classWrapper, null, annotationMethod);

        MyAnnotation result = testInstance.getMethodAnnotation(MyAnnotation.class);
        assertEquals("annotationValue", result.value());
    }

    @Test
    public void nullWhenNoAnnotation() {
        testInstance = new TestInstance(classWrapper, null, noAnnotationMethod);

        MyAnnotation result = testInstance.getMethodAnnotation(MyAnnotation.class);
        assertNull(result);
    }


    @Retention(RUNTIME)
    private @interface MyAnnotation {
        String value();
    }

    private static class MyClass {

        @MyAnnotation("annotationValue")
        public void annotation() {
        }

        public void noAnnotation() {
        }
    }
}
