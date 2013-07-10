package org.unitils.dbunit.dataset.comparison;

import org.junit.Before;
import org.junit.Test;
import org.unitils.dbunit.dataset.Row;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class TableDifferenceGetMissingRowsTest {

    /* Tested object */
    private TableDifference tableDifference;

    private Row row1;
    private Row row2;


    @Before
    public void initialize() {
        row1 = new Row();
        row2 = new Row();

        tableDifference = new TableDifference(null, null);
    }


    @Test
    public void getMissingRows() {
        tableDifference.addMissingRow(row1);
        tableDifference.addMissingRow(row2);

        List<Row> result = tableDifference.getMissingRows();
        assertEquals(asList(row1, row2), result);
    }

    @Test
    public void emptyWhenNoMissingRows() {
        List<Row> result = tableDifference.getMissingRows();
        assertTrue(result.isEmpty());
    }
}
