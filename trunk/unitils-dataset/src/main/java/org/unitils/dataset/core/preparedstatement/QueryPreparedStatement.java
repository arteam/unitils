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

import org.unitils.dataset.core.Row;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public abstract class QueryPreparedStatement<T extends QueryResultSet> extends BasePreparedStatement {


    public QueryPreparedStatement(String schemaName, String tableName, Connection connection) throws SQLException {
        super(schemaName, tableName, connection);
    }


    public T executeQuery() throws SQLException {
        PreparedStatement preparedStatement = buildPreparedStatement();
        return createResultSetWrapper(preparedStatement.executeQuery());
    }

    public T executeQuery(Row row, List<String> variables) throws SQLException {
        addRow(row, variables);
        PreparedStatement preparedStatement = buildPreparedStatement();
        return createResultSetWrapper(preparedStatement.executeQuery());
    }

    protected abstract T createResultSetWrapper(ResultSet resultSet);

}