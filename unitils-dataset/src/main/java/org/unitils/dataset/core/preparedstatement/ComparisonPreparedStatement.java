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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ComparisonPreparedStatement extends QueryPreparedStatement<ComparisonResultSet> {

    protected StringBuilder identifiersBuilder = new StringBuilder();
    protected StringBuilder columnsBuilder = new StringBuilder();
    protected List<String> statementParameters = new ArrayList<String>();


    public ComparisonPreparedStatement(String schemaName, String tableName, Connection connection) throws SQLException {
        super(schemaName, tableName, connection);
    }

    @Override
    protected ComparisonResultSet createResultSetWrapper(ResultSet resultSet) {
        return new ComparisonResultSet(resultSet, primaryKeyColumnNames);
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

    protected void addStatementParameter(String value, boolean primaryKey) {
        statementParameters.add(value);
    }

    @Override
    protected List<String> getStatementParameters() {
        return statementParameters;
    }

    @Override
    protected String buildStatement() {
        buildIdentifierPart();
        finalizeStatementParts();

        StringBuilder sql = new StringBuilder();
        sql.append("select ");
        sql.append(columnsBuilder);
        sql.append(identifiersBuilder);
        sql.append(" from ");
        sql.append(schemaName);
        sql.append(".");
        sql.append(tableName);
        return sql.toString();
    }

    protected void buildIdentifierPart() {
        if (primaryKeyColumnNames.isEmpty()) {
            return;
        }
        identifiersBuilder.append(", ");
        for (String primaryKeyColumnName : primaryKeyColumnNames) {
            identifiersBuilder.append(primaryKeyColumnName);
            identifiersBuilder.append(", ");
        }
        identifiersBuilder.setLength(identifiersBuilder.length() - 2);
    }

    protected void finalizeStatementParts() {
        columnsBuilder.setLength(columnsBuilder.length() - 2);
    }

}