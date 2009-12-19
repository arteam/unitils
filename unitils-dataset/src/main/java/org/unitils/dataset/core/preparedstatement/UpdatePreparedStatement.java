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

import org.unitils.core.UnitilsException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class UpdatePreparedStatement extends BasePreparedStatement {

    protected StringBuilder statementBuilder = new StringBuilder();
    protected StringBuilder whereClauseBuilder = new StringBuilder();

    protected List<String> whereValues = new ArrayList<String>();
    protected List<String> statementParameters = new ArrayList<String>();
    protected List<String> whereParameters = new ArrayList<String>();


    public UpdatePreparedStatement(String schemaName, String tableName, Connection connection) throws SQLException {
        super(schemaName, tableName, connection);
    }


    @Override
    protected void addColumnName(String columnName, boolean primaryKey) {
        if (primaryKey) {
            whereClauseBuilder.append(columnName);
            whereClauseBuilder.append('=');
        }
        statementBuilder.append(columnName);
        statementBuilder.append('=');
    }

    @Override
    protected void addValue(String value, boolean primaryKey) {
        if (primaryKey) {
            whereClauseBuilder.append(value);
            whereClauseBuilder.append(", ");
        }
        statementBuilder.append(value);
        statementBuilder.append(", ");
    }

    @Override
    protected void addStatementParameter(String value, boolean primaryKey) {
        if (primaryKey) {
            whereParameters.add(value);
        }
        statementParameters.add(value);
    }

    @Override
    protected List<String> getStatementParameters() {
        List<String> result = new ArrayList<String>(statementParameters);
        result.addAll(whereParameters);
        return result;
    }

    @Override
    protected String buildStatement() {
        assertAllPrimaryKeyColumnsUsed();
        finalizeStatementParts();

        StringBuilder sql = new StringBuilder();
        sql.append("update ");
        sql.append(schemaName);
        sql.append(".");
        sql.append(tableName);
        sql.append(" set ");
        sql.append(statementBuilder);
        sql.append(" where ");
        sql.append(whereClauseBuilder);
        return sql.toString();
    }

    protected void finalizeStatementParts() {
        statementBuilder.setLength(statementBuilder.length() - 2);
        whereClauseBuilder.setLength(whereClauseBuilder.length() - 2);
    }

    protected void assertAllPrimaryKeyColumnsUsed() {
        if (!remainingPrimaryKeyColumnNames.isEmpty()) {
            throw new UnitilsException("Unable to create update statement for data set. Unable to determine record to update because there were no values for primary keys specified. Missing primary key columns: " + remainingPrimaryKeyColumnNames);
        }
    }

}