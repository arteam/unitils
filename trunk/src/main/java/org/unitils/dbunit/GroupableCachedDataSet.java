/*
 * Copyright 2006 the original author or authors.
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
package org.unitils.dbunit;

import org.dbunit.dataset.*;
import org.dbunit.dataset.stream.IDataSetConsumer;
import org.dbunit.dataset.stream.IDataSetProducer;

import java.util.ArrayList;
import java.util.List;

/**
 * todo
 */
public class GroupableCachedDataSet extends AbstractDataSet implements IDataSetConsumer {

    private ITable[] _tables;

    private List _tableList;
    private DefaultTable _activeTable;

    /**
     * DEFAULT constructor.
     */
    public GroupableCachedDataSet() {
    }

    /**
     * Creates a copy of the specified dataset.
     */
    public GroupableCachedDataSet(IDataSet dataSet) throws DataSetException {
        List tableList = new ArrayList();
        ITableIterator iterator = dataSet.iterator();
        while (iterator.next()) {
            tableList.add(new CachedTable(iterator.getTable()));
        }
        _tables = (ITable[]) tableList.toArray(new ITable[0]);
    }

    /**
     * Creates a GroupableCachedDataSet that syncronously consume the specified producer.
     */
    public GroupableCachedDataSet(IDataSetProducer producer) throws DataSetException {
        producer.setConsumer(this);
        producer.produce();
    }

    ////////////////////////////////////////////////////////////////////////////
    // AbstractDataSet class

    protected ITableIterator createIterator(boolean reversed)
            throws DataSetException {
        return new DefaultTableIterator(_tables, reversed);
    }

    ////////////////////////////////////////////////////////////////////////
    // IDataSetConsumer interface

    public void startDataSet() throws DataSetException {
        _tableList = new ArrayList();
        _tables = null;
    }

    public void endDataSet() throws DataSetException {
        _tables = (ITable[]) _tableList.toArray(new ITable[0]);
        _tableList = null;
    }

    public void startTable(ITableMetaData metaData) throws DataSetException {
        _activeTable = findTable(metaData);
        if (_activeTable == null) {
            _activeTable = new DefaultTable(metaData);
            _tableList.add(_activeTable);
        }
    }

    private DefaultTable findTable(ITableMetaData metaData) {
        String tableName = metaData.getTableName();
        for (int i = 0; i < _tableList.size(); i++) {
            DefaultTable table = (DefaultTable) _tableList.get(i);
            if (table.getTableMetaData().getTableName().equals(tableName)) {
                return table;
            }
        }
        return null;
    }

    public void endTable() throws DataSetException {
        _activeTable = null;
    }

    public void row(Object[] values) throws DataSetException {
        _activeTable.addRow(values);
    }


}
