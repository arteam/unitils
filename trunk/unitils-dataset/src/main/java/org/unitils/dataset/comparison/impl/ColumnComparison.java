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
package org.unitils.dataset.comparison.impl;

import org.unitils.dataset.core.Column;

/**
 * The comparison result of 2 columns.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ColumnComparison {

    /* The name of the data set column */
    private Column column;

    /* The expected value */
    private String expectedValue;

    /* The expected value */
    private String actualValue;


    public ColumnComparison(Column column, String expectedValue, String actualValue) {
        this.column = column;
        this.expectedValue = expectedValue;
        this.actualValue = actualValue;
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

    /**
     * @return True if both columns are a match
     */
    public boolean isMatch() {
        return (expectedValue == null && actualValue == null) || (expectedValue != null && expectedValue.equals(actualValue));
    }
}