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
import org.unitils.reflectionassert.difference.Difference;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * A class for holding the difference between two objects.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class MapDifference extends Difference {

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

    @Override
    public int getInnerDifferenceCount() {
        return valueDifferences.size();
    }

    @Override
    public Difference getInnerDifference(String name) {
        return valueDifferences.get(name);
    }

    public void addValueDifference(Object key, Difference difference) {
        valueDifferences.put(key, difference);
    }

    public Map<Object, Difference> getValueDifferences() {
        return valueDifferences;
    }

    @Override
    public String format(String fieldName, DifferenceFormatter differenceFormatter) {
        return differenceFormatter.format(fieldName, this);
    }


}