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

package org.unitils.database;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.unitils.database.SqlUnitils.executeUpdate;
import static org.unitils.database.SqlUnitils.executeUpdateQuietly;

/**
 * @author Tim Ducheyne
 */
public class SqlUnitilsGetTableCountIntegrationTest {

    @Before
    public void initialize() {
        cleanup();
        executeUpdate("create table my_table (id int)");
        executeUpdate("create table my_table (id int)", "database2");
        executeUpdate("insert into my_table (id) values ('111')");
        executeUpdate("insert into my_table (id) values ('222')", "database2");
        executeUpdate("insert into my_table (id) values ('333')", "database2");
    }

    @After
    public void cleanup() {
        executeUpdateQuietly("drop table my_table", "database1");
        executeUpdateQuietly("drop table my_table", "database2");
    }


    @Test
    public void defaultDatabase() throws Exception {
        long result = SqlUnitils.getTableCount("my_table");
        assertEquals(1, result);
    }

    @Test
    public void namedDatabase() throws Exception {
        long result = SqlUnitils.getTableCount("my_table", "database2");
        assertEquals(2, result);
    }

    @Test
    public void exceptionWhenDatabaseNameNotFound() throws Exception {
        try {
            SqlUnitils.getTableCount("my_table", "xxx");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("No configuration found for database with name 'xxx'", e.getMessage());
        }
    }

    @Test
    public void defaultDatabaseWhenNullDatabaseName() throws Exception {
        long result = SqlUnitils.getTableCount("my_table", null);
        assertEquals(1, result);
    }

    @Test
    public void exceptionWhenTableNotFound() throws Exception {
        try {
            SqlUnitils.getTableCount("xxx");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to execute statement: 'select count(1) from xxx'.\n" +
                    "Reason: BadSqlGrammarException: StatementCallback; bad SQL grammar [select count(1) from xxx]; nested exception is java.sql.SQLException: Table not found in statement [select count(1) from xxx]", e.getMessage());
        }
    }
}
