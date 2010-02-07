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

import org.unitils.dataset.comparison.ColumnComparison;
import org.unitils.dataset.comparison.RowComparison;
import org.unitils.dataset.core.Column;
import org.unitils.dataset.core.ProcessedColumn;
import org.unitils.dataset.core.Row;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ComparisonResultSet extends QueryResultSet {

    private List<ProcessedColumn> processedColumns;


    public ComparisonResultSet(List<ProcessedColumn> processedColumns, Connection connection, PreparedStatement preparedStatement, ResultSet resultSet, Set<String> primaryKeyColumnNames) {
        super(connection, preparedStatement, resultSet, primaryKeyColumnNames);
        this.processedColumns = processedColumns;
    }


    public RowComparison getRowComparison(Row row) throws SQLException {
        RowComparison rowComparison = new RowComparison(row);

        for (int index = 0; index < processedColumns.size(); index++) {
            ColumnComparison columnComparison = getColumnComparison(index);
            rowComparison.addColumnComparison(columnComparison);
        }
        return rowComparison;
    }

    protected ColumnComparison getColumnComparison(int index) throws SQLException {
        ProcessedColumn processedColumn = processedColumns.get(index);
        Column column = processedColumn.getColumn();
        boolean primaryKey = processedColumn.isPrimaryKey();

        String expectedValue = getExpectedValue(index);
        String actualValue = getActualValue(index);
        return new ColumnComparison(column, expectedValue, actualValue, primaryKey);
    }


    protected String getExpectedValue(int columnIndex) throws SQLException {
        return getValue((columnIndex * 2) + 1);
    }

    protected String getActualValue(int columnIndex) throws SQLException {
        return getValue(columnIndex * 2);
    }

}
