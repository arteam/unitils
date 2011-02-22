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
package org.unitils.dataset.model.database;

import java.util.*;

/**
 * A database row.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class Row {

    /* The identifier of the row (e.g. primary keys or row number), null if undefined */
    private String identifier;
    /* The table name, not null */
    private TableName tableName;
    /* The columns of the row */
    private Map<String, Value> valuesPerColumnName = new LinkedHashMap<String, Value>();


    /**
     * Creates a database row.
     *
     * @param tableName The table name, not null
     */
    public Row(TableName tableName) {
        this(null, tableName);
    }

    /**
     * Creates a database row.
     *
     * @param identifier The identifier of the row (e.g. primary keys or row number), null if undefined
     * @param tableName  The table name, not null
     */
    public Row(String identifier, TableName tableName) {
        this.identifier = identifier;
        this.tableName = tableName;
    }


    /**
     * @return The identifier of the row (e.g. primary keys or row number), null if undefined
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * @return The table name, not null
     */
    public TableName getTableName() {
        return tableName;
    }

    public Set<String> getColumnNames() {
        return valuesPerColumnName.keySet();
    }

    /**
     * @return The values of the row, not null
     */
    public List<Value> getValues() {
        return new ArrayList<Value>(valuesPerColumnName.values());
    }

    /**
     * @param column The column for which to get the value, not null
     * @return The value, null if not found
     */
    public Value getValue(Column column) {
        return valuesPerColumnName.get(column.getName());
    }

    /**
     * Adds a column to the row.
     *
     * @param value The column to add, not null
     */
    public void addValue(Value value) {
        valuesPerColumnName.put(value.getColumn().getName(), value);
    }

    /**
     * @return True if this row has no columns
     */
    public boolean isEmpty() {
        return valuesPerColumnName.isEmpty();
    }


    /**
     * @return The string representation of this row, not null
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(tableName);
        stringBuilder.append(" [");
        for (Value value : valuesPerColumnName.values()) {
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