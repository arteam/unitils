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

import org.unitils.core.UnitilsException;
import org.unitils.dataset.comparison.DataSetComparison;
import org.unitils.dataset.comparison.DatabaseContentLogger;
import org.unitils.dataset.comparison.TableComparison;
import org.unitils.dataset.core.DatabaseColumn;
import org.unitils.dataset.core.DatabaseRow;
import org.unitils.dataset.core.Value;
import org.unitils.dataset.loader.impl.Database;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.apache.commons.lang.StringUtils.rightPad;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DefaultDatabaseContentLogger implements DatabaseContentLogger {

    protected Database database;
    protected TableContentRetriever tableContentRetriever;


    public void init(Database database, TableContentRetriever tableContentRetriever) {
        this.database = database;
        this.tableContentRetriever = tableContentRetriever;
    }


    public String getDatabaseContentForComparison(DataSetComparison dataSetComparison) {
        try {
            StringBuilder contentBuilder = new StringBuilder();
            for (TableComparison tableComparison : dataSetComparison.getTableComparisons()) {
                getActualTableContent(tableComparison, contentBuilder);
            }
            return contentBuilder.toString();
        } catch (UnitilsException e) {
            throw e;
        } catch (Exception e) {
            throw new UnitilsException("Unable to log actual database content for data set comparison.", e);
        }
    }


    protected void getActualTableContent(TableComparison tableComparison, StringBuilder contentBuilder) throws Exception {
        String qualifiedTableName = tableComparison.getQualifiedTableName();
        Set<String> primaryKeyColumnNames = database.getPrimaryKeyColumnNames(qualifiedTableName);
        List<DatabaseColumn> databaseColumns = database.getDatabaseColumns(qualifiedTableName);

        TableContents tableContents = tableContentRetriever.getTableContents(qualifiedTableName, databaseColumns, primaryKeyColumnNames);
        try {
            int nrOfColumns = tableContents.getNrOfColumns();
            List<String> columnNames = tableContents.getColumnNames();
            List<List<String>> values = new ArrayList<List<String>>(nrOfColumns);
            List<Integer> columnSizes = new ArrayList<Integer>(nrOfColumns);
            List<Boolean> rowWithExactMatch = new ArrayList<Boolean>();

            contentBuilder.append(qualifiedTableName);
            contentBuilder.append('\n');
            for (String columnName : columnNames) {
                columnSizes.add(columnName.length());
                values.add(new ArrayList<String>());
            }
            DatabaseRow databaseRow;
            while ((databaseRow = tableContents.getDatabaseRow()) != null) {
                String rowIdentifier = databaseRow.getIdentifier();
                rowWithExactMatch.add(tableComparison.isMatchingRow(rowIdentifier));

                List<Value> databaseColumnWithValues = databaseRow.getDatabaseColumnsWithValue();
                for (int i = 0; i < nrOfColumns; i++) {
                    Value databaseColumnWithValue = databaseColumnWithValues.get(i);
                    Object value = databaseColumnWithValue.getValue();
                    String valueAsString = "";
                    if (value != null) {
                        valueAsString = value.toString();
                    }

                    values.get(i).add(valueAsString);
                    columnSizes.set(i, Math.max(columnSizes.get(i), valueAsString.length()));
                }
            }
            getContent(columnNames, values, rowWithExactMatch, columnSizes, contentBuilder);

        } finally {
            tableContents.close();
        }
    }

    protected void getContent(List<String> columnNames, List<List<String>> values, List<Boolean> rowWithExactMatch, List<Integer> columnSizes, StringBuilder contentBuilder) {
        int nrOfRows = values.get(0).size();
        if (nrOfRows == 0) {
            contentBuilder.append("   <empty table>");
            return;
        }

        contentBuilder.append("   ");
        for (int i = 0; i < columnNames.size(); i++) {
            contentBuilder.append(rightPad(columnNames.get(i), columnSizes.get(i) + 2));
        }
        contentBuilder.append('\n');

        for (int i = 0; i < nrOfRows; i++) {
            if (rowWithExactMatch.get(i)) {
                contentBuilder.append("-> ");
            } else {
                contentBuilder.append("   ");
            }
            for (int ii = 0; ii < values.size(); ii++) {
                contentBuilder.append(rightPad(values.get(ii).get(i), columnSizes.get(ii) + 2));
            }
            contentBuilder.append('\n');
        }
    }

}