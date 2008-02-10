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

import org.unitils.reflectionassert.formatter.DifferenceFormatter;

import java.util.HashMap;
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


    /**
     * Creates a difference.
     *
     * @param message    a message describing the difference
     * @param leftValue  the left instance
     * @param rightValue the right instance
     */
    public CollectionDifference(String message, Object leftValue, Object rightValue) {
        super(message, leftValue, rightValue);
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
     * Gets the number of element differences.
     *
     * @return The count
     */
    @Override
    public int getInnerDifferenceCount() {
        return elementDifferences.size();
    }


    /**
     * Gets the difference at the given index.
     *
     * @param name The index as a string
     * @return The difference, null if there is no difference.
     */
    @Override
    public Difference getInnerDifference(String name) {
        Integer index;
        try {
            index = new Integer(name);
        } catch (NumberFormatException e) {
            return null;
        }

        return elementDifferences.get(index);
    }


    /**
     * Double dispatch method. Dispatches back to the given difference formatter.
     *
     * @param fieldName           The current field, null for top-level
     * @param differenceFormatter The formatter, not null
     * @return The formatted result
     */
    @Override
    public String format(String fieldName, DifferenceFormatter differenceFormatter) {
        return differenceFormatter.format(fieldName, this);
    }

}