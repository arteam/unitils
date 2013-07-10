/*
 * Copyright 2012,  Unitils.org
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
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.datasetfactory.DataSetFactory;
import org.unitils.dbunit.datasetfactory.MultiSchemaDataSet;
import org.unitils.dbunit.datasetfactory.impl.DbUnitDataSet;
import org.unitils.dbunit.datasetfactory.impl.DbUnitTable;
import org.unitils.dbunit.datasetloadstrategy.impl.InsertLoadStrategy;
import org.unitils.dbunit.datasetloadstrategy.impl.RefreshLoadStrategy;
import org.unitils.dbunit.datasetloadstrategy.impl.UpdateLoadStrategy;
import org.unitilsnew.UnitilsJUnit4;

import java.io.File;
import java.util.List;

import static java.util.Arrays.asList;
import static org.dbunit.dataset.datatype.DataType.VARCHAR;
import static org.unitils.database.SqlAssert.assertString;
import static org.unitils.database.SqlUnitils.executeUpdate;
import static org.unitils.database.SqlUnitils.executeUpdateQuietly;
import static org.unitils.dbunit.DbUnitUnitils.resetDbUnitConnections;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DbUnitModuleDataSetIntegrationTest extends UnitilsJUnit4 {


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
    @DataSet
    public void defaultDataSet() throws Exception {
        assertString("DbUnitModuleDataSetIntegrationTest.xml", "select dataset from table_a");
    }

    @Test
    @DataSet("DbUnitModuleDataSetIntegrationTest-custom.xml")
    public void specifiedDataSet() throws Exception {
        assertString("DbUnitModuleDataSetIntegrationTest-custom.xml", "select dataset from table_a");
    }

    @Test
    @DataSet(value = "DbUnitModuleDataSetIntegrationTest-update.xml", loadStrategy = UpdateLoadStrategy.class)
    public void updateLoadStrategy() throws Exception {
        assertString("DbUnitModuleDataSetIntegrationTest-update.xml", "select dataset from table_b where pk = 2");
    }

    @Test
    @DataSet(value = "DbUnitModuleDataSetIntegrationTest-refresh.xml", loadStrategy = RefreshLoadStrategy.class)
    public void refreshLoadStrategy() throws Exception {
        assertString("DbUnitModuleDataSetIntegrationTest-refresh-a.xml", "select dataset from table_b where pk = 2");
        assertString("DbUnitModuleDataSetIntegrationTest-refresh-b.xml", "select dataset from table_b where pk = 9");
    }

    @Test
    @DataSet(loadStrategy = InsertLoadStrategy.class)
    public void insertLoadStrategy() throws Exception {
        assertString("DbUnitModuleDataSetIntegrationTest.xml", "select dataset from table_a");
    }

    @Test
    @DataSet(value = "DbUnitModuleDataSetIntegrationTest-update.xml", factory = CustomDataSetFactory.class)
    public void customFactory() throws Exception {
        assertString("custom-factory", "select dataset from table_a");
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
