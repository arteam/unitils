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

package org.unitils.core.reflect;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;
import static org.unitils.reflectionassert.ReflectionAssert.assertPropertyReflectionEquals;

/**
 * @author Tim Ducheyne
 */
public class ClassWrapperGetFieldsTest {

    /* Tested object */
    private ClassWrapper classWrapper;

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
        classWrapper = new ClassWrapper(MyClass.class);

        List<FieldWrapper> result = classWrapper.getFields();
        assertPropertyReflectionEquals("wrappedField", asList(field1b, field3, field1a, field2), result);
    }

    @Test
    public void emptyWhenNoFields() {
        classWrapper = new ClassWrapper(NoFieldsClass.class);

        List<FieldWrapper> result = classWrapper.getFields();
        assertTrue(result.isEmpty());
    }


    @Test
    public void namedFields() {
        classWrapper = new ClassWrapper(MyClass.class);

        List<FieldWrapper> result = classWrapper.getFields(asList("field1", "field2"));
        assertPropertyReflectionEquals("wrappedField", asList(field1b, field2), result);
    }

    @Test
    public void emptyWhenEmptyNames() {
        classWrapper = new ClassWrapper(MyClass.class);

        List<FieldWrapper> result = classWrapper.getFields(Collections.<String>emptyList());
        assertTrue(result.isEmpty());
    }


    private static class SuperClass {

        private String field1;
        private String field2;
    }

    private static class MyClass extends SuperClass {

        private static String staticFieldIsIgnored;

        private String field1;
        private String field3;
    }

    private static class NoFieldsClass {
    }
}
