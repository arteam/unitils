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

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class TypeWrapperIsAssignableParameterizedTypeTest {

    /* Tested object */
    private TypeWrapper typeWrapper;

    private ParameterizedType type1;
    private ParameterizedType type2;
    private ParameterizedType type3;
    private ParameterizedType type4;
    private ParameterizedType type5;
    private ParameterizedType type6;
    private ParameterizedType type7;
    private ParameterizedType type8;


    @Before
    public void initialize() throws Exception {
        typeWrapper = new TypeWrapper(null);

        type1 = (ParameterizedType) MyClass.class.getDeclaredField("field1").getGenericType();
        type2 = (ParameterizedType) MyClass.class.getDeclaredField("field2").getGenericType();
        type3 = (ParameterizedType) MyClass.class.getDeclaredField("field3").getGenericType();
        type4 = (ParameterizedType) MyClass.class.getDeclaredField("field4").getGenericType();
        type5 = (ParameterizedType) MyClass.class.getDeclaredField("field5").getGenericType();
        type6 = (ParameterizedType) MyClass.class.getDeclaredField("field6").getGenericType();
        type7 = (ParameterizedType) MyClass.class.getDeclaredField("field7").getGenericType();
        type8 = (ParameterizedType) MyClass.class.getDeclaredField("field8").getGenericType();
    }


    @Test
    public void equal() {
        boolean result = typeWrapper.isAssignableParameterizedType(type1, type2);
        assertTrue(result);
    }

    @Test
    public void assignableParameter() {
        boolean result = typeWrapper.isAssignableParameterizedType(type6, type7);
        assertTrue(result);
    }

    @Test
    public void nestedAssignableParameters() {
        boolean result = typeWrapper.isAssignableParameterizedType(type3, type4);
        assertTrue(result);
    }

    @Test
    public void falseWhenDifferentNrOfTypeParameters() {
        boolean result = typeWrapper.isAssignableParameterizedType(type2, type7);
        assertFalse(result);
    }

    @Test
    public void falseWhenNotEqualNonWildCardType() {
        boolean result = typeWrapper.isAssignableParameterizedType(type7, type8);
        assertFalse(result);
    }

    @Test
    public void falseWhenNotAssignableToWildCard() {
        boolean result = typeWrapper.isAssignableParameterizedType(type6, type8);
        assertFalse(result);
    }

    @Test
    public void falseWhenNotAssignableToNestedWildCard() {
        boolean result = typeWrapper.isAssignableParameterizedType(type3, type5);
        assertFalse(result);
    }


    private static class MyClass {

        private Map<Type1, Type1> field1;
        private Map<Type1, Type1> field2;

        private Map<? extends List<? extends Type1>, ? extends List<?>> field3;
        private Map<List<Type1>, List<Type2>> field4;
        private Map<List<String>, List<String>> field5;

        private List<? extends Type1> field6;
        private List<Type2> field7;
        private List<String> field8;
    }


    private static class Type1 {
    }

    private static class Type2 extends Type1 {
    }
}
