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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class ClassWrapperHashCodeTest {


    @Test
    public void hashCodeForClass() {
        ClassWrapper classWrapper = new ClassWrapper(StringBuffer.class);
        int result = classWrapper.hashCode();

        assertTrue(result != 0);
    }

    @Test
    public void sameHashCodeWhenEqual() {
        ClassWrapper classWrapper1 = new ClassWrapper(StringBuffer.class);
        ClassWrapper classWrapper2 = new ClassWrapper(StringBuffer.class);

        assertEquals(classWrapper1.hashCode(), classWrapper2.hashCode());
    }

    @Test
    public void nullClass() {
        ClassWrapper classWrapper = new ClassWrapper(null);
        int result = classWrapper.hashCode();

        assertEquals(0, result);
    }
}
