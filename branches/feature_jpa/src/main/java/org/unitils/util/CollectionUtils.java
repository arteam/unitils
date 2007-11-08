package org.unitils.util;

import java.util.ArrayList;
import java.util.List;
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
    
    
    public static <T> List<T> subList(List<T> original, int fromIndex, int toIndex) {
    	List<T> subList = new ArrayList<T>();
    	for (int i = fromIndex; i < toIndex; i++) {
    		subList.add(original.get(i));
    	}
    	return subList;
    }
}
