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
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import org.unitils.dbunit.datasetfactory.DataSetFactory;
import org.unitils.dbunit.datasetfactory.MultiSchemaDataSet;
import org.unitils.dbunit.datasetfactory.impl.DbUnitDataSet;
import org.unitils.dbunit.datasetfactory.impl.DbUnitTable;
import org.unitils.dbunit.datasetloadstrategy.impl.InsertLoadStrategy;
import org.unitils.dbunit.datasetloadstrategy.impl.RefreshLoadStrategy;
import org.unitils.dbunit.datasetloadstrategy.impl.UpdateLoadStrategy;

import java.io.File;
import java.util.List;

import static java.util.Arrays.asList;
import static org.dbunit.dataset.datatype.DataType.VARCHAR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.unitils.database.SqlAssert.assertString;
import static org.unitils.database.SqlAssert.assertStringList;
import static org.unitils.database.SqlUnitils.executeUpdate;
import static org.unitils.database.SqlUnitils.executeUpdateQuietly;
import static org.unitils.dbunit.DbUnitUnitils.resetDbUnitConnections;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DbUnitUnitilsInsertDataSetIntegrationTest extends UnitilsJUnit4 {


    @Before
    public void initialize() throws Exception {
        dropTestTable();
        createTestTables();
        resetDbUnitConnections();
    }

    @After
    public void cleanUp() throws Exception {
        dropTestTable();
    }


    @Test
    public void defaultDataSet() throws Exception {
        DbUnitUnitils.insertDataSet(this);
        assertString("DbUnitUnitilsInsertDataSetIntegrationTest.xml", "select dataset from table_a");
    }

    @Test
    public void usingTestClass() throws Exception {
        DbUnitUnitils.insertDataSet(DbUnitUnitilsInsertDataSetIntegrationTest.class);
        assertString("DbUnitUnitilsInsertDataSetIntegrationTest.xml", "select dataset from table_a");
    }

    @Test
    public void specifiedDataSet() throws Exception {
        DbUnitUnitils.insertDataSet(this, "DbUnitUnitilsInsertDataSetIntegrationTest-custom.xml");
        assertString("DbUnitUnitilsInsertDataSetIntegrationTest-custom.xml", "select dataset from table_a");
    }

    @Test
    public void multipleSpecifiedDataSets() throws Exception {
        DbUnitUnitils.insertDataSet(this, "DbUnitUnitilsInsertDataSetIntegrationTest.xml", "DbUnitUnitilsInsertDataSetIntegrationTest-custom.xml");
        assertStringList(asList("DbUnitUnitilsInsertDataSetIntegrationTest.xml", "DbUnitUnitilsInsertDataSetIntegrationTest-custom.xml"), "select dataset from table_a");
    }

    @Test
    public void updateLoadStrategy() throws Exception {
        DbUnitUnitils.insertDataSet(this, UpdateLoadStrategy.class, null, "DbUnitUnitilsInsertDataSetIntegrationTest-update.xml");
        assertString("DbUnitUnitilsInsertDataSetIntegrationTest-update.xml", "select dataset from table_b where pk = 2");
    }

    @Test
    public void refreshLoadStrategy() throws Exception {
        DbUnitUnitils.insertDataSet(this, RefreshLoadStrategy.class, null, "DbUnitUnitilsInsertDataSetIntegrationTest-refresh.xml");
        assertString("DbUnitUnitilsInsertDataSetIntegrationTest-refresh-a.xml", "select dataset from table_b where pk = 2");
        assertString("DbUnitUnitilsInsertDataSetIntegrationTest-refresh-b.xml", "select dataset from table_b where pk = 9");
    }

    @Test
    public void insertLoadStrategy() throws Exception {
        DbUnitUnitils.insertDataSet(this, InsertLoadStrategy.class, null);
        assertString("DbUnitUnitilsInsertDataSetIntegrationTest.xml", "select dataset from table_a");
    }

    @Test
    public void customFactory() throws Exception {
        DbUnitUnitils.insertDataSet(this, null, CustomDataSetFactory.class, "DbUnitUnitilsInsertDataSetIntegrationTest.xml");
        assertString("custom-factory", "select dataset from table_a");
    }

    @Test
    public void exceptionWhenFileNotFound() throws Exception {
        try {
            DbUnitUnitils.insertDataSet(this, "xxx");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to resolve data set with name xxx for test class class org.unitils.dbunit.DbUnitUnitilsInsertDataSetIntegrationTest\n" +
                    "Reason: File with name org/unitils/dbunit/xxx cannot be found.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenInvalidDataSet() throws Exception {
        try {
            DbUnitUnitils.insertDataSet(this, "DbUnitUnitilsInsertDataSetIntegrationTest-invalid.xml");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to read data set file DbUnitUnitilsInsertDataSetIntegrationTest-invalid.xml\n" +
                    "Reason: SAXParseException: Content is not allowed in prolog.", e.getMessage());
        }
    }

    @Test
    public void nullValueInDataSet() throws Exception {
        DbUnitUnitils.insertDataSet(this, "DbUnitUnitilsInsertDataSetIntegrationTest-null.xml");
        assertString(null, "select dataset from table_b where pk = 1");
    }


    private void createTestTables() {
        executeUpdate("create table table_a (dataset varchar(100), column2 varchar(100))");
        executeUpdate("create table table_b (pk integer primary key, dataset varchar(100))");
        executeUpdate("insert into table_b (pk, dataset) values (1, '111')");
        executeUpdate("insert into table_b (pk, dataset) values (2, '222')");
        executeUpdate("insert into table_b (pk, dataset) values (3, '333')");
    }

    private void dropTestTable() {
        executeUpdateQuietly("drop table table_a");
        executeUpdateQuietly("drop table table_b");
    }

    public static class CustomDataSetFactory implements DataSetFactory {

        public String getDataSetFileExtension() {
            return "xml";
        }

        public MultiSchemaDataSet createDataSet(List<File> dataSetFiles) {
            DbUnitTable tableA = new DbUnitTable("table_a");
            tableA.addColumn(new Column("dataset", VARCHAR));
            tableA.addRow(asList("custom-factory"));

            DbUnitDataSet dataSet = new DbUnitDataSet();
            dataSet.addTable(tableA);

            MultiSchemaDataSet multiSchemaDataSet = new MultiSchemaDataSet();
            multiSchemaDataSet.setDataSetForSchema("public", dataSet);
            return multiSchemaDataSet;
        }
    }
}
