package org.unitils.dbunit.dataset.comparison;

import org.junit.Before;
import org.junit.Test;
import org.unitils.dbunit.dataset.Column;

import static org.dbunit.dataset.datatype.DataType.VARCHAR;
import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 */
public class ColumnDifferenceGetColumnTest {

    /* Tested object */
    private ColumnDifference columnDifference;

    private Column column;
    private Column actualColumn;


    @Before
    public void initialize() {
        column = new Column("column", VARCHAR, "111");
        actualColumn = new Column("column", VARCHAR, "222");

        columnDifference = new ColumnDifference(column, actualColumn);
    }


    @Test
    public void getColumn() {
        Column result = columnDifference.getColumn();
        assertSame(column, result);
    }
}
