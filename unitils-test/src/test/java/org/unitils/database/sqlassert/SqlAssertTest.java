/*
 * Copyright 2011,  Unitils.org
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

package org.unitils.database.sqlassert;

import junit.framework.AssertionFailedError;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;
import org.unitils.database.annotations.TestDataSource;
import org.unitilsnew.UnitilsJUnit4;

import javax.sql.DataSource;

import static org.unitils.database.SQLUnitils.executeUpdate;
import static org.unitils.database.SQLUnitils.executeUpdateQuietly;
import static org.unitils.database.sqlassert.SqlAssert.*;

/**
 * @author Jeroen Horemans
 */
public class SqlAssertTest extends UnitilsJUnit4 {

    private static final String SQL_SELECT_STATEMENT = "select col1, col2 from test";
    private static final String SQL_SELECT_STATEMENT_ONE_ROW = "select col1, col2 from test where col1 = '1'";
    private static final String SQL_COUNT_STATEMENT = "select count(*) from test";

    @TestDataSource
    private DataSource dataSource;


    @Before
    public void initialize() {
        dropTestTables();
        createTestTables();

        executeUpdate("delete from test", dataSource);
        executeUpdate("insert into test values ('1', 'one')", dataSource);
        executeUpdate("insert into test values ('2', 'two')", dataSource);
    }

    @After
    public void cleanup() {
        dropTestTables();
    }


    @Test(expected = UnitilsException.class)
    public void triggerSqlException() {
        assertSingleRowSqlResult("select * from not_existing_random_table", dataSource, new String[]{"one", "1"});
    }

    @Test
    public void assertSingleRowSqlResultMainSucces() {
        assertSingleRowSqlResult(SQL_SELECT_STATEMENT_ONE_ROW, dataSource, new String[]{"one", "1"});
    }

    @Test
    public void assertSingleRowSqlResultDifferentOrderInResults() {
        assertSingleRowSqlResult(SQL_SELECT_STATEMENT_ONE_ROW, dataSource, new String[]{"1", "one"});
    }

    @Test(expected = AssertionFailedError.class)
    public void assertSingleRowSqlResultMainFailure() {
        assertSingleRowSqlResult(SQL_SELECT_STATEMENT, dataSource, new String[]{"two", "1"});
    }

    @Test(expected = AssertionFailedError.class)
    public void assertSingleRowSqlResultFailureOnNumber() {
        assertSingleRowSqlResult(SQL_SELECT_STATEMENT, dataSource, new String[]{"one", "2"});
    }

    @Test
    public void assertMultipleRowSqlResultTestMainSucces() {
        String[][] expected = new String[][]{{"two", "2"}, {"one", "1"}};
        assertMultipleRowSqlResult(SQL_SELECT_STATEMENT, dataSource, expected);

    }

    @Test
    public void assertMultipleRowSqlResultTestMainSuccesOtherImpl() {
        assertMultipleRowSqlResult(SQL_SELECT_STATEMENT, dataSource, new String[]{"one", "1"}, new String[]{"two", "2"});
    }

    @Test(expected = AssertionFailedError.class)
    public void assertMultipleRowSqlResultTestMainFailure() {
        String[][] expected = new String[][]{{"two", "1"}, {"one", "1"}};
        assertMultipleRowSqlResult(SQL_SELECT_STATEMENT, dataSource, expected);
    }

    @Test(expected = AssertionFailedError.class)
    public void assertMultipleRowSqlResultTestMainFailureDifferentNumber() {
        String[][] expected = new String[][]{{"two", "1"}, {"one", "1"}, {"three", "3"}};
        assertMultipleRowSqlResult(SQL_SELECT_STATEMENT, dataSource, expected);
    }

    @Test(expected = AssertionFailedError.class)
    public void assertMultipleRowSqlResultTestMainFailureNotEnough() {
        String[][] expected = new String[][]{{"two", "1"},};
        assertMultipleRowSqlResult(SQL_SELECT_STATEMENT, dataSource, expected);
    }

    @Test
    public void assertCountSqlResultMainSucces() {
        assertCountSqlResult(SQL_COUNT_STATEMENT, dataSource, 2L);
    }

    @Test(expected = AssertionFailedError.class)
    public void assertCountSqlResultMainFailure() {
        assertCountSqlResult(SQL_COUNT_STATEMENT, dataSource, 1L);
    }


    private void createTestTables() {
        executeUpdate("create table TEST (col1 varchar(100), col2 varchar(100))", dataSource);
    }

    private void dropTestTables() {
        executeUpdateQuietly("drop table TEST", dataSource);
    }

}
