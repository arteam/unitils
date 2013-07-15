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

package org.unitils.core.reflect;

import org.junit.Before;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.reflect.Field;
import java.util.List;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class FieldWrapperGetAnnotationsTest {

    /* Tested object */
    private FieldWrapper fieldWrapper;

    private Field annotationsField;
    private Field noAnnotationsField;


    @Before
    public void initialize() throws Exception {
        annotationsField = MyClass.class.getDeclaredField("annotationsField");
        noAnnotationsField = MyClass.class.getDeclaredField("noAnnotationsField");
    }


    @Test
    public void annotations() {
        fieldWrapper = new FieldWrapper(annotationsField);

        List<Annotation> result = fieldWrapper.getAnnotations();
        assertEquals(2, result.size());
        assertEquals("annotation1", ((MyAnnotation1) result.get(0)).value());
        assertEquals("annotation2", ((MyAnnotation2) result.get(1)).value());
    }

    @Test
    public void noAnnotationsFound() {
        fieldWrapper = new FieldWrapper(noAnnotationsField);

        List<Annotation> result = fieldWrapper.getAnnotations();
        assertTrue(result.isEmpty());
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
        private String annotationsField;
        private String noAnnotationsField;
    }
}
