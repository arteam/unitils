/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.util;

import junit.framework.TestCase;
import static org.unitils.reflectionassert.ReflectionAssert.assertPropertyLenEquals;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Test for {@link ReflectionUtils} working with field types, eg assignable from.
 */
public class ReflectionUtilsTypesTest extends TestCase {


    /**
     * Test for getting all non-static fields assignable from.
     */
    public void testGetFieldsAssignableFrom() {
        List<Field> fields = ReflectionUtils.getFieldsAssignableFrom(TestObject.class, String.class, false);
        assertPropertyLenEquals("name", Arrays.asList("objectField", "stringField"), fields);
    }


    /**
     * Test for getting all static fields assignable from.
     */
    public void testGetFieldsAssignableFrom_static() {
        List<Field> fields = ReflectionUtils.getFieldsAssignableFrom(TestObject.class, String.class, true);
        assertPropertyLenEquals("name", Arrays.asList("staticObjectField", "staticStringField"), fields);
    }


    /**
     * Test for getting all non-static fields but no assignable from found. An empty list should be returned.
     * A list should not contain a field of type test object.
     */
    public void testGetFieldsAssignableFrom_noneFound() {
        List<Field> fields = ReflectionUtils.getFieldsAssignableFrom(List.class, TestObject.class, false);
        assertTrue(fields.isEmpty());
    }


    /**
     * Test for getting all non-static primitive fields assignable from.
     * Note: {@link Integer#TYPE} is the class type of a primitive int.
     */
    public void testGetFieldsAssignableFrom_primitive() {
        List<Field> fields = ReflectionUtils.getFieldsAssignableFrom(TestObject.class, Integer.TYPE, false);
        assertPropertyLenEquals("name", Arrays.asList("intField"), fields);
    }


    /**
     * Test for getting the first non-static field having exact same type.
     */
    public void testGetFirstFieldOfType() {
        List<Field> fields = ReflectionUtils.getFieldsOfType(TestObject.class, String.class, false);
        assertPropertyLenEquals("name", Arrays.asList("stringField"), fields);
    }


    /**
     * Test for getting the first static field having exact same type.
     */
    public void testGetFirstFieldOfType_static() {
        List<Field> field = ReflectionUtils.getFieldsOfType(TestObject.class, String.class, true);
        assertPropertyLenEquals("name", Arrays.asList("staticStringField"), field);
    }


    /**
     * Test for getting the first non-static field but none has exact same type.
     */
    public void testGetFieldsOfType_noneFound() {
        List<Field> fields = ReflectionUtils.getFieldsOfType(TestObject.class, List.class, false);
        assertEquals(0, fields.size());
    }


    /**
     * Test for getting the first non-static primitive field.
     * Note: {@link Integer#TYPE} is the class type of a primitive int.
     */
    public void testGetFieldsOfType_primitive() {
        List<Field> fields = ReflectionUtils.getFieldsOfType(TestObject.class, Integer.TYPE, false);
        assertPropertyLenEquals("name", Arrays.asList("intField"), fields);
    }


    /**
     * Test for getting all non-static setters for fields assignable from.
     */
    public void testGetSettersAssignableFrom() {
        List<Method> methods = ReflectionUtils.getSettersAssignableFrom(TestObject.class, String.class, false);
        assertPropertyLenEquals("name", Arrays.asList("setObjectField", "setStringField"), methods);
    }


    /**
     * Test for getting all static setters for fields assignable from.
     */
    public void testGetSettersAssignableFrom_static() {
        List<Method> methods = ReflectionUtils.getSettersAssignableFrom(TestObject.class, String.class, true);
        assertPropertyLenEquals("name", Arrays.asList("setStaticObjectField", "setStaticStringField"), methods);
    }


    /**
     * Test for getting all non-static setters for fields but no assignable from found. An empty list should be returned.
     * A list should not contain a field of type test object.
     */
    public void testGetSettersAssignableFrom_noneFound() {
        List<Method> methods = ReflectionUtils.getSettersAssignableFrom(List.class, TestObject.class, false);
        assertTrue(methods.isEmpty());
    }


    /**
     * Test for getting all non-static setters for primitive fields assignable from.
     * Note: {@Integer#TYPE} is the class type of a primitive int.
     */
    public void testGetSettersAssignableFrom_primitive() {
        List<Method> methods = ReflectionUtils.getSettersAssignableFrom(TestObject.class, Integer.TYPE, false);
        assertPropertyLenEquals("name", Arrays.asList("setIntField"), methods);
    }


    /**
     * Test for getting the first non-static setter for a field having exact same type.
     */
    public void testGetFirstSetterOfType() {
        List<Method> methods = ReflectionUtils.getSettersOfType(TestObject.class, String.class, false);
        assertPropertyLenEquals("name", Arrays.asList("setStringField"), methods);
    }


    /**
     * Test for getting the first static setter for a field having exact same type.
     */
    public void testGetFirstSetterOfType_static() {
        List<Method> methods = ReflectionUtils.getSettersOfType(TestObject.class, String.class, true);
        assertPropertyLenEquals("name", Arrays.asList("setStaticStringField"), methods);
    }


    /**
     * Test for getting the first non-static primitive setter for a field but none has exact same type.
     */
    public void testGetFirstSetterOfType_noneFound() {
        List<Method> methods = ReflectionUtils.getSettersOfType(TestObject.class, List.class, false);
        assertEquals(0, methods.size());
    }


    /**
     * Test for getting the first non-static primitive setter for a field having exact same type.
     */
    public void testGetSettersOfType_primitive() {
        List<Method> methods = ReflectionUtils.getSettersOfType(TestObject.class, Integer.TYPE, false);
        assertPropertyLenEquals("name", Arrays.asList("setIntField"), methods);
    }


    /**
     * Test for getting a setter of a property.
     */
    public void testGetSetter() {
        Method method = ReflectionUtils.getSetter(TestObject.class, "stringField", false);
        assertPropertyLenEquals("name", "setStringField", method);
    }


    /**
     * Test for getting a setter of a static property.
     */
    public void testGetSetter_static() {
        Method method = ReflectionUtils.getSetter(TestObject.class, "staticStringField", true);
        assertPropertyLenEquals("name", "setStaticStringField", method);
    }


    /**
     * Test for getting a setter of an unexisting property. Null should be returned.
     */
    public void testGetSetter_unexistingField() {
        Method method = ReflectionUtils.getSetter(TestObject.class, "xxxx", false);
        assertNull(method);
    }


    /**
     * Test for getting a getter of a property.
     */
    public void testGetGetter() {
        Method method = ReflectionUtils.getGetter(TestObject.class, "stringField", false);
        assertPropertyLenEquals("name", "getStringField", method);
    }


    /**
     * Test for getting a getter of a static property.
     */
    public void testGetGetter_static() {
        Method method = ReflectionUtils.getGetter(TestObject.class, "staticStringField", true);
        assertPropertyLenEquals("name", "getStaticStringField", method);
    }


    /**
     * Test for getting a getter of an unexisting property. Null should be returned.
     */
    public void testGetGetter_unexistingField() {
        Method method = ReflectionUtils.getGetter(TestObject.class, "xxxx", false);
        assertNull(method);
    }


    /**
     * Test for getting a field.
     */
    public void testGetFieldWithName() {
        Field field = ReflectionUtils.getFieldWithName(TestObject.class, "stringField", false);
        assertPropertyLenEquals("name", "stringField", field);
    }


    /**
     * Test for getting a static field.
     */
    public void testGetFieldWithName_static() {
        Field field = ReflectionUtils.getFieldWithName(TestObject.class, "staticStringField", true);
        assertPropertyLenEquals("name", "staticStringField", field);
    }


    /**
     * Test for getting an unexisting field. Null should be returned.
     */
    public void testGetFieldWithName_unexistingField() {
        Field field = ReflectionUtils.getFieldWithName(TestObject.class, "xxxx", false);
        assertNull(field);
    }

    /**
     * A test object containing static and non-static fields of different types.
     */
    private static class TestObject {

        private static Object staticObjectField;

        private static String staticStringField;

        private static int staticIntField;

        private Object objectField;

        private String stringField;

        private int intField;


        public static Object getStaticObjectField() {
            return staticObjectField;
        }

        public static void setStaticObjectField(Object staticObjectField) {
            TestObject.staticObjectField = staticObjectField;
        }

        public static String getStaticStringField() {
            return staticStringField;
        }

        public static void setStaticStringField(String staticStringField) {
            TestObject.staticStringField = staticStringField;
        }

        public static int getStaticIntField() {
            return staticIntField;
        }

        public static void setStaticIntField(int staticIntField) {
            TestObject.staticIntField = staticIntField;
        }

        public Object getObjectField() {
            return objectField;
        }

        public void setObjectField(Object objectField) {
            this.objectField = objectField;
        }

        public String getStringField() {
            return stringField;
        }

        public void setStringField(String stringField) {
            this.stringField = stringField;
        }

        public int getIntField() {
            return intField;
        }

        public void setIntField(int intField) {
            this.intField = intField;
        }
    }
}
