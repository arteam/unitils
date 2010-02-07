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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.UnitilsException;
import org.unitils.dataset.comparison.*;
import org.unitils.dataset.core.*;
import org.unitils.dataset.loader.impl.Database;
import org.unitils.dataset.loader.impl.NameProcessor;

import java.util.List;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DefaultDataSetComparator implements DataSetComparator {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DefaultDataSetComparator.class);

    protected Database database;
    protected String identifierQuoteString;


    public void init(Database database) {
        this.database = database;
    }

    public DataSetComparison compare(DataSet expectedDataSet, List<String> variables) {
        NameProcessor nameProcessor = new NameProcessor(database.getIdentifierQuoteString());
        ColumnProcessor columnProcessor = new ColumnProcessor(expectedDataSet.getLiteralToken(), expectedDataSet.getVariableToken(), nameProcessor);
        RowComparator rowComparator = createRowComparator();
        rowComparator.init(columnProcessor, nameProcessor, database);

        return compareDataSet(expectedDataSet, variables, rowComparator);
    }


    protected DataSetComparison compareDataSet(DataSet dataSet, List<String> variables, RowComparator rowComparator) {
        DataSetComparison dataSetComparison = new DataSetComparison();
        for (Schema schema : dataSet.getSchemas()) {
            SchemaComparison schemaComparison = compareSchema(schema, variables, rowComparator);
            dataSetComparison.addSchemaComparison(schemaComparison);
        }
        return dataSetComparison;
    }

    protected SchemaComparison compareSchema(Schema schema, List<String> variables, RowComparator rowComparator) {
        SchemaComparison schemaComparison = new SchemaComparison(schema);
        for (Table table : schema.getTables()) {
            TableComparison tableComparison = compareTable(table, variables, rowComparator);
            schemaComparison.addTableComparison(tableComparison);
        }
        return schemaComparison;
    }

    protected TableComparison compareTable(Table table, List<String> variables, RowComparator rowComparator) {
        TableComparison tableDifference = new TableComparison(table);
        findMatches(table, variables, tableDifference, rowComparator);
        findBestComparisons(table, variables, tableDifference, rowComparator);
        return tableDifference;
    }


    protected void findMatches(Table table, List<String> variables, TableComparison tableComparison, RowComparator rowComparator) {
        for (Row row : table.getRows()) {
            try {
                database.addExtraParentColumnsForChild(row);
                ComparisonResultSet comparisonResultSet = rowComparator.compareRowWithDatabase(row, variables);
                try {
                    findMatchesAndTablesThatShouldHaveNoMoreRecords(row, variables, comparisonResultSet, tableComparison);
                } finally {
                    comparisonResultSet.close();
                }
            } catch (Exception e) {
                throw new UnitilsException("Unable to compare data set row for table: " + table + ", row: [" + row + "], variables: " + variables, e);
            }
        }
    }

    protected void findBestComparisons(Table table, List<String> variables, TableComparison tableComparison, RowComparator rowComparator) {
        for (Row row : table.getRows()) {
            if (row.isEmpty() || row.isNotExists() || tableComparison.hasMatch(row)) {
                continue;
            }
            try {
                ComparisonResultSet comparisonResultSet = rowComparator.compareRowWithDatabase(row, variables);
                try {
                    findBestComparisons(row, variables, comparisonResultSet, tableComparison);
                } finally {
                    comparisonResultSet.close();
                }
            } catch (Exception e) {
                throw new UnitilsException("Unable to compare data set row for table: " + table + ", row: [" + row + "], variables: " + variables, e);
            }
        }
    }


    protected void findMatchesAndTablesThatShouldHaveNoMoreRecords(Row row, List<String> variables, ComparisonResultSet comparisonResultSet, TableComparison tableComparison) throws Exception {
        while (comparisonResultSet.next()) {
            String rowIdentifier = comparisonResultSet.getRowIdentifier();
            if (tableComparison.isActualRowWithExactMatch(rowIdentifier)) {
                continue;
            }
            if (row.isEmpty()) {
                if (!row.isNotExists()) {
                    tableComparison.setExpectedNoMoreRecordsButFoundMore(true);
                }
                break;
            }
            RowComparison rowComparison = comparisonResultSet.getRowComparison(row);
            if (rowComparison.isMatch()) {
                tableComparison.replaceIfBetterRowComparison(rowIdentifier, rowComparison);
                break;
            }
        }
    }

    protected void findBestComparisons(Row row, List<String> variables, ComparisonResultSet comparisonResultSet, TableComparison tableComparison) throws Exception {
        boolean foundActualRow = false;

        while (comparisonResultSet.next()) {
            String rowIdentifier = comparisonResultSet.getRowIdentifier();
            if (tableComparison.isActualRowWithExactMatch(rowIdentifier)) {
                continue;
            }
            RowComparison rowComparison = comparisonResultSet.getRowComparison(row);
            tableComparison.replaceIfBetterRowComparison(rowIdentifier, rowComparison);
            foundActualRow = true;
        }
        if (!foundActualRow) {
            tableComparison.addMissingRow(row);
        }
    }


    protected RowComparator createRowComparator() {
        return new RowComparator();
    }

}