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
package org.unitils.dataset.assertstrategy.impl;

import org.unitils.core.util.DbUtils;
import org.unitils.dataset.core.database.Column;
import org.unitils.dataset.core.database.Row;
import org.unitils.dataset.core.database.Value;
import org.unitils.dataset.sqltypehandler.SqlTypeHandler;
import org.unitils.dataset.sqltypehandler.SqlTypeHandlerRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class TableContents {

    protected String qualifiedTableName;
    protected List<Column> columns;

    protected Connection connection;
    protected PreparedStatement preparedStatement;
    protected ResultSet resultSet;

    protected SqlTypeHandlerRepository sqlTypeHandlerRepository;
    protected Set<String> primaryKeyColumnNames;

    protected int rowIndex;

    protected boolean useRowIndexAsIdentifier = false;


    public TableContents(String qualifiedTableName, List<Column> columns, SqlTypeHandlerRepository sqlTypeHandlerRepository, Set<String> primaryKeyColumnNames, Connection connection, PreparedStatement preparedStatement, ResultSet resultSet) {
        this.qualifiedTableName = qualifiedTableName;
        this.columns = columns;
        this.sqlTypeHandlerRepository = sqlTypeHandlerRepository;
        this.primaryKeyColumnNames = primaryKeyColumnNames;

        this.connection = connection;
        this.preparedStatement = preparedStatement;
        this.resultSet = resultSet;

        this.rowIndex = 0;
        if (primaryKeyColumnNames.isEmpty()) {
            useRowIndexAsIdentifier = true;
        }
    }

    public void close() throws SQLException {
        DbUtils.close(connection, preparedStatement, resultSet);
    }


    public String getRowIdentifier() throws SQLException {
        StringBuilder identifier = new StringBuilder();
        if (useRowIndexAsIdentifier) {
            identifier.append(rowIndex);
            return identifier.toString();
        }
        identifier.append('#');
        for (String primaryKeyColumnName : primaryKeyColumnNames) {
            String primaryKeyValue = resultSet.getString(primaryKeyColumnName);
            identifier.append(primaryKeyColumnName);
            identifier.append('=');
            identifier.append(primaryKeyValue);
            identifier.append(',');
        }
        identifier.setLength(identifier.length() - 1);
        identifier.append('#');
        return identifier.toString();
    }


    public Row getRow() throws Exception {
        if (!resultSet.next()) {
            return null;
        }
        rowIndex++;
        Row row = new Row(getRowIdentifier(), qualifiedTableName);

        for (int i = 0; i < columns.size(); i++) {
            Column column = columns.get(i);

            int sqlType = column.getSqlType();
            SqlTypeHandler<?> sqlTypeHandler = sqlTypeHandlerRepository.getSqlTypeHandler(sqlType);

            Object value = sqlTypeHandler.getResultSetValue(resultSet, i + 1, sqlType);
            row.addValue(new Value(value, false, column));
        }
        return row;
    }

    protected Object getValue(Column column, SqlTypeHandler sqlTypeHandler) throws Exception {
        // todo not correct
        int index = columns.indexOf(column) + 1;

        int sqlType = column.getSqlType();
        return sqlTypeHandler.getResultSetValue(resultSet, index, sqlType);
    }

    public int getNrOfColumns() throws SQLException {
        return columns.size();
    }

    public List<String> getColumnNames() throws SQLException {
        int nrOfColumns = getNrOfColumns();
        List<String> columnNames = new ArrayList<String>(nrOfColumns);
        for (int i = 0; i < nrOfColumns; i++) {
            columnNames.add(getColumnName(i));
        }
        return columnNames;
    }

    public String getColumnName(int columnIndex) throws SQLException {
        return resultSet.getMetaData().getColumnName(columnIndex + 1);
    }
}