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
import org.unitils.reflectionassert.util.Difference;

import java.util.Date;
import java.util.Map;
import java.util.Stack;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class LenientDatesComparator extends ReflectionComparator {


    // todo javadoc
    public LenientDatesComparator(ReflectionComparator chainedComparator) {
        super(chainedComparator);
    }


    // todo javadoc
    @Override
    public Difference doGetDifference(Object left, Object right, Stack<String> fieldStack, Map<TraversedInstancePair, Boolean> traversedInstancePairs) {
        if ((right == null && left instanceof Date) || (left == null && right instanceof Date)) {
            return new Difference("Lenient dates, but not both instantiated or both null.", left, right, fieldStack);
        }
        if (right instanceof Date && left instanceof Date) {
            return null;
        }
        return chainedComparator.doGetDifference(left, right, fieldStack, traversedInstancePairs);
    }
}
