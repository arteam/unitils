/*
 * Copyright 2009,  Unitils.org
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
import org.unitils.dataset.core.Column;
import org.unitils.dataset.core.ColumnProcessor;
import org.unitils.dataset.core.ProcessedColumn;
import org.unitils.dataset.core.Row;
import org.unitils.dataset.loader.impl.Database;
import org.unitils.dataset.loader.impl.NameProcessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.unitils.core.util.DbUtils.closeQuietly;
import static org.unitils.dataset.util.PreparedStatementUtils.createPreparedStatement;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class RowComparator {

    protected ColumnProcessor columnProcessor;
    protected NameProcessor nameProcessor;
    protected Database database;


    public void init(ColumnProcessor columnProcessor, NameProcessor nameProcessor, Database database) {
        this.columnProcessor = columnProcessor;
        this.nameProcessor = nameProcessor;
        this.database = database;
    }


    public ComparisonResultSet compareRowWithDatabase(Row row, List<String> variables) throws SQLException {
        Set<String> primaryKeyColumnNames = database.getPrimaryKeyColumnNames(row.getTable());

        StringBuilder columnsPart = new StringBuilder();
        StringBuilder valuesPart = new StringBuilder();
        List<String> statementValues = new ArrayList<String>();

        List<ProcessedColumn> processedColumns = processColumns(row, variables, primaryKeyColumnNames);
        for (ProcessedColumn processedColumn : processedColumns) {
            addColumnToStatementParts(processedColumn, columnsPart, statementValues);
        }

        addIdentifiersToStatementParts(primaryKeyColumnNames, columnsPart);

        String tableName = nameProcessor.getTableName(row.getTable());
        String sql = createStatement(tableName, columnsPart);
        return executeQuery(processedColumns, primaryKeyColumnNames, sql, statementValues);
    }


    protected List<ProcessedColumn> processColumns(Row row, List<String> variables, Set<String> primaryKeyColumnNames) {
        List<ProcessedColumn> processedColumns = new ArrayList<ProcessedColumn>();
        for (Column column : row.getColumns()) {
            boolean primaryKey = column.hasName(primaryKeyColumnNames);
            ProcessedColumn processedColumn = columnProcessor.processColumn(column, variables, primaryKey);
            processedColumns.add(processedColumn);
        }
        return processedColumns;
    }

    protected void addColumnToStatementParts(ProcessedColumn column, StringBuilder columnsPart, List<String> statementValues) {
        columnsPart.append(column.getName());
        columnsPart.append(", ");
        if (column.isLiteralValue()) {
            columnsPart.append(column.getValue());
        } else {
            columnsPart.append('?');
            statementValues.add(column.getValue());
        }
        columnsPart.append(", ");
    }

    protected String createStatement(String tableName, StringBuilder columnsPart) {
        if (columnsPart.length() == 0) {
            // row without columns
            columnsPart.append("1");
        } else {
            columnsPart.setLength(columnsPart.length() - 2);
        }

        StringBuilder sql = new StringBuilder();
        sql.append("select ");
        sql.append(columnsPart);
        sql.append(" from ");
        sql.append(tableName);
        return sql.toString();
    }

    protected void addIdentifiersToStatementParts(Set<String> primaryKeyColumnNames, StringBuilder columnsPart) {
        if (primaryKeyColumnNames.isEmpty()) {
            return;
        }
        for (String primaryKeyColumnName : primaryKeyColumnNames) {
            columnsPart.append(nameProcessor.getQuotedName(primaryKeyColumnName));
            columnsPart.append(", ");
        }
    }


    protected ComparisonResultSet executeQuery(List<ProcessedColumn> processedColumns, Set<String> primaryKeyColumnNames, String sql, List<String> statementValues) throws SQLException {
        Connection connection = database.createConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = createPreparedStatement(sql, statementValues, connection);
            resultSet = preparedStatement.executeQuery();
            return new ComparisonResultSet(processedColumns, connection, preparedStatement, resultSet, primaryKeyColumnNames);
        } catch (Throwable t) {
            closeQuietly(connection, preparedStatement, resultSet);
            throw new UnitilsException("Unable to compare row with database content.", t);
        }
    }

}