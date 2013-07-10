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

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

/**
 * @author Tim Ducheyne
 */
public class TableAddRowsTest {

    /* Tested object */
    private Table table;

    private Row row1;
    private Row row2;


    @Before
    public void initialize() {
        table = new Table("name");

        row1 = new Row();
        row2 = new Row();
    }


    @Test
    public void addRows() {
        table.addRow(row1);
        table.addRow(row2);
        List<Row> result = table.getRows();
        assertEquals(asList(row1, row2), result);
    }
}