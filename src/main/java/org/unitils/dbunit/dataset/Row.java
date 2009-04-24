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
import org.unitils.dbunit.dataset.comparison.RowDifference;
import org.unitils.dbunit.dataset.comparison.ValueDifference;

import java.util.ArrayList;
import java.util.List;

/**
 * A data set row
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class Row {

    /* The values of the row */
    private List<Value> values = new ArrayList<Value>();

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
     * Adds a value to the row. Only one value per column can be added.
     *
     * @param value The value to add, not null
     * @throws UnitilsException When a value for the same column was already added
     */
    public void addValue(Value value) {
        Value existingValue = getValue(value.getColumnName());
        if (existingValue != null) {
            throw new UnitilsException("Unable to add value to data set row. A value for this column already exists. Column name: " + value.getColumnName() + ", existing value: " + existingValue.getValue() + ", new value: " + value.getValue());
        }
        values.add(value);
    }


    /**
     * Compares the row with the given actual row.
     *
     * @param actualRow The row to compare with, not null
     * @return The difference, null the pk columns did not match
     */
    public boolean canCompare(Row actualRow) {
        for (String primaryKeyColumnName : primaryKeyColumnNames) {
            Value value = getValue(primaryKeyColumnName);
            Value actualValue = actualRow.getValue(primaryKeyColumnName);
            if (value != null && !value.equalValue(actualValue)) {
                return false;
            }
        }
        return true;
    }


    /**
     * Compares the row with the given actual row.
     *
     * @param actualRow The row to compare with, not null
     * @return The difference, null if none found
     */
    public RowDifference compare(Row actualRow) {
        RowDifference rowDifference = new RowDifference(this, actualRow);
        for (Value value : values) {
            String columnName = value.getColumnName();

            Value actualValue = actualRow.getValue(columnName);
            if (actualValue == null || !value.equalValue(actualValue)) {
                rowDifference.addValueDifference(new ValueDifference(value, actualValue));
            }
        }
        if (rowDifference.isMatch()) {
            return null;
        }
        return rowDifference;
    }


    /**
     * Gets the value for the given column.
     *
     * @param columnName The column, not null
     * @return The value, null if the column is not found
     */
    public Value getValue(String columnName) {
        for (Value value : values) {
            if (columnName.equalsIgnoreCase(value.getColumnName())) {
                return value;
            }
        }
        return null;
    }


    /**
     * @return The values of the row, not null
     */
    public List<Value> getValues() {
        return values;
    }
}
