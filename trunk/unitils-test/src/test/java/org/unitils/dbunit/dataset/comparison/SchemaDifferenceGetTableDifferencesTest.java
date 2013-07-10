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
public class SchemaDifferenceGetTableDifferencesTest {

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
    public void getTableDifferences() {
        schemaDifference.addTableDifference(tableDifference1);
        schemaDifference.addTableDifference(tableDifference2);

        List<TableDifference> result = schemaDifference.getTableDifferences();
        assertEquals(asList(tableDifference1, tableDifference2), result);
    }

    @Test
    public void emptyWhenNoTableDifferences() {
        List<TableDifference> result = schemaDifference.getTableDifferences();
        assertTrue(result.isEmpty());
    }
}
