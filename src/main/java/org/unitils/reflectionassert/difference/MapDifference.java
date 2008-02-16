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

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * A class for holding the difference between two collections or arrays.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class MapDifference extends Difference {

    /* The differences per key */
    private Map<Object, Difference> valueDifferences = new IdentityHashMap<Object, Difference>();


    /**
     * Creates a difference.
     *
     * @param message    a message describing the difference
     * @param leftValue  the left instance
     * @param rightValue the right instance
     */
    public MapDifference(String message, Object leftValue, Object rightValue) {
        super(message, leftValue, rightValue);
    }


    /**
     * Adds a difference for the element at the given key.
     *
     * @param key        The key
     * @param difference The difference, not null
     */
    public void addValueDifference(Object key, Difference difference) {
        valueDifferences.put(key, difference);
    }


    /**
     * Gets all element differences per key.
     *
     * @return The differences, not null
     */
    public Map<Object, Difference> getValueDifferences() {
        return valueDifferences;
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