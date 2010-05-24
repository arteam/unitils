/*
 * Copyright Unitils.org
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
package org.unitils.dataset.comparison;

import org.unitils.dataset.core.Row;

import java.util.*;

/**
 * The differences between 2 data set tables.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class TableComparison {

    /* The table name prefixed with the schema name and quoted if it is a case-sensitive name. */
    private String qualifiedTableName;

    /* True if expected no more records but found more records in the database */
    private boolean expectedNoMoreRecordsButFoundMore = false;

    /* The data set rows for which no database row was found, empty if none found */
    private List<Row> missingRows = new ArrayList<Row>();

    private List<Row> rowsThatShouldNotHaveMatched = new ArrayList<Row>();

    /* The best differences of the comparisons between the rows of the tables with the epxected database row as key */
    private Map<Row, RowComparison> bestRowComparisons = new LinkedHashMap<Row, RowComparison>();

    private Set<String> rowIdentifiersWithMatch = new HashSet<String>();


    /**
     * Creates a table difference.
     *
     * @param qualifiedTableName The table name prefixed with the schema name and quoted if it is a case-sensitive name, not null
     */
    public TableComparison(String qualifiedTableName) {
        this.qualifiedTableName = qualifiedTableName;
    }


    /**
     * @return The table name prefixed with the schema name and quoted if it is a case-sensitive name, not null
     */
    public String getQualifiedTableName() {
        return qualifiedTableName;
    }

    /**
     * @return True if expected empty but not empty in the database
     */
    public boolean isExpectedNoMoreRecordsButFoundMore() {
        return expectedNoMoreRecordsButFoundMore;
    }


    /**
     * @param expectedNoMoreRecordsButFoundMore
     *         True if expected no more records but found more records in the database
     */
    public void setExpectedNoMoreRecordsButFoundMore(boolean expectedNoMoreRecordsButFoundMore) {
        this.expectedNoMoreRecordsButFoundMore = expectedNoMoreRecordsButFoundMore;
    }

    /**
     * @return The data set rows for which no database row was found, empty if none found
     */
    public List<Row> getMissingRows() {
        return missingRows;
    }

    /**
     * Adds a data set row for which no database row was found.
     *
     * @param missingRow The missing row, not null
     */
    public void addMissingRow(Row missingRow) {
        missingRows.add(missingRow);
    }


    public List<Row> getRowsThatShouldNotHaveMatched() {
        return rowsThatShouldNotHaveMatched;
    }

    public void setMatchingRowThatShouldNotHaveMatched(Row row) {
        rowIdentifiersWithMatch.contains(row.getIdentifier());
        rowsThatShouldNotHaveMatched.add(row);
    }

    /**
     * @param rowIdentifier The identifier of the actual row in the database, not null
     * @return True if the actual row index was already used for a match
     */
    public boolean isMatchingRow(String rowIdentifier) {
        return rowIdentifiersWithMatch.contains(rowIdentifier);
    }

    public void setMatchingRow(RowComparison rowComparison) {
        bestRowComparisons.remove(rowComparison.getExpectedRow());
        rowIdentifiersWithMatch.add(rowComparison.getActualRow().getIdentifier());
    }

    /**
     * @return The best results in the comparison between the rows, not null
     */
    public List<RowComparison> getBestRowComparisons() {
        return new ArrayList<RowComparison>(bestRowComparisons.values());
    }

    /**
     * @param expectedRow The expected row, not null
     * @return The best comparison for the given rows, not null
     */
    public RowComparison getBestRowComparison(Row expectedRow) {
        return bestRowComparisons.get(expectedRow);
    }


    /**
     * Sets the given difference as best row difference if it is better than the current best row difference.
     *
     * @param rowComparison The comparison result, not null
     */
    public void replaceIfBetterRowComparison(RowComparison rowComparison) {
        Row expectedRow = rowComparison.getExpectedRow();
        RowComparison currentRowComparison = bestRowComparisons.get(expectedRow);
        if (currentRowComparison == null || rowComparison.isBetterMatch(currentRowComparison)) {
            bestRowComparisons.put(expectedRow, rowComparison);
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
        if (!rowsThatShouldNotHaveMatched.isEmpty()) {
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