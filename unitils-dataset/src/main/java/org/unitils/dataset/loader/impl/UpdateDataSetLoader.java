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
import org.unitils.dataset.core.DatabaseColumnWithValue;
import org.unitils.dataset.core.DatabaseRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class UpdateDataSetLoader extends BaseDataSetLoader {


    @Override
    protected int loadDatabaseRow(DatabaseRow databaseRow) throws Exception {
        List<DatabaseColumnWithValue> statementValues = new ArrayList<DatabaseColumnWithValue>();
        String sql = buildUpdateStatement(databaseRow, statementValues);

        int nrUpdates = databaseAccessor.executeUpdate(sql, statementValues);
        if (nrUpdates == 0) {
            handleNoUpdatesPerformed();
        }
        return nrUpdates;
    }


    protected void handleNoUpdatesPerformed() {
        throw new UnitilsException("Unable to update record for data set. No record found in database with matching primary key values.");
    }

    @Override
    protected void handleUnusedPrimaryKeyColumns(Set<String> unusedPrimaryKeyColumnNames) {
        throw new UnitilsException("Unable to create update statement for data set row. Unable to determine record to update because no values for primary keys were specified. Missing primary key columns: " + unusedPrimaryKeyColumnNames);
    }

    protected void handleNoPrimaryKeyColumnFound() {
        throw new UnitilsException("Unable to create update statement for data set row. Unable to determine record to update because no values for primary keys were specified.");
    }


    protected String buildUpdateStatement(DatabaseRow databaseRow, List<DatabaseColumnWithValue> statementValues) {
        List<DatabaseColumnWithValue> columnsValues = new ArrayList<DatabaseColumnWithValue>();
        List<DatabaseColumnWithValue> whereValues = new ArrayList<DatabaseColumnWithValue>();
        StringBuilder columnsPart = new StringBuilder();
        StringBuilder wherePart = new StringBuilder();

        boolean primaryKeyColumnFound = false;
        for (DatabaseColumnWithValue databaseColumn : databaseRow.getDatabaseColumnsWithValue()) {
            if (databaseColumn.isPrimaryKey()) {
                primaryKeyColumnFound = true;
                addColumnToStatementPart(databaseColumn, wherePart, whereValues);
            }
            addColumnToStatementPart(databaseColumn, columnsPart, columnsValues);
        }

        if (!primaryKeyColumnFound) {
            handleNoPrimaryKeyColumnFound();
        }

        statementValues.addAll(columnsValues);
        statementValues.addAll(whereValues);
        return createStatement(databaseRow.getQualifiedTableName(), columnsPart, wherePart);
    }

    protected void addColumnToStatementPart(DatabaseColumnWithValue databaseColumn, StringBuilder statementPart, List<DatabaseColumnWithValue> parameters) {
        statementPart.append(databaseColumn.getColumnName());
        statementPart.append('=');

        if (databaseColumn.isLiteralValue()) {
            statementPart.append(databaseColumn.getValue());
        } else {
            statementPart.append('?');
            parameters.add(databaseColumn);
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

}