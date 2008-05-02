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
package org.unitils.reflectionassert.formatter.util;

import org.unitils.reflectionassert.difference.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A utility class to find the best matching differences out of all element differences in
 * an unordered collection difference.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class BestMatchFinder {


    /**
     * The visitor for visiting the difference tree
     */
    protected MatchingScoreVisitor matchingScoreVisitor = new MatchingScoreVisitor();


    /**
     * Gets the best matching differences for the given unordered collection difference.
     * The unordered collection difference contains the differences of all left-elements with all right-elements.
     * The result will contain 1 difference for each left and right element. Only differences are returned, if a left
     * and right element were an exact match, they will not be returned.
     *
     * @param unorderedCollectionDifference The difference, not null
     * @return The best difference per left and right index
     */
    public Map<Integer, Map<Integer, Difference>> getBestMatches(UnorderedCollectionDifference unorderedCollectionDifference) {
        // find the indexes of the exact matches so that they can be filtered out
        List<Integer> leftIndexMatches = new ArrayList<Integer>();
        List<Integer> rightIndexMatches = new ArrayList<Integer>();
        findIndexesOfExactMatches(unorderedCollectionDifference.getElementDifferences(), leftIndexMatches, rightIndexMatches);

        Map<Integer, Map<Integer, Difference>> result = new HashMap<Integer, Map<Integer, Difference>>();
        for (Map.Entry<Integer, Map<Integer, Difference>> leftDifferences : unorderedCollectionDifference.getElementDifferences().entrySet()) {
            Integer leftIndex = leftDifferences.getKey();
            if (leftIndexMatches.contains(leftIndex)) {
                // ignore best matches
                continue;
            }

            // find the best matching score of this left element with all right elements
            int bestMatchingScore = Integer.MAX_VALUE;
            for (Map.Entry<Integer, Difference> rightDifferences : leftDifferences.getValue().entrySet()) {
                Integer rightIndex = rightDifferences.getKey();
                if (rightIndexMatches.contains(rightIndex)) {
                    // ignore best matches
                    continue;
                }

                int matchingScore;
                Difference difference = rightDifferences.getValue();
                if (difference == null) {
                    matchingScore = 0;
                } else {
                    matchingScore = difference.accept(matchingScoreVisitor, null);
                }

                if (matchingScore < bestMatchingScore) {
                    bestMatchingScore = matchingScore;

                    Map<Integer, Difference> resultDifference = new HashMap<Integer, Difference>();
                    resultDifference.put(rightIndex, difference);
                    result.put(leftIndex, resultDifference);
                }
            }
        }
        return result;
    }


    /**
     * Finds all indexes that have a null difference.
     *
     * @param elementDifferences All element differences, not null
     * @param leftIndexMatches   The resulting left indexes, not null
     * @param rightIndexMatches  The resulting right indexes, not null
     */
    protected void findIndexesOfExactMatches(Map<Integer, Map<Integer, Difference>> elementDifferences, List<Integer> leftIndexMatches, List<Integer> rightIndexMatches) {
        for (Map.Entry<Integer, Map<Integer, Difference>> leftDifferences : elementDifferences.entrySet()) {
            Integer leftIndex = leftDifferences.getKey();
            for (Map.Entry<Integer, Difference> rightDifferences : leftDifferences.getValue().entrySet()) {
                Integer rightIndex = rightDifferences.getKey();
                Difference difference = rightDifferences.getValue();
                if (difference == null) {
                    leftIndexMatches.add(leftIndex);
                    rightIndexMatches.add(rightIndex);
                }
            }
        }
    }


    /**
     * Gets the matching score for a simple difference.
     * This will return 0 in case both objects are of the same type.
     * If both objects are of a different type, they are less likely to be a best match, so 5 is returned.
     *
     * @param difference The difference, not null
     * @return The score
     */
    protected int getMatchingScore(Difference difference) {
        Object leftValue = difference.getLeftValue();
        Object rightValue = difference.getRightValue();
        if (leftValue != null && rightValue != null && !leftValue.getClass().equals(rightValue.getClass())) {
            return 5;
        }
        return 1;
    }


    /**
     * Gets the matching score for an object difference.
     * Returns the nr of field differences.
     *
     * @param objectDifference The difference, not null
     * @return The score
     */
    protected int getMatchingScore(ObjectDifference objectDifference) {
        return objectDifference.getFieldDifferences().size();
    }


    /**
     * Gets the matching score for a map difference.
     * Returns the nr of value differences.
     *
     * @param mapDifference The difference, not null
     * @return The score
     */
    protected int getMatchingScore(MapDifference mapDifference) {
        return mapDifference.getValueDifferences().size();
    }


    /**
     * Gets the matching score for a collection difference.
     * Returns the nr of element differences.
     *
     * @param collectionDifference The difference, not null
     * @return The score
     */
    protected int getMatchingScore(CollectionDifference collectionDifference) {
        return collectionDifference.getElementDifferences().size();
    }


    /**
     * Gets the matching score for an unordered collection difference.
     * Returns the sum of the matching scores of the best matches.
     *
     * @param unorderedCollectionDifference The difference, not null
     * @return The score
     */
    protected int getMatchingScore(UnorderedCollectionDifference unorderedCollectionDifference) {
        int totalMatchingScore = 0;

        Map<Integer, Map<Integer, Difference>> bestMatchDifferences = getBestMatches(unorderedCollectionDifference);
        for (Map<Integer, Difference> differences : bestMatchDifferences.values()) {
            int bestMatchingScore = Integer.MAX_VALUE;

            for (Difference difference : differences.values()) {
                if (difference == null) {
                    bestMatchingScore = 0;
                    break;
                }

                int matchingScore = difference.accept(matchingScoreVisitor, null);
                if (matchingScore < bestMatchingScore) {
                    bestMatchingScore = matchingScore;
                }
            }
            totalMatchingScore += bestMatchingScore;
        }
        return totalMatchingScore;
    }


    /**
     * The visitor for visiting the difference tree.
     */
    protected class MatchingScoreVisitor implements DifferenceVisitor<Integer, Integer> {

        public Integer visit(Difference difference, Integer argument) {
            return getMatchingScore(difference);
        }

        public Integer visit(ObjectDifference objectDifference, Integer argument) {
            return getMatchingScore(objectDifference);
        }

        public Integer visit(MapDifference mapDifference, Integer argument) {
            return getMatchingScore(mapDifference);
        }

        public Integer visit(CollectionDifference collectionDifference, Integer argument) {
            return getMatchingScore(collectionDifference);
        }

        public Integer visit(UnorderedCollectionDifference unorderedCollectionDifference, Integer argument) {
            return getMatchingScore(unorderedCollectionDifference);
        }
    }
}