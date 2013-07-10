package org.unitils.dbunit.dataset.comparison;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

/**
 * @author Tim Ducheyne
 */
public class SchemaDifferenceAddTableDifferenceTest {

    /* Tested object */
    private SchemaDifference schemaDifference;

    private TableDifference tableDifference1;
    private TableDifference tableDifference2;


    @Before
    public void initialize() {
        tableDifference1 = new TableDifference(null, null);
        tableDifference2 = new TableDifference(null, null);

        schemaDifference = new SchemaDifference(null, null);
    }


    @Test
    public void addTableDifference() {
        schemaDifference.addTableDifference(tableDifference1);
        schemaDifference.addTableDifference(tableDifference2);
        List<TableDifference> result = schemaDifference.getTableDifferences();
        assertEquals(asList(tableDifference1, tableDifference2), result);
    }
}
