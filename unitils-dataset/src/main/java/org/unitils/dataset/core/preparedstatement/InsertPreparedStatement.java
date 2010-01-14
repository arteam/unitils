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
package org.unitils.dataset.core.preparedstatement;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class InsertPreparedStatement extends InsertUpdatePreparedStatement {

    protected StringBuilder columnNamesPartBuilder = new StringBuilder();
    protected StringBuilder valuesPartBuilder = new StringBuilder();
    protected List<String> statementParameters = new ArrayList<String>();


    public InsertPreparedStatement(String schemaName, String tableName, Connection connection) throws SQLException {
        super(schemaName, tableName, connection);
    }


    @Override
    protected void addColumnName(String columnName, boolean primaryKey) {
        columnNamesPartBuilder.append(columnName);
        columnNamesPartBuilder.append(", ");
    }

    @Override
    protected void addValue(String value, boolean primaryKey) {
        valuesPartBuilder.append(value);
        valuesPartBuilder.append(", ");
    }

    @Override
    protected void addStatementParameter(String value, boolean primaryKey) {
        statementParameters.add(value);
    }

    @Override
    protected List<String> getStatementParameters() {
        return statementParameters;
    }

    @Override
    protected String buildStatement() {
        finalizeStatementParts();

        StringBuilder sql = new StringBuilder();
        sql.append("insert into ");
        sql.append(schemaName);
        sql.append(".");
        sql.append(tableName);
        sql.append(" (");
        sql.append(columnNamesPartBuilder);
        sql.append(") values (");
        sql.append(valuesPartBuilder);
        sql.append(')');
        return sql.toString();
    }

    protected void finalizeStatementParts() {
        columnNamesPartBuilder.setLength(columnNamesPartBuilder.length() - 2);
        valuesPartBuilder.setLength(valuesPartBuilder.length() - 2);
    }
}