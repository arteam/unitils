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
package org.unitils.reflectionassert.comparator.impl;

import org.unitils.reflectionassert.comparator.Comparator;
import org.unitils.reflectionassert.difference.Difference;
import org.unitils.reflectionassert.ReflectionComparator;

/**
 * todo javadoc
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class IgnoreDefaultsComparator implements Comparator {


    public boolean canCompare(Object left, Object right) {
        // object types
        if (left == null) {
            return true;
        }
        // primitive boolean types
        if (left instanceof Boolean && !(Boolean) left) {
            return true;
        }
        // primitive character types
        if (left instanceof Character && (Character) left == 0) {
            return true;
        }
        // primitive int/long/double/float types
        if (left instanceof Number && ((Number) left).doubleValue() == 0) {
            return true;
        }
        return false;
    }


    // todo javadoc
    public Difference compare(Object left, Object right, ReflectionComparator reflectionComparator) {
        // ignore
        return null;
    }
}
