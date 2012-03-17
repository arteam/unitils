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

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class TestInstanceGetTestFieldsTest {

    /* Tested object */
    private TestInstance testInstance;

    private TestClass testClass;
    private TestClass noFieldsTestClass;
    private Object testObject;
    private NoFieldsClass noFieldsTestObject;

    private Field field1a;
    private Field field2;
    private Field field1b;
    private Field field3;


    @Before
    public void initialize() throws Exception {
        field1a = SuperClass.class.getDeclaredField("field1");
        field2 = SuperClass.class.getDeclaredField("field2");
        field1b = MyClass.class.getDeclaredField("field1");
        field3 = MyClass.class.getDeclaredField("field3");

        testClass = new TestClass(MyClass.class);
        noFieldsTestClass = new TestClass(NoFieldsClass.class);
        testObject = new MyClass();
        noFieldsTestObject = new NoFieldsClass();
    }


    @Test
    public void fields() {
        testInstance = new TestInstance(testClass, testObject, null);

        List<TestField> result = testInstance.getTestFields();
        assertEquals(4, result.size());
        assertEquals(field1b, result.get(0).getField());
        assertEquals(field3, result.get(1).getField());
        assertEquals(field1a, result.get(2).getField());
        assertEquals(field2, result.get(3).getField());

        assertSame(testObject, result.get(0).testObject);
        assertSame(testObject, result.get(1).testObject);
        assertSame(testObject, result.get(2).testObject);
        assertSame(testObject, result.get(3).testObject);
    }

    @Test
    public void emptyWhenNoFields() {
        testInstance = new TestInstance(noFieldsTestClass, noFieldsTestObject, null);

        List<TestField> result = testInstance.getTestFields();
        assertTrue(result.isEmpty());
    }

    @Test
    public void testFieldsAreCached() {
        testInstance = new TestInstance(testClass, testObject, null);

        List<TestField> result1 = testInstance.getTestFields();
        List<TestField> result2 = testInstance.getTestFields();
        assertSame(result1, result2);
    }


    private static class SuperClass {

        private String field1;
        private String field2;
    }

    private static class MyClass extends SuperClass {

        private static String staticField;

        private String field1;
        private String field3;
    }

    private static class NoFieldsClass {
    }
}
