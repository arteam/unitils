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
import org.unitils.dbunit.dataset.comparison.SchemaDifference;
import org.unitils.dbunit.dataset.comparison.TableDifference;
import org.unitils.mock.Mock;
import org.unitilsnew.UnitilsJUnit4;

import static org.dbunit.dataset.datatype.DataType.VARCHAR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class DataSetAssertAppendTableDifferencesTest extends UnitilsJUnit4 {

    /* Tested object */
    private DataSetAssert dataSetAssert;

    private Mock<ObjectFormatter> objectFormatterMock;
    private StringBuilder stringBuilder;

    private Table expectedTable1;
    private SchemaDifference schemaDifference;
    private Table expectedTable2;
    private Table actualTable1;
    private Table actualTable2;
    private Row row1;
    private Row row2;
    private Row row3;
    private TableDifference tableDifference1;
    private TableDifference tableDifference2;


    @Before
    public void initialize() {
        dataSetAssert = new DataSetAssert(objectFormatterMock.getMock());

        expectedTable1 = new Table("table1");
        expectedTable2 = new Table("table2");
        actualTable1 = new Table("table1");
        actualTable2 = new Table("table2");

        row1 = new Row();
        row1.addColumn(new Column("column1", VARCHAR, "valueA"));
        row1.addColumn(new Column("column2", VARCHAR, "valueB"));
        row2 = new Row();
        row2.addColumn(new Column("column1", VARCHAR, "valueC"));
        row3 = new Row();
        row3.addColumn(new Column("column3", VARCHAR, "valueD"));

        tableDifference1 = new TableDifference(expectedTable1, actualTable1);
        tableDifference2 = new TableDifference(expectedTable2, actualTable2);
        stringBuilder = new StringBuilder();

        objectFormatterMock.returns("A").format("valueA");
        objectFormatterMock.returns("B").format("valueB");
        objectFormatterMock.returns("C").format("valueC");
        objectFormatterMock.returns("D").format("valueD");
        schemaDifference = new SchemaDifference(new Schema("schema"), new Schema("schema"));
    }


    @Test
    public void expectedTableEmptyButActualTableNot() {
        schemaDifference.addTableDifference(tableDifference1);
        schemaDifference.addTableDifference(tableDifference2);

        dataSetAssert.appendTableDifferences(schemaDifference, stringBuilder);
        assertEquals("Expected table to be empty but found rows for table schema.table1\n" +
                "Expected table to be empty but found rows for table schema.table2\n", stringBuilder.toString());
    }

    @Test
    public void missingRows() {
        expectedTable1.addRow(row1);
        expectedTable1.addRow(row2);
        tableDifference1.addMissingRow(row1);
        tableDifference1.addMissingRow(row2);
        expectedTable2.addRow(row3);
        tableDifference2.addMissingRow(row3);
        schemaDifference.addTableDifference(tableDifference1);
        schemaDifference.addTableDifference(tableDifference2);

        dataSetAssert.appendTableDifferences(schemaDifference, stringBuilder);
        assertEquals("Found differences for table schema.table1:\n" +
                "  Missing row:\n" +
                "    column1, column2\n" +
                "    A, B\n" +
                "  Missing row:\n" +
                "    column1\n" +
                "    C\n" +
                "Found differences for table schema.table2:\n" +
                "  Missing row:\n" +
                "    column3\n" +
                "    D\n", stringBuilder.toString());
    }

    // todo best matches

    @Test
    public void emptyWhenNoTableDifferences() {
        dataSetAssert.appendTableDifferences(schemaDifference, stringBuilder);
        assertTrue(stringBuilder.toString().isEmpty());
    }
}
