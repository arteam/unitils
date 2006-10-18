/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.util;

import junit.framework.TestCase;
import org.unitils.core.UnitilsException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Test for {@link ReflectionUtils}.
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
    public void testInvokeMethod() {
        Object result = ReflectionUtils.invokeMethod(testObject, fieldSetterMethod, "newValue");
        assertNull(result);
        assertEquals("newValue", testObject.getField());
    }


    /**
     * Test for performing a method invocation. Null value
     */
    public void testInvokeMethod_null() {
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
