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
package org.unitils.reflectionassert.comparator.impl;

import org.unitils.reflectionassert.ReflectionComparator;
import org.unitils.reflectionassert.comparator.Comparator;
import org.unitils.reflectionassert.difference.Difference;
import org.unitils.reflectionassert.difference.UnorderedCollectionDifference;
import static org.unitils.util.CollectionUtils.convertToCollection;

import java.util.ArrayList;
import java.util.Collection;


/**
 * A comparator for collections and arrays that ignores the order of both collections.
 * Both collections are found equal if they both contain the same elements (in any order).
 * This implements the LENIENT_ORDER comparison mode.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class LenientOrderCollectionComparator implements Comparator {


    /**
     * Returns true if both objects are not null and are both Arrays or Collections.
     *
     * @param left  The left object
     * @param right The right object
     * @return True for Arrays and Collections
     */
    public boolean canCompare(Object left, Object right) {
        if (left == null || right == null) {
            return false;
        }
        if ((left.getClass().isArray() || left instanceof Collection) && (right.getClass().isArray() || right instanceof Collection)) {
            return true;
        }
        return false;
    }


    /**
     * Compares the given collections/arrays but ignoring the actual order of the elements.
     * This will first try to find a sequence that is an exact match. If no such sequence can be found,
     * the difference of all elements with all other elements are calculated one by one.
     *
     * @param left                 The left array/collection, not null
     * @param right                The right array/collection, not null
     * @param onlyFirstDifference  True if only the first difference should be returned
     * @param reflectionComparator The root comparator for inner comparisons, not null
     * @return An UnorderedCollectionDifference or null if both collections are equal
     */
    public Difference compare(Object left, Object right, boolean onlyFirstDifference, ReflectionComparator reflectionComparator) {
        // Convert to list and compare as collection
        ArrayList<Object> leftList = new ArrayList<Object>(convertToCollection(left));
        ArrayList<Object> rightList = new ArrayList<Object>(convertToCollection(right));

        // check whether a combination exists
        boolean isEqual = isEqual(leftList, rightList, 0, reflectionComparator);
        if (isEqual) {
            // found a match
            return null;
        }

        // no match found, determine all differences
        UnorderedCollectionDifference difference = new UnorderedCollectionDifference("Collections/arrays are different", left, right);
        if (onlyFirstDifference) {
            return difference;
        }
        getAllDifferences(leftList, rightList, difference, reflectionComparator);
        return difference;
    }


    /**
     * Recursively checks whether there is a sequence so that both collections have matching elements.
     * This will loop over the elements of the left list and then try to find a match for these elements in the right
     * list. If a match is found, the element is removed from the right collection and the comparison is recursively
     * performed again on the remaining elements.
     *
     * @param leftList             The left list, not null
     * @param rightList            The right list, not null
     * @param leftIndex            The current index in the left collection
     * @param reflectionComparator reflectionComparator The comparator for the element comparisons, not null
     * @return True if a match is found
     */
    @SuppressWarnings({"unchecked"})
    protected boolean isEqual(ArrayList<Object> leftList, ArrayList<Object> rightList, int leftIndex, ReflectionComparator reflectionComparator) {
        if (leftIndex >= leftList.size()) {
            // end of the recursion
            // if there are no more elements left in the right and left collections, a match is found
            return (rightList.isEmpty());
        }

        Object leftValue = leftList.get(leftIndex);
        for (int rightIndex = 0; rightIndex < rightList.size(); rightIndex++) {
            Object rightValue = rightList.get(rightIndex);

            Difference elementDifference = reflectionComparator.getAllDifferences(leftValue, rightValue);
            if (elementDifference != null) {
                // elements are not matching
                continue;
            }

            // match found, try to find a match for the remaining elements
            ArrayList<Object> rightListClone = (ArrayList<Object>) rightList.clone();
            rightListClone.remove(rightIndex);

            boolean isEqual = isEqual(leftList, rightListClone, leftIndex + 1, reflectionComparator);
            if (isEqual) {
                return true;
            }
        }
        return false;
    }


    /**
     * Calculates the difference of all elements in the left list with all elements of the right list.
     * NOTE: because difference are cached in the reflection comparator, comparing two elements that were already
     * compared in the isEqual step will not be done twice (should be very fast).
     *
     * @param leftList             The left list, not null
     * @param rightList            The right list, not null
     * @param difference           The root difference to which all differences will be added, not null
     * @param reflectionComparator The comparator for element comparisons, not null
     */
    @SuppressWarnings({"unchecked"})
    protected void getAllDifferences(ArrayList<Object> leftList, ArrayList<Object> rightList, UnorderedCollectionDifference difference, ReflectionComparator reflectionComparator) {

        for (int leftIndex = 0; leftIndex < leftList.size(); leftIndex++) {
            Object leftValue = leftList.get(leftIndex);
            for (int rightIndex = 0; rightIndex < rightList.size(); rightIndex++) {
                Object rightValue = rightList.get(rightIndex);
                Difference elementDifference = reflectionComparator.getAllDifferences(leftValue, rightValue);
                difference.addElementDifference(leftIndex, rightIndex, elementDifference);
            }
        }
    }
}
