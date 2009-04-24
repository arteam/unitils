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

import static org.dbunit.dataset.datatype.DataType.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.dbunit.dataset.comparison.RowDifference;
import org.unitils.dbunit.dataset.comparison.TableDifference;
import org.unitils.dbunit.dataset.comparison.ValueDifference;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * todo javadoc
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
    public void testAssertEqualTables() throws Exception {
        addExpectedRow("value");
        addActualRow("value");

        TableDifference result = expectedTable.compare(actualTable);

        assertNull(result);
    }


    @Test
    public void testDifferentStringValue() throws Exception {
        addExpectedRow("value");
        addActualRow("xxxx");

        TableDifference result = expectedTable.compare(actualTable);

        assertValueDifference(result, "value", "xxxx");
    }


    @Test
    public void testEqualDateValue() throws Exception {
        Date date = new GregorianCalendar(2009, 5, 10).getTime();
        addExpectedRow("2009-06-10");
        addActualRow(date);

        TableDifference result = expectedTable.compare(actualTable);

        assertNull(result);
    }


    @Test
    public void testDifferentDateValue() throws Exception {
        Date date = new GregorianCalendar(2009, 5, 10).getTime();
        addExpectedRow("1980-01-05");
        addActualRow(date);

        TableDifference result = expectedTable.compare(actualTable);

        assertValueDifference(result, "1980-01-05", date);
    }

    @Test
    public void testEqualDoubleValue() throws Exception {
        addExpectedRow("-1.00");
        addActualRow(-1.0);

        TableDifference result = expectedTable.compare(actualTable);

        assertNull(result);
    }

    @Test
    public void testDifferentDoubleValue() throws Exception {
        addExpectedRow("-1.00");
        addActualRow(-5.0);

        TableDifference result = expectedTable.compare(actualTable);

        assertValueDifference(result, "-1.00", -5.0);
    }

    @Test
    public void testMissingRow() throws Exception {
        addExpectedRow("value1");
        addExpectedRow("value2");
        addActualRow("value1");

        TableDifference result = expectedTable.compare(actualTable);

        assertMissingRow(result);
    }


    private void assertValueDifference(TableDifference tableDifference, String expectedValue, Object actualValue) {
        RowDifference rowDifference = tableDifference.getBestRowDifferences().get(0);
        ValueDifference valueDifference = rowDifference.getValueDifferences().get(0);
        assertEquals(expectedValue, valueDifference.getValue().getValue());
        assertEquals(actualValue, valueDifference.getActualValue().getValue());
    }

    private void assertMissingRow(TableDifference tableDifference) {
        Row row = tableDifference.getMissingRows().get(0);
        assertNotNull(row);
    }


    private void addActualRow(String value) {
        Row dataSetRow = new Row();
        dataSetRow.addValue(new Value("column", VARCHAR, value));
        actualTable.addRow(dataSetRow);
    }


    private void addActualRow(Date date) {
        Row dataSetRow = new Row();
        dataSetRow.addValue(new Value("column", DATE, date));
        actualTable.addRow(dataSetRow);
    }

    private void addActualRow(Double number) {
        Row dataSetRow = new Row();
        dataSetRow.addValue(new Value("column", DOUBLE, number));
        actualTable.addRow(dataSetRow);
    }


    private void addExpectedRow(String value) {
        Row dataSetRow = new Row();
        dataSetRow.addValue(new Value("column", VARCHAR, value));
        expectedTable.addRow(dataSetRow);
    }
}
