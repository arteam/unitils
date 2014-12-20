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

import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class CollectionUtilsConvertToObjectArrayTest {

    @Test
    public void booleanArray() {
        Object[] result = CollectionUtils.convertToObjectArray(new boolean[]{true, false});
        assertArrayEquals(new Boolean[]{true, false}, result);
    }

    @Test
    public void byteArray() {
        Object[] result = CollectionUtils.convertToObjectArray(new byte[]{1, 2});
        assertArrayEquals(new Byte[]{1, 2}, result);
    }

    @Test
    public void shortArray() {
        Object[] result = CollectionUtils.convertToObjectArray(new short[]{1, 2});
        assertArrayEquals(new Short[]{1, 2}, result);
    }

    @Test
    public void charArray() {
        Object[] result = CollectionUtils.convertToObjectArray(new char[]{'a', 'b'});
        assertArrayEquals(new Character[]{'a', 'b'}, result);
    }

    @Test
    public void intArray() {
        Object[] result = CollectionUtils.convertToObjectArray(new int[]{1, 2});
        assertArrayEquals(new Integer[]{1, 2}, result);
    }

    @Test
    public void longArray() {
        Object[] result = CollectionUtils.convertToObjectArray(new long[]{1, 2});
        assertArrayEquals(new Long[]{1L, 2L}, result);
    }

    @Test
    public void floatArray() {
        Object[] result = CollectionUtils.convertToObjectArray(new float[]{1, 2});
        assertArrayEquals(new Float[]{1f, 2f}, result);
    }

    @Test
    public void doubleArray() {
        Object[] result = CollectionUtils.convertToObjectArray(new double[]{1, 2});
        assertArrayEquals(new Double[]{1d, 2d}, result);
    }

    @Test
    public void objectArray() {
        Object[] result = CollectionUtils.convertToObjectArray(new String[]{"1", "2"});
        assertArrayEquals(new String[]{"1", "2"}, result);
    }

    @Test
    public void emptyArray() {
        Object[] result = CollectionUtils.convertToObjectArray(new int[0]);
        assertArrayEquals(new Integer[0], result);
    }

    @Test
    public void classCastExceptionWhenNotAnArray() {
        try {
            CollectionUtils.convertToObjectArray("1");
            fail("ClassCastException expected");
        } catch (ClassCastException e) {
            assertEquals("java.lang.String cannot be cast to [Ljava.lang.Object;", e.getMessage());
        }
    }

    @Test
    public void nullWhenNull() {
        Object[] result = CollectionUtils.convertToObjectArray(null);
        assertNull(result);
    }
}
