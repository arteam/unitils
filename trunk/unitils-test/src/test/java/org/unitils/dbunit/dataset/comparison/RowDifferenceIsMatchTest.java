package org.unitils.dbunit.dataset.comparison;

import org.junit.Before;
import org.junit.Test;
import org.unitils.dbunit.dataset.Column;

import static org.dbunit.dataset.datatype.DataType.VARCHAR;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class RowDifferenceIsMatchTest {

    /* Tested object */
    private RowDifference rowDifference;

    private Column column;
    private ColumnDifference columnDifference;


    @Before
    public void initialize() {
        column = new Column("column", VARCHAR, "1");
        columnDifference = new ColumnDifference(null, null);

        rowDifference = new RowDifference(null, null);
    }


    @Test
    public void trueWhenNoDifferences() {
        boolean result = rowDifference.isMatch();
        assertTrue(result);
    }

    @Test
    public void falseWhenMissingColumn() {
        rowDifference.addMissingColumn(column);

        boolean result = rowDifference.isMatch();
        assertFalse(result);
    }

    @Test
    public void falseWhenColumnDifference() {
        rowDifference.addColumnDifference(columnDifference);

        boolean result = rowDifference.isMatch();
        assertFalse(result);
    }
}
