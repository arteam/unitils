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

import org.unitils.dataset.loader.impl.Database;
import org.unitils.dataset.loader.impl.IdentifierNameProcessor;
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
    protected Database database;


    public void init(IdentifierNameProcessor identifierNameProcessor, SqlTypeHandlerRepository sqlTypeHandlerRepository, Database database) {
        this.identifierNameProcessor = identifierNameProcessor;
        this.sqlTypeHandlerRepository = sqlTypeHandlerRepository;
        this.database = database;
    }


    public DatabaseRow process(DataSetRow dataSetRow, List<String> variables, Set<String> unusedPrimaryKeyColumnNames) throws Exception {
        DataSetSettings dataSetSettings = dataSetRow.getDataSetSettings();
        boolean caseSensitive = dataSetSettings.isCaseSensitive();

        database.addExtraParentColumnsForChild(dataSetRow);

        String qualifiedTableName = identifierNameProcessor.getQualifiedTableName(dataSetRow);
        DatabaseRow databaseRow = new DatabaseRow(qualifiedTableName);

        Set<String> allPrimaryKeyColumnNames = database.getPrimaryKeyColumnNames(qualifiedTableName);
        Set<String> remainingPrimaryKeyColumnNames = new HashSet<String>(allPrimaryKeyColumnNames);

        for (DataSetColumn column : dataSetRow.getColumns()) {
            boolean primaryKey = isPrimaryKeyColumn(column, caseSensitive, allPrimaryKeyColumnNames, remainingPrimaryKeyColumnNames);
            DatabaseColumnWithValue databaseColumn = processColumn(qualifiedTableName, column, variables, primaryKey, dataSetSettings);
            databaseRow.addDatabaseColumnWithValue(databaseColumn);
        }

        unusedPrimaryKeyColumnNames.addAll(remainingPrimaryKeyColumnNames);
        return databaseRow;
    }


    /**
     * Gets the value, filling in the variable declarations using the given variables.
     * The first variable replaces $0, the second $1 etc.
     * If there are not enough variable values, the remaining declaration will not be replaced.
     * If the value is an escaped literal, the escaped token is replaced.
     *
     * @param dataSetColumn The column to process, not null
     * @param variables     The variable values, not null
     * @param primaryKey    True if the column is a primary key column
     * @return the processed value, not null
     */
    protected DatabaseColumnWithValue processColumn(String qualifiedTableName, DataSetColumn dataSetColumn, List<String> variables, boolean primaryKey, DataSetSettings dataSetSettings) throws Exception {
        char literalToken = dataSetSettings.getLiteralToken();
        char variableToken = dataSetSettings.getVariableToken();
        boolean caseSensitive = dataSetSettings.isCaseSensitive();
        String columnName = dataSetColumn.getName();

        String valueWithVariablesFilledIn = getValueWithVariablesFilledIn(dataSetColumn.getValue(), variables, variableToken);

        boolean isLiteralValue = false;
        if (isLiteralValue(valueWithVariablesFilledIn, literalToken)) {
            isLiteralValue = true;
            valueWithVariablesFilledIn = extractLiteralValue(valueWithVariablesFilledIn);
        } else if (isEscapedLiteralValue(valueWithVariablesFilledIn, literalToken)) {
            valueWithVariablesFilledIn = extractLiteralValue(valueWithVariablesFilledIn);
        }

        if (caseSensitive) {
            columnName = database.quoteIdentifier(columnName);
        } else {
            columnName = database.toCorrectCaseIdentifier(columnName);
        }

        int sqlType = database.getColumnSqlType(qualifiedTableName, columnName);
        SqlTypeHandler sqlTypeHandler = sqlTypeHandlerRepository.getSqlTypeHandler(sqlType);

        Object correctTypeValue;
        if (isLiteralValue) {
            correctTypeValue = valueWithVariablesFilledIn;
        } else {
            correctTypeValue = sqlTypeHandler.getValue(valueWithVariablesFilledIn, sqlType);
        }

        return new DatabaseColumnWithValue(columnName, correctTypeValue, sqlType, sqlTypeHandler, isLiteralValue, primaryKey);
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
     * @param value The value, not null
     * @return True if the given value is a literal value
     */
    protected boolean isLiteralValue(String value, char literalToken) {
        return value.startsWith("" + literalToken) && !isEscapedLiteralValue(value, literalToken);
    }

    /**
     * @param value The value, not null
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

    protected boolean isPrimaryKeyColumn(DataSetColumn dataSetColumn, boolean caseSensitive, Set<String> primaryKeyColumnNames, Set<String> remainingPrimaryKeyColumnNames) {
        String matchedPrimaryKeyColumnName = null;
        for (String primaryKeyColumnName : primaryKeyColumnNames) {
            if (dataSetColumn.hasName(primaryKeyColumnName, caseSensitive)) {
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