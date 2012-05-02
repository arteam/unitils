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

import org.junit.Before;
import org.junit.Test;
import org.unitils.dbunit.dataset.comparison.ColumnDifference;
import org.unitils.dbunit.dataset.comparison.RowDifference;
import org.unitils.dbunit.dataset.comparison.TableDifference;
import org.unitilsnew.UnitilsJUnit4;

import static org.dbunit.dataset.datatype.DataType.VARCHAR;
import static org.junit.Assert.*;

/**
 * Tests the comparison behavior of a data set table.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class TableComparisonTest extends UnitilsJUnit4 {

    private Table expectedTable;
    private Table actualTable;


    @Before
    public void initialize() {
        expectedTable = new Table("test_table");
        actualTable = new Table("test_table");
    }


    @Test
    public void testEqualTables() throws Exception {
        addRow(expectedTable, "value1", "value2");
        addRow(actualTable, "value1", "value2");

        TableDifference result = expectedTable.compare(actualTable);

        assertNull(result);
    }


    @Test
    public void testEqualTablesWithPrimaryKeys() throws Exception {
        addRow(expectedTable, "pk1", "value1");
        addRowWithPrimaryKey(actualTable, "pk1", "value1");

        TableDifference result = expectedTable.compare(actualTable);

        assertNull(result);
    }


    @Test
    public void testMissingRowForPrimaryKey() throws Exception {
        addRow(expectedTable, "pk1", "value");
        addRowWithPrimaryKey(actualTable, "xxxx", "value");

        TableDifference result = expectedTable.compare(actualTable);

        assertMissingRow(result, "pk1");
    }


    @Test
    public void testMissingRowWithoutPrimaryKey() throws Exception {
        addRow(expectedTable, "value1");
        addRow(expectedTable, "value2");
        addRow(actualTable, "value1");

        TableDifference result = expectedTable.compare(actualTable);

        assertMissingRow(result, "value2");
    }


    @Test
    public void testDifferentValueUsingPrimaryKey() throws Exception {
        addRow(expectedTable, "pk1", "value1");
        addRow(expectedTable, "pk2", "value2");
        addRowWithPrimaryKey(actualTable, "pk1", "value2");

        TableDifference result = expectedTable.compare(actualTable);

        assertDifferentRows(result, "value1", "value2");
    }


    @Test
    public void testDifferentValueWithoutPrimaryKey() throws Exception {
        addRow(expectedTable, "value1", "value2");
        addRow(actualTable, "value1", "xxxx");

        TableDifference result = expectedTable.compare(actualTable);

        assertDifferentRows(result, "value2", "xxxx");
    }


    @Test
    public void testBestMatchingDifferences() throws Exception {
        addRow(expectedTable, "xxxx", "value2a", "value3");
        addRow(expectedTable, "yyyy", "value2b", "value3");
        addRow(actualTable, "value1", "value2b", "value3");
        addRow(actualTable, "value1", "value2a", "value3");

        TableDifference result = expectedTable.compare(actualTable);

        RowDifference rowDifference1 = getRowDifference(result, "xxxx", "value1");
        assertEquals("value2a", rowDifference1.getActualRow().getColumn("column1").getValue());
        RowDifference rowDifference2 = getRowDifference(result, "yyyy", "value1");
        assertEquals("value2b", rowDifference2.getActualRow().getColumn("column1").getValue());
    }


    @Test
    public void testBestMatchingDifferencesWithMatchingRow() throws Exception {
        addRow(expectedTable, "xxxx", "yyyy", "value3");
        addRow(expectedTable, "value1", "value2", "value3");
        addRow(actualTable, "value1", "value2", "value3");
        addRow(actualTable, "value1", "value2", "value3");

        TableDifference result = expectedTable.compare(actualTable);

        assertDifferentRows(result, "xxxx", "value1");
    }


    @Test
    public void testMissingTableDoubleMatch() throws Exception {
        addRow(expectedTable, "value1");
        addRow(expectedTable, "value1");
        addRow(actualTable, "value1");

        TableDifference result = expectedTable.compare(actualTable);

        assertMissingRow(result, "value1");
        assertEquals(1, result.getMissingRows().size());
    }


    private void assertDifferentRows(TableDifference tableDifference, String expectedValue, Object actualValue) {
        RowDifference rowDifference = getRowDifference(tableDifference, expectedValue, actualValue);
        assertNotNull("Row difference not found for expected value: " + expectedValue + " and actual value: " + actualValue, rowDifference);
    }


    private RowDifference getRowDifference(TableDifference tableDifference, String expectedValue, Object actualValue) {
        for (RowDifference rowDifference : tableDifference.getBestRowDifferences()) {
            ColumnDifference valueDifference = rowDifference.getColumnDifferences().get(0);
            if (expectedValue.equals(valueDifference.getColumn().getValue()) && actualValue.equals(valueDifference.getActualColumn().getValue())) {
                return rowDifference;
            }
        }
        return null;
    }


    private void assertMissingRow(TableDifference tableDifference, String value) {
        Row row = tableDifference.getMissingRows().get(0);
        assertEquals(value, row.getColumn("column0").getValue());
    }


    private void addRowWithPrimaryKey(Table table, String pkValue, String... values) {
        Row row = new Row();
        row.addPrimaryKeyColumn(new Column("column0", VARCHAR, pkValue));
        for (int i = 0; i < values.length; i++) {
            row.addColumn(new Column("column" + (i + 1), VARCHAR, values[i]));
        }
        table.addRow(row);
    }


    private void addRow(Table table, String... values) {
        Row row = new Row();
        for (int i = 0; i < values.length; i++) {
            row.addColumn(new Column("column" + i, VARCHAR, values[i]));
        }
        table.addRow(row);
    }
}
