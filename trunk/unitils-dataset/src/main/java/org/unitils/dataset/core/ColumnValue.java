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
 * A column value.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ColumnValue {

    /* The name of the column */
    private String columnName;
    /* The value */
    private Object value;
    /* The value as a string */
    private String valueAsString;
    /* The sql type of the column in the database */
    private int sqlType;


    /**
     * Creates a data set column
     *
     * @param columnName    The name of the column, not null
     * @param value         The value
     * @param valueAsString The value as a string, not null
     * @param sqlType       The sql type of the column in the database
     */
    public ColumnValue(String columnName, Object value, String valueAsString, int sqlType) {
        this.columnName = columnName;
        this.value = value;
        this.valueAsString = valueAsString;
        this.sqlType = sqlType;
    }


    /**
     * @return The name of the column, not null
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * @return The sql type of the column in the database
     */
    public int getSqlType() {
        return sqlType;
    }

    /**
     * @return The value
     */
    public Object getValue() {
        return value;
    }

    /**
     * @return The value as a string, not null
     */
    public String getValueAsString() {
        return valueAsString;
    }


    /**
     * @return The string representation of this column, not null
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(columnName);
        stringBuilder.append("=\"");
        stringBuilder.append(value);
        stringBuilder.append("\"");
        return stringBuilder.toString();
    }

}