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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class TypeWrapperHashCodeTest {


    @Test
    public void hashCodeForClass() {
        TypeWrapper typeWrapper = new TypeWrapper(StringBuffer.class);
        int result = typeWrapper.hashCode();

        assertTrue(result != 0);
    }

    @Test
    public void sameHashCodeWhenEqual() {
        TypeWrapper typeWrapper1 = new TypeWrapper(StringBuffer.class);
        TypeWrapper typeWrapper2 = new TypeWrapper(StringBuffer.class);

        assertEquals(typeWrapper1.hashCode(), typeWrapper2.hashCode());
    }

    @Test
    public void nullType() {
        TypeWrapper typeWrapper = new TypeWrapper(null);
        int result = typeWrapper.hashCode();

        assertEquals(0, result);
    }
}
