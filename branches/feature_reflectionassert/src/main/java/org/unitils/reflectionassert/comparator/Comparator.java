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
package org.unitils.reflectionassert.comparator;

import org.unitils.reflectionassert.ReflectionComparator;
import org.unitils.reflectionassert.difference.Difference;

/**
 * Interface for comparing 2 given objects.
 * <p/>
 * One should always first call canCompare before calling compare to perform the actual comparison.
 * If canCompare returns false, the comparator implementation is not suited to compare the given objects and compare
 * should not be called.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public interface Comparator {


    /**
     * Checks whether this comparator can compare the given objects.
     * <p/>
     * This method should always be called before calling the compare method. If false is returned compare
     * should not be invoked.
     *
     * @param left  The left object
     * @param right The right object
     * @return True if compare can be called, false otherwise
     */
    boolean canCompare(Object left, Object right);


    /**
     * Compares the given objects and returns the difference (if any).
     * <p/>
     * The reflection comparator is passed as an argument and can be used to perform inner comparisons.
     * E.g. during the comparison of an object the given comparator can be used to compare the instance
     * fields of the object.
     *
     * @param left                 The left object
     * @param right                The right object
     * @param reflectionComparator The root comparator for inner comparisons, not null
     * @return The difference, null if a match is found
     */
    Difference compare(Object left, Object right, ReflectionComparator reflectionComparator);

}
