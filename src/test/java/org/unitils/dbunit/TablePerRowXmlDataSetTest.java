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

import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableIterator;
import org.unitils.UnitilsJUnit3;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertPropertyLenEquals;

import java.io.ByteArrayInputStream;
import java.util.Arrays;

/**
 * Test for {@link TablePerRowXmlDataSet}.
 */
public class TablePerRowXmlDataSetTest extends UnitilsJUnit3 {

    /* Test data set xml with the second elment for table A containing less columns */
    private static final String XML_LESS_COLUMNS_LAST = "<dataset><TABLE_A COLUMN_1='1' COLUMN_2='2' COLUMN_3='3'/><TABLE_A COLUMN_2='4' /></dataset>";

    /* Test data set xml with the first elment for table A containing less columns */
    private static final String XML_LESS_COLUMNS_FIRST = "<dataset><TABLE_A COLUMN_2='4' /><TABLE_A COLUMN_1='1' COLUMN_2='2' COLUMN_3='3'/></dataset>";


    /**
     * Test the loading of a data set with 2 rows for a table, but the second
     * row has less columns than the first one.
     */
    public void testLoadDataSet_lessColumnsLast() throws Exception {

        TablePerRowXmlDataSet result = new TablePerRowXmlDataSet(new ByteArrayInputStream(XML_LESS_COLUMNS_LAST.getBytes()));

        assertLenEquals(new String[]{"TABLE_A", "TABLE_A"}, result.getTableNames());
        ITableIterator tableIterator = result.iterator();

        // first table A row
        assertTrue(tableIterator.next());
        ITable table = tableIterator.getTable();
        assertEquals(1, table.getRowCount());
        assertPropertyLenEquals("columnName", Arrays.asList("COLUMN_1", "COLUMN_2", "COLUMN_3"), Arrays.asList(table.getTableMetaData().getColumns()));
        assertEquals("1", table.getValue(0, "COLUMN_1"));
        assertEquals("2", table.getValue(0, "COLUMN_2"));
        assertEquals("3", table.getValue(0, "COLUMN_3"));

        // second table A row
        assertTrue(tableIterator.next());
        table = tableIterator.getTable();
        assertPropertyLenEquals("columnName", Arrays.asList("COLUMN_2"), Arrays.asList(table.getTableMetaData().getColumns()));
        assertEquals(1, table.getRowCount());
        assertEquals("4", table.getValue(0, "COLUMN_2"));
        assertFalse(tableIterator.next());
    }


    /**
     * Test the loading of a data set with 2 rows for a table, but the first
     * row has less columns than the second one.
     */
    public void testLoadDataSet_lessColumnsFirst() throws Exception {

        TablePerRowXmlDataSet result = new TablePerRowXmlDataSet(new ByteArrayInputStream(XML_LESS_COLUMNS_FIRST.getBytes()));

        assertLenEquals(new String[]{"TABLE_A", "TABLE_A"}, result.getTableNames());
        ITableIterator tableIterator = result.iterator();

        // first table A row
        assertTrue(tableIterator.next());
        ITable table = tableIterator.getTable();
        assertPropertyLenEquals("columnName", Arrays.asList("COLUMN_2"), Arrays.asList(table.getTableMetaData().getColumns()));
        assertEquals(1, table.getRowCount());
        assertEquals("4", table.getValue(0, "COLUMN_2"));

        // second table A row
        assertTrue(tableIterator.next());
        table = tableIterator.getTable();
        assertEquals(1, table.getRowCount());
        assertPropertyLenEquals("columnName", Arrays.asList("COLUMN_1", "COLUMN_2", "COLUMN_3"), Arrays.asList(table.getTableMetaData().getColumns()));
        assertEquals("1", table.getValue(0, "COLUMN_1"));
        assertEquals("2", table.getValue(0, "COLUMN_2"));
        assertEquals("3", table.getValue(0, "COLUMN_3"));
        assertFalse(tableIterator.next());
    }


}
