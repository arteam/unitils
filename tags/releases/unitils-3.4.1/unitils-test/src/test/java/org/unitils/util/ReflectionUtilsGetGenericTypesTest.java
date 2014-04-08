/*
 * Copyright 2008,  Unitils.org
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

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.unitils.core.UnitilsException;
import static org.unitils.util.ReflectionUtils.getFieldWithName;
import static org.unitils.util.ReflectionUtils.getGenericType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Test for the get generic types method of the reflection utils
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ReflectionUtilsGetGenericTypesTest {


    @Test
    public void generic() throws Exception {
        Type result = getGenericType(getFieldWithName(TestClass.class, "genericField", false));
        assertEquals(String.class, result);
    }


    @Test(expected = UnitilsException.class)
    public void notGeneric() throws Exception {
        getGenericType(getFieldWithName(TestClass.class, "notGenericField", false));
    }


    @Test
    public void nestedGeneric() throws Exception {
        Type result = getGenericType(getFieldWithName(TestClass.class, "nestedGenericField", false));
        assertEquals(Map.class, ((ParameterizedType) result).getRawType());
    }


    @Test(expected = UnitilsException.class)
    public void moreThanOneGenericType() throws Exception {
        getGenericType(getFieldWithName(TestClass.class, "multipleGenericField", false));
    }


    private static class TestClass {

        private Object notGenericField;

        private Class<String> genericField;

        private List<Map<String, List<String>>> nestedGenericField;

        private Map<String, String> multipleGenericField;
    }
}