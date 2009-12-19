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
import org.unitils.dataset.comparison.DatabaseContentRetriever;
import org.unitils.dataset.core.preparedstatement.QueryResultSet;
import org.unitils.dataset.core.preparedstatement.TableContentPreparedStatement;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.rightPad;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DefaultDatabaseContentRetriever implements DatabaseContentRetriever {

    protected DataSource dataSource;


    public void init(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public String getActualDatabaseContentForDataSetComparison(DataSetComparison dataSetComparison) {
        try {
            StringBuilder contentBuilder = new StringBuilder();
            getActualDatabaseContent(dataSetComparison, contentBuilder);
            return contentBuilder.toString();
        } catch (UnitilsException e) {
            throw e;
        } catch (Exception e) {
            throw new UnitilsException("Unable to log actual database content for data set comparison.", e);
        }
    }

    protected void getActualDatabaseContent(DataSetComparison dataSetComparison, StringBuilder contentBuilder) throws SQLException {
        Connection connection = dataSource.getConnection();
        try {
            for (SchemaComparison schemaComparison : dataSetComparison.getSchemaComparisons()) {
                String schemaName = schemaComparison.getName();
                for (TableComparison tableComparison : schemaComparison.getTableComparisons()) {
                    getActualTableContent(schemaName, tableComparison, connection, contentBuilder);
                }
            }
        } finally {
            connection.close();
        }
    }

    @SuppressWarnings({"unchecked"})
    protected void getActualTableContent(String schemaName, TableComparison tableComparison, Connection connection, StringBuilder contentBuilder) throws SQLException {
        String tableName = tableComparison.getName();
        TableContentPreparedStatement preparedStatementWrapper = createPreparedStatementWrapper(schemaName, tableName, connection);
        QueryResultSet resultSet = preparedStatementWrapper.executeQuery();

        int nrOfColumns = resultSet.getNrOfColumns();
        if (nrOfColumns == 0) {
            return;
        }
        List<String> columnNames = resultSet.getColumnNames();
        List<List<String>> values = new ArrayList(nrOfColumns);
        List<Integer> columnSizes = new ArrayList<Integer>(nrOfColumns);
        List<Boolean> rowWithExactMatch = new ArrayList<Boolean>();

        contentBuilder.append(schemaName);
        contentBuilder.append('.');
        contentBuilder.append(tableName);
        contentBuilder.append('\n');
        for (String columnName : columnNames) {
            columnSizes.add(columnName.length());
            values.add(new ArrayList<String>());
        }
        while (resultSet.next()) {
            String rowIdentifier = resultSet.getRowIdentifier();
            rowWithExactMatch.add(tableComparison.isActualRowWithExactMatch(rowIdentifier));

            for (int i = 0; i < nrOfColumns; i++) {
                String value = resultSet.getValue(i);
                if (value == null) {
                    value = "";
                }
                values.get(i).add(value);
                columnSizes.set(i, Math.max(columnSizes.get(i), value.length()));
            }
        }
        getContent(columnNames, values, rowWithExactMatch, columnSizes, contentBuilder);
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

    protected TableContentPreparedStatement createPreparedStatementWrapper(String schemaName, String tableName, Connection connection) throws SQLException {
        return new TableContentPreparedStatement(schemaName, tableName, connection);
    }

}