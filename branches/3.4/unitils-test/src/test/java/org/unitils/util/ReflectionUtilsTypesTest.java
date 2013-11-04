/*
 * Copyright 2008,  Unitils.org
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

import static junit.framework.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.unitils.reflectionassert.ReflectionAssert;

import static org.unitils.reflectionassert.ReflectionAssert.assertPropertyLenientEquals;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static java.util.Arrays.asList;

import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

/**
 * Test for {@link ReflectionUtils} working with field types, eg assignable from.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class ReflectionUtilsTypesTest {
    
    private TestClass1 testclass;

    /** */
    @Before
    public void setUp() {
        testclass = new TestClass1();
    }


    /**
     * Test for getting all non-static fields assignable from.
     */
    @Test
    public void testGetFieldsAssignableFrom() {
        Set<Field> fields = ReflectionUtils.getFieldsAssignableFrom(TestSubClass.class, String.class, false);
        assertPropertyLenientEquals("name", asList("objectField", "stringField", "subClassObjectField", "subClassStringField"), fields);
    }


    /**
     * Test for getting all static fields assignable from.
     */
    @Test
    public void testGetFieldsAssignableFrom_static() {
        Set<Field> fields = ReflectionUtils.getFieldsAssignableFrom(TestSubClass.class, String.class, true);
        assertPropertyLenientEquals("name", asList("staticObjectField", "staticStringField", "subClassStaticObjectField", "subClassStaticStringField"), fields);
    }


    /**
     * Test for getting all non-static fields but no assignable from found. An empty list should be returned.
     * A list should not contain a field of type test object.
     */
    @Test
    public void testGetFieldsAssignableFrom_noneFound() {
        Set<Field> fields = ReflectionUtils.getFieldsAssignableFrom(List.class, TestSubClass.class, false);
        assertTrue(fields.isEmpty());
    }


    /**
     * Test for getting all non-static primitive fields assignable from.
     * Note: {@link Integer#TYPE} is the class type of a primitive int.
     */
    @Test
    public void testGetFieldsAssignableFrom_primitive() {
        Set<Field> fields = ReflectionUtils.getFieldsAssignableFrom(TestSubClass.class, Integer.TYPE, false);
        assertPropertyLenientEquals("name", asList("intField", "subClassIntField"), fields);
    }


    /**
     * Test for getting the first non-static field having exact same type.
     */
    @Test
    public void testGetFieldsOfType() {
        Set<Field> fields = ReflectionUtils.getFieldsOfType(TestSubClass.class, String.class, false);
        assertPropertyLenientEquals("name", asList("stringField", "subClassStringField"), fields);
    }


    /**
     * Test for getting the first static field having exact same type.
     */
    @Test
    public void testGetFieldsOfType_static() {
        Set<Field> field = ReflectionUtils.getFieldsOfType(TestSubClass.class, String.class, true);
        assertPropertyLenientEquals("name", asList("staticStringField", "subClassStaticStringField"), field);
    }


    /**
     * Test for getting the first non-static field but none has exact same type.
     */
    @Test
    public void testGetFieldsOfType_noneFound() {
        Set<Field> fields = ReflectionUtils.getFieldsOfType(TestSubClass.class, List.class, false);
        assertEquals(0, fields.size());
    }


    /**
     * Test for getting the first non-static primitive field.
     * Note: {@link Integer#TYPE} is the class type of a primitive int.
     */
    @Test
    public void testGetFieldsOfType_primitive() {
        Set<Field> fields = ReflectionUtils.getFieldsOfType(TestSubClass.class, Integer.TYPE, false);
        assertPropertyLenientEquals("name", asList("intField", "subClassIntField"), fields);
    }


    /**
     * Test for getting all non-static setters for fields assignable from.
     */
    @Test
    public void testGetSettersAssignableFrom() {
        Set<Method> methods = ReflectionUtils.getSettersAssignableFrom(TestSubClass.class, String.class, false);
        assertPropertyLenientEquals("name", asList("setObjectField", "setStringField", "setSubClassObjectField", "setSubClassStringField"), methods);
    }


    /**
     * Test for getting all static setters for fields assignable from.
     */
    @Test
    public void testGetSettersAssignableFrom_static() {
        Set<Method> methods = ReflectionUtils.getSettersAssignableFrom(TestSubClass.class, String.class, true);
        assertPropertyLenientEquals("name", asList("setStaticObjectField", "setStaticStringField", "setSubClassStaticObjectField", "setSubClassStaticStringField"), methods);
    }


    /**
     * Test for getting all non-static setters for fields but no assignable from found. An empty list should be returned.
     * A list should not contain a field of type test object.
     */
    @Test
    public void testGetSettersAssignableFrom_noneFound() {
        Set<Method> methods = ReflectionUtils.getSettersAssignableFrom(List.class, TestSubClass.class, false);
        assertTrue(methods.isEmpty());
    }


    /**
     * Test for getting all non-static setters for primitive fields assignable from.
     * Note: {@link Integer#TYPE} is the class type of a primitive int.
     */
    @Test
    public void testGetSettersAssignableFrom_primitive() {
        Set<Method> methods = ReflectionUtils.getSettersAssignableFrom(TestSubClass.class, Integer.TYPE, false);
        assertPropertyLenientEquals("name", asList("setIntField", "setSubClassIntField"), methods);
    }


    /**
     * Test for getting the first non-static setter for a field having exact same type.
     */
    @Test
    public void testGetFirstSetterOfType() {
        Set<Method> methods = ReflectionUtils.getSettersOfType(TestSubClass.class, String.class, false);
        assertPropertyLenientEquals("name", asList("setStringField", "setSubClassStringField"), methods);
    }


    /**
     * Test for getting the first static setter for a field having exact same type.
     */
    @Test
    public void testGetFirstSetterOfType_static() {
        Set<Method> methods = ReflectionUtils.getSettersOfType(TestSubClass.class, String.class, true);
        assertPropertyLenientEquals("name", asList("setStaticStringField", "setSubClassStaticStringField"), methods);
    }


    /**
     * Test for getting the first non-static primitive setter for a field but none has exact same type.
     */
    @Test
    public void testGetFirstSetterOfType_noneFound() {
        Set<Method> methods = ReflectionUtils.getSettersOfType(TestSubClass.class, List.class, false);
        assertEquals(0, methods.size());
    }


    /**
     * Test for getting the first non-static primitive setter for a field having exact same type.
     */
    @Test
    public void testGetSettersOfType_primitive() {
        Set<Method> methods = ReflectionUtils.getSettersOfType(TestSubClass.class, Integer.TYPE, false);
        assertPropertyLenientEquals("name", asList("setIntField", "setSubClassIntField"), methods);
    }


    /**
     * Test for getting a setter of a property.
     */
    @Test
    public void testGetSetter() {
        Method method = ReflectionUtils.getSetter(TestSubClass.class, "stringField", false);
        assertPropertyLenientEquals("name", "setStringField", method);
    }


    /**
     * Test for getting a setter of a static property.
     */
    @Test
    public void testGetSetter_static() {
        Method method = ReflectionUtils.getSetter(TestSubClass.class, "staticStringField", true);
        assertPropertyLenientEquals("name", "setStaticStringField", method);
    }


    /**
     * Test for getting a setter of an unexisting property. Null should be returned.
     */
    @Test
    public void testGetSetter_unexistingField() {
        Method method = ReflectionUtils.getSetter(TestSubClass.class, "xxxx", false);
        assertNull(method);
    }

    /**
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    @Test
    public void testString() throws SecurityException, NoSuchFieldException {
        ReflectionUtils.setFieldValue(testclass, "stringTest", "test");
        Assert.assertEquals("test", testclass.getStringTest());
    }


    /**
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    @Test
    public void testInt() throws SecurityException, NoSuchFieldException{
        ReflectionUtils.setFieldValue(testclass, "intTest", 5);
        Assert.assertEquals(5, testclass.getIntTest());
    }


    /**
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    @Test
    public void testByte() throws SecurityException, NoSuchFieldException{
        ReflectionUtils.setFieldValue(testclass, "byteTest", new Byte("125"));
        ReflectionAssert.assertLenientEquals(new Byte("125"), testclass.getByteTest());
    }

    /**
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    @Test
    public void testObject() throws SecurityException, NoSuchFieldException{
        Person expected = new Person("testName", "testSurname");
        ReflectionUtils.setFieldValue(testclass, "personTest", expected);
        ReflectionAssert.assertLenientEquals(expected, testclass.getPersonTest());
    }

    /**
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    @Test
    public void testArray() throws SecurityException, NoSuchFieldException{
        String[] expected = new String[]{"test1", "test2", "test3", "test4", "test5"};
        ReflectionUtils.setFieldValue(testclass, "arrTest", expected);
        ReflectionAssert.assertLenientEquals(expected, testclass.getArrTest());
    }

    @Test
    public void testGetAllMethods() {
        Set<Method> actual = ReflectionUtils.getAllMethods(Person.class, false, String.class);
        Assert.assertEquals(2, actual.size());
        
    }
    
    @Test
    public void testGetAllMethodsStatic() {
        ReflectionUtils.getAllMethods(Person.class, true);
    }


    /**
     * Test for getting a getter of a property.
     */
    @Test
    public void testGetGetter() {
        Method method = ReflectionUtils.getGetter(TestSubClass.class, "stringField", false);
        assertPropertyLenientEquals("name", "getStringField", method);
    }


    /**
     * Test for getting a getter of a static property.
     */
    @Test
    public void testGetGetter_static() {
        Method method = ReflectionUtils.getGetter(TestSubClass.class, "staticStringField", true);
        assertPropertyLenientEquals("name", "getStaticStringField", method);
    }


    /**
     * Test for getting a getter of an unexisting property. Null should be returned.
     */
    @Test
    public void testGetGetter_unexistingField() {
        Method method = ReflectionUtils.getGetter(TestSubClass.class, "xxxx", false);
        assertNull(method);
    }

    /**
     * Test for getting a getter for a setter method.
     */
    @Test
    public void testGetGetterForSetter() {
        Method setter = ReflectionUtils.getSetter(TestSubClass.class, "stringField", false);
        Method method = ReflectionUtils.getGetter(setter, false);
        assertPropertyLenientEquals("name", "getStringField", method);
    }


    /**
     * Test for getting a getter for a static setter method.
     */
    @Test
    public void testGetGetterForSetter_static() {
        Method setter = ReflectionUtils.getSetter(TestSubClass.class, "staticStringField", true);
        Method method = ReflectionUtils.getGetter(setter, true);
        assertPropertyLenientEquals("name", "getStaticStringField", method);
    }


    /**
     * Test for getting a unexisting getter of a setter. Null should be returned.
     * The setterOnlyStringField has no getter method.
     */
    @Test
    public void testGetGetterForSetter_unexistingGetter() {
        Method setter = ReflectionUtils.getSetter(TestSubClass.class, "setterOnlyField", false);
        Method method = ReflectionUtils.getGetter(setter, false);
        assertNull(method);
    }

    /**
     * Test for getting a field.
     */
    @Test
    public void testGetFieldWithName() {
        Field field = ReflectionUtils.getFieldWithName(TestSubClass.class, "stringField", false);
        assertPropertyLenientEquals("name", "stringField", field);
    }


    /**
     * Test for getting a static field.
     */
    @Test
    public void testGetFieldWithName_static() {
        Field field = ReflectionUtils.getFieldWithName(TestSubClass.class, "staticStringField", true);
        assertPropertyLenientEquals("name", "staticStringField", field);
    }


    /**
     * Test for getting an unexisting field. Null should be returned.
     */
    @Test
    public void testGetFieldWithName_unexistingField() {
        Field field = ReflectionUtils.getFieldWithName(TestSubClass.class, "xxxx", false);
        assertNull(field);
    }
    
    /**
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    @Test(expected = NoSuchFieldException.class)
    public void testError() throws SecurityException, NoSuchFieldException {
        ReflectionUtils.setFieldValue(testclass, "testtest", "test");
    }


    /**
     * A test object containing static and non-static fields of different types.
     */
    private static class TestClass {

        private static Object staticObjectField;

        private static String staticStringField;

        private static int staticIntField;

        private Object objectField;

        private String stringField;

        private int intField;

        /* has no getter */
        protected Map<?, ?> setterOnlyField;


        public static Object getStaticObjectField() {
            return staticObjectField;
        }

        public static void setStaticObjectField(Object staticObjectField) {
            TestClass.staticObjectField = staticObjectField;
        }

        public static String getStaticStringField() {
            return staticStringField;
        }

        public static void setStaticStringField(String staticStringField) {
            TestClass.staticStringField = staticStringField;
        }

        public static int getStaticIntField() {
            return staticIntField;
        }

        public static void setStaticIntField(int staticIntField) {
            TestClass.staticIntField = staticIntField;
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

        public void setSetterOnlyField(Map<?, ?> setterOnlyField) {
            this.setterOnlyField = setterOnlyField;
        }

    }

    public static class TestSubClass extends TestClass {

        private static Object subClassStaticObjectField;

        private static String subClassStaticStringField;

        private static int subClassStaticIntField;

        private Object subClassObjectField;

        private String subClassStringField;

        private int subClassIntField;

        /* has no getter */
        protected Map<?, ?> subClassSetterOnlyField;


        public static Object getSubClassStaticObjectField() {
            return subClassStaticObjectField;
        }

        public static void setSubClassStaticObjectField(Object staticObjectField) {
            subClassStaticObjectField = staticObjectField;
        }

        public static String getSubClassStaticStringField() {
            return subClassStaticStringField;
        }

        public static void setSubClassStaticStringField(String staticStringField) {
            subClassStaticStringField = staticStringField;
        }

        public static int getSubClassStaticIntField() {
            return subClassStaticIntField;
        }

        public static void setSubClassStaticIntField(int staticIntField) {
            subClassStaticIntField = staticIntField;
        }

        public Object getSubClassObjectField() {
            return subClassObjectField;
        }

        public void setSubClassObjectField(Object objectField) {
            this.subClassObjectField = objectField;
        }

        public String getSubClassStringField() {
            return subClassStringField;
        }

        public void setSubClassStringField(String stringField) {
            this.subClassStringField = stringField;
        }

        public int getSubClassIntField() {
            return subClassIntField;
        }

        public void setSubClassIntField(int intField) {
            this.subClassIntField = intField;
        }

        public void setSubClassSetterOnlyField(Map<?, ?> setterOnlyField) {
            this.subClassSetterOnlyField = setterOnlyField;
        }
    }
    
    /**
     * 
     * Just a testclass.
     * 
     * @author Jeroen Horemans
     * @author Thomas De Rycke
     * @author Willemijn Wouters
     * 
     * @since 3.4
     *
     */
    private class TestClass1 {
        private String stringTest;
        private int intTest;
        private byte byteTest;
        private Person personTest;
        private String[] arrTest;

        /**
         * @return the stringTest
         */
        public String getStringTest() {
            return stringTest;
        }

        /**
         * @return the intTest
         */
        public int getIntTest() {
            return intTest;
        }

        /**
         * @return the byteTest
         */
        public byte getByteTest() {
            return byteTest;
        }

        /**
         * @return the personTest
         */
        public Person getPersonTest() {
            return personTest;
        }

        /**
         * @return the arrTest
         */
        public String[] getArrTest() {
            return arrTest;
        }





    }
    @SuppressWarnings("unused")
    private static class Person {
        private static int countPersons = 0;
        private String name;
        private String surname;
        private boolean female;
        /**
         * @param name
         * @param surname
         */
        public Person(String name, String surname) {
            super();
            this.name = name;
            this.surname = surname;
        }
        
        
        /**
         * @return the name
         */
        public String getName() {
            return name;
        }
        
        
        /**
         * @return the surname
         */
        public String getSurname() {
            return surname;
        }
        
        
        /**
         * @param name the name to set
         */
        public void setName(String name) {
            this.name = name;
        }
        
        /**
         * @param surname the surname to set
         */
        public void setSurname(String surname) {
            this.surname = surname;
        }
        
        
        /**
         * @return the countPersons
         */
        public static int getCountPersons() {
            return countPersons;
        }
    }
}
