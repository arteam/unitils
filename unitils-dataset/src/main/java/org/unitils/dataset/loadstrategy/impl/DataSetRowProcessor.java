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
package org.unitils.dataset.loadstrategy.impl;

import org.unitils.dataset.database.DatabaseMetaData;
import org.unitils.dataset.model.database.Column;
import org.unitils.dataset.model.database.Row;
import org.unitils.dataset.model.database.Value;
import org.unitils.dataset.model.dataset.DataSetRow;
import org.unitils.dataset.model.dataset.DataSetSettings;
import org.unitils.dataset.model.dataset.DataSetValue;
import org.unitils.dataset.sqltypehandler.SqlTypeHandler;
import org.unitils.dataset.sqltypehandler.SqlTypeHandlerRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DataSetRowProcessor {

    protected IdentifierNameProcessor identifierNameProcessor;
    protected SqlTypeHandlerRepository sqlTypeHandlerRepository;
    protected DatabaseMetaData databaseMetaData;


    public DataSetRowProcessor(IdentifierNameProcessor identifierNameProcessor, SqlTypeHandlerRepository sqlTypeHandlerRepository, DatabaseMetaData databaseMetaData) {
        this.identifierNameProcessor = identifierNameProcessor;
        this.sqlTypeHandlerRepository = sqlTypeHandlerRepository;
        this.databaseMetaData = databaseMetaData;
    }


    public Row process(DataSetRow dataSetRow, List<String> variables, Set<String> unusedPrimaryKeyColumnNames) throws Exception {
        DataSetSettings dataSetSettings = dataSetRow.getDataSetSettings();

        String qualifiedTableName = identifierNameProcessor.getQualifiedTableName(dataSetRow);
        databaseMetaData.assertTableNameExists(qualifiedTableName);
        databaseMetaData.addExtraParentColumnsForChild(dataSetRow);

        Row row = new Row(qualifiedTableName);

        Set<String> allPrimaryKeyColumnNames = databaseMetaData.getPrimaryKeyColumnNames(qualifiedTableName);
        Set<String> remainingPrimaryKeyColumnNames = new HashSet<String>(allPrimaryKeyColumnNames);

        for (DataSetValue dataSetValue : dataSetRow.getColumns()) {
            Column column = createColumn(qualifiedTableName, dataSetValue, dataSetSettings, allPrimaryKeyColumnNames, remainingPrimaryKeyColumnNames);
            Value value = createValue(dataSetValue, column, variables, dataSetSettings);
            row.addValue(value);
        }

        unusedPrimaryKeyColumnNames.addAll(remainingPrimaryKeyColumnNames);
        return row;
    }

    protected Column createColumn(String qualifiedTableName, DataSetValue dataSetValue, DataSetSettings dataSetSettings, Set<String> allPrimaryKeyColumnNames, Set<String> remainingPrimaryKeyColumnNames) throws Exception {
        String columnName = identifierNameProcessor.getCorrectCaseColumnName(dataSetValue.getColumnName(), dataSetSettings);
        int sqlType = databaseMetaData.getColumnSqlType(qualifiedTableName, columnName);
        boolean primaryKey = isPrimaryKeyColumn(columnName, allPrimaryKeyColumnNames, remainingPrimaryKeyColumnNames);

        return new Column(columnName, sqlType, primaryKey);
    }

    /**
     * Gets the value, filling in the variable declarations using the given variables.
     * The first variable replaces $0, the second $1 etc.
     * If there are not enough variable values, the remaining declaration will not be replaced.
     * If the value is an escaped literal, the escaped token is replaced.
     *
     * @param dataSetValue The column to process, not null
     * @param variables    The variable values, not null
     * @return the processed value, not null
     */
    protected Value createValue(DataSetValue dataSetValue, Column column, List<String> variables, DataSetSettings dataSetSettings) throws Exception {
        char literalToken = dataSetSettings.getLiteralToken();
        char variableToken = dataSetSettings.getVariableToken();
        int sqlType = column.getSqlType();

        String valueWithVariablesFilledIn = getValueWithVariablesFilledIn(dataSetValue.getValue(), variables, variableToken);

        boolean isLiteralValue = false;
        if (isLiteralValue(valueWithVariablesFilledIn, literalToken)) {
            isLiteralValue = true;
            valueWithVariablesFilledIn = extractLiteralValue(valueWithVariablesFilledIn);
        } else if (isEscapedLiteralValue(valueWithVariablesFilledIn, literalToken)) {
            valueWithVariablesFilledIn = extractLiteralValue(valueWithVariablesFilledIn);
        }

        Object correctTypeValue;
        if (isLiteralValue) {
            correctTypeValue = valueWithVariablesFilledIn;
        } else {
            SqlTypeHandler sqlTypeHandler = sqlTypeHandlerRepository.getSqlTypeHandler(sqlType);
            correctTypeValue = sqlTypeHandler.getValue(valueWithVariablesFilledIn, sqlType);
        }
        return new Value(correctTypeValue, isLiteralValue, column);
    }


    /**
     * @param value     The value, not null
     * @param variables The variable values to fill in, not null
     * @return The value with the variable declarations replaced by the given variables, not null
     */
    protected String getValueWithVariablesFilledIn(String value, List<String> variables, char variableToken) {
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
     * @param value        The value, not null
     * @param literalToken The token that marks a literal value, eg =
     * @return True if the given value is a literal value
     */
    protected boolean isLiteralValue(String value, char literalToken) {
        return value.startsWith("" + literalToken) && !isEscapedLiteralValue(value, literalToken);
    }

    /**
     * @param value        The value, not null
     * @param literalToken The token that marks a literal value, eg =
     * @return True if the given value is an escaped literal value
     */
    protected boolean isEscapedLiteralValue(String value, char literalToken) {
        return value.startsWith("" + literalToken + literalToken);
    }


    /**
     * @param value The value, not null
     * @return The literal value for the given value, not null
     */
    protected String extractLiteralValue(String value) {
        return value.substring(1);
    }

    protected boolean isPrimaryKeyColumn(String columnName, Set<String> allPrimaryKeyColumnNames, Set<String> remainingPrimaryKeyColumnNames) {
        String columnNameWithoutQuotes = databaseMetaData.removeIdentifierQuotes(columnName);
        String matchedPrimaryKeyColumnName = null;
        for (String primaryKeyColumnName : allPrimaryKeyColumnNames) {
            if (primaryKeyColumnName.equals(columnNameWithoutQuotes)) {
                matchedPrimaryKeyColumnName = primaryKeyColumnName;
                break;
            }
        }
        if (matchedPrimaryKeyColumnName != null) {
            remainingPrimaryKeyColumnNames.remove(matchedPrimaryKeyColumnName);
            return true;
        }
        return false;
    }

}