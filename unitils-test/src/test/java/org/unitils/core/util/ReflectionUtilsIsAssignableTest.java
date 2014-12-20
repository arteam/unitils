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
package org.unitils.core.util;

import org.junit.Test;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class ReflectionUtilsIsAssignableTest {

    @Test
    public void booleanLeft() {
        boolean result = ReflectionUtils.isAssignable(boolean.class, Boolean.class);
        assertTrue(result);
    }

    @Test
    public void booleanRight() {
        boolean result = ReflectionUtils.isAssignable(Boolean.class, boolean.class);
        assertTrue(result);
    }

    @Test
    public void byteLeft() {
        boolean result = ReflectionUtils.isAssignable(byte.class, Byte.class);
        assertTrue(result);
    }

    @Test
    public void byteRight() {
        boolean result = ReflectionUtils.isAssignable(Byte.class, byte.class);
        assertTrue(result);
    }

    @Test
    public void charLeft() {
        boolean result = ReflectionUtils.isAssignable(char.class, Character.class);
        assertTrue(result);
    }

    @Test
    public void charRight() {
        boolean result = ReflectionUtils.isAssignable(Character.class, char.class);
        assertTrue(result);
    }

    @Test
    public void intLeft() {
        boolean result = ReflectionUtils.isAssignable(int.class, Integer.class);
        assertTrue(result);
    }

    @Test
    public void intRight() {
        boolean result = ReflectionUtils.isAssignable(Integer.class, int.class);
        assertTrue(result);
    }

    @Test
    public void longLeft() {
        boolean result = ReflectionUtils.isAssignable(long.class, Long.class);
        assertTrue(result);
    }

    @Test
    public void longRight() {
        boolean result = ReflectionUtils.isAssignable(Long.class, long.class);
        assertTrue(result);
    }

    @Test
    public void floatLeft() {
        boolean result = ReflectionUtils.isAssignable(float.class, Float.class);
        assertTrue(result);
    }


    @Test
    public void floatRight() {
        boolean result = ReflectionUtils.isAssignable(Float.class, Float.class);
        assertTrue(result);
    }

    @Test
    public void doubleLeft() {
        boolean result = ReflectionUtils.isAssignable(Double.class, double.class);
        assertTrue(result);
    }

    @Test
    public void doubleRight() {
        boolean result = ReflectionUtils.isAssignable(double.class, Double.class);
        assertTrue(result);
    }

    @Test
    public void assignableType() throws Exception {
        Type type1 = MyClassB.class.getDeclaredField("field1").getGenericType();
        Type type2 = MyClassB.class.getDeclaredField("field2").getGenericType();
        boolean result = ReflectionUtils.isAssignable(type1, type2);
        assertTrue(result);
    }

    @Test
    public void notAssignableType() throws Exception {
        Type type1 = MyClassB.class.getDeclaredField("field1").getGenericType();
        Type type2 = MyClassB.class.getDeclaredField("field2").getGenericType();
        boolean result = ReflectionUtils.isAssignable(type2, type1);
        assertFalse(result);
    }


    private static class MyClassB {

        private List<String> field1;
        private List<?> field2;
    }
}