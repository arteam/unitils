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
 * A column in a data set row
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DataSetColumn {

    /* The name of the data set column */
    private String name;
    /* The data set value */
    private String value;


    /**
     * Creates a data set column
     *
     * @param name  The name of the data set column, not null
     * @param value The value, not null
     */
    public DataSetColumn(String name, String value) {
        this.name = name;
        this.value = value;
    }


    /**
     * @return The name of the data set column, not null
     */
    public String getName() {
        return name;
    }

    /**
     * @param columnName    The name to compare with, not null
     * @param caseSensitive True if the column name is case sensitive
     * @return True if the given column name is equal to the name of this column respecting case sensitivity
     */
    public boolean hasName(String columnName, boolean caseSensitive) {
        if (caseSensitive) {
            return name.equals(columnName);
        }
        return name.equalsIgnoreCase(columnName);
    }


    /**
     * @return The value from the data set, not null
     */
    public String getValue() {
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