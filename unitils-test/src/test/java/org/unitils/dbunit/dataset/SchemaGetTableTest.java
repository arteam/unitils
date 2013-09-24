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
public class SchemaGetTableTest {

    /* Tested object */
    private Schema schema;

    private Table table1;
    private Table table2;


    @Before
    public void initialize() {
        schema = new Schema("name");

        table1 = new Table("table1");
        table2 = new Table("table2");
        schema.addTable(table1);
        schema.addTable(table2);
    }


    @Test
    public void getTable() {
        Table result = schema.getTable("table1");
        assertSame(table1, result);
    }

    @Test
    public void nullWhenNotFound() {
        Table result = schema.getTable("xxx");
        assertNull(result);
    }

    @Test
    public void casingIsIgnored() {
        Table result = schema.getTable("TABLE1");
        assertSame(table1, result);
    }
}