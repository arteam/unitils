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
import org.unitils.reflectionassert.comparator.Comparison;
import org.unitils.reflectionassert.comparator.Difference;

import java.util.Date;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class LenientDatesComparator implements Comparator {


    // todo javadoc
    public Difference compare(Comparison comparison) {
        Object left = comparison.getLeft();
        Object right = comparison.getRight();

        if ((right == null && left instanceof Date) || (left == null && right instanceof Date)) {
            return comparison.createDifference("Lenient dates, but not both instantiated or both null.");
        }
        if (right instanceof Date && left instanceof Date) {
            return null;
        }
        return comparison.invokeNextComparator();
    }
}
