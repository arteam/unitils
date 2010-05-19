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
import org.unitils.dataset.comparison.DataSetComparator;
import org.unitils.dataset.comparison.DataSetComparison;
import org.unitils.dataset.comparison.RowComparison;
import org.unitils.dataset.comparison.TableComparison;
import org.unitils.dataset.core.DataSetRow;
import org.unitils.dataset.core.DataSetRowProcessor;
import org.unitils.dataset.core.DatabaseColumn;
import org.unitils.dataset.core.DatabaseRow;
import org.unitils.dataset.factory.DataSetRowSource;
import org.unitils.dataset.loader.impl.Database;

import java.util.*;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DefaultDataSetComparator implements DataSetComparator {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DefaultDataSetComparator.class);

    protected DataSetRowProcessor dataSetRowProcessor;
    protected TableContentRetriever tableContentRetriever;

    protected Database database;


    public void init(DataSetRowProcessor dataSetRowProcessor, TableContentRetriever tableContentRetriever, Database database) {
        this.dataSetRowProcessor = dataSetRowProcessor;
        this.tableContentRetriever = tableContentRetriever;
        this.database = database;
    }

    public DataSetComparison compare(DataSetRowSource expectedDataSetRowSource, List<String> variables) {
        DataSetComparison dataSetComparison = new DataSetComparison();
        try {
            Map<String, Map<DataSetRow, DatabaseRow>> rowsPerTable = getRowsPerTable(expectedDataSetRowSource, variables);
            for (Map.Entry<String, Map<DataSetRow, DatabaseRow>> entry : rowsPerTable.entrySet()) {
                String qualifiedTableName = entry.getKey();
                Map<DataSetRow, DatabaseRow> dataSetRows = entry.getValue();

                List<DatabaseColumn> databaseColumns = getAllUsedDatabaseColumns(dataSetRows);
                Set<String> primaryKeyColumnNames = database.getPrimaryKeyColumnNames(qualifiedTableName);

                TableComparison tableComparison = compareDataSetRowsForTable(qualifiedTableName, databaseColumns, dataSetRows, primaryKeyColumnNames);
                dataSetComparison.addTableComparison(tableComparison);
            }
            return dataSetComparison;

        } catch (Exception e) {
            //todo implement
            e.printStackTrace();
            throw new UnitilsException("todo", e);
        }
    }

    protected List<DatabaseColumn> getAllUsedDatabaseColumns(Map<DataSetRow, DatabaseRow> dataSetRows) {
        Map<String, DatabaseColumn> databaseColumnsPerName = new LinkedHashMap<String, DatabaseColumn>();
        for (DatabaseRow databaseRow : dataSetRows.values()) {
            for (DatabaseColumn databaseColumn : databaseRow.getDatabaseColumnsWithValue()) {
                databaseColumnsPerName.put(databaseColumn.getColumnName(), databaseColumn);
            }
        }
        return new ArrayList<DatabaseColumn>(databaseColumnsPerName.values());
    }


    protected TableComparison compareDataSetRowsForTable(String qualifiedTableName, List<DatabaseColumn> databaseColumns, Map<DataSetRow, DatabaseRow> dataSetRows, Set<String> primaryKeyColumnNames) throws Exception {
        TableContents tableContents = tableContentRetriever.getTableContents(qualifiedTableName, databaseColumns, primaryKeyColumnNames);
        try {
            return compareTable(qualifiedTableName, tableContents, dataSetRows);
        } finally {
            tableContents.close();
        }
    }
    

    protected Map<String, Map<DataSetRow, DatabaseRow>> getRowsPerTable(DataSetRowSource dataSetRowSource, List<String> variables) throws Exception {
        Map<String, Map<DataSetRow, DatabaseRow>> dataSetRowsPerTable = new LinkedHashMap<String, Map<DataSetRow, DatabaseRow>>();
        DataSetRow dataSetRow;
        while ((dataSetRow = dataSetRowSource.getNextDataSetRow()) != null) {
            DatabaseRow databaseRow = dataSetRowProcessor.process(dataSetRow, variables, new HashSet<String>());
            String qualifiedTableName = databaseRow.getQualifiedTableName();

            Map<DataSetRow, DatabaseRow> dataSetRows = dataSetRowsPerTable.get(qualifiedTableName);
            if (dataSetRows == null) {
                dataSetRows = new LinkedHashMap<DataSetRow, DatabaseRow>();
                dataSetRowsPerTable.put(qualifiedTableName, dataSetRows);
            }
            dataSetRows.put(dataSetRow, databaseRow);
        }
        return dataSetRowsPerTable;
    }


    protected TableComparison compareTable(String qualifiedTableName, TableContents tableContents, Map<DataSetRow, DatabaseRow> dataSetRows) throws Exception {
        TableComparison tableComparison = new TableComparison(qualifiedTableName);

        List<RowComparison> currentRowComparisons = new ArrayList<RowComparison>();

        DatabaseRow actualDatabaseRow;
        while ((actualDatabaseRow = tableContents.getDatabaseRow()) != null) {
            if (dataSetRows.isEmpty()) {
                break;
            }

            Iterator<Map.Entry<DataSetRow, DatabaseRow>> iterator1 = dataSetRows.entrySet().iterator();
            if (iterator1.hasNext()) {
                Map.Entry<DataSetRow, DatabaseRow> entry = iterator1.next();

                DataSetRow expectedDataSetRow = entry.getKey();
                DatabaseRow expectedDatabaseRow = entry.getValue();

                if (expectedDataSetRow.isEmpty()) {
                    iterator1.remove();
                    if (!expectedDataSetRow.isNotExists()) {
                        tableComparison.setExpectedNoMoreRecordsButFoundMore(true);
                    }
                }
            }

            Iterator<Map.Entry<DataSetRow, DatabaseRow>> iterator = dataSetRows.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<DataSetRow, DatabaseRow> entry = iterator.next();

                DataSetRow dataSetRow = entry.getKey();
                DatabaseRow expectedDatabaseRow = entry.getValue();

                if (dataSetRow.isEmpty()) {
                    continue;
                }

                RowComparison rowComparison = new RowComparison(expectedDatabaseRow, actualDatabaseRow);
                currentRowComparisons.add(rowComparison);

                if (rowComparison.isMatch()) {
                    if (dataSetRow.isNotExists()) {
                        tableComparison.setMatchingRowThatShouldNotHaveMatched(actualDatabaseRow);
                    }
                    tableComparison.setMatchingRow(rowComparison);
                    iterator.remove();
                    currentRowComparisons.clear();
                    break;
                }
            }

            for (RowComparison rowComparison : currentRowComparisons) {
                tableComparison.replaceIfBetterRowComparison(rowComparison);
            }
            currentRowComparisons.clear();
        }

        for (DatabaseRow expectedDatabaseRow : dataSetRows.values()) {
            if (expectedDatabaseRow.isEmpty()) {
                continue;
            }
            if (tableComparison.getBestRowComparison(expectedDatabaseRow) == null) {
                tableComparison.addMissingRow(expectedDatabaseRow);
            }
        }

        return tableComparison;
    }

}