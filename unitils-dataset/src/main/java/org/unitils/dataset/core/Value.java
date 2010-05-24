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
public class Value {

    /* The value */
    private Object value;
    /* True if this value is a literal value */
    private boolean literalValue;
    private DatabaseColumn databaseColumn;


    /**
     * @param value          The value, not null
     * @param literalValue   True if this value is a literal value
     * @param databaseColumn The column that this value is for, not null
     */
    public Value(Object value, boolean literalValue, DatabaseColumn databaseColumn) {
        this.value = value;
        this.literalValue = literalValue;
        this.databaseColumn = databaseColumn;
    }


    public boolean isEqualValue(Value otherDatabaseColumnWithValue) {
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

    public DatabaseColumn getDatabaseColumn() {
        return databaseColumn;
    }


    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(databaseColumn.getColumnName());
        stringBuilder.append('=');
        stringBuilder.append(value);
        return stringBuilder.toString();
    }

}