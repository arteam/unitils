package org.unitils.dbunit.dataset.comparison;

import org.junit.Before;
import org.junit.Test;
import org.unitils.dbunit.dataset.Column;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class RowDifferenceGetMissingColumnsTest {

    /* Tested object */
    private RowDifference rowDifference;

    private Column column1;
    private Column column2;


    @Before
    public void initialize() {
        column1 = new Column(null, null, null);
        column2 = new Column(null, null, null);

        rowDifference = new RowDifference(null, null);
    }


    @Test
    public void getMissingColumns() {
        rowDifference.addMissingColumn(column1);
        rowDifference.addMissingColumn(column2);

        List<Column> result = rowDifference.getMissingColumns();
        assertEquals(asList(column1, column2), result);
    }

    @Test
    public void emptyWhenNoMissingColumns() {
        List<Column> result = rowDifference.getMissingColumns();
        assertTrue(result.isEmpty());
    }
}
