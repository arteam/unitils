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

    /* The primary key columns, empty if none defined */
    private List<Column> primaryKeyColumns = new ArrayList<Column>();

    /* The columns of the row */
    private List<Column> columns = new ArrayList<Column>();


    /**
     * Gets the column for the given name. The name is case insensitive.
     *
     * @param columnName The name of the column, not null
     * @return The column, null if not found
     */
    public Column getColumn(String columnName) {
        for (Column primaryKeyColumn : primaryKeyColumns) {
            if (columnName.equalsIgnoreCase(primaryKeyColumn.getName())) {
                return primaryKeyColumn;
            }
        }
        for (Column column : columns) {
            if (columnName.equalsIgnoreCase(column.getName())) {
                return column;
            }
        }
        return null;
    }


    /**
     * @return The primary key columns, empty if none defined
     */
    public List<Column> getPrimaryKeyColumns() {
        return primaryKeyColumns;
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
     * @param primaryKeyColumn The column to add, not null
     * @throws UnitilsException When a value for the same column was already added
     */
    public void addPrimaryKeyColumn(Column primaryKeyColumn) {
        Column existingColumn = getColumn(primaryKeyColumn.getName());
        if (existingColumn != null) {
            throw new UnitilsException("Unable to add primary column to data set row. A column for this name already exists. Column name: " + primaryKeyColumn.getName() + ", existing value: " + existingColumn.getValue() + ", new value: " + primaryKeyColumn.getValue());
        }
        primaryKeyColumns.add(primaryKeyColumn);
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
     * @param actualRow The row to compare with, not null
     * @return True if the pk columns did not match
     */
    public boolean hasDifferentPrimaryKeyColumns(Row actualRow) {
        for (Column primaryKeyColumn : actualRow.getPrimaryKeyColumns()) {
            Column column = getColumn(primaryKeyColumn.getName());
            if (column != null && column.compare(primaryKeyColumn) != null) {
                return true;
            }
        }
        return false;
    }


    /**
     * Compares the row with the given actual row.
     *
     * @param actualRow The row to compare with, not null
     * @return The difference, null if none found
     */
    public RowDifference compare(Row actualRow) {
        RowDifference rowDifference = new RowDifference(this, actualRow);
        compareColumns(primaryKeyColumns, actualRow, rowDifference);
        compareColumns(columns, actualRow, rowDifference);

        if (rowDifference.isMatch()) {
            return null;
        }
        return rowDifference;
    }


    /**
     * Compares the given columns with the columns of the actual row.
     *
     * @param columns   The columns to compare, not null
     * @param actualRow The columns to compare with, not null
     * @param result    The result to add the differences to, not null
     */
    protected void compareColumns(List<Column> columns, Row actualRow, RowDifference result) {
        for (Column column : columns) {
            Column actualColumn = actualRow.getColumn(column.getName());
            if (actualColumn == null) {
                result.addMissingColumn(column);
            } else {
                ColumnDifference columnDifference = column.compare(actualColumn);
                if (columnDifference != null) {
                    result.addColumnDifference(columnDifference);
                }
            }
        }
    }

}
