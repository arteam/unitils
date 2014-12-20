package org.unitils.dbunit.dataset.comparison;

import org.junit.Before;
import org.junit.Test;
import org.unitils.dbunit.dataset.Schema;

import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 */
public class SchemaDifferenceGetActualSchemaTest {

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
    public void getActualSchema() {
        Schema result = schemaDifference.getActualSchema();
        assertSame(actualSchema, result);
    }
}
