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

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.unitils.database.SqlUnitils.executeUpdate;
import static org.unitils.database.SqlUnitils.executeUpdateQuietly;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;

/**
 * @author Tim Ducheyne
 */
public class SqlUnitilsGetObjectListIntegrationTest {

    @Before
    public void initialize() {
        cleanup();
        executeUpdate("create table my_table (value int, other varchar)");
        executeUpdate("create table my_table (value int)", "database2");
        executeUpdate("insert into my_table (value, other) values (111, 'xxx')");
        executeUpdate("insert into my_table (value) values (222)");
        executeUpdate("insert into my_table (value) values (333)", "database2");
    }

    @After
    public void cleanup() {
        executeUpdateQuietly("drop table my_table", "database1");
        executeUpdateQuietly("drop table my_table", "database2");
    }


    @Test
    public void defaultDatabase() throws Exception {
        List<Integer> result = SqlUnitils.getObjectList("select value from my_table", Integer.class);
        assertLenientEquals(asList(111, 222), result);
    }

    @Test
    public void namedDatabase() throws Exception {
        List<Integer> result = SqlUnitils.getObjectList("select value from my_table", Integer.class, "database2");
        assertLenientEquals(asList(333), result);
    }

    @Test
    public void nullValue() throws Exception {
        executeUpdate("update my_table set value = null");

        List<Integer> result = SqlUnitils.getObjectList("select value from my_table", Integer.class);
        assertLenientEquals(asList(null, null), result);
    }

    @Test
    public void exceptionWhenValueNotOfCorrectType() throws Exception {
        try {
            SqlUnitils.getObjectList("select other from my_table where value = 111", Integer.class);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to execute statement: 'select other from my_table where value = 111'.\n" +
                    "Reason: BadSqlGrammarException: StatementCallback; bad SQL grammar [select other from my_table where value = 111]; nested exception is java.sql.SQLException: Wrong data type: type: VARCHAR (12) expected: INTEGER value: xxx", e.getMessage());
        }
    }

    @Test
    public void emptyListWhenNoResultsFound() throws Exception {
        List<Integer> result = SqlUnitils.getObjectList("select value from my_table where value = '999'", Integer.class);
        assertTrue(result.isEmpty());
    }

    @Test
    public void exceptionWhenDatabaseNameNotFound() throws Exception {
        try {
            SqlUnitils.getObjectList("select value from my_table", Integer.class, "xxx");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("No configuration found for database with name 'xxx'", e.getMessage());
        }
    }

    @Test
    public void defaultDatabaseWhenNullDatabaseName() throws Exception {
        List<Integer> result = SqlUnitils.getObjectList("select value from my_table", Integer.class, null);
        assertLenientEquals(asList(111, 222), result);
    }

    @Test
    public void exceptionWhenInvalidStatement() throws Exception {
        try {
            SqlUnitils.getObjectList("xxx", Integer.class);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to execute statement: 'xxx'.\n" +
                    "Reason: BadSqlGrammarException: StatementCallback; bad SQL grammar [xxx]; nested exception is java.sql.SQLException: Unexpected token: XXX in statement [xxx]", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenNullType() throws Exception {
        try {
            SqlUnitils.getObjectList("select value from my_table", null);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to get value list. Type cannot be null.", e.getMessage());
        }
    }
}
