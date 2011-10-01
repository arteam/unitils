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
package org.unitils.dataset.loadstrategy.loader.impl;

import org.unitils.dataset.model.database.Row;
import org.unitils.dataset.model.database.TableName;
import org.unitils.dataset.model.database.Value;

import java.util.ArrayList;
import java.util.List;


/**
 * Data set loader that uses insert statements to load the data set rows.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class InsertDataSetLoader extends BaseDataSetLoader {


    protected int loadRow(Row row) throws Exception {
        StringBuilder columnsPart = new StringBuilder();
        StringBuilder valuesPart = new StringBuilder();
        List<Value> statementValues = new ArrayList<Value>();

        for (Value value : row.getValues()) {
            addValueToStatementParts(value, columnsPart, valuesPart, statementValues);
        }
        String sql = createStatement(row.getTableName(), columnsPart, valuesPart);
        return databaseAccessor.executeUpdate(sql, statementValues);
    }


    protected void addValueToStatementParts(Value value, StringBuilder columnsPart, StringBuilder valuesPart, List<Value> statementValues) {
        columnsPart.append(value.getColumn().getName());
        columnsPart.append(", ");

        if (value.isLiteralValue()) {
            valuesPart.append(value.getValue());
        } else {
            valuesPart.append('?');
            statementValues.add(value);
        }
        valuesPart.append(", ");
    }

    protected String createStatement(TableName tableName, StringBuilder columnsPart, StringBuilder valuesPart) {
        columnsPart.setLength(columnsPart.length() - 2);
        valuesPart.setLength(valuesPart.length() - 2);

        StringBuilder sql = new StringBuilder();
        sql.append("insert into ");
        sql.append(tableName.getQualifiedTableName());
        sql.append(" (");
        sql.append(columnsPart);
        sql.append(") values (");
        sql.append(valuesPart);
        sql.append(')');
        return sql.toString();
    }

}