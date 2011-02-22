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
package org.unitils.dataset.database;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.unitils.core.UnitilsException;
import org.unitils.dataset.model.database.Value;

import java.sql.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DatabaseAccessor {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DatabaseAccessor.class);

    private DataSourceWrapper dataSourceWrapper;


    public DatabaseAccessor(DataSourceWrapper dataSourceWrapper) {
        this.dataSourceWrapper = dataSourceWrapper;
    }

    public int executeUpdates(List<DatabaseStatement> updates) throws Exception {
        Connection connection = dataSourceWrapper.getConnection();
        try {
            int nbUpdatedRows = 0;
            for (DatabaseStatement update : updates) {
                nbUpdatedRows += executeUpdate(update, connection);
            }
            return nbUpdatedRows;
        } finally {
            close(connection);
        }
    }

    protected int executeUpdate(DatabaseStatement update, Connection connection) throws Exception {
        return executeUpdate(update.getSql(), update.getParameters(), connection);
    }

    public int executeUpdate(String sql, List<Value> statementValues) throws Exception {
        Connection connection = dataSourceWrapper.getConnection();
        try {
            return executeUpdate(sql, statementValues, connection);
        } finally {
            close(connection);
        }
    }

    protected int executeUpdate(String sql, List<Value> statementValues, Connection connection) throws Exception {
        logStatement(sql, statementValues);
        if (statementValues.isEmpty()) {
            Statement statement = connection.createStatement();
            try {
                return statement.executeUpdate(sql);
            } finally {
                close(statement);
            }
        } else {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            try {
                setStatementValues(preparedStatement, statementValues);
                return preparedStatement.executeUpdate();
            } finally {
                close(preparedStatement);
            }
        }
    }

    /**
     * Returns the items extracted from the result of the given query.
     *
     * @param sql the sql string for retrieving the items
     * @return The items, not null
     */
    public Set<String> getItemsAsStringSet(String sql) {
        logger.debug(sql);

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSourceWrapper.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            Set<String> result = new HashSet<String>();
            while (resultSet.next()) {
                result.add(resultSet.getString(1));
            }
            return result;

        } catch (Exception e) {
            throw new UnitilsException("Error while executing statement: " + sql, e);
        } finally {
            close(connection, statement, resultSet);
        }
    }

    protected void setStatementValues(PreparedStatement preparedStatement, List<Value> statementValues) throws Exception {
        if (statementValues == null || statementValues.isEmpty()) {
            return;
        }
        int index = 1;
        for (Value value : statementValues) {
            int sqlType = value.getColumn().getSqlType();
            preparedStatement.setObject(index++, value.getValue(), sqlType);
        }
    }


    protected void logStatement(String sql, List<Value> statementValues) {
        if (statementValues.isEmpty()) {
            logger.debug(sql);
        } else {
            StringBuilder message = new StringBuilder(sql);
            if (!statementValues.isEmpty()) {
                message.append(" <- ");
                for (Value statementValue : statementValues) {
                    message.append(statementValue.getValue());
                    message.append(", ");
                }
                message.setLength(message.length() - 2);
            }
            logger.debug(message);
        }
    }

    protected void close(Connection connection) {
        DataSourceUtils.releaseConnection(connection, dataSourceWrapper.unitilsDataSource.getDataSource());
    }

    protected void close(Connection connection, Statement statement, ResultSet resultSet) {
        close(statement);
        close(resultSet);
        close(connection);
    }

    protected void close(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                logger.warn("Unable to close resultset. Ignoring exception.");
            }
        }
    }

    protected void close(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                logger.warn("Unable to close statement. Ignoring exception.");
            }
        }
    }
}