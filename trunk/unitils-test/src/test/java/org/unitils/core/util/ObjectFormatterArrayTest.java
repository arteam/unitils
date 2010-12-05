/*
 * Copyright Unitils.org
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
import org.unitils.UnitilsJUnit4;

import static org.junit.Assert.assertEquals;


/**
 * Tests the formatting of proxies and mocks.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ObjectFormatterArrayTest extends UnitilsJUnit4 {

    private ObjectFormatter objectFormatter = new ObjectFormatter(2, 3);


    @Test
    public void byteArray() {
        byte[] array = new byte[]{1, 2, 3};
        String result = objectFormatter.format(array);
        assertEquals("[1, 2, 3]", result);
    }

    @Test
    public void byteArrayMaxElements() {
        byte[] array = new byte[]{1, 2, 3, 4};
        String result = objectFormatter.format(array);
        assertEquals("[1, 2, 3, ...]", result);
    }

    @Test
    public void emptyByteArray() {
        byte[] array = new byte[0];
        String result = objectFormatter.format(array);
        assertEquals("[]", result);
    }


    @Test
    public void shortArray() {
        short[] array = new short[]{1, 2, 3};
        String result = objectFormatter.format(array);
        assertEquals("[1, 2, 3]", result);
    }

    @Test
    public void shortArrayMaxElements() {
        short[] array = new short[]{1, 2, 3, 4};
        String result = objectFormatter.format(array);
        assertEquals("[1, 2, 3, ...]", result);
    }

    @Test
    public void emptyShortArray() {
        short[] array = new short[0];
        String result = objectFormatter.format(array);
        assertEquals("[]", result);
    }


    @Test
    public void intArray() {
        int[] array = new int[]{1, 2, 3};
        String result = objectFormatter.format(array);
        assertEquals("[1, 2, 3]", result);
    }

    @Test
    public void intArrayMaxElements() {
        int[] array = new int[]{1, 2, 3, 4};
        String result = objectFormatter.format(array);
        assertEquals("[1, 2, 3, ...]", result);
    }

    @Test
    public void emptyIntArray() {
        int[] array = new int[0];
        String result = objectFormatter.format(array);
        assertEquals("[]", result);
    }


    @Test
    public void longArray() {
        long[] array = new long[]{1, 2, 3};
        String result = objectFormatter.format(array);
        assertEquals("[1, 2, 3]", result);
    }

    @Test
    public void longArrayMaxElements() {
        long[] array = new long[]{1, 2, 3, 4};
        String result = objectFormatter.format(array);
        assertEquals("[1, 2, 3, ...]", result);
    }

    @Test
    public void emptyLongArray() {
        long[] array = new long[0];
        String result = objectFormatter.format(array);
        assertEquals("[]", result);
    }


    @Test
    public void charArray() {
        char[] array = new char[]{0, ' ', 't'};
        String result = objectFormatter.format(array);
        assertEquals("['\u0000', ' ', 't']", result);
    }

    @Test
    public void charArrayMaxElements() {
        char[] array = new char[]{'1', '2', '3', '4'};
        String result = objectFormatter.format(array);
        assertEquals("['1', '2', '3', ...]", result);
    }

    @Test
    public void emptyCharArray() {
        char[] array = new char[0];
        String result = objectFormatter.format(array);
        assertEquals("[]", result);
    }


    @Test
    public void floatArray() {
        float[] array = new float[]{1.1F, 2.2F, 3.3F};
        String result = objectFormatter.format(array);
        assertEquals("[1.1, 2.2, 3.3]", result);
    }

    @Test
    public void floatArrayMaxElements() {
        float[] array = new float[]{1.1F, 2.2F, 3.3F, 4.4F};
        String result = objectFormatter.format(array);
        assertEquals("[1.1, 2.2, 3.3, ...]", result);
    }

    @Test
    public void emptyFloatArray() {
        float[] array = new float[0];
        String result = objectFormatter.format(array);
        assertEquals("[]", result);
    }


    @Test
    public void doubleArray() {
        double[] array = new double[]{1.1, 2.2, 3.3};
        String result = objectFormatter.format(array);
        assertEquals("[1.1, 2.2, 3.3]", result);
    }

    @Test
    public void doubleArrayMaxElements() {
        double[] array = new double[]{1.1, 2.2, 3.3, 4.4};
        String result = objectFormatter.format(array);
        assertEquals("[1.1, 2.2, 3.3, ...]", result);
    }

    @Test
    public void emptyDoubleArray() {
        double[] array = new double[0];
        String result = objectFormatter.format(array);
        assertEquals("[]", result);
    }


    @Test
    public void booleanArray() {
        boolean[] array = new boolean[]{true, false, true};
        String result = objectFormatter.format(array);
        assertEquals("[true, false, true]", result);
    }

    @Test
    public void booleanArrayMaxElements() {
        boolean[] array = new boolean[]{true, false, true, false};
        String result = objectFormatter.format(array);
        assertEquals("[true, false, true, ...]", result);
    }

    @Test
    public void emptyBooleanArray() {
        boolean[] array = new boolean[0];
        String result = objectFormatter.format(array);
        assertEquals("[]", result);
    }


    @Test
    public void objectArray() {
        String[] array = new String[]{null, "", "test"};
        String result = objectFormatter.format(array);
        assertEquals("[null, \"\", \"test\"]", result);
    }

    @Test
    public void objectArrayMaxElements() {
        String[] array = new String[]{"1", "2", "3", "4"};
        String result = objectFormatter.format(array);
        assertEquals("[\"1\", \"2\", \"3\", ...]", result);
    }

    @Test
    public void emptyObjectArray() {
        Object[] array = new Object[0];
        String result = objectFormatter.format(array);
        assertEquals("[]", result);
    }
}