/*
 * Copyright 2008,  Unitils.org
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

import org.unitils.core.util.ObjectFormatter;
import org.unitils.dataset.comparison.DataSetComparator;
import org.unitils.dataset.comparison.DatabaseContentRetriever;
import org.unitils.dataset.comparison.ExpectedDataSetAssert;
import org.unitils.dataset.core.DataSet;
import org.unitils.dataset.core.Row;

import java.util.List;

import static java.lang.Math.max;
import static org.apache.commons.lang.StringUtils.rightPad;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DefaultExpectedDataSetAssert implements ExpectedDataSetAssert {

    /* Utility for creating string representations */
    protected ObjectFormatter objectFormatter = new ObjectFormatter();

    protected DataSetComparator dataSetComparator;
    protected DatabaseContentRetriever databaseContentLogger;


    /**
     * Initializes the data set assert.
     *
     * @param dataSetComparator     The comparator that will create the data set comparison, not null
     * @param databaseContentLogger The logger for displaying the database content, null if the content should not be logged
     */
    public void init(DataSetComparator dataSetComparator, DatabaseContentRetriever databaseContentLogger) {
        this.dataSetComparator = dataSetComparator;
        this.databaseContentLogger = databaseContentLogger;
    }

    /**
     * Asserts that the given expected schema is equal to the actual schema.
     * Tables, rows or columns that are not specified in the expected schema will be ignored.
     * If an empty table is specified in the expected schema, it will check that the actual table is also be empty.
     *
     * @param expectedDataSet The expected data set, not null
     * @param variables       Variables that will be replaced in the data set if needed, not null
     * @throws AssertionError When the assertion fails.
     */
    public void assertEqual(DataSet expectedDataSet, List<String> variables) throws AssertionError {
        DataSetComparison dataSetComparison = dataSetComparator.compare(expectedDataSet, variables);
        if (!dataSetComparison.isMatch()) {
            String message = generateErrorMessage(dataSetComparison, dataSetComparator);
            if (databaseContentLogger != null) {
                String databaseContent = databaseContentLogger.getActualDatabaseContentForDataSetComparison(dataSetComparison);

                StringBuilder messageBuilder = new StringBuilder(message);
                messageBuilder.append("== Actual Database Content ==\n\n");
                messageBuilder.append(databaseContent);
                message = messageBuilder.toString();
            }
            throw new AssertionError(message);
        }
    }


    /**
     * @param dataSetComparison The comparison result, not null
     * @param dataSetComparator The comparator that can output the actual database content, not null
     * @return the assertion failed message for the given comparison result, not null
     */
    protected String generateErrorMessage(DataSetComparison dataSetComparison, DataSetComparator dataSetComparator) {
        StringBuilder result = new StringBuilder("Assertion failed. Differences found between the expected data set and actual database content.\n\n");
        for (SchemaComparison schemaComparison : dataSetComparison.getSchemaComparisons()) {
            appendSchemaComparison(schemaComparison, result);
        }
        return result.toString();
    }


    protected void appendSchemaComparison(SchemaComparison schemaComparison, StringBuilder result) {
        String schemaName = schemaComparison.getName();
        for (TableComparison tableComparison : schemaComparison.getTableComparisons()) {
            if (tableComparison.isExpectedNoMoreRecordsButFoundMore()) {
                appendExpectedToBeEmptyButWasNotTableComparison(schemaName, tableComparison, result);
            } else {
                appendTableComparison(schemaName, tableComparison, result);
            }
        }
    }

    protected void appendExpectedToBeEmptyButWasNotTableComparison(String schemaName, TableComparison tableComparison, StringBuilder result) {
        result.append("Expected no more database records in table ");
        appendTableName(schemaName, tableComparison.getName(), result);
        result.append(" but found more records.\n");
    }

    protected void appendTableComparison(String schemaName, TableComparison tableComparison, StringBuilder result) {
        result.append("Found differences for table ");
        appendTableName(schemaName, tableComparison.getName(), result);
        result.append(":\n");
        for (Row missingRow : tableComparison.getMissingRows()) {
            appendMissingRow(missingRow, result);
        }
        for (RowComparison rowComparison : tableComparison.getBestRowComparisons()) {
            appendBestRowComparison(rowComparison, result);
        }
        result.append('\n');
    }


    protected void appendMissingRow(Row missingRow, StringBuilder result) {
        result.append("* No database record found for data set row:  ");
        result.append(missingRow);
        result.append("\n");
    }

    protected void appendBestRowComparison(RowComparison rowComparison, StringBuilder result) {
        result.append("* Different database record found for data set row:  ");
        result.append(rowComparison.getDataSetRow());
        result.append("\n");

        StringBuilder columnNames = new StringBuilder();
        StringBuilder expectedValues = new StringBuilder();
        StringBuilder actualValues = new StringBuilder();
        for (ColumnComparison columnComparison : rowComparison.getColumnComparisons()) {
            appendColumnComparison(columnComparison, columnNames, expectedValues, actualValues);
        }
        result.append("\n                ");
        result.append(columnNames);
        result.append("\n     Expected:  ");
        result.append(expectedValues);
        result.append("\n     Actual:    ");
        result.append(actualValues);
        result.append("\n\n");
    }


    protected void appendColumnComparison(ColumnComparison columnComparison, StringBuilder columnNames, StringBuilder expectedValues, StringBuilder actualValues) {
        String columnName = columnComparison.getColumnName();
        String expectedValue = columnComparison.getExpectedValue();
        String actualValue = columnComparison.getActualValue();
        if (actualValue == null) {
            actualValue = "<null>";
        }

        int columnSize = max(max(columnName.length(), expectedValue.length()), actualValue.length()) + 2;
        columnNames.append(rightPad(columnName, columnSize));
        expectedValues.append(rightPad(expectedValue, columnSize));
        actualValues.append(rightPad(actualValue, columnSize));
    }

    protected void appendTableName(String schemaName, String tableName, StringBuilder result) {
        result.append(schemaName);
        result.append(".");
        result.append(tableName);
    }

}