/*
 * Copyright 2006-2007,  Unitils.org
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

import static java.util.Arrays.asList;
import static org.unitils.util.CollectionUtils.asSet;
import static org.unitils.util.ReflectionUtils.createInstanceOfType;
import static org.unitils.util.ReflectionUtils.getFieldName;
import static org.unitils.util.ReflectionUtils.getFieldValue;
import static org.unitils.util.ReflectionUtils.invokeMethod;
import static org.unitils.util.ReflectionUtils.setFieldAndSetterValue;
import static org.unitils.util.ReflectionUtils.setFieldValue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;

import junit.framework.TestCase;

import org.unitils.core.UnitilsException;

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
    @Override
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
        String result = (String) createInstanceOfType("java.lang.String", false);
        assertNotNull(result);
    }


    /**
     * Test for creating a class instance, but with an unexisting class name.
     */
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
    public void testGetFieldValue() {
        Object result = getFieldValue(testObject, field);
        assertEquals("testValue", result);
    }


    /**
     * Test for getting the value of a field that is not of the test object.
     */
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
     * Test for setting the value of a field.
     */
    public void testSetFieldValue() {
        setFieldValue(testObject, field, "newValue");
        assertEquals("newValue", testObject.getField());
    }


    /**
     * Test for setting the value of a field. Null value.
     */
    public void testSetFieldValue_null() {
        setFieldValue(testObject, field, null);
        assertNull(testObject.getField());
    }


    /**
     * Test for setting the value of a field that is not of the test object.
     */
    public void testSetFieldValue_unexistingField() throws Exception {
        //get another field
        Field anotherField = getClass().getDeclaredField("testObject");

        try {
            setFieldValue(testObject, anotherField, "newValue");
            fail("UnitilsException expected");

        } catch (UnitilsException e) {
            //expected
        }
    }


    /**
     * Test for setting the value of a field and setter.
     */
    public void testSetFieldAndSetterValue_field() {
        setFieldAndSetterValue(testObject, asSet(field), new HashSet<Method>(), "newValue");
        assertEquals("newValue", testObject.getField());
    }


    /**
     * Test for setting the value of a field and setter.
     */
    public void testSetFieldAndSetterValue_setter() {
        setFieldAndSetterValue(testObject, new HashSet<Field>(), asSet(fieldSetterMethod), "newValue");
        assertEquals("newValue", testObject.getField());
    }


    /**
     * Test for setting the value of a field and setter. Null value
     */
    public void testSetFieldAndSetterValue_null() {
        setFieldAndSetterValue(testObject, asSet(field), asSet(fieldSetterMethod), null);
        assertNull(testObject.getField());
    }


    /**
     * Test for setting the value of a field and setter. Field not found
     */
    public void testSetFieldAndSetterValue_unexistingField() throws Exception {
        //get another field
        Field anotherField = getClass().getDeclaredField("testObject");
        try {
            setFieldAndSetterValue(testObject, asSet(anotherField), asSet(fieldSetterMethod), "newValue");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            //expected
        }
    }


    /**
     * Test for setting the value of a field and setter. Method not found
     */
    public void testSetFieldAndSetterValue_unexistingMethod() throws Exception {
        //get another field
        Method anotherMethod = getClass().getDeclaredMethod("testInvokeMethod_unexistingMethod");
        try {
            setFieldAndSetterValue(testObject, asSet(field), asSet(anotherMethod), "newValue");
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
            setFieldValue(testObject, field, 0);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            //expected
        }
    }


    /**
     * Test for performing a method invocation.
     */
    public void testInvokeMethod() throws Exception {
        Object result = invokeMethod(testObject, fieldSetterMethod, "newValue");
        assertNull(result);
        assertEquals("newValue", testObject.getField());
    }


    /**
     * Test for performing a method invocation. Null value
     */
    public void testInvokeMethod_null() throws Exception {
        Object result = invokeMethod(testObject, fieldSetterMethod, (Object) null);
        assertNull(result);
        assertNull(testObject.getField());
    }


    /**
     * Test for performing a method invocation of a method that is not of the test object.
     */
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
    public void testInvokeMethod_wrongType() throws Exception {
        try {
            invokeMethod(testObject, fieldSetterMethod, 0);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            //expected
        }
    }


    /**
     * Test for performing a method invocation. Null value
     */
    public void testGetFieldName() throws Exception {
        String result = getFieldName(fieldSetterMethod);
        assertEquals("field", result);
    }


    /**
     * Test for performing a method invocation. Null value
     */
    public void testGetFieldName_noSetter() throws Exception {
        Method anotherMethod = getClass().getDeclaredMethod("testGetFieldName_noSetter");
        try {
            getFieldName(anotherMethod);
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
