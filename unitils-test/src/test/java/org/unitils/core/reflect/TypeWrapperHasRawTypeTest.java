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

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class TypeWrapperHasRawTypeTest {

    /* Tested object */
    private TypeWrapper typeWrapper;

    private Type parametrizedType;


    @Before
    public void initialize() throws Exception {
        parametrizedType = MyClass.class.getDeclaredField("parametrizedField").getGenericType();
    }


    @Test
    public void trueWhenSameClassType() {
        typeWrapper = new TypeWrapper(MyClass.class);

        boolean result = typeWrapper.hasRawType(MyClass.class);
        assertTrue(result);
    }

    @Test
    public void falseWhenDifferentClassType() {
        typeWrapper = new TypeWrapper(MyClass.class);

        boolean result = typeWrapper.hasRawType(Map.class);
        assertFalse(result);
    }

    @Test
    public void trueWhenSameRawType() {
        typeWrapper = new TypeWrapper(parametrizedType);

        boolean result = typeWrapper.hasRawType(List.class);
        assertTrue(result);
    }

    @Test
    public void falseWhenDifferentRawType() {
        typeWrapper = new TypeWrapper(parametrizedType);

        boolean result = typeWrapper.hasRawType(List.class);
        assertTrue(result);
    }


    private static class MyClass {

        private List<String> parametrizedField;
    }
}
