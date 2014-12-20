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

import org.junit.Test;

import static org.dbunit.dataset.ITable.NO_VALUE;
import static org.dbunit.dataset.datatype.DataType.VARCHAR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Tim Ducheyne
 */
public class ColumnGetValueTest {


    @Test
    public void getValue() {
        Column column = new Column("name", VARCHAR, "value");

        Object result = column.getValue();
        assertEquals("value", result);
    }

    @Test
    public void nullValue() {
        Column column = new Column("name", VARCHAR, null);

        Object result = column.getValue();
        assertNull(result);
    }

    @Test
    public void nullWhenNoValue() {
        Column column = new Column("name", VARCHAR, NO_VALUE);

        Object result = column.getValue();
        assertNull(result);
    }
}