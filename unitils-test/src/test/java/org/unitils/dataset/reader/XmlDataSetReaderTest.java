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
import static org.junit.Assert.*;
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

    @Test
    public void parentChild() throws Exception {
        DataSet result = xmlDataSetReader.readDataSetXml(getDataSetFile("ParentChildDataSet.xml"));

        Schema schema = result.getSchema("SCHEMA_A");
        assertLenientEquals(new String[]{"TABLE_A", "TABLE_B", "TABLE_C"}, schema.getTableNames());

        Table tableA = schema.getTable("TABLE_A");
        Row rowA = tableA.getRow(0);
        assertColumnNames(rowA, "COLUMN_1");

        Table tableB = schema.getTable("TABLE_B");
        Row rowB = tableB.getRow(0);
        assertColumnNames(rowB, "COLUMN_2");
        assertSame(rowA, rowB.getParentRow());

        Table tableC = schema.getTable("TABLE_C");
        Row rowC = tableC.getRow(0);
        assertColumnNames(rowC, "COLUMN_3");
        assertSame(rowB, rowC.getParentRow());
    }

    @Test
    public void notExists() throws Exception {
        DataSet result = xmlDataSetReader.readDataSetXml(getDataSetFile("NotExistsDataSet.xml"));

        assertLenientEquals(new String[]{"SCHEMA_A"}, result.getSchemaNames());
        Schema schema = result.getSchema("SCHEMA_A");

        assertLenientEquals(new String[]{"TABLE_A"}, schema.getTableNames());
        Table table = schema.getTable("TABLE_A");
        assertEquals(3, table.getNrOfRows());

        assertTrue(table.getRow(0).isNotExists());
        assertTrue(table.getRow(1).isNotExists());
        assertFalse(table.getRow(2).isNotExists());
    }

    @Test
    public void fullyQualifiedWithNamespacesAndXsdDeclarations() throws Exception {
        DataSet result = xmlDataSetReader.readDataSetXml(getDataSetFile("FullyQualifiedDataSet.xml"));

        assertLenientEquals(new String[]{"SCHEMA_A", "SCHEMA_B"}, result.getSchemaNames());

        Schema schemaA = result.getSchema("SCHEMA_A");
        Table table1 = schemaA.getTable("TABLE_A");
        assertEquals(3, table1.getNrOfRows());
        assertFalse(table1.getRow(0).isNotExists());
        assertFalse(table1.getRow(1).isNotExists());
        assertTrue(table1.getRow(2).isNotExists());

        Schema schemaB = result.getSchema("SCHEMA_B");
        Table table2 = schemaB.getTable("TABLE_A");
        assertEquals(1, table2.getNrOfRows());
        assertFalse(table2.getRow(0).isNotExists());
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