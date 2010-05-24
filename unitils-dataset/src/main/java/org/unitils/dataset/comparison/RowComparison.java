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

import org.unitils.dataset.core.DatabaseColumn;
import org.unitils.dataset.core.DatabaseRow;
import org.unitils.dataset.core.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class RowComparison {

    private DatabaseRow expectedDatabaseRow;

    private DatabaseRow actualDatabaseRow;

    /* The column comparison results */
    private List<ColumnDifference> columnDifferences = new ArrayList<ColumnDifference>();


    public RowComparison(DatabaseRow expectedDatabaseRow, DatabaseRow actualDatabaseRow) {
        this.expectedDatabaseRow = expectedDatabaseRow;
        this.actualDatabaseRow = actualDatabaseRow;
        this.columnDifferences = createColumnDifferences();
    }


    public DatabaseRow getExpectedDatabaseRow() {
        return expectedDatabaseRow;
    }

    public DatabaseRow getActualDatabaseRow() {
        return actualDatabaseRow;
    }

    /**
     * @return The differences between the rows, empty if there is a match
     */
    public List<ColumnDifference> getColumnDifferences() {
        return columnDifferences;
    }

    /**
     * @return The nr of different primary key values, 0 if there is a match or no primary key
     */
    public int getNrOfPrimaryKeyDifferences() {
        int nrOfPrimaryKeyDifferences = 0;
        for (ColumnDifference columnDifference : columnDifferences) {
            if (columnDifference.isPrimaryKey()) {
                nrOfPrimaryKeyDifferences++;
            }
        }
        return nrOfPrimaryKeyDifferences;
    }

    /**
     * @return The nr of differences, 0 if there is a match
     */
    public int getNrOfDifferences() {
        return columnDifferences.size();
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
     * @return True if both rows are a match
     */
    public boolean isMatch() {
        return columnDifferences.isEmpty();
    }


    protected List<ColumnDifference> createColumnDifferences() {
        List<ColumnDifference> columnDifferences = new ArrayList<ColumnDifference>();

        for (Value expectedDatabaseColumnWithValue : expectedDatabaseRow.getDatabaseColumnsWithValue()) {
            DatabaseColumn databaseColumn = expectedDatabaseColumnWithValue.getDatabaseColumn();
            Value actualDatabaseColumnsWithValue = actualDatabaseRow.getDatabaseColumnsWithValue(databaseColumn);
            if (!expectedDatabaseColumnWithValue.isEqualValue(actualDatabaseColumnsWithValue)) {
                boolean primaryKey = databaseColumn.isPrimaryKey();
                String columnName = databaseColumn.getColumnName();
                Object expectedValue = expectedDatabaseColumnWithValue.getValue();
                Object actualValue = actualDatabaseColumnsWithValue.getValue();
                columnDifferences.add(new ColumnDifference(columnName, expectedValue, actualValue, primaryKey));
            }
        }
        return columnDifferences;
    }
}