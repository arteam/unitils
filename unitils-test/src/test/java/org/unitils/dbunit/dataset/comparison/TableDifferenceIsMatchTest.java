package org.unitils.dbunit.dataset.comparison;

import org.junit.Before;
import org.junit.Test;
import org.unitils.dbunit.dataset.Row;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class TableDifferenceIsMatchTest {

    /* Tested object */
    private TableDifference tableDifference;

    private Row row;
    private RowDifference rowDifference;


    @Before
    public void initialize() {
        row = new Row();
        rowDifference = new RowDifference(null, null);

        tableDifference = new TableDifference(null, null);
    }


    @Test
    public void trueWhenNoDifferences() {
        boolean result = rowDifference.isMatch();
        assertTrue(result);
    }

    @Test
    public void falseWhenMissingRow() {
        tableDifference.addMissingRow(row);

        boolean result = tableDifference.isMatch();
        assertFalse(result);
    }

    @Test
    public void falseWhenRowDifference() {
        tableDifference.setIfBestRowDifference(rowDifference);

        boolean result = tableDifference.isMatch();
        assertFalse(result);
    }
}
