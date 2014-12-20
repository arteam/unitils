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

import static org.unitils.database.SqlUnitils.executeUpdate;
import static org.unitils.database.SqlUnitils.executeUpdateQuietly;

/**
 * @author Tim Ducheyne
 */
public class DbMaintainUnitilsClearDatabaseIntegrationTest {


    @Before
    public void initialize() {
        cleanup();
        executeUpdate("create table my_table (id int)");
    }

    @After
    public void cleanup() {
        executeUpdateQuietly("drop table my_table");
    }


    @Test
    public void clearDatabase() throws Exception {
        SqlAssert.assertInteger(1, "select count(1) from INFORMATION_SCHEMA.SYSTEM_TABLES where TABLE_NAME = 'MY_TABLE'");

        DbMaintainUnitils.clearDatabase();
        SqlAssert.assertInteger(0, "select count(1) from INFORMATION_SCHEMA.SYSTEM_TABLES where TABLE_NAME = 'MY_TABLE'");
    }
}
