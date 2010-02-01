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
import org.unitils.dataset.core.Table;

import java.util.*;

/**
 * The differences between 2 data set tables.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class TableComparison {

    /* The data set table that was compared */
    private Table dataSetTable;

    /* True if expected no more records but found more records in the database */
    private boolean expectedNoMoreRecordsButFoundMore = false;

    /* The rows for which no other row was found in the actual table, empty if none found */
    private List<Row> missingRows = new ArrayList<Row>();

    /* The best differences of the comparisons between the rows of the tables with the data set row as key */
    private Map<Row, RowComparison> bestRowComparisons = new LinkedHashMap<Row, RowComparison>();

    private Set<String> actualRowIdentifiersWithMatch = new HashSet<String>();


    /**
     * Creates a table difference.
     *
     * @param dataSetTable The data set table that was compared, not null
     */
    public TableComparison(Table dataSetTable) {
        this.dataSetTable = dataSetTable;
    }


    /**
     * @return The data set table that was compared, not null
     */
    public Table getDataSetTable() {
        return dataSetTable;
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
     * @param rowIdentifier The identifier of the actual row in the database, not null
     * @return True if the actual row index was already used for a match
     */
    public boolean isActualRowWithExactMatch(String rowIdentifier) {
        return actualRowIdentifiersWithMatch.contains(rowIdentifier);
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
     * @param rowIdentifier The identifier of the actual row in the database, not null
     * @param rowComparison The comparison result, not null
     */
    public void replaceIfBetterRowComparison(String rowIdentifier, RowComparison rowComparison) {
        Row dataSetRow = rowComparison.getDataSetRow();
        RowComparison currentRowComparison = bestRowComparisons.get(dataSetRow);
        if (currentRowComparison == null || rowComparison.isBetterMatch(currentRowComparison)) {
            bestRowComparisons.put(dataSetRow, rowComparison);
            if (rowComparison.isMatch()) {
                actualRowIdentifiersWithMatch.add(rowIdentifier);
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
            if (!rowComparison.isMatch() || rowComparison.shouldNotHaveMatched()) {
                return false;
            }
        }
        return true;
    }

}