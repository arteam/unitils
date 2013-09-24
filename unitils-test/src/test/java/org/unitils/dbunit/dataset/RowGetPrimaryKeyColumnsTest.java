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

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class RowGetPrimaryKeyColumnsTest {

    /* Tested object */
    private Row row = new Row();

    private Column column1;
    private Column column2;
    private Column column3;


    @Before
    public void initialize() {
        column1 = new Column("column1", null, null);
        column2 = new Column("column2", null, null);
        column3 = new Column("column3", null, null);
    }

    @Test
    public void getPrimaryKeyColumns() {
        row.addPrimaryKeyColumn(column1);
        row.addPrimaryKeyColumn(column2);
        row.addColumn(column3);

        List<Column> result = row.getPrimaryKeyColumns();
        assertEquals(asList(column1, column2), result);
    }

    @Test
    public void emptyWhenNoPrimaryKeyColumns() {
        List<Column> result = row.getPrimaryKeyColumns();
        assertTrue(result.isEmpty());
    }
}