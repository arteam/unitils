/*
 * Copyright 2009,  Unitils.org
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

import java.util.ArrayList;
import java.util.List;

/**
 * A data set row.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class Row {

    /* The columns of the row */
    private List<Column> columns = new ArrayList<Column>();

    /* The table this row belongs to */
    private Table table;

    /* The parent row that is referenced (foreign key) by this row, null if not defined */
    private Row parentRow;

    /* True if the row should not exist, false if it should exist */
    private boolean notExists;


    /**
     * Creates a data set row.
     */
    public Row() {
        this(null, false);
    }

    /**
     * Creates a data set row.
     *
     * @param parentRow The parent row that is referenced (foreign key) by this row, null if not defined
     * @param notExists True if the row should not exist, false if it should exist
     */
    public Row(Row parentRow, boolean notExists) {
        this.parentRow = parentRow;
        this.notExists = notExists;
    }


    /**
     * @param table The table this row belongs to, not null
     */
    void setTable(Table table) {
        this.table = table;
    }

    /**
     * @return The table this row belongs to, not null
     */
    public Table getTable() {
        return table;
    }

    /**
     * @return The parent row that is referenced (foreign key) by this row, null if not defined
     */
    public Row getParentRow() {
        return parentRow;
    }

    /**
     * @return True if the row should not exist, false if it should exist
     */
    public boolean isNotExists() {
        return notExists;
    }

    /**
     * Gets the column for the given name.
     *
     * @param columnName The name of the column, not null
     * @return The column, null if not found
     */
    public Column getColumn(String columnName) {
        for (Column column : columns) {
            if (column.hasName(columnName)) {
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
     * @return The nr of columns in the row >= 0
     */
    public int getNrOfColumns() {
        return columns.size();
    }

    /**
     * @return True if this row has no columns
     */
    public boolean isEmpty() {
        return columns.isEmpty();
    }

    /**
     * Adds a column to the row. A column can only be added once.
     *
     * @param column The column to add, not null
     * @throws org.unitils.core.UnitilsException
     *          When a value for the same column was already added
     */
    public void addColumn(Column column) {
        Column existingColumn = getColumn(column.getName());
        if (existingColumn != null) {
            throw new UnitilsException("Unable to add column to data set row. Duplicate column name: " + column.getName());
        }
        columns.add(column);
    }

    /**
     * @return The string representation of this row, not null
     */
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