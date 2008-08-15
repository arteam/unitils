/*
 * Copyright 2008,  Unitils.org
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

/**
 * A visitor for visiting all types of differences.
 * <p/>
 * All difference classes should implement a double-dispatch method as follows:
 * <code><pre>
 * public &lt;T, A&gt; T accept(DifferenceVisitor&lt;T, A&gt; visitor, A argument) {
 *      return visitor.visit(this, argument);
 * }
 * </pre></code>
 * <p/>
 * The visitor logic can the be invoked as follows:
 * <code><pre>
 * difference.accept(new MyVisitor(), anOptionalArgument);
 * </pre></code>
 * <p/>
 * T determines the result type,
 * A determines the type of the optional argument
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public interface DifferenceVisitor<T, A> {


    /**
     * Visits a simple difference.
     *
     * @param difference The difference
     * @param argument   An optional argument, null if NA
     * @return The result
     */
    T visit(Difference difference, A argument);


    /**
     * Visits an object difference.
     *
     * @param objectDifference The difference
     * @param argument         An optional argument, null if NA
     * @return The result
     */
    T visit(ObjectDifference objectDifference, A argument);


    /**
     * Visits a map difference.
     *
     * @param mapDifference The difference
     * @param argument      An optional argument, null if NA
     * @return The result
     */
    T visit(MapDifference mapDifference, A argument);


    /**
     * Visits a collection difference.
     *
     * @param collectionDifference The difference
     * @param argument             An optional argument, null if NA
     * @return The result
     */
    T visit(CollectionDifference collectionDifference, A argument);


    /**
     * Visits an unordered collection difference.
     *
     * @param unorderedCollectionDifference The difference
     * @param argument                      An optional argument, null if NA
     * @return The result
     */
    T visit(UnorderedCollectionDifference unorderedCollectionDifference, A argument);

}
