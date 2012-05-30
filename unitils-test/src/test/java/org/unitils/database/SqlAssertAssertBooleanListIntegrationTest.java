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

import java.util.Arrays;
import java.util.Collections;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.unitils.database.SqlUnitils.executeUpdate;
import static org.unitils.database.SqlUnitils.executeUpdateQuietly;

/**
 * @author Tim Ducheyne
 */
public class SqlAssertAssertBooleanListIntegrationTest {

    @Before
    public void initialize() {
        cleanup();
        executeUpdate("create table my_table (value int, other varchar)");
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
        executeUpdate("insert into my_table (value) values (0)");
        SqlAssert.assertBooleanList(asList(true, false), "select value from my_table");
    }

    @Test
    public void orderIsIgnored() throws Exception {
        executeUpdate("insert into my_table (value) values (1)");
        executeUpdate("insert into my_table (value) values (0)");
        SqlAssert.assertBooleanList(asList(false, true), "select value from my_table");
    }

    @Test
    public void assertionFailedForDefaultDatabase() throws Exception {
        executeUpdate("insert into my_table (value) values (1)");
        try {
            SqlAssert.assertBooleanList(asList(true, false), "select value from my_table");
            fail("AssertionError expected");
        } catch (AssertionError e) {
            assertEquals("Different result found for query 'select value from my_table':\n" +
                    "Expected: [true, false], actual: [true]\n" +
                    "\n" +
                    "--- Found following differences ---\n" +
                    "Collections have a different size: Expected 2, actual 1.\n" +
                    "\n" +
                    "--- Difference detail tree ---\n" +
                    " expected: [true, false]\n" +
                    "   actual: [true]\n\n", e.getMessage());
        }
    }

    @Test
    public void assertionSuccessfulForNamedDatabase() throws Exception {
        executeUpdate("insert into my_table (value) values (0)", "database2");
        executeUpdate("insert into my_table (value) values (1)", "database2");
        SqlAssert.assertBooleanList(asList(false, true), "select value from my_table", "database2");
    }

    @Test
    public void assertionFailedForNamedDatabase() throws Exception {
        executeUpdate("insert into my_table (value) values (0)", "database2");
        try {
            SqlAssert.assertBooleanList(asList(false, true), "select value from my_table", "database2");
            fail("AssertionError expected");
        } catch (AssertionError e) {
            assertEquals("Different result found for query 'select value from my_table':\n" +
                    "Expected: [false, true], actual: [false]\n" +
                    "\n" +
                    "--- Found following differences ---\n" +
                    "Collections have a different size: Expected 2, actual 1.\n" +
                    "\n" +
                    "--- Difference detail tree ---\n" +
                    " expected: [false, true]\n" +
                    "   actual: [false]\n\n", e.getMessage());
        }
    }

    @Test
    public void assertionSuccessfulForEmptyResult() throws Exception {
        SqlAssert.assertBooleanList(Collections.<Boolean>emptyList(), "select value from my_table");
    }

    @Test
    public void assertionFailedForEmptyResult() throws Exception {
        try {
            SqlAssert.assertBooleanList(asList(true, false), "select value from my_table");
            fail("AssertionError expected");
        } catch (AssertionError e) {
            assertEquals("Different result found for query 'select value from my_table':\n" +
                    "Expected: [true, false], actual: []\n" +
                    "\n" +
                    "--- Found following differences ---\n" +
                    "Collections have a different size: Expected 2, actual 0.\n" +
                    "\n" +
                    "--- Difference detail tree ---\n" +
                    " expected: [true, false]\n" +
                    "   actual: []\n\n", e.getMessage());
        }
    }

    @Test
    public void assertionSuccessfulForNullValue() throws Exception {
        executeUpdate("insert into my_table (value) values (null)");
        executeUpdate("insert into my_table (value) values (null)");
        SqlAssert.assertBooleanList(Arrays.<Boolean>asList(null, null), "select value from my_table");
    }

    @Test
    public void assertionFailedForNullValue() throws Exception {
        executeUpdate("insert into my_table (value) values (1)");
        try {
            SqlAssert.assertBooleanList(Arrays.<Boolean>asList(null, null), "select value from my_table");
            fail("AssertionError expected");
        } catch (AssertionError e) {
            assertEquals("Different result found for query 'select value from my_table':\n" +
                    "Expected: [null, null], actual: [true]\n" +
                    "\n" +
                    "--- Found following differences ---\n" +
                    "Collections have a different size: Expected 2, actual 1.\n" +
                    "[0,0]: expected: null, actual: true\n" +
                    "[1,0]: expected: null, actual: true\n" +
                    "\n" +
                    "--- Difference detail tree ---\n" +
                    " expected: [null, null]\n" +
                    "   actual: [true]\n" +
                    "\n" +
                    "[0,0] expected: null\n" +
                    "[0,0]   actual: true\n" +
                    "\n" +
                    "[1,0] expected: null\n" +
                    "[1,0]   actual: true\n\n", e.getMessage());
        }
    }

    @Test
    public void assertionSuccessfulForNonBooleanValue() throws Exception {
        executeUpdate("insert into my_table (other) values ('xxx')");
        SqlAssert.assertBooleanList(asList(false), "select other from my_table");
    }

    @Test
    public void assertionFailedForNonBooleanValue() throws Exception {
        executeUpdate("insert into my_table (other) values ('xxx')");
        try {
            SqlAssert.assertBooleanList(asList(true), "select other from my_table");
            fail("AssertionError expected");
        } catch (AssertionError e) {
            assertEquals("Different result found for query 'select other from my_table':\n" +
                    "Expected: [true], actual: [false]\n" +
                    "\n" +
                    "--- Found following differences ---\n" +
                    "[0,0]: expected: true, actual: false\n" +
                    "\n" +
                    "--- Difference detail tree ---\n" +
                    " expected: [true]\n" +
                    "   actual: [false]\n" +
                    "\n" +
                    "[0,0] expected: true\n" +
                    "[0,0]   actual: false\n\n", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenDatabaseNameNotFound() throws Exception {
        try {
            SqlAssert.assertBooleanList(asList(true), "select value from my_table", "xxx");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("No configuration found for database with name 'xxx'", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenInvalidStatement() throws Exception {
        try {
            SqlAssert.assertBooleanList(asList(true), "xxx");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to execute statement: 'xxx'.\n" +
                    "Reason: BadSqlGrammarException: StatementCallback; bad SQL grammar [xxx]; nested exception is java.sql.SQLException: Unexpected token: XXX in statement [xxx]", e.getMessage());
        }
    }
}
