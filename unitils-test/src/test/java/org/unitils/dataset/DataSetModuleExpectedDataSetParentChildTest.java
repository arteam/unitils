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
package org.unitils.dataset;

import org.junit.Test;

import static org.unitils.database.SQLUnitils.executeUpdate;
import static org.unitils.database.SQLUnitils.executeUpdateQuietly;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DataSetModuleExpectedDataSetParentChildTest extends DataSetModuleDataSetTestBase {


    @Test
    public void matchingDataSet() throws Exception {
        // todo implement
//        dataSetModule.loadDataSet(asList("DataSetModuleExpectedDataSetParentChildTest.xml"), new ArrayList<String>(), getClass(), InsertDataSetLoader2.class);
//        dataSetModule.assertExpectedDataSet(asList("DataSetModuleExpectedDataSetParentChildTest.xml"), new ArrayList<String>(), getClass(), true);
    }

    @Test
    public void differentDataSet() throws Exception {
        // todo implement
//        dataSetModule.loadDataSet(asList("DataSetModuleExpectedDataSetParentChildTest.xml"), new ArrayList<String>(), getClass(), InsertDataSetLoader2.class);
//        try {
//            dataSetModule.assertExpectedDataSet(asList("DataSetModuleExpectedDataSetParentChildTest-different.xml"), new ArrayList<String>(), getClass(), true);
//        } catch (AssertionError e) {
//            assertMessageContains("Found differences for table PUBLIC.PARENT", e);
//            assertMessageContains("Different database record found for data set row:  pk1=\"777\", pk2=\"888\", parentColumn=\"xxxx\"", e);
//            assertMessageContains("Found differences for table PUBLIC.CHILD", e);
//            assertMessageContains("Different database record found for data set row:  pk=\"3\", childColumn=\"child\", FK1=\"777\", FK2=\"888\"", e);
//            assertMessageContains("Actual database content", e);
//            return;
//        }
//        fail("Expected an AssertionError"); //fail also raises assertion errors
    }


    @Override
    protected void createTestTables() {
        executeUpdate("create table parent (pk1 integer not null, pk2 integer not null, parentColumn varchar(100), primary key (pk1, pk2))", dataSource);
        executeUpdate("create table child (pk integer not null primary key, childColumn varchar(100), fk1 integer, fk2 integer, foreign key (fk1, fk2) references parent(pk1, pk2))", dataSource);
    }

    @Override
    protected void dropTestTable() {
        executeUpdateQuietly("drop table child", dataSource);
        executeUpdateQuietly("drop table parent", dataSource);
    }
}