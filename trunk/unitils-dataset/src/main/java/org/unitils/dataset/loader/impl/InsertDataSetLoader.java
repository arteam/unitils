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

import org.unitils.dataset.core.DatabaseColumnWithValue;
import org.unitils.dataset.core.DatabaseRow;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class InsertDataSetLoader extends BaseDataSetLoader {


    protected int loadDatabaseRow(DatabaseRow databaseRow) throws Exception {
        StringBuilder columnsPart = new StringBuilder();
        StringBuilder valuesPart = new StringBuilder();
        List<DatabaseColumnWithValue> statementValues = new ArrayList<DatabaseColumnWithValue>();

        for (DatabaseColumnWithValue databaseColumn : databaseRow.getDatabaseColumnsWithValue()) {
            addColumnToStatementParts(databaseColumn, columnsPart, valuesPart, statementValues);
        }
        String sql = createStatement(databaseRow.getQualifiedTableName(), columnsPart, valuesPart);
        return databaseAccessor.executeUpdate(sql, statementValues);
    }


    protected void addColumnToStatementParts(DatabaseColumnWithValue databaseColumn, StringBuilder columnsPart, StringBuilder valuesPart, List<DatabaseColumnWithValue> statementValues) {
        columnsPart.append(databaseColumn.getColumnName());
        columnsPart.append(", ");

        if (databaseColumn.isLiteralValue()) {
            valuesPart.append(databaseColumn.getValue());
        } else {
            valuesPart.append('?');
            statementValues.add(databaseColumn);
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