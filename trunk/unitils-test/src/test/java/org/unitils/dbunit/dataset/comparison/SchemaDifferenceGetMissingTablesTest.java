package org.unitils.dbunit.dataset.comparison;

import org.junit.Before;
import org.junit.Test;
import org.unitils.dbunit.dataset.Table;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class SchemaDifferenceGetMissingTablesTest {

    /* Tested object */
    private SchemaDifference schemaDifference;

    private Table table1;
    private Table table2;


    @Before
    public void initialize() {
        table1 = new Table("table1");
        table2 = new Table("table2");

        schemaDifference = new SchemaDifference(null, null);
    }


    @Test
    public void getMissingTables() {
        schemaDifference.addMissingTable(table1);
        schemaDifference.addMissingTable(table2);

        List<Table> result = schemaDifference.getMissingTables();
        assertEquals(asList(table1, table2), result);
    }

    @Test
    public void emptyWhenNoMissingTables() {
        List<Table> result = schemaDifference.getMissingTables();
        assertTrue(result.isEmpty());
    }
}
