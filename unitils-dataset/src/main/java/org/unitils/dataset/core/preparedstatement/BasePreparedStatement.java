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
import org.unitils.dataset.core.Row;
import org.unitils.dataset.core.Value;
import org.unitils.dataset.loader.impl.DatabaseMetaDataHelper;

import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

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

    protected DatabaseMetaDataHelper databaseMetaDataHelper;
    protected Set<String> primaryKeyColumnNames;
    protected Set<String> remainingPrimaryKeyColumnNames;


    protected BasePreparedStatement(String schemaName, String tableName, Connection connection) throws SQLException {
        this.schemaName = schemaName;
        this.tableName = tableName;
        this.connection = connection;

        databaseMetaDataHelper = createDatabaseMetaDataHelper(connection);
        this.primaryKeyColumnNames = databaseMetaDataHelper.getPrimaryKeyColumnNames(schemaName, tableName);
        this.remainingPrimaryKeyColumnNames = new HashSet<String>(primaryKeyColumnNames);
    }

    public void close() throws SQLException {
        if (preparedStatement != null) {
            preparedStatement.close();
        }
    }

    protected void addRow(Row row, List<String> variables) throws SQLException {
        List<Column> parentColumnsForChild = createExtraParentColumnsForChild(row);
        for (Column column : row.getColumns()) {
            Column parentColumn = getParentColumn(column, parentColumnsForChild);
            if (parentColumn != null) {
                logger.warn("Child row contained a value for a parent foreign key column: " + column + ". This value will be ignored and overridden by the actual value of the parent row: " + parentColumn);
                continue;
            }
            addColumn(column, variables);
        }
        for (Column foreignKeyColumn : parentColumnsForChild) {
            addColumn(foreignKeyColumn, variables);
        }
    }

    protected void addColumn(Column column, List<String> variables) {
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

    protected boolean isPrimaryKeyColumn(Column column) {
        for (String primaryKeyColumnName : primaryKeyColumnNames) {
            if (column.hasName(primaryKeyColumnName)) {
                remainingPrimaryKeyColumnNames.remove(primaryKeyColumnName);
                return true;
            }
        }
        return false;
    }

    protected List<Column> createExtraParentColumnsForChild(Row childRow) throws SQLException {
        List<Column> result = new ArrayList<Column>();
        Row parentRow = childRow.getParentRow();
        if (parentRow == null) {
            return result;
        }
        Map<String, String> parentChildColumnNames = databaseMetaDataHelper.getChildForeignKeyColumns(parentRow.getTable(), childRow.getTable());
        for (Map.Entry<String, String> entry : parentChildColumnNames.entrySet()) {
            String parentColumnName = entry.getKey();
            String childColumnName = entry.getValue();

            Column parentColumn = parentRow.getColumn(parentColumnName);
            if (parentColumn == null) {
                continue;
            }
            String parentValue = parentColumn.getOriginalValue();
            result.add(new Column(childColumnName, parentValue, true, (char) 0, (char) 0));
        }
        return result;
    }


    protected Column getParentColumn(Column column, List<Column> parentColumns) {
        for (Column foreignKeyColumn : parentColumns) {
            if (column.hasName(foreignKeyColumn.getName())) {
                return foreignKeyColumn;
            }
        }
        return null;
    }

    protected DatabaseMetaDataHelper createDatabaseMetaDataHelper(Connection connection) {
        return new DatabaseMetaDataHelper(connection);
    }

}