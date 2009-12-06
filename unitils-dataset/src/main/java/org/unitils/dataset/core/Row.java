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
package org.unitils.dataset.core;

import org.unitils.core.UnitilsException;
import org.unitils.dataset.core.comparison.ColumnDifference;
import org.unitils.dataset.core.comparison.RowDifference;

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
    public Column getColumnIgnoreCase(String columnName) {
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
     * @return The nr of columns in the row >= 0
     */
    public int getNrOfColumns() {
        return columns.size();
    }

    /**
     * Adds a column to the row. A column can only be added once.
     *
     * @param primaryKeyColumn The column to add, not null
     * @throws org.unitils.core.UnitilsException
     *          When a value for the same column was already added
     */
    public void addPrimaryKeyColumn(Column primaryKeyColumn) {
        Column existingColumn = getColumnIgnoreCase(primaryKeyColumn.getName());
        if (existingColumn != null) {
            throw new UnitilsException("Unable to add primary column to data set row. Duplicate column name: " + primaryKeyColumn.getName());
        }
        primaryKeyColumns.add(primaryKeyColumn);
    }

    /**
     * Adds a column to the row. A column can only be added once.
     *
     * @param column The column to add, not null
     * @throws org.unitils.core.UnitilsException
     *          When a value for the same column was already added
     */
    public void addColumn(Column column) {
        Column existingColumn = getColumnIgnoreCase(column.getName());
        if (existingColumn != null) {
            throw new UnitilsException("Unable to add column to data set row. Duplicate column name: " + column.getName());
        }
        columns.add(column);
    }

    /**
     * @param actualRow The row to compare with, not null
     * @return True if the pk columns did not match
     */
    public boolean hasDifferentPrimaryKeyColumns(Row actualRow) {
        for (Column primaryKeyColumn : actualRow.getPrimaryKeyColumns()) {
            Column column = getColumnIgnoreCase(primaryKeyColumn.getName());
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
            Column actualColumn = actualRow.getColumnIgnoreCase(column.getName());
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

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Column column : columns) {
            stringBuilder.append(column);
            stringBuilder.append(", ");
        }
        if (stringBuilder.length() == 0) {
            stringBuilder.append("<empty row>");
        } else {
            stringBuilder.setLength(stringBuilder.length() - 2);
        }
        return stringBuilder.toString();
    }
}