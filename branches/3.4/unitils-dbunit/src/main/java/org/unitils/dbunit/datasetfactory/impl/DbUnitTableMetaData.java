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

import org.dbunit.dataset.AbstractTableMetaData;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Tim Ducheyne
 */
public class DbUnitTableMetaData extends AbstractTableMetaData {


    protected String tableName;
    protected Set<Column> columns = new LinkedHashSet<Column>(10);
    protected List<String> columnNames = new ArrayList<String>(10);


    public DbUnitTableMetaData(String tableName) {
        this.tableName = tableName;
    }


    public String getTableName() {
        return tableName;
    }

    public Column[] getColumns() throws DataSetException {
        return columns.toArray(new Column[columns.size()]);
    }

    public void addColumn(Column column) {
        boolean added = columns.add(column);
        if (added) {
            columnNames.add(column.getColumnName());
        }
    }

    public Column[] getPrimaryKeys() throws DataSetException {
        return new Column[0];
    }

    public List<String> getColumnNames() {
        return columnNames;
    }
}
