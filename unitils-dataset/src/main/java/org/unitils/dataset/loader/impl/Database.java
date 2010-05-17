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
import org.unitils.core.dbsupport.DbSupport;
import org.unitils.dataset.core.DataSetColumn;
import org.unitils.dataset.core.DataSetRow;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static org.unitils.core.util.DbUtils.close;


/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class Database {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(Database.class);

    public static final int SQL_TYPE_UNKNOWN = -1;

    private DbSupport dbSupport;

    private Map<String, Set<String>> tablePrimaryKeysCache = new HashMap<String, Set<String>>();
    private Map<String, Map<String, Integer>> tableColumnSqlTypesCache = new HashMap<String, Map<String, Integer>>();


    public void init(DbSupport dbSupport) {
        this.dbSupport = dbSupport;
    }


    public String getSchemaName() {
        return dbSupport.getSchemaName();
    }

    /**
     * Gets the quoted name if it is a case-sensitive name.
     *
     * @param name The name to quote, not null
     * @return The quoted name or the original name if quoting is not supported or not case sensitive
     */
    public String quoteIdentifier(String name) {
        return dbSupport.quoted(name);
    }

    public String toCorrectCaseIdentifier(String name) {
        return dbSupport.toCorrectCaseIdentifier(name);
    }


    public Connection getConnection() throws SQLException {
        // todo move to db utils and register with spring
        return dbSupport.getSQLHandler().getDataSource().getConnection();
    }


    public Set<String> getPrimaryKeyColumnNames(String schemaName, String tableName, boolean caseSensitive) throws SQLException {
        String key = schemaName + "." + tableName;
        Set<String> primaryKeyColumnNames = tablePrimaryKeysCache.get(key);
        if (primaryKeyColumnNames != null) {
            return primaryKeyColumnNames;
        }
        primaryKeyColumnNames = new LinkedHashSet<String>();

        Connection connection = getConnection();
        ResultSet resultSet = null;
        try {
            String correctCaseSchemaName = schemaName;
            String correctCaseTableName = tableName;
            if (!caseSensitive) {
                correctCaseSchemaName = toCorrectCaseIdentifier(schemaName);
                correctCaseTableName = toCorrectCaseIdentifier(tableName);
            }

            resultSet = connection.getMetaData().getPrimaryKeys(null, correctCaseSchemaName, correctCaseTableName);
            while (resultSet.next()) {
                primaryKeyColumnNames.add(resultSet.getString("COLUMN_NAME"));
            }
            tablePrimaryKeysCache.put(key, primaryKeyColumnNames);
            return primaryKeyColumnNames;

        } finally {
            close(connection, null, resultSet);
        }
    }


    public int getColumnSqlType(String schemaName, String tableName, String columnName, boolean caseSensitive) throws SQLException {
        Map<String, Integer> columnSqlTypes = getColumnSqlTypes(schemaName, tableName, caseSensitive);

        String correctCaseColumnName = columnName;
        if (!caseSensitive) {
            correctCaseColumnName = toCorrectCaseIdentifier(columnName);
        }
        Integer columnSqlType = columnSqlTypes.get(correctCaseColumnName);
        if (columnSqlType == null) {
            // todo handle -1 in set object 
            return SQL_TYPE_UNKNOWN;
        }
        return columnSqlType;
    }

    protected Map<String, Integer> getColumnSqlTypes(String schemaName, String tableName, boolean caseSensitive) throws SQLException {
        String key = schemaName + "." + tableName;
        Map<String, Integer> columnSqlTypes = tableColumnSqlTypesCache.get(key);
        if (columnSqlTypes != null) {
            return columnSqlTypes;
        }

        columnSqlTypes = new HashMap<String, Integer>();
        Connection connection = getConnection();
        ResultSet resultSet = null;
        try {
            String correctCaseSchemaName = schemaName;
            String correctCaseTableName = tableName;
            if (!caseSensitive) {
                correctCaseSchemaName = toCorrectCaseIdentifier(schemaName);
                correctCaseTableName = toCorrectCaseIdentifier(tableName);
            }

            resultSet = connection.getMetaData().getColumns(null, correctCaseSchemaName, correctCaseTableName, null);
            while (resultSet.next()) {
                String columnName = resultSet.getString("COLUMN_NAME");
                int sqlType = resultSet.getInt("DATA_TYPE");
                columnSqlTypes.put(columnName, sqlType);
            }

            tableColumnSqlTypesCache.put(key, columnSqlTypes);
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

            DataSetColumn parentDataSetColumn = parentRow.getDataSetColumn(parentColumnName);
            if (parentDataSetColumn == null) {
                throw new UnitilsException("Unable to add parent columns to child row. No value found in parent for column " + parentColumnName + ". This value is needed for child column " + childColumnName);
            }

            DataSetColumn existingChildDataSetColumn = childRow.removeColumn(childColumnName);
            if (existingChildDataSetColumn != null) {
                logger.warn("Child row contained a value for a parent foreign key column: " + existingChildDataSetColumn + ". This value will be ignored and overridden by the actual value of the parent row: " + parentDataSetColumn);
            }

            String parentValue = parentDataSetColumn.getValue();
            DataSetColumn parentChildColumn = new DataSetColumn(childColumnName, parentValue);
            childRow.addDataSetColumn(parentChildColumn);
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

}