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
 * Formatter that will output all objects in the difference tree. For an unordered collection difference,
 * the best matching differences are taken.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class TreeDifferenceFormatter implements DifferenceFormatter {

    /**
     * Formatter for object values.
     */
    protected ObjectFormatter objectFormatter = new ObjectFormatter();

    /**
     * The visitor for visiting the difference tree
     */
    protected TreeDifferenceFormatterVisitor treeDifferenceFormatterVisitor = new TreeDifferenceFormatterVisitor();

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
        return difference.accept(treeDifferenceFormatterVisitor, null);
    }


    /**
     * Creates a string representation of a simple difference.
     *
     * @param difference The difference, not null
     * @param fieldName  The current fieldName, null for root
     * @return The string representation, not null
     */
    protected String formatDifference(Difference difference, String fieldName) {
        String fieldNameString = fieldName == null ? "" : (fieldName + "   ");
        String result = fieldNameString + "[L] " + objectFormatter.format(difference.getLeftValue()) + "\n";
        result += repeat(" ", fieldNameString.length()) + "[R] " + objectFormatter.format(difference.getRightValue()) + "\n";
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
        String result = formatDifference((Difference) objectDifference, fieldName);

        for (Map.Entry<String, Difference> fieldDifference : objectDifference.getFieldDifferences().entrySet()) {
            String innerFieldName = createFieldName(fieldName, fieldDifference.getKey(), true);
            result += fieldDifference.getValue().accept(treeDifferenceFormatterVisitor, innerFieldName);
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
        String result = formatDifference((Difference) collectionDifference, fieldName);

        for (Map.Entry<Integer, Difference> elementDifferences : collectionDifference.getElementDifferences().entrySet()) {
            String innerFieldName = createFieldName(fieldName, "[" + elementDifferences.getKey() + "]", false);
            result += elementDifferences.getValue().accept(treeDifferenceFormatterVisitor, innerFieldName);
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
        String result = formatDifference((Difference) mapDifference, fieldName);

        for (Map.Entry<Object, Difference> valueDifference : mapDifference.getValueDifferences().entrySet()) {
            String innerFieldName = createFieldName(fieldName, objectFormatter.format(valueDifference.getKey()), true);
            result += valueDifference.getValue().accept(treeDifferenceFormatterVisitor, innerFieldName);
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
        String result = formatDifference((Difference) unorderedCollectionDifference, fieldName);

        Map<Integer, Map<Integer, Difference>> bestMatchingElementDifferences = bestMatchFinder.getBestMatches(unorderedCollectionDifference);
        for (Map.Entry<Integer, Map<Integer, Difference>> leftDifferences : bestMatchingElementDifferences.entrySet()) {
            int leftIndex = leftDifferences.getKey();
            for (Map.Entry<Integer, Difference> rightDifferences : leftDifferences.getValue().entrySet()) {
                int rightIndex = rightDifferences.getKey();

                Difference difference = rightDifferences.getValue();
                if (difference == null) {
                    continue;
                }

                String innerFieldName = createFieldName(fieldName, "[" + leftIndex + "," + rightIndex + "]", false);
                result += difference.accept(treeDifferenceFormatterVisitor, innerFieldName);
            }
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
    protected class TreeDifferenceFormatterVisitor implements DifferenceVisitor<String, String> {

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