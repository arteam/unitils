/*
 * Copyright Unitils.org
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
import org.unitils.dataset.comparison.impl.DefaultDataSetComparator;
import org.unitils.dataset.comparison.impl.TableContentRetriever;
import org.unitils.dataset.comparison.impl.TableContents;
import org.unitils.dataset.core.*;
import org.unitils.dataset.factory.DataSetRowSource;
import org.unitils.dataset.loader.impl.Database;
import org.unitils.mock.Mock;

import java.util.ArrayList;
import java.util.List;

import static java.sql.Types.INTEGER;
import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DefaultDataSetComparatorRowsTest extends UnitilsJUnit4 {

    /* Tested object */
    private DefaultDataSetComparator defaultDataSetComparator = new DefaultDataSetComparator();

    protected Mock<Database> database;
    protected Mock<DataSetRowProcessor> dataSetRowProcessor;
    protected Mock<TableContentRetriever> tableContentRetriever;
    protected Mock<TableContents> tableContents;
    protected Mock<DataSetRowSource> dataSetRowSource;

    protected List<String> emptyVariables = new ArrayList<String>();

    protected DatabaseRow actualDatabaseRow1;
    protected DatabaseRow actualDatabaseRow2;
    protected DatabaseRow actualDatabaseRow3;

    @Before
    public void initialize() throws Exception {
        defaultDataSetComparator.init(dataSetRowProcessor.getMock(), tableContentRetriever.getMock(), database.getMock());

        tableContentRetriever.onceReturns(tableContents).getTableContents(null, null, null);

        actualDatabaseRow1 = new DatabaseRow("1", "schema.table");
        actualDatabaseRow1.addDatabaseColumnWithValue(new DatabaseColumnWithValue("column1", 11, INTEGER, false, true));
        actualDatabaseRow1.addDatabaseColumnWithValue(new DatabaseColumnWithValue("column2", 12, INTEGER, false, true));
        actualDatabaseRow1.addDatabaseColumnWithValue(new DatabaseColumnWithValue("column3", 13, INTEGER, false, true));

        actualDatabaseRow2 = new DatabaseRow("2", "schema.table");
        actualDatabaseRow2.addDatabaseColumnWithValue(new DatabaseColumnWithValue("column1", 21, INTEGER, false, true));
        actualDatabaseRow2.addDatabaseColumnWithValue(new DatabaseColumnWithValue("column2", 22, INTEGER, false, true));
        actualDatabaseRow2.addDatabaseColumnWithValue(new DatabaseColumnWithValue("column3", 23, INTEGER, false, true));

        actualDatabaseRow3 = new DatabaseRow("3", "schema.table");
        actualDatabaseRow3.addDatabaseColumnWithValue(new DatabaseColumnWithValue("column1", 31, INTEGER, false, true));
        actualDatabaseRow3.addDatabaseColumnWithValue(new DatabaseColumnWithValue("column2", 32, INTEGER, false, true));
        actualDatabaseRow3.addDatabaseColumnWithValue(new DatabaseColumnWithValue("column3", 33, INTEGER, false, true));
    }


    @Test
    public void allRowsAreMatches() throws Exception {
        setExpectedRow(createExpectedDatabaseRow(createColumn("column1", 11), createColumn("column2", 12)));
        setExpectedRow(createExpectedDatabaseRow(createColumn("column1", 21), createColumn("column2", 22), createColumn("column3", 23)));
        setActualRow(actualDatabaseRow1);
        setActualRow(actualDatabaseRow2);
        setActualRow(actualDatabaseRow3);

        DataSetComparison dataSetComparison = defaultDataSetComparator.compare(dataSetRowSource.getMock(), emptyVariables);
        assertTrue(dataSetComparison.isMatch());
        assertRowMatch(true, "1", dataSetComparison);
        assertRowMatch(true, "2", dataSetComparison);
        assertRowMatch(false, "3", dataSetComparison);
    }

    @Test
    public void firstRowIsAMatch() throws Exception {
        setExpectedRow(createExpectedDatabaseRow(createColumn("column1", 11)));
        setActualRow(actualDatabaseRow1);
        setActualRow(actualDatabaseRow2);

        DataSetComparison dataSetComparison = defaultDataSetComparator.compare(dataSetRowSource.getMock(), emptyVariables);
        assertTrue(dataSetComparison.isMatch());
        assertRowMatch(true, "1", dataSetComparison);
    }

    @Test
    public void lastRowIsAMatch() throws Exception {
        setExpectedRow(createExpectedDatabaseRow(createColumn("column1", 21)));
        setActualRow(actualDatabaseRow1);
        setActualRow(actualDatabaseRow2);

        DataSetComparison dataSetComparison = defaultDataSetComparator.compare(dataSetRowSource.getMock(), emptyVariables);
        assertTrue(dataSetComparison.isMatch());
        assertRowMatch(true, "2", dataSetComparison);
    }

    @Test
    public void missingRow() throws Exception {
        setExpectedRow(createExpectedDatabaseRow(createColumn("column1", 11)));
        setExpectedRow(createExpectedDatabaseRow(createColumn("column1", 21)));
        setActualRow(actualDatabaseRow1);

        DataSetComparison dataSetComparison = defaultDataSetComparator.compare(dataSetRowSource.getMock(), emptyVariables);
        assertNrOfMissingRows(1, dataSetComparison);
    }

    @Test
    public void difference() throws Exception {
        setExpectedRow(createExpectedDatabaseRow(createColumn("column1", 777), createColumn("column2", 888), createColumn("column3", 999)));
        setActualRow(actualDatabaseRow1);

        DataSetComparison dataSetComparison = defaultDataSetComparator.compare(dataSetRowSource.getMock(), emptyVariables);
        assertColumnDifference(0, "column1", 777, 11, dataSetComparison);
        assertColumnDifference(1, "column2", 888, 12, dataSetComparison);
        assertColumnDifference(2, "column3", 999, 13, dataSetComparison);
    }


    @Test
    public void allRowsHaveDifferences() throws Exception {
        DatabaseRow expectedDatabaseRow1 = createExpectedDatabaseRow(createColumn("column1", 999), createColumn("column2", 999));
        DatabaseRow expectedDatabaseRow2 = createExpectedDatabaseRow(createColumn("column1", 999), createColumn("column2", 999), createColumn("column3", 999));
        setExpectedRow(expectedDatabaseRow1);
        setExpectedRow(expectedDatabaseRow2);
        setActualRow(actualDatabaseRow1);
        setActualRow(actualDatabaseRow2);
        setActualRow(actualDatabaseRow3);

        DataSetComparison dataSetComparison = defaultDataSetComparator.compare(dataSetRowSource.getMock(), emptyVariables);
        assertFalse(dataSetComparison.isMatch());
        assertBestRowComparison(expectedDatabaseRow1, actualDatabaseRow1, dataSetComparison);
        assertBestRowComparison(expectedDatabaseRow2, actualDatabaseRow1, dataSetComparison);
    }

    @Test
    public void firstRowIsBetterMatch() throws Exception {
        DatabaseRow expectedDatabaseRow1 = createExpectedDatabaseRow(createColumn("column1", 11), createColumn("column2", 999));
        setExpectedRow(expectedDatabaseRow1);
        setActualRow(actualDatabaseRow1);
        setActualRow(actualDatabaseRow2);

        DataSetComparison dataSetComparison = defaultDataSetComparator.compare(dataSetRowSource.getMock(), emptyVariables);
        assertFalse(dataSetComparison.isMatch());
        assertBestRowComparison(expectedDatabaseRow1, actualDatabaseRow1, dataSetComparison);
    }

    @Test
    public void lastRowIsBetterMatch() throws Exception {
        DatabaseRow expectedDatabaseRow1 = createExpectedDatabaseRow(createColumn("column1", 999), createColumn("column2", 22));
        setExpectedRow(expectedDatabaseRow1);
        setActualRow(actualDatabaseRow1);
        setActualRow(actualDatabaseRow2);

        DataSetComparison dataSetComparison = defaultDataSetComparator.compare(dataSetRowSource.getMock(), emptyVariables);
        assertFalse(dataSetComparison.isMatch());
        assertBestRowComparison(expectedDatabaseRow1, actualDatabaseRow2, dataSetComparison);
    }

    @Test
    public void expectedNoMoreRecordsInDatabase() throws Exception {
        setExpectedRow(createExpectedDatabaseRow(createColumn("column1", 11)));
        setExpectedRow(createExpectedDatabaseRow(createColumn("column1", 21)));
        setEmptyExpectedRow();
        setActualRow(actualDatabaseRow1);
        setActualRow(actualDatabaseRow2);

        DataSetComparison dataSetComparison = defaultDataSetComparator.compare(dataSetRowSource.getMock(), emptyVariables);
        assertTrue(dataSetComparison.isMatch());
        assertIsExpectedNoMoreRecordsButFoundMore(false, dataSetComparison);
    }

    @Test
    public void expectedNoMoreRecordsInDatabaseButFoundMore() throws Exception {
        setExpectedRow(createExpectedDatabaseRow(createColumn("column1", 11)));
        setExpectedRow(createExpectedDatabaseRow(createColumn("column1", 21)));
        setEmptyExpectedRow();
        setActualRow(actualDatabaseRow1);
        setActualRow(actualDatabaseRow2);
        setActualRow(actualDatabaseRow3);

        DataSetComparison dataSetComparison = defaultDataSetComparator.compare(dataSetRowSource.getMock(), emptyVariables);
        assertFalse(dataSetComparison.isMatch());
        assertIsExpectedNoMoreRecordsButFoundMore(true, dataSetComparison);
    }

    @Test
    public void noRowsInDataSet() throws Exception {
        setActualRow(actualDatabaseRow1);
        setActualRow(actualDatabaseRow2);

        DataSetComparison dataSetComparison = defaultDataSetComparator.compare(dataSetRowSource.getMock(), emptyVariables);
        assertTrue(dataSetComparison.isMatch());
    }

    @Test
    public void noRowsInTable() throws Exception {
        setExpectedRow(createExpectedDatabaseRow(createColumn("column1", 11)));

        DataSetComparison dataSetComparison = defaultDataSetComparator.compare(dataSetRowSource.getMock(), emptyVariables);
        assertFalse(dataSetComparison.isMatch());
    }

    @Test
    public void noRowsInTableAndOnlyEmptyElementInDataSet() throws Exception {
        setEmptyExpectedRow();

        DataSetComparison dataSetComparison = defaultDataSetComparator.compare(dataSetRowSource.getMock(), emptyVariables);
        assertTrue(dataSetComparison.isMatch());
    }


    private DatabaseColumnWithValue createColumn(String name, Object value) {
        return new DatabaseColumnWithValue(name, value, INTEGER, false, true);
    }

    private DatabaseRow createExpectedDatabaseRow(DatabaseColumnWithValue... databaseColumnWithValues) {
        DatabaseRow expectedDatabaseRow = new DatabaseRow("schema.table");
        for (DatabaseColumnWithValue databaseColumnWithValue : databaseColumnWithValues) {
            expectedDatabaseRow.addDatabaseColumnWithValue(databaseColumnWithValue);
        }
        return expectedDatabaseRow;
    }

    private void setExpectedRow(DatabaseRow databaseRow) throws Exception {
        dataSetRowSource.onceReturns(createDataSetRow()).getNextDataSetRow();
        dataSetRowProcessor.onceReturns(databaseRow).process(null, emptyVariables, null);
    }

    private void setEmptyExpectedRow() throws Exception {
        dataSetRowSource.onceReturns(createEmptyDataSetRow()).getNextDataSetRow();
        dataSetRowProcessor.onceReturns(createExpectedDatabaseRow()).process(null, emptyVariables, null);
    }

    private void setActualRow(DatabaseRow databaseRow) throws Exception {
        tableContents.onceReturns(databaseRow).getDatabaseRow();
    }


    private void assertNrOfMissingRows(int expectedNrOfMissingRows, DataSetComparison dataSetComparison) {
        TableComparison tableComparison = dataSetComparison.getTableComparisons().get(0);
        int nrOfMissingRows = tableComparison.getMissingRows().size();
        assertEquals("Found different nr of missing rows", expectedNrOfMissingRows, nrOfMissingRows);
    }

    private void assertRowMatch(boolean match, String rowIdentifier, DataSetComparison dataSetComparison) {
        TableComparison tableComparison = dataSetComparison.getTableComparisons().get(0);
        boolean result = tableComparison.isMatchingRow(rowIdentifier);
        assertEquals(match, result);
    }

    private void assertColumnDifference(int index, String columnName, Object expectedValue, Object actualValue, DataSetComparison dataSetComparison) {
        TableComparison tableComparison = dataSetComparison.getTableComparisons().get(0);
        RowComparison bestRowComparison = tableComparison.getBestRowComparisons().get(0);
        ColumnDifference columnDifference1 = bestRowComparison.getColumnDifferences().get(index);
        assertEquals(columnName, columnDifference1.getColumnName());
        assertEquals(expectedValue, columnDifference1.getExpectedValue());
        assertEquals(actualValue, columnDifference1.getActualValue());
    }

    private void assertBestRowComparison(DatabaseRow expectedDatabaseRow, DatabaseRow actualDatabaseRow, DataSetComparison dataSetComparison) {
        TableComparison tableComparison = dataSetComparison.getTableComparisons().get(0);
        RowComparison bestRowComparison = tableComparison.getBestRowComparison(expectedDatabaseRow);
        assertSame(expectedDatabaseRow, bestRowComparison.getExpectedDatabaseRow());
        assertSame(actualDatabaseRow, bestRowComparison.getActualDatabaseRow());
    }

    private void assertIsExpectedNoMoreRecordsButFoundMore(boolean expectedValue, DataSetComparison dataSetComparison) {
        TableComparison tableComparison = dataSetComparison.getTableComparisons().get(0);
        assertEquals(expectedValue, tableComparison.isExpectedNoMoreRecordsButFoundMore());
    }


    private DataSetRow createDataSetRow() {
        DataSetSettings dataSetSettings = new DataSetSettings('=', '$', false);
        DataSetRow dataSetRow = new DataSetRow("schema", "table", null, false, dataSetSettings);
        dataSetRow.addDataSetColumn(new DataSetColumn("column", "value"));
        return dataSetRow;
    }

    private DataSetRow createEmptyDataSetRow() {
        DataSetSettings dataSetSettings = new DataSetSettings('=', '$', false);
        return new DataSetRow("schema", "table", null, false, dataSetSettings);
    }

}