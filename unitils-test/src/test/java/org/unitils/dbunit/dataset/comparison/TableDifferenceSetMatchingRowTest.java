package org.unitils.dbunit.dataset.comparison;

import org.junit.Before;
import org.junit.Test;
import org.unitils.dbunit.dataset.Row;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;

/**
 * @author Tim Ducheyne
 */
public class TableDifferenceSetMatchingRowTest {

    /* Tested object */
    private TableDifference tableDifference;

    private Row row1;
    private Row row2;
    private Row actualRow1;
    private Row actualRow2;


    @Before
    public void initialize() {
        row1 = new Row();
        row2 = new Row();
        actualRow1 = new Row();
        actualRow2 = new Row();

        tableDifference = new TableDifference(null, null);
    }


    @Test
    public void allBestDifferencesUsingActualRowAreRemoved() {
        RowDifference rowDifference1 = new RowDifference(new Row(), actualRow1);
        RowDifference rowDifference2 = new RowDifference(new Row(), actualRow2);
        RowDifference rowDifference3 = new RowDifference(new Row(), actualRow1);
        tableDifference.setIfBestRowDifference(rowDifference1);
        tableDifference.setIfBestRowDifference(rowDifference2);
        tableDifference.setIfBestRowDifference(rowDifference3);

        tableDifference.setMatchingRow(row1, actualRow1);
        List<RowDifference> result = tableDifference.getBestRowDifferences();
        assertEquals(asList(rowDifference2), result);
    }

    @Test
    public void bestRowDifferencesForRowIsRemoved() {
        RowDifference rowDifference1 = new RowDifference(row1, new Row());
        RowDifference rowDifference2 = new RowDifference(row2, new Row());
        tableDifference.setIfBestRowDifference(rowDifference1);
        tableDifference.setIfBestRowDifference(rowDifference2);

        tableDifference.setMatchingRow(row1, actualRow1);
        List<RowDifference> result = tableDifference.getBestRowDifferences();
        assertEquals(asList(rowDifference2), result);
    }

    @Test
    public void ignoredWhenRowsNotFound() {
        RowDifference rowDifference1 = new RowDifference(row1, actualRow1);
        RowDifference rowDifference2 = new RowDifference(row2, actualRow2);
        tableDifference.setIfBestRowDifference(rowDifference1);
        tableDifference.setIfBestRowDifference(rowDifference2);

        tableDifference.setMatchingRow(new Row(), new Row());
        List<RowDifference> result = tableDifference.getBestRowDifferences();
        assertLenientEquals(asList(rowDifference1, rowDifference2), result);
    }
}
