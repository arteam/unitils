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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import org.unitils.dbunit.dataset.comparison.RowDifference;
import org.unitils.dbunit.dataset.comparison.ValueDifference;

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
        addValue(expectedRow, "column1", "value1");
        addValue(expectedRow, "column2", "value2");
        addValue(actualRow, "column1", "value1");
        addValue(actualRow, "column2", "value2");

        RowDifference result = expectedRow.compare(actualRow);

        assertNull(result);
    }


    @Test
    public void testDifferentValues() throws Exception {
        addValue(expectedRow, "column1", "value1");
        addValue(expectedRow, "column2", "value2");
        addValue(actualRow, "column1", "xxxx");
        addValue(actualRow, "column2", "yyyy");

        RowDifference result = expectedRow.compare(actualRow);

        ValueDifference valueDifference1 = result.getValueDifference("column1");
        ValueDifference valueDifference2 = result.getValueDifference("column2");
        assertValueDifference(valueDifference1, "value1", "xxxx");
        assertValueDifference(valueDifference2, "value2", "yyyy");
    }


    @Test
    public void testEqualsWithMoreValuesInActualRow() throws Exception {
        addValue(expectedRow, "column1", "value1");
        addValue(actualRow, "column1", "value1");
        addValue(actualRow, "column2", "yyyy");

        RowDifference result = expectedRow.compare(actualRow);

        assertNull(result);
    }


    @Test
    public void testMissingValueInActualRow() throws Exception {
        addValue(expectedRow, "column1", "value1");
        addValue(expectedRow, "column2", "value2");
        addValue(actualRow, "column1", "value1");

        RowDifference result = expectedRow.compare(actualRow);

        ValueDifference valueDifference = result.getValueDifference("column2");
        assertEquals("value2", valueDifference.getValue().getValue());
        assertNull("value2", valueDifference.getActualValue());
    }


    @Test(expected = UnitilsException.class)
    public void testAddingTwoValuesForSameColumn() throws Exception {
        addValue(expectedRow, "column", "value");
        addValue(expectedRow, "column", "value");
    }


    private void assertValueDifference(ValueDifference valueDifference, String expectedValue, String actualValue) {
        assertEquals(expectedValue, valueDifference.getValue().getValue());
        assertEquals(actualValue, valueDifference.getActualValue().getValue());
    }


    private void addValue(Row row, String columnName, String value) {
        row.addValue(new Value(columnName, VARCHAR, value));
    }

}