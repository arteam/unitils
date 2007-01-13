/*
 * Copyright 2006 the original author or authors.
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

import junit.framework.TestCase;
import org.unitils.core.UnitilsException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Test for {@link ReflectionUtils}.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class ReflectionUtilsTest extends TestCase {


    /* A test object instance */
    private TestObject testObject;

    /* A field in the test object */
    private Field field;

    /* A setter method in the test object */
    private Method fieldSetterMethod;


    /**
     * Sets up the test fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();

        testObject = new TestObject();
        field = TestObject.class.getDeclaredField("field");
        fieldSetterMethod = TestObject.class.getDeclaredMethod("setField", String.class);
    }


    /**
     * Test for creating a class instance.
     */
    public void testCreateInstanceOfType() {
        String result = ReflectionUtils.createInstanceOfType("java.lang.String");
        assertNotNull(result);
    }


    /**
     * Test for creating a class instance, but with an unexisting class name.
     */
    public void testCreateInstanceOfType_classNotFound() {
        try {
            ReflectionUtils.createInstanceOfType("xxxxxx");
            fail("UnitilsException expected");

        } catch (UnitilsException e) {
            //expected
        }
    }


    /**
     * Test for getting the value of a field.
     */
    public void testGetFieldValue() {
        Object result = ReflectionUtils.getFieldValue(testObject, field);
        assertEquals("testValue", result);
    }


    /**
     * Test for getting the value of a field that is not of the test object.
     */
    public void testGetFieldValue_unexistingField() throws Exception {

        //get another field
        Field anotherField = getClass().getDeclaredField("testObject");
        try {
            ReflectionUtils.getFieldValue(testObject, anotherField);
            fail("UnitilsException expected");

        } catch (UnitilsException e) {
            //expected
        }
    }


    /**
     * Test for setting the value of a field.
     */
    public void testSetFieldValue() {
        ReflectionUtils.setFieldValue(testObject, field, "newValue");
        assertEquals("newValue", testObject.getField());
    }


    /**
     * Test for setting the value of a field. Null value.
     */
    public void testSetFieldValue_null() {
        ReflectionUtils.setFieldValue(testObject, field, null);
        assertNull(testObject.getField());
    }


    /**
     * Test for setting the value of a field that is not of the test object.
     */
    public void testSetFieldValue_unexistingField() throws Exception {

        //get another field
        Field anotherField = getClass().getDeclaredField("testObject");

        try {
            ReflectionUtils.setFieldValue(testObject, anotherField, "newValue");
            fail("UnitilsException expected");

        } catch (UnitilsException e) {
            //expected
        }
    }


    /**
     * Test for setting the value of a field that is of a wrong type.
     */
    public void testSetFieldValue_wrongType() throws Exception {

        try {
            ReflectionUtils.setFieldValue(testObject, field, 0);
            fail("UnitilsException expected");

        } catch (UnitilsException e) {
            //expected
        }
    }


    /**
     * Test for performing a method invocation.
     */
    public void testInvokeMethod() throws InvocationTargetException {
        Object result = ReflectionUtils.invokeMethod(testObject, fieldSetterMethod, "newValue");
        assertNull(result);
        assertEquals("newValue", testObject.getField());
    }


    /**
     * Test for performing a method invocation. Null value
     */
    public void testInvokeMethod_null() throws InvocationTargetException {
        Object result = ReflectionUtils.invokeMethod(testObject, fieldSetterMethod, (Object) null);
        assertNull(result);
        assertNull(testObject.getField());
    }

    /**
     * Test for performing a method invocation of a method that is not of the test object.
     */
    public void testInvokeMethod_unexistingMethod() throws Exception {

        //get another field
        Method anotherMethod = getClass().getDeclaredMethod("testInvokeMethod_unexistingMethod");

        try {
            ReflectionUtils.invokeMethod(testObject, anotherMethod);
            fail("UnitilsException expected");

        } catch (UnitilsException e) {
            //expected
        }
    }


    /**
     * Test for performing a method invocation of a field that is of a wrong type.
     */
    public void testInvokeMethod_wrongType() throws Exception {

        try {
            ReflectionUtils.invokeMethod(testObject, fieldSetterMethod, 0);
            fail("UnitilsException expected");

        } catch (UnitilsException e) {
            //expected
        }
    }


    /**
     * A test object containing a private field.
     */
    private static class TestObject {

        private String field = "testValue";

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }
    }
}
