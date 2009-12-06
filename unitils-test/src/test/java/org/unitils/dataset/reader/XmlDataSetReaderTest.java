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
import org.unitils.dataset.core.*;
import org.unitils.dataset.factory.impl.XmlDataSetReader;

import java.io.File;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertPropertyLenientEquals;
import static org.unitils.thirdparty.org.apache.commons.io.FileUtils.toFile;

/**
 * Tests for reading an xml data set
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class XmlDataSetReaderTest extends UnitilsJUnit4 {

    /* Tested object */
    private XmlDataSetReader xmlDataSetReader;


    @Before
    public void setUp() throws Exception {
        xmlDataSetReader = new XmlDataSetReader("SCHEMA_A", false, '=', '$');
    }


    /**
     * Test the loading of a data set with 2 rows for a table, but the second
     * row has less columns than the first one.
     */
    @Test
    public void lessColumnsForLastRow() throws Exception {
        DataSet result = xmlDataSetReader.readDataSetXml(getDataSetFile("LessColumnsLastDataSet.xml"));

        assertLenientEquals(new String[]{"SCHEMA_A"}, result.getSchemaNames());
        Schema schema = result.getSchema("SCHEMA_A");

        assertLenientEquals(new String[]{"TABLE_A"}, schema.getTableNames());
        Table table = schema.getTable("TABLE_A");
        assertEquals(2, table.getNrOfRows());

        assertColumnNames(table.getRow(0), "COLUMN_1", "COLUMN_2", "COLUMN_3");
        assertColumnValues(table.getRow(0), "1", "2", "3");

        assertColumnNames(table.getRow(1), "COLUMN_2");
        assertColumnValues(table.getRow(1), "4");
    }


    /**
     * Test the loading of a data set with 2 rows for a table, but the first
     * row has less columns than the second one.
     */
    @Test
    public void lessColumnsForFirstRow() throws Exception {
        DataSet result = xmlDataSetReader.readDataSetXml(getDataSetFile("LessColumnsFirstDataSet.xml"));

        assertLenientEquals(new String[]{"SCHEMA_A"}, result.getSchemaNames());
        Schema schema = result.getSchema("SCHEMA_A");

        assertLenientEquals(new String[]{"TABLE_A"}, schema.getTableNames());
        Table table = schema.getTable("TABLE_A");
        assertEquals(2, table.getNrOfRows());

        assertColumnNames(table.getRow(0), "COLUMN_2");
        assertColumnValues(table.getRow(0), "4");

        assertColumnNames(table.getRow(1), "COLUMN_1", "COLUMN_2", "COLUMN_3");
        assertColumnValues(table.getRow(1), "1", "2", "3");
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