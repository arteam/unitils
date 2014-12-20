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

import org.dbunit.dataset.Column;
import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.dbunit.dataset.ITable.NO_VALUE;
import static org.dbunit.dataset.datatype.DataType.VARCHAR;
import static org.junit.Assert.assertEquals;

/**
 * @author Tim Ducheyne
 */
public class DbUnitTableAddRowTest {

    /* Tested object */
    private DbUnitTable dbUnitTable;


    @Before
    public void initialize() {
        dbUnitTable = new DbUnitTable("table");

        dbUnitTable.addColumn(new Column("column1", VARCHAR));
        dbUnitTable.addColumn(new Column("column2", VARCHAR));
    }


    @Test
    public void addRow() throws Exception {
        dbUnitTable.addRow(asList("111", "222"));
        dbUnitTable.addRow(asList("333", "444"));

        assertEquals(2, dbUnitTable.getRowCount());
        assertEquals("111", dbUnitTable.getValue(0, "column1"));
        assertEquals("222", dbUnitTable.getValue(0, "column2"));
        assertEquals("333", dbUnitTable.getValue(1, "column1"));
        assertEquals("444", dbUnitTable.getValue(1, "column2"));
    }

    @Test
    public void noValueWhenNoValueWasSpecified() throws Exception {
        dbUnitTable.addRow(asList("111"));

        assertEquals("111", dbUnitTable.getValue(0, "column1"));
        assertEquals(NO_VALUE, dbUnitTable.getValue(0, "column2"));
    }

    @Test
    public void nullValue() throws Exception {
        dbUnitTable.addRow(asList(null, "222"));

        assertEquals(null, dbUnitTable.getValue(0, "column1"));
        assertEquals("222", dbUnitTable.getValue(0, "column2"));
    }

    @Test
    public void emptyRow() throws Exception {
        dbUnitTable.addRow(emptyList());

        assertEquals(1, dbUnitTable.getRowCount());
        assertEquals(NO_VALUE, dbUnitTable.getValue(0, "column1"));
        assertEquals(NO_VALUE, dbUnitTable.getValue(0, "column2"));
    }
}
