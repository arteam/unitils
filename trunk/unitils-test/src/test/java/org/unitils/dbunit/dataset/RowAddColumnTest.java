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

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class RowAddColumnTest {

    /* Tested object */
    private Row row = new Row();

    private Column column1;
    private Column column2;


    @Before
    public void initialize() {
        column1 = new Column("column1", null, null);
        column2 = new Column("column2", null, null);
    }


    @Test
    public void addColumn() {
        row.addColumn(column1);
        assertEquals(asList(column1), row.getColumns());
        assertTrue(row.getPrimaryKeyColumns().isEmpty());
    }

    @Test
    public void addMultipleColumns() {
        row.addColumn(column1);
        row.addColumn(column2);
        assertEquals(asList(column1, column2), row.getColumns());
    }

    @Test
    public void exceptionWhenColumnWithSameNameAlreadyExists() {
        row.addColumn(column1);
        try {
            row.addColumn(new Column("column1", null, null));
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to add column to data set row. Column with name 'column1' already exists.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenPrimaryKeyColumnWithSameNameAlreadyExists() {
        row.addPrimaryKeyColumn(column1);
        try {
            row.addColumn(new Column("column1", null, null));
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to add column to data set row. Column with name 'column1' already exists.", e.getMessage());
        }
    }
}