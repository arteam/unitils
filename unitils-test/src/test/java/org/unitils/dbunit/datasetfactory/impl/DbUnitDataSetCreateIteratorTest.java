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

import org.dbunit.dataset.ITableIterator;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class DbUnitDataSetCreateIteratorTest {

    /* Tested object */
    private DbUnitDataSet dbUnitDataSet;

    private DbUnitTable table1;
    private DbUnitTable table2;


    @Before
    public void initialize() {
        dbUnitDataSet = new DbUnitDataSet();

        table1 = new DbUnitTable("table1");
        table2 = new DbUnitTable("table2");
    }


    @Test
    public void createIterator() throws Exception {
        dbUnitDataSet.addTable(table1);
        dbUnitDataSet.addTable(table2);

        ITableIterator result = dbUnitDataSet.createIterator(false);
        assertTrue(result.next());
        assertSame(table1, result.getTable());
        assertTrue(result.next());
        assertSame(table2, result.getTable());
        assertFalse(result.next());
    }

    @Test
    public void reversedIterator() throws Exception {
        dbUnitDataSet.addTable(table1);
        dbUnitDataSet.addTable(table2);

        ITableIterator result = dbUnitDataSet.createIterator(true);
        assertTrue(result.next());
        assertSame(table2, result.getTable());
        assertTrue(result.next());
        assertSame(table1, result.getTable());
        assertFalse(result.next());
    }

    @Test
    public void emptyIterator() throws Exception {
        ITableIterator result = dbUnitDataSet.createIterator(true);
        assertFalse(result.next());
    }
}
