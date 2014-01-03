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
import org.unitils.core.UnitilsException;

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class ReflectionUtilsGetGenericParameterTypeTest {


    @Test
    public void genericType() throws Exception {
        Type type = MyClass.class.getDeclaredField("field1").getGenericType();

        Type result = ReflectionUtils.getGenericParameterType(type);
        assertEquals(String.class, result);
    }

    @Test
    public void nestedGenericType() throws Exception {
        Type type = MyClass.class.getDeclaredField("field5").getGenericType();

        Type result = ReflectionUtils.getGenericParameterType(type);
        assertEquals(List.class, result);
    }

    @Test
    public void unboundType() throws Exception {
        Type type = MyClass.class.getDeclaredField("field2").getGenericType();

        Type result = ReflectionUtils.getGenericParameterType(type);
        assertTrue(result instanceof WildcardType);
    }

    @Test
    public void nullWhenNotGeneric() throws Exception {
        Type type = MyClass.class.getDeclaredField("field3").getGenericType();

        Type result = ReflectionUtils.getGenericParameterType(type);
        assertNull(result);
    }

    @Test
    public void exceptionWhenMoreThanOneGenericType() throws Exception {
        Type type = MyClass.class.getDeclaredField("field4").getGenericType();
        try {
            ReflectionUtils.getGenericParameterType(type);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to determine unique generic type for type: java.util.Map<java.lang.String, java.lang.String>. The type declares more than one generic type: java.util.Map<java.lang.String, java.lang.String>", e.getMessage());
        }
    }


    private static class MyClass {

        private List<String> field1;
        private List<?> field2;
        private String field3;
        private Map<String, String> field4;
        private List<List<String>> field5;
    }
}