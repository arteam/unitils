/*
 * Copyright 2006-2009,  Unitils.org
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.dataset.core.Column;
import org.unitils.dataset.core.Value;

import java.sql.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public abstract class BasePreparedStatement {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(BasePreparedStatement.class);

    protected String schemaName;
    protected String tableName;
    protected Connection connection;
    protected PreparedStatement preparedStatement;

    protected Set<String> primaryKeyColumnNames;
    protected Set<String> remainingPrimaryKeyColumnNames;


    protected BasePreparedStatement(String schemaName, String tableName, Connection connection) throws SQLException {
        this.schemaName = schemaName;
        this.tableName = tableName;
        this.connection = connection;
        this.primaryKeyColumnNames = getPrimaryKeyColumnNames(schemaName, tableName, connection);
        this.remainingPrimaryKeyColumnNames = new HashSet<String>(primaryKeyColumnNames);
    }

    public int executeUpdate() throws SQLException {
        preparedStatement = buildPreparedStatement();
        return preparedStatement.executeUpdate();
    }

    public void close() throws SQLException {
        if (preparedStatement != null) {
            preparedStatement.close();
        }
    }

    public void addColumn(Column column, List<String> variables) {
        boolean primaryKey = isPrimaryKeyColumn(column);

        Value value = column.getValue(variables);
        if (value.isLiteralValue()) {
            addLiteralColumn(column.getName(), value.getValue(), primaryKey);
        } else {
            addColumn(column.getName(), value.getValue(), primaryKey);
        }
    }

    protected void addColumn(String columnName, String value, boolean primaryKey) {
        addColumnName(columnName, primaryKey);
        addValue("?", primaryKey);
        addStatementParameter(value, primaryKey);
    }

    protected void addLiteralColumn(String columnName, String value, boolean primaryKey) {
        addColumnName(columnName, primaryKey);
        addValue(value, primaryKey);
    }

    protected abstract void addColumnName(String columnName, boolean primaryKey);

    protected abstract void addValue(String value, boolean primaryKey);

    protected abstract void addStatementParameter(String value, boolean primaryKey);

    protected abstract String buildStatement();

    protected abstract List<String> getStatementParameters();


    public Set<String> getPrimaryKeyColumnNames() {
        return primaryKeyColumnNames;
    }


    protected PreparedStatement buildPreparedStatement() throws SQLException {
        String sql = buildStatement();
        List<String> statementValues = getStatementParameters();
        logStatement(sql, statementValues);

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        setStatementValues(preparedStatement, statementValues);
        return preparedStatement;
    }

    protected void setStatementValues(PreparedStatement preparedStatement, List<String> statementValues) throws SQLException {
        if (statementValues.isEmpty()) {
            return;
        }
        ParameterMetaData parameterMetaData = preparedStatement.getParameterMetaData();

        int index = 1;
        for (String value : statementValues) {
            int columnTypeInDatabase = parameterMetaData.getParameterType(index);
            preparedStatement.setObject(index++, value, columnTypeInDatabase);
        }
    }

    protected void logStatement(String sql, List<String> statementValues) {
        if (statementValues.isEmpty()) {
            logger.debug(sql);
        } else {
            logger.debug(sql + " <- " + statementValues);
        }
    }

    protected Set<String> getPrimaryKeyColumnNames(String schemaName, String tableName, Connection connection) throws SQLException {
        Set<String> primaryKeyColumnNames = new HashSet<String>();
        ResultSet resultSet = connection.getMetaData().getPrimaryKeys(null, schemaName, tableName);
        while (resultSet.next()) {
            primaryKeyColumnNames.add(resultSet.getString("COLUMN_NAME"));
        }
        return primaryKeyColumnNames;
    }

    protected boolean isPrimaryKeyColumn(Column column) {
        for (String primaryKeyColumnName : primaryKeyColumnNames) {
            if (column.hasName(primaryKeyColumnName)) {
                remainingPrimaryKeyColumnNames.remove(primaryKeyColumnName);
                return true;
            }
        }
        return false;
    }


}