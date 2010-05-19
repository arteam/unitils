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
import org.unitils.dataset.comparison.*;
import org.unitils.dataset.core.DatabaseRow;
import org.unitils.dataset.factory.DataSetResolver;
import org.unitils.dataset.factory.DataSetRowSource;
import org.unitils.dataset.loader.impl.Database;

import java.util.List;
import java.util.Properties;

import static java.lang.Math.max;
import static org.apache.commons.lang.StringUtils.rightPad;
import static org.unitils.core.util.ConfigUtils.getConfiguredInstanceOf;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DefaultExpectedDataSetStrategy implements ExpectedDataSetStrategy {

    /* Utility for creating string representations */
    protected ObjectFormatter objectFormatter = new ObjectFormatter();

    protected DataSetResolver dataSetResolver;
    protected DataSetComparator dataSetComparator;
    protected DatabaseContentLogger databaseContentLogger;


    public void init(Properties configuration, Database database) {
        this.dataSetResolver = createDataSetResolver(configuration);
        this.dataSetComparator = createDataSetComparator();
        this.databaseContentLogger = createDatabaseContentLogger();
    }

    /**
     * Asserts that the given expected schema is equal to the actual schema.
     * Tables, rows or columns that are not specified in the expected schema will be ignored.
     * If an empty table is specified in the expected schema, it will check that the actual table is also be empty.
     *
     * @param expectedDataSetRowSource The expected data set, not null
     * @param variables                Variables that will be replaced in the data set if needed, not null
     * @throws AssertionError When the assertion fails.
     */
    public void assertEqual(DataSetRowSource expectedDataSetRowSource, List<String> variables) throws AssertionError {
        DataSetComparison dataSetComparison = dataSetComparator.compare(expectedDataSetRowSource, variables);
        if (!dataSetComparison.isMatch()) {
            String message = generateErrorMessage(dataSetComparison, dataSetComparator);
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
        for (TableComparison tableComparison : dataSetComparison.getTableComparisons()) {
            if (tableComparison.isExpectedNoMoreRecordsButFoundMore()) {
                appendExpectedToBeEmptyButWasNotTableComparison(tableComparison, result);
            } else {
                appendTableComparison(tableComparison, result);
            }
        }

        if (databaseContentLogger != null) {
            String databaseContent = databaseContentLogger.getDatabaseContentForComparison(dataSetComparison);
            result.append("== Actual database content ==\n\n");
            result.append(databaseContent);
        }
        return result.toString();
    }

    protected void appendExpectedToBeEmptyButWasNotTableComparison(TableComparison tableComparison, StringBuilder result) {
        result.append("Expected no more database records in table ");
        result.append(tableComparison.getQualifiedTableName());
        result.append(" but found more records.\n\n");
    }

    protected void appendTableComparison(TableComparison tableComparison, StringBuilder result) {       
        for (DatabaseRow databaseRow : tableComparison.getMissingRows()) {
            appendMissingRow(databaseRow, result);
        }
        for (DatabaseRow databaseRow : tableComparison.getRowsThatShouldNotHaveMatched()) {
            appendRowThatShouldNotHaveMatch(databaseRow, result);
        }
        for (RowComparison rowComparison : tableComparison.getBestRowComparisons()) {
            appendBestRowComparison(rowComparison, result);
        }
        result.append('\n');
    }


    protected void appendMissingRow(DatabaseRow missingRow, StringBuilder result) {
        result.append("* No database record found for data set row:  ");
        result.append(missingRow);
        result.append("\n");
    }

    protected void appendRowThatShouldNotHaveMatch(DatabaseRow rowThatShouldNotHaveMatch, StringBuilder result) {
        result.append("* Expected not to find a match for data set row: ");
        result.append(rowThatShouldNotHaveMatch);
        result.append("\n");
    }

    protected void appendBestRowComparison(RowComparison rowComparison, StringBuilder result) {
        result.append("* No match found for data set row:  ");
        result.append(rowComparison.getExpectedDatabaseRow());
        result.append("\n");

        StringBuilder columnNames = new StringBuilder();
        StringBuilder expectedValues = new StringBuilder();
        StringBuilder actualValues = new StringBuilder();
        for (ColumnDifference columnComparison : rowComparison.getColumnDifferences()) {
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


    protected void appendColumnComparison(ColumnDifference columnDifference, StringBuilder columnNames, StringBuilder expectedValues, StringBuilder actualValues) {
        String columnName = columnDifference.getColumnName();
        String expectedValue = columnDifference.getExpectedValueAsString();
        String actualValue = columnDifference.getActualValueAsString();

        int columnSize = max(max(columnName.length(), expectedValue.length()), actualValue.length()) + 2;
        columnNames.append(rightPad(columnName, columnSize));
        expectedValues.append(rightPad(expectedValue, columnSize));
        actualValues.append(rightPad(actualValue, columnSize));
    }


    protected DataSetResolver createDataSetResolver(Properties configuration) {
        return getConfiguredInstanceOf(DataSetResolver.class, configuration);
    }


    protected DataSetComparator createDataSetComparator() {
        return new DefaultDataSetComparator();
    }

    protected DatabaseContentLogger createDatabaseContentLogger() {
        return new DefaultDatabaseContentLogger();
    }

}