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

import static org.dbunit.dataset.datatype.DataType.VARCHAR;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class RowHasDifferentPrimaryKeyColumnsTest {

    /* Tested object */
    private Row row = new Row();

    private Row actualRow;
    private Column column1;
    private Column column1b;
    private Column column2;


    @Before
    public void initialize() {
        actualRow = new Row();
        column1 = new Column("column1", VARCHAR, "aaa");
        column1b = new Column("column1", VARCHAR, "bbb");
        column2 = new Column("column2", VARCHAR, null);
    }


    @Test
    public void trueWhenColumnDifferentValue() {
        row.addPrimaryKeyColumn(column1);
        actualRow.addPrimaryKeyColumn(column1b);

        boolean result = row.hasDifferentPrimaryKeyColumns(actualRow);
        assertTrue(result);
    }

    @Test
    public void falseWhenNoPrimaryKeyColumns() {
        row.addPrimaryKeyColumn(column2);
        actualRow.addPrimaryKeyColumn(column1);
        actualRow.addPrimaryKeyColumn(column2);

        boolean result = row.hasDifferentPrimaryKeyColumns(actualRow);
        assertFalse(result);
    }

    @Test
    public void falseWhenEqualPrimaryKeyColumns() {
        row.addPrimaryKeyColumn(column1);
        row.addPrimaryKeyColumn(column2);
        actualRow.addPrimaryKeyColumn(column1);
        actualRow.addPrimaryKeyColumn(column2);

        boolean result = row.hasDifferentPrimaryKeyColumns(actualRow);
        assertFalse(result);
    }

    @Test
    public void equalWhenBothHaveNoPrimaryKeys() {
        boolean result = row.hasDifferentPrimaryKeyColumns(actualRow);
        assertFalse(result);
    }
}