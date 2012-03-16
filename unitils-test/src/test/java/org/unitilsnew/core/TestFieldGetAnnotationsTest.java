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

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.reflect.Field;
import java.util.List;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class TestFieldGetAnnotationsTest {

    /* Tested object */
    private TestField testField;

    private Field field;
    private Field noAnnotationsField;
    private Object testObject;


    @Before
    public void initialize() throws Exception {
        field = MyClass.class.getDeclaredField("field");
        noAnnotationsField = MyClass.class.getDeclaredField("noAnnotationsField");
        testObject = new MyClass();

        testField = new TestField(field, testObject);
    }


    @Test
    public void annotations() {
        List<Annotation> result = testField.getAnnotations();

        assertEquals(2, result.size());
        assertEquals("annotation1", ((MyAnnotation1) result.get(0)).value());
        assertEquals("annotation2", ((MyAnnotation2) result.get(1)).value());
    }

    @Test
    public void noAnnotationsFound() {
        testField = new TestField(noAnnotationsField, testObject);

        List<Annotation> result = testField.getAnnotations();
        assertTrue(result.isEmpty());
    }

    @Test
    public void annotationsAreCached() {
        List<Annotation> result1 = testField.getAnnotations();
        List<Annotation> result2 = testField.getAnnotations();

        assertSame(result1, result2);
    }


    @Retention(RUNTIME)
    private @interface MyAnnotation1 {
        String value();
    }

    @Retention(RUNTIME)
    private @interface MyAnnotation2 {
        String value();
    }

    private static class MyClass {

        @MyAnnotation1("annotation1")
        @MyAnnotation2("annotation2")
        private String field;
        private String noAnnotationsField;
    }
}
