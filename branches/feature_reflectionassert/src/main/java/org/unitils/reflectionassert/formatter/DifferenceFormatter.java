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
package org.unitils.reflectionassert.formatter;

import org.unitils.reflectionassert.difference.*;


/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public interface DifferenceFormatter {


    String format(String fieldName, Difference difference);

    String format(String fieldName, ObjectDifference objectDifference);

    String format(String fieldName, MapDifference objectDifference);

    String format(String fieldName, CollectionDifference collectionDifference);

    String format(String fieldName, UnorderedCollectionDifference unorderedCollectionDifference);

}
