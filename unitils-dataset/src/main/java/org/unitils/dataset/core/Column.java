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

/**
 * A column in a data set row
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class Column {

    /* The name of the data set column */
    private String name;

    /* The original value */
    private String value;

    /* True if the column name is case sensitive */
    private boolean caseSensitive;


    /**
     * Creates a data set column
     *
     * @param name          The name of the data set column, not null
     * @param value         The value, not null
     * @param caseSensitive True if the table name is case sensitive
     */
    public Column(String name, String value, boolean caseSensitive) {
        this.name = name;
        this.value = value;
        this.caseSensitive = caseSensitive;
    }


    /**
     * @return The name of the data set column, not null
     */
    public String getName() {
        return name;
    }

    /**
     * @return True if the table name is case sensitive
     */
    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    /**
     * @param columnName The name to compare with, not null
     * @return True if the given column name is equal to the name of this column respecting case sensitivity
     */
    public boolean hasName(String columnName) {
        if (caseSensitive) {
            return name.equals(columnName);
        }
        return name.equalsIgnoreCase(columnName);
    }

    /**
     * @return The original value from the data set, not null
     */
    public String getOriginalValue() {
        return value;
    }


    /**
     * @return The string representation of this column, not null
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(name);
        stringBuilder.append("=\"");
        stringBuilder.append(value);
        stringBuilder.append("\"");
        return stringBuilder.toString();
    }

}