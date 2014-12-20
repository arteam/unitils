package org.unitils.dbunit.dataset.comparison;

import org.junit.Before;
import org.junit.Test;
import org.unitils.dbunit.dataset.Column;
import org.unitils.dbunit.dataset.Row;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;

/**
 * @author Tim Ducheyne
 */
public class TableDifferenceSetIfBestRowDifferenceTest {

    /* Tested object */
    private TableDifference tableDifference;

    private Row row1;
    private Row row2;
    private Row actualRow1;
    private Row actualRow2;
    private RowDifference rowDifference1;
    private RowDifference rowDifference1b;
    private RowDifference rowDifference2;


    @Before
    public void initialize() {
        row1 = new Row();
        row2 = new Row();
        actualRow1 = new Row();
        actualRow2 = new Row();

        rowDifference1 = new RowDifference(row1, actualRow1);
        rowDifference1b = new RowDifference(row1, actualRow1);
        rowDifference2 = new RowDifference(row2, actualRow2);

        tableDifference = new TableDifference(null, null);
    }


    @Test
    public void firstRowDifference() {
        tableDifference.setIfBestRowDifference(rowDifference1);
        List<RowDifference> result = tableDifference.getBestRowDifferences();
        assertEquals(asList(rowDifference1), result);
    }

    @Test
    public void setWhenBetterRowDifference() {
        rowDifference1.addMissingColumn(new Column(null, null, null));

        tableDifference.setIfBestRowDifference(rowDifference1);
        tableDifference.setIfBestRowDifference(rowDifference1b);
        List<RowDifference> result = tableDifference.getBestRowDifferences();
        assertEquals(asList(rowDifference1b), result);
    }

    @Test
    public void doNotSetWhenEqualRowDifference() {
        rowDifference1.addMissingColumn(new Column(null, null, null));
        rowDifference1b.addMissingColumn(new Column(null, null, null));

        tableDifference.setIfBestRowDifference(rowDifference1);
        tableDifference.setIfBestRowDifference(rowDifference1b);
        List<RowDifference> result = tableDifference.getBestRowDifferences();
        assertEquals(asList(rowDifference1), result);
    }

    @Test
    public void doNotSetWhenWorseRowDifference() {
        rowDifference1b.addMissingColumn(new Column(null, null, null));

        tableDifference.setIfBestRowDifference(rowDifference1);
        tableDifference.setIfBestRowDifference(rowDifference1b);
        List<RowDifference> result = tableDifference.getBestRowDifferences();
        assertEquals(asList(rowDifference1), result);
    }

    @Test
    public void doNotSetWhenBetterButForOtherRow() {
        rowDifference1.addMissingColumn(new Column(null, null, null));

        tableDifference.setIfBestRowDifference(rowDifference1);
        tableDifference.setIfBestRowDifference(rowDifference2);
        List<RowDifference> result = tableDifference.getBestRowDifferences();
        assertLenientEquals(asList(rowDifference1, rowDifference2), result);
    }
}
