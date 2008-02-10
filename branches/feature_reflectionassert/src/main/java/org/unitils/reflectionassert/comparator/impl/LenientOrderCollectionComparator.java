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
 * todo javadoc
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class LenientOrderCollectionComparator implements Comparator {


    public boolean canCompare(Object left, Object right) {
        if (left == null || right == null) {
            return false;
        }
        if ((left.getClass().isArray() || left instanceof Collection) && (right.getClass().isArray() || right instanceof Collection)) {
            return true;
        }
        return false;
    }


    // todo javadoc
    public Difference compare(Object left, Object right, ReflectionComparator reflectionComparator) {
        // Convert to list and compare as collection
        ArrayList<Object> leftList = new ArrayList<Object>(convertToCollection(left));
        ArrayList<Object> rightList = new ArrayList<Object>(convertToCollection(right));

        boolean isEqual = isEqual(leftList, rightList, 0, reflectionComparator);
        if (isEqual) {
            return null;
        }

        UnorderedCollectionDifference difference = new UnorderedCollectionDifference("Collections/arrays are different", left, right);
        getAllDifferences(leftList, rightList, difference, reflectionComparator);
        return difference;
    }


    @SuppressWarnings({"unchecked"})
    protected boolean isEqual(ArrayList<Object> leftList, ArrayList<Object> rightList, int leftIndex, ReflectionComparator reflectionComparator) {
        if (leftIndex >= leftList.size()) {
            return (rightList.isEmpty());
        }

        Object leftValue = leftList.get(leftIndex);
        for (int rightIndex = 0; rightIndex < rightList.size(); rightIndex++) {
            Object rightValue = rightList.get(rightIndex);

            Difference elementDifference = reflectionComparator.getAllDifferences(leftValue, rightValue);
            if (elementDifference != null) {
                continue;
            }

            ArrayList<Object> rightListClone = (ArrayList<Object>) rightList.clone();
            rightListClone.remove(rightIndex);

            boolean isEqual = isEqual(leftList, rightListClone, leftIndex + 1, reflectionComparator);
            if (isEqual) {
                return true;
            }
        }
        return false;
    }


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
