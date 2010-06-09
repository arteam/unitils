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
package org.unitils.dataset.assertstrategy.impl;

import org.unitils.core.UnitilsException;
import org.unitils.dataset.assertstrategy.DataSetComparator;
import org.unitils.dataset.assertstrategy.model.DataSetComparison;
import org.unitils.dataset.assertstrategy.model.RowComparison;
import org.unitils.dataset.assertstrategy.model.TableComparison;
import org.unitils.dataset.database.DatabaseMetaData;
import org.unitils.dataset.loadstrategy.impl.DataSetRowProcessor;
import org.unitils.dataset.model.database.Column;
import org.unitils.dataset.model.database.Row;
import org.unitils.dataset.model.database.Value;
import org.unitils.dataset.model.dataset.DataSetRow;
import org.unitils.dataset.rowsource.DataSetRowSource;

import java.util.*;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DefaultDataSetComparator implements DataSetComparator {

    protected DataSetRowProcessor dataSetRowProcessor;
    protected TableContentRetriever tableContentRetriever;

    protected DatabaseMetaData database;


    public void init(DataSetRowProcessor dataSetRowProcessor, TableContentRetriever tableContentRetriever, DatabaseMetaData database) {
        this.dataSetRowProcessor = dataSetRowProcessor;
        this.tableContentRetriever = tableContentRetriever;
        this.database = database;
    }

    public DataSetComparison compare(DataSetRowSource expectedDataSetRowSource, List<String> variables) {
        DataSetComparison dataSetComparison = new DataSetComparison();
        try {
            Map<String, Map<DataSetRow, Row>> rowsPerTable = getRowsPerTable(expectedDataSetRowSource, variables);
            for (Map.Entry<String, Map<DataSetRow, Row>> entry : rowsPerTable.entrySet()) {
                String qualifiedTableName = entry.getKey();
                Map<DataSetRow, Row> dataSetRows = entry.getValue();

                List<Column> columns = getAllUsedColumns(dataSetRows);
                Set<String> primaryKeyColumnNames = database.getPrimaryKeyColumnNames(qualifiedTableName);

                TableComparison tableComparison = compareDataSetRowsForTable(qualifiedTableName, columns, dataSetRows, primaryKeyColumnNames);
                dataSetComparison.addTableComparison(tableComparison);
            }
            return dataSetComparison;

        } catch (Exception e) {
            //todo implement
            e.printStackTrace();
            throw new UnitilsException("todo", e);
        }
    }

    protected List<Column> getAllUsedColumns(Map<DataSetRow, Row> dataSetRows) {
        Map<String, Column> columnsPerName = new LinkedHashMap<String, Column>();
        for (Row row : dataSetRows.values()) {
            for (Value value : row.getValues()) {
                Column column = value.getColumn();
                columnsPerName.put(column.getName(), column);
            }
        }
        return new ArrayList<Column>(columnsPerName.values());
    }


    protected TableComparison compareDataSetRowsForTable(String qualifiedTableName, List<Column> columns, Map<DataSetRow, Row> dataSetRows, Set<String> primaryKeyColumnNames) throws Exception {
        TableContents tableContents = tableContentRetriever.getTableContents(qualifiedTableName, columns, primaryKeyColumnNames);
        try {
            return compareTable(qualifiedTableName, tableContents, dataSetRows);
        } finally {
            tableContents.close();
        }
    }


    protected Map<String, Map<DataSetRow, Row>> getRowsPerTable(DataSetRowSource dataSetRowSource, List<String> variables) throws Exception {
        Map<String, Map<DataSetRow, Row>> dataSetRowsPerTable = new LinkedHashMap<String, Map<DataSetRow, Row>>();
        DataSetRow dataSetRow;
        while ((dataSetRow = dataSetRowSource.getNextDataSetRow()) != null) {
            Row row = dataSetRowProcessor.process(dataSetRow, variables, new HashSet<String>());
            for (Value value : row.getValues()) {
                if (value.isLiteralValue()) {
                    throw new UnitilsException("Literal values in an expected data set are not supported. Found literal values in data set row: " + dataSetRow);
                }
            }

            String qualifiedTableName = row.getQualifiedTableName();

            Map<DataSetRow, Row> dataSetRows = dataSetRowsPerTable.get(qualifiedTableName);
            if (dataSetRows == null) {
                dataSetRows = new LinkedHashMap<DataSetRow, Row>();
                dataSetRowsPerTable.put(qualifiedTableName, dataSetRows);
            }
            dataSetRows.put(dataSetRow, row);
        }
        return dataSetRowsPerTable;
    }


    protected TableComparison compareTable(String qualifiedTableName, TableContents tableContents, Map<DataSetRow, Row> dataSetRows) throws Exception {
        TableComparison tableComparison = new TableComparison(qualifiedTableName);

        List<RowComparison> currentRowComparisons = new ArrayList<RowComparison>();

        Row actualRow;
        while ((actualRow = tableContents.getRow()) != null) {
            if (dataSetRows.isEmpty()) {
                break;
            }

            boolean matchFound = false;
            boolean emptyRowFound = false;

            Iterator<Map.Entry<DataSetRow, Row>> iterator = dataSetRows.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<DataSetRow, Row> entry = iterator.next();

                DataSetRow dataSetRow = entry.getKey();
                Row expectedRow = entry.getValue();

                if (dataSetRow.isEmpty()) {
                    if (!dataSetRow.isNotExists()) {
                        emptyRowFound = true;
                    }
                    continue;
                }

                RowComparison rowComparison = new RowComparison(expectedRow, actualRow);
                currentRowComparisons.add(rowComparison);

                if (rowComparison.isMatch()) {
                    if (dataSetRow.isNotExists()) {
                        tableComparison.setMatchingRowThatShouldNotHaveMatched(actualRow);
                    } else {
                        matchFound = true;
                        tableComparison.setMatchingRow(rowComparison);
                    }
                    iterator.remove();
                    currentRowComparisons.clear();
                    break;
                } else {
                    if (dataSetRow.isNotExists()) {
                        iterator.remove();
                        currentRowComparisons.clear();
                        break;
                    }
                }
            }

            if (emptyRowFound && !matchFound) {
                tableComparison.setExpectedNoMoreRecordsButFoundMore(true);
            }

            for (RowComparison rowComparison : currentRowComparisons) {
                tableComparison.replaceIfBetterRowComparison(rowComparison);
            }
            currentRowComparisons.clear();
        }

        for (Row expectedRow : dataSetRows.values()) {
            if (expectedRow.isEmpty()) {
                continue;
            }
            if (tableComparison.getBestRowComparison(expectedRow) == null) {
                tableComparison.addMissingRow(expectedRow);
            }
        }

        return tableComparison;
    }

}