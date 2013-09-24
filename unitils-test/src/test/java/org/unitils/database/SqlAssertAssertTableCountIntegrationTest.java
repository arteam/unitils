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
import org.unitils.core.UnitilsException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.unitils.database.SqlUnitils.executeUpdate;
import static org.unitils.database.SqlUnitils.executeUpdateQuietly;

/**
 * @author Tim Ducheyne
 */
public class SqlAssertAssertTableCountIntegrationTest {

    @Before
    public void initialize() {
        cleanup();
        executeUpdate("create table my_table (value int)");
        executeUpdate("create table my_table (value int)", "database2");
    }

    @After
    public void cleanup() {
        executeUpdateQuietly("drop table my_table", "database1");
        executeUpdateQuietly("drop table my_table", "database2");
    }


    @Test
    public void assertionSuccessfulForDefaultDatabase() throws Exception {
        executeUpdate("insert into my_table (value) values (1)");
        executeUpdate("insert into my_table (value) values (2)");

        SqlAssert.assertTableCount(2, "my_table");
    }

    @Test
    public void assertionFailedForDefaultDatabase() throws Exception {
        executeUpdate("insert into my_table (value) values (1)");
        try {
            SqlAssert.assertTableCount(2, "my_table");
            fail("AssertionError expected");
        } catch (AssertionError e) {
            assertEquals("Different table count for table 'my_table': expected:<2> but was:<1>", e.getMessage());
        }
    }

    @Test
    public void assertionSuccessfulForNamedDatabase() throws Exception {
        executeUpdate("insert into my_table (value) values (1)", "database2");
        executeUpdate("insert into my_table (value) values (2)", "database2");

        SqlAssert.assertTableCount(2, "my_table", "database2");
    }

    @Test
    public void assertionFailedForNamedDatabase() throws Exception {
        executeUpdate("insert into my_table (value) values (1)", "database2");
        try {
            SqlAssert.assertTableCount(2, "my_table", "database2");
            fail("AssertionError expected");
        } catch (AssertionError e) {
            assertEquals("Different table count for table 'my_table': expected:<2> but was:<1>", e.getMessage());
        }
    }

    @Test
    public void assertionFailedNoResults() throws Exception {
        try {
            SqlAssert.assertTableCount(2, "my_table");
            fail("AssertionError expected");
        } catch (AssertionError e) {
            assertEquals("Different table count for table 'my_table': expected:<2> but was:<0>", e.getMessage());
        }
    }

    @Test
    public void assertionSuccessfulNoResults() throws Exception {
        SqlAssert.assertTableCount(0, "my_table");
    }

    @Test
    public void exceptionWhenDatabaseNameNotFound() throws Exception {
        try {
            SqlAssert.assertTableCount(0, "my_table", "xxx");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("No configuration found for database with name 'xxx'", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenUnknownTable() throws Exception {
        try {
            SqlAssert.assertTableCount(0, "xxx");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to execute statement: 'select count(1) from xxx'.\n" +
                    "Reason: BadSqlGrammarException: StatementCallback; bad SQL grammar [select count(1) from xxx]; nested exception is java.sql.SQLException: Table not found in statement [select count(1) from xxx]", e.getMessage());
        }
    }
}
