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

import java.util.List;


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

    /* True if the table name is case sensitive */
    private boolean caseSensitive;

    /* The token that identifies a literal, e.g. for '=literal value' */
    private char literalToken;

    /* The token that identifies a variable, e.g. $ for $0 $1 etc */
    private char variableToken;


    /**
     * Creates a value
     *
     * @param name          The name of the data set column, not null
     * @param value         The value, not null
     * @param caseSensitive True if the table name is case sensitive
     * @param literalToken  The token that identifies a literal, e.g. for '=literal value'
     * @param variableToken The token that identifies a variable, e.g. $ for $0 $1 etc
     */
    public Column(String name, String value, boolean caseSensitive, char literalToken, char variableToken) {
        this.name = name;
        this.value = value;
        this.caseSensitive = caseSensitive;
        this.literalToken = literalToken;
        this.variableToken = variableToken;
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
     * @return The token that identifies a literal, e.g. for '=literal value'
     */
    public char getLiteralToken() {
        return literalToken;
    }

    /**
     * @return The token that identifies a variable, e.g. $ for $0 $1 etc
     */
    public char getVariableToken() {
        return variableToken;
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
     * @param value The value, not null
     * @return True if the given value is a literal value
     */
    public boolean isLiteralValue(String value) {
        return value.startsWith("" + literalToken) && !isEscapedLiteralValue(value);
    }

    /**
     * @param value The value, not null
     * @return True if the given value is an escaped literal value
     */
    public boolean isEscapedLiteralValue(String value) {
        return value.startsWith("" + literalToken + literalToken);
    }

    /**
     * Gets the value, filling in the variable declarations using the given variables.
     * The first variable replaces $0, the second $1 etc.
     * If there are not enough variable values, the remaining declaration will not be replaced.
     * If the value is an escaped literal, the escaped token is replaced.
     *
     * @param variables The variable values, not null
     * @return the processed value, not null
     */
    public Value getValue(List<String> variables) {
        String valueWithVariablesFilledIn = getValueWithVariablesFilledIn(variables);
        if (isLiteralValue(valueWithVariablesFilledIn)) {
            return getLiteralValue(valueWithVariablesFilledIn);
        }
        if (isEscapedLiteralValue(valueWithVariablesFilledIn)) {
            return getEscapedLiteralValue(valueWithVariablesFilledIn);
        }
        return new Value(valueWithVariablesFilledIn, false);
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


    /**
     * @param variables The variable values, not null
     * @return The value with the variable declarations replaced by the given variables, not null
     */
    protected String getValueWithVariablesFilledIn(List<String> variables) {
        StringBuilder valueStringBuilder = new StringBuilder(value);
        for (int variableIndex = 0; variableIndex < variables.size(); variableIndex++) {
            replaceVariableDeclaration(valueStringBuilder, variableIndex, variables.get(variableIndex));
        }
        return valueStringBuilder.toString();
    }

    /**
     * @param valueStringBuilder The value in which to replace the variable declaration
     * @param variableIndex      The variable index, >=0
     * @param variable           The variable value to use, not null
     */
    protected void replaceVariableDeclaration(StringBuilder valueStringBuilder, int variableIndex, String variable) {
        String variableDeclaration = "" + variableToken + variableIndex;
        int index = 0;
        while ((index = valueStringBuilder.indexOf(variableDeclaration, index)) != -1) {
            if (index > 0 && valueStringBuilder.charAt(index - 1) == variableToken) {
                valueStringBuilder.deleteCharAt(index - 1);
            } else {
                valueStringBuilder.replace(index, index + 2, variable);
            }
        }
    }

    /**
     * @param value The value, not null
     * @return The literal value for the given value, not null
     */
    protected Value getLiteralValue(String value) {
        String result = value.substring(1);
        return new Value(result, true);
    }

    /**
     * @param value The value, not null
     * @return The escaped literal value for the given value, not null
     */
    protected Value getEscapedLiteralValue(String value) {
        String result = value.substring(1);
        return new Value(result, false);
    }

}