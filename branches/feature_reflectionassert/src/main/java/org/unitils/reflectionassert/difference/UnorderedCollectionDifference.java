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
package org.unitils.reflectionassert.difference;

import org.unitils.reflectionassert.formatter.DifferenceFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class for holding the difference between two objects.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class UnorderedCollectionDifference extends Difference {


    private Map<Integer, Map<Integer, Difference>> elementDifferences = new HashMap<Integer, Map<Integer, Difference>>();


    /**
     * Creates a difference.
     *
     * @param message    a message describing the difference
     * @param leftValue  the left instance
     * @param rightValue the right instance
     */
    public UnorderedCollectionDifference(String message, Object leftValue, Object rightValue) {
        super(message, leftValue, rightValue);
    }


    public void addElementDifference(int leftIndex, int rightIndex, Difference difference) {
        Map<Integer, Difference> rightDifferences = elementDifferences.get(leftIndex);
        if (rightDifferences == null) {
            rightDifferences = new HashMap<Integer, Difference>();
            elementDifferences.put(leftIndex, rightDifferences);
        }
        rightDifferences.put(rightIndex, difference);
    }


    public Map<Integer, Map<Integer, Difference>> getElementDifferences() {
        return elementDifferences;
    }


    //todo implement
    public int getInnerDifferenceCount() {
        List<Integer> rightIndexMatches = new ArrayList<Integer>();
        int totalMatchingScore = 0;
        for (Map<Integer, Difference> differences : elementDifferences.values()) {
            int bestMatchingScore = Integer.MAX_VALUE;

            for (Map.Entry<Integer, Difference> rightDifferences : differences.entrySet()) {
                Integer rightIndex = rightDifferences.getKey();
                Difference difference = rightDifferences.getValue();

                if (difference == null) {
                    bestMatchingScore = -1;
                    rightIndexMatches.add(rightIndex);
                    break;
                }

                if (rightIndexMatches.contains(rightIndex)) {
                    continue;
                }

                int matchingScore = difference.getInnerDifferenceCount();
                if (matchingScore < bestMatchingScore) {
                    bestMatchingScore = matchingScore;
                }
            }
            totalMatchingScore += bestMatchingScore;
        }
        return totalMatchingScore;
    }


    @Override
    public Difference getInnerDifference(String name) {
        Integer index;
        try {
            index = new Integer(name);
        } catch (NumberFormatException e) {
            return null;
        }

        Map<Integer, Difference> differences = elementDifferences.get(index);
        if (differences == null || differences.isEmpty()) {
            return null;
        }
        return differences.values().iterator().next();
    }


    @Override
    public String format(String fieldName, DifferenceFormatter differenceFormatter) {
        return differenceFormatter.format(fieldName, this);
    }


    public Map<Integer, Map<Integer, Difference>> getBestMatchingElementDifferences() {
        List<Integer> rightIndexMatches = new ArrayList<Integer>();

        Map<Integer, Map<Integer, Difference>> result = new HashMap<Integer, Map<Integer, Difference>>();
        for (Map.Entry<Integer, Map<Integer, Difference>> leftDifferences : elementDifferences.entrySet()) {
            Integer leftIndex = leftDifferences.getKey();

            int bestMatchingScore = Integer.MAX_VALUE;
            for (Map.Entry<Integer, Difference> rightDifferences : leftDifferences.getValue().entrySet()) {
                Integer rightIndex = rightDifferences.getKey();
                Difference difference = rightDifferences.getValue();

                int matchingScore;
                if (difference == null) {
                    matchingScore = 0;
                    rightIndexMatches.add(rightIndex);
                } else {
                    matchingScore = difference.getInnerDifferenceCount();
                }

                if (difference != null && rightIndexMatches.contains(rightIndex)) {
                    continue;
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

}