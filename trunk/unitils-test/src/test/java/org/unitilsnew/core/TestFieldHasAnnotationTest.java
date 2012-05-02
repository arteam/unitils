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
import org.unitilsnew.core.reflect.FieldWrapper;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class TestFieldHasAnnotationTest {

    /* Tested object */
    private TestField testField;


    @Before
    public void initialize() throws Exception {
        FieldWrapper field = new FieldWrapper(MyClass.class.getDeclaredField("field"));
        Object testObject = new MyClass();

        testField = new TestField(field, testObject);
    }


    @Test
    public void hasAnnotation() {
        boolean result = testField.hasAnnotation(MyAnnotation.class);
        assertTrue(result);
    }

    @Test
    public void annotationNotFound() {
        boolean result = testField.hasAnnotation(Target.class);
        assertFalse(result);
    }


    @Retention(RUNTIME)
    private @interface MyAnnotation {
    }


    private static class MyClass {

        @MyAnnotation
        private String field;
    }

}
