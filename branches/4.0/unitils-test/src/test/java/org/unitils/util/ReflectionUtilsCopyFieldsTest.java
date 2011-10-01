/*
 * Copyright Unitils.org
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

import java.util.Collection;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ReflectionUtilsCopyFieldsTest {


    @Test
    public void copyFields() {
        TestClass fromObject = new TestClass(5, "value", asList("1", "2"));
        TestClass toObject = new TestClass();

        ReflectionUtils.copyFields(fromObject, toObject);
        assertEquals(5, toObject.getField1());
        assertEquals("value", toObject.getField2());
        assertEquals(asList("1", "2"), toObject.getField3());
    }

    @Test
    public void copyFieldsSubClass() {
        SubClass fromObject = new SubClass(5, "value", asList("1", "2"), "value with same name", "sub value");
        SubClass toObject = new SubClass();

        ReflectionUtils.copyFields(fromObject, toObject);
        assertEquals(5, toObject.getField1());
        assertEquals("value", ((TestClass) toObject).field2);
        assertEquals("value with same name", toObject.field2);
        assertEquals(asList("1", "2"), toObject.getField3());
        assertEquals("sub value", toObject.getField4());
    }

    @Test
    public void notAllFields() {
        TestClass fromObject = new TestClass(5, "value", asList("1", "2"));
        SubClass toObject = new SubClass();

        ReflectionUtils.copyFields(fromObject, toObject);
        assertEquals(5, toObject.getField1());
        assertEquals("value", ((TestClass) toObject).field2);
        assertNull(toObject.field2);
        assertEquals(asList("1", "2"), toObject.getField3());
        assertNull(toObject.getField4());
    }


    private static class TestClass {

        private int field1;
        protected String field2;
        private Collection<String> field3;

        private TestClass() {
        }

        private TestClass(int field1, String field2, Collection<String> field3) {
            this.field1 = field1;
            this.field2 = field2;
            this.field3 = field3;
        }

        public int getField1() {
            return field1;
        }

        public String getField2() {
            return field2;
        }

        public Collection<String> getField3() {
            return field3;
        }
    }

    private static class SubClass extends TestClass {

        protected String field2;
        private String field4;

        private SubClass() {
        }

        private SubClass(int field1, String superField2, Collection<String> field3, String field2, String field4) {
            super(field1, superField2, field3);
            this.field2 = field2;
            this.field4 = field4;
        }

        public String getField2() {
            return field2;
        }

        public String getField4() {
            return field4;
        }
    }
}
