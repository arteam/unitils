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

package org.unitils.core;

import org.junit.Before;
import org.junit.Test;
import org.unitils.core.reflect.ClassWrapper;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class TestInstanceGetTestFieldTest {

    /* Tested object */
    private TestInstance testInstance;

    private Object testObject;
    private Field field;
    private Field staticField;


    @Before
    public void initialize() throws Exception {
        field = MyClass.class.getDeclaredField("field");
        staticField = MyClass.class.getDeclaredField("staticField");

        ClassWrapper classWrapper = new ClassWrapper(MyClass.class);
        testObject = new MyClass();
        testInstance = new TestInstance(classWrapper, testObject, null);
    }


    @Test
    public void regularField() {
        TestField result = testInstance.getTestField("field");

        assertEquals(field, result.getField());
        assertSame(testObject, result.getTestObject());
    }

    @Test
    public void staticField() {
        TestField result = testInstance.getTestField("staticField");

        assertEquals(staticField, result.getField());
        assertSame(testObject, result.getTestObject());
    }

    @Test
    public void exceptionWhenFieldNotFound() {
        try {
            testInstance.getTestField("xxxx");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to get field with name 'xxxx'. No such field exists on class org.unitils.core.TestInstanceGetTestFieldTest$MyClass or one of its superclasses.", e.getMessage());
        }
    }


    private static class MyClass {

        private static String staticField;
        private String field;
    }
}
