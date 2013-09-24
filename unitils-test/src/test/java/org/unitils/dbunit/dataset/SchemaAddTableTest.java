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

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Tim Ducheyne
 */
public class SchemaAddTableTest {

    /* Tested object */
    private Schema schema;

    private Table table1;
    private Table table2;


    @Before
    public void initialize() {
        schema = new Schema("name");

        table1 = new Table("table1");
        table2 = new Table("table2");
    }


    @Test
    public void addTable() {
        schema.addTable(table1);
        schema.addTable(table2);

        List<Table> result = schema.getTables();
        assertEquals(asList(table1, table2), result);
    }

    @Test
    public void exceptionWhenTableWithSameNameAlreadyExists() {
        schema.addTable(table1);
        try {
            schema.addTable(new Table("TABLE1"));
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to add table to data set. A table with name TABLE1 already exists.", e.getMessage());

        }
    }
}