/*
 * Copyright 2008,  Unitils.org
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
import org.unitils.dataset.core.*;
import org.unitils.dataset.util.PreparedStatementWrapper;
import org.unitils.mock.Mock;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DefaultDataSetComparatorRowsTest extends UnitilsJUnit4 {

    /* Tested object */
    private DefaultDataSetComparator defaultDataSetComparator = new TestDefaultDataSetComparator();

    private Mock<DataSource> dataSource;
    private Mock<Connection> connection;
    private Mock<PreparedStatementWrapper> preparedStatementWrapper;
    private Mock<ResultSet> resultSet;

    protected DataSet dataSetWith3Rows;
    protected DataSet dataSetWithEmptyRow;

    protected List<String> emptyVariables = new ArrayList<String>();


    @Before
    public void initialize() throws Exception {
        dataSource.returns(connection).getConnection();
        preparedStatementWrapper.returns(resultSet).executeQuery();
        defaultDataSetComparator.init(dataSource.getMock());

        dataSetWith3Rows = createDataSetWith3Rows();
        dataSetWithEmptyRow = createDataSetWithEmptyRow();
    }


    @Test
    public void allRowsAreMatches() throws Exception {
        // find matches: row 1
        setResultSetRow("1", "1");
        // find matches: row 2
        rowsSkipped(1);
        setResultSetRow("2", "2");
        // find matches: row 3
        rowsSkipped(2);
        setResultSetRow("3", "3");

        DataSetComparison dataSetComparison = defaultDataSetComparator.compare(dataSetWith3Rows, emptyVariables);
        assertTrue(dataSetComparison.isMatch());
    }

    @Test
    public void missingRow() throws Exception {
        // find matches: row 1
        setResultSetRow("1", "1");
        // find matches: row 2
        rowsSkipped(1);
        setResultSetRow("2", "2");
        // find matches: row 3
        resultSet.onceReturns(false).next();
        // find best comparisons: row 1
        // find best comparisons: row 2
        // find best comparisons: row 3
        rowsSkipped(2);
        resultSet.onceReturns(false).next();

        DataSetComparison dataSetComparison = defaultDataSetComparator.compare(dataSetWith3Rows, emptyVariables);
        assertNrOfMissingRows(1, dataSetComparison);
    }

    @Test
    public void differences() throws Exception {
        // find matches: row 1
        setResultSetRow("1", "1");
        // find matches: row 2
        rowsSkipped(1);
        setResultSetRow("2", "x", "3");
        resultSet.onceReturns(false).next();
        // find matches: row 3
        rowsSkipped(1);
        setResultSetRow("y", "x", "3");
        resultSet.onceReturns(false).next();
        // find best comparisons: row 1
        // find best comparisons: row 2
        rowsSkipped(1);
        setResultSetRow("2", "x", "3");
        resultSet.onceReturns(false).next();
        // find best comparisons: row 3
        rowsSkipped(1);
        setResultSetRow("y", "x", "3");
        resultSet.onceReturns(false).next();

        DataSetComparison dataSetComparison = defaultDataSetComparator.compare(dataSetWith3Rows, emptyVariables);
        assertRowMatch(0, dataSetComparison);
        assertRowDifference(1, "2", "x", dataSetComparison);
        assertRowDifference(2, "y", "x", dataSetComparison);
    }

    @Test
    public void expectedNoMoreRecordsInDatabaseButFoundMore() throws Exception {
        setResultSetRow("1", "1");
        DataSetComparison dataSetComparison = defaultDataSetComparator.compare(dataSetWithEmptyRow, emptyVariables);
        assertIsExpectedNoMoreRecordsButFoundMore(true, dataSetComparison);
    }

    @Test
    public void notExpectedNoMoreRecordsInDatabaseButFoundMore() throws Exception {
        setResultSetRow("1", "1");
        DataSetComparison dataSetComparison = defaultDataSetComparator.compare(dataSetWith3Rows, emptyVariables);
        assertIsExpectedNoMoreRecordsButFoundMore(false, dataSetComparison);
    }


    private void rowsSkipped(int nrRows) throws Exception {
        for (int i = 0; i < nrRows; i++) {
            resultSet.onceReturns(true).next();
        }
    }

    private void setResultSetRow(String expectedValue, String... actualValues) throws Exception {
        for (String actualValue : actualValues) {
            resultSet.onceReturns(true).next();
            resultSet.onceReturns(actualValue).getString(1);
            resultSet.onceReturns(expectedValue).getString(2);
        }
    }

    private void assertNrOfMissingRows(int expectedNrOfMissingRows, DataSetComparison dataSetComparison) {
        SchemaComparison schemaComparison = dataSetComparison.getSchemaComparisons().get(0);
        TableComparison tableComparison = schemaComparison.getTableComparisons().get(0);
        int nrOfMissingRows = tableComparison.getMissingRows().size();

        assertEquals("Found different nr of missing rows", expectedNrOfMissingRows, nrOfMissingRows);
    }

    private void assertRowMatch(int index, DataSetComparison dataSetComparison) {
        SchemaComparison schemaComparison = dataSetComparison.getSchemaComparisons().get(0);
        TableComparison tableComparison = schemaComparison.getTableComparisons().get(0);
        RowComparison rowComparison = tableComparison.getBestRowComparisons().get(index);

        assertTrue("Row comparison with index " + index + " was not a match", rowComparison.isMatch());
    }

    private void assertRowDifference(int index, String expectedValue, String actualValue, DataSetComparison dataSetComparison) {
        SchemaComparison schemaComparison = dataSetComparison.getSchemaComparisons().get(0);
        TableComparison tableComparison = schemaComparison.getTableComparisons().get(0);
        RowComparison rowComparison = tableComparison.getBestRowComparisons().get(index);
        ColumnComparison columnComparison = rowComparison.getColumnComparisons().get(0);

        assertEquals(expectedValue, columnComparison.getExpectedValue());
        assertEquals(actualValue, columnComparison.getActualValue());
    }

    private void assertIsExpectedNoMoreRecordsButFoundMore(boolean expectedValue, DataSetComparison dataSetComparison) {
        SchemaComparison schemaComparison = dataSetComparison.getSchemaComparisons().get(0);
        TableComparison tableComparison = schemaComparison.getTableComparisons().get(0);
        assertEquals(expectedValue, tableComparison.isExpectedNoMoreRecordsButFoundMore());
    }

    private DataSet createDataSetWith3Rows() {
        Table tableA = new Table("table_a");
        tableA.addRow(createRow());
        tableA.addRow(createRow());
        tableA.addRow(createRow());
        return createDataSet(tableA);
    }

    private DataSet createDataSetWithEmptyRow() {
        Table tableA = new Table("table_a");
        tableA.addRow(new Row());
        return createDataSet(tableA);
    }

    private DataSet createDataSet(Table table) {
        Schema schema = new Schema("my_schema", false, new HashSet<String>());
        schema.addTable(table);
        DataSet dataSet = new DataSet();
        dataSet.addSchema(schema);
        return dataSet;
    }

    private Row createRow() {
        Row row = new Row();
        row.addColumn(createColumn("column_1", "1"));
        return row;
    }

    private Column createColumn(String name, String value) {
        return new Column(name, value, false, '=', '$');
    }


    private class TestDefaultDataSetComparator extends DefaultDataSetComparator {
        @Override
        protected PreparedStatementWrapper createPreparedStatementWrapper(String schemaName, String tableName, Row row, List<String> variables, Connection connection) throws Exception {
            return preparedStatementWrapper.getMock();
        }
    }
}