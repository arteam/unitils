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

import java.util.Collection;
import java.util.Map;

/**
 * Helper class for generating a string representation of a collection or array.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ArrayAndCollectionFormatter {

    /* The maximum nr of elements for arrays and collections to display */
    protected int maxNrOfElements;
    /* The object formatter that uses this array/collection formatter */
    protected ObjectFormatter objectFormatter;


    /**
     * Creates a formatter with the given maximum nr of elements.
     *
     * @param maxNrOfElements The maximum nr of elements for arrays and collections to display  > 0
     * @param objectFormatter The object formatter that uses this array/collection formatter, not null
     */
    public ArrayAndCollectionFormatter(int maxNrOfElements, ObjectFormatter objectFormatter) {
        this.maxNrOfElements = maxNrOfElements;
        this.objectFormatter = objectFormatter;
    }


    /**
     * Formats the given array.
     *
     * @param array        The array, not null
     * @param currentDepth The current recursion depth
     * @param result       The builder to append the result to, not null
     */
    public void formatArray(Object array, int currentDepth, StringBuilder result) {
        if (array instanceof byte[]) {
            formatByteArray((byte[]) array, result);
            return;
        }
        if (array instanceof short[]) {
            formatShortArray((short[]) array, result);
            return;
        }
        if (array instanceof int[]) {
            formatIntArray((int[]) array, result);
            return;
        }
        if (array instanceof long[]) {
            formatLongArray((long[]) array, result);
            return;
        }
        if (array instanceof char[]) {
            formatCharArray((char[]) array, result);
            return;
        }
        if (array instanceof float[]) {
            formatFloatArray((float[]) array, result);
            return;
        }
        if (array instanceof double[]) {
            formatDoubleArray((double[]) array, result);
            return;
        }
        if (array instanceof boolean[]) {
            formatBooleanArray((boolean[]) array, result);
            return;
        }
        formatObjectArray((Object[]) array, currentDepth, result);
    }

    /**
     * Formats the given collection.
     *
     * @param collection   The collection, not null
     * @param currentDepth The current recursion depth
     * @param result       The builder to append the result to, not null
     */
    public void formatCollection(Collection<?> collection, int currentDepth, StringBuilder result) {
        result.append("[");
        int count = 0;
        for (Object element : collection) {
            if (count++ > 0) {
                result.append(", ");
            }
            objectFormatter.formatImpl(element, currentDepth + 1, result);

            if (count >= maxNrOfElements && count < collection.size()) {
                result.append(", ...");
                break;
            }
        }
        result.append("]");
    }

    /**
     * Formats the given map.
     *
     * @param map          The map, not null
     * @param currentDepth The current recursion depth
     * @param result       The builder to append the result to, not null
     */
    public void formatMap(Map<?, ?> map, int currentDepth, StringBuilder result) {
        result.append("{");
        int count = 0;
        for (Map.Entry<?, ?> element : map.entrySet()) {
            if (count++ > 0) {
                result.append(", ");
            }
            objectFormatter.formatImpl(element.getKey(), currentDepth, result);
            result.append("=");
            objectFormatter.formatImpl(element.getValue(), currentDepth + 1, result);

            if (count >= maxNrOfElements && count < map.size()) {
                result.append(", ...");
                break;
            }
        }
        result.append("}");
    }


    protected void formatObjectArray(Object[] array, int currentDepth, StringBuilder result) {
        result.append("[");
        int i = 0;
        for (; i < array.length && i < maxNrOfElements; i++) {
            if (i > 0) {
                result.append(", ");
            }
            objectFormatter.formatImpl(array[i], currentDepth + 1, result);
        }
        if (i < array.length) {
            result.append(", ...");
        }
        result.append("]");
    }

    protected void formatByteArray(byte[] array, StringBuilder result) {
        result.append("[");
        int i = 0;
        for (; i < array.length && i < maxNrOfElements; i++) {
            if (i > 0) {
                result.append(", ");
            }
            result.append(array[i]);
        }
        if (i < array.length) {
            result.append(", ...");
        }
        result.append("]");
    }

    protected void formatShortArray(short[] array, StringBuilder result) {
        result.append("[");
        int i = 0;
        for (; i < array.length && i < maxNrOfElements; i++) {
            if (i > 0) {
                result.append(", ");
            }
            result.append(array[i]);
        }
        if (i < array.length) {
            result.append(", ...");
        }
        result.append("]");
    }

    protected void formatIntArray(int[] array, StringBuilder result) {
        result.append("[");
        int i = 0;
        for (; i < array.length && i < maxNrOfElements; i++) {
            if (i > 0) {
                result.append(", ");
            }
            result.append(array[i]);
        }
        if (i < array.length) {
            result.append(", ...");
        }
        result.append("]");
    }

    protected void formatLongArray(long[] array, StringBuilder result) {
        result.append("[");
        int i = 0;
        for (; i < array.length && i < maxNrOfElements; i++) {
            if (i > 0) {
                result.append(", ");
            }
            result.append(array[i]);
        }
        if (i < array.length) {
            result.append(", ...");
        }
        result.append("]");
    }

    protected void formatCharArray(char[] array, StringBuilder result) {
        result.append("[");
        int i = 0;
        for (; i < array.length && i < maxNrOfElements; i++) {
            if (i > 0) {
                result.append(", ");
            }
            result.append('\'');
            result.append(array[i]);
            result.append('\'');
        }
        if (i < array.length) {
            result.append(", ...");
        }
        result.append("]");
    }

    protected void formatFloatArray(float[] array, StringBuilder result) {
        result.append("[");
        int i = 0;
        for (; i < array.length && i < maxNrOfElements; i++) {
            if (i > 0) {
                result.append(", ");
            }
            result.append(array[i]);
        }
        if (i < array.length) {
            result.append(", ...");
        }
        result.append("]");
    }

    protected void formatDoubleArray(double[] array, StringBuilder result) {
        result.append("[");
        int i = 0;
        for (; i < array.length && i < maxNrOfElements; i++) {
            if (i > 0) {
                result.append(", ");
            }
            result.append(array[i]);
        }
        if (i < array.length) {
            result.append(", ...");
        }
        result.append("]");
    }

    protected void formatBooleanArray(boolean[] array, StringBuilder result) {
        result.append("[");
        int i = 0;
        for (; i < array.length && i < maxNrOfElements; i++) {
            if (i > 0) {
                result.append(", ");
            }
            result.append(array[i]);
        }
        if (i < array.length) {
            result.append(", ...");
        }
        result.append("]");
    }
}
