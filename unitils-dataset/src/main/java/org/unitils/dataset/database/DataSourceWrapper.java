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
import org.dbmaintain.database.IdentifierProcessor;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.unitils.core.UnitilsException;
import org.unitils.database.UnitilsDataSource;
import org.unitils.dataset.model.database.Column;
import org.unitils.dataset.model.database.TableName;
import org.unitils.dataset.model.dataset.DataSetRow;
import org.unitils.dataset.model.dataset.DataSetSettings;
import org.unitils.dataset.model.dataset.DataSetValue;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static org.unitils.database.util.DbUtils.close;


/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DataSourceWrapper {

    public static final int SQL_TYPE_UNKNOWN = -1;

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DataSourceWrapper.class);

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


    public String removeIdentifierQuotes(String columnName) {
        return identifierProcessor.removeIdentifierQuotes(columnName);
    }

    /**
     * Gets the table name prefixed with the schema name and quoted if it is a case-sensitive name.
     *
     * @param schemaName    The schema name in the correct case, not null
     * @param tableName     The table name in the correct case, not null
     * @param caseSensitive True if the names are case sensitive and should be quoted
     * @return The qualified table name, not null
     */
    public String getQualifiedTableName(String schemaName, String tableName, boolean caseSensitive) {
        if (caseSensitive) {
            schemaName = identifierProcessor.quoted(schemaName);
            tableName = identifierProcessor.quoted(tableName);
        }
        return schemaName + "." + tableName;
    }

    /**
     * Gets the column name in the correct case and quoted if it's a case-sensitive name.
     *
     * @param columnName      The column name, not null
     * @param dataSetSettings The data set settings, not null
     * @return The column name in the correct case, not null
     */
    public String getCorrectCaseColumnName(String columnName, DataSetSettings dataSetSettings) {
        boolean caseSensitive = dataSetSettings.isCaseSensitive();
        if (caseSensitive) {
            return identifierProcessor.quoted(columnName);
        } else {
            return identifierProcessor.toCorrectCaseIdentifier(columnName);
        }
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
            tablePrimaryKeysCache.put(tableName, primaryKeyColumnNames);

        } finally {
            close(connection, null, resultSet);
        }
        if (primaryKeyColumnNames.isEmpty()) {
            assertTableExists(tableName);
        }
        return primaryKeyColumnNames;
    }

    protected void assertTableExists(TableName tableName) throws SQLException {
        Set<TableName> tableNames = getTableNames(tableName.getSchemaName());
        if (!tableNames.contains(tableName)) {
            throw new UnitilsException("Table does not exist: " + tableName);
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


    public void addExtraParentColumnsForChild(DataSetRow childRow) throws SQLException {
        DataSetRow parentRow = childRow.getParentRow();
        if (parentRow == null) {
            return;
        }

        boolean caseSensitive = childRow.getDataSetSettings().isCaseSensitive();
        Map<String, String> parentChildColumnNames = getChildForeignKeyColumns(parentRow, childRow, caseSensitive);

        for (Map.Entry<String, String> entry : parentChildColumnNames.entrySet()) {
            String parentColumnName = entry.getKey();
            String childColumnName = entry.getValue();

            DataSetValue parentDataSetValue = parentRow.getDataSetColumn(parentColumnName);
            if (parentDataSetValue == null) {
                throw new UnitilsException("Unable to add parent columns to child row. No value found in parent for column " + parentColumnName + ". This value is needed for child column " + childColumnName);
            }

            DataSetValue existingChildDataSetValue = childRow.removeColumn(childColumnName);
            if (existingChildDataSetValue != null) {
                logger.warn("Child row contained a value for a parent foreign key column: " + existingChildDataSetValue + ". This value will be ignored and overridden by the actual value of the parent row: " + parentDataSetValue);
            }

            String parentValue = parentDataSetValue.getValue();
            DataSetValue parentChildValue = new DataSetValue(childColumnName, parentValue);
            childRow.addDataSetValue(parentChildValue);
        }
    }

    protected Map<String, String> getChildForeignKeyColumns(DataSetRow parentRow, DataSetRow childRow, boolean caseSensitive) throws SQLException {
        Map<String, String> result = new LinkedHashMap<String, String>();

        Connection connection = getConnection();
        ResultSet resultSet = null;
        try {
            TableName parentTableName = getTableName(parentRow.getSchemaName(), parentRow.getTableName(), caseSensitive);
            TableName childTableName = getTableName(childRow.getSchemaName(), childRow.getTableName(), caseSensitive);

            resultSet = connection.getMetaData().getImportedKeys(null, childTableName.getSchemaName(), childTableName.getTableName());
            while (resultSet.next()) {
                String parentForeignKeySchemaName = resultSet.getString("PKTABLE_SCHEM");
                String parentForeignKeyTableName = resultSet.getString("PKTABLE_NAME");
                if (!parentTableName.getSchemaName().equals(parentForeignKeySchemaName) || !parentTableName.getTableName().equals(parentForeignKeyTableName)) {
                    continue;
                }
                String parentForeignKeyColumnName = resultSet.getString("PKCOLUMN_NAME");
                String childForeignKeyColumnName = resultSet.getString("FKCOLUMN_NAME");
                result.put(parentForeignKeyColumnName, childForeignKeyColumnName);
            }
            if (result.isEmpty()) {
                throw new UnitilsException("Unable to get foreign key columns for child table: " + childRow + ". No foreign key relationship found with parent table: " + parentRow);
            }
            return result;
        } finally {
            close(connection, null, resultSet);
        }
    }
}