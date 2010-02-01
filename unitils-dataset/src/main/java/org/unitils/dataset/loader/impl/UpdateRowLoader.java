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
package org.unitils.dataset.loader.impl;

import org.unitils.core.UnitilsException;
import org.unitils.dataset.core.ProcessedColumn;
import org.unitils.dataset.core.Row;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.unitils.dataset.util.PreparedStatementUtils.executeUpdate;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class UpdateRowLoader extends BaseRowLoader {


    protected int load(String tableName, List<ProcessedColumn> processedColumns, Connection connection) throws SQLException {
        StringBuilder columnsPart = new StringBuilder();
        StringBuilder wherePart = new StringBuilder();
        List<String> columnsValues = new ArrayList<String>();
        List<String> whereValues = new ArrayList<String>();

        for (ProcessedColumn processedColumn : processedColumns) {
            if (processedColumn.isPrimaryKey()) {
                addColumnToStatementPart(processedColumn, wherePart, whereValues);
            }
            addColumnToStatementPart(processedColumn, columnsPart, columnsValues);
        }
        String sql = createStatement(tableName, columnsPart, wherePart);
        List<String> statementValues = new ArrayList<String>(columnsValues);
        statementValues.addAll(whereValues);
        int nrUpdates = executeUpdate(sql, statementValues, connection);
        if (nrUpdates == 0) {
            handleNoUpdatesPerformed();
        }
        return nrUpdates;
    }

    protected void handleNoUpdatesPerformed() {
        throw new UnitilsException("Unable to update record for data set. No record found in database with matching primary key values.");
    }


    protected void addColumnToStatementPart(ProcessedColumn column, StringBuilder statementPart, List<String> parameters) {
        statementPart.append(column.getName());
        statementPart.append('=');
        if (column.isLiteralValue()) {
            statementPart.append(column.getValue());
        } else {
            statementPart.append('?');
            parameters.add(column.getValue());
        }
        statementPart.append(", ");
    }

    protected String createStatement(String tableName, StringBuilder columnsPart, StringBuilder wherePart) {
        columnsPart.setLength(columnsPart.length() - 2);
        wherePart.setLength(wherePart.length() - 2);

        StringBuilder sql = new StringBuilder();
        sql.append("update ");
        sql.append(tableName);
        sql.append(" set ");
        sql.append(columnsPart);
        sql.append(" where ");
        sql.append(wherePart);
        return sql.toString();
    }


    protected List<ProcessedColumn> processColumns(Row row, List<String> variables, Set<String> unusedPrimaryKeyColumnNames) {
        List<ProcessedColumn> processedColumns = super.processColumns(row, variables, unusedPrimaryKeyColumnNames);
        if (!unusedPrimaryKeyColumnNames.isEmpty()) {
            throw new UnitilsException("Unable to create update statement for data set. Unable to determine record to update because there were no values for primary keys specified. Missing primary key columns: " + unusedPrimaryKeyColumnNames);
        }
        return processedColumns;
    }

}