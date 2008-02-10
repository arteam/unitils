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

import org.unitils.reflectionassert.difference.Difference;
import org.unitils.reflectionassert.ReflectionComparator;

/**
 * todo javadoc
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public interface Comparator {


    boolean canCompare(Object left, Object right);

    // todo javadoc
    // If this ReflectionComparator is able to check whether their is a difference in the given left
    //and right objects (i.e. canHandle(Object,Object) returns true), the objects are compared.
    Difference compare(Object left, Object right, ReflectionComparator reflectionComparator);

}
