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
package org.unitils.dataset.assertstrategy.model;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ColumnDifference {

    /* The name of the database column */
    private String columnName;
    /* The actual value */
    private Object expectedValue;
    /* The expected value */
    private Object actualValue;
    /* True if this column is a primary key column */
    private boolean primaryKey;


    public ColumnDifference(String columnName, Object expectedValue, Object actualValue, boolean primaryKey) {
        this.columnName = columnName;
        this.expectedValue = expectedValue;
        this.actualValue = actualValue;
        this.primaryKey = primaryKey;
    }


    public String getColumnName() {
        return columnName;
    }

    public Object getExpectedValue() {
        return expectedValue;
    }

    public Object getActualValue() {
        return actualValue;
    }

    public String getExpectedValueAsString() {
        if (expectedValue == null) {
            return "<null>";
        }
        return expectedValue.toString();
    }

    public String getActualValueAsString() {
        if (actualValue == null) {
            actualValue = "<null>";
        }
        return actualValue.toString();
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }
}