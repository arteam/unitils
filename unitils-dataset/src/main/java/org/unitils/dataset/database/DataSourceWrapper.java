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

import org.dbmaintain.database.IdentifierProcessor;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.unitils.core.UnitilsException;
import org.unitils.database.UnitilsDataSource;
import org.unitils.dataset.model.database.Column;
import org.unitils.dataset.model.database.TableName;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static org.unitils.database.util.DbUtils.close;


/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DataSourceWrapper {

    public static final int SQL_TYPE_UNKNOWN = -1;

    protected UnitilsDataSource unitilsDataSource;
    protected IdentifierProcessor identifierProcessor;

    protected Set<String> schemaNames;
    protected Map<TableName, Set<String>> tablePrimaryKeysCache = new HashMap<TableName, Set<String>>();
    protected Map<String, Set<TableName>> tableNamesCache = new HashMap<String, Set<TableName>>();
    protected Map<TableName, Set<Column>> columnsCache = new HashMap<TableName, Set<Column>>();
    protected Map<TableName, Map<String, Integer>> tableColumnSqlTypesCache = new HashMap<TableName, Map<String, Integer>>();


    public DataSourceWrapper(UnitilsDataSource unitilsDataSource, IdentifierProcessor identifierProcessor) {
        this.unitilsDataSource = unitilsDataSource;
        this.identifierProcessor = identifierProcessor;
    }


    public Connection getConnection() throws SQLException {
        DataSource dataSource = unitilsDataSource.getDataSource();
        return DataSourceUtils.getConnection(dataSource);
    }

    public String getDefaultSchemaName() {
        return identifierProcessor.getDefaultSchemaName();
    }

    public Set<String> getSchemaNames() {
        if (schemaNames != null) {
            return schemaNames;
        }
        schemaNames = new LinkedHashSet<String>();
        for (String schemaName : unitilsDataSource.getSchemaNames()) {
            String correctCaseSchemaName = identifierProcessor.toCorrectCaseIdentifier(schemaName);
            schemaNames.add(correctCaseSchemaName);
        }
        return schemaNames;
    }

    public TableName getTableName(String schemaName, String tableName, boolean caseSensitive) {
        if (schemaName == null) {
            schemaName = getDefaultSchemaName();
        }
        if (!caseSensitive) {
            schemaName = identifierProcessor.toCorrectCaseIdentifier(schemaName);
            tableName = identifierProcessor.toCorrectCaseIdentifier(tableName);
        }
        String qualifiedTableName = identifierProcessor.qualified(schemaName, tableName);
        return new TableName(schemaName, tableName, qualifiedTableName);
    }

    public Set<TableName> getTableNames(String schemaName) throws SQLException {
        Set<TableName> tableNames = tableNamesCache.get(schemaName);
        if (tableNames != null) {
            return tableNames;
        }
        tableNames = new LinkedHashSet<TableName>();

        Connection connection = getConnection();
        ResultSet resultSet = null;
        try {
            resultSet = connection.getMetaData().getTables(null, schemaName, null, null);
            while (resultSet.next()) {
                String tableName = resultSet.getString("TABLE_NAME");
                tableNames.add(getTableName(schemaName, tableName, true));
            }
            tableNamesCache.put(schemaName, tableNames);
            return tableNames;
        } finally {
            close(connection, null, resultSet);
        }
    }

    public Set<Column> getColumns(TableName tableName) throws SQLException {
        Set<Column> columns = columnsCache.get(tableName);
        if (columns != null) {
            return columns;
        }
        columns = new LinkedHashSet<Column>();
        Set<String> primaryKeyColumnNames = getPrimaryKeyColumnNames(tableName);

        Connection connection = getConnection();
        ResultSet resultSet = null;
        try {
            resultSet = connection.getMetaData().getColumns(null, tableName.getSchemaName(), tableName.getTableName(), null);
            while (resultSet.next()) {
                String columnName = resultSet.getString("COLUMN_NAME");
                int sqlType = resultSet.getInt("DATA_TYPE");
                boolean primaryKey = primaryKeyColumnNames.contains(columnName);

                Column column = new Column(columnName, sqlType, primaryKey);
                columns.add(column);
            }
            columnsCache.put(tableName, columns);
            return columns;
        } finally {
            close(connection, null, resultSet);
        }
    }

    public Set<String> getPrimaryKeyColumnNames(TableName tableName) throws SQLException {
        Set<String> primaryKeyColumnNames = tablePrimaryKeysCache.get(tableName);
        if (primaryKeyColumnNames != null) {
            return primaryKeyColumnNames;
        }
        primaryKeyColumnNames = new LinkedHashSet<String>();

        Connection connection = getConnection();
        ResultSet resultSet = null;
        try {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            resultSet = databaseMetaData.getPrimaryKeys(null, tableName.getSchemaName(), tableName.getTableName());
            while (resultSet.next()) {
                primaryKeyColumnNames.add(resultSet.getString("COLUMN_NAME"));
            }
            if (primaryKeyColumnNames.isEmpty()) {
                assertTableExists(tableName, databaseMetaData);
            }
            tablePrimaryKeysCache.put(tableName, primaryKeyColumnNames);
            return primaryKeyColumnNames;

        } finally {
            close(connection, null, resultSet);
        }
    }

    private void assertTableExists(TableName tableName, DatabaseMetaData databaseMetaData) throws SQLException {
        ResultSet resultSet = null;
        try {
            resultSet = databaseMetaData.getTables(null, tableName.getSchemaName(), tableName.getTableName(), null);
            if (!resultSet.next()) {
                throw new UnitilsException("Table does not exist: " + tableName);
            }
        } finally {
            resultSet.close();
        }
    }


    public int getColumnSqlType(TableName tableName, String columnName) throws SQLException {
        Map<String, Integer> columnSqlTypes = getColumnSqlTypes(tableName);

        Integer columnSqlType = columnSqlTypes.get(columnName);
        if (columnSqlType == null) {
            return SQL_TYPE_UNKNOWN;
        }
        return columnSqlType;
    }


    protected Map<String, Integer> getColumnSqlTypes(TableName tableName) throws SQLException {
        Map<String, Integer> columnSqlTypes = tableColumnSqlTypesCache.get(tableName);
        if (columnSqlTypes != null) {
            return columnSqlTypes;
        }
        columnSqlTypes = new HashMap<String, Integer>();

        Connection connection = getConnection();
        ResultSet resultSet = null;
        try {
            resultSet = connection.getMetaData().getColumns(null, tableName.getSchemaName(), tableName.getTableName(), null);
            while (resultSet.next()) {
                String columnName = resultSet.getString("COLUMN_NAME");
                int sqlType = resultSet.getInt("DATA_TYPE");
                columnSqlTypes.put(columnName, sqlType);
            }
            tableColumnSqlTypesCache.put(tableName, columnSqlTypes);
            return columnSqlTypes;
        } finally {
            close(connection, null, resultSet);
        }
    }


}