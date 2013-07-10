package org.unitils.dbunit.dataset.comparison;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class RowDifferenceGetColumnDifferencesTest {

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
    public void getColumnDifferences() {
        rowDifference.addColumnDifference(columnDifference1);
        rowDifference.addColumnDifference(columnDifference2);

        List<ColumnDifference> result = rowDifference.getColumnDifferences();
        assertEquals(asList(columnDifference1, columnDifference2), result);
    }

    @Test
    public void emptyWhenNoColumnDifferences() {
        List<ColumnDifference> result = rowDifference.getColumnDifferences();
        assertTrue(result.isEmpty());
    }
}
