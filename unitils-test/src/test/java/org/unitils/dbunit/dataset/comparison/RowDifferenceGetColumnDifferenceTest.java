package org.unitils.dbunit.dataset.comparison;

import org.junit.Before;
import org.junit.Test;
import org.unitils.dbunit.dataset.Column;

import static org.dbunit.dataset.datatype.DataType.VARCHAR;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 */
public class RowDifferenceGetColumnDifferenceTest {

    /* Tested object */
    private RowDifference rowDifference;

    private ColumnDifference columnDifference1;
    private ColumnDifference columnDifference2;


    @Before
    public void initialize() {
        Column column1 = new Column("column1", VARCHAR, "1");
        Column column2 = new Column("column2", VARCHAR, "2");
        columnDifference1 = new ColumnDifference(column1, column1);
        columnDifference2 = new ColumnDifference(column2, column2);

        rowDifference = new RowDifference(null, null);
    }


    @Test
    public void getColumnDifference() {
        rowDifference.addColumnDifference(columnDifference1);
        rowDifference.addColumnDifference(columnDifference2);

        ColumnDifference result = rowDifference.getColumnDifference("column1");
        assertSame(columnDifference1, result);
    }

    @Test
    public void columnNameIsCaseInsensitive() {
        rowDifference.addColumnDifference(columnDifference1);

        ColumnDifference result = rowDifference.getColumnDifference("COLUMN1");
        assertSame(columnDifference1, result);
    }

    @Test
    public void nullWhenColumnNotFound() {
        rowDifference.addColumnDifference(columnDifference1);

        ColumnDifference result = rowDifference.getColumnDifference("xxx");
        assertNull(result);
    }

    @Test
    public void nullWhenNoColumnDifference() {
        ColumnDifference result = rowDifference.getColumnDifference("column1");
        assertNull(result);
    }
}
