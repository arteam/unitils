package org.unitils.dbunit.dataset.comparison;

import org.junit.Before;
import org.junit.Test;
import org.unitils.dbunit.dataset.Row;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 */
public class TableDifferenceGetBestRowDifferenceTest {

    /* Tested object */
    private TableDifference tableDifference;

    private Row row1;
    private Row row2;
    private RowDifference rowDifference1;
    private RowDifference rowDifference2;


    @Before
    public void initialize() {
        row1 = new Row();
        row2 = new Row();
        rowDifference1 = new RowDifference(row1, new Row());
        rowDifference2 = new RowDifference(row2, new Row());

        tableDifference = new TableDifference(null, null);
    }


    @Test
    public void getBestRowDifference() {
        tableDifference.setIfBestRowDifference(rowDifference1);
        tableDifference.setIfBestRowDifference(rowDifference2);

        RowDifference result = tableDifference.getBestRowDifference(row2);
        assertSame(rowDifference2, result);
    }

    @Test
    public void nullWhenRowNotFound() {
        RowDifference result = tableDifference.getBestRowDifference(new Row());
        assertNull(result);
    }

    @Test
    public void nullWhenNull() {
        RowDifference result = tableDifference.getBestRowDifference(null);
        assertNull(result);
    }
}
