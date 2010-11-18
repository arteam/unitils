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
package org.unitils.dataset.loadstrategy.impl;

import org.unitils.core.UnitilsException;
import org.unitils.dataset.database.DataSourceWrapper;
import org.unitils.dataset.database.DatabaseAccessor;
import org.unitils.dataset.model.database.TableName;
import org.unitils.dataset.model.database.Value;
import org.unitils.dataset.model.dataset.DataSetRow;
import org.unitils.dataset.model.dataset.DataSetSettings;
import org.unitils.dataset.rowsource.DataSetRowSource;

import java.util.*;


/**
 * Deletes all content from the tables in the data set (in reverse order).
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class TableContentDeleter {

    protected DatabaseAccessor databaseAccessor;
    protected DataSourceWrapper dataSourceWrapper;


    public TableContentDeleter(DatabaseAccessor databaseAccessor, DataSourceWrapper dataSourceWrapper) {
        this.databaseAccessor = databaseAccessor;
        this.dataSourceWrapper = dataSourceWrapper;
    }


    public void deleteDataFromTablesInReverseOrder(DataSetRowSource dataSetRowSource) {
        List<TableName> tableNames = getTableNamesInReverseOrder(dataSetRowSource);
        for (TableName tableName : tableNames) {
            try {
                deleteTableContent(tableName);
            } catch (Exception e) {
                throw new UnitilsException("Unable to delete data from table " + tableName, e);
            }
        }
    }


    protected List<TableName> getTableNamesInReverseOrder(DataSetRowSource dataSetRowSource) {
        Map<TableName, TableName> tableNamesMap = new LinkedHashMap<TableName, TableName>();

        DataSetRow dataSetRow;
        while ((dataSetRow = dataSetRowSource.getNextDataSetRow()) != null) {
            DataSetSettings dataSetSettings = dataSetRow.getDataSetSettings();
            TableName tableName = dataSourceWrapper.getTableName(dataSetRow.getSchemaName(), dataSetRow.getTableName(), dataSetSettings.isCaseSensitive());

            if (!tableNamesMap.containsKey(tableName)) {
                tableNamesMap.put(tableName, tableName);
            }
        }
        List<TableName> tableNames = new ArrayList<TableName>(tableNamesMap.values());
        Collections.reverse(tableNames);
        return tableNames;
    }


    protected void deleteTableContent(TableName tableName) throws Exception {
        String sql = createStatement(tableName);
        databaseAccessor.executeUpdate(sql, new ArrayList<Value>());
    }

    protected String createStatement(TableName tableName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("delete from ");
        stringBuilder.append(tableName.getQualifiedTableName());
        return stringBuilder.toString();
    }

}