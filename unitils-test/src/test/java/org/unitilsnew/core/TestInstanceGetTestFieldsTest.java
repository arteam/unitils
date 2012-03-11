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

import static org.junit.Assert.fail;

/**
 * @author Tim Ducheyne
 */
public class TestInstanceGetTestFieldsTest {

    /* Tested object */
    private TestInstance testInstance;

    private TestClass testClass;
    private TestClass subTestClass;
    private Object testObject;
    private Object subTestObject;
    private Field field1;
    private Field field2;
    private Field subClassField1;
    private Field subClassField3;


    @Before
    public void initialize() throws Exception {
        field1 = MyClass.class.getDeclaredField("field1");
        field2 = MyClass.class.getDeclaredField("field2");
        subClassField1 = MySubClass.class.getDeclaredField("field1");
        subClassField3 = MySubClass.class.getDeclaredField("field3");
        testClass = new TestClass(MyClass.class);
        subTestClass = new TestClass(MySubClass.class);
        testObject = new MyClass();
        subTestObject = new MySubClass();
    }


    @Test
    public void fields() {
        testInstance = new TestInstance(testClass, testObject, null);

        List<TestField> result = testInstance.getTestFields();
        assertContainsField(field1, result);
        assertContainsField(field2, result);
    }

    @Test
    public void superClassFieldsAreAlsoReturned() {
        testInstance = new TestInstance(subTestClass, subTestObject, null);

        List<TestField> result = testInstance.getTestFields();
        assertContainsField(field1, result);
        assertContainsField(field2, result);
        assertContainsField(subClassField1, result);
        assertContainsField(subClassField3, result);
    }


    private void assertContainsField(Field field, List<TestField> testFields) {
        for (TestField testField : testFields) {
            if (field.equals(testField.getField())) {
                return;
            }
        }
        fail("Field " + field + " not found in list: " + testFields);
    }


    private static class MyClass {

        private String field1;
        private String field2;
    }

    private static class MySubClass extends MyClass {

        private String field1;
        private String field3;
    }
}
