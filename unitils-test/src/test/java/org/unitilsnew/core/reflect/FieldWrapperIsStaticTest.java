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

package org.unitilsnew.core.reflect;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class FieldWrapperIsStaticTest {

    /* Tested object */
    private FieldWrapper fieldWrapper;

    private Field field;
    private Field staticField;


    @Before
    public void initialize() throws Exception {
        field = MyClass.class.getDeclaredField("field");
        staticField = MyClass.class.getDeclaredField("staticField");
    }


    @Test
    public void notStatic() {
        fieldWrapper = new FieldWrapper(field);

        boolean result = fieldWrapper.isStatic();
        assertFalse(result);
    }

    @Test
    public void isStatic() {
        fieldWrapper = new FieldWrapper(staticField);

        boolean result = fieldWrapper.isStatic();
        assertTrue(result);
    }


    public static class MyClass {

        private static String staticField;
        private String field;
    }
}
