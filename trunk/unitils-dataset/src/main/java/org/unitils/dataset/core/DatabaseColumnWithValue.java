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

/**
 * A value of a data set column for which all variables and literal tokens were processed.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DatabaseColumnWithValue extends DatabaseColumn {

    /* The value */
    private Object value;
    /* True if this value is a literal value */
    private boolean literalValue;


    /**
     * @param columnName   The column name, not null
     * @param value        The value, not null
     * @param sqlType      The sql type of the column in the database
     * @param literalValue True if this value is a literal value
     * @param primaryKey   True if the column is a primary key column
     */
    public DatabaseColumnWithValue(String columnName, Object value, int sqlType, boolean literalValue, boolean primaryKey) {
        super(columnName, sqlType, primaryKey);
        this.value = value;
        this.literalValue = literalValue;
    }


    public boolean isEqualValue(DatabaseColumnWithValue otherDatabaseColumnWithValue) {
        Object otherValue = otherDatabaseColumnWithValue.getValue();
        if (value == otherValue) {
            return true;
        }
        return value != null && value.equals(otherValue);
    }

    /**
     * @return The value, not null
     */
    public Object getValue() {
        return value;
    }

    /**
     * @return True if this value is a literal value
     */
    public boolean isLiteralValue() {
        return literalValue;
    }


    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getColumnName());
        stringBuilder.append('=');
        stringBuilder.append(value);
        return stringBuilder.toString();
    }

}