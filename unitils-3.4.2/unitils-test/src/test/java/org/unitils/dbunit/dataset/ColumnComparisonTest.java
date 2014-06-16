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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import org.unitils.core.UnitilsException;
import org.unitils.dbunit.dataset.comparison.ColumnDifference;

import java.util.GregorianCalendar;

/**
 * Tests the comparison behavior of a data set column
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ColumnComparisonTest {


    @Test
    public void equalStringValue() throws Exception {
        Column expectedValue = new Column("column", VARCHAR, "value");
        Column actualValue = new Column("column", VARCHAR, "value");

        ColumnDifference result = expectedValue.compare(actualValue);

        assertNull(result);
    }


    @Test
    public void differentStringValue() throws Exception {
        Column expectedValue = new Column("column", VARCHAR, "value");
        Column actualValue = new Column("column", VARCHAR, "xxxx");

        ColumnDifference result = expectedValue.compare(actualValue);

        assertNotNull(result);
    }


    @Test
    public void equalDateValue() throws Exception {
        Column expectedValue = new Column("column", VARCHAR, "2009-06-10");
        Column actualValue = new Column("column", DATE, new GregorianCalendar(2009, 5, 10).getTime());

        ColumnDifference result = expectedValue.compare(actualValue);

        assertNull(result);
    }


    @Test
    public void differentDateValue() throws Exception {
        Column expectedValue = new Column("column", VARCHAR, "1980-01-05");
        Column actualValue = new Column("column", DATE, new GregorianCalendar(2009, 5, 10).getTime());

        ColumnDifference result = expectedValue.compare(actualValue);

        assertNotNull(result);
    }


    @Test
    public void equalDoubleValue() throws Exception {
        Column expectedValue = new Column("column", VARCHAR, "-1.00");
        Column actualValue = new Column("column", DOUBLE, -1.0);

        ColumnDifference result = expectedValue.compare(actualValue);

        assertNull(result);
    }


    @Test
    public void differentDoubleValue() throws Exception {
        Column expectedValue = new Column("column", VARCHAR, "-1.00");
        Column actualValue = new Column("column", DOUBLE, -5);

        ColumnDifference result = expectedValue.compare(actualValue);

        assertNotNull(result);
    }


    @Test(expected = UnitilsException.class)
    public void invalidType() throws Exception {
        Column expectedValue = new Column("column", VARCHAR, "xxxxxxx");
        Column actualValue = new Column("column", DOUBLE, -5);

        expectedValue.compare(actualValue);
    }


    @Test
    public void nullValues() throws Exception {
        Column expectedValue = new Column("column", VARCHAR, null);
        Column actualValue = new Column("column", VARCHAR, null);

        ColumnDifference result = expectedValue.compare(actualValue);

        assertNull(result);
    }


    @Test
    public void nullExpectedValue() throws Exception {
        Column expectedValue = new Column("column", VARCHAR, null);
        Column actualValue = new Column("column", VARCHAR, "xxxxxxx");

        ColumnDifference result = expectedValue.compare(actualValue);

        assertNull(result);
    }


    @Test
    public void nullActualValue() throws Exception {
        Column expectedValue = new Column("column", VARCHAR, "xxxxxxx");
        Column actualValue = new Column("column", VARCHAR, null);

        ColumnDifference result = expectedValue.compare(actualValue);

        assertNotNull(result);
    }

}