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
public class DatabaseRow {

    private String identifier;

    /* The table name prefixed with the schema name and quoted if it is a case-sensitive name. */
    private String qualifiedTableName;

    /* The columns of the row */
    private Map<String, DatabaseColumnWithValue> databaseColumnsWithValuePerName = new LinkedHashMap<String, DatabaseColumnWithValue>();

    /**
     * Creates a database row.
     *
     * @param qualifiedTableName The table name prefixed with the schema name and quoted if it is a case-sensitive name, not null
     */
    public DatabaseRow(String qualifiedTableName) {
        this(null, qualifiedTableName);
    }

    /**
     * Creates a database row.
     *
     * @param qualifiedTableName The table name prefixed with the schema name and quoted if it is a case-sensitive name, not null
     */
    public DatabaseRow(String identifier, String qualifiedTableName) {
        this.identifier = identifier;
        this.qualifiedTableName = qualifiedTableName;
    }

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
     * @return The columns of the row, not null
     */
    public List<DatabaseColumnWithValue> getDatabaseColumnsWithValue() {
        return new ArrayList<DatabaseColumnWithValue>(databaseColumnsWithValuePerName.values());
    }

    /**
     * @param databaseColumn The column to get, not null
     * @return The column with value, null if not found
     */
    public DatabaseColumnWithValue getDatabaseColumnsWithValue(DatabaseColumn databaseColumn) {
        return databaseColumnsWithValuePerName.get(databaseColumn.getColumnName());
    }

    /**
     * Adds a column to the row.
     *
     * @param databaseColumnWithValue The column to add, not null
     */
    public void addDatabaseColumnWithValue(DatabaseColumnWithValue databaseColumnWithValue) {
        databaseColumnsWithValuePerName.put(databaseColumnWithValue.getColumnName(), databaseColumnWithValue);
    }


    /**
     * @return The nr of columns in the row >= 0
     */
    public int getNrOfColumns() {
        return databaseColumnsWithValuePerName.size();
    }

    /**
     * @return True if this row has no columns
     */
    public boolean isEmpty() {
        return databaseColumnsWithValuePerName.isEmpty();
    }

    /**
     * @return The string representation of this row, not null
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(qualifiedTableName);
        stringBuilder.append(" [");
        for (DatabaseColumnWithValue databaseColumn : databaseColumnsWithValuePerName.values()) {
            stringBuilder.append(databaseColumn);
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