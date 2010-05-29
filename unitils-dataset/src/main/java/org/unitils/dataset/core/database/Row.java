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
package org.unitils.dataset.core.database;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A database row.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class Row {

    /* The identifier of the row (e.g. primary keys or row number), null if undefined */
    private String identifier;
    /* The table name prefixed with the schema name and quoted if it is a case-sensitive name. */
    private String qualifiedTableName;
    /* The columns of the row */
    private Map<String, Value> valuesPerColumnName = new LinkedHashMap<String, Value>();


    /**
     * Creates a database row.
     *
     * @param qualifiedTableName The table name prefixed with the schema name and quoted if it is a case-sensitive name, not null
     */
    public Row(String qualifiedTableName) {
        this(null, qualifiedTableName);
    }

    /**
     * Creates a database row.
     *
     * @param identifier         The identifier of the row (e.g. primary keys or row number), null if undefined
     * @param qualifiedTableName The table name prefixed with the schema name and quoted if it is a case-sensitive name, not null
     */
    public Row(String identifier, String qualifiedTableName) {
        this.identifier = identifier;
        this.qualifiedTableName = qualifiedTableName;
    }


    /**
     * @return The identifier of the row (e.g. primary keys or row number), null if undefined
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * @return The table name prefixed with the schema name and quoted if it is a case-sensitive name, not null
     */
    public String getQualifiedTableName() {
        return qualifiedTableName;
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
        stringBuilder.append(qualifiedTableName);
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