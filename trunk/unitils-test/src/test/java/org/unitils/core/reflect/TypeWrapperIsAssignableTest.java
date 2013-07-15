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
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class TypeWrapperIsAssignableTest {

    /* Tested object */
    private TypeWrapper typeWrapper;

    private Type genericType1;
    private Type genericType2;
    private Type wildCardType;
    private Type genericType3;


    @Before
    public void initialize() throws Exception {
        typeWrapper = new TypeWrapper(null);

        genericType1 = MyClass.class.getDeclaredField("field1").getGenericType();
        genericType2 = MyClass.class.getDeclaredField("field2").getGenericType();
        wildCardType = ((ParameterizedType) MyClass.class.getDeclaredField("field3").getGenericType()).getActualTypeArguments()[0];
        genericType3 = MyClass.class.getDeclaredField("field4").getGenericType();
    }


    @Test
    public void equal() {
        boolean result = typeWrapper.isAssignable(Type1.class, Type1.class);
        assertTrue(result);
    }

    @Test
    public void objectType() {
        boolean result = typeWrapper.isAssignable(Object.class, Type1.class);
        assertTrue(result);
    }

    @Test
    public void subClass() {
        boolean result = typeWrapper.isAssignable(Type1.class, Type2.class);
        assertTrue(result);
    }

    @Test
    public void falseWhenSuperClass() {
        boolean result = typeWrapper.isAssignable(Type2.class, Type1.class);
        assertFalse(result);
    }

    @Test
    public void assignableGenericType() {
        boolean result = typeWrapper.isAssignable(genericType1, genericType2);
        assertTrue(result);
    }

    @Test
    public void falseWhenNotAssignableGenericType() {
        boolean result = typeWrapper.isAssignable(genericType1, genericType3);
        assertFalse(result);
    }

    @Test
    public void assignableToWildCardType() {
        boolean result = typeWrapper.isAssignable(wildCardType, Type1.class);
        assertTrue(result);
    }

    @Test
    public void falseWhenNotAssignableToWildCardType() {
        boolean result = typeWrapper.isAssignable(wildCardType, genericType1);
        assertFalse(result);
    }

    @Test
    public void falseWhenNullFromType() {
        boolean result = typeWrapper.isAssignable(null, Type1.class);
        assertFalse(result);
    }

    @Test
    public void falseWhenNullToType() {
        boolean result = typeWrapper.isAssignable(Type1.class, null);
        assertFalse(result);
    }


    private static class MyClass {

        private Map<Type1, Type1> field1;
        private Map<Type1, Type1> field2;
        private List<? extends Type1> field3;
        private List<Type1> field4;
    }


    private static class Type1 {
    }

    private static class Type2 extends Type1 {
    }
}
