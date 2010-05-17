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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.dataset.core.DataSetColumn;
import org.unitils.dataset.core.DataSetRow;
import org.unitils.dataset.core.DataSetSettings;
import org.unitils.dataset.factory.impl.XmlDataSetRowSource;

import java.io.File;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertPropertyLenientEquals;
import static org.unitils.thirdparty.org.apache.commons.io.FileUtils.toFile;

/**
 * Tests for reading an xml data set
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class XmlDataSetStaxReaderTest extends UnitilsJUnit4 {

    /* Tested object */
    private XmlDataSetRowSource xmlDataSetStaxReader;


    @Before
    public void setUp() throws Exception {
        DataSetSettings defauDataSetSettings = new DataSetSettings('=', '$', false);
        xmlDataSetStaxReader = new XmlDataSetRowSource();
        xmlDataSetStaxReader.init("SCHEMA_A", defauDataSetSettings);
    }

    @After
    public void cleanUp() throws Exception {
        xmlDataSetStaxReader.close();
    }


    @Test
    public void readTwoRows() throws Exception {
        xmlDataSetStaxReader.open(getDataSetFile("LessColumnsLastDataSet.xml"));

        DataSetRow row1 = xmlDataSetStaxReader.getNextDataSetRow();
        assertEquals("SCHEMA_A", row1.getSchemaName());
        assertEquals("TABLE_A", row1.getTableName());
        assertColumnNames(row1, "COLUMN_1", "COLUMN_2", "COLUMN_3");
        assertColumnValues(row1, "1", "2", "3");

        DataSetRow row2 = xmlDataSetStaxReader.getNextDataSetRow();
        assertEquals("SCHEMA_A", row2.getSchemaName());
        assertEquals("TABLE_A", row2.getTableName());
        assertColumnNames(row2, "COLUMN_2");
        assertColumnValues(row2, "4");

        DataSetRow row3 = xmlDataSetStaxReader.getNextDataSetRow();
        assertNull(row3);
    }

    @Test
    public void lessColumnsForLastRow() throws Exception {
        xmlDataSetStaxReader.open(getDataSetFile("LessColumnsLastDataSet.xml"));

        DataSetRow row1 = xmlDataSetStaxReader.getNextDataSetRow();
        assertColumnNames(row1, "COLUMN_1", "COLUMN_2", "COLUMN_3");
        assertColumnValues(row1, "1", "2", "3");

        DataSetRow row2 = xmlDataSetStaxReader.getNextDataSetRow();
        assertColumnNames(row2, "COLUMN_2");
        assertColumnValues(row2, "4");
    }

    @Test
    public void lessColumnsForFirstRow() throws Exception {
        xmlDataSetStaxReader.open(getDataSetFile("LessColumnsFirstDataSet.xml"));

        DataSetRow row1 = xmlDataSetStaxReader.getNextDataSetRow();
        assertColumnNames(row1, "COLUMN_2");
        assertColumnValues(row1, "4");

        DataSetRow row2 = xmlDataSetStaxReader.getNextDataSetRow();
        assertColumnNames(row2, "COLUMN_1", "COLUMN_2", "COLUMN_3");
        assertColumnValues(row2, "1", "2", "3");
    }

    @Test
    public void parentChild() throws Exception {
        xmlDataSetStaxReader.open(getDataSetFile("ParentChildDataSet.xml"));

        DataSetRow row1 = xmlDataSetStaxReader.getNextDataSetRow();
        assertEquals("TABLE_A", row1.getTableName());
        assertColumnNames(row1, "COLUMN_1");

        DataSetRow row2 = xmlDataSetStaxReader.getNextDataSetRow();
        assertEquals("TABLE_B", row2.getTableName());
        assertColumnNames(row2, "COLUMN_2");
        assertSame(row1, row2.getParentRow());

        DataSetRow row3 = xmlDataSetStaxReader.getNextDataSetRow();
        assertEquals("TABLE_C", row3.getTableName());
        assertColumnNames(row3, "COLUMN_3");
        assertSame(row2, row3.getParentRow());
    }

    @Test
    public void notExists() throws Exception {
        xmlDataSetStaxReader.open(getDataSetFile("NotExistsDataSet.xml"));

        DataSetRow row1 = xmlDataSetStaxReader.getNextDataSetRow();
        assertTrue(row1.isNotExists());
        DataSetRow row2 = xmlDataSetStaxReader.getNextDataSetRow();
        assertTrue(row2.isNotExists());
        DataSetRow row3 = xmlDataSetStaxReader.getNextDataSetRow();
        assertFalse(row3.isNotExists());
    }

    @Test
    public void fullyQualifiedWithNamespacesAndXsdDeclarations() throws Exception {
        xmlDataSetStaxReader.open(getDataSetFile("FullyQualifiedDataSet.xml"));

        DataSetRow row1 = xmlDataSetStaxReader.getNextDataSetRow();
        assertEquals("SCHEMA_A", row1.getSchemaName());
        assertFalse(row1.isNotExists());

        DataSetRow row2 = xmlDataSetStaxReader.getNextDataSetRow();
        assertEquals("SCHEMA_A", row2.getSchemaName());
        assertFalse(row2.isNotExists());

        DataSetRow row3 = xmlDataSetStaxReader.getNextDataSetRow();
        assertEquals("SCHEMA_B", row3.getSchemaName());
        assertFalse(row3.isNotExists());

        DataSetRow row4 = xmlDataSetStaxReader.getNextDataSetRow();
        assertEquals("SCHEMA_A", row4.getSchemaName());
        assertTrue(row4.isNotExists());
    }


    private void assertColumnNames(DataSetRow dataSetRow, String... values) {
        List<DataSetColumn> columns = dataSetRow.getColumns();
        assertPropertyLenientEquals("name", asList(values), columns);
    }

    private void assertColumnValues(DataSetRow dataSetRow, String... values) {
        List<DataSetColumn> columns = dataSetRow.getColumns();
        assertPropertyLenientEquals("value", asList(values), columns);
    }

    private File getDataSetFile(String dataSetFileNames) throws Exception {
        return toFile(getClass().getResource(dataSetFileNames));
    }

}