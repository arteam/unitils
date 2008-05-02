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
package org.unitils.reflectionassert.difference;

import java.util.HashMap;
import java.util.Map;

/**
 * A class for holding the difference between all elements of two collections.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class UnorderedCollectionDifference extends Difference {

    /* The differences per left-index and right-index */
    private Map<Integer, Map<Integer, Difference>> elementDifferences = new HashMap<Integer, Map<Integer, Difference>>();


    /**
     * Creates a difference.
     *
     * @param message    a message describing the difference
     * @param leftValue  the left instance
     * @param rightValue the right instance
     */
    public UnorderedCollectionDifference(String message, Object leftValue, Object rightValue) {
        super(message, leftValue, rightValue);
    }


    /**
     * Adds a difference or a match for the elements at the given left and right index.
     *
     * @param leftIndex  The index of the left element
     * @param rightIndex The index of the right element
     * @param difference The difference, null for a match
     */
    public void addElementDifference(int leftIndex, int rightIndex, Difference difference) {
        Map<Integer, Difference> rightDifferences = elementDifferences.get(leftIndex);
        if (rightDifferences == null) {
            rightDifferences = new HashMap<Integer, Difference>();
            elementDifferences.put(leftIndex, rightDifferences);
        }
        rightDifferences.put(rightIndex, difference);
    }


    /**
     * Gets all element differences per left index and right index.
     * A null difference means a match.
     *
     * @return The differences, not null
     */
    public Map<Integer, Map<Integer, Difference>> getElementDifferences() {
        return elementDifferences;
    }


    /**
     * Double dispatch method. Dispatches back to the given visitor.
     * <p/>
     * All subclasses should copy this method in their own class body.
     *
     * @param visitor  The visitor, not null
     * @param argument An optional argument for the visitor, null if not applicable
     * @return The result
     */
    public <T, A> T accept(DifferenceVisitor<T, A> visitor, A argument) {
        return visitor.visit(this, argument);
    }

}