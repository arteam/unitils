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

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Tim Ducheyne
 */
public class ReflectionUtilsGetGenericParameterClassTest {


    @Test
    public void genericParameterClass() throws Exception {
        Type type = MyClass.class.getDeclaredField("field1").getGenericType();

        Class<?> result = ReflectionUtils.getGenericParameterClass(type);
        assertEquals(String.class, result);
    }

    @Test
    public void nullWhenUnboundType() throws Exception {
        Type type = MyClass.class.getDeclaredField("field2").getGenericType();

        Class<?> result = ReflectionUtils.getGenericParameterClass(type);
        assertNull(result);
    }

    @Test
    public void nullWhenNotGeneric() throws Exception {
        Type type = MyClass.class.getDeclaredField("field3").getGenericType();

        Class<?> result = ReflectionUtils.getGenericParameterClass(type);
        assertNull(result);
    }


    private static class MyClass {

        private List<String> field1;
        private List<?> field2;
        private String field3;
    }
}