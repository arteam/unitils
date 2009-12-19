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
public class TableContentPreparedStatement extends QueryPreparedStatement<QueryResultSet> {


    public TableContentPreparedStatement(String schemaName, String tableName, Connection connection) throws SQLException {
        super(schemaName, tableName, connection);
    }

    @Override
    protected String buildStatement() {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from ");
        sql.append(schemaName);
        sql.append(".");
        sql.append(tableName);
        return sql.toString();
    }

    @Override
    protected QueryResultSet createResultSetWrapper(ResultSet resultSet) {
        return new QueryResultSet(resultSet, primaryKeyColumnNames);
    }

    @Override
    protected void addColumnName(String columnName, boolean primaryKey) {
    }

    @Override
    protected void addValue(String value, boolean primaryKey) {
    }

    @Override
    protected void addStatementParameter(String value, boolean primaryKey) {
    }

    @Override
    protected List<String> getStatementParameters() {
        return new ArrayList<String>();
    }


}