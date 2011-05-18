package org.unitils.database.sqlassert;

import static org.unitils.database.SQLUnitils.executeUpdate;
import static org.unitils.database.SQLUnitils.executeUpdateQuietly;

import javax.sql.DataSource;

import junit.framework.AssertionFailedError;

import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import org.unitils.database.annotations.TestDataSource;


public class SqlAssertTest extends UnitilsJUnit4 {

    private static final String SQL_SELECT_STATEMENT = "select col1, col2 from test";
    
    private static final String SQL_SELECT_STATEMENT_ONE_ROW = "select col1, col2 from test where col1 = '1'";

    private static final String SQL_COUNT_STATEMENT = "select count(*) from test";

    @TestDataSource
    DataSource dataSource;

    @Before
	public void setUp() throws Exception {
    	LogFactory.getLog(SqlAssert.class).debug("creating the table");
    	 executeUpdateQuietly("create table test (col1 varchar(100), col2 varchar(100))", dataSource);
    	 executeUpdate("delete from test", dataSource);
    	 executeUpdate("insert into test values ('1', 'one')", dataSource);
    	 executeUpdate("insert into test values ('2', 'two')", dataSource);
	}


    
    @Test(expected = UnitilsException.class)
    public void triggerSqlException() {
        SqlAssert.assertSingleRowSqlResult("select * from not_existing_random_table", dataSource, new String[]{
            "one", "1"
        });
    }

    @Test
    public void assertSingleRowSqlResultMainSucces() {
        SqlAssert.assertSingleRowSqlResult(SQL_SELECT_STATEMENT_ONE_ROW, dataSource, new String[]{
            "one", "1"
        });
    }

    @Test
    public void assertSingleRowSqlResultDifferentOrderInResults() {
        SqlAssert.assertSingleRowSqlResult(SQL_SELECT_STATEMENT_ONE_ROW, dataSource, new String[]{
            "1", "one"
        });
    }

    @Test(expected = AssertionFailedError.class)
    public void assertSingleRowSqlResultMainFailure() {
        SqlAssert.assertSingleRowSqlResult(SQL_SELECT_STATEMENT, dataSource, new String[]{
            "two", "1"
        });

    }

    @Test(expected = AssertionFailedError.class)
    public void assertSingleRowSqlResultFailureOnNumber() {
        SqlAssert.assertSingleRowSqlResult(SQL_SELECT_STATEMENT, dataSource, new String[]{
            "one", "2"
        });

    }

    @Test
    public void assertMultipleRowSqlResultTestMainSucces() {
        String[][] expected = new String[][]{
            {
                "two", "2"
            }, {
                "one", "1"
            }
        };
        SqlAssert.assertMultipleRowSqlResult(SQL_SELECT_STATEMENT, dataSource, expected);

    }

    @Test
    public void assertMultipleRowSqlResultTestMainSuccesOtherImpl() {
        SqlAssert.assertMultipleRowSqlResult(SQL_SELECT_STATEMENT, dataSource, new String[]{
            "one", "1"
        }, new String[]{
            "two", "2"
        });

    }

    @Test(expected = AssertionFailedError.class)
    public void assertMultipleRowSqlResultTestMainFailure() {
        String[][] expected = new String[][]{
            {
                "two", "1"
            }, {
                "one", "1"
            }
        };
        SqlAssert.assertMultipleRowSqlResult(SQL_SELECT_STATEMENT, dataSource, expected);

    }

    @Test(expected = AssertionFailedError.class)
    public void assertMultipleRowSqlResultTestMainFailureDifferentNumber() {
        String[][] expected = new String[][]{
            {
                "two", "1"
            }, {
                "one", "1"
            }, {
                "three", "3"
            }
        };
        SqlAssert.assertMultipleRowSqlResult(SQL_SELECT_STATEMENT, dataSource, expected);

    }

    @Test(expected = AssertionFailedError.class)
    public void assertMultipleRowSqlResultTestMainFailureNotEnough() {
        String[][] expected = new String[][]{
            {
                "two", "1"
            },
        };
        SqlAssert.assertMultipleRowSqlResult(SQL_SELECT_STATEMENT, dataSource, expected);

    }

    @Test
    public void assertCountSqlResultMainSucces() {
        SqlAssert.assertCountSqlResult(SQL_COUNT_STATEMENT, dataSource, 2L);

    }

    @Test(expected = AssertionFailedError.class)
    public void assertCountSqlResultMainFailure() {
        SqlAssert.assertCountSqlResult(SQL_COUNT_STATEMENT, dataSource, 1L);

    }

}
