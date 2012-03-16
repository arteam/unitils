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

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Tim Ducheyne
 */
public class TestFieldGetAnnotationTest {

    /* Tested object */
    private TestField testField;


    @Before
    public void initialize() throws Exception {
        Field field = MyClass.class.getDeclaredField("field");
        MyClass testObject = new MyClass();

        testField = new TestField(field, testObject);
    }


    @Test
    public void annotationOfType() {
        MyAnnotation result = testField.getAnnotation(MyAnnotation.class);
        assertEquals("annotation1", result.value());
    }

    @Test
    public void nullWhenAnnotationNotFound() {
        Target result = testField.getAnnotation(Target.class);
        assertNull(result);
    }


    @Retention(RUNTIME)
    private @interface MyAnnotation {
        String value();
    }

    private static class MyClass {

        @MyAnnotation("annotation1")
        private String field;
    }
}
