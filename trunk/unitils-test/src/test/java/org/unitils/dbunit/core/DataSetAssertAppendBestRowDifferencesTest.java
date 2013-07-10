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
import org.unitils.dbunit.dataset.Table;
import org.unitils.dbunit.dataset.comparison.ColumnDifference;
import org.unitils.dbunit.dataset.comparison.RowDifference;
import org.unitils.dbunit.dataset.comparison.TableDifference;
import org.unitils.mock.Mock;
import org.unitilsnew.UnitilsJUnit4;

import static org.dbunit.dataset.datatype.DataType.VARCHAR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class DataSetAssertAppendBestRowDifferencesTest extends UnitilsJUnit4 {

    /* Tested object */
    private DataSetAssert dataSetAssert;

    private Mock<ObjectFormatter> objectFormatterMock;

    private TableDifference tableDifference;
    private ColumnDifference columnDifference1;
    private ColumnDifference columnDifference2;
    private StringBuilder stringBuilder;
    private Row row1;
    private Row row2;
    private Row row3;
    private Table expectedTable;
    private Table actualTable;


    @Before
    public void initialize() {
        dataSetAssert = new DataSetAssert(objectFormatterMock.getMock());

        objectFormatterMock.returns("A").format("valueA");
        objectFormatterMock.returns("B").format("valueB");
        objectFormatterMock.returns("C").format("valueC");
        objectFormatterMock.returns("D").format("valueD");

        stringBuilder = new StringBuilder();
        expectedTable = new Table("table1");
        actualTable = new Table("table2");
        row1 = new Row();
        row1.addColumn(new Column("column1", VARCHAR, "valueA"));
        row1.addColumn(new Column("column2", VARCHAR, "valueB"));
        row2 = new Row();
        row2.addColumn(new Column("column1", VARCHAR, "valueC"));
        row3 = new Row();
        row3.addColumn(new Column("column3", VARCHAR, "valueD"));

        tableDifference = new TableDifference(expectedTable, actualTable);
        columnDifference1 = new ColumnDifference(new Column("column2", null, "valueA"), new Column("column2", null, "valueB"));
        columnDifference2 = new ColumnDifference(new Column("column3", null, "valueA"), new Column("column3", null, "valueC"));
    }


    @Test
    public void rowDifferences() {
        RowDifference rowDifference1 = new RowDifference(row1, null);
        rowDifference1.addMissingColumn(new Column("column1", null, null));
        rowDifference1.addColumnDifference(columnDifference1);
        rowDifference1.addColumnDifference(columnDifference2);
        tableDifference.setIfBestRowDifference(rowDifference1);
        RowDifference rowDifference2 = new RowDifference(row3, null);
        rowDifference2.addColumnDifference(columnDifference2);
        tableDifference.setIfBestRowDifference(rowDifference2);

        dataSetAssert.appendBestRowDifferences(tableDifference, stringBuilder);
        String difference1 = "  Different row:\n" +
                "    column1, column2\n" +
                "    A, B\n" +
                "  Best matching differences:\n" +
                "    column1: missing\n" +
                "    column2: A <-> B\n" +
                "    column3: A <-> C\n";
        String difference2 = "  Different row:\n" +
                "    column3\n" +
                "    D\n" +
                "  Best matching differences:\n" +
                "    column3: A <-> C\n";
        String result = stringBuilder.toString();
        assertTrue(result, result.equals(difference1 + difference2) || result.equals(difference2 + difference1));
    }

    @Test
    public void noMissingColumns() {
        RowDifference rowDifference1 = new RowDifference(row1, null);
        rowDifference1.addColumnDifference(columnDifference1);
        tableDifference.setIfBestRowDifference(rowDifference1);

        dataSetAssert.appendBestRowDifferences(tableDifference, stringBuilder);
        assertEquals("  Different row:\n" +
                "    column1, column2\n" +
                "    A, B\n" +
                "  Best matching differences:\n" +
                "    column2: A <-> B\n", stringBuilder.toString());
    }

    @Test
    public void onlyMissingColumns() {
        RowDifference rowDifference1 = new RowDifference(row1, null);
        rowDifference1.addMissingColumn(new Column("column1", null, null));
        tableDifference.setIfBestRowDifference(rowDifference1);

        dataSetAssert.appendBestRowDifferences(tableDifference, stringBuilder);
        assertEquals("  Different row:\n" +
                "    column1, column2\n" +
                "    A, B\n" +
                "  Best matching differences:\n" +
                "    column1: missing\n", stringBuilder.toString());
    }

    @Test
    public void emptyWhenNoRowDifferences() {
        dataSetAssert.appendBestRowDifferences(tableDifference, stringBuilder);
        assertTrue(stringBuilder.toString().isEmpty());
    }
}
