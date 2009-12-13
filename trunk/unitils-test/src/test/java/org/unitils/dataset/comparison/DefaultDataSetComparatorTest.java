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

import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DefaultDataSetComparatorTest extends UnitilsJUnit4 {

    /* Tested object */
    private DefaultDataSetComparator defaultDataSetComparator = new TestDefaultDataSetComparator();

    private Mock<DataSource> dataSource;
    private Mock<Connection> connection;
    private Mock<PreparedStatementWrapper> preparedStatementWrapper;
    private Mock<ResultSet> resultSet;

    protected DataSet dataSet;
    protected DataSet emptyDataSet;

    protected List<String> emptyVariables = new ArrayList<String>();


    @Before
    public void initialize() throws Exception {
        dataSource.returns(connection).getConnection();
        preparedStatementWrapper.returns(resultSet).executeQuery();
        defaultDataSetComparator.init(dataSource.getMock());

        dataSet = createDataSet();
        emptyDataSet = createEmptyDataSet();
    }


    @Test
    public void equal() throws Exception {
        setResultSetRows("row1col1", "row1col1", "row1col2", "row1col2",
                "row2col1", "xxxx", "row2col2", "yyyy");
        DataSetComparison dataSetComparison = defaultDataSetComparator.compare(dataSet, emptyVariables);
        assertTrue(dataSetComparison.isMatch());
    }

    @Test
    public void difference() throws Exception {
        setResultSetRows("row1col1", "xxxx", "row1col2", "yyyy",
                "row2col1", "xxxx", "row2col2", "yyyy");
        DataSetComparison dataSetComparison = defaultDataSetComparator.compare(dataSet, emptyVariables);
        assertColumnComparison(0, "row1col1", "xxxx", dataSetComparison);
        assertColumnComparison(1, "row1col2", "yyyy", dataSetComparison);
    }

    @Test
    public void firstRowIsBetterMatch() throws Exception {
        setResultSetRows("row1col1", "row1col1", "row1col2", "yyyy",
                "row2col1", "xxxx", "row2col2", "yyyy");
        DataSetComparison dataSetComparison = defaultDataSetComparator.compare(dataSet, emptyVariables);
        assertColumnComparison(1, "row1col2", "yyyy", dataSetComparison);
    }

    @Test
    public void secondRowIsBetterMatch() throws Exception {
        setResultSetRows("row1col1", "xxxx", "row1col2", "yyyy",
                "row2col1", "row2col1", "row2col2", "yyyy");
        DataSetComparison dataSetComparison = defaultDataSetComparator.compare(dataSet, emptyVariables);
        assertColumnComparison(1, "row2col2", "yyyy", dataSetComparison);
    }

    @Test
    public void emptyTable() throws Exception {
        DataSetComparison dataSetComparison = defaultDataSetComparator.compare(dataSet, emptyVariables);

        SchemaComparison schemaComparison = dataSetComparison.getSchemaComparisons().get(0);
        TableComparison tableComparison = schemaComparison.getTableComparisons().get(0);
        List<Row> missingRows = tableComparison.getMissingRows();

        assertFalse(dataSetComparison.isMatch());
        assertEquals("my_schema", schemaComparison.getName());
        assertEquals("table_a", tableComparison.getName());
        assertEquals(1, missingRows.size());
    }

    @Test
    public void emptyDataSet() throws Exception {
        setResultSetRows("row1col1", "row1col1", "row1col2", "row1col2", "row2col1", "xxxx", "row2col2", "yyyy");
        DataSetComparison dataSetComparison = defaultDataSetComparator.compare(dataSet, emptyVariables);
        assertTrue(dataSetComparison.isMatch());
    }


    private void setResultSetRows(String expectedRow1Col1, String actualRow1Col1, String expectedRow1Col2, String actualRow1Col2,
                                  String expectedRow2Col1, String actualRow2Col1, String expectedRow2Col2, String actualRow2Col2) throws Exception {
        // first run finds the matches
        resultSet.onceReturns(true).next();
        setResultSetRowValues(expectedRow1Col1, actualRow1Col1, expectedRow1Col2, actualRow1Col2);
        resultSet.onceReturns(true).next();
        setResultSetRowValues(expectedRow2Col1, actualRow2Col1, expectedRow2Col2, actualRow2Col2);
        resultSet.onceReturns(false).next();
        // second run finds the best comparisons
        resultSet.onceReturns(true).next();
        setResultSetRowValues(expectedRow1Col1, actualRow1Col1, expectedRow1Col2, actualRow1Col2);
        resultSet.onceReturns(true).next();
        setResultSetRowValues(expectedRow2Col1, actualRow2Col1, expectedRow2Col2, actualRow2Col2);
        resultSet.onceReturns(false).next();
    }

    private void setResultSetRowValues(String expectedCol1, String actualCol1, String expectedCol2, String actualCol2) throws Exception {
        resultSet.onceReturns(actualCol1).getString(1);
        resultSet.onceReturns(expectedCol1).getString(2);
        resultSet.onceReturns(actualCol2).getString(3);
        resultSet.onceReturns(expectedCol2).getString(4);
    }

    private void assertColumnComparison(int index, String expectedValue, String actualValue, DataSetComparison dataSetComparison) {
        SchemaComparison schemaComparison = dataSetComparison.getSchemaComparisons().get(0);
        TableComparison tableComparison = schemaComparison.getTableComparisons().get(0);
        RowComparison rowComparison = tableComparison.getBestRowComparisons().get(0);
        ColumnComparison columnComparison = rowComparison.getColumnComparisons().get(index);

        assertEquals(expectedValue, columnComparison.getExpectedValue());
        assertEquals(actualValue, columnComparison.getActualValue());
    }

    private DataSet createDataSet() {
        Table tableA = new Table("table_a");
        Row row1 = new Row();
        row1.addColumn(createColumn("column_1", "1"));
        row1.addColumn(createColumn("column_2", "2"));
        tableA.addRow(row1);

        Schema schema = new Schema("my_schema", false, new HashSet<String>());
        schema.addTable(tableA);
        return createDataSet(schema);
    }

    private DataSet createEmptyDataSet() {
        Schema schema = new Schema("my_schema", false, new HashSet<String>());
        schema.addTable(new Table("table_a"));
        return createDataSet(schema);
    }

    private DataSet createDataSet(Schema schema) {
        DataSet dataSet = new DataSet();
        dataSet.addSchema(schema);
        return dataSet;
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