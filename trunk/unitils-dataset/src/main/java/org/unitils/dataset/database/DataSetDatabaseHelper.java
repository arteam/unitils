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
import org.unitils.core.UnitilsException;
import org.unitils.dataset.model.database.TableName;
import org.unitils.dataset.model.dataset.DataSetRow;
import org.unitils.dataset.model.dataset.DataSetSettings;
import org.unitils.dataset.model.dataset.DataSetValue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.unitils.core.util.DbUtils.close;

/**
 * Handles correct casing of database identifier, e.g. table names.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DataSetDatabaseHelper {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DataSetDatabaseHelper.class);

    private DataSourceWrapper dataSourceWrapper;
    private IdentifierProcessor identifierProcessor;


    public DataSetDatabaseHelper(DataSourceWrapper dataSourceWrapper, IdentifierProcessor identifierProcessor) {
        this.dataSourceWrapper = dataSourceWrapper;
        this.identifierProcessor = identifierProcessor;
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

        Connection connection = dataSourceWrapper.getConnection();
        ResultSet resultSet = null;
        try {
            TableName parentTableName = dataSourceWrapper.getTableName(parentRow.getSchemaName(), parentRow.getTableName(), caseSensitive);
            TableName childTableName = dataSourceWrapper.getTableName(childRow.getSchemaName(), childRow.getTableName(), caseSensitive);

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