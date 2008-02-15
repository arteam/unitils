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
import org.unitils.reflectionassert.formatter.util.BestMatchFinder;
import org.unitils.reflectionassert.formatter.util.ObjectFormatter;

import java.util.Map;

/**
 * Formatter that will output all leaf differences in the tree and also the difference of
 * all best matches in case of an unordered collection difference.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DefaultDifferenceFormatter implements DifferenceFormatter {

    /**
     * The current nr of outputted differences.
     */
    protected int differenceCount = 0;

    /**
     * The current nr of tabs.
     */
    protected int indent = 0;

    /**
     * True when an unordered collection is being formatted.
     */
    protected boolean outputtingUnorderedCollectionDifference = false;

    /**
     * Formatter for object values.
     */
    protected ObjectFormatter objectFormatter = new ObjectFormatter();

    /**
     * The visitor for visiting the difference tree
     */
    protected DifferenceFormatterVisitor differenceFormatterVisitor = new DifferenceFormatterVisitor();

    /**
     * A best match finder for unordered collection differences.
     */
    protected BestMatchFinder bestMatchFinder = new BestMatchFinder();


    /**
     * Creates a string representation of the given difference tree.
     *
     * @param difference The root difference, not null
     * @return The string representation, not null
     */
    public String format(Difference difference) {
        return difference.accept(differenceFormatterVisitor, null);
    }


    /**
     * Creates a string representation of a simple difference.
     *
     * @param difference The difference, not null
     * @param fieldName  The current fieldName, null for root
     * @return The string representation, not null
     */
    protected String formatDifference(Difference difference, String fieldName) {
        String message = ++differenceCount + ") " + difference.getMessage();
        String result = append("", message);
        result = append(result, repeat("-", message.length()));
        if (fieldName != null) {
            result = append(result, "Field: " + fieldName + "\n");
        }
        result = append(result, "Left : " + objectFormatter.format(difference.getLeftValue()));
        result = append(result, "Right: " + objectFormatter.format(difference.getRightValue()) + "\n\n");
        return result;
    }


    /**
     * Creates a string representation of an object difference.
     *
     * @param objectDifference The difference, not null
     * @param fieldName        The current fieldName, null for root
     * @return The string representation, not null
     */
    protected String formatDifference(ObjectDifference objectDifference, String fieldName) {
        String result = "";
        for (Map.Entry<String, Difference> fieldDifference : objectDifference.getFieldDifferences().entrySet()) {
            String innerFieldName = fieldDifference.getKey();
            if (fieldName != null) {
                innerFieldName = fieldName + "." + innerFieldName;
            }
            result += fieldDifference.getValue().accept(differenceFormatterVisitor, innerFieldName);
        }
        return result;
    }


    /**
     * Creates a string representation of a collection difference.
     *
     * @param collectionDifference The difference, not null
     * @param fieldName            The current fieldName, null for root
     * @return The string representation, not null
     */
    protected String formatDifference(CollectionDifference collectionDifference, String fieldName) {
        String result = "";
        for (Map.Entry<Integer, Difference> elementDifferences : collectionDifference.getElementDifferences().entrySet()) {
            String innerFieldName = "[" + elementDifferences.getKey() + "]";
            if (fieldName != null) {
                innerFieldName = fieldName + innerFieldName;
            }
            result += elementDifferences.getValue().accept(differenceFormatterVisitor, innerFieldName);
        }
        return result;
    }


    /**
     * Creates a string representation of a map difference.
     *
     * @param mapDifference The difference, not null
     * @param fieldName     The current fieldName, null for root
     * @return The string representation, not null
     */
    protected String formatDifference(MapDifference mapDifference, String fieldName) {
        String result = "";
        for (Map.Entry<Object, Difference> valueDifference : mapDifference.getValueDifferences().entrySet()) {
            String innerFieldName = objectFormatter.format(valueDifference.getKey());
            if (fieldName != null) {
                innerFieldName = fieldName + "." + innerFieldName;
            }
            result += valueDifference.getValue().accept(differenceFormatterVisitor, innerFieldName);
        }
        return result;
    }


    /**
     * Creates a string representation of an unorder collection difference.
     *
     * @param unorderedCollectionDifference The difference, not null
     * @param fieldName                     The current fieldName, null for root
     * @return The string representation, not null
     */
    protected String formatDifference(UnorderedCollectionDifference unorderedCollectionDifference, String fieldName) {
        String result = "";
        if (!outputtingUnorderedCollectionDifference) {
            String message = ++differenceCount + ") Different collections - Multiple possible matches";
            result = append("", message);
            result = append(result, repeat("-", message.length()));
            if (fieldName != null) {
                result = append(result, "Field: " + fieldName + "\n");
            }
            result = append(result, "Differences with best matches:");
            result = append(result, "Compared elements are indicated as [leftIndex,rightIndex]\n");
        }


        int currentDifferenceCount = differenceCount;

        Map<Integer, Map<Integer, Difference>> bestMatchingElementDifferences = bestMatchFinder.getBestMatches(unorderedCollectionDifference);
        for (Map.Entry<Integer, Map<Integer, Difference>> leftDifferences : bestMatchingElementDifferences.entrySet()) {
            int leftIndex = leftDifferences.getKey();
            for (Map.Entry<Integer, Difference> rightDifferences : leftDifferences.getValue().entrySet()) {
                int rightIndex = rightDifferences.getKey();
                Difference difference = rightDifferences.getValue();

                if (difference != null) {
                    String innerFieldName = "[" + leftIndex + "," + rightIndex + "]";

                    if (!outputtingUnorderedCollectionDifference) {
                        outputtingUnorderedCollectionDifference = true;
                        differenceCount = 0;

                        if (Difference.class.equals(difference.getClass())) {
                            result = append(result, "* " + innerFieldName);

                        } else {
                            String fieldString = "* " + innerFieldName;
                            result = append(result, fieldString + "   Left : " + objectFormatter.format(difference.getLeftValue()));
                            result = append(result, repeat(" ", fieldString.length()) + "   Right: " + objectFormatter.format(difference.getRightValue()));
                        }
                        indent++;

                        result += "\n" + difference.accept(differenceFormatterVisitor, null);
                        indent--;
                    } else {
                        if (fieldName != null) {
                            innerFieldName = fieldName + innerFieldName;
                        }
                        result += difference.accept(differenceFormatterVisitor, innerFieldName);
                    }
                }
            }
        }
        outputtingUnorderedCollectionDifference = false;
        differenceCount = currentDifferenceCount;
        return result;
    }


    /**
     * Appends a line to the given string and indents the line with the current indentation value.
     *
     * @param currentString The current string, not null
     * @param newString     The string to append, not null
     * @return The new string, not null
     */
    protected String append(String currentString, String newString) {
        String result = currentString;
        result += repeat("    ", indent) + newString + "\n";
        return result;
    }


    /**
     * The visitor for visiting the difference tree.
     */
    protected class DifferenceFormatterVisitor implements DifferenceVisitor<String, String> {

        public String visit(Difference difference, String fieldName) {
            return formatDifference(difference, fieldName);
        }

        public String visit(ObjectDifference objectDifference, String fieldName) {
            return formatDifference(objectDifference, fieldName);
        }

        public String visit(MapDifference mapDifference, String fieldName) {
            return formatDifference(mapDifference, fieldName);
        }

        public String visit(CollectionDifference collectionDifference, String fieldName) {
            return formatDifference(collectionDifference, fieldName);
        }

        public String visit(UnorderedCollectionDifference unorderedCollectionDifference, String fieldName) {
            return formatDifference(unorderedCollectionDifference, fieldName);
        }
    }


}