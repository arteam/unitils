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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 */
public class TypeWrapperTest {

    /* Tested object */
    private TypeWrapper typeWrapper;


    @Before
    public void initialize() throws Exception {
        typeWrapper = new TypeWrapper(StringBuilder.class);
    }


    @Test
    public void getWrappedType() {
        Type result = typeWrapper.getWrappedType();
        assertSame(StringBuilder.class, result);
    }

    @Test
    public void nameAsToString() {
        String result = typeWrapper.toString();
        assertEquals("java.lang.StringBuilder", result);
    }

}
