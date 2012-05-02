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

package org.unitilsnew.core.reflect;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.WildcardType;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class TypeWrapperIsAssignableWildcardTypeTest {

    /* Tested object */
    private TypeWrapper typeWrapper;

    private WildcardType type1;
    private WildcardType type2;
    private WildcardType type3;


    @Before
    public void initialize() throws Exception {
        typeWrapper = new TypeWrapper(null);

        type1 = (WildcardType) ((ParameterizedType) MyClass.class.getDeclaredField("field1").getGenericType()).getActualTypeArguments()[0];
        type2 = (WildcardType) ((ParameterizedType) MyClass.class.getDeclaredField("field2").getGenericType()).getActualTypeArguments()[0];
        type3 = (WildcardType) ((ParameterizedType) MyClass.class.getDeclaredField("field3").getGenericType()).getActualTypeArguments()[0];
    }


    @Test
    public void noBounds() {
        boolean result = typeWrapper.isAssignableWildcardType(type3, Type1.class);
        assertTrue(result);
    }

    @Test
    public void matchingUpperBound() {
        boolean result = typeWrapper.isAssignableWildcardType(type1, Type3.class);
        assertTrue(result);
    }

    @Test
    public void notMatchingUpperBound() {
        boolean result = typeWrapper.isAssignableWildcardType(type1, Type1.class);
        assertFalse(result);
    }

    @Test
    public void matchingLowerBound() {
        boolean result = typeWrapper.isAssignableWildcardType(type2, Type1.class);
        assertTrue(result);
    }

    @Test
    public void notMatchingLowerBound() {
        boolean result = typeWrapper.isAssignableWildcardType(type2, Type3.class);
        assertFalse(result);
    }


    private static class MyClass {

        private List<? extends Type2> field1;
        private List<? super Type2> field2;
        private List<?> field3;
    }


    private static class Type1 {
    }

    private static class Type2 extends Type1 {
    }

    private static class Type3 extends Type2 {
    }
}
