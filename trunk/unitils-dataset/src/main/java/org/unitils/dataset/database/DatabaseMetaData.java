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
import org.dbmaintain.database.Database;
import org.unitils.core.UnitilsException;
import org.unitils.dataset.model.database.Column;
import org.unitils.dataset.model.dataset.DataSetRow;
import org.unitils.dataset.model.dataset.DataSetValue;
import org.unitils.dataset.sqltypehandler.SqlTypeHandlerRepository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static org.unitils.core.util.DbUtils.close;


/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DatabaseMetaData {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DatabaseMetaData.class);

    public static final int SQL_TYPE_UNKNOWN = -1;

    protected Database defaultDatabase;
    protected SqlTypeHandlerRepository sqlTypeHandlerRepository;

    protected Map<String, Set<String>> tablePrimaryKeysCache = new HashMap<String, Set<String>>();
    protected Map<String, Map<String, Integer>> tableColumnSqlTypesCache = new HashMap<String, Map<String, Integer>>();
    private Set<String> confirmedTableNames = new HashSet<String>();

    public DatabaseMetaData(Database defaultDatabase, SqlTypeHandlerRepository sqlTypeHandlerRepository) {
        this.defaultDatabase = defaultDatabase;
        this.sqlTypeHandlerRepository = sqlTypeHandlerRepository;
    }


    public String getSchemaName() {
        return defaultDatabase.getDefaultSchemaName();
    }

    /**
     * Gets the quoted name if it is a case-sensitive name.
     *
     * @param name The name to quote, not null
     * @return The quoted name or the original name if quoting is not supported or not case sensitive
     */
    public String quoteIdentifier(String name) {
        return defaultDatabase.quoted(name);
    }

    public String toCorrectCaseIdentifier(String name) {
        return defaultDatabase.toCorrectCaseIdentifier(name);
    }

    public String removeIdentifierQuotes(String schemaName) {
        return defaultDatabase.removeIdentifierQuotes(schemaName);
    }

    public Connection getConnection() throws SQLException {
        // todo move to db utils and register with spring
        return defaultDatabase.getDataSource().getConnection();
    }

    public Database getDefaultDatabase() {
        return defaultDatabase;
    }

    public Set<String> getPrimaryKeyColumnNames(String qualifiedTableName) throws SQLException {
        Set<String> primaryKeyColumnNames = tablePrimaryKeysCache.get(qualifiedTableName);
        if (primaryKeyColumnNames != null) {
            return primaryKeyColumnNames;
        }
        primaryKeyColumnNames = new LinkedHashSet<String>();

        Connection connection = getConnection();
        ResultSet resultSet = null;
        try {
            String schemaName = getSchemaName(qualifiedTableName);
            String tableName = getTableName(qualifiedTableName);

            resultSet = connection.getMetaData().getPrimaryKeys(null, schemaName, tableName);
            while (resultSet.next()) {
                primaryKeyColumnNames.add(resultSet.getString("COLUMN_NAME"));
            }
            tablePrimaryKeysCache.put(qualifiedTableName, primaryKeyColumnNames);
            return primaryKeyColumnNames;

        } finally {
            close(connection, null, resultSet);
        }
    }


    public List<Column> getColumns(String qualifiedTableName) throws SQLException {
        List<Column> columns = new ArrayList<Column>();
        Set<String> primaryKeyColumnNames = getPrimaryKeyColumnNames(qualifiedTableName);

        Connection connection = getConnection();
        ResultSet resultSet = null;
        try {
            String schemaName = getSchemaName(qualifiedTableName);
            String tableName = getTableName(qualifiedTableName);

            resultSet = connection.getMetaData().getColumns(null, schemaName, tableName, null);
            while (resultSet.next()) {
                String columnName = resultSet.getString("COLUMN_NAME");
                int sqlType = resultSet.getInt("DATA_TYPE");
                boolean primaryKey = primaryKeyColumnNames.contains(columnName);

                Column column = new Column(columnName, sqlType, primaryKey);
                columns.add(column);
            }
            return columns;
        } finally {
            close(connection, null, resultSet);
        }
    }

    public int getColumnSqlType(String qualifiedTableName, String columnName) throws SQLException {
        Map<String, Integer> columnSqlTypes = getColumnSqlTypes(qualifiedTableName);

        Integer columnSqlType = columnSqlTypes.get(columnName);
        if (columnSqlType == null) {
            return SQL_TYPE_UNKNOWN;
        }
        return columnSqlType;
    }

    public Boolean tableExists(String qualifiedTableName) throws SQLException {
        if (confirmedTableNames.contains(qualifiedTableName)) {
            return true;
        }
        Connection connection = getConnection();
        ResultSet tables = connection.getMetaData().getTables(null, getSchemaName(qualifiedTableName), getTableName(qualifiedTableName), null);
        if (tables.first()) {
            confirmedTableNames.add(qualifiedTableName);
            return true;
        }
        return false;
    }

    protected Map<String, Integer> getColumnSqlTypes(String qualifiedTableName) throws SQLException {
        Map<String, Integer> columnSqlTypes = tableColumnSqlTypesCache.get(qualifiedTableName);
        if (columnSqlTypes != null) {
            return columnSqlTypes;
        }

        columnSqlTypes = new HashMap<String, Integer>();
        Connection connection = getConnection();
        ResultSet resultSet = null;
        try {
            String schemaName = getSchemaName(qualifiedTableName);
            String tableName = getTableName(qualifiedTableName);

            resultSet = connection.getMetaData().getColumns(null, schemaName, tableName, null);
            while (resultSet.next()) {
                String columnName = resultSet.getString("COLUMN_NAME");
                int sqlType = resultSet.getInt("DATA_TYPE");
                columnSqlTypes.put(columnName, sqlType);
            }

            tableColumnSqlTypesCache.put(qualifiedTableName, columnSqlTypes);
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
            String correctCaseParentSchemaName = parentRow.getSchemaName();
            String correctCaseParentTableName = parentRow.getTableName();
            String correctCaseChildSchemaName = childRow.getSchemaName();
            String correctCaseChildTableName = childRow.getTableName();
            if (!caseSensitive) {
                correctCaseParentSchemaName = toCorrectCaseIdentifier(correctCaseParentSchemaName);
                correctCaseParentTableName = toCorrectCaseIdentifier(correctCaseParentTableName);
                correctCaseChildSchemaName = toCorrectCaseIdentifier(correctCaseChildSchemaName);
                correctCaseChildTableName = toCorrectCaseIdentifier(correctCaseChildTableName);
            }

            resultSet = connection.getMetaData().getImportedKeys(null, correctCaseChildSchemaName, correctCaseChildTableName);
            while (resultSet.next()) {
                String parentForeignKeySchemaName = resultSet.getString("PKTABLE_SCHEM");
                String parentForeignKeyTableName = resultSet.getString("PKTABLE_NAME");
                if (!correctCaseParentSchemaName.equals(parentForeignKeySchemaName) || !correctCaseParentTableName.equals(parentForeignKeyTableName)) {
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


    protected String getSchemaName(String qualifiedTableName) {
        int index = qualifiedTableName.indexOf('.');
        if (index == -1) {
            throw new UnitilsException("Unable to determine schema name for qualified table name " + qualifiedTableName);
        }
        String schemaName = qualifiedTableName.substring(0, index);
        return removeIdentifierQuotes(schemaName);
    }


    protected String getTableName(String qualifiedTableName) {
        int index = qualifiedTableName.indexOf('.');
        if (index == -1) {
            throw new UnitilsException("Unable to determine table name for qualified table name " + qualifiedTableName);
        }
        String tableName = qualifiedTableName.substring(index + 1);
        return removeIdentifierQuotes(tableName);
    }


}