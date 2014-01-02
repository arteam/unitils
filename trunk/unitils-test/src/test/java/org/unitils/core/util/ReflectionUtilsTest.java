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
package org.unitils.core.util;

import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.*;
import static org.unitils.core.util.ReflectionUtils.*;

/**
 * Test for {@link org.unitils.core.util.ReflectionUtils}.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class ReflectionUtilsTest {


    /* A test object instance */
    private TestObject testObject;
    /* A field in the test object */
    private Field field;
    /* A setter method in the test object */
    private Method fieldSetterMethod;


    /**
     * Sets up the test fixture.
     */
    @Before
    public void initialize() throws Exception {
        testObject = new TestObject();
        field = TestObject.class.getDeclaredField("field");
        fieldSetterMethod = TestObject.class.getDeclaredMethod("setField", String.class);
    }


    /**
     * Test for creating a class instance.
     */
    @Test
    public void testCreateInstanceOfType() {
        String result = createInstanceOfType("java.lang.String", false);
        assertNotNull(result);
    }


    /**
     * Test for creating a class instance, but with an unexisting class name.
     */
    @Test
    public void testCreateInstanceOfType_classNotFound() {
        try {
            createInstanceOfType("xxxxxx", false);
            fail("UnitilsException expected");

        } catch (UnitilsException e) {
            //expected
        }
    }


    /**
     * Test for getting the value of a field.
     */
    @Test
    public void testGetFieldValue() {
        Object result = getFieldValue(testObject, field);
        assertEquals("testValue", result);
    }


    /**
     * Test for getting the value of a field that is not of the test object.
     */
    @Test
    public void testGetFieldValue_unexistingField() throws Exception {
        //get another field
        Field anotherField = getClass().getDeclaredField("testObject");
        try {
            getFieldValue(testObject, anotherField);
            fail("UnitilsException expected");

        } catch (UnitilsException e) {
            //expected
        }
    }

    /**
     * Test for performing a method invocation.
     */
    @Test
    public void testInvokeMethod() throws Exception {
        Object result = invokeMethod(testObject, fieldSetterMethod, "newValue");
        assertNull(result);
        assertEquals("newValue", testObject.getField());
    }


    /**
     * Test for performing a method invocation. Null value
     */
    @Test
    public void testInvokeMethod_null() throws Exception {
        Object result = invokeMethod(testObject, fieldSetterMethod, (Object) null);
        assertNull(result);
        assertNull(testObject.getField());
    }

    /**
     * Test for performing a method invocation of a method that is not of the test object.
     */
    @Test
    public void testInvokeMethod_unexistingMethod() throws Exception {
        //get another method
        Method anotherMethod = getClass().getDeclaredMethod("testInvokeMethod_unexistingMethod");
        try {
            invokeMethod(testObject, anotherMethod);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            //expected
        }
    }

    /**
     * Test for performing a method invocation of a field that is of a wrong type.
     */
    @Test
    public void testInvokeMethod_wrongType() throws Exception {
        try {
            invokeMethod(testObject, fieldSetterMethod, 0);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            //expected
        }
    }

    /**
     * Tests creating a represenation of a method name.
     */
    @Test
    public void testGetSimpleMethodName() {
        String result = getSimpleMethodName(fieldSetterMethod);
        assertEquals("TestObject.setField()", result);
    }

    /**
     * A test object containing a private field.
     */
    private static class TestObject {

        private String field = "testValue";

        private boolean boolField = true;

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public boolean isBoolField() {
            return boolField;
        }
    }
}
