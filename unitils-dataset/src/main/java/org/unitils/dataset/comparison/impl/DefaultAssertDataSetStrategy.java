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
package org.unitils.dataset.comparison.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.UnitilsException;
import org.unitils.dataset.comparison.AssertDataSetStrategy;
import org.unitils.dataset.comparison.DataSetComparator;
import org.unitils.dataset.comparison.DatabaseContentLogger;
import org.unitils.dataset.comparison.model.ColumnDifference;
import org.unitils.dataset.comparison.model.DataSetComparison;
import org.unitils.dataset.comparison.model.RowComparison;
import org.unitils.dataset.comparison.model.TableComparison;
import org.unitils.dataset.core.database.Row;
import org.unitils.dataset.core.impl.BaseLoadDataSetStrategy;
import org.unitils.dataset.core.impl.DataSetRowProcessor;
import org.unitils.dataset.database.DatabaseMetaData;
import org.unitils.dataset.loader.impl.IdentifierNameProcessor;
import org.unitils.dataset.rowsource.DataSetRowSource;
import org.unitils.dataset.sqltypehandler.SqlTypeHandlerRepository;

import java.util.List;
import java.util.Properties;

import static java.lang.Math.max;
import static org.apache.commons.lang.StringUtils.rightPad;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DefaultAssertDataSetStrategy implements AssertDataSetStrategy {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(BaseLoadDataSetStrategy.class);

    protected DataSetComparator dataSetComparator;
    protected IdentifierNameProcessor identifierNameProcessor;
    protected DataSetRowProcessor dataSetRowProcessor;

    protected DatabaseContentLogger databaseContentLogger;
    protected TableContentRetriever tableContentRetriever;


    public void init(Properties configuration, DatabaseMetaData database) {
        this.identifierNameProcessor = createIdentifierNameProcessor(database);
        this.dataSetRowProcessor = createDataSetRowProcessor(identifierNameProcessor, database);
        this.tableContentRetriever = createTableContentRetriever(database);
        this.dataSetComparator = createDataSetComparator(dataSetRowProcessor, tableContentRetriever, database);
        this.databaseContentLogger = createDatabaseContentLogger(database, tableContentRetriever);
    }

    /**
     * Asserts that the given expected schema is equal to the actual schema.
     * Tables, rows or columns that are not specified in the expected schema will be ignored.
     * If an empty table is specified in the expected schema, it will check that the actual table is also be empty.
     */
    public void perform(DataSetRowSource dataSetRowSource, List<String> variables, boolean logDatabaseContentOnAssertionError) {
        logger.info("Comparing data sets file: " + dataSetRowSource.getDataSetName());
        try {
            dataSetRowSource.open();
            DataSetComparison dataSetComparison = dataSetComparator.compare(dataSetRowSource, variables);
            if (!dataSetComparison.isMatch()) {
                String message = generateErrorMessage(dataSetComparison, logDatabaseContentOnAssertionError);
                throw new AssertionError(message);
            }

        } catch (Exception e) {
            throw new UnitilsException("Unable to load data set file: " + dataSetRowSource.getDataSetName(), e);
        } finally {
            dataSetRowSource.close();
        }
    }


    /**
     * @param dataSetComparison The comparison result, not null
     * @return the assertion failed message for the given comparison result, not null
     */
    protected String generateErrorMessage(DataSetComparison dataSetComparison, boolean logDatabaseContent) {
        StringBuilder result = new StringBuilder("Assertion failed. Differences found between the expected data set and actual database content.\n\n");
        for (TableComparison tableComparison : dataSetComparison.getTableComparisons()) {
            if (tableComparison.isExpectedNoMoreRecordsButFoundMore()) {
                appendExpectedToBeEmptyButWasNotTableComparison(tableComparison, result);
            } else {
                appendTableComparison(tableComparison, result);
            }
        }

        if (logDatabaseContent) {
            String databaseContent = databaseContentLogger.getDatabaseContentForComparison(dataSetComparison);
            result.append("== Actual database content (matched rows indicated with ->) ==\n");
            result.append(databaseContent);
            result.append('\n');
        }
        return result.toString();
    }

    protected void appendExpectedToBeEmptyButWasNotTableComparison(TableComparison tableComparison, StringBuilder result) {
        result.append("Expected no more database records in table ");
        result.append(tableComparison.getQualifiedTableName());
        result.append(" but found more records.\n\n");
    }

    protected void appendTableComparison(TableComparison tableComparison, StringBuilder result) {
        for (Row row : tableComparison.getMissingRows()) {
            appendMissingRow(row, result);
        }
        for (Row row : tableComparison.getRowsThatShouldNotHaveMatched()) {
            appendRowThatShouldNotHaveMatch(row, result);
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

    protected void appendRowThatShouldNotHaveMatch(Row rowThatShouldNotHaveMatch, StringBuilder result) {
        result.append("* Expected not to find a match for data set row: ");
        result.append(rowThatShouldNotHaveMatch);
        result.append("\n");
    }

    protected void appendBestRowComparison(RowComparison rowComparison, StringBuilder result) {
        result.append("* No match found for data set row:  ");
        result.append(rowComparison.getExpectedRow());
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


    protected DataSetComparator createDataSetComparator(DataSetRowProcessor dataSetRowProcessor, TableContentRetriever tableContentRetriever, DatabaseMetaData database) {
        DataSetComparator dataSetComparator = new DefaultDataSetComparator();
        dataSetComparator.init(dataSetRowProcessor, tableContentRetriever, database);
        return dataSetComparator;
    }

    protected DatabaseContentLogger createDatabaseContentLogger(DatabaseMetaData database, TableContentRetriever tableContentRetriever) {
        DatabaseContentLogger databaseContentLogger = new DefaultDatabaseContentLogger();
        databaseContentLogger.init(database, tableContentRetriever);
        return databaseContentLogger;
    }

    protected TableContentRetriever createTableContentRetriever(DatabaseMetaData database) {
        SqlTypeHandlerRepository sqlTypeHandlerRepository = new SqlTypeHandlerRepository();
        TableContentRetriever tableContentRetriever = new TableContentRetriever();
        tableContentRetriever.init(database, sqlTypeHandlerRepository);
        return tableContentRetriever;
    }

    protected IdentifierNameProcessor createIdentifierNameProcessor(DatabaseMetaData database) {
        // todo refactor initialization
        IdentifierNameProcessor identifierNameProcessor = new IdentifierNameProcessor();
        identifierNameProcessor.init(database);
        return identifierNameProcessor;
    }

    protected DataSetRowProcessor createDataSetRowProcessor(IdentifierNameProcessor identifierNameProcessor, DatabaseMetaData database) {
        // todo refactor initialization
        SqlTypeHandlerRepository sqlTypeHandlerRepository = new SqlTypeHandlerRepository();
        return new DataSetRowProcessor(identifierNameProcessor, sqlTypeHandlerRepository, database);
    }

}