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
 * A value of a data set column for which all variables and literal tokens were processed.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ProcessedColumn {

    /* The column name */
    private String name;
    /* The value */
    private String value;
    /* True if this value is a literal value */
    private boolean literalValue;
    /* True if the column is a primary key column */
    private boolean primaryKey;


    /**
     * @param name         The column name, not null
     * @param value        The value, not null
     * @param literalValue True if this value is a literal value
     * @param primaryKey   True if the column is a primary key column
     */
    public ProcessedColumn(String name, String value, boolean literalValue, boolean primaryKey) {
        this.name = name;
        this.value = value;
        this.literalValue = literalValue;
        this.primaryKey = primaryKey;
    }


    /**
     * @return The column name, not null
     */
    public String getName() {
        return name;
    }

    /**
     * @return The value, not null
     */
    public String getValue() {
        return value;
    }

    /**
     * @return True if this value is a literal value
     */
    public boolean isLiteralValue() {
        return literalValue;
    }

    /**
     * @return True if the column is a primary key column
     */
    public boolean isPrimaryKey() {
        return primaryKey;
    }
}