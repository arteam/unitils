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
package org.unitils.dataset.core;

import org.unitils.core.UnitilsException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A data set row.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DataSetRow {

    /* The columns of the row */
    private List<DataSetColumn> columns = new ArrayList<DataSetColumn>();

    /* The name of the schema */
    private String schemaName;
    /* The name of the table */
    private String tableName;
    /* The parent row that is referenced (foreign key) by this row, null if not defined */
    private DataSetRow parentRow;
    /* True if the row should not exist, false if it should exist */
    private boolean notExists;
    /* The settings for the data set */
    private DataSetSettings dataSetSettings;


    /**
     * Creates a data set row.
     *
     * @param schemaName      The name of the schema, not null
     * @param tableName       The name of the table, not null
     * @param parentRow       The parent row that is referenced (foreign key) by this row, null if not defined
     * @param notExists       True if the row should not exist, false if it should exist
     * @param dataSetSettings The settings for the data set, not null
     */
    public DataSetRow(String schemaName, String tableName, DataSetRow parentRow, boolean notExists, DataSetSettings dataSetSettings) {
        this.schemaName = schemaName;
        this.tableName = tableName;
        this.parentRow = parentRow;
        this.notExists = notExists;
        this.dataSetSettings = dataSetSettings;
    }


    /**
     * @return The name of the schema, not null
     */
    public String getSchemaName() {
        return schemaName;
    }

    /**
     * @return The name of the schema, not null
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @return The parent row that is referenced (foreign key) by this row, null if not defined
     */
    public DataSetRow getParentRow() {
        return parentRow;
    }

    /**
     * @return True if the row should not exist, false if it should exist
     */
    public boolean isNotExists() {
        return notExists;
    }

    /**
     * @return The settings for the data set, not null
     */
    public DataSetSettings getDataSetSettings() {
        return dataSetSettings;
    }

    /**
     * Gets the column for the given name.
     *
     * @param columnName The name of the column, not null
     * @return The column, null if not found
     */
    public DataSetColumn getDataSetColumn(String columnName) {
        boolean caseSensitive = getDataSetSettings().isCaseSensitive();
        for (DataSetColumn dataSetColumn : columns) {
            if (dataSetColumn.hasName(columnName, caseSensitive)) {
                return dataSetColumn;
            }
        }
        return null;
    }

    /**
     * Removes the column for the given name.
     *
     * @param columnName The name of the column, not null
     * @return The column that was removed, null if not found
     */
    public DataSetColumn removeColumn(String columnName) {
        boolean caseSensitive = getDataSetSettings().isCaseSensitive();
        Iterator<DataSetColumn> iterator = columns.iterator();
        while (iterator.hasNext()) {
            DataSetColumn column = iterator.next();
            if (column.hasName(columnName, caseSensitive)) {
                iterator.remove();
                return column;
            }
        }
        return null;
    }


    /**
     * @return The columns of the row, not null
     */
    public List<DataSetColumn> getColumns() {
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
    public void addDataSetColumn(DataSetColumn column) {
        DataSetColumn existingColumn = getDataSetColumn(column.getName());
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
        stringBuilder.append(schemaName);
        stringBuilder.append(".");
        stringBuilder.append(tableName);
        stringBuilder.append(" [");
        for (DataSetColumn column : columns) {
            stringBuilder.append(column);
            stringBuilder.append(", ");
        }
        if (stringBuilder.length() == 0) {
            stringBuilder.append("<empty row>");
        } else {
            stringBuilder.setLength(stringBuilder.length() - 2);
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

}