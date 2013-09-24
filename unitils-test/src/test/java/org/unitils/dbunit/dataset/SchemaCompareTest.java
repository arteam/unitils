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
import org.unitils.dbunit.dataset.comparison.SchemaDifference;
import org.unitils.dbunit.dataset.comparison.TableDifference;

import java.util.List;

import static java.util.Arrays.asList;
import static org.dbunit.dataset.datatype.DataType.VARCHAR;
import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class SchemaCompareTest {

    /* Tested object */
    private Schema schema;

    private Schema actualSchema;
    private Table table1;
    private Table table1b;
    private Table table2;
    private Table table2b;


    @Before
    public void initialize() {
        schema = new Schema("name");

        actualSchema = new Schema("name");
        table1 = new Table("table1");
        addRow(table1, "aaa");
        table1b = new Table("table1");
        addRow(table1b, "bbb");
        table2 = new Table("table2");
        addRow(table2, "ccc");
        table2b = new Table("table2");
        addRow(table2b, "ddd");
    }

    @Test
    public void nullWhenNoDifference() {
        schema.addTable(table1);
        schema.addTable(table2);
        actualSchema.addTable(table1);
        actualSchema.addTable(table2);

        SchemaDifference result = schema.compare(actualSchema);
        assertNull(result);
    }


    @Test
    public void nullWhenBothHaveNoTables() {
        SchemaDifference result = schema.compare(actualSchema);
        assertNull(result);
    }

    @Test
    public void nullWhenEqualButMoreActualTables() {
        schema.addTable(table1);
        actualSchema.addTable(table1);
        actualSchema.addTable(table2);

        SchemaDifference result = schema.compare(actualSchema);
        assertNull(result);
    }

    @Test
    public void differenceWhenMissingActualTables() {
        schema.addTable(table1);
        schema.addTable(table2);

        SchemaDifference result = schema.compare(actualSchema);
        assertEquals(asList(table1, table2), result.getMissingTables());
    }

    @Test
    public void differenceWhenDifferentTables() {
        schema.addTable(table1);
        schema.addTable(table2);
        actualSchema.addTable(table1b);
        actualSchema.addTable(table2b);

        SchemaDifference result = schema.compare(actualSchema);
        List<TableDifference> tableDifferences = result.getTableDifferences();
        assertEquals(2, tableDifferences.size());
        assertSame(table1, tableDifferences.get(0).getTable());
        assertSame(table1b, tableDifferences.get(0).getActualTable());
        assertSame(table2, tableDifferences.get(1).getTable());
        assertSame(table2b, tableDifferences.get(1).getActualTable());
    }


    private void addRow(Table table, String value) {
        Row row = new Row();
        row.addColumn(new Column("column1", VARCHAR, value));
        table.addRow(row);
    }
}