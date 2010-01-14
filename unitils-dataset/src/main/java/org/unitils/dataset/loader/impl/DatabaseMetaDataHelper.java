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

import org.unitils.core.UnitilsException;
import org.unitils.dataset.core.Table;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DatabaseMetaDataHelper {

    private Connection connection;

    public DatabaseMetaDataHelper(Connection connection) {
        this.connection = connection;
    }


    public Set<String> getPrimaryKeyColumnNames(String schemaName, String tableName) throws SQLException {
        Set<String> primaryKeyColumnNames = new HashSet<String>();
        ResultSet resultSet = connection.getMetaData().getPrimaryKeys(null, schemaName, tableName);
        while (resultSet.next()) {
            primaryKeyColumnNames.add(resultSet.getString("COLUMN_NAME"));
        }
        return primaryKeyColumnNames;
    }

    public Map<String, String> getChildForeignKeyColumns(Table parentTable, Table childTable) throws SQLException {
        Map<String, String> result = new HashMap<String, String>();

        ResultSet resultSet = connection.getMetaData().getImportedKeys(null, childTable.getSchema().getName(), childTable.getName());
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
    }
}