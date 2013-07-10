package org.unitils.dbunit.dataset.comparison;

import org.junit.Before;
import org.junit.Test;
import org.unitils.dbunit.dataset.Table;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class SchemaDifferenceIsMatchTest {

    /* Tested object */
    private SchemaDifference schemaDifference;

    private Table table;
    private TableDifference tableDifference;


    @Before
    public void initialize() {
        table = new Table("table1");
        tableDifference = new TableDifference(null, null);

        schemaDifference = new SchemaDifference(null, null);
    }


    @Test
    public void trueWhenNoDifferences() {
        boolean result = schemaDifference.isMatch();
        assertTrue(result);
    }

    @Test
    public void falseWhenMissingColumn() {
        schemaDifference.addMissingTable(table);

        boolean result = schemaDifference.isMatch();
        assertFalse(result);
    }

    @Test
    public void falseWhenColumnDifference() {
        schemaDifference.addTableDifference(tableDifference);

        boolean result = schemaDifference.isMatch();
        assertFalse(result);
    }
}
