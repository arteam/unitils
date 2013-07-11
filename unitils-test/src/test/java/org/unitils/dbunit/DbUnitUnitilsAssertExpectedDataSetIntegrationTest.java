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
package org.unitils.dbunit;

import org.dbunit.dataset.Column;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.datasetfactory.DataSetFactory;
import org.unitils.dbunit.datasetfactory.MultiSchemaDataSet;
import org.unitils.dbunit.datasetfactory.impl.DbUnitDataSet;
import org.unitils.dbunit.datasetfactory.impl.DbUnitTable;
import org.unitilsnew.UnitilsJUnit4;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import static java.util.Arrays.asList;
import static org.dbunit.dataset.datatype.DataType.VARCHAR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.unitils.database.SqlUnitils.executeUpdate;
import static org.unitils.database.SqlUnitils.executeUpdateQuietly;
import static org.unitils.dbunit.DbUnitUnitils.assertExpectedDataSet;
import static org.unitils.dbunit.DbUnitUnitils.resetDbUnitConnections;

/**
 * @author Tim Ducheyne
 */
@DataSet
public class DbUnitUnitilsAssertExpectedDataSetIntegrationTest extends UnitilsJUnit4 {


    @Before
    public void initialize() throws Exception {
        dropTestTable();
        createTestTable();
        resetDbUnitConnections();
    }

    @After
    public void tearDown() throws Exception {
        dropTestTable();
    }


    @Test
    public void equalDataSet() throws Exception {
        assertExpectedDataSet(this, "DbUnitUnitilsAssertExpectedDataSetIntegrationTest.xml");
    }

    @Test
    public void equalWithPartialDataSet() throws Exception {
        assertExpectedDataSet(this, "DbUnitUnitilsAssertExpectedDataSetIntegrationTest-partial.xml");
    }

    @Test
    public void exceptionWhenDifferentValues() throws Exception {
        try {
            assertExpectedDataSet(this, "DbUnitUnitilsAssertExpectedDataSetIntegrationTest-differentValues.xml");
            fail("AssertionError expected");
        } catch (AssertionError e) {
            assertEquals("Assertion failed. Differences found between the expected data set and actual database content.\n" +
                    "Found differences for table public.TABLE_A:\n" +
                    "  Different row:\n" +
                    "    pk, column1, column2\n" +
                    "    \"1\", \"value1\", \"xxxx\"\n" +
                    "  Best matching differences:\n" +
                    "    column2: \"xxxx\" <-> \"value2\"\n" +
                    "Found differences for table public.TABLE_B:\n" +
                    "  Different row:\n" +
                    "    column1\n" +
                    "    \"yyyy\"\n" +
                    "  Best matching differences:\n" +
                    "    column1: \"yyyy\" <-> \"aaa\"\n" +
                    "Actual database content:\n" +
                    "  public.TABLE_A\n" +
                    "    PK, COLUMN1, COLUMN2\n" +
                    "    \"1\", \"value1\", \"value2\"\n" +
                    "    \"2\", \"value2\", \"value3\"\n" +
                    "    \"3\", \"\", \"value4\"\n" +
                    "  public.TABLE_B\n" +
                    "    COLUMN1, COLUMN2\n" +
                    "    \"aaa\", null\n" +
                    "    null, \"bbb\"\n", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenMissingRow() throws Exception {
        try {
            assertExpectedDataSet(this, "DbUnitUnitilsAssertExpectedDataSetIntegrationTest-missingRow.xml");
            fail("AssertionError expected");
        } catch (AssertionError e) {
            assertEquals("Assertion failed. Differences found between the expected data set and actual database content.\n" +
                    "Found differences for table public.TABLE_A:\n" +
                    "  Missing row:\n" +
                    "    pk, column1, column2\n" +
                    "    \"xx\", \"value1\", \"value2\"\n" +
                    "Actual database content:\n" +
                    "  public.TABLE_A\n" +
                    "    PK, COLUMN1, COLUMN2\n" +
                    "    \"1\", \"value1\", \"value2\"\n" +
                    "    \"2\", \"value2\", \"value3\"\n" +
                    "    \"3\", \"\", \"value4\"\n", e.getMessage());
        }
    }

    @Test
    public void missingRowBecauseRowWasAlreadyMatchedUsingPrimaryKey() throws Exception {
        try {
            assertExpectedDataSet(this, "DbUnitUnitilsAssertExpectedDataSetIntegrationTest-double.xml");
            fail("AssertionError expected");
        } catch (AssertionError e) {
            assertEquals("Assertion failed. Differences found between the expected data set and actual database content.\n" +
                    "Found differences for table public.TABLE_A:\n" +
                    "  Missing row:\n" +
                    "    pk, column1\n" +
                    "    \"1\", \"value2\"\n" +
                    "Found differences for table public.TABLE_B:\n" +
                    "  Different row:\n" +
                    "    column1\n" +
                    "    \"aaa\"\n" +
                    "  Best matching differences:\n" +
                    "    column1: \"aaa\" <-> null\n" +
                    "Actual database content:\n" +
                    "  public.TABLE_A\n" +
                    "    PK, COLUMN1, COLUMN2\n" +
                    "    \"1\", \"value1\", \"value2\"\n" +
                    "    \"2\", \"value2\", \"value3\"\n" +
                    "    \"3\", \"\", \"value4\"\n" +
                    "  public.TABLE_B\n" +
                    "    COLUMN1, COLUMN2\n" +
                    "    \"aaa\", null\n" +
                    "    null, \"bbb\"\n", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenEmptyTableExpectedButNotEmpty() throws Exception {
        try {
            assertExpectedDataSet(this, "DbUnitUnitilsAssertExpectedDataSetIntegrationTest-emptyTable.xml");
            fail("AssertionError expected");
        } catch (AssertionError e) {
            assertEquals("Assertion failed. Differences found between the expected data set and actual database content.\n" +
                    "Expected table to be empty but found rows for table public.TABLE_A\n" +
                    "Actual database content:\n" +
                    "  public.TABLE_A\n" +
                    "    PK, COLUMN1, COLUMN2\n" +
                    "    \"1\", \"value1\", \"value2\"\n" +
                    "    \"2\", \"value2\", \"value3\"\n" +
                    "    \"3\", \"\", \"value4\"\n", e.getMessage());
        }
    }

    @Test
    public void assertingNullValue() throws Exception {
        assertExpectedDataSet(this, "DbUnitUnitilsAssertExpectedDataSetIntegrationTest-nullValue.xml");
    }

    @Test
    public void exceptionWhenExpectingNullValueButWasNot() throws Exception {
        try {
            assertExpectedDataSet(this, "DbUnitUnitilsAssertExpectedDataSetIntegrationTest-notNullValue.xml");
            fail("AssertionError expected");
        } catch (AssertionError e) {
            assertEquals("Assertion failed. Differences found between the expected data set and actual database content.\n" +
                    "Found differences for table public.TABLE_A:\n" +
                    "  Different row:\n" +
                    "    pk, column1, column2\n" +
                    "    \"2\", null, \"value3\"\n" +
                    "  Best matching differences:\n" +
                    "    column1: null <-> \"value2\"\n" +
                    "Actual database content:\n" +
                    "  public.TABLE_A\n" +
                    "    PK, COLUMN1, COLUMN2\n" +
                    "    \"1\", \"value1\", \"value2\"\n" +
                    "    \"2\", \"value2\", \"value3\"\n" +
                    "    \"3\", \"\", \"value4\"\n", e.getMessage());
        }
    }

    @Test
    public void assertingEmptyValue() throws Exception {
        assertExpectedDataSet(this, "DbUnitUnitilsAssertExpectedDataSetIntegrationTest-emptyValue.xml");
    }

    @Test
    public void exceptionWhenExpectingEmptyValueButWasNot() throws Exception {
        try {
            assertExpectedDataSet(this, "DbUnitUnitilsAssertExpectedDataSetIntegrationTest-notEmptyValue.xml");
            fail("AssertionError expected");
        } catch (AssertionError e) {
            assertEquals("Assertion failed. Differences found between the expected data set and actual database content.\n" +
                    "Found differences for table public.TABLE_A:\n" +
                    "  Different row:\n" +
                    "    pk, column1, column2\n" +
                    "    \"2\", \"\", \"value3\"\n" +
                    "  Best matching differences:\n" +
                    "    column1: \"\" <-> \"value2\"\n" +
                    "Actual database content:\n" +
                    "  public.TABLE_A\n" +
                    "    PK, COLUMN1, COLUMN2\n" +
                    "    \"1\", \"value1\", \"value2\"\n" +
                    "    \"2\", \"value2\", \"value3\"\n" +
                    "    \"3\", \"\", \"value4\"\n", e.getMessage());
        }
    }

    @Test
    public void customFactory() throws Exception {
        assertExpectedDataSet(this, CustomDataSetFactory.class, "DbUnitUnitilsAssertExpectedDataSetIntegrationTest-emptyTable.xml");
    }

    @Test
    public void exceptionWhenFileNotFound() throws Exception {
        try {
            assertExpectedDataSet(this, "xxx");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to resolve data set with name xxx for test class class org.unitils.dbunit.DbUnitUnitilsAssertExpectedDataSetIntegrationTest\n" +
                    "Reason: File with name org/unitils/dbunit/xxx cannot be found.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenInvalidDataSet() throws Exception {
        try {
            assertExpectedDataSet(this, "DbUnitUnitilsAssertExpectedDataSetIntegrationTest-invalid.xml");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to read data set file DbUnitUnitilsAssertExpectedDataSetIntegrationTest-invalid.xml\n" +
                    "Reason: SAXParseException: Content is not allowed in prolog.", e.getMessage());
        }
    }

    @Test
    public void constructionForCoverage() {
        new DbUnitUnitils();
    }

    private void createTestTable() throws SQLException {
        executeUpdate("create table TABLE_A (pk varchar(2) primary key, column1 varchar(10), column2 varchar(10))");
        executeUpdate("create table TABLE_B (column1 varchar(10), column2 varchar(10))");
    }

    private void dropTestTable() throws SQLException {
        executeUpdateQuietly("drop table TABLE_A");
        executeUpdateQuietly("drop table TABLE_B");
    }

    public static class CustomDataSetFactory implements DataSetFactory {

        public String getDataSetFileExtension() {
            return "xml";
        }

        public MultiSchemaDataSet createDataSet(List<File> dataSetFiles) {
            DbUnitTable tableA = new DbUnitTable("table_a");
            tableA.addColumn(new Column("pk", VARCHAR));
            tableA.addColumn(new Column("column1", VARCHAR));
            tableA.addRow(asList("1", "value1"));

            DbUnitDataSet dataSet = new DbUnitDataSet();
            dataSet.addTable(tableA);

            MultiSchemaDataSet multiSchemaDataSet = new MultiSchemaDataSet();
            multiSchemaDataSet.setDataSetForSchema("public", dataSet);
            return multiSchemaDataSet;
        }
    }
}