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
package org.unitils.dataset.model.dataset;

import org.unitils.core.UnitilsException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * A data set row.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DataSetRow {

    /* The columns of the row */
    private List<DataSetValue> dataSetValues = new ArrayList<DataSetValue>();

    /* The name of the schema, null for the default schema */
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
     * @param schemaName      The name of the schema, null for the default schema
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
     * @return The name of the schema, null for the default schema
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
    public DataSetValue getDataSetColumn(String columnName) {
        boolean caseSensitive = dataSetSettings.isCaseSensitive();
        for (DataSetValue dataSetValue : dataSetValues) {
            if (dataSetValue.hasName(columnName, caseSensitive)) {
                return dataSetValue;
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
    public DataSetValue removeColumn(String columnName) {
        boolean caseSensitive = getDataSetSettings().isCaseSensitive();
        Iterator<DataSetValue> iterator = dataSetValues.iterator();
        while (iterator.hasNext()) {
            DataSetValue value = iterator.next();
            if (value.hasName(columnName, caseSensitive)) {
                iterator.remove();
                return value;
            }
        }
        return null;
    }


    /**
     * @return The columns of the row, not null
     */
    public List<DataSetValue> getColumns() {
        return dataSetValues;
    }

    /**
     * @return The nr of columns in the row >= 0
     */
    public int getNrOfColumns() {
        return dataSetValues.size();
    }

    /**
     * @return True if this row has no columns
     */
    public boolean isEmpty() {
        return dataSetValues.isEmpty();
    }

    /**
     * Adds a column to the row. A column can only be added once.
     *
     * @param value The column to add, not null
     * @throws org.unitils.core.UnitilsException
     *          When a value for the same column was already added
     */
    public void addDataSetValue(DataSetValue value) {
        DataSetValue existingValue = getDataSetColumn(value.getColumnName());
        if (existingValue != null) {
            throw new UnitilsException("Unable to add column to data set row. Duplicate column name: " + value.getColumnName());
        }
        dataSetValues.add(value);
    }

    /**
     * @return The string representation of this row, not null
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (!isBlank(schemaName)) {
            stringBuilder.append(schemaName);
            stringBuilder.append(".");
        }
        stringBuilder.append(tableName);
        stringBuilder.append(" [");
        for (DataSetValue value : dataSetValues) {
            stringBuilder.append(value);
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