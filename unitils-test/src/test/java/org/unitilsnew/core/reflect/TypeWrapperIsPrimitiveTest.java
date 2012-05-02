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

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class TypeWrapperIsPrimitiveTest {

    /* Tested object */
    private TypeWrapper typeWrapper;

    private Type genericType;


    @Before
    public void initialize() throws Exception {
        genericType = MyClass.class.getDeclaredField("genericType").getGenericType();
    }

    @Test
    public void primitive() {
        typeWrapper = new TypeWrapper(Integer.TYPE);

        boolean result = typeWrapper.isPrimitive();
        assertTrue(result);
    }

    @Test
    public void falseWhenNotPrimitive() {
        typeWrapper = new TypeWrapper(StringBuilder.class);

        boolean result = typeWrapper.isPrimitive();
        assertFalse(result);
    }

    @Test
    public void falseWhenNotClassType() {
        typeWrapper = new TypeWrapper(genericType);

        boolean result = typeWrapper.isPrimitive();
        assertFalse(result);
    }


    private static class MyClass {

        private List<String> genericType;
    }
}
