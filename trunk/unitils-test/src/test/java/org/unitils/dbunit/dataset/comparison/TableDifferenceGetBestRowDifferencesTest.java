package org.unitils.dbunit.dataset.comparison;

import org.junit.Before;
import org.junit.Test;
import org.unitils.dbunit.dataset.Row;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;

/**
 * @author Tim Ducheyne
 */
public class TableDifferenceGetBestRowDifferencesTest {

    /* Tested object */
    private TableDifference tableDifference;

    private RowDifference rowDifference1;
    private RowDifference rowDifference2;


    @Before
    public void initialize() {
        rowDifference1 = new RowDifference(new Row(), new Row());
        rowDifference2 = new RowDifference(new Row(), new Row());

        tableDifference = new TableDifference(null, null);
    }


    @Test
    public void getBestRowDifferences() {
        tableDifference.setIfBestRowDifference(rowDifference1);
        tableDifference.setIfBestRowDifference(rowDifference2);

        List<RowDifference> result = tableDifference.getBestRowDifferences();
        assertLenientEquals(asList(rowDifference1, rowDifference2), result);
    }

    @Test
    public void emptyWhenNoRowDifferences() {
        List<RowDifference> result = tableDifference.getBestRowDifferences();
        assertTrue(result.isEmpty());
    }
}
