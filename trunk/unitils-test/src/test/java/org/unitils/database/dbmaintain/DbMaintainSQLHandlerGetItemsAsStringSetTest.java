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

package org.unitils.database.dbmaintain;

import org.dbmaintain.database.DatabaseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.database.annotation.TestDataSource;
import org.unitilsnew.UnitilsJUnit4;

import javax.sql.DataSource;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.unitils.database.SqlUnitils.executeUpdate;
import static org.unitils.database.SqlUnitils.executeUpdateQuietly;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;

/**
 * @author Tim Ducheyne
 */
public class DbMaintainSQLHandlerGetItemsAsStringSetTest extends UnitilsJUnit4 {

    /* Tested object */
    private DbMaintainSQLHandler dbMaintainSQLHandler;

    @TestDataSource
    private DataSource dataSource;


    @Before
    public void initialize() {
        dbMaintainSQLHandler = new DbMaintainSQLHandler(null);

        executeUpdate("create table my_table (id int, value varchar)");
        executeUpdate("insert into my_table (id, value) values (10, 'value1')");
        executeUpdate("insert into my_table (id, value) values (20, 'value2')");
    }

    @After
    public void cleanup() {
        executeUpdateQuietly("drop table my_table");
    }


    @Test
    public void getItemsAsStringSet() {
        Set<String> result = dbMaintainSQLHandler.getItemsAsStringSet("select value from my_table", dataSource);
        assertLenientEquals(asList("value1", "value2"), result);
    }

    @Test
    public void exceptionWhenFailure() {
        try {
            dbMaintainSQLHandler.getItemsAsStringSet("xx", dataSource);
            fail("DatabaseException expected");
        } catch (DatabaseException e) {
            assertEquals("Error while executing query:\n" +
                    "xx", e.getMessage());
        }
    }

    @Test
    public void emptyWhenNoValueFound() {
        Set<String> result = dbMaintainSQLHandler.getItemsAsStringSet("select value from my_table where id = 999", dataSource);
        assertTrue(result.isEmpty());
    }
}
