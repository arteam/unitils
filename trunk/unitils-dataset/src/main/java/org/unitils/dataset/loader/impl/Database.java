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
import org.unitils.dataset.core.Column;
import org.unitils.dataset.core.Row;
import org.unitils.dataset.core.Table;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static org.unitils.core.util.DbUtils.close;


/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class Database {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(Database.class);

    private DbSupport dbSupport;


    public Database(DbSupport dbSupport) {
        this.dbSupport = dbSupport;
    }


    public String getSchemaName() {
        return dbSupport.getSchemaName();
    }

    public String getIdentifierQuoteString() {
        return dbSupport.getIdentifierQuoteString();
    }

    public Connection createConnection() throws SQLException {
        // todo move to db utils and register with spring
        return dbSupport.getSQLHandler().getDataSource().getConnection();
    }


    public Set<String> getPrimaryKeyColumnNames(Table table) throws SQLException {
        Set<String> primaryKeyColumnNames = new LinkedHashSet<String>();

        Connection connection = createConnection();
        ResultSet resultSet = null;
        try {
            String schemaName = dbSupport.toCorrectCaseIdentifier(table.getSchema().getName());
            String tableName = dbSupport.toCorrectCaseIdentifier(table.getName());

            resultSet = connection.getMetaData().getPrimaryKeys(null, schemaName, tableName);
            while (resultSet.next()) {
                primaryKeyColumnNames.add(resultSet.getString("COLUMN_NAME"));
            }
            return primaryKeyColumnNames;
        } finally {
            close(connection, null, resultSet);
        }
    }


    public void addExtraParentColumnsForChild(Row childRow) throws SQLException {
        Row parentRow = childRow.getParentRow();
        if (parentRow == null) {
            return;
        }
        Map<String, String> parentChildColumnNames = getChildForeignKeyColumns(parentRow.getTable(), childRow.getTable());
        for (Map.Entry<String, String> entry : parentChildColumnNames.entrySet()) {
            String parentColumnName = entry.getKey();
            String childColumnName = entry.getValue();

            Column parentColumn = parentRow.getColumn(parentColumnName);
            if (parentColumn == null) {
                continue;
            }

            Column existingChildColumn = childRow.removeColumn(childColumnName);
            if (existingChildColumn != null) {
                logger.warn("Child row contained a value for a parent foreign key column: " + existingChildColumn + ". This value will be ignored and overridden by the actual value of the parent row: " + parentColumn);
            }

            String parentValue = parentColumn.getOriginalValue();
            Column parentChildColumn = new Column(childColumnName, parentValue, true);
            childRow.addColumn(parentChildColumn);
        }
    }

    protected Map<String, String> getChildForeignKeyColumns(Table parentTable, Table childTable) throws SQLException {
        Map<String, String> result = new LinkedHashMap<String, String>();

        Connection connection = createConnection();
        ResultSet resultSet = null;
        try {
            resultSet = connection.getMetaData().getImportedKeys(null, childTable.getSchema().getName(), childTable.getName());
            while (resultSet.next()) {
                String parentForeignKeySchemaName = resultSet.getString("PKTABLE_SCHEM");
                String parentForeignKeyTableName = resultSet.getString("PKTABLE_NAME");
                if (!parentTable.getSchema().hasName(parentForeignKeySchemaName) || !parentTable.hasName(parentForeignKeyTableName)) {
                    continue;
                }
                String parentForeignKeyColumnName = resultSet.getString("PKCOLUMN_NAME");
                String childForeignKeyColumnName = resultSet.getString("FKCOLUMN_NAME");
                result.put(parentForeignKeyColumnName, childForeignKeyColumnName);
            }
            if (result.isEmpty()) {
                throw new UnitilsException("Unable to get foreign key columns for child table: " + childTable + ". No foreign key relationship found with parent table: " + parentTable);
            }
            return result;
        } finally {
            close(connection, null, resultSet);
        }
    }

}