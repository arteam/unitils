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

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class TestClassGetFieldsTest {

    /* Tested object */
    private TestClass testClass;

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
    }


    @Test
    public void fields() {
        testClass = new TestClass(MyClass.class);

        List<Field> result = testClass.getFields();
        assertEquals(asList(field1b, field3, field1a, field2), result);
    }

    @Test
    public void emptyWhenNoFields() {
        testClass = new TestClass(NoFieldsClass.class);

        List<Field> result = testClass.getFields();
        assertTrue(result.isEmpty());
    }

    @Test
    public void testFieldsAreCached() {
        testClass = new TestClass(MyClass.class);

        List<Field> result1 = testClass.getFields();
        List<Field> result2 = testClass.getFields();
        assertSame(result1, result2);
    }


    private static class SuperClass {

        private String field1;
        private String field2;
    }

    private static class MyClass extends SuperClass {

        private String field1;
        private String field3;
    }

    private static class NoFieldsClass {
    }
}
