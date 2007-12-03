package org.unitils.util;

import java.util.Set;
import java.util.HashSet;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class CollectionUtils {

    public static <T> Set<T> asSet(T... elements) {
        Set<T> result = new HashSet<T>();
        for (T element : elements) {
            result.add(element);
        }
        return result;
    }
}
