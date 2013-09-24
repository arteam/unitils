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

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 */
public class RowGetColumnTest {

    /* Tested object */
    private Row row = new Row();

    private Column column1;
    private Column column2;
    private Column column3;
    private Column column4;


    @Before
    public void initialize() {
        column1 = new Column("column1", null, null);
        column2 = new Column("column2", null, null);
        column3 = new Column("column3", null, null);
        column4 = new Column("column4", null, null);
        row.addPrimaryKeyColumn(column1);
        row.addPrimaryKeyColumn(column2);
        row.addColumn(column3);
        row.addColumn(column4);
    }


    @Test
    public void primaryKeyColumn() {
        Column result = row.getColumn("column2");
        assertSame(column2, result);
    }

    @Test
    public void regularColumn() {
        Column result = row.getColumn("column4");
        assertSame(column4, result);
    }

    @Test
    public void nullWhenNotFound() {
        Column result = row.getColumn("xxx");
        assertNull(result);
    }

    @Test
    public void casingIsIgnored() {
        Column result = row.getColumn("COLUMN1");
        assertSame(column1, result);
    }
}