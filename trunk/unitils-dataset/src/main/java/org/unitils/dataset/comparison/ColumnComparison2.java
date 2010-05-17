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
package org.unitils.dataset.comparison;

import org.unitils.dataset.core.Column;

/**
 * The comparison result of 2 columns.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ColumnComparison2 {

    /* The name of the data set column */
    private Column column;
    /* The expected value */
    private String expectedValue;
    /* The expected value */
    private String actualValue;
    /* True if the both values are equal */
    private boolean equal;
    /* True if this column is part of a primary key */
    private boolean primaryKey;


    public ColumnComparison2(Column column, String expectedValue, String actualValue, boolean equal, boolean primaryKey) {
        this.column = column;
        this.expectedValue = expectedValue;
        this.actualValue = actualValue;
        this.equal = equal;
        this.primaryKey = primaryKey;
    }


    public Column getColumn() {
        return column;
    }

    public String getColumnName() {
        return column.getName();
    }

    public String getExpectedValue() {
        return expectedValue;
    }

    public String getActualValue() {
        return actualValue;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    /**
     * @return True if both columns are a match
     */
    public boolean isMatch() {
        return equal;
    }
}