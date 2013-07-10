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
import org.dbunit.dataset.NoSuchColumnException;
import org.dbunit.dataset.RowOutOfBoundsException;
import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.dbunit.dataset.datatype.DataType.VARCHAR;
import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class DbUnitTableGetValueTest {

    /* Tested object */
    private DbUnitTable dbUnitTable;


    @Before
    public void initialize() {
        dbUnitTable = new DbUnitTable("table");

        dbUnitTable.addColumn(new Column("column1", VARCHAR));
        dbUnitTable.addColumn(new Column("column2", VARCHAR));
        dbUnitTable.addRow(asList("111", "222"));
        dbUnitTable.addRow(asList("333"));
    }


    @Test
    public void getValue() throws Exception {
        assertEquals("111", dbUnitTable.getValue(0, "column1"));
        assertEquals("222", dbUnitTable.getValue(0, "column2"));
        assertEquals("333", dbUnitTable.getValue(1, "column1"));
    }

    @Test
    public void nullWhenNoValue() throws Exception {
        Object result = dbUnitTable.getValue(1, "column2");
        assertNull(result);
    }

    @Test
    public void exceptionWhenUnknownColumn() throws Exception {
        try {
            dbUnitTable.getValue(0, "xxx");
            fail("NoSuchColumnException expected");
        } catch (NoSuchColumnException e) {
            assertEquals("table.XXX -  (Non-uppercase input column: xxx) in ColumnNameToIndexes cache map. Note that the map's column names are NOT case sensitive.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenRowIndexTooHigh() throws Exception {
        try {
            dbUnitTable.getValue(2, "column1");
            fail("RowOutOfBoundsException expected");
        } catch (RowOutOfBoundsException e) {
            assertEquals("2 >= 2", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenNegativeRowIndex() throws Exception {
        try {
            dbUnitTable.getValue(-1, "column1");
            fail("RowOutOfBoundsException expected");
        } catch (RowOutOfBoundsException e) {
            assertEquals("-1 < 0", e.getMessage());
        }
    }
}
