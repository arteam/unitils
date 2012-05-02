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

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class TypeWrapperEqualsTest {


    @Test
    public void equal() {
        TypeWrapper typeWrapper1 = new TypeWrapper(StringBuffer.class);
        TypeWrapper typeWrapper2 = new TypeWrapper(StringBuffer.class);

        assertTrue(typeWrapper1.equals(typeWrapper2));
        assertTrue(typeWrapper2.equals(typeWrapper1));
    }

    @Test
    public void same() {
        TypeWrapper typeWrapper = new TypeWrapper(StringBuffer.class);

        assertTrue(typeWrapper.equals(typeWrapper));
    }

    @Test
    public void notEqual() {
        TypeWrapper typeWrapper1 = new TypeWrapper(StringBuffer.class);
        TypeWrapper typeWrapper2 = new TypeWrapper(List.class);

        assertFalse(typeWrapper1.equals(typeWrapper2));
        assertFalse(typeWrapper2.equals(typeWrapper1));
    }

    @Test
    @SuppressWarnings("ObjectEqualsNull")
    public void notEqualToNull() {
        TypeWrapper typeWrapper = new TypeWrapper(StringBuffer.class);

        assertFalse(typeWrapper.equals(null));
    }

    @Test
    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    public void notEqualToOtherType() {
        TypeWrapper typeWrapper = new TypeWrapper(StringBuffer.class);

        assertFalse(typeWrapper.equals("xxx"));
    }

    @Test
    public void nullTypes() {
        TypeWrapper typeWrapper1 = new TypeWrapper(null);
        TypeWrapper typeWrapper2 = new TypeWrapper(null);

        assertTrue(typeWrapper1.equals(typeWrapper2));
    }
}
