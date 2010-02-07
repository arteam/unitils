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

import org.unitils.dataset.loader.impl.NameProcessor;

import java.util.List;


/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ColumnProcessor {

    /* The token that identifies a literal, e.g. for '=literal value' */
    protected char literalToken;
    /* The token that identifies a variable, e.g. $ for $0 $1 etc */
    protected char variableToken;
    /* The processor for handling table and column names */
    protected NameProcessor nameProcessor;


    /**
     * Creates a processor for values of the given data set.
     *
     * @param literalToken  The token that identifies a literal, e.g. for '=literal value'
     * @param variableToken The token that identifies a variable, e.g. $ for $0 $1 etc
     * @param nameProcessor The processor for handling table and column names, not null
     */
    public ColumnProcessor(char literalToken, char variableToken, NameProcessor nameProcessor) {
        this.literalToken = literalToken;
        this.variableToken = variableToken;
        this.nameProcessor = nameProcessor;
    }


    /**
     * Gets the value, filling in the variable declarations using the given variables.
     * The first variable replaces $0, the second $1 etc.
     * If there are not enough variable values, the remaining declaration will not be replaced.
     * If the value is an escaped literal, the escaped token is replaced.
     *
     * @param column     The column to process, not null
     * @param variables  The variable values, not null
     * @param primaryKey True if the column is a primary key column
     * @return the processed value, not null
     */
    public ProcessedColumn processColumn(Column column, List<String> variables, boolean primaryKey) {
        String columnName = nameProcessor.getColumnName(column);
        String processedValue = getValueWithVariablesFilledIn(column.getValue(), variables);

        boolean isLiteralValue = false;
        if (isLiteralValue(processedValue)) {
            isLiteralValue = true;
            processedValue = extractLiteralValue(processedValue);
        } else if (isEscapedLiteralValue(processedValue)) {
            processedValue = extractLiteralValue(processedValue);
        }
        return new ProcessedColumn(columnName, processedValue, isLiteralValue, primaryKey, column);
    }


    /**
     * @param value     The value, not null
     * @param variables The variable values to fill in, not null
     * @return The value with the variable declarations replaced by the given variables, not null
     */
    protected String getValueWithVariablesFilledIn(String value, List<String> variables) {
        StringBuilder valueStringBuilder = new StringBuilder(value);
        for (int variableIndex = 0; variableIndex < variables.size(); variableIndex++) {
            replaceVariableDeclaration(valueStringBuilder, variableIndex, variables.get(variableIndex), variableToken);
        }
        return valueStringBuilder.toString();
    }

    /**
     * @param valueStringBuilder The value in which to replace the variable declaration
     * @param variableIndex      The variable index, >=0
     * @param variable           The variable value to use, not null
     * @param variableToken      The token that identifies a variable, e.g. $ for $0 $1 etc
     */
    protected void replaceVariableDeclaration(StringBuilder valueStringBuilder, int variableIndex, String variable, char variableToken) {
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
     * @return True if the given value is a literal value
     */
    protected boolean isLiteralValue(String value) {
        return value.startsWith("" + literalToken) && !isEscapedLiteralValue(value);
    }

    /**
     * @param value The value, not null
     * @return True if the given value is an escaped literal value
     */
    protected boolean isEscapedLiteralValue(String value) {
        return value.startsWith("" + literalToken + literalToken);
    }


    /**
     * @param value The value, not null
     * @return The literal value for the given value, not null
     */
    protected String extractLiteralValue(String value) {
        return value.substring(1);
    }

}