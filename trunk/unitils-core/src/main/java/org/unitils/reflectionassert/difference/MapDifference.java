/*
 * Copyright 2013,  Unitils.org
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

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * A class for holding the difference between two collections or arrays.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class MapDifference extends Difference {

    /* The differences per key */
    protected Map<Object, Difference> valueDifferences = new IdentityHashMap<Object, Difference>();
    /* The keys of the left map that were missing in the right map */
    protected List<Object> leftMissingKeys = new ArrayList<Object>();
    /* The keys of the right map that were missing in the left map */
    protected List<Object> rightMissingKeys = new ArrayList<Object>();
    /* The left object as a map */
    protected Map<?, ?> leftMap;
    /* The right object as a map */
    protected Map<?, ?> rightMap;

    /**
     * Creates a difference.
     *
     * @param message    a message describing the difference
     * @param leftValue  the left instance
     * @param rightValue the right instance
     * @param leftMap    The left instance as a map
     * @param rightMap   The right instance as a map
     */
    public MapDifference(String message, Object leftValue, Object rightValue, Map<?, ?> leftMap, Map<?, ?> rightMap) {
        super(message, leftValue, rightValue);
        this.leftMap = leftMap;
        this.rightMap = rightMap;
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
     * Adds a key of the left map that is missing in the right map.
     *
     * @param key The left key
     */
    public void addLeftMissingKey(Object key) {
        leftMissingKeys.add(key);
    }

    /**
     * Gets the keys of the left maps that were missing in the right map.
     *
     * @return The keys, not null
     */
    public List<Object> getLeftMissingKeys() {
        return leftMissingKeys;
    }

    /**
     * Adds a key of the right map that is missing in the left map.
     *
     * @param key The right key
     */
    public void addRightMissingKey(Object key) {
        rightMissingKeys.add(key);
    }

    /**
     * Gets the keys of the left maps that were missing in the right map.
     *
     * @return The keys, not null
     */
    public List<Object> getRightMissingKeys() {
        return rightMissingKeys;
    }

    /**
     * @return The left instance as a map
     */
    public Map<?, ?> getLeftMap() {
        return leftMap;
    }

    /**
     * @return The right instance as a map
     */
    public Map<?, ?> getRightMap() {
        return rightMap;
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