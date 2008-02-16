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
 * Formatter that will output all leaf differences in the tree and, in case of an unordered collection difference,
 * the difference of all best matches.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DefaultDifferenceFormatter implements DifferenceFormatter {

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
        String result = formatTitle(difference.getMessage(), fieldName);
        result += formatLine("Left : " + objectFormatter.format(difference.getLeftValue()));
        result += formatLine("Right: " + objectFormatter.format(difference.getRightValue()) + "\n\n");
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
            String innerFieldName = createFieldName(fieldName, fieldDifference.getKey(), true);
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
            String innerFieldName = createFieldName(fieldName, "[" + elementDifferences.getKey() + "]", false);
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
            String innerFieldName = createFieldName(fieldName, objectFormatter.format(valueDifference.getKey()), true);
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
            result = formatTitle("Different collections - Differences with best matches", fieldName);
        }

        Map<Integer, Map<Integer, Difference>> bestMatchingElementDifferences = bestMatchFinder.getBestMatches(unorderedCollectionDifference);
        for (Map.Entry<Integer, Map<Integer, Difference>> leftDifferences : bestMatchingElementDifferences.entrySet()) {
            int leftIndex = leftDifferences.getKey();
            for (Map.Entry<Integer, Difference> rightDifferences : leftDifferences.getValue().entrySet()) {
                int rightIndex = rightDifferences.getKey();

                Difference difference = rightDifferences.getValue();
                if (difference == null) {
                    continue;
                }

                String elementName = "[" + leftIndex + "," + rightIndex + "]";
                if (outputtingUnorderedCollectionDifference) {
                    String innerFieldName = createFieldName(fieldName, elementName, false);
                    result += difference.accept(differenceFormatterVisitor, innerFieldName);
                    continue;
                }

                outputtingUnorderedCollectionDifference = true;
                if (Difference.class.equals(difference.getClass())) {
                    result += formatLine("* " + elementName);
                } else {
                    result += formatLine("* " + elementName + "   Left : " + objectFormatter.format(difference.getLeftValue()));
                    result += formatLine(repeat(" ", elementName.length() + 2) + "   Right: " + objectFormatter.format(difference.getRightValue()));
                }

                indent++;
                result += "\n" + difference.accept(differenceFormatterVisitor, null);
                indent--;
                outputtingUnorderedCollectionDifference = false;
            }
        }
        return result;
    }


    /**
     * Formats a string by applying the correct indentation and adding a new line.
     *
     * @param text The text, not null
     * @return The formatted line, not null
     */
    protected String formatLine(String text) {
        return repeat("     ", indent) + text + "\n";
    }


    /**
     * Appends a title to the given string and indents the line with the current indentation value.
     * A number is prefixed when needed
     *
     * @param title     The title to append, not null
     * @param fieldName The fieldName, null for no field name
     * @return The new string, not null
     */
    protected String formatTitle(String title, String fieldName) {
        String result = formatLine(title);
        result += formatLine(repeat("-", title.length()));

        if (fieldName != null) {
            result += formatLine("Field: " + fieldName + "\n");
        }
        return result;
    }


    /**
     * Adds the inner field name to the given field name.
     *
     * @param fieldName      The field
     * @param innerFieldName The field to append, not null
     * @param includePoint   True if a point should be added
     * @return The field name
     */
    protected String createFieldName(String fieldName, String innerFieldName, boolean includePoint) {
        if (fieldName == null) {
            return innerFieldName;
        }
        String result = fieldName;
        if (includePoint) {
            result += ".";
        }
        return result + innerFieldName;
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