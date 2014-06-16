/*
 * Copyright 2013,  Unitils.org
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
package org.unitils.dbunit.datasetfactory.impl;

import org.dbunit.dataset.AbstractTable;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITableMetaData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tim Ducheyne
 */
public class DbUnitTable extends AbstractTable {

    protected DbUnitTableMetaData tableMetaData;
    protected List<List<?>> rows = new ArrayList<List<?>>(5);


    public DbUnitTable(String tableName) {
        this.tableMetaData = new DbUnitTableMetaData(tableName);
    }


    public ITableMetaData getTableMetaData() {
        return tableMetaData;
    }


    public void addColumn(Column column) {
        tableMetaData.addColumn(column);
    }

    public List<String> getColumnNames() {
        return tableMetaData.getColumnNames();
    }

    public void addRow(List<?> row) {
        rows.add(row);
    }

    public int getRowCount() {
        return rows.size();
    }

    public Object getValue(int rowIndex, String columnName) throws DataSetException {
        assertValidRowIndex(rowIndex);
        int columnIndex = tableMetaData.getColumnIndex(columnName);
        List<?> row = rows.get(rowIndex);
        if (columnIndex >= row.size()) {
            return NO_VALUE;
        }
        return row.get(columnIndex);
    }
}
