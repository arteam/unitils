/*
 * Copyright 2006 the original author or authors.
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

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Map;

/**
 * Test for {@link DataSetXmlReader}.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DataSetXmlReaderTest extends UnitilsJUnit3 {

    /* Test data set xml with the second elment for table A containing less columns */
    private static final String XML_LESS_COLUMNS_LAST =
            "<dataset>" +
                    "   <TABLE_A COLUMN_1=\"1\" COLUMN_2=\"2\" COLUMN_3=\"3\" />" +
                    "   <TABLE_A COLUMN_2=\"4\" />" +
                    "</dataset>";

    /* Test data set xml with the first elment for table A containing less columns */
    private static final String XML_LESS_COLUMNS_FIRST =
            "<dataset>" +
                    "   <TABLE_A COLUMN_2=\"4\" />" +
                    "   <TABLE_A COLUMN_1=\"1\" COLUMN_2=\"2\" COLUMN_3=\"3\" />" +
                    "</dataset>";

    /* Test data set xml containing tables for 3 schemas: default A, B and C */
    private static final String XML_MULTI_SCHEMA =
            "<?xml version='1.0' encoding='UTF-8'?>" +
                    "<dataset xmlns=\"SCHEMA_D\" xmlns:a=\"SCHEMA_A\" xmlns:b=\"SCHEMA_B\" xmlns:c=\"SCHEMA_C\">" +
                    "   <TABLE_A COLUMN_1=\"1\" />" +
                    "   <b:TABLE_A COLUMN_1=\"2\" />" +
                    "   <TABLE_A COLUMN_1=\"3\" />" +
                    "   <b:TABLE_A COLUMN_1=\"4\" />" +
                    "   <TABLE_A COLUMN_2=\"5\" />" +
                    "   <c:TABLE_A COLUMN_1=\"6\" />" +
                    "   <c:TABLE_A COLUMN_1=\"7\" />" +
                    "</dataset>";

    /* Test data set xml containing tables for 3 schemas: default A, B and C, no default namespace defined */
    private static final String XML_MULTI_SCHEMA_NO_DEFAULT =
            "<?xml version='1.0' encoding='UTF-8'?>" +
                    "<dataset xmlns:a=\"SCHEMA_A\" xmlns:b=\"SCHEMA_B\" xmlns:c=\"SCHEMA_C\">" +
                    "   <TABLE_A COLUMN_1=\"1\" />" +
                    "   <b:TABLE_A COLUMN_1=\"2\" />" +
                    "   <TABLE_A COLUMN_1=\"3\" />" +
                    "   <b:TABLE_A COLUMN_1=\"4\" />" +
                    "   <TABLE_A COLUMN_2=\"5\" />" +
                    "   <c:TABLE_A COLUMN_1=\"6\" />" +
                    "   <c:TABLE_A COLUMN_1=\"7\" />" +
                    "</dataset>";

    /* Tested object */
    private DataSetXmlReader dataSetXmlReader;


    /**
     * Creates the test fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();
        dataSetXmlReader = new DataSetXmlReader("SCHEMA_A");
    }


    /**
     * Test the loading of a data set with 2 rows for a table, but the second
     * row has less columns than the first one.
     */
    public void testLoadDataSet_lessColumnsLast() throws Exception {
        Map<String, IDataSet> result = dataSetXmlReader.readDataSetXml(new ByteArrayInputStream(XML_LESS_COLUMNS_LAST.getBytes()));

        // there should be 1 dataset for the default schema A
        assertLenEquals(new String[]{"SCHEMA_A"}, result.keySet());

        // the dataset should contain 2 tables with the same name
        IDataSet dataSet = result.get("SCHEMA_A");
        assertLenEquals(new String[]{"TABLE_A", "TABLE_A"}, dataSet.getTableNames());
        ITableIterator tableIterator = dataSet.iterator();

        // first table TABLE_A row should contain 3 columns
        assertTrue(tableIterator.next());
        ITable table = tableIterator.getTable();
        assertEquals(1, table.getRowCount());
        assertPropertyLenEquals("columnName", Arrays.asList("COLUMN_1", "COLUMN_2", "COLUMN_3"), Arrays.asList(table.getTableMetaData().getColumns()));
        assertEquals("1", table.getValue(0, "COLUMN_1"));
        assertEquals("2", table.getValue(0, "COLUMN_2"));
        assertEquals("3", table.getValue(0, "COLUMN_3"));

        // second table TABLE_A row should contain 1 column
        assertTrue(tableIterator.next());
        table = tableIterator.getTable();
        assertPropertyLenEquals("columnName", Arrays.asList("COLUMN_2"), Arrays.asList(table.getTableMetaData().getColumns()));
        assertEquals(1, table.getRowCount());
        assertEquals("4", table.getValue(0, "COLUMN_2"));
        assertFalse(tableIterator.next());
    }


    /**
     * Test the loading of a data set with 2 rows for a table, but the first
     * row has less columns than the second one.
     */
    public void testLoadDataSet_lessColumnsFirst() throws Exception {
        Map<String, IDataSet> result = dataSetXmlReader.readDataSetXml(new ByteArrayInputStream(XML_LESS_COLUMNS_FIRST.getBytes()));

        // there should be 1 dataset for the default schema A
        assertLenEquals(new String[]{"SCHEMA_A"}, result.keySet());

        // the dataset should contain 2 tables with the same name
        IDataSet dataSet = result.get("SCHEMA_A");
        assertLenEquals(new String[]{"TABLE_A", "TABLE_A"}, dataSet.getTableNames());
        ITableIterator tableIterator = dataSet.iterator();

        // first table TABLE_A row should contain 1 column
        assertTrue(tableIterator.next());
        ITable table = tableIterator.getTable();
        assertPropertyLenEquals("columnName", Arrays.asList("COLUMN_2"), Arrays.asList(table.getTableMetaData().getColumns()));
        assertEquals(1, table.getRowCount());
        assertEquals("4", table.getValue(0, "COLUMN_2"));

        // second table TABLE_A row should contain 3 columns
        assertTrue(tableIterator.next());
        table = tableIterator.getTable();
        assertEquals(1, table.getRowCount());
        assertPropertyLenEquals("columnName", Arrays.asList("COLUMN_1", "COLUMN_2", "COLUMN_3"), Arrays.asList(table.getTableMetaData().getColumns()));
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
        Map<String, IDataSet> result = dataSetXmlReader.readDataSetXml(new ByteArrayInputStream(XML_MULTI_SCHEMA.getBytes()));

        // there should be 3 schemas
        assertLenEquals(new String[]{"SCHEMA_D", "SCHEMA_B", "SCHEMA_C"}, result.keySet());

        // schema D should contain 3 tables
        IDataSet dataSetA = result.get("SCHEMA_D");
        assertLenEquals(new String[]{"TABLE_A", "TABLE_A", "TABLE_A"}, dataSetA.getTableNames());
        ITableIterator tableIterator = dataSetA.iterator();

        // schema B should contain 2 tables
        IDataSet dataSetB = result.get("SCHEMA_B");
        assertLenEquals(new String[]{"TABLE_A", "TABLE_A"}, dataSetB.getTableNames());

        // schema C should contain 2 tables
        IDataSet dataSetC = result.get("SCHEMA_C");
        assertLenEquals(new String[]{"TABLE_A", "TABLE_A"}, dataSetB.getTableNames());
    }


    /**
     * Test the loading of a data set with 3 schemas:
     * schema A (default) contains 3 records for TABLE_A, schema B and C contain 2 records for TABLE_A.
     */
    public void testLoadDataSet_multiSchemaNoDefault() throws Exception {
        Map<String, IDataSet> result = dataSetXmlReader.readDataSetXml(new ByteArrayInputStream(XML_MULTI_SCHEMA_NO_DEFAULT.getBytes()));

        // there should be 3 schemas
        assertLenEquals(new String[]{"SCHEMA_A", "SCHEMA_B", "SCHEMA_C"}, result.keySet());

        // default schema A should contain 3 tables
        IDataSet dataSetA = result.get("SCHEMA_A");
        assertLenEquals(new String[]{"TABLE_A", "TABLE_A", "TABLE_A"}, dataSetA.getTableNames());
        ITableIterator tableIterator = dataSetA.iterator();

        // schema B should contain 2 tables
        IDataSet dataSetB = result.get("SCHEMA_B");
        assertLenEquals(new String[]{"TABLE_A", "TABLE_A"}, dataSetB.getTableNames());

        // schema C should contain 2 tables
        IDataSet dataSetC = result.get("SCHEMA_C");
        assertLenEquals(new String[]{"TABLE_A", "TABLE_A"}, dataSetB.getTableNames());


    }


}
