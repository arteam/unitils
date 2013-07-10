package org.unitils.dbunit.dataset.comparison;

import org.junit.Before;
import org.junit.Test;
import org.unitils.dbunit.dataset.Row;

import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 */
public class RowDifferenceGetRowTest {

    /* Tested object */
    private RowDifference rowDifference;

    private Row row;
    private Row actualRow;


    @Before
    public void initialize() {
        row = new Row();
        actualRow = new Row();

        rowDifference = new RowDifference(row, actualRow);
    }


    @Test
    public void getRow() {
        Row result = rowDifference.getRow();
        assertSame(row, result);
    }
}
