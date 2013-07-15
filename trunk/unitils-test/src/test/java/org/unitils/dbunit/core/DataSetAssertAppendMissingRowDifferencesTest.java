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
import org.unitils.core.util.ObjectFormatter;
import org.unitils.dbunit.dataset.Column;
import org.unitils.dbunit.dataset.Row;
import org.unitils.dbunit.dataset.comparison.TableDifference;
import org.unitils.mock.Mock;

import static org.dbunit.dataset.datatype.DataType.VARCHAR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class DataSetAssertAppendMissingRowDifferencesTest extends UnitilsJUnit4 {

    /* Tested object */
    private DataSetAssert dataSetAssert;

    private Mock<ObjectFormatter> objectFormatterMock;

    private StringBuilder stringBuilder;
    private TableDifference tableDifference;
    private Row row1;
    private Row row2;


    @Before
    public void initialize() {
        dataSetAssert = new DataSetAssert(objectFormatterMock.getMock());

        objectFormatterMock.returns("A").format("valueA");
        objectFormatterMock.returns("B").format("valueB");
        objectFormatterMock.returns("C").format("valueC");

        stringBuilder = new StringBuilder();
        tableDifference = new TableDifference(null, null);

        row1 = new Row();
        row1.addColumn(new Column("column1", VARCHAR, "valueA"));
        row1.addColumn(new Column("column2", VARCHAR, "valueB"));
        row2 = new Row();
        row2.addColumn(new Column("column1", VARCHAR, "valueC"));
    }


    @Test
    public void appendSchemaContent() {
        tableDifference.addMissingRow(row1);
        tableDifference.addMissingRow(row2);

        dataSetAssert.appendMissingRowDifferences(tableDifference, stringBuilder);
        assertEquals("  Missing row:\n" +
                "    column1, column2\n" +
                "    A, B\n" +
                "  Missing row:\n" +
                "    column1\n" +
                "    C\n", stringBuilder.toString());
    }

    @Test
    public void emptyWhenNoMissingRows() {
        dataSetAssert.appendMissingRowDifferences(tableDifference, stringBuilder);
        assertTrue(stringBuilder.toString().isEmpty());
    }
}
