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

import java.util.Map;
import java.util.Stack;

/**
 * todo javadoc
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class IgnoreDefaultsComparator extends ReflectionComparator {


    // todo javadoc
    public IgnoreDefaultsComparator(ReflectionComparator chainedComparator) {
        super(chainedComparator);
    }

    // todo javadoc
    @Override
    public Difference doGetDifference(Object left, Object right, Stack<String> fieldStack, Map<TraversedInstancePair, Boolean> traversedInstancePairs) {
        // object types
        if (left == null) {
            return null;
        }
        // primitive boolean types
        if (left instanceof Boolean && !(Boolean) left) {
            return null;
        }
        // primitive character types
        if (left instanceof Character && (Character) left == 0) {
            return null;
        }
        // primitive int/long/double/float types
        if (left instanceof Number && ((Number) left).doubleValue() == 0) {
            return null;
        }
        return chainedComparator.doGetDifference(left, right, fieldStack, traversedInstancePairs);
    }
}
