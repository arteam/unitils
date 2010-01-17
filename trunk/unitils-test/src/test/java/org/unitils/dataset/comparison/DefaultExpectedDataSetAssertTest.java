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
import org.unitils.dataset.comparison.impl.*;
import org.unitils.dataset.core.Column;
import org.unitils.dataset.core.Row;
import org.unitils.mock.Mock;

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
            assertErrorMessageContains("Found differences for table schema_a.table_a", e);
            assertErrorMessageContains("Different database record found for data set row:  column_1=\"1\", column_2=\"2\"", e);
            assertErrorMessageContains("Different database record found for data set row:  column_3=\"3\", column_4=\"4\"", e);
            assertErrorMessageContains("Expected", e);
            assertErrorMessageContains("Actual", e);
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
            assertErrorMessageContains("Found differences for table schema_a.table_a", e);
            assertErrorMessageContains("No database record found for data set row:  column_1=\"1\", column_2=\"2\"", e);
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
            assertErrorMessageContains("Expected no more database records in table schema_a.table_a but found more records.", e);
            return;
        }
        fail("Expected an AssertionError"); //fail also raises assertion errors
    }


    private void assertErrorMessageContains(String message, AssertionError e) {
        String errorMessage = e.getMessage();
        assertTrue("Error message did not contain '" + message + "'. Error message: " + errorMessage, errorMessage.contains(message));
    }

    private DataSetComparison createMatchingDataSetComparison() {
        return createDataSetComparison(new TableComparison("table_a"));
    }

    private DataSetComparison createDataSetComparisonWithDifferences() {
        TableComparison tableComparison = new TableComparison("table_a");
        tableComparison.replaceIfBetterRowComparison("1", createRowDifference1());
        tableComparison.replaceIfBetterRowComparison("2", createRowDifference2());
        return createDataSetComparison(tableComparison);
    }

    private DataSetComparison createDataSetComparisonWithMissingRows() {
        TableComparison tableComparison = new TableComparison("table_a");
        tableComparison.addMissingRow(createRow(createColumn("column_1", "1"), createColumn("column_2", "2")));
        tableComparison.addMissingRow(createRow(createColumn("column_3", "5"), createColumn("column_4", "6")));
        return createDataSetComparison(tableComparison);
    }

    private DataSetComparison createDataSetComparisonWithExpectedButNotEmptyTable() {
        TableComparison tableComparison = new TableComparison("table_a");
        tableComparison.setExpectedNoMoreRecordsButFoundMore(true);
        return createDataSetComparison(tableComparison);
    }


    private DataSetComparison createDataSetComparison(TableComparison tableComparison) {
        SchemaComparison schemaComparison = new SchemaComparison("schema_a");
        schemaComparison.addTableComparison(tableComparison);

        DataSetComparison dataSetComparison = new DataSetComparison();
        dataSetComparison.addSchemaComparison(schemaComparison);
        return dataSetComparison;
    }

    private RowComparison createRowDifference1() {
        Column column1 = createColumn("column_1", "1");
        Column column2 = createColumn("column_2", "2");
        RowComparison rowComparison = new RowComparison(createRow(column1, column2));
        rowComparison.addColumnComparison(new ColumnComparison(column1, "1", "x"));
        rowComparison.addColumnComparison(new ColumnComparison(column2, "2", "2"));
        return rowComparison;
    }

    private RowComparison createRowDifference2() {
        Column column3 = createColumn("column_3", "3");
        Column column4 = createColumn("column_4", "4");
        RowComparison rowComparison = new RowComparison(createRow(column3, column4));
        rowComparison.addColumnComparison(new ColumnComparison(column3, "3", "3"));
        rowComparison.addColumnComparison(new ColumnComparison(column4, "4", "y"));
        return rowComparison;
    }

    private Column createColumn(String name, String value) {
        return new Column(name, value, false, '=', '$');
    }

    private Row createRow(Column... columns) {
        Row row = new Row();
        for (Column column : columns) {
            row.addColumn(column);
        }
        return row;
    }

}
