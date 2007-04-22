package org.unitils.reflectionassert;

import java.util.Collection;
import java.util.Stack;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

/**
 * todo javadoc
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class LenientOrderCollectionComparator extends CollectionComparator {

    /**
     * todo javadoc
     * @param chainedComparator
     */
    public LenientOrderCollectionComparator(ReflectionComparator chainedComparator) {
        super(chainedComparator);
    }

    /**
     * todo javadoc
     *
     * @param left
     * @param right
     * @param fieldStack
     * @param traversedInstancePairs
     * @return
     */
    @Override
    protected Difference doGetDifference(Object left, Object right, Stack<String> fieldStack, Set<TraversedInstancePair> traversedInstancePairs) {
        // Convert to list and compare as collection
        Collection<?> leftCollection = convertToCollection(left);
        Collection<?> rightCollection = convertToCollection(right);

        if (leftCollection.size() != rightCollection.size()) {
            return new Difference("Different array/collection sizes. Left size: " + leftCollection.size() + ", right size: " +
                    rightCollection.size(), left, right, fieldStack);
        }

        // Create copy from which we can remove elements.
        ArrayList rightCopy = new ArrayList<Object>(rightCollection);

        for (Object lhsValue : leftCollection) {

            boolean found = false;
            Iterator rhsIterator = rightCopy.iterator();
            while (rhsIterator.hasNext()) {
                Object rhsValue = rhsIterator.next();

                // Compare values using reflection
                boolean equal = isEqual(lhsValue, rhsValue);
                if (equal) {
                    rhsIterator.remove();
                    found = true;
                    break;
                }
            }

            if (!found) {
                return new Difference("Left value not found in right collection/array. Left value: " + lhsValue, left, right, fieldStack);
            }
        }
        return null;
    }

}
