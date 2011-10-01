/*
 * Copyright Unitils.org
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
import org.unitils.core.UnitilsException;
import org.unitils.dataset.model.dataset.DataSetRow;
import org.unitils.dataset.model.dataset.DataSetSettings;
import org.unitils.dataset.model.dataset.DataSetValue;
import org.unitils.dataset.rowsource.impl.InlineDataSetRowSource;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertPropertyLenientEquals;

/**
 * Tests for reading an xml data set
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class InlineDataSetRowSourceTest extends UnitilsJUnit4 {

    /* Tested object */
    private InlineDataSetRowSource inlineDataSetRowSource;

    private DataSetSettings defaultDataSetSettings;

    @Before
    public void setUp() throws Exception {
        defaultDataSetSettings = new DataSetSettings('=', '$', false);
    }

    @After
    public void cleanUp() throws Exception {
        inlineDataSetRowSource.close();
    }


    @Test
    public void getNextDataSetRow() throws Exception {
        List<String> dataSet = asList("SCHEMA_A.TABLE_A COL1=test, col2=5  , col3  =   spaces  ");
        inlineDataSetRowSource = new InlineDataSetRowSource(dataSet, defaultDataSetSettings);
        inlineDataSetRowSource.open();

        DataSetRow row1 = inlineDataSetRowSource.getNextDataSetRow();
        assertEquals("SCHEMA_A", row1.getSchemaName());
        assertEquals("TABLE_A", row1.getTableName());
        assertColumnNames(row1, "COL1", "col2", "col3");
        assertColumnValues(row1, "test", "5  ", "   spaces  ");
    }

    @Test
    public void quotedValues() throws Exception {
        List<String> dataSet = asList("SCHEMA_A.TABLE_A COL1='test', col2='5  ', col3  ='  ,spaces  '  ");
        inlineDataSetRowSource = new InlineDataSetRowSource(dataSet, defaultDataSetSettings);
        inlineDataSetRowSource.open();

        DataSetRow row1 = inlineDataSetRowSource.getNextDataSetRow();
        assertEquals("SCHEMA_A", row1.getSchemaName());
        assertEquals("TABLE_A", row1.getTableName());
        assertColumnNames(row1, "COL1", "col2", "col3");
        assertColumnValues(row1, "test", "5  ", "  ,spaces  ");
    }

    @Test(expected = UnitilsException.class)
    public void invalidTextAfterFinalQuotes() throws Exception {
        List<String> dataSet = asList("SCHEMA_A.TABLE_A COL1='test' xxx");
        inlineDataSetRowSource = new InlineDataSetRowSource(dataSet, defaultDataSetSettings);
        inlineDataSetRowSource.open();
        inlineDataSetRowSource.getNextDataSetRow();
    }

    @Test
    public void twoRows() throws Exception {
        List<String> dataSet = asList("schema1.table1 column1=1", "schema2.table2 column2=2");
        inlineDataSetRowSource = new InlineDataSetRowSource(dataSet, defaultDataSetSettings);
        inlineDataSetRowSource.open();

        DataSetRow row1 = inlineDataSetRowSource.getNextDataSetRow();
        assertEquals("schema1", row1.getSchemaName());
        assertEquals("table1", row1.getTableName());
        assertColumnNames(row1, "column1");
        assertColumnValues(row1, "1");

        DataSetRow row2 = inlineDataSetRowSource.getNextDataSetRow();
        assertEquals("schema2", row2.getSchemaName());
        assertEquals("table2", row2.getTableName());
        assertColumnNames(row2, "column2");
        assertColumnValues(row2, "2");

        DataSetRow row3 = inlineDataSetRowSource.getNextDataSetRow();
        assertNull(row3);
    }

    @Test
    public void defaultSchemaName() throws Exception {
        List<String> dataSet = asList("table column=1");
        inlineDataSetRowSource = new InlineDataSetRowSource(dataSet, defaultDataSetSettings);
        inlineDataSetRowSource.open();

        DataSetRow row = inlineDataSetRowSource.getNextDataSetRow();
        assertNull(row.getSchemaName());
        assertEquals("table", row.getTableName());
    }

    @Test
    public void escapedCommas() throws Exception {
        List<String> dataSet = asList("table col1=,,,col2=,,,,");
        inlineDataSetRowSource = new InlineDataSetRowSource(dataSet, defaultDataSetSettings);
        inlineDataSetRowSource.open();

        DataSetRow row = inlineDataSetRowSource.getNextDataSetRow();
        assertColumnNames(row, "col1", "col2");
        assertColumnValues(row, ",", ",,");
    }

    @Test
    public void escapedQuotes() throws Exception {
        List<String> dataSet = asList("table col1='''', col2='', col3=''''''");
        inlineDataSetRowSource = new InlineDataSetRowSource(dataSet, defaultDataSetSettings);
        inlineDataSetRowSource.open();

        DataSetRow row = inlineDataSetRowSource.getNextDataSetRow();
        assertColumnNames(row, "col1", "col2", "col3");
        assertColumnValues(row, "'", "", "''");
    }

    @Test
    public void spaces() throws Exception {
        List<String> dataSet = asList("table col1=    'a '' value'   , col2  = d , col3 =  , col4=,col5 = 'a'   ");
        inlineDataSetRowSource = new InlineDataSetRowSource(dataSet, defaultDataSetSettings);
        inlineDataSetRowSource.open();

        DataSetRow row = inlineDataSetRowSource.getNextDataSetRow();
        assertColumnNames(row, "col1", "col2", "col3", "col4", "col5");
        assertColumnValues(row, "a ' value", " d ", "  ", "", "a");
    }

    @Test(expected = UnitilsException.class)
    public void quotesNotClosed() throws Exception {
        List<String> dataSet = asList("table col1=' , col2=d");
        inlineDataSetRowSource = new InlineDataSetRowSource(dataSet, defaultDataSetSettings);
        inlineDataSetRowSource.open();
        inlineDataSetRowSource.getNextDataSetRow();
    }

    @Test
    public void notExists() throws Exception {
        List<String> dataSet = asList("!table col1=a");
        inlineDataSetRowSource = new InlineDataSetRowSource(dataSet, defaultDataSetSettings);
        inlineDataSetRowSource.open();

        DataSetRow row = inlineDataSetRowSource.getNextDataSetRow();
        assertTrue(row.isNotExists());
        assertEquals("table", row.getTableName());
        assertColumnNames(row, "col1");
        assertColumnValues(row, "a");
    }

    @Test
    public void notExistsWithSpaces() throws Exception {
        List<String> dataSet = asList(" ! table col1=a");
        inlineDataSetRowSource = new InlineDataSetRowSource(dataSet, defaultDataSetSettings);
        inlineDataSetRowSource.open();

        DataSetRow row = inlineDataSetRowSource.getNextDataSetRow();
        assertTrue(row.isNotExists());
        assertEquals("table", row.getTableName());
        assertColumnNames(row, "col1");
        assertColumnValues(row, "a");
    }


    private void assertColumnNames(DataSetRow dataSetRow, String... values) {
        List<DataSetValue> columns = dataSetRow.getColumns();
        assertPropertyLenientEquals("columnName", asList(values), columns);
    }

    private void assertColumnValues(DataSetRow dataSetRow, String... values) {
        List<DataSetValue> columns = dataSetRow.getColumns();
        assertPropertyLenientEquals("value", asList(values), columns);
    }

}