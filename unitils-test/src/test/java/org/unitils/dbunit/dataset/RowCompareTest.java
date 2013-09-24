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
import org.unitils.dbunit.dataset.comparison.ColumnDifference;
import org.unitils.dbunit.dataset.comparison.RowDifference;

import java.util.List;

import static java.util.Arrays.asList;
import static org.dbunit.dataset.datatype.DataType.VARCHAR;
import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class RowCompareTest {

    /* Tested object */
    private Row row = new Row();

    private Row actualRow;
    private Column column1;
    private Column column1b;
    private Column column2;
    private Column column2b;


    @Before
    public void initialize() {
        actualRow = new Row();
        column1 = new Column("column1", VARCHAR, "aaa");
        column1b = new Column("column1", VARCHAR, "bbb");
        column2 = new Column("column2", VARCHAR, "ccc");
        column2b = new Column("column2", VARCHAR, "ddd");
    }


    @Test
    public void nullWhenNoDifferences() {
        row.addPrimaryKeyColumn(column1);
        row.addColumn(column2);
        actualRow.addPrimaryKeyColumn(column1);
        actualRow.addColumn(column2);

        RowDifference result = row.compare(actualRow);
        assertNull(result);
    }

    @Test
    public void nullWhenBothHaveNoColumns() {
        RowDifference result = row.compare(actualRow);
        assertNull(result);
    }

    @Test
    public void nullWhenEqualValuesButMoreActualColumns() {
        row.addPrimaryKeyColumn(column1);
        actualRow.addPrimaryKeyColumn(column1);
        actualRow.addColumn(column2);

        RowDifference result = row.compare(actualRow);
        assertNull(result);
    }

    @Test
    public void differenceWhenMissingActualColumns() {
        row.addPrimaryKeyColumn(column1);
        row.addColumn(column2);

        RowDifference result = row.compare(actualRow);
        assertEquals(asList(column1, column2), result.getMissingColumns());
    }

    @Test
    public void differenceWhenDifferentValues() {
        row.addPrimaryKeyColumn(column1);
        row.addColumn(column2);
        actualRow.addPrimaryKeyColumn(column1b);
        actualRow.addColumn(column2b);

        RowDifference result = row.compare(actualRow);
        List<ColumnDifference> columnDifferences = result.getColumnDifferences();
        assertEquals(2, columnDifferences.size());
        assertSame(column1, columnDifferences.get(0).getColumn());
        assertSame(column1b, columnDifferences.get(0).getActualColumn());
        assertSame(column2, columnDifferences.get(1).getColumn());
        assertSame(column2b, columnDifferences.get(1).getActualColumn());
    }
}