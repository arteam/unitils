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

import static org.junit.Assert.*;
import static org.unitilsnew.database.SqlUnitils.executeUpdate;
import static org.unitilsnew.database.SqlUnitils.executeUpdateQuietly;

/**
 * @author Tim Ducheyne
 */
public class SqlUnitilsGetObjectIntegrationTest {

    @Before
    public void initialize() {
        cleanup();
        executeUpdate("create table my_table (value int, other varchar)");
        executeUpdate("create table my_table (value int)", "database2");
        executeUpdate("insert into my_table (value, other) values (11, 'xxx')");
        executeUpdate("insert into my_table (value) values (22)", "database2");
    }

    @After
    public void cleanup() {
        executeUpdateQuietly("drop table my_table", "database1");
        executeUpdateQuietly("drop table my_table", "database2");
    }


    @Test
    public void defaultDatabase() throws Exception {
        Integer result = SqlUnitils.getObject("select value from my_table", Integer.class);
        assertEquals(new Integer(11), result);
    }

    @Test
    public void namedDatabase() throws Exception {
        Integer result = SqlUnitils.getObject("select value from my_table", Integer.class, "database2");
        assertEquals(new Integer(22), result);
    }

    @Test
    public void nullValue() throws Exception {
        executeUpdate("update my_table set value = null");

        Integer result = SqlUnitils.getObject("select value from my_table", Integer.class);
        assertNull(result);
    }

    @Test
    public void exceptionWhenValueNotOfCorrectType() throws Exception {
        try {
            SqlUnitils.getObject("select other from my_table", Integer.class);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to execute statement: 'select other from my_table'.\n" +
                    "Reason: BadSqlGrammarException: StatementCallback; bad SQL grammar [select other from my_table]; nested exception is java.sql.SQLException: Wrong data type: type: VARCHAR (12) expected: INTEGER value: xxx", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenMoreThanOneResultFound() throws Exception {
        executeUpdate("insert into my_table (value) values (33)");
        try {
            SqlUnitils.getObject("select value from my_table", Integer.class);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to get value. Statement produced more than 1 result: 'select value from my_table'.\n" +
                    "Reason: IncorrectResultSizeDataAccessException: Incorrect result size: expected 1, actual 2", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenNoResultsFound() throws Exception {
        try {
            SqlUnitils.getObject("select value from my_table where value = 999", Integer.class);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to get value. Statement did not produce any results: 'select value from my_table where value = 999'.\n" +
                    "Reason: EmptyResultDataAccessException: Incorrect result size: expected 1, actual 0", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenDatabaseNameNotFound() throws Exception {
        try {
            SqlUnitils.getObject("select value from my_table", Integer.class, "xxx");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("No configuration found for database with name 'xxx'", e.getMessage());
        }
    }

    @Test
    public void defaultDatabaseWhenNullDatabaseName() throws Exception {
        Integer result = SqlUnitils.getObject("select value from my_table", Integer.class, null);
        assertEquals(new Integer(11), result);
    }

    @Test
    public void exceptionWhenNullType() throws Exception {
        try {
            SqlUnitils.getObject("select value from my_table", null);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to get value. Type cannot be null.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenInvalidStatement() throws Exception {
        try {
            SqlUnitils.getObject("xxx", Integer.class);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to execute statement: 'xxx'.\n" +
                    "Reason: BadSqlGrammarException: StatementCallback; bad SQL grammar [xxx]; nested exception is java.sql.SQLException: Unexpected token: XXX in statement [xxx]", e.getMessage());
        }
    }
}
