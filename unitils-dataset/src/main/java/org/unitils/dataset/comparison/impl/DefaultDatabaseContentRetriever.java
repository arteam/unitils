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

import org.unitils.core.UnitilsException;
import org.unitils.dataset.comparison.DataSetComparison;
import org.unitils.dataset.comparison.TableComparison;
import org.unitils.dataset.core.Table;
import org.unitils.dataset.loader.impl.Database;
import org.unitils.dataset.util.DatabaseAccessor;

import java.sql.SQLException;
import java.util.List;

import static org.apache.commons.lang.StringUtils.rightPad;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DefaultDatabaseContentRetriever {

    protected Database database;
    protected DatabaseAccessor databaseAccessor;


    public void init(Database database, DatabaseAccessor databaseAccessor) {
        this.database = database;
        this.databaseAccessor = databaseAccessor;
    }


    public String getActualDatabaseContentForDataSetComparison(DataSetComparison dataSetComparison) {
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


    @SuppressWarnings({"unchecked"})
    protected void getActualTableContent(TableComparison tableComparison, StringBuilder contentBuilder) throws Exception {
        // todo implement
        Table table = null;//tableComparison.getDataSetTable();
        TableContentRetriever preparedStatementWrapper = createPreparedStatementWrapper();
        // todo implement
//        QueryResultSet2 resultSet = null; //preparedStatementWrapper.getTableContent(tableComparison.getDataSetTable());
//        try {
//
//            int nrOfColumns = resultSet.getNrOfColumns();
//            if (nrOfColumns == 0) {
//                return;
//            }
//            List<String> columnNames = resultSet.getColumnNames();
//            List<List<String>> values = new ArrayList(nrOfColumns);
//            List<Integer> columnSizes = new ArrayList<Integer>(nrOfColumns);
//            List<Boolean> rowWithExactMatch = new ArrayList<Boolean>();
//
//            contentBuilder.append(table.getSchema().getName());
//            contentBuilder.append('.');
//            contentBuilder.append(table.getName());
//            contentBuilder.append('\n');
//            for (String columnName : columnNames) {
//                columnSizes.add(columnName.length());
//                values.add(new ArrayList<String>());
//            }
//            while (resultSet.next()) {
//                String rowIdentifier = resultSet.getRowIdentifier();
//                // todo implement
//                //rowWithExactMatch.add(tableComparison.isRowAlreadyMatched(rowIdentifier));
//
//                for (int i = 0; i < nrOfColumns; i++) {
//                    ColumnValue columnValue = resultSet.getColumnValue(i);
//                    String value = columnValue.getValueAsString();
//                    if (value == null) {
//                        value = "";
//                    }
//                    values.get(i).add(value);
//                    columnSizes.set(i, Math.max(columnSizes.get(i), value.length()));
//                }
//            }
//            getContent(columnNames, values, rowWithExactMatch, columnSizes, contentBuilder);
//        } finally {
//            resultSet.close();
//        }
    }

    protected void getContent(List<String> columnNames, List<List<String>> values, List<Boolean> rowWithExactMatch, List<Integer> columnSizes, StringBuilder contentBuilder) {
        int nrOfRows = values.get(0).size();
        if (nrOfRows == 0) {
            contentBuilder.append("<empty table>");
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

    protected TableContentRetriever createPreparedStatementWrapper() throws SQLException {
        //NameProcessor nameProcessor = new NameProcessor(database.getIdentifierQuoteString());
        //return new TableContentRetriever(nameProcessor, database, databaseAccessor);
        // todo implement

        return null;
    }

}