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
package org.unitils.dataset.comparison;

import org.unitils.dataset.core.Row;

import java.util.ArrayList;
import java.util.List;

/**
 * The comparison result of 2 data set rows.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class RowComparison {

    /* The data set row that was compared */
    private Row dataSetRow;

    /* The column comparison results */
    private List<ColumnComparison> columnComparisons = new ArrayList<ColumnComparison>();


    /**
     * Creates a row comparison result.
     *
     * @param dataSetRow The data set row that was compared, not null
     */
    public RowComparison(Row dataSetRow) {
        this.dataSetRow = dataSetRow;
    }


    /**
     * @return The data set row that was compared, not null
     */
    public Row getDataSetRow() {
        return dataSetRow;
    }

    /**
     * @return The differences between the rows, empty if there is a match
     */
    public List<ColumnComparison> getColumnComparisons() {
        return columnComparisons;
    }

    /**
     * Adds a column comparison result
     *
     * @param columnComparison The comparison, not null
     */
    public void addColumnComparison(ColumnComparison columnComparison) {
        columnComparisons.add(columnComparison);
    }

    /**
     * @return The nr of different primary key values, 0 if there is a match or no primary key
     */
    public int getNrOfPrimaryKeyDifferences() {
        int nrOfPrimaryKeyDifferences = 0;
        for (ColumnComparison columnComparison : columnComparisons) {
            if (columnComparison.isPrimaryKey() && !columnComparison.isMatch()) {
                nrOfPrimaryKeyDifferences++;
            }
        }
        return nrOfPrimaryKeyDifferences;
    }

    /**
     * @return The nr of differences, 0 if there is a match
     */
    public int getNrOfDifferences() {
        int nrOfDifferences = 0;
        for (ColumnComparison columnComparison : columnComparisons) {
            if (!columnComparison.isMatch()) {
                nrOfDifferences++;
            }
        }
        return nrOfDifferences;
    }

    /**
     * Returns true if this row comparison is a better match than the given one.
     * First we look at the nr of matching primary keys columns. If there are more matching PKs, the row
     * is considered to be a better match. If both rows have the same amount of matching PKs,
     * all the other columns are taken into account.
     *
     * @param rowComparison The result to compare with, not null
     * @return True if this comparison has less differences
     */
    public boolean isBetterMatch(RowComparison rowComparison) {
        if (getNrOfPrimaryKeyDifferences() == rowComparison.getNrOfPrimaryKeyDifferences()) {
            return getNrOfDifferences() < rowComparison.getNrOfDifferences();
        }
        return getNrOfPrimaryKeyDifferences() < rowComparison.getNrOfPrimaryKeyDifferences();
    }

    /**
     * @return True if no match should have been found for the row
     */
    public boolean shouldNotHaveMatched() {
        return dataSetRow.isNotExists();
    }

    /**
     * @return True if both rows are a match
     */
    public boolean isMatch() {
        for (ColumnComparison columnComparison : columnComparisons) {
            if (!columnComparison.isMatch()) {
                return false;
            }
        }
        return true;
    }
}