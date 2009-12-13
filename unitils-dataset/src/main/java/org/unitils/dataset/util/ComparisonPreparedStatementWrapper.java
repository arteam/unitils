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
package org.unitils.dataset.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ComparisonPreparedStatementWrapper extends PreparedStatementWrapper {

    protected StringBuilder columnsBuilder = new StringBuilder();
    protected List<String> statementParameters = new ArrayList<String>();


    public ComparisonPreparedStatementWrapper(String schemaName, String tableName, Connection connection) throws SQLException {
        super(schemaName, tableName, connection);
    }


    @Override
    protected void addColumnName(String columnName, boolean primaryKey) {
        columnsBuilder.append(columnName);
        columnsBuilder.append(", ");
    }

    @Override
    protected void addValue(String value, boolean primaryKey) {
        columnsBuilder.append(value);
        columnsBuilder.append(", ");
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
        sql.append("select ");
        sql.append(columnsBuilder);
        sql.append(" from ");
        sql.append(schemaName);
        sql.append(".");
        sql.append(tableName);
        return sql.toString();
    }

    protected void finalizeStatementParts() {
        columnsBuilder.setLength(columnsBuilder.length() - 2);
    }
}