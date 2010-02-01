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

import org.unitils.dataset.core.ProcessedColumn;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.unitils.dataset.util.PreparedStatementUtils.executeUpdate;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class InsertRowLoader extends BaseRowLoader {


    protected int load(String tableName, List<ProcessedColumn> processedColumns, Connection connection) throws SQLException {
        StringBuilder columnsPart = new StringBuilder();
        StringBuilder valuesPart = new StringBuilder();
        List<String> statementValues = new ArrayList<String>();

        for (ProcessedColumn processedColumn : processedColumns) {
            addColumnToStatementParts(processedColumn, columnsPart, valuesPart, statementValues);
        }
        String sql = createStatement(tableName, columnsPart, valuesPart);
        return executeUpdate(sql, statementValues, connection);
    }


    protected void addColumnToStatementParts(ProcessedColumn column, StringBuilder columnsPart, StringBuilder valuesPart, List<String> statementValues) {
        columnsPart.append(column.getName());
        columnsPart.append(", ");
        if (column.isLiteralValue()) {
            valuesPart.append(column.getValue());
        } else {
            valuesPart.append('?');
            statementValues.add(column.getValue());
        }
        valuesPart.append(", ");
    }

    protected String createStatement(String tableName, StringBuilder columnsPart, StringBuilder valuesPart) {
        columnsPart.setLength(columnsPart.length() - 2);
        valuesPart.setLength(valuesPart.length() - 2);

        StringBuilder sql = new StringBuilder();
        sql.append("insert into ");
        sql.append(tableName);
        sql.append(" (");
        sql.append(columnsPart);
        sql.append(") values (");
        sql.append(valuesPart);
        sql.append(')');
        return sql.toString();
    }

}