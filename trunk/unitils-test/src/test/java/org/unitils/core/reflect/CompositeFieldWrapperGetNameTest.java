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

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

/**
 * @author Tim Ducheyne
 */
public class CompositeFieldWrapperGetNameTest {

    /* Tested object */
    private CompositeFieldWrapper compositeFieldWrapper;

    private Field field1;
    private Field field2;
    private Field field3;


    @Before
    public void initialize() throws Exception {
        field1 = MyClass.class.getDeclaredField("field1");
        field2 = MyClass.class.getDeclaredField("field2");
        field3 = MyClass.class.getDeclaredField("field3");
    }


    @Test
    public void getName() throws Exception {
        compositeFieldWrapper = new CompositeFieldWrapper(asList(field1, field2, field3));

        String result = compositeFieldWrapper.getName();
        assertEquals("field1.field2.field3", result);
    }

    @Test
    public void singleField() throws Exception {
        compositeFieldWrapper = new CompositeFieldWrapper(asList(field1));

        String result = compositeFieldWrapper.getName();
        assertEquals("field1", result);
    }

    @Test
    public void emptyWhenNoFields() throws Exception {
        compositeFieldWrapper = new CompositeFieldWrapper(Collections.<Field>emptyList());

        String result = compositeFieldWrapper.getName();
        assertEquals("", result);
    }


    private static class MyClass {

        private String field1;
        private String field2;
        private String field3;
    }
}
