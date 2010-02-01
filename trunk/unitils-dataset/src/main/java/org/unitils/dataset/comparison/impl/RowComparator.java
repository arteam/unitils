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
import org.unitils.dataset.comparison.impl.ComparisonResultSet;
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

    protected StringBuilder columnsBuilder = new StringBuilder();
    protected List<String> statementParameters = new ArrayList<String>();


    public RowComparator(ColumnProcessor columnProcessor, NameProcessor nameProcessor, Database database) throws SQLException {
        this.columnProcessor = columnProcessor;
        this.nameProcessor = nameProcessor;
        this.database = database;
    }


    public ComparisonResultSet compareRowWithDatabase(Row row, List<String> variables) throws SQLException {
        Set<String> primaryKeyColumnNames = database.getPrimaryKeyColumnNames(row.getTable());

        addRow(row, variables);
        String tableName = nameProcessor.getTableName(row.getTable());
        String sql = buildStatement(tableName, primaryKeyColumnNames);

        Connection connection = database.createConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = createPreparedStatement(sql, statementParameters, connection);
            resultSet = preparedStatement.executeQuery();
            return new ComparisonResultSet(connection, preparedStatement, resultSet, primaryKeyColumnNames);
        } catch (Throwable t) {
            closeQuietly(connection, preparedStatement, resultSet);
            throw new UnitilsException("Unable to compare row with database content. Row: " + row + ", variables: " + variables, t);
        }
    }


    protected void addRow(Row row, List<String> variables) throws SQLException {
        database.addExtraParentColumnsForChild(row);
        for (Column column : row.getColumns()) {
            // todo check primary key columns
            ProcessedColumn processedColumn = columnProcessor.processColumn(column, variables, false);
            addColumn(processedColumn);
        }
    }

    protected void addColumn(ProcessedColumn column) {
        columnsBuilder.append(column.getName());
        columnsBuilder.append(", ");
        if (column.isLiteralValue()) {
            columnsBuilder.append(column.getValue());
        } else {
            columnsBuilder.append('?');
            statementParameters.add(column.getValue());
        }
        columnsBuilder.append(", ");
    }


    protected String buildStatement(String tableName, Set<String> primaryKeyColumnNames) {
        appendIdentifiers(primaryKeyColumnNames);
        finalizeStatementParts();

        StringBuilder sql = new StringBuilder();
        sql.append("select ");
        sql.append(columnsBuilder);
        sql.append(" from ");
        sql.append(tableName);
        return sql.toString();
    }

    protected void appendIdentifiers(Set<String> primaryKeyColumnNames) {
        if (primaryKeyColumnNames.isEmpty()) {
            return;
        }
        for (String primaryKeyColumnName : primaryKeyColumnNames) {
            columnsBuilder.append(nameProcessor.getQuotedName(primaryKeyColumnName));
            columnsBuilder.append(", ");
        }
    }

    protected void finalizeStatementParts() {
        if (hasColumns()) {
            columnsBuilder.setLength(columnsBuilder.length() - 2);
        } else {
            columnsBuilder.append("1");
        }
    }

    protected boolean hasColumns() {
        return columnsBuilder.length() != 0;
    }
}