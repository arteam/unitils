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
package org.unitils.dataset;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.fail;
import static org.unitils.database.SQLUnitils.executeUpdate;
import static org.unitils.database.SQLUnitils.executeUpdateQuietly;
import static org.unitils.dataset.DataSetLoader.cleanInsertDataSetFile;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class AssertExpectedDataSetParentChildTest extends OneDbDataSetTestBase {

    @Test
    public void matchingDataSet() throws Exception {
        cleanInsertDataSetFile(this, "DataSetModuleExpectedDataSetParentChildTest.xml");
        DataSetAssert.assertDataSet(this, "DataSetModuleExpectedDataSetParentChildTest.xml");
    }

    @Test
    public void differentDataSet() throws Exception {
        try {
            cleanInsertDataSetFile(this, "DataSetModuleExpectedDataSetParentChildTest.xml");
            DataSetAssert.assertDataSet(this, "DataSetModuleExpectedDataSetParentChildTest-different.xml");

        } catch (AssertionError e) {
            assertMessageContains("No match found for data set row:  PUBLIC.PARENT [PK1=777, PK2=888, PARENTCOLUMN=xxxx]", e);
            assertMessageContains("Expected:  777  888  xxxx", e);
            assertMessageContains("Actual:    1    2    parent", e);
            assertMessageContains("No match found for data set row:  PUBLIC.CHILD [PK=3, CHILDCOLUMN=child, FK1=777, FK2=888]", e);
            assertMessageContains("Expected:  777  888", e);
            assertMessageContains("Actual:    1    2", e);
            assertMessageContains("Actual database content", e);
            return;
        }
        fail("Expected an AssertionError"); //fail also raises assertion errors
    }


    @Override
    @Before
    public void createTestTables() {
        executeUpdate("create table parent (pk1 integer not null, pk2 integer not null, parentColumn varchar(100), primary key (pk1, pk2))", dataSource);
        executeUpdate("create table child (pk integer not null primary key, childColumn varchar(100), fk1 integer, fk2 integer, foreign key (fk1, fk2) references parent(pk1, pk2))", dataSource);
    }

    @After
    public void dropTestTables() {
        executeUpdateQuietly("drop table child", dataSource);
        executeUpdateQuietly("drop table parent", dataSource);
    }
}