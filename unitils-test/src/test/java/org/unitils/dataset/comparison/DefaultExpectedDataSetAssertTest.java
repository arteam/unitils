/*
 * Copyright 2006-2009,  Unitils.org
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
package org.unitils.dataset.comparison;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.dataset.comparison.impl.DefaultExpectedDataSetAssert;
import org.unitils.dataset.core.DatabaseColumnWithValue;
import org.unitils.dataset.core.DatabaseRow;
import org.unitils.mock.Mock;

import static java.sql.Types.VARCHAR;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DefaultExpectedDataSetAssertTest extends UnitilsJUnit4 {

    /* Tested object */
    private DefaultExpectedDataSetAssert defaultExpectedDataSetAssert = new DefaultExpectedDataSetAssert();

    private Mock<DataSetComparator> dataSetComparator;

    private DataSetComparison matchingDataSetComparison;
    private DataSetComparison dataSetComparisonWithDifferences;
    private DataSetComparison dataSetComparisonWithMissingRows;
    private DataSetComparison dataSetComparisonWithExpectedButNotEmptyTables;


    @Before
    public void initialize() {
        defaultExpectedDataSetAssert.init(dataSetComparator.getMock(), null);

        matchingDataSetComparison = createMatchingDataSetComparison();
        dataSetComparisonWithDifferences = createDataSetComparisonWithDifferences();
        dataSetComparisonWithMissingRows = createDataSetComparisonWithMissingRows();
        dataSetComparisonWithExpectedButNotEmptyTables = createDataSetComparisonWithExpectedButNotEmptyTable();
    }


    @Test
    public void match() {
        dataSetComparator.returns(matchingDataSetComparison).compare(null, null);
        defaultExpectedDataSetAssert.assertEqual(null, null);
    }

    @Test
    public void differences() {
        try {
            dataSetComparator.returns(dataSetComparisonWithDifferences).compare(null, null);
            defaultExpectedDataSetAssert.assertEqual(null, null);
        } catch (AssertionError e) {
            assertErrorMessageContains("No match found for data set row:  schema.table   column_1=1, column_2=2", e);
            assertErrorMessageContains("Expected:  1         2", e);
            assertErrorMessageContains("Actual:    666       777", e);
            assertErrorMessageContains("No match found for data set row:  schema.table   column_1=3, column_2=4", e);
            assertErrorMessageContains("Expected:  3         4", e);
            assertErrorMessageContains("Actual:    888       999", e);
            return;
        }
        fail("Expected an AssertionError"); //fail also raises assertion errors
    }

    @Test
    public void missingRows() {
        try {
            dataSetComparator.returns(dataSetComparisonWithMissingRows).compare(null, null);
            defaultExpectedDataSetAssert.assertEqual(null, null);
        } catch (AssertionError e) {
            assertErrorMessageContains("No database record found for data set row:  schema.table   column_1=1, column_2=2", e);
            assertErrorMessageContains("No database record found for data set row:  schema.table   column_3=3, column_4=4", e);
            return;
        }
        fail("Expected an AssertionError"); //fail also raises assertion errors
    }

    @Test
    public void expectedTableToBeEmptyButWasNot() {
        try {
            dataSetComparator.returns(dataSetComparisonWithExpectedButNotEmptyTables).compare(null, null);
            defaultExpectedDataSetAssert.assertEqual(null, null);
        } catch (AssertionError e) {
            assertErrorMessageContains("Expected no more database records in table schema.table but found more records.", e);
            return;
        }
        fail("Expected an AssertionError"); //fail also raises assertion errors
    }


    private void assertErrorMessageContains(String message, AssertionError e) {
        String errorMessage = e.getMessage();
        assertTrue("Error message did not contain '" + message + "'. Error message: " + errorMessage, errorMessage.contains(message));
    }

    private DataSetComparison createMatchingDataSetComparison() {
        return createDataSetComparison(new TableComparison("schema.tsble"));
    }

    private DataSetComparison createDataSetComparisonWithDifferences() {
        TableComparison tableComparison = new TableComparison("schema.table");
        tableComparison.replaceIfBetterRowComparison(createRowComparisonWithDifferences(1, 666, 2, 777));
        tableComparison.replaceIfBetterRowComparison(createRowComparisonWithDifferences(3, 888, 4, 999));
        return createDataSetComparison(tableComparison);
    }

    private DataSetComparison createDataSetComparisonWithMissingRows() {
        TableComparison tableComparison = new TableComparison("schema.table");
        tableComparison.addMissingRow(createExpectedDatabaseRow(createDatabaseColumnWithValue("column_1", 1), createDatabaseColumnWithValue("column_2", 2)));
        tableComparison.addMissingRow(createExpectedDatabaseRow(createDatabaseColumnWithValue("column_3", 3), createDatabaseColumnWithValue("column_4", 4)));
        return createDataSetComparison(tableComparison);
    }

    private DataSetComparison createDataSetComparisonWithExpectedButNotEmptyTable() {
        TableComparison tableComparison = new TableComparison("schema.table");
        tableComparison.setExpectedNoMoreRecordsButFoundMore(true);
        return createDataSetComparison(tableComparison);
    }


    private DataSetComparison createDataSetComparison(TableComparison tableComparison) {
        DataSetComparison dataSetComparison = new DataSetComparison();
        dataSetComparison.addTableComparison(tableComparison);
        return dataSetComparison;
    }

    private RowComparison createRowComparisonWithDifferences(Object expectedValue1, Object actualValue1, Object expectedValue2, Object actualValue2) {
        DatabaseRow expectedDatabaseRow = createExpectedDatabaseRow(createDatabaseColumnWithValue("column_1", expectedValue1), createDatabaseColumnWithValue("column_2", expectedValue2));
        DatabaseRow actualDatabaseRow = createActualDatabaseRow("1", createDatabaseColumnWithValue("column_1", actualValue1), createDatabaseColumnWithValue("column_2", actualValue2));
        return new RowComparison(expectedDatabaseRow, actualDatabaseRow);
    }


    private DatabaseColumnWithValue createDatabaseColumnWithValue(String name, Object value) {
        return new DatabaseColumnWithValue(name, value, VARCHAR, null, false, false);
    }

    private DatabaseRow createExpectedDatabaseRow(DatabaseColumnWithValue... columns) {
        DatabaseRow row = new DatabaseRow("schema.table");
        for (DatabaseColumnWithValue column : columns) {
            row.addDatabaseColumnWithValue(column);
        }
        return row;
    }

    private DatabaseRow createActualDatabaseRow(String rowIdentifier, DatabaseColumnWithValue... columns) {
        DatabaseRow row = new DatabaseRow(rowIdentifier, "schema.table");
        for (DatabaseColumnWithValue column : columns) {
            row.addDatabaseColumnWithValue(column);
        }
        return row;
    }

}
