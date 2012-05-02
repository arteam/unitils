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
public class ClassWrapperEqualsTest {


    @Test
    public void equal() {
        ClassWrapper classWrapper1 = new ClassWrapper(StringBuffer.class);
        ClassWrapper classWrapper2 = new ClassWrapper(StringBuffer.class);

        assertTrue(classWrapper1.equals(classWrapper2));
        assertTrue(classWrapper2.equals(classWrapper1));
    }

    @Test
    public void same() {
        ClassWrapper classWrapper = new ClassWrapper(StringBuffer.class);

        assertTrue(classWrapper.equals(classWrapper));
    }

    @Test
    public void notEqual() {
        ClassWrapper classWrapper1 = new ClassWrapper(StringBuffer.class);
        ClassWrapper classWrapper2 = new ClassWrapper(List.class);

        assertFalse(classWrapper1.equals(classWrapper2));
        assertFalse(classWrapper2.equals(classWrapper1));
    }

    @Test
    @SuppressWarnings("ObjectEqualsNull")
    public void notEqualToNull() {
        ClassWrapper classWrapper = new ClassWrapper(StringBuffer.class);

        assertFalse(classWrapper.equals(null));
    }

    @Test
    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    public void notEqualToOtherType() {
        ClassWrapper classWrapper = new ClassWrapper(StringBuffer.class);

        assertFalse(classWrapper.equals("xxx"));
    }

    @Test
    public void nullClasses() {
        ClassWrapper classWrapper1 = new ClassWrapper(null);
        ClassWrapper classWrapper2 = new ClassWrapper(null);

        assertTrue(classWrapper1.equals(classWrapper2));
    }
}
