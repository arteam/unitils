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
package org.unitils.dbunit.dataset;

import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;

import static org.dbunit.dataset.ITable.NO_VALUE;
import static org.dbunit.dataset.datatype.DataType.BIGINT;
import static org.dbunit.dataset.datatype.DataType.VARCHAR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Tim Ducheyne
 */
public class ColumnGetCastedValueTest {

    /* Tested object */
    private Column column;


    @Before
    public void initialize() {
        column = new Column("name", VARCHAR, "value");
    }


    @Test
    public void getCastedValue() {
        Object result = column.getCastedValue(VARCHAR);
        assertEquals("value", result);
    }

    @Test
    public void nullWhenValueIsNull() {
        column = new Column("name", BIGINT, null);

        Object result = column.getCastedValue(VARCHAR);
        assertNull(result);
    }

    @Test
    public void nullWhenValueIsNoValue() {
        column = new Column("name", BIGINT, NO_VALUE);

        Object result = column.getCastedValue(VARCHAR);
        assertNull(result);
    }

    @Test
    public void exceptionWhenCastFails() {
        try {
            column.getCastedValue(BIGINT);
        } catch (UnitilsException e) {
            assertEquals("Unable to convert \"value\" to BIGINT. Column name: name, current type: VARCHAR\n" +
                    "Reason: TypeCastException: Unable to typecast value <value> of type <java.lang.String> to BIGINT", e.getMessage());
        }
    }
}