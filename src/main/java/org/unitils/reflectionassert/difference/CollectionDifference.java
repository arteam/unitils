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
import java.util.List;
import java.util.Map;

/**
 * A class for holding the difference between two collections or arrays.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class CollectionDifference extends Difference {

    /* The differences per index */
    private Map<Integer, Difference> elementDifferences = new HashMap<Integer, Difference>();

    /* The left object as a list */
    private List<?> leftList;

    /* The right object as a list */
    private List<?> rightList;


    /**
     * Creates a difference.
     *
     * @param message    A message describing the difference
     * @param leftValue  The left instance
     * @param rightValue The right instance
     * @param leftList   The left instance as a list
     * @param rightList  The right instance as a list
     */
    public CollectionDifference(String message, Object leftValue, Object rightValue, List<?> leftList, List<?> rightList) {
        super(message, leftValue, rightValue);
        this.leftList = leftList;
        this.rightList = rightList;
    }


    /**
     * Adds a difference for the element at the given index.
     *
     * @param index      The element index
     * @param difference The difference, not null
     */
    public void addElementDifference(int index, Difference difference) {
        elementDifferences.put(index, difference);
    }


    /**
     * Gets all element differences per index.
     *
     * @return The differences, not null
     */
    public Map<Integer, Difference> getElementDifferences() {
        return elementDifferences;
    }


    /**
     * @return The left instance as a list
     */
    public List<?> getLeftList() {
        return leftList;
    }


    /**
     * @return The right instance as a list
     */
    public List<?> getRightList() {
        return rightList;
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
    @Override
    public <T, A> T accept(DifferenceVisitor<T, A> visitor, A argument) {
        return visitor.visit(this, argument);
    }

}