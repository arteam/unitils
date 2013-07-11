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
package org.unitils.dbunit.datasetfactory.impl;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableIterator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;
import org.unitils.dbunit.datasetfactory.MultiSchemaDataSet;
import org.unitilsnew.UnitilsJUnit4;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;

import static java.util.Arrays.asList;
import static org.dbunit.dataset.ITable.NO_VALUE;
import static org.junit.Assert.*;
import static org.unitils.reflectionassert.ReflectionAssert.*;
import static org.unitils.thirdparty.org.apache.commons.io.FileUtils.toFile;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class MultiSchemaXmlDataSetFactoryCreateDataSetTest extends UnitilsJUnit4 {

    /* Tested object */
    private MultiSchemaXmlDataSetFactory multiSchemaXmlDataSetFactory;


    @Before
    public void initialize() throws Exception {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        saxParserFactory.setNamespaceAware(true);

        multiSchemaXmlDataSetFactory = new MultiSchemaXmlDataSetFactory("SCHEMA_A", saxParserFactory);
    }

    @After
    public void cleanUp() {
        System.clearProperty("javax.xml.parsers.SAXParserFactory");
    }


    @Test
    public void lessColumnsLast() throws Exception {
        File file = toFile(getClass().getResource("LessColumnsLastDataSet.xml"));
        MultiSchemaDataSet result = multiSchemaXmlDataSetFactory.createDataSet(asList(file));

        // there should be 1 data set for the default schema A
        assertLenientEquals(asList("SCHEMA_A"), result.getSchemaNames());

        // the data set should contain 2 tables with the same name
        IDataSet dataSet = result.getDataSetForSchema("SCHEMA_A");
        assertLenientEquals(asList("TABLE_A"), dataSet.getTableNames());
        ITableIterator tableIterator = dataSet.iterator();

        // TABLE_A
        assertTrue(tableIterator.next());
        ITable table = tableIterator.getTable();
        assertEquals(2, table.getRowCount());
        assertPropertyReflectionEquals("columnName", asList("COLUMN_1", "COLUMN_2", "COLUMN_3"), asList(table.getTableMetaData().getColumns()));
        assertEquals("1", table.getValue(0, "COLUMN_1"));
        assertEquals("2", table.getValue(0, "COLUMN_2"));
        assertEquals("3", table.getValue(0, "COLUMN_3"));
        assertEquals(NO_VALUE, table.getValue(1, "COLUMN_1"));
        assertEquals("4", table.getValue(1, "COLUMN_2"));
        assertEquals(NO_VALUE, table.getValue(1, "COLUMN_3"));
        // no more tables
        assertFalse(tableIterator.next());
    }

    @Test
    public void lessColumnsFirst() throws Exception {
        File file = toFile(getClass().getResource("LessColumnsFirstDataSet.xml"));
        MultiSchemaDataSet result = multiSchemaXmlDataSetFactory.createDataSet(asList(file));

        // there should be 1 data set for the default schema A
        assertLenientEquals(new String[]{"SCHEMA_A"}, result.getSchemaNames());

        // the data set should contain 2 tables with the same name
        IDataSet dataSet = result.getDataSetForSchema("SCHEMA_A");
        assertLenientEquals(new String[]{"TABLE_A"}, dataSet.getTableNames());
        ITableIterator tableIterator = dataSet.iterator();

        // TABLE_A
        assertTrue(tableIterator.next());
        ITable table = tableIterator.getTable();
        assertPropertyReflectionEquals("columnName", asList("COLUMN_2", "COLUMN_1", "COLUMN_3"), asList(table.getTableMetaData().getColumns()));
        assertEquals(2, table.getRowCount());
        assertEquals(NO_VALUE, table.getValue(0, "COLUMN_1"));
        assertEquals("4", table.getValue(0, "COLUMN_2"));
        assertEquals(NO_VALUE, table.getValue(0, "COLUMN_3"));
        assertEquals("1", table.getValue(1, "COLUMN_1"));
        assertEquals("2", table.getValue(1, "COLUMN_2"));
        assertEquals("3", table.getValue(1, "COLUMN_3"));
        // no more tables
        assertFalse(tableIterator.next());
    }

    @Test
    public void multiSchema() throws Exception {
        File file = toFile(getClass().getResource("MultiSchemaDataSet.xml"));
        MultiSchemaDataSet result = multiSchemaXmlDataSetFactory.createDataSet(asList(file));

        // there should be 3 schemas
        assertLenientEquals(new String[]{"SCHEMA_D", "SCHEMA_B", "SCHEMA_C"}, result.getSchemaNames());

        // schema D should contain table_a with 3 rows
        IDataSet dataSetA = result.getDataSetForSchema("SCHEMA_D");
        assertLenientEquals(asList("TABLE_A"), dataSetA.getTableNames());
        ITable tableA1 = dataSetA.getTable("TABLE_A");
        assertEquals(3, tableA1.getRowCount());
        assertPropertyReflectionEquals("columnName", asList("COLUMN_1", "COLUMN_2"), asList(tableA1.getTableMetaData().getColumns()));
        assertEquals("1", tableA1.getValue(0, "COLUMN_1"));
        assertEquals(NO_VALUE, tableA1.getValue(0, "COLUMN_2"));
        assertEquals("3", tableA1.getValue(1, "COLUMN_1"));
        assertEquals(NO_VALUE, tableA1.getValue(1, "COLUMN_2"));
        assertEquals(NO_VALUE, tableA1.getValue(2, "COLUMN_1"));
        assertEquals("5", tableA1.getValue(2, "COLUMN_2"));

        // schema B should contain table_a and table_b
        IDataSet dataSetB = result.getDataSetForSchema("SCHEMA_B");
        assertLenientEquals(asList("TABLE_A", "TABLE_B"), dataSetB.getTableNames());
        ITable tableA2 = dataSetB.getTable("TABLE_A");
        assertEquals(1, tableA2.getRowCount());
        assertPropertyReflectionEquals("columnName", asList("COLUMN_1"), asList(tableA2.getTableMetaData().getColumns()));
        assertEquals("2", tableA2.getValue(0, "COLUMN_1"));
        ITable tableB = dataSetB.getTable("TABLE_B");
        assertEquals(1, tableB.getRowCount());
        assertPropertyReflectionEquals("columnName", asList("COLUMN_1"), asList(tableB.getTableMetaData().getColumns()));
        assertEquals("4", tableB.getValue(0, "COLUMN_1"));

        // schema C should contain table_a
        IDataSet dataSetC = result.getDataSetForSchema("SCHEMA_C");
        assertLenientEquals(asList("TABLE_A"), dataSetC.getTableNames());
        ITable tableA3 = dataSetC.getTable("TABLE_A");
        assertEquals(2, tableA3.getRowCount());
        assertPropertyLenientEquals("columnName", asList("COLUMN_1"), asList(tableA3.getTableMetaData().getColumns()));
        assertEquals("6", tableA3.getValue(0, "COLUMN_1"));
        assertEquals("7", tableA3.getValue(1, "COLUMN_1"));
    }

    @Test
    public void multiSchemaNoDefault() throws Exception {
        File file = toFile(getClass().getResource("MultiSchemaNoDefaultDataSet.xml"));
        MultiSchemaDataSet result = multiSchemaXmlDataSetFactory.createDataSet(asList(file));

        // there should be 3 schemas
        assertLenientEquals(asList("SCHEMA_A", "SCHEMA_B", "SCHEMA_C"), result.getSchemaNames());

        // default schema A should contain 3 tables
        IDataSet dataSetA = result.getDataSetForSchema("SCHEMA_A");
        assertReflectionEquals(asList("TABLE_A", "TABLE_B", "TABLE_C"), dataSetA.getTableNames());

        // schema B should contain 2 tables
        IDataSet dataSetB = result.getDataSetForSchema("SCHEMA_B");
        assertReflectionEquals(asList("TABLE_A", "TABLE_B"), dataSetB.getTableNames());

        // schema C should contain 2 tables
        IDataSet dataSetC = result.getDataSetForSchema("SCHEMA_C");
        assertReflectionEquals(asList("TABLE_A", "TABLE_B"), dataSetC.getTableNames());
    }

    @Test
    public void ignoreRowsWithoutColumns() throws Exception {
        File file = toFile(getClass().getResource("RowsWithoutColumnsDataSet.xml"));
        MultiSchemaDataSet result = multiSchemaXmlDataSetFactory.createDataSet(asList(file));

        IDataSet dataSet = result.getDataSetForSchema("SCHEMA_A");
        assertReflectionEquals(asList("TABLE_A", "TABLE_B"), dataSet.getTableNames());

        ITable tableA = dataSet.getTable("TABLE_A");
        assertEquals(0, tableA.getRowCount());

        ITable tableB = dataSet.getTable("TABLE_B");
        assertEquals(1, tableB.getRowCount());
    }

    @Test
    public void multipleFiles() throws Exception {
        File file1 = toFile(getClass().getResource("LessColumnsLastDataSet.xml"));
        File file2 = toFile(getClass().getResource("LessColumnsFirstDataSet.xml"));
        MultiSchemaDataSet result = multiSchemaXmlDataSetFactory.createDataSet(asList(file1, file2));

        // there should be 1 data set for the default schema A
        assertLenientEquals(asList("SCHEMA_A"), result.getSchemaNames());

        // the data set should contain 4 tables with the same name
        IDataSet dataSet = result.getDataSetForSchema("SCHEMA_A");
        assertReflectionEquals(asList("TABLE_A"), dataSet.getTableNames());
    }

    @Test
    public void exceptionWhenFileNotFound() throws Exception {
        File file = new File("xxx");
        try {
            multiSchemaXmlDataSetFactory.createDataSet(asList(file));
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertTrue(e.getMessage().contains("Unable to read data set file xxx\n" +
                    "Reason: FileNotFoundException: xxx"));
        }
    }

    @Test
    public void exceptionWhenUnableParseXml() throws Exception {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        saxParserFactory.setValidating(true);      // this will trigger the error
        multiSchemaXmlDataSetFactory = new MultiSchemaXmlDataSetFactory("SCHEMA_A", saxParserFactory);

        File file = toFile(getClass().getResource("MultiSchemaDataSet.xml"));
        try {
            multiSchemaXmlDataSetFactory.createDataSet(asList(file));
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to read data set file MultiSchemaDataSet.xml\n" +
                    "Reason: SAXParseException: Document root element \"dataset\", must match DOCTYPE root \"null\".", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenUnableToCreateParser() throws Exception {
        System.setProperty("javax.xml.parsers.SAXParserFactory", MockSAXParserFactory.class.getName());
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        multiSchemaXmlDataSetFactory = new MultiSchemaXmlDataSetFactory("SCHEMA_A", saxParserFactory);

        try {
            multiSchemaXmlDataSetFactory.createDataSet(null);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to create SAX parser to read data set xml.\n" +
                    "Reason: ParserConfigurationException: expected", e.getMessage());
        }
    }


    public static class MockSAXParserFactory extends SAXParserFactory {

        @Override
        public SAXParser newSAXParser() throws ParserConfigurationException, SAXException {
            throw new ParserConfigurationException("expected");
        }

        @Override
        public void setFeature(String name, boolean value) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException {
        }

        @Override
        public boolean getFeature(String name) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException {
            return false;
        }
    }
}