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
import org.unitils.core.util.ObjectFormatter;
import org.unitils.dbunit.dataset.Column;
import org.unitils.dbunit.dataset.Row;
import org.unitils.dbunit.dataset.Schema;
import org.unitils.dbunit.dataset.Table;
import org.unitils.mock.Mock;
import org.unitilsnew.UnitilsJUnit4;

import static org.dbunit.dataset.datatype.DataType.NUMERIC;
import static org.dbunit.dataset.datatype.DataType.VARCHAR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class DataSetAssertAppendSchemaContentTest extends UnitilsJUnit4 {

    /* Tested object */
    private DataSetAssert dataSetAssert;

    private Mock<ObjectFormatter> objectFormatterMock;

    private StringBuilder stringBuilder;
    private Schema expectedSchema;
    private Schema actualSchema;


    @Before
    public void initialize() {
        dataSetAssert = new DataSetAssert(objectFormatterMock.getMock());

        stringBuilder = new StringBuilder();
        objectFormatterMock.returns("A").format("valueA");
        objectFormatterMock.returns("B").format("valueB");
        objectFormatterMock.returns("C").format("valueC");
        objectFormatterMock.returns("5").format("value5");

        Row row1 = new Row();
        row1.addPrimaryKeyColumn(new Column("pk", NUMERIC, "value5"));
        row1.addColumn(new Column("column1", VARCHAR, "valueA"));
        row1.addColumn(new Column("column2", VARCHAR, "valueB"));
        Row row2 = new Row();
        row2.addColumn(new Column("pk", NUMERIC, null));
        row2.addColumn(new Column("column1", VARCHAR, "valueC"));
        row2.addColumn(new Column("column2", VARCHAR, null));
        Row row3 = new Row();
        row3.addColumn(new Column("column3", VARCHAR, "valueD"));

        Table expectedTable1 = new Table("table1");
        Table expectedTable3 = new Table("table3");
        Table actualTable1 = new Table("table1");
        actualTable1.addRow(row1);
        actualTable1.addRow(row2);
        Table actualTable2 = new Table("table2");
        actualTable2.addRow(row3);
        Table actualTable3 = new Table("table3");

        expectedSchema = new Schema("schema");
        expectedSchema.addTable(expectedTable1);
        expectedSchema.addTable(expectedTable3);

        actualSchema = new Schema("schema");
        actualSchema.addTable(actualTable1);
        actualSchema.addTable(actualTable2);
        actualSchema.addTable(actualTable3);
    }


    @Test
    public void appendSchemaContent() {
        dataSetAssert.appendSchemaContent(expectedSchema, actualSchema, stringBuilder);
        assertEquals("  schema.table1\n" +
                "    pk, column1, column2\n" +
                "    5, A, B\n" +
                "    null, C, null\n" +
                "  schema.table3\n" +
                "    <empty table>\n", stringBuilder.toString());
    }

    @Test
    public void emptyExpectedDataSet() {
        expectedSchema.getTables().clear();

        dataSetAssert.appendSchemaContent(expectedSchema, actualSchema, stringBuilder);
        assertTrue(stringBuilder.toString().isEmpty());
    }

    @Test
    public void emptyActualDataSet() {
        actualSchema.getTables().clear();

        dataSetAssert.appendSchemaContent(expectedSchema, actualSchema, stringBuilder);
        assertTrue(stringBuilder.toString().isEmpty());
    }
}
