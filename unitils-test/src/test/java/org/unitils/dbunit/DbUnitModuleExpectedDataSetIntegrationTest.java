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
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.annotation.ExpectedDataSet;
import org.unitils.dbunit.datasetfactory.DataSetFactory;
import org.unitils.dbunit.datasetfactory.MultiSchemaDataSet;
import org.unitils.dbunit.datasetfactory.impl.DbUnitDataSet;
import org.unitils.dbunit.datasetfactory.impl.DbUnitTable;
import org.unitilsnew.UnitilsJUnit4;

import java.io.File;
import java.util.List;

import static java.util.Arrays.asList;
import static org.dbunit.dataset.datatype.DataType.VARCHAR;
import static org.unitils.database.SqlUnitils.executeUpdate;
import static org.unitils.database.SqlUnitils.executeUpdateQuietly;
import static org.unitils.dbunit.DbUnitUnitils.resetDbUnitConnections;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DbUnitModuleExpectedDataSetIntegrationTest extends UnitilsJUnit4 {


    @Before
    public void initialize() throws Exception {
        dropTestTable();
        createTestTable();
        resetDbUnitConnections();
    }

    @After
    public void cleanUp() throws Exception {
        dropTestTable();
    }


    @Test
    @DataSet("DbUnitModuleExpectedDataSetIntegrationTest.defaultDataSet-result.xml")
    @ExpectedDataSet
    public void defaultDataSet() throws Exception {
    }

    @Test
    @DataSet("DbUnitModuleExpectedDataSetIntegrationTest-custom.xml")
    @ExpectedDataSet("DbUnitModuleExpectedDataSetIntegrationTest-custom.xml")
    public void specifiedDataSet() throws Exception {
    }

    @Test
    @DataSet("DbUnitModuleExpectedDataSetIntegrationTest-factory.xml")
    @ExpectedDataSet(value = "DbUnitModuleExpectedDataSetIntegrationTest-custom.xml", factory = CustomDataSetFactory.class)
    public void customFactory() throws Exception {
    }


    private void createTestTable() {
        executeUpdate("create table TEST(dataset varchar(100))");
    }

    private void dropTestTable() {
        executeUpdateQuietly("drop table TEST");
    }

    public static class CustomDataSetFactory implements DataSetFactory {

        public String getDataSetFileExtension() {
            return "xml";
        }

        public MultiSchemaDataSet createDataSet(List<File> dataSetFiles) {
            DbUnitTable table = new DbUnitTable("test");
            table.addColumn(new Column("dataset", VARCHAR));
            table.addRow(asList("custom-factory"));

            DbUnitDataSet dataSet = new DbUnitDataSet();
            dataSet.addTable(table);

            MultiSchemaDataSet multiSchemaDataSet = new MultiSchemaDataSet();
            multiSchemaDataSet.setDataSetForSchema("public", dataSet);
            return multiSchemaDataSet;
        }
    }
}
