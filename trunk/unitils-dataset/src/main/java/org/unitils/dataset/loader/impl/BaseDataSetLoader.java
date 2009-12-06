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
package org.unitils.dataset.loader.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.UnitilsException;
import org.unitils.dataset.core.*;
import org.unitils.dataset.loader.DataSetLoader;
import org.unitils.dataset.util.PreparedStatementWrapper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public abstract class BaseDataSetLoader implements DataSetLoader {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(BaseDataSetLoader.class);

    protected DataSource dataSource;


    public void init(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    //todo dataset
    // todo case sensitive, literal token, variable token
    public void load(DataSet dataSet, List<String> variables) {
        try {
            loadDataSet(dataSet, variables);
        } catch (UnitilsException e) {
            throw e;
        } catch (Exception e) {
            throw new UnitilsException("Unable to load data set.", e);
        }
    }

    protected abstract PreparedStatementWrapper createPreparedStatementWrapper(String schemaName, String tableName, Connection connection) throws Exception;


    protected void loadDataSet(DataSet dataSet, List<String> variables) throws SQLException {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            for (Schema schema : dataSet.getSchemas()) {
                loadSchema(schema, dataSet, variables, connection);
            }
        } finally {
            connection.close();
        }
    }

    protected void loadSchema(Schema schema, DataSet dataSet, List<String> variables, Connection connection) throws SQLException {
        String schemaName = schema.getName();
        for (Table table : schema.getTables()) {
            loadTable(schemaName, table, dataSet, variables, connection);
        }
    }

    protected void loadTable(String schemaName, Table table, DataSet dataSet, List<String> variables, Connection connection) {
        String tableName = table.getName();
        for (Row row : table.getRows()) {
            if (row.getNrOfColumns() == 0) {
                continue;
            }
            loadRowHandleExceptions(schemaName, tableName, row, dataSet, variables, connection);
        }
    }

    protected int loadRowHandleExceptions(String schemaName, String tableName, Row row, DataSet dataSet, List<String> variables, Connection connection) {
        try {
            return loadRow(schemaName, tableName, row, dataSet, variables, connection);
        } catch (Exception e) {
            throw new UnitilsException("Unable to load data set row for schema: " + schemaName + ", table: " + tableName + ", row: [" + row + "], variables: " + variables, e);
        }
    }

    protected int loadRow(String schemaName, String tableName, Row row, DataSet dataSet, List<String> variables, Connection connection) throws Exception {
        PreparedStatementWrapper preparedStatementWrapper = createPreparedStatementWrapper(schemaName, tableName, connection);
        return loadRow(row, dataSet, variables, preparedStatementWrapper);
    }

    protected int loadRow(Row row, DataSet dataSet, List<String> variables, PreparedStatementWrapper preparedStatementWrapper) throws SQLException {
        for (Column column : row.getColumns()) {
            String variablesReplacedValue = replaceVariableDeclarations(column.getValue(), variables, dataSet.getVariableToken());

            String literalValue = getLiteralValue(variablesReplacedValue, dataSet.getLiteralToken());
            if (literalValue != null) {
                preparedStatementWrapper.addLiteralColumn(column.getName(), literalValue);
            } else {
                String value = replaceEscapedLiteralValue(variablesReplacedValue, dataSet.getLiteralToken());
                preparedStatementWrapper.addColumn(column.getName(), value);
            }
        }
        return preparedStatementWrapper.executeUpdate();
    }


    protected String replaceVariableDeclarations(String value, List<String> variables, char variableToken) {
        StringBuilder valueStringBuilder = new StringBuilder(value);
        for (int variableIndex = 0; variableIndex < variables.size(); variableIndex++) {
            replaceVariableDeclaration(valueStringBuilder, variableToken, variableIndex, variables.get(variableIndex));
        }
        return valueStringBuilder.toString();
    }

    protected void replaceVariableDeclaration(StringBuilder valueStringBuilder, char variableToken, int variableIndex, String variable) {
        String variableDeclaration = "" + variableToken + variableIndex;
        int index = 0;
        while ((index = valueStringBuilder.indexOf(variableDeclaration, index)) != -1) {
            if (index > 0 && valueStringBuilder.charAt(index - 1) == variableToken) {
                valueStringBuilder.deleteCharAt(index - 1);
            } else {
                valueStringBuilder.replace(index, index + 2, variable);
            }
        }
    }


    protected String replaceEscapedLiteralValue(String value, char literalToken) {
        if (value.startsWith("" + literalToken + literalToken)) {
            return value.substring(1);
        }
        return value;
    }

    protected String getLiteralValue(String value, char literalToken) {
        if (value.startsWith("" + literalToken) && !value.startsWith("" + literalToken + literalToken)) {
            return value.substring(1);
        }
        return null;
    }

}