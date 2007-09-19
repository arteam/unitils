/*
 * Copyright 2006-2007,  Unitils.org
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
package org.unitils.dbunit.util;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableIterator;
import org.unitils.UnitilsJUnit3;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertPropertyLenEquals;
import static org.unitils.thirdparty.org.apache.commons.io.FileUtils.toFile;

import java.io.File;
import static java.util.Arrays.asList;

/**
 * Test for {@link MultiSchemaXmlDataSetReader}.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class MultiSchemaXmlDataSetReaderTest extends UnitilsJUnit3 {

    /* Tested object */
    private MultiSchemaXmlDataSetReader multiSchemaXmlDataSetReader;


    /**
     * Creates the test fixture.
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        multiSchemaXmlDataSetReader = new MultiSchemaXmlDataSetReader("SCHEMA_A");
    }


    /**
     * Test the loading of a data set with 2 rows for a table, but the second
     * row has less columns than the first one.
     */
    public void testLoadDataSet_lessColumnsLast() throws Exception {
        MultiSchemaDataSet result = multiSchemaXmlDataSetReader.readDataSetXml(toFile(getClass().getResource("LessColumnsLastDataSet.xml")));

        // there should be 1 dataset for the default schema A
        assertLenEquals(new String[]{"SCHEMA_A"}, result.getSchemaNames());

        // the dataset should contain 2 tables with the same name
        IDataSet dataSet = result.getDataSetForSchema("SCHEMA_A");
        assertLenEquals(new String[]{"TABLE_A", "TABLE_A"}, dataSet.getTableNames());
        ITableIterator tableIterator = dataSet.iterator();

        // first table TABLE_A row should contain 3 columns
        assertTrue(tableIterator.next());
        ITable table = tableIterator.getTable();
        assertEquals(1, table.getRowCount());
        assertPropertyLenEquals("columnName", asList("COLUMN_1", "COLUMN_2", "COLUMN_3"), asList(table.getTableMetaData().getColumns()));
        assertEquals("1", table.getValue(0, "COLUMN_1"));
        assertEquals("2", table.getValue(0, "COLUMN_2"));
        assertEquals("3", table.getValue(0, "COLUMN_3"));

        // second table TABLE_A row should contain 1 column
        assertTrue(tableIterator.next());
        table = tableIterator.getTable();
        assertPropertyLenEquals("columnName", asList("COLUMN_2"), asList(table.getTableMetaData().getColumns()));
        assertEquals(1, table.getRowCount());
        assertEquals("4", table.getValue(0, "COLUMN_2"));
        assertFalse(tableIterator.next());
    }


    /**
     * Test the loading of a data set with 2 rows for a table, but the first
     * row has less columns than the second one.
     */
    public void testLoadDataSet_lessColumnsFirst() throws Exception {
        MultiSchemaDataSet result = multiSchemaXmlDataSetReader.readDataSetXml(toFile(getClass().getResource("LessColumnsFirstDataSet.xml")));

        // there should be 1 dataset for the default schema A
        assertLenEquals(new String[]{"SCHEMA_A"}, result.getSchemaNames());

        // the dataset should contain 2 tables with the same name
        IDataSet dataSet = result.getDataSetForSchema("SCHEMA_A");
        assertLenEquals(new String[]{"TABLE_A", "TABLE_A"}, dataSet.getTableNames());
        ITableIterator tableIterator = dataSet.iterator();

        // first table TABLE_A row should contain 1 column
        assertTrue(tableIterator.next());
        ITable table = tableIterator.getTable();
        assertPropertyLenEquals("columnName", asList("COLUMN_2"), asList(table.getTableMetaData().getColumns()));
        assertEquals(1, table.getRowCount());
        assertEquals("4", table.getValue(0, "COLUMN_2"));

        // second table TABLE_A row should contain 3 columns
        assertTrue(tableIterator.next());
        table = tableIterator.getTable();
        assertEquals(1, table.getRowCount());
        assertPropertyLenEquals("columnName", asList("COLUMN_1", "COLUMN_2", "COLUMN_3"), asList(table.getTableMetaData().getColumns()));
        assertEquals("1", table.getValue(0, "COLUMN_1"));
        assertEquals("2", table.getValue(0, "COLUMN_2"));
        assertEquals("3", table.getValue(0, "COLUMN_3"));
        assertFalse(tableIterator.next());
    }


    /**
     * Test the loading of a data set with 3 schemas:
     * schema D (overrides default schema A) contains 3 records for TABLE_A, schema B and C contain 2 records for TABLE_A.
     */
    public void testLoadDataSet_multiSchema() throws Exception {
        MultiSchemaDataSet result = multiSchemaXmlDataSetReader.readDataSetXml(toFile(getClass().getResource("MultiSchemaDataSet.xml")));

        // there should be 3 schemas
        assertLenEquals(new String[]{"SCHEMA_D", "SCHEMA_B", "SCHEMA_C"}, result.getSchemaNames());

        // schema D should contain 3 tables
        IDataSet dataSetA = result.getDataSetForSchema("SCHEMA_D");
        assertLenEquals(new String[]{"TABLE_A", "TABLE_A", "TABLE_A"}, dataSetA.getTableNames());

        // schema B should contain 2 tables
        IDataSet dataSetB = result.getDataSetForSchema("SCHEMA_B");
        assertLenEquals(new String[]{"TABLE_A", "TABLE_A"}, dataSetB.getTableNames());

        // schema C should contain 2 tables
        IDataSet dataSetC = result.getDataSetForSchema("SCHEMA_C");
        assertLenEquals(new String[]{"TABLE_A", "TABLE_A"}, dataSetC.getTableNames());
    }


    /**
     * Test the loading of a data set with 3 schemas:
     * schema A (default) contains 3 records for TABLE_A, schema B and C contain 2 records for TABLE_A.
     */
    public void testLoadDataSet_multiSchemaNoDefault() throws Exception {
        MultiSchemaDataSet result = multiSchemaXmlDataSetReader.readDataSetXml(toFile(getClass().getResource("MultiSchemaNoDefaultDataSet.xml")));

        // there should be 3 schemas
        assertLenEquals(new String[]{"SCHEMA_A", "SCHEMA_B", "SCHEMA_C"}, result.getSchemaNames());

        // default schema A should contain 3 tables
        IDataSet dataSetA = result.getDataSetForSchema("SCHEMA_A");
        assertLenEquals(new String[]{"TABLE_A", "TABLE_A", "TABLE_A"}, dataSetA.getTableNames());

        // schema B should contain 2 tables
        IDataSet dataSetB = result.getDataSetForSchema("SCHEMA_B");
        assertLenEquals(new String[]{"TABLE_A", "TABLE_A"}, dataSetB.getTableNames());

        // schema C should contain 2 tables
        IDataSet dataSetC = result.getDataSetForSchema("SCHEMA_C");
        assertLenEquals(new String[]{"TABLE_A", "TABLE_A"}, dataSetC.getTableNames());
    }


    /**
     * Test the loading of a data set out of 2 files:
     * this will load the LessColumnsLastDataSet.xml and  LessColumnsFirstDataSet.xml dataset
     */
    public void testLoadDataSet_multiInputStreams() throws Exception {
        File file1 = toFile(getClass().getResource("LessColumnsLastDataSet.xml"));
        File file2 = toFile(getClass().getResource("LessColumnsFirstDataSet.xml"));
        MultiSchemaDataSet result = multiSchemaXmlDataSetReader.readDataSetXml(file1, file2);

        // there should be 1 dataset for the default schema A
        assertLenEquals(new String[]{"SCHEMA_A"}, result.getSchemaNames());

        // the dataset should contain 4 tables with the same name
        IDataSet dataSet = result.getDataSetForSchema("SCHEMA_A");
        assertLenEquals(new String[]{"TABLE_A", "TABLE_A", "TABLE_A", "TABLE_A"}, dataSet.getTableNames());
    }
}
