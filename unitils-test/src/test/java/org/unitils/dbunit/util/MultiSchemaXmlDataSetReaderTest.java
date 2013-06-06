/*
 * Copyright 2013,  Unitils.org
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
import org.junit.Before;
import org.junit.Test;
import org.unitils.dbunit.datasetfactory.MultiSchemaDataSet;
import org.unitils.dbunit.datasetfactory.impl.MultiSchemaXmlDataSetReader;
import org.unitilsnew.UnitilsJUnit4;

import java.io.File;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertPropertyLenientEquals;
import static org.unitils.thirdparty.org.apache.commons.io.FileUtils.toFile;

/**
 * Test for {@link org.unitils.dbunit.datasetfactory.impl.MultiSchemaXmlDataSetReader}.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class MultiSchemaXmlDataSetReaderTest extends UnitilsJUnit4 {

    /* Tested object */
    private MultiSchemaXmlDataSetReader multiSchemaXmlDataSetReader;


    /**
     * Creates the test fixture.
     */
    @Before
    public void setUp() throws Exception {
        multiSchemaXmlDataSetReader = new MultiSchemaXmlDataSetReader("SCHEMA_A");
    }


    /**
     * Test the loading of a data set with 2 rows for a table, but the second
     * row has less columns than the first one.
     */
    @Test
    public void testLoadDataSet_lessColumnsLast() throws Exception {
        File file = toFile(getClass().getResource("LessColumnsLastDataSet.xml"));
        MultiSchemaDataSet result = multiSchemaXmlDataSetReader.readDataSetXml(asList(file));

        // there should be 1 data set for the default schema A
        assertLenientEquals(new String[]{"SCHEMA_A"}, result.getSchemaNames());

        // the data set should contain 2 tables with the same name
        IDataSet dataSet = result.getDataSetForSchema("SCHEMA_A");
        assertLenientEquals(new String[]{"TABLE_A", "TABLE_A"}, dataSet.getTableNames());
        ITableIterator tableIterator = dataSet.iterator();

        // first table TABLE_A row should contain 3 columns
        assertTrue(tableIterator.next());
        ITable table = tableIterator.getTable();
        assertEquals(1, table.getRowCount());
        assertPropertyLenientEquals("columnName", asList("COLUMN_1", "COLUMN_2", "COLUMN_3"), asList(table.getTableMetaData().getColumns()));
        assertEquals("1", table.getValue(0, "COLUMN_1"));
        assertEquals("2", table.getValue(0, "COLUMN_2"));
        assertEquals("3", table.getValue(0, "COLUMN_3"));

        // second table TABLE_A row should contain 1 column
        assertTrue(tableIterator.next());
        table = tableIterator.getTable();
        assertPropertyLenientEquals("columnName", asList("COLUMN_2"), asList(table.getTableMetaData().getColumns()));
        assertEquals(1, table.getRowCount());
        assertEquals("4", table.getValue(0, "COLUMN_2"));
        assertFalse(tableIterator.next());
    }


    /**
     * Test the loading of a data set with 2 rows for a table, but the first
     * row has less columns than the second one.
     */
    @Test
    public void testLoadDataSet_lessColumnsFirst() throws Exception {
        File file = toFile(getClass().getResource("LessColumnsFirstDataSet.xml"));
        MultiSchemaDataSet result = multiSchemaXmlDataSetReader.readDataSetXml(asList(file));

        // there should be 1 data set for the default schema A
        assertLenientEquals(new String[]{"SCHEMA_A"}, result.getSchemaNames());

        // the data set should contain 2 tables with the same name
        IDataSet dataSet = result.getDataSetForSchema("SCHEMA_A");
        assertLenientEquals(new String[]{"TABLE_A", "TABLE_A"}, dataSet.getTableNames());
        ITableIterator tableIterator = dataSet.iterator();

        // first table TABLE_A row should contain 1 column
        assertTrue(tableIterator.next());
        ITable table = tableIterator.getTable();
        assertPropertyLenientEquals("columnName", asList("COLUMN_2"), asList(table.getTableMetaData().getColumns()));
        assertEquals(1, table.getRowCount());
        assertEquals("4", table.getValue(0, "COLUMN_2"));

        // second table TABLE_A row should contain 3 columns
        assertTrue(tableIterator.next());
        table = tableIterator.getTable();
        assertEquals(1, table.getRowCount());
        assertPropertyLenientEquals("columnName", asList("COLUMN_1", "COLUMN_2", "COLUMN_3"), asList(table.getTableMetaData().getColumns()));
        assertEquals("1", table.getValue(0, "COLUMN_1"));
        assertEquals("2", table.getValue(0, "COLUMN_2"));
        assertEquals("3", table.getValue(0, "COLUMN_3"));
        assertFalse(tableIterator.next());
    }


    /**
     * Test the loading of a data set with 3 schemas:
     * schema D (overrides default schema A) contains 3 records for TABLE_A, schema B and C contain 2 records for TABLE_A.
     */
    @Test
    public void testLoadDataSet_multiSchema() throws Exception {
        File file = toFile(getClass().getResource("MultiSchemaDataSet.xml"));
        MultiSchemaDataSet result = multiSchemaXmlDataSetReader.readDataSetXml(asList(file));

        // there should be 3 schemas
        assertLenientEquals(new String[]{"SCHEMA_D", "SCHEMA_B", "SCHEMA_C"}, result.getSchemaNames());

        // schema D should contain 3 tables
        IDataSet dataSetA = result.getDataSetForSchema("SCHEMA_D");
        assertLenientEquals(new String[]{"TABLE_A", "TABLE_A", "TABLE_A"}, dataSetA.getTableNames());

        // schema B should contain 2 tables
        IDataSet dataSetB = result.getDataSetForSchema("SCHEMA_B");
        assertLenientEquals(new String[]{"TABLE_A", "TABLE_A"}, dataSetB.getTableNames());

        // schema C should contain 2 tables
        IDataSet dataSetC = result.getDataSetForSchema("SCHEMA_C");
        assertLenientEquals(new String[]{"TABLE_A", "TABLE_A"}, dataSetC.getTableNames());
    }


    /**
     * Test the loading of a data set with 3 schemas:
     * schema A (default) contains 3 records for TABLE_A, schema B and C contain 2 records for TABLE_A.
     */
    @Test
    public void testLoadDataSet_multiSchemaNoDefault() throws Exception {
        File file = toFile(getClass().getResource("MultiSchemaNoDefaultDataSet.xml"));
        MultiSchemaDataSet result = multiSchemaXmlDataSetReader.readDataSetXml(asList(file));

        // there should be 3 schemas
        assertLenientEquals(new String[]{"SCHEMA_A", "SCHEMA_B", "SCHEMA_C"}, result.getSchemaNames());

        // default schema A should contain 3 tables
        IDataSet dataSetA = result.getDataSetForSchema("SCHEMA_A");
        assertLenientEquals(new String[]{"TABLE_A", "TABLE_A", "TABLE_A"}, dataSetA.getTableNames());

        // schema B should contain 2 tables
        IDataSet dataSetB = result.getDataSetForSchema("SCHEMA_B");
        assertLenientEquals(new String[]{"TABLE_A", "TABLE_A"}, dataSetB.getTableNames());

        // schema C should contain 2 tables
        IDataSet dataSetC = result.getDataSetForSchema("SCHEMA_C");
        assertLenientEquals(new String[]{"TABLE_A", "TABLE_A"}, dataSetC.getTableNames());
    }


    /**
     * Test the loading of a data set out of 2 files:
     * this will load the LessColumnsLastDataSet.xml and  LessColumnsFirstDataSet.xml data set
     */
    @Test
    public void testLoadDataSet_multiInputStreams() throws Exception {
        File file1 = toFile(getClass().getResource("LessColumnsLastDataSet.xml"));
        File file2 = toFile(getClass().getResource("LessColumnsFirstDataSet.xml"));
        MultiSchemaDataSet result = multiSchemaXmlDataSetReader.readDataSetXml(asList(file1, file2));

        // there should be 1 data set for the default schema A
        assertLenientEquals(new String[]{"SCHEMA_A"}, result.getSchemaNames());

        // the data set should contain 4 tables with the same name
        IDataSet dataSet = result.getDataSetForSchema("SCHEMA_A");
        assertLenientEquals(new String[]{"TABLE_A", "TABLE_A", "TABLE_A", "TABLE_A"}, dataSet.getTableNames());
    }
}
