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
import org.unitils.dbunit.dataset.comparison.ColumnDifference;
import org.unitils.dbunit.dataset.comparison.RowDifference;
import org.unitils.dbunit.dataset.comparison.SchemaDifference;
import org.unitils.dbunit.dataset.comparison.TableDifference;
import org.unitils.mock.Mock;
import org.unitilsnew.UnitilsJUnit4;

import static java.util.Arrays.asList;
import static org.dbunit.dataset.datatype.DataType.VARCHAR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Tim Ducheyne
 */
public class DataSetAssertAssertEqualSchemasTest extends UnitilsJUnit4 {

    /* Tested object */
    private DataSetAssert dataSetAssert;

    private Mock<ObjectFormatter> objectFormatterMock;
    private Mock<Schema> expectedSchemaMock;
    private Schema actualSchema;

    private SchemaDifference schemaDifference;
    private TableDifference tableDifference;
    private Table expectedTable;
    private Table actualTable;


    @Before
    public void initialize() {
        dataSetAssert = new DataSetAssert(objectFormatterMock.getMock());

        objectFormatterMock.returns("A").format("valueA");
        objectFormatterMock.returns("C").format("valueC");

        Row expectedRow = new Row();
        expectedRow.addColumn(new Column("column1", VARCHAR, "valueA"));
        Row actualRow = new Row();
        actualRow.addColumn(new Column("column1", VARCHAR, "valueC"));

        expectedTable = new Table("table");
        expectedTable.addRow(expectedRow);
        actualTable = new Table("table");
        actualTable.addRow(actualRow);
        actualSchema = new Schema("schema");
        actualSchema.addTable(actualTable);

        ColumnDifference columnDifference = new ColumnDifference(new Column("column1", null, "valueA"), new Column("column1", null, "valueC"));
        RowDifference rowDifference = new RowDifference(expectedRow, actualRow);
        rowDifference.addColumnDifference(columnDifference);
        tableDifference = new TableDifference(expectedTable, actualTable);
        tableDifference.setIfBestRowDifference(rowDifference);
        schemaDifference = new SchemaDifference(expectedSchemaMock.getMock(), actualSchema);

        expectedSchemaMock.returns("schema").getName();
        expectedSchemaMock.returns(asList(expectedTable)).getTables();
        expectedSchemaMock.returns(schemaDifference).compare(actualSchema);
    }


    @Test
    public void noExceptionWhenNoSchemaDifference() {
        expectedSchemaMock.onceReturns(null).compare(actualSchema);

        dataSetAssert.assertEqualSchemas(expectedSchemaMock.getMock(), actualSchema);
    }

    @Test
    public void exceptionWhenSchemaDifference() {
        schemaDifference.addMissingTable(new Table("missingTable"));
        schemaDifference.addTableDifference(tableDifference);
        try {
            dataSetAssert.assertEqualSchemas(expectedSchemaMock.getMock(), actualSchema);
            fail("AssertionError expected");
        } catch (AssertionError e) {
            assertEquals("Assertion failed. Differences found between the expected data set and actual database content.\n" +
                    "Found missing table schema.missingTable\n" +
                    "Found differences for table schema.table:\n" +
                    "  Different row:\n" +
                    "    column1\n" +
                    "    A\n" +
                    "  Best matching differences:\n" +
                    "    column1: A <-> C\n" +
                    "Actual database content:\n" +
                    "  schema.table\n" +
                    "    column1\n" +
                    "    C\n", e.getMessage());
        }


    }
}
