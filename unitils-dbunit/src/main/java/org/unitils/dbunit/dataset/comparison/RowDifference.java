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

import org.unitils.dbunit.dataset.Column;
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

    /* The columns that were not found in the actual row, empty if none found */
    private List<Column> missingColumns = new ArrayList<Column>();

    /* The differences between the rows, empty if there is a match */
    private List<ColumnDifference> columnDifferences = new ArrayList<ColumnDifference>();


    /**
     * Create a row difference.
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
    public List<ColumnDifference> getColumnDifferences() {
        return columnDifferences;
    }


    /**
     * @return The columns that were not found in the actual row, empty if none found
     */
    public List<Column> getMissingColumns() {
        return missingColumns;
    }


    /**
     * @param columnName The column to find the difference for, not null
     * @return The differences of that column, null if not found
     */
    public ColumnDifference getColumnDifference(String columnName) {
        for (ColumnDifference columnDifference : columnDifferences) {
            if (columnName.equals(columnDifference.getColumn().getName())) {
                return columnDifference;
            }
        }
        return null;
    }


    /**
     * Adds a difference for a column
     *
     * @param columnDifference The difference, not null
     */
    public void addColumnDifference(ColumnDifference columnDifference) {
        columnDifferences.add(columnDifference);
    }


    /**
     * Adds a column that was not found in the actual row
     *
     * @param column The missing column, not null
     */
    public void addMissingColumn(Column column) {
        missingColumns.add(column);
    }


    /**
     * @param rowComparison The result to compare with, not null
     * @return True if the given result has less differences
     */
    public boolean isBetterMatch(RowDifference rowComparison) {
        return (columnDifferences.size() + missingColumns.size()) < (rowComparison.getMissingColumns().size() + rowComparison.getColumnDifferences().size());
    }


    /**
     * @return True if both rows are a match
     */
    public boolean isMatch() {
        return columnDifferences.isEmpty() && missingColumns.isEmpty();
    }
}
