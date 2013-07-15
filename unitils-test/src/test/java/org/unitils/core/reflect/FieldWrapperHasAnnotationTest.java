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

import java.lang.annotation.Retention;
import java.lang.reflect.Field;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class FieldWrapperHasAnnotationTest {

    /* Tested object */
    private FieldWrapper fieldWrapper;

    private Field noAnnotationField;
    private Field annotationField;


    @Before
    public void initialize() throws Exception {
        noAnnotationField = MyClass.class.getDeclaredField("noAnnotationField");
        annotationField = MyClass.class.getDeclaredField("annotationField");
    }


    @Test
    public void annotation() {
        fieldWrapper = new FieldWrapper(annotationField);

        boolean result = fieldWrapper.hasAnnotation(MyAnnotation.class);
        assertTrue(result);
    }

    @Test
    public void noAnnotation() {
        fieldWrapper = new FieldWrapper(noAnnotationField);

        boolean result = fieldWrapper.hasAnnotation(MyAnnotation.class);
        assertFalse(result);
    }

    @Test
    public void noAnnotationOfType() {
        fieldWrapper = new FieldWrapper(noAnnotationField);

        boolean result = fieldWrapper.hasAnnotation(OtherAnnotation.class);
        assertFalse(result);
    }


    @Retention(RUNTIME)
    private @interface MyAnnotation {

        String value();
    }

    @Retention(RUNTIME)
    private @interface OtherAnnotation {
    }

    private static class MyClass {

        private String noAnnotationField;

        @MyAnnotation("value")
        private String annotationField;
    }

}
