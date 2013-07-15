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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class TypeWrapperIsAssignableClassTest {

    /* Tested object */
    private TypeWrapper typeWrapper = new TypeWrapper(null);


    @Test
    public void booleanLeftAutoBoxing() {
        boolean result = typeWrapper.isAssignableClass(Boolean.TYPE, Boolean.class);
        assertTrue(result);
    }

    @Test
    public void booleanRightAutoBoxing() {
        boolean result = typeWrapper.isAssignableClass(Boolean.class, Boolean.TYPE);
        assertTrue(result);
    }

    @Test
    public void charLeftAutoBoxing() {
        boolean result = typeWrapper.isAssignableClass(Character.TYPE, Character.class);
        assertTrue(result);
    }

    @Test
    public void charRightAutoBoxing() {
        boolean result = typeWrapper.isAssignableClass(Character.class, Character.TYPE);
        assertTrue(result);
    }

    @Test
    public void intLeftAutoBoxing() {
        boolean result = typeWrapper.isAssignableClass(Integer.TYPE, Integer.class);
        assertTrue(result);
    }

    @Test
    public void intRightAutoBoxing() {
        boolean result = typeWrapper.isAssignableClass(Integer.class, Integer.TYPE);
        assertTrue(result);
    }

    @Test
    public void longLeftAutoBoxing() {
        boolean result = typeWrapper.isAssignableClass(Long.TYPE, Long.class);
        assertTrue(result);
    }

    @Test
    public void longRightAutoBoxing() {
        boolean result = typeWrapper.isAssignableClass(Long.class, Long.TYPE);
        assertTrue(result);
    }

    @Test
    public void floatLeftAutoBoxing() {
        boolean result = typeWrapper.isAssignableClass(Float.TYPE, Float.class);
        assertTrue(result);
    }

    @Test
    public void floatRightAutoBoxing() {
        boolean result = typeWrapper.isAssignableClass(Float.class, Float.TYPE);
        assertTrue(result);
    }

    @Test
    public void doubleLeftAutoBoxing() {
        boolean result = typeWrapper.isAssignableClass(Double.TYPE, Double.class);
        assertTrue(result);
    }

    @Test
    public void doubleRightAutoBoxing() {
        boolean result = typeWrapper.isAssignableClass(Double.class, Double.TYPE);
        assertTrue(result);
    }

    @Test
    public void subClass() {
        boolean result = typeWrapper.isAssignableClass(Type1.class, Type2.class);
        assertTrue(result);
    }

    @Test
    public void falseWhenSuperClass() {
        boolean result = typeWrapper.isAssignableClass(Type2.class, Type1.class);
        assertFalse(result);
    }

    @Test
    public void falseWhenOtherClass() {
        boolean result = typeWrapper.isAssignableClass(Type1.class, String.class);
        assertFalse(result);
    }


    private static class Type1 {
    }

    private static class Type2 extends Type1 {
    }
}
