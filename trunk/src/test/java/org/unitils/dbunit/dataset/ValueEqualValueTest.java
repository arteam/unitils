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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.unitils.core.UnitilsException;

import java.util.GregorianCalendar;

/**
 * Tests the equalValue behavior of Values
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ValueEqualValueTest {


    @Test
    public void testEqualStringValue() throws Exception {
        Value expectedValue = new Value("column", VARCHAR, "value");
        Value actualValue = new Value("column", VARCHAR, "value");

        boolean result = expectedValue.equalValue(actualValue);

        assertTrue(result);
    }


    @Test
    public void testDifferentStringValue() throws Exception {
        Value expectedValue = new Value("column", VARCHAR, "value");
        Value actualValue = new Value("column", VARCHAR, "xxxx");

        boolean result = expectedValue.equalValue(actualValue);

        assertFalse(result);
    }


    @Test
    public void testEqualDateValue() throws Exception {
        Value expectedValue = new Value("column", VARCHAR, "2009-06-10");
        Value actualValue = new Value("column", DATE, new GregorianCalendar(2009, 5, 10).getTime());

        boolean result = expectedValue.equalValue(actualValue);

        assertTrue(result);
    }


    @Test
    public void testDifferentDateValue() throws Exception {
        Value expectedValue = new Value("column", VARCHAR, "1980-01-05");
        Value actualValue = new Value("column", DATE, new GregorianCalendar(2009, 5, 10).getTime());

        boolean result = expectedValue.equalValue(actualValue);

        assertFalse(result);
    }


    @Test
    public void testEqualDoubleValue() throws Exception {
        Value expectedValue = new Value("column", VARCHAR, "-1.00");
        Value actualValue = new Value("column", DOUBLE, -1.0);

        boolean result = expectedValue.equalValue(actualValue);

        assertTrue(result);
    }


    @Test
    public void testDifferentDoubleValue() throws Exception {
        Value expectedValue = new Value("column", VARCHAR, "-1.00");
        Value actualValue = new Value("column", DOUBLE, -5);

        boolean result = expectedValue.equalValue(actualValue);

        assertFalse(result);
    }


    @Test(expected = UnitilsException.class)
    public void testInvalidType() throws Exception {
        Value expectedValue = new Value("column", VARCHAR, "xxxxxxx");
        Value actualValue = new Value("column", DOUBLE, -5);

        expectedValue.equalValue(actualValue);
    }


    @Test
    public void testNullValue() throws Exception {
        Value expectedValue = new Value("column", VARCHAR, "value");

        boolean result = expectedValue.equalValue(null);

        assertFalse(result);
    }


    @Test
    public void testDifferentColumn() throws Exception {
        Value expectedValue = new Value("column", VARCHAR, "value");
        Value actualValue = new Value("otherColumn", VARCHAR, "value");

        boolean result = expectedValue.equalValue(actualValue);

        assertFalse(result);
    }
}