/*
 * Copyright 2006-2009,  Unitils.org
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
package org.unitils.dataset.comparison.impl;

import org.unitils.dataset.core.Row;

import java.util.*;

/**
 * The differences between 2 data set tables.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class TableComparison {

    /* The name of the table */
    private String name;

    /* True if expected no more records but found more records in the database */
    private boolean expectedNoMoreRecordsButFoundMore = false;

    /* The rows for which no other row was found in the actual table, empty if none found */
    private List<Row> missingRows = new ArrayList<Row>();

    /* The best differences of the comparisons between the rows of the tables with the data set row as key */
    private Map<Row, RowComparison> bestRowComparisons = new LinkedHashMap<Row, RowComparison>();

    private Set<Integer> actualRowIndexesWithMatch = new HashSet<Integer>();

    /**
     * Creates a table difference.
     *
     * @param name The table name, not null
     */
    public TableComparison(String name) {
        this.name = name;
    }


    /**
     * @return The name of the table, not null
     */
    public String getName() {
        return name;
    }

    /**
     * @return True if expected empty but not empty in the database
     */
    public boolean isExpectedNoMoreRecordsButFoundMore() {
        return expectedNoMoreRecordsButFoundMore;
    }

    /**
     * @return The rows for which no other row was found in the actual table, empty if none found
     */
    public List<Row> getMissingRows() {
        return missingRows;
    }

    /**
     * @param expectedNoMoreRecordsButFoundMore
     *         True if expected no more records but found more records in the database
     */
    public void setExpectedNoMoreRecordsButFoundMore(boolean expectedNoMoreRecordsButFoundMore) {
        this.expectedNoMoreRecordsButFoundMore = expectedNoMoreRecordsButFoundMore;
    }

    /**
     * Adds a rows for which no other row was found in the actual table.
     *
     * @param missingRow The missing row, not null
     */
    public void addMissingRow(Row missingRow) {
        missingRows.add(missingRow);
    }

    /**
     * @param row The row to check, not null
     * @return True if a match was found for the given row
     */
    public boolean hasMatch(Row row) {
        RowComparison rowComparison = bestRowComparisons.get(row);
        return rowComparison != null && rowComparison.isMatch();
    }

    /**
     * @param actualRowIndex The actual row index in the database >= 1
     * @return True if the actual row index was already used for a match
     */
    public boolean isActualRowIndexWithExactMatch(int actualRowIndex) {
        return actualRowIndexesWithMatch.contains(actualRowIndex);
    }

    /**
     * @return The best results in the comparison between the rows, not null
     */
    public List<RowComparison> getBestRowComparisons() {
        return new ArrayList<RowComparison>(bestRowComparisons.values());
    }

    /**
     * Sets the given difference as best row difference if it is better than the current best row difference.
     *
     * @param actualRowIndex The index of the actual row in the database >= 1
     * @param rowComparison  The comparison result, not null
     */
    public void replaceIfBetterRowComparison(int actualRowIndex, RowComparison rowComparison) {
        Row dataSetRow = rowComparison.getDataSetRow();
        RowComparison currentRowComparison = bestRowComparisons.get(dataSetRow);
        if (currentRowComparison == null || rowComparison.isBetterMatch(currentRowComparison)) {
            bestRowComparisons.put(dataSetRow, rowComparison);
            if (rowComparison.isMatch()) {
                actualRowIndexesWithMatch.add(actualRowIndex);
            }
        }
    }

    /**
     * @return True if both tables are a match
     */
    public boolean isMatch() {
        if (expectedNoMoreRecordsButFoundMore) {
            return false;
        }
        if (!missingRows.isEmpty()) {
            return false;
        }
        for (RowComparison rowComparison : bestRowComparisons.values()) {
            if (!rowComparison.isMatch()) {
                return false;
            }
        }
        return true;
    }

}