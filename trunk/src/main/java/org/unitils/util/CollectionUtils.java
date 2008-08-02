/*
 * Copyright 2006-2007,  Unitils.org
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
package org.unitils.util;

import org.apache.commons.lang.ArrayUtils;

import java.util.*;
import static java.util.Arrays.asList;

/**
 * Class containing collection related utilities
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class CollectionUtils {


    /**
     * Gets a list containing all elements from the given index to the given index.
     *
     * @param list      The original list
     * @param fromIndex The from index
     * @param toIndex   The to index
     * @return The sub-list, not null
     */
    public static <T> List<T> subList(List<T> list, int fromIndex, int toIndex) {
        List<T> result = new ArrayList<T>();
        if (list == null) {
            return result;
        }
        for (int i = fromIndex; i < toIndex; i++) {
            result.add(list.get(i));
        }
        return result;
    }


    /**
     * Converts the given array of elements to a set.
     *
     * @param elements The elements
     * @return The elements as a set, empty if elements was null
     */
    public static <T> Set<T> asSet(T... elements) {
        Set<T> result = new HashSet<T>();
        if (elements == null) {
            return result;
        }
        result.addAll(asList(elements));
        return result;
    }


    /**
     * Converts the given array or collection object (possibly primitive array) to type Collection
     *
     * @param object The array or collection
     * @return The object collection
     */
    public static Collection<?> convertToCollection(Object object) {
        if (object instanceof Collection<?>) {
            return (Collection<?>) object;
        }

        // If needed convert primitive array to object array
        Object[] objectArray = convertToObjectArray(object);

        // Convert array to collection
        return asList(objectArray);
    }


    /**
     * Converts the given array object (possibly primitive array) to type Object[]
     *
     * @param object The array
     * @return The object array
     */
    public static Object[] convertToObjectArray(Object object) {
        if (object instanceof byte[]) {
            return ArrayUtils.toObject((byte[]) object);

        } else if (object instanceof short[]) {
            return ArrayUtils.toObject((short[]) object);

        } else if (object instanceof int[]) {
            return ArrayUtils.toObject((int[]) object);

        } else if (object instanceof long[]) {
            return ArrayUtils.toObject((long[]) object);

        } else if (object instanceof char[]) {
            return ArrayUtils.toObject((char[]) object);

        } else if (object instanceof float[]) {
            return ArrayUtils.toObject((float[]) object);

        } else if (object instanceof double[]) {
            return ArrayUtils.toObject((double[]) object);

        } else if (object instanceof boolean[]) {
            return ArrayUtils.toObject((boolean[]) object);

        } else {
            return (Object[]) object;
        }
    }
}
