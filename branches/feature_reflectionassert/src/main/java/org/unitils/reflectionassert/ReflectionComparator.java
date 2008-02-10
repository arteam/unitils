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
package org.unitils.reflectionassert;

import org.unitils.reflectionassert.comparator.Comparator;
import org.unitils.reflectionassert.difference.Difference;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;


/**
 * Abstract superclass that defines a template for sub implementations that can compare objects of a certain kind.
 * Different instances of different subtypes will be chained to obtain a reflection comparator chain. This chain
 * will compare two objects with eachother through reflection. Depending on the composition of the chain, a number
 * of 'leniency levels' are in operation.
 * <p/>
 * If the check indicates that both objects are not equal, the first (and only the first!) found difference is returned.
 * The actual difference can then be retrieved by the fieldStack, leftValue and rightValue properties.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ReflectionComparator {

    /**
     * The comparator chain.
     */
    protected List<Comparator> comparators;

    protected Stack<String> fieldStack = new Stack<String>();

    protected Map<Object, Map<Object, Difference>> cachedResults = new IdentityHashMap<Object, Map<Object, Difference>>();


    /**
     * Constructs a new instance, with the given comparator as the next element in the chain. Makes sure that this
     * instance is registered as root comparator of the given chained comparator. Setting the root comparator gets
     * propagated to all elements in the chain. This way, all comparators share the same root at all times.
     *
     * @param comparators The comparator chain
     */
    public ReflectionComparator(List<Comparator> comparators) {
        this.comparators = comparators;
    }


    /**
     * Checks whether there is a difference between the left and right objects. Whether there is a difference, depends
     * on the concrete comparators in the chain.
     *
     * @param left  the left instance
     * @param right the right instance
     * @return the difference, null if there is no difference
     */
    public Difference getDifference(Object left, Object right) {
        // todo implement
        return getAllDifferences(left, right);
    }


    // todo javadoc
    public Difference getAllDifferences(Object left, Object right) {

        Map<Object, Difference> cachedResult = cachedResults.get(left);
        if (cachedResult != null) {
            if (cachedResult.containsKey(right)) {
                return cachedResult.get(right);
            }
        } else {
            cachedResult = new IdentityHashMap<Object, Difference>();
            cachedResults.put(left, cachedResult);
        }
        cachedResult.put(right, null);

        Difference result = null;
        for (Comparator comparator : comparators) {
            if (comparator.canCompare(left, right)) {
                result = comparator.compare(left, right, this);
                break;
            }
        }

        cachedResult.put(right, result);
        return result;
    }


    /**
     * Checks whether there is no difference between the left and right objects. The meaning of no difference is
     * determined by the set comparator modes. See class javadoc for more info.
     *
     * @param left  the left instance
     * @param right the right instance
     * @return true if there is no difference, false otherwise
     */
    public boolean isEqual(Object left, Object right) {
        Difference difference = getDifference(left, right);
        return difference == null;
    }

}
