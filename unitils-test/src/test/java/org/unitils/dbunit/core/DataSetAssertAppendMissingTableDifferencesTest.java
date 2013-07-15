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
package org.unitils.dbunit.core;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.dbunit.dataset.Schema;
import org.unitils.dbunit.dataset.Table;
import org.unitils.dbunit.dataset.comparison.SchemaDifference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class DataSetAssertAppendMissingTableDifferencesTest extends UnitilsJUnit4 {

    /* Tested object */
    private DataSetAssert dataSetAssert;

    private SchemaDifference schemaDifference;
    private StringBuilder stringBuilder;
    private Table table1;
    private Table table2;


    @Before
    public void initialize() {
        dataSetAssert = new DataSetAssert(null);

        table1 = new Table("table1");
        table2 = new Table("table2");
        stringBuilder = new StringBuilder();

        schemaDifference = new SchemaDifference(new Schema("schema"), new Schema("schema"));
    }


    @Test
    public void missingTables() {
        schemaDifference.addMissingTable(table1);
        schemaDifference.addMissingTable(table2);

        dataSetAssert.appendMissingTableDifferences(schemaDifference, stringBuilder);
        assertEquals("Found missing table schema.table1\n" +
                "Found missing table schema.table2\n", stringBuilder.toString());
    }

    @Test
    public void emptyWhenNoMissingTables() {
        dataSetAssert.appendMissingTableDifferences(schemaDifference, stringBuilder);
        assertTrue(stringBuilder.toString().isEmpty());
    }
}
