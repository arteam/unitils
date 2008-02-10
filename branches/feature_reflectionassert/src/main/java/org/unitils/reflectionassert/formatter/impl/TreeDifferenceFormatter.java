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
public class TreeDifferenceFormatter implements DifferenceFormatter {


    protected ObjectFormatter objectFormatter = new ObjectFormatter();


    public String format(String fieldName, Difference difference) {
        String fieldNameString = "";
        if (fieldName != null) {
            fieldNameString = fieldName + "   ";
        }
        String result = fieldNameString + "[L] " + objectFormatter.format(difference.getLeftValue()) + "\n";
        result += repeat(" ", fieldNameString.length()) + "[R] " + objectFormatter.format(difference.getRightValue()) + "\n";
        return result;
    }


    public String format(String fieldName, CollectionDifference collectionDifference) {
        String result = format(fieldName, (Difference) collectionDifference);

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
        String result = format(fieldName, (Difference) unorderedCollectionDifference);

        for (Map.Entry<Integer, Map<Integer, Difference>> leftDifferences : unorderedCollectionDifference.getBestMatchingElementDifferences().entrySet()) {
            int leftIndex = leftDifferences.getKey();
            for (Map.Entry<Integer, Difference> rightDifferences : leftDifferences.getValue().entrySet()) {
                int rightIndex = rightDifferences.getKey();
                Difference difference = rightDifferences.getValue();

                String innerFieldName = "[" + leftIndex + "," + rightIndex + "]";
                if (fieldName != null) {
                    innerFieldName = fieldName + innerFieldName;
                }

                if (difference != null) {
                    result += difference.format(innerFieldName, this);
                }
            }
        }
        return result;
    }


    public String format(String fieldName, ObjectDifference objectDifference) {
        String result = format(fieldName, (Difference) objectDifference);

        for (Map.Entry<String, Difference> fieldDifference : objectDifference.getFieldDifferences().entrySet()) {
            String innerFieldName = fieldDifference.getKey();
            if (fieldName != null) {
                innerFieldName = fieldName + "." + innerFieldName;
            }
            result += fieldDifference.getValue().format(innerFieldName, this);
        }
        return result;
    }


    public String format(String fieldName, MapDifference mapDifference) {
        String result = format(fieldName, (Difference) mapDifference);

        for (Map.Entry<Object, Difference> valueDifference : mapDifference.getValueDifferences().entrySet()) {
            String innerFieldName = objectFormatter.format(valueDifference.getKey());
            if (fieldName != null) {
                innerFieldName = fieldName + "." + innerFieldName;
            }
            result += valueDifference.getValue().format(innerFieldName, this);
        }
        return result;
    }


}