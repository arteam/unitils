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

import static org.dbunit.dataset.datatype.DataType.VARCHAR;
import static org.junit.Assert.*;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import org.unitils.dbunit.dataset.comparison.ColumnDifference;
import org.unitils.dbunit.dataset.comparison.RowDifference;

/**
 * Tests the comparison behavior of a data set row
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class RowComparisonTest extends UnitilsJUnit4 {

    private Row expectedRow = new Row();
    private Row actualRow = new Row();


    @Test
    public void testEqualRows() throws Exception {
        addColumn(expectedRow, "column1", "value1");
        addColumn(expectedRow, "column2", "value2");
        addColumn(actualRow, "column1", "value1");
        addColumn(actualRow, "column2", "value2");

        RowDifference result = expectedRow.compare(actualRow);

        assertNull(result);
    }


    @Test
    public void testDifferentValues() throws Exception {
        addColumn(expectedRow, "column1", "value1");
        addColumn(expectedRow, "column2", "value2");
        addColumn(actualRow, "column1", "xxxx");
        addColumn(actualRow, "column2", "yyyy");

        RowDifference result = expectedRow.compare(actualRow);

        ColumnDifference columnDifference1 = result.getColumnDifference("column1");
        ColumnDifference columnDifference2 = result.getColumnDifference("column2");
        assertColumnDifference(columnDifference1, "value1", "xxxx");
        assertColumnDifference(columnDifference2, "value2", "yyyy");
    }


    @Test
    public void testEqualsWithMoreColumnsInActualRow() throws Exception {
        addColumn(expectedRow, "column1", "value1");
        addColumn(actualRow, "column1", "value1");
        addColumn(actualRow, "column2", "yyyy");

        RowDifference result = expectedRow.compare(actualRow);

        assertNull(result);
    }


    @Test
    public void testMissingColumnInActualRow() throws Exception {
        addColumn(expectedRow, "column1", "value1");
        addColumn(expectedRow, "column2", "value2");
        addColumn(actualRow, "column1", "value1");

        RowDifference result = expectedRow.compare(actualRow);

        assertMissingColumn(result);
    }


    @Test(expected = UnitilsException.class)
    public void testAddingTwoColumnsForSameName() throws Exception {
        addColumn(expectedRow, "column", "value");
        addColumn(expectedRow, "column", "value");
    }


    private void assertColumnDifference(ColumnDifference columnDifference, String expectedValue, String actualValue) {
        assertEquals(expectedValue, columnDifference.getColumn().getValue());
        assertEquals(actualValue, columnDifference.getActualColumn().getValue());
    }


    private void assertMissingColumn(RowDifference rowDifference) {
        Column column = rowDifference.getMissingColumns().get(0);
        assertNotNull(column);
    }


    private void addColumn(Row row, String columnName, String value) {
        row.addColumn(new Column(columnName, VARCHAR, value));
    }

}