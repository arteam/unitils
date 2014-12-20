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
import org.unitils.dbunit.dataset.comparison.TableDifference;

import static java.util.Arrays.asList;
import static org.dbunit.dataset.datatype.DataType.VARCHAR;
import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class TableCompareTest {

    /* Tested object */
    private Table table;

    private Table actualTable;
    private Row row1;
    private Row row1b;
    private Row row2;
    private Row row2b;


    @Before
    public void initialize() {
        table = new Table("name");

        actualTable = new Table("name");
        row1 = new Row();
        row1.addPrimaryKeyColumn(new Column("pk", VARCHAR, "1"));
        row1.addColumn(new Column("column1", VARCHAR, "aaa"));
        row1b = new Row();
        row1b.addPrimaryKeyColumn(new Column("pk", VARCHAR, "1"));
        row1b.addColumn(new Column("column1", VARCHAR, "bbb"));
        row2 = new Row();
        row2.addPrimaryKeyColumn(new Column("pk", VARCHAR, "2"));
        row2.addColumn(new Column("column1", VARCHAR, "ccc"));
        row2b = new Row();
        row2b.addPrimaryKeyColumn(new Column("pk", VARCHAR, "2"));
        row2b.addColumn(new Column("column1", VARCHAR, "ddd"));
    }


    @Test
    public void nullWhenNoDifference() {
        table.addRow(row1);
        table.addRow(row2);
        actualTable.addRow(row1);
        actualTable.addRow(row2);

        TableDifference result = table.compare(actualTable);
        assertNull(result);
    }

    @Test
    public void nullWhenBothHaveNoRows() {
        TableDifference result = table.compare(actualTable);
        assertNull(result);
    }

    @Test
    public void nullWhenEqualButMoreActualRows() {
        table.addRow(row1);
        actualTable.addRow(row1);
        actualTable.addRow(row2);

        TableDifference result = table.compare(actualTable);
        assertNull(result);
    }

    @Test
    public void differenceWhenRowEmptyButActualRowNot() {
        actualTable.addRow(row1);

        TableDifference result = table.compare(actualTable);
        assertTrue(result.getMissingRows().isEmpty());
        assertTrue(result.getBestRowDifferences().isEmpty());
    }

    @Test
    public void differenceWhenMissingActualRows() {
        table.addRow(row1);
        table.addRow(row2);
        actualTable.addRow(row1);

        TableDifference result = table.compare(actualTable);
        assertEquals(asList(row2), result.getMissingRows());
    }

    @Test
    public void differenceWhenDifferentValues() {
        table.addRow(row1);
        table.addRow(row2);
        actualTable.addRow(row1b);
        actualTable.addRow(row2b);

        TableDifference result = table.compare(actualTable);
        assertEquals(2, result.getBestRowDifferences().size());
        assertSame(row1, result.getBestRowDifference(row1).getRow());
        assertSame(row1b, result.getBestRowDifference(row1).getActualRow());
        assertSame(row2, result.getBestRowDifference(row2).getRow());
        assertSame(row2b, result.getBestRowDifference(row2).getActualRow());
    }
}