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
package org.unitils.database;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.unitils.UnitilsJUnit4;
import org.unitils.database.core.DataSourceWrapper;

import static org.unitils.database.DatabaseUnitils.getDataSourceWrapper;
import static org.unitils.database.SqlAssert.assertTableCount;
import static org.unitils.database.SqlUnitils.executeUpdate;
import static org.unitils.database.SqlUnitils.executeUpdateQuietly;

/**
 * @author Tim Ducheyne
 */
@ContextConfiguration
public class DatabaseUnitilsSpringTransactionIntegrationTest extends UnitilsJUnit4 {


    @Before
    public void initialize() {
        executeUpdate("create table my_table (id int)");
    }

    @After
    public void cleanup() {
        executeUpdateQuietly("drop table my_table");
    }


    @Test
    public void specifiedTransactionManager() throws Exception {
        DatabaseUnitils.startTransactionForTransactionManager("transactionManager1");

        DataSourceWrapper dataSourceWrapper = getDataSourceWrapper();
        dataSourceWrapper.executeUpdate("insert into my_table(id) values (111)");

        DatabaseUnitils.commitTransaction();

        assertTableCount(1, "my_table");
    }

    // todo more tests:  database name, tx manager + database name etc
}
