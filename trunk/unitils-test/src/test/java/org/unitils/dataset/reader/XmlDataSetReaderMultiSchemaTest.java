/*
 * Copyright 2008,  Unitils.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unitils.dataset.reader;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.dataset.core.Column;
import org.unitils.dataset.core.DataSet;
import org.unitils.dataset.core.Row;
import org.unitils.dataset.core.Schema;
import org.unitils.dataset.factory.impl.XmlDataSetReader;

import java.io.File;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertPropertyLenientEquals;
import static org.unitils.thirdparty.org.apache.commons.io.FileUtils.toFile;

/**
 * Tests for reading an xml data set using multiple schemas
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class XmlDataSetReaderMultiSchemaTest extends UnitilsJUnit4 {

    /* Tested object */
    private XmlDataSetReader xmlDataSetReader;


    @Before
    public void setUp() throws Exception {
        xmlDataSetReader = new XmlDataSetReader("SCHEMA_A", false, '=', '$');
    }


    /**
     * Test the loading of a data set with 3 schemas:
     * schema D (overrides default schema A) contains 3 records for TABLE_A, schema B and C contain 2 records for TABLE_A.
     */
    @Test
    public void multiSchema() throws Exception {
        DataSet result = xmlDataSetReader.readDataSetXml(getDataSetFile("MultiSchemaDataSet.xml"));

        assertLenientEquals(new String[]{"SCHEMA_D", "SCHEMA_B", "SCHEMA_C"}, result.getSchemaNames());

        Schema schemaD = result.getSchema("SCHEMA_D");
        assertLenientEquals(new String[]{"TABLE_A"}, schemaD.getTableNames());
        assertEquals(3, schemaD.getTable("TABLE_A").getNrOfRows());

        Schema schemaB = result.getSchema("SCHEMA_B");
        assertLenientEquals(new String[]{"TABLE_A"}, schemaB.getTableNames());
        assertEquals(2, schemaB.getTable("TABLE_A").getNrOfRows());

        Schema schemaC = result.getSchema("SCHEMA_C");
        assertLenientEquals(new String[]{"TABLE_A"}, schemaC.getTableNames());
        assertEquals(2, schemaC.getTable("TABLE_A").getNrOfRows());
    }

    /**
     * Test the loading of a data set with 3 schemas:
     * schema A (default) contains 3 records for TABLE_A, schema B and C contain 2 records for TABLE_A.
     */
    @Test
    public void noDefaultSchema() throws Exception {
        DataSet result = xmlDataSetReader.readDataSetXml(getDataSetFile("MultiSchemaNoDefaultDataSet.xml"));

        assertLenientEquals(new String[]{"SCHEMA_A", "SCHEMA_B", "SCHEMA_C"}, result.getSchemaNames());

        Schema schemaA = result.getSchema("SCHEMA_A");
        assertLenientEquals(new String[]{"TABLE_A"}, schemaA.getTableNames());
        assertEquals(3, schemaA.getTable("TABLE_A").getNrOfRows());

        Schema schemaB = result.getSchema("SCHEMA_B");
        assertLenientEquals(new String[]{"TABLE_A"}, schemaB.getTableNames());
        assertEquals(2, schemaB.getTable("TABLE_A").getNrOfRows());

        Schema schemaC = result.getSchema("SCHEMA_C");
        assertLenientEquals(new String[]{"TABLE_A"}, schemaC.getTableNames());
        assertEquals(2, schemaC.getTable("TABLE_A").getNrOfRows());
    }


    private void assertColumnNames(Row row, String... values) {
        List<Column> columns = row.getColumns();
        assertPropertyLenientEquals("name", asList(values), columns);
    }

    private void assertColumnValues(Row row, String... values) {
        List<Column> columns = row.getColumns();
        assertPropertyLenientEquals("value", asList(values), columns);
    }

    private File getDataSetFile(String dataSetFileNames) {
        return toFile(getClass().getResource(dataSetFileNames));
    }

}