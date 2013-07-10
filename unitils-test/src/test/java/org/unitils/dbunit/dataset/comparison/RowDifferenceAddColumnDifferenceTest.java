package org.unitils.dbunit.dataset.comparison;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

/**
 * @author Tim Ducheyne
 */
public class RowDifferenceAddColumnDifferenceTest {

    /* Tested object */
    private RowDifference rowDifference;

    private ColumnDifference columnDifference1;
    private ColumnDifference columnDifference2;


    @Before
    public void initialize() {
        columnDifference1 = new ColumnDifference(null, null);
        columnDifference2 = new ColumnDifference(null, null);

        rowDifference = new RowDifference(null, null);
    }


    @Test
    public void addColumnDifferences() {
        rowDifference.addColumnDifference(columnDifference1);
        rowDifference.addColumnDifference(columnDifference2);
        List<ColumnDifference> result = rowDifference.getColumnDifferences();
        assertEquals(asList(columnDifference1, columnDifference2), result);
    }
}
