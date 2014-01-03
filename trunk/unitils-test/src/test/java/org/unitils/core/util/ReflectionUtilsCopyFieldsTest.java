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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Tim Ducheyne
 */
public class ReflectionUtilsCopyFieldsTest {


    @Test
    public void copyFields() throws IllegalAccessException {
        TestClass testClass1 = new TestClass("a", 1, "b");
        TestClass testClass2 = new TestClass("c", 2, "d");

        ReflectionUtils.copyFields(testClass1, testClass2);
        assertEquals("a", testClass2.field1);
        assertEquals(1, testClass2.field2);
        assertEquals("b", testClass2.field3);
    }


    private static class SuperClass {

        protected static int staticField = 55;
        protected String field1;

        private SuperClass(String field1) {
            this.field1 = field1;
        }
    }

    private class TestClass extends SuperClass {

        private int field2;
        private String field3;

        private TestClass(String field1, int field2, String field3) {
            super(field1);
            this.field2 = field2;
            this.field3 = field3;
        }
    }
}