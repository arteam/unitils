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

import org.dbunit.dataset.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Tim Ducheyne
 */
public class DbUnitDataSet extends AbstractDataSet {

    protected Map<String, DbUnitTable> tablesPerName = new LinkedHashMap<String, DbUnitTable>(5);


    public ITable getTable(String tableName) {
        return getDbUnitTable(tableName);
    }

    public DbUnitTable getDbUnitTable(String tableName) {
        return tablesPerName.get(tableName);
    }

    public void addTable(DbUnitTable table) {
        String tableName = table.getTableMetaData().getTableName();
        tablesPerName.put(tableName, table);
    }


    @Override
    protected ITableIterator createIterator(boolean reversed) throws DataSetException {
        ITable[] tables = tablesPerName.values().toArray(new ITable[tablesPerName.size()]);
        return new DefaultTableIterator(tables, reversed);
    }
}
