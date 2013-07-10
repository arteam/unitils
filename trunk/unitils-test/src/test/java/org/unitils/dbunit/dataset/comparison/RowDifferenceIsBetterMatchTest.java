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
public class RowDifferenceIsBetterMatchTest {

    /* Tested object */
    private RowDifference rowDifference1;
    private RowDifference rowDifference2;

    private Column column;
    private ColumnDifference columnDifference;


    @Before
    public void initialize() {
        column = new Column("column", VARCHAR, "1");
        columnDifference = new ColumnDifference(null, null);

        rowDifference1 = new RowDifference(null, null);
        rowDifference2 = new RowDifference(null, null);
    }


    @Test
    public void trueWhenLessMissingColumns() {
        rowDifference1.addMissingColumn(column);
        rowDifference2.addMissingColumn(column);
        rowDifference2.addMissingColumn(column);

        boolean result = rowDifference1.isBetterMatch(rowDifference2);
        assertTrue(result);
    }

    @Test
    public void falseWhenMoreMissingColumns() {
        rowDifference1.addMissingColumn(column);
        rowDifference1.addMissingColumn(column);
        rowDifference2.addMissingColumn(column);

        boolean result = rowDifference1.isBetterMatch(rowDifference2);
        assertFalse(result);
    }

    @Test
    public void trueWhenLessColumnDifferences() {
        rowDifference1.addColumnDifference(columnDifference);
        rowDifference2.addColumnDifference(columnDifference);
        rowDifference2.addColumnDifference(columnDifference);

        boolean result = rowDifference1.isBetterMatch(rowDifference2);
        assertTrue(result);
    }

    @Test
    public void falseWhenMoreColumnDifferences() {
        rowDifference1.addColumnDifference(columnDifference);
        rowDifference1.addColumnDifference(columnDifference);
        rowDifference2.addColumnDifference(columnDifference);

        boolean result = rowDifference1.isBetterMatch(rowDifference2);
        assertFalse(result);
    }

    @Test
    public void falseWhenEqualDifferences() {
        rowDifference1.addMissingColumn(column);
        rowDifference1.addColumnDifference(columnDifference);
        rowDifference2.addMissingColumn(column);
        rowDifference2.addColumnDifference(columnDifference);

        boolean result = rowDifference1.isBetterMatch(rowDifference2);
        assertFalse(result);
    }

    @Test
    public void falseWhenBothHaveNoDifferences() {
        boolean result = rowDifference1.isBetterMatch(rowDifference2);
        assertFalse(result);
    }
}
