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
package org.unitils.dataset.core;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

/**
 * todo javadoc
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ColumnGetValueTest {

    private List<String> emptyVariables = new ArrayList<String>();


    @Test
    public void regularValue() {
        Column column = new Column("column", "value", false, '=', '$');

        Value result = column.getValue(emptyVariables);
        assertEquals("value", result.getValue());
        assertFalse(result.isLiteralValue());
    }

    @Test
    public void literalValue() {
        Column column = new Column("column", "=value", false, '=', '$');

        Value result = column.getValue(emptyVariables);
        assertEquals("value", result.getValue());
        assertTrue(result.isLiteralValue());
    }

    @Test
    public void escapedLiteralValue() {
        Column column = new Column("column", "==value", false, '=', '$');

        Value result = column.getValue(emptyVariables);
        assertEquals("=value", result.getValue());
        assertFalse(result.isLiteralValue());
    }

    @Test
    public void variables() {
        Column column = new Column("column", "$0value $0 $1", false, '=', '$');

        Value result = column.getValue(asList("1", "2"));
        assertEquals("1value 1 2", result.getValue());
        assertFalse(result.isLiteralValue());
    }

    @Test
    public void literalValueThroughVariable() {
        Column column = new Column("column", "$0", false, '=', '$');

        Value result = column.getValue(asList("=value"));
        assertEquals("value", result.getValue());
        assertTrue(result.isLiteralValue());
    }

    @Test
    public void escapedLiteralValueThroughVariable() {
        Column column = new Column("column", "$0", false, '=', '$');

        Value result = column.getValue(asList("==value"));
        assertEquals("=value", result.getValue());
        assertFalse(result.isLiteralValue());
    }

}
