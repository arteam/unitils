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

import org.dbunit.dataset.datatype.DataType;
import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;
import org.unitils.dbunit.dataset.comparison.SchemaDifference;
import org.unitils.dbunit.dataset.comparison.TableDifference;
import org.unitilsnew.UnitilsJUnit4;

import static org.dbunit.dataset.datatype.DataType.VARCHAR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests the comparison behavior of a data set schema.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class SchemaComparisonTest extends UnitilsJUnit4 {

    private Schema expectedSchema;
    private Schema actualSchema;


    @Before
    public void initialize() {
        expectedSchema = new Schema("test_schema");
        actualSchema = new Schema("test_schema");
    }


    @Test
    public void testEqualTables() throws Exception {
        addTable(expectedSchema, "table1", "value");
        addTable(actualSchema, "table1", "value");

        SchemaDifference result = expectedSchema.compare(actualSchema);

        assertNull(result);
    }


    @Test
    public void testEqualTablesDifferentCase() throws Exception {
        addTable(expectedSchema, "TABLE1", "value");
        addTable(actualSchema, "table1", "value");

        SchemaDifference result = expectedSchema.compare(actualSchema);

        assertNull(result);
    }


    @Test
    public void testDifferentTables() throws Exception {
        addTable(expectedSchema, "table1", "value");
        addTable(actualSchema, "table1", "xxxx");

        SchemaDifference result = expectedSchema.compare(actualSchema);

        assertDifferentTables(result, "table1");
    }


    @Test
    public void testMissingTable() throws Exception {
        addTable(expectedSchema, "table1", "value");
        addTable(expectedSchema, "table2", "value");
        addTable(actualSchema, "table1", "value");

        SchemaDifference result = expectedSchema.compare(actualSchema);

        assertMissingTable(result, "table2");
    }


    @Test
    public void testEmptyExpectedTableAndActualTable() throws Exception {
        addEmptyTable(expectedSchema, "table1");
        addEmptyTable(actualSchema, "table1");

        SchemaDifference result = expectedSchema.compare(actualSchema);

        assertNull(result);
    }


    @Test
    public void testEmptyExpectedTableButActualTableNotEmpty() throws Exception {
        addEmptyTable(expectedSchema, "table1");
        addTable(actualSchema, "table1", "value");

        SchemaDifference result = expectedSchema.compare(actualSchema);

        assertDifferentTables(result, "table1");
    }


    @Test(expected = UnitilsException.class)
    public void testAddingTwoTablesForSameName() throws Exception {
        addTable(expectedSchema, "table", "value");
        addTable(expectedSchema, "table", "value");
    }


    private void assertDifferentTables(SchemaDifference schemaDifference, String tableName) {
        TableDifference tableDifference = schemaDifference.getTableDifferences().get(0);
        assertEquals(tableName, tableDifference.getActualTable().getName());
    }


    private void assertMissingTable(SchemaDifference schemaDifference, String tableName) {
        Table table = schemaDifference.getMissingTables().get(0);
        assertEquals(tableName, table.getName());
    }


    private void addRowWithPrimaryKey(Table table, String pkValue, String... values) {
        Row row = new Row();
        row.addPrimaryKeyColumn(new Column("column0", VARCHAR, pkValue));
        for (int i = 0; i < values.length; i++) {
            row.addColumn(new Column("column" + (i + 1), VARCHAR, values[i]));
        }
        table.addRow(row);
    }


    private void addTable(Schema schema, String tableName, String value) {
        Row row = new Row();
        row.addColumn(new Column("column", DataType.VARCHAR, value));

        Table table = new Table(tableName);
        table.addRow(row);
        schema.addTable(table);
    }


    private void addEmptyTable(Schema schema, String tableName) {
        Table table = new Table(tableName);
        schema.addTable(table);
    }

}