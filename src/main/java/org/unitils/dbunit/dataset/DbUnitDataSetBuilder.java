/*
 * Copyright 2006-2009,  Unitils.org
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
package org.unitils.dbunit.dataset;

import org.dbunit.dataset.*;
import org.dbunit.dataset.datatype.DataType;
import org.unitils.core.UnitilsException;

import java.util.ArrayList;
import java.util.List;

/**
 * todo javadoc
 */
public class DbUnitDataSetBuilder {


    public Schema createDataSetSchema(String schemaName, IDataSet dbUnitDataSet) {
        Schema result = new Schema(schemaName);
        try {
            addTables(dbUnitDataSet, result);
            return result;

        } catch (DataSetException e) {
            throw new UnitilsException("Unable to create data set for db unit data set.", e);
        }
    }


    protected void addTables(IDataSet dbUnitDataSet, Schema dataSet) throws DataSetException {
        ITableIterator dbUnitTableIterator = dbUnitDataSet.iterator();
        while (dbUnitTableIterator.next()) {
            ITable dbUnitTable = dbUnitTableIterator.getTable();
            String tableName = dbUnitTable.getTableMetaData().getTableName();
            List<String> primaryKeyColumnNames = getPrimaryKeyColumnNames(dbUnitTable);

            Table table = dataSet.getTable(tableName);
            if (table == null) {
                table = new Table(tableName);
                dataSet.addTable(table);
            }
            addRows(dbUnitTable, table, primaryKeyColumnNames);
        }
    }

    private List<String> getPrimaryKeyColumnNames(ITable dbUnitTable) throws DataSetException {
        List<String> result = new ArrayList<String>();
        for (Column column : dbUnitTable.getTableMetaData().getPrimaryKeys()) {
            result.add(column.getColumnName());
        }
        return result;
    }

    protected void addRows(ITable dbUnitTable, Table dataSetTable, List<String> primaryKeyColumnNames) throws DataSetException {
        Column[] columns = dbUnitTable.getTableMetaData().getColumns();
        int rowCount = dbUnitTable.getRowCount();
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            Row row = new Row(primaryKeyColumnNames);
            dataSetTable.addRow(row);

            for (Column column : columns) {
                String columnName = column.getColumnName();
                DataType columnType = column.getDataType();
                Object value = dbUnitTable.getValue(rowIndex, columnName);

                row.addValue(new Value(columnName, columnType, value));
            }
        }
    }

}
