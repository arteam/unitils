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

import org.unitils.reflectionassert.comparator.Difference;

import java.util.Stack;

/**
 * todo javadoc
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public interface Comparison {


    Object getLeft();


    Object getRight();


    Stack<String> getFieldStack();


    Difference createDifference(String message);


    Difference invokeNextComparator();


    Difference getInnerDifference(Object left, Object right);


    Difference getNewDifference(Object left, Object right);

}
