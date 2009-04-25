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
package org.unitils.dbunit.dataset;

import org.unitils.core.UnitilsException;
import org.unitils.dbunit.dataset.comparison.ColumnDifference;
import org.unitils.dbunit.dataset.comparison.RowDifference;

import java.util.ArrayList;
import java.util.List;

/**
 * A data set row
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class Row {

    /* The columns of the row */
    private List<Column> columns = new ArrayList<Column>();

    /* The names of the primary key columns, empty if none defined */
    private List<String> primaryKeyColumnNames;


    /**
     * Creates a row.
     */
    public Row() {
        this(new ArrayList<String>());
    }


    /**
     * Creates a row.
     *
     * @param primaryKeyColumnNames The names of the primary key columns, empty if none defined
     */
    public Row(List<String> primaryKeyColumnNames) {
        this.primaryKeyColumnNames = primaryKeyColumnNames;
    }


    /**
     * @return The names of the primary key columns, empty if none defined
     */
    public List<String> getPrimaryKeyColumnNames() {
        return primaryKeyColumnNames;
    }


    /**
     * Compares the row with the given actual row.
     *
     * @param actualRow The row to compare with, not null
     * @return The difference, null the pk columns did not match
     */
    public boolean canCompare(Row actualRow) {
        for (String primaryKeyColumnName : primaryKeyColumnNames) {
            Column column = getColumn(primaryKeyColumnName);
            Column actualColumn = actualRow.getColumn(primaryKeyColumnName);
            if (column != null && column.compare(actualColumn) != null) {
                return false;
            }
        }
        return true;
    }


    /**
     * Gets the column for the given name.
     *
     * @param columnName The name of the column, not null
     * @return The column, null if not found
     */
    public Column getColumn(String columnName) {
        for (Column column : columns) {
            if (columnName.equalsIgnoreCase(column.getName())) {
                return column;
            }
        }
        return null;
    }


    /**
     * @return The columns of the row, not null
     */
    public List<Column> getColumns() {
        return columns;
    }


    /**
     * Adds a column to the row. A column can only be added once.
     *
     * @param column The column to add, not null
     * @throws UnitilsException When a value for the same column was already added
     */
    public void addColumn(Column column) {
        Column existingColumn = getColumn(column.getName());
        if (existingColumn != null) {
            throw new UnitilsException("Unable to add column to data set row. A column for this name already exists. Column name: " + column.getName() + ", existing value: " + existingColumn.getValue() + ", new value: " + column.getValue());
        }
        columns.add(column);
    }


    /**
     * Compares the row with the given actual row.
     *
     * @param actualRow The row to compare with, not null
     * @return The difference, null if none found
     */
    public RowDifference compare(Row actualRow) {
        RowDifference rowDifference = new RowDifference(this, actualRow);
        for (Column column : columns) {
            String columnName = column.getName();

            Column actualColumn = actualRow.getColumn(columnName);
            if (actualColumn == null) {
                rowDifference.addMissingColumn(column);
            } else {
                ColumnDifference columnDifference = column.compare(actualColumn);
                if (columnDifference != null) {
                    rowDifference.addColumnDifference(columnDifference);
                }
            }
        }
        if (rowDifference.isMatch()) {
            return null;
        }
        return rowDifference;
    }

}
