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
package org.unitils.dataset.loader.impl;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Tests for creating loading data sets using insert statements and first deleting all data from the tables.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class CleanInsertDataSetLoaderTest extends DataSetLoaderTestBase {

    /* Tested object */
    private CleanInsertDataSetLoader dataSetLoader = new CleanInsertDataSetLoader();


    @Before
    public void initialize() throws Exception {
        initializeDataSetLoader(dataSetLoader);
        initializePrimaryKeys("column_1", "column_3", "column_5");
    }


    @Test
    public void insertDataSet() throws Exception {
        dataSetLoader.load(dataSet, new ArrayList<String>());

        connection.assertInvoked().prepareStatement("delete from my_schema.table_b");
        connection.assertInvoked().prepareStatement("delete from my_schema.table_a");
        connection.assertInvoked().prepareStatement("insert into my_schema.table_a (column_1,column_2) values (?,?)");
        connection.assertInvoked().prepareStatement("insert into my_schema.table_a (column_3,column_4) values (?,?)");
        connection.assertInvoked().prepareStatement("insert into my_schema.table_b (column_5,column_6) values (?,?)");
    }

    @Test
    public void insertDataSetWithLiteralValues() throws Exception {
        dataSetLoader.load(dataSetWithLiteralValues, new ArrayList<String>());
        connection.assertInvoked().prepareStatement("delete from my_schema.table_a");
        connection.assertInvoked().prepareStatement("insert into my_schema.table_a (column_1,column_2,column_3) values (sysdate,null,?)");
        preparedStatement.assertInvoked().setObject(1, "=escaped", 0);
    }

    @Test
    public void schemaWithEmtpyTable() throws Exception {
        dataSetLoader.load(dataSetWithEmptyTable, new ArrayList<String>());
        connection.assertInvoked().prepareStatement("delete from my_schema.table_a");
    }

    @Test
    public void schemaWithEmtpyRows() throws Exception {
        dataSetLoader.load(dataSetWithEmptyRows, new ArrayList<String>());
        connection.assertInvoked().prepareStatement("delete from my_schema.table_a");
    }
}