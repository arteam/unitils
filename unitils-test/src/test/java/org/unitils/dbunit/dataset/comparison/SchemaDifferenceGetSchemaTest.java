package org.unitils.dbunit.dataset.comparison;

import org.junit.Before;
import org.junit.Test;
import org.unitils.dbunit.dataset.Schema;

import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 */
public class SchemaDifferenceGetSchemaTest {

    /* Tested object */
    private SchemaDifference schemaDifference;

    private Schema schema;
    private Schema actualSchema;


    @Before
    public void initialize() {
        schema = new Schema("schema");
        actualSchema = new Schema("schema");

        schemaDifference = new SchemaDifference(schema, actualSchema);
    }


    @Test
    public void getSchema() {
        Schema result = schemaDifference.getSchema();
        assertSame(schema, result);
    }
}
