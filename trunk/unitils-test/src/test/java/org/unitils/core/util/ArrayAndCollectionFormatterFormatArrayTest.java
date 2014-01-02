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

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;
import org.unitils.mock.core.proxy.ProxyInvocation;
import org.unitils.mock.mockbehavior.MockBehavior;

import static org.junit.Assert.assertEquals;

/**
 * @author Tim Ducheyne
 */
public class ArrayAndCollectionFormatterFormatArrayTest extends UnitilsJUnit4 {

    private ArrayAndCollectionFormatter arrayAndCollectionFormatter;

    private Mock<ObjectFormatter> objectFormatterMock;
    @Dummy
    private MyObject myObject1;
    @Dummy
    private MyObject myObject2;
    private StringBuilder result;


    @Before
    public void initialize() {
        arrayAndCollectionFormatter = new ArrayAndCollectionFormatter(3, objectFormatterMock.getMock());

        result = new StringBuilder();
    }


    @Test
    public void byteArray() {
        byte[] byteArray = new byte[]{4, 5, 6};

        arrayAndCollectionFormatter.formatArray(byteArray, 0, result);
        assertEquals("[4, 5, 6]", result.toString());
    }

    @Test
    public void emptyByteArray() {
        byte[] byteArray = new byte[0];

        arrayAndCollectionFormatter.formatArray(byteArray, 0, result);
        assertEquals("[]", result.toString());
    }

    @Test
    public void truncatedByteArray() {
        byte[] byteArray = new byte[]{4, 5, 6, 7, 8};

        arrayAndCollectionFormatter.formatArray(byteArray, 0, result);
        assertEquals("[4, 5, 6, ...]", result.toString());
    }

    @Test
    public void shortArray() {
        short[] shortArray = new short[]{4, 5, 6};

        arrayAndCollectionFormatter.formatArray(shortArray, 0, result);
        assertEquals("[4, 5, 6]", result.toString());
    }

    @Test
    public void emptyShortArray() {
        short[] shortArray = new short[0];

        arrayAndCollectionFormatter.formatArray(shortArray, 0, result);
        assertEquals("[]", result.toString());
    }

    @Test
    public void truncatedShortArray() {
        short[] shortArray = new short[]{4, 5, 6, 7, 8};

        arrayAndCollectionFormatter.formatArray(shortArray, 0, result);
        assertEquals("[4, 5, 6, ...]", result.toString());
    }

    @Test
    public void intArray() {
        int[] intArray = new int[]{4, 5, 6};

        arrayAndCollectionFormatter.formatArray(intArray, 0, result);
        assertEquals("[4, 5, 6]", result.toString());
    }

    @Test
    public void emptyIntArray() {
        int[] intArray = new int[0];

        arrayAndCollectionFormatter.formatArray(intArray, 0, result);
        assertEquals("[]", result.toString());
    }

    @Test
    public void truncatedIntArray() {
        int[] intArray = new int[]{4, 5, 6, 7, 8};

        arrayAndCollectionFormatter.formatArray(intArray, 0, result);
        assertEquals("[4, 5, 6, ...]", result.toString());
    }

    @Test
    public void longArray() {
        long[] longArray = new long[]{4, 5, 6};

        arrayAndCollectionFormatter.formatArray(longArray, 0, result);
        assertEquals("[4, 5, 6]", result.toString());
    }

    @Test
    public void emptyLongArray() {
        long[] longArray = new long[0];

        arrayAndCollectionFormatter.formatArray(longArray, 0, result);
        assertEquals("[]", result.toString());
    }

    @Test
    public void truncatedLongArray() {
        long[] longArray = new long[]{4, 5, 6, 7, 8};

        arrayAndCollectionFormatter.formatArray(longArray, 0, result);
        assertEquals("[4, 5, 6, ...]", result.toString());
    }

    @Test
    public void charArray() {
        char[] charArray = new char[]{'a', 'b', 'c'};

        arrayAndCollectionFormatter.formatArray(charArray, 0, result);
        assertEquals("['a', 'b', 'c']", result.toString());
    }

    @Test
    public void emptyCharArray() {
        char[] charArray = new char[0];

        arrayAndCollectionFormatter.formatArray(charArray, 0, result);
        assertEquals("[]", result.toString());
    }

    @Test
    public void truncatedCharArray() {
        char[] charArray = new char[]{'a', 'b', 'c', 'd', 'e'};

        arrayAndCollectionFormatter.formatArray(charArray, 0, result);
        assertEquals("['a', 'b', 'c', ...]", result.toString());
    }

    @Test
    public void floatArray() {
        float[] floatArray = new float[]{4, 5.3f, 6.0f};

        arrayAndCollectionFormatter.formatArray(floatArray, 0, result);
        assertEquals("[4.0, 5.3, 6.0]", result.toString());
    }

    @Test
    public void emptyFloatArray() {
        float[] floatArray = new float[0];

        arrayAndCollectionFormatter.formatArray(floatArray, 0, result);
        assertEquals("[]", result.toString());
    }

    @Test
    public void truncatedFloatArray() {
        float[] floatArray = new float[]{4, 5.3f, 6.0f, 7, 8};

        arrayAndCollectionFormatter.formatArray(floatArray, 0, result);
        assertEquals("[4.0, 5.3, 6.0, ...]", result.toString());
    }

    @Test
    public void doubleArray() {
        double[] doubleArray = new double[]{4, 5.3d, 6.0d};

        arrayAndCollectionFormatter.formatArray(doubleArray, 0, result);
        assertEquals("[4.0, 5.3, 6.0]", result.toString());
    }

    @Test
    public void emptyDoubleArray() {
        double[] doubleArray = new double[0];

        arrayAndCollectionFormatter.formatArray(doubleArray, 0, result);
        assertEquals("[]", result.toString());
    }

    @Test
    public void truncatedDoubleArray() {
        double[] doubleArray = new double[]{4, 5.3d, 6.0d, 7, 8};

        arrayAndCollectionFormatter.formatArray(doubleArray, 0, result);
        assertEquals("[4.0, 5.3, 6.0, ...]", result.toString());
    }

    @Test
    public void booleanArray() {
        boolean[] booleanArray = new boolean[]{true, false, true};

        arrayAndCollectionFormatter.formatArray(booleanArray, 0, result);
        assertEquals("[true, false, true]", result.toString());
    }

    @Test
    public void emptyBooleanArray() {
        boolean[] booleanArray = new boolean[0];

        arrayAndCollectionFormatter.formatArray(booleanArray, 0, result);
        assertEquals("[]", result.toString());
    }

    @Test
    public void truncatedBooleanArray() {
        boolean[] booleanArray = new boolean[]{true, false, true, false};

        arrayAndCollectionFormatter.formatArray(booleanArray, 0, result);
        assertEquals("[true, false, true, ...]", result.toString());
    }

    @Test
    public void objectArray() {
        MyObject[] objectArray = new MyObject[]{myObject1, myObject2};
        objectFormatterMock.performs(new MockBehavior() {
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                result.append("11");
                return null;
            }
        }).formatImpl(myObject1, 3, result);
        objectFormatterMock.performs(new MockBehavior() {
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                result.append("22");
                return null;
            }
        }).formatImpl(myObject2, 3, result);

        arrayAndCollectionFormatter.formatArray(objectArray, 2, result);
        assertEquals("[11, 22]", result.toString());
    }

    @Test
    public void emptyObjectArray() {
        MyObject[] objectArray = new MyObject[0];

        arrayAndCollectionFormatter.formatArray(objectArray, 2, result);
        assertEquals("[]", result.toString());
    }

    @Test
    public void truncatedObjectArray() {
        MyObject[] objectArray = new MyObject[]{myObject1, myObject2, myObject1, myObject2};
        objectFormatterMock.performs(new MockBehavior() {
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                result.append("11");
                return null;
            }
        }).formatImpl(myObject1, 3, result);
        objectFormatterMock.performs(new MockBehavior() {
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                result.append("22");
                return null;
            }
        }).formatImpl(myObject2, 3, result);

        arrayAndCollectionFormatter.formatArray(objectArray, 2, result);
        assertEquals("[11, 22, 11, ...]", result.toString());
    }


    public static class MyObject {
    }
}
