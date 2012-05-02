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
public class TypeWrapperIsParameterizedTypeTest {

    /* Tested object */
    private TypeWrapper typeWrapper;

    private Type classType;
    private Type parametrizedType;


    @Before
    public void initialize() throws Exception {
        classType = StringBuilder.class;
        parametrizedType = MyClass.class.getDeclaredField("parametrizedField").getGenericType();
    }


    @Test
    public void parametrizedType() {
        typeWrapper = new TypeWrapper(parametrizedType);

        boolean result = typeWrapper.isParameterizedType();
        assertTrue(result);
    }

    @Test
    public void falseWhenClassType() {
        typeWrapper = new TypeWrapper(classType);

        boolean result = typeWrapper.isParameterizedType();
        assertFalse(result);
    }


    private static class MyClass {

        private List<String> parametrizedField;
    }
}
