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
package org.unitils.reflectionassert.formatter.impl;

import static org.apache.commons.lang.StringUtils.repeat;
import org.unitils.reflectionassert.difference.*;
import org.unitils.reflectionassert.formatter.DifferenceFormatter;
import org.unitils.reflectionassert.formatter.util.ObjectFormatter;

import java.util.Map;

/**
 * todo javadoc
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DefaultDifferenceFormatter implements DifferenceFormatter {


    protected int differenceCount = 0;

    protected int indent = 0;

    protected ObjectFormatter objectFormatter = new ObjectFormatter();


    protected String append(String currentString, String newString) {
        String result = currentString;
        result += repeat("\t", indent) + newString + "\n";
        return result;
    }


    public String format(String fieldName, Difference difference) {
        String result = append("", ++differenceCount + ") " + difference.getMessage());
        result = append(result, repeat("-", result.length() - 1));
        if (fieldName != null) {
            result = append(result, "Field: " + fieldName + "\n");
        }
        result = append(result, "Left : " + objectFormatter.format(difference.getLeftValue()));
        result = append(result, "Right: " + objectFormatter.format(difference.getRightValue()) + "\n");
        return result;
    }


    public String format(String fieldName, CollectionDifference collectionDifference) {
        String result = "";

        for (Map.Entry<Integer, Difference> elementDifferences : collectionDifference.getElementDifferences().entrySet()) {
            String innerFieldName = "[" + elementDifferences.getKey() + "]";
            if (fieldName != null) {
                innerFieldName = fieldName + innerFieldName;
            }

            result += elementDifferences.getValue().format(innerFieldName, this);
        }
        return result;
    }


    public String format(String fieldName, UnorderedCollectionDifference unorderedCollectionDifference) {
        String result = append("", ++differenceCount + ") Different collections - Multiple possible matches");
        result = append(result, repeat("-", result.length() - 1));
        if (fieldName != null) {
            result = append(result, "Field: " + fieldName + "\n");
        }
        result = append(result, "Differences with best matches:\n");

        int currentDifferenceCount = differenceCount;
        differenceCount = 0;

        for (Map.Entry<Integer, Map<Integer, Difference>> leftDifferences : unorderedCollectionDifference.getBestMatchingElementDifferences().entrySet()) {
            int leftIndex = leftDifferences.getKey();
            for (Map.Entry<Integer, Difference> rightDifferences : leftDifferences.getValue().entrySet()) {
                int rightIndex = rightDifferences.getKey();
                Difference difference = rightDifferences.getValue();

                if (difference != null) {
                    result = append(result, "* Left index " + leftIndex + ", right index " + rightIndex);
                    indent++;
                    result = append(result, "Left : " + objectFormatter.format(difference.getLeftValue()));
                    result = append(result, "Right: " + objectFormatter.format(difference.getRightValue()) + "\n");
                    result += difference.format(null, this);
                    indent--;
                }
            }
        }
        differenceCount = currentDifferenceCount;
        return result;
    }

    public String format(String fieldName, ObjectDifference objectDifference) {
        Map<String, Difference> fieldDifferences = objectDifference.getFieldDifferences();
        if (fieldDifferences.isEmpty()) {
            // todo implement
        }

        String result = "";
        for (Map.Entry<String, Difference> fieldDifference : fieldDifferences.entrySet()) {
            String innerFieldName = fieldDifference.getKey();
            if (fieldName != null) {
                innerFieldName = fieldName + "." + innerFieldName;
            }
            result += fieldDifference.getValue().format(innerFieldName, this);
        }
        return result;
    }


    public String format(String fieldName, MapDifference mapDifference) {
        Map<Object, Difference> valueDifferences = mapDifference.getValueDifferences();
        if (valueDifferences.isEmpty()) {
            // todo implement
        }

        String result = "";
        for (Map.Entry<Object, Difference> valueDifference : valueDifferences.entrySet()) {
            String innerFieldName = objectFormatter.format(valueDifference.getKey());
            if (fieldName != null) {
                innerFieldName = fieldName + "." + innerFieldName;
            }
            result += valueDifference.getValue().format(innerFieldName, this);
        }
        return result;
    }


}