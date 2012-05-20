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

package org.unitilsnew.database;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.unitilsnew.database.SqlUnitils.executeUpdate;
import static org.unitilsnew.database.SqlUnitils.executeUpdateQuietly;

/**
 * @author Tim Ducheyne
 */
public class SqlAssertAssertRowsIntegrationTest {

    @Before
    public void initialize() {
        cleanup();
        executeUpdate("create table my_table (col1 int, col2 int, col3 int)");
        executeUpdate("create table my_table (col1 int, col2 int, col3 int)", "database2");
    }

    @After
    public void cleanup() {
        executeUpdateQuietly("drop table my_table", "database1");
        executeUpdateQuietly("drop table my_table", "database2");
    }


    @Test
    public void assertionSuccessfulForDefaultDatabase() throws Exception {
        executeUpdate("insert into my_table (col1, col2, col3) values (1, 2, 3)");
        executeUpdate("insert into my_table (col1, col2, col3) values (4, 5, 6)");

        SqlAssert.assertRows("select col1, col2, col3 from my_table", new String[]{"1", "2", "3"}, new String[]{"4", "5", "6"});
    }

    @Test
    public void assertionFailedForDefaultDatabase() throws Exception {
        executeUpdate("insert into my_table (col1, col2, col3) values (1, 1, 1)");
        try {
            SqlAssert.assertRows("select col1, col2, col3 from my_table", new String[]{"1", "2", "3"}, new String[]{"4", "5", "6"});
            fail("AssertionError expected");
        } catch (AssertionError e) {
            assertEquals("Expected: [[\"1\", \"2\", \"3\"], [\"4\", \"5\", \"6\"]], actual: [[\"1\", \"1\", \"1\"]]\n" +
                    "\n" +
                    "--- Found following differences ---\n" +
                    "Collections have a different size: Expected 2, actual 1.\n" +
                    "\n" +
                    "--- Difference detail tree ---\n" +
                    " expected: [[\"1\", \"2\", \"3\"], [\"4\", \"5\", \"6\"]]\n" +
                    "   actual: [[\"1\", \"1\", \"1\"]]\n\n", e.getMessage());
        }
    }

    @Test
    public void assertionSuccessfulForNamedDatabase() throws Exception {
        executeUpdate("insert into my_table (col1, col2, col3) values (1, 2, 3)", "database2");
        executeUpdate("insert into my_table (col1, col2, col3) values (4, 5, 6)", "database2");

        SqlAssert.assertRows("select col1, col2, col3 from my_table", "database2", new String[]{"1", "2", "3"}, new String[]{"4", "5", "6"});
    }

    @Test
    public void assertionFailedForNamedDatabase() throws Exception {
        executeUpdate("insert into my_table (col1, col2, col3) values (1, 1, 1)", "database2");
        try {
            SqlAssert.assertRows("select col1, col2, col3 from my_table", "database2", new String[]{"1", "2", "3"}, new String[]{"4", "5", "6"});
            fail("AssertionError expected");
        } catch (AssertionError e) {
            assertEquals("Expected: [[\"1\", \"2\", \"3\"], [\"4\", \"5\", \"6\"]], actual: [[\"1\", \"1\", \"1\"]]\n" +
                    "\n" +
                    "--- Found following differences ---\n" +
                    "Collections have a different size: Expected 2, actual 1.\n" +
                    "\n" +
                    "--- Difference detail tree ---\n" +
                    " expected: [[\"1\", \"2\", \"3\"], [\"4\", \"5\", \"6\"]]\n" +
                    "   actual: [[\"1\", \"1\", \"1\"]]\n\n", e.getMessage());
        }
    }

    @Test
    public void assertionFailedNoResults() throws Exception {
        try {
            SqlAssert.assertRows("select col1, col2, col3 from my_table", new String[]{"1", "2", "3"}, new String[]{"4", "5", "6"});
            fail("AssertionError expected");
        } catch (AssertionError e) {
            assertEquals("Expected: [[\"1\", \"2\", \"3\"], [\"4\", \"5\", \"6\"]], actual: []\n" +
                    "\n" +
                    "--- Found following differences ---\n" +
                    "Collections have a different size: Expected 2, actual 0.\n" +
                    "\n" +
                    "--- Difference detail tree ---\n" +
                    " expected: [[\"1\", \"2\", \"3\"], [\"4\", \"5\", \"6\"]]\n" +
                    "   actual: []\n\n", e.getMessage());
        }
    }

    @Test
    public void assertionSuccessfulNoValues() throws Exception {
        SqlAssert.assertRows("select col1, col2, col3 from my_table");
    }

    @Test
    public void assertionFailedNoValues() throws Exception {
        executeUpdate("insert into my_table (col1, col2, col3) values (1, 2, 3)");
        try {
            SqlAssert.assertRows("select col1, col2, col3 from my_table");
            fail("AssertionError expected");
        } catch (AssertionError e) {
            assertEquals("Expected: [], actual: [[\"1\", \"2\", \"3\"]]\n" +
                    "\n" +
                    "--- Found following differences ---\n" +
                    "Collections have a different size: Expected 0, actual 1.\n" +
                    "\n" +
                    "--- Difference detail tree ---\n" +
                    " expected: []\n" +
                    "   actual: [[\"1\", \"2\", \"3\"]]\n\n", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenDatabaseNameNotFound() throws Exception {
        try {
            SqlAssert.assertRows("select col1, col2, col3 from my_table", "xxx");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("No configuration found for database with name 'xxx'", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenInvalidStatement() throws Exception {
        try {
            SqlAssert.assertRows("xxx");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to execute statement: 'xxx'. Reason:\n" +
                    "StatementCallback; bad SQL grammar [xxx]; nested exception is java.sql.SQLException: Unexpected token: XXX in statement [xxx]", e.getMessage());
        }
    }
}
