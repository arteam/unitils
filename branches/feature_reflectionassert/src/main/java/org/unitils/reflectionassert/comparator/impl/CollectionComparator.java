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
import org.unitils.reflectionassert.difference.CollectionDifference;
import org.unitils.reflectionassert.difference.Difference;
import static org.unitils.util.CollectionUtils.convertToCollection;

import java.util.Collection;
import java.util.Iterator;

/**
 * todo javadoc
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class CollectionComparator implements Comparator {


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
        Collection<?> leftCollection = convertToCollection(left);
        Collection<?> rightCollection = convertToCollection(right);

        int elementIndex = -1;
        CollectionDifference difference = new CollectionDifference("Different elements", left, right);

        Iterator<?> leftIterator = leftCollection.iterator();
        Iterator<?> rightIterator = rightCollection.iterator();
        while (leftIterator.hasNext() && rightIterator.hasNext()) {
            elementIndex++;

            Difference elementDifference = reflectionComparator.getAllDifferences(leftIterator.next(), rightIterator.next());
            if (elementDifference != null) {
                difference.addElementDifference(elementIndex, elementDifference);
            }
        }

        int leftElementIndex = elementIndex;
        while (leftIterator.hasNext()) {
            leftElementIndex++;
            difference.addElementDifference(leftElementIndex, new Difference("Left element not found in right collection", leftIterator.next(), null));
        }

        int rightElementIndex = elementIndex;
        while (rightIterator.hasNext()) {
            rightElementIndex++;
            difference.addElementDifference(rightElementIndex, new Difference("Right element not found in left collection", null, rightIterator.next()));
        }

        if (difference.getElementDifferences().isEmpty()) {
            return null;
        }
        return difference;
    }

}
