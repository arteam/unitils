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
package org.unitils.util;

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.Assert.assertNull;
import static org.unitils.reflectionassert.ReflectionAssert.assertPropertyLenientEquals;

/**
 * Test for {@link ReflectionUtils} working with field types, eg assignable from.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class ReflectionUtilsTypesTest {

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
}
