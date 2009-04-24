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
package org.unitils.dbunit.dataset.comparison;

import org.unitils.dbunit.dataset.Row;

import java.util.ArrayList;
import java.util.List;

/**
 * The difference between 2 data set rows.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class RowDifference {

    /* The expected row, not null */
    private Row row;

    /* The actual row, not null */
    private Row actualRow;

    /* The differences between the rows, empty if there is a match */
    private List<ValueDifference> valueDifferences = new ArrayList<ValueDifference>();


    /**
     * Create a comparison result.
     *
     * @param row       The expected row, not null
     * @param actualRow The actual row, null if the row was not found
     */
    public RowDifference(Row row, Row actualRow) {
        this.row = row;
        this.actualRow = actualRow;
    }


    /**
     * @return The expected row, not null
     */
    public Row getRow() {
        return row;
    }


    /**
     * @return The actual row, null if the row was not found
     */
    public Row getActualRow() {
        return actualRow;
    }


    /**
     * @return The differences between the rows, empty if there is a match
     */
    public List<ValueDifference> getValueDifferences() {
        return valueDifferences;
    }


    /**
     * @param columnName The column to find the difference for, not null
     * @return The differences of that column, null if not found
     */
    public ValueDifference getValueDifference(String columnName) {
        for (ValueDifference valueDifference : valueDifferences) {
            if (columnName.equals(valueDifference.getValue().getColumnName())) {
                return valueDifference;
            }
        }
        return null;
    }

    /**
     * Adds a difference for a column
     *
     * @param valueDifference The difference, not null
     */
    public void addValueDifference(ValueDifference valueDifference) {
        valueDifferences.add(valueDifference);
    }


    /**
     * @param rowComparison The result to compare with, not null
     * @return True if the given result has less differences
     */
    public boolean isBetterMatch(RowDifference rowComparison) {
        return valueDifferences.size() < rowComparison.getValueDifferences().size();
    }


    /**
     * @return True if both rows are a match
     */
    public boolean isMatch() {
        return valueDifferences.isEmpty();
    }


}
