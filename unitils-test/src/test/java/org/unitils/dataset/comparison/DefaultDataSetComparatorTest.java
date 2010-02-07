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
import org.unitils.dataset.comparison.impl.ComparisonResultSet;
import org.unitils.dataset.comparison.impl.DefaultDataSetComparator;
import org.unitils.dataset.comparison.impl.RowComparator;
import org.unitils.dataset.core.*;
import org.unitils.dataset.loader.impl.Database;
import org.unitils.mock.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DefaultDataSetComparatorTest extends UnitilsJUnit4 {

    /* Tested object */
    private DefaultDataSetComparator defaultDataSetComparator = new TestDefaultDataSetComparator();

    private Mock<Database> database;
    private Mock<RowComparator> rowComparator;
    private Mock<ComparisonResultSet> comparisonResultSet;

    protected Row row;
    protected DataSet dataSet;
    protected DataSet emptyDataSet;

    protected List<String> emptyVariables = new ArrayList<String>();


    @Before
    public void initialize() throws Exception {
        rowComparator.returns(comparisonResultSet).compareRowWithDatabase(null, null);
        defaultDataSetComparator.init(database.getMock());

        row = createRow();
        dataSet = createDataSet(row);
        emptyDataSet = createEmptyDataSet();
    }


    @Test
    public void firstRowIsAMatch() throws Exception {
        setResultSetRows("row1col1", "row1col1", "row1col2", "row1col2",
                "row2col1", "xxxx", "row2col2", "yyyy");
        DataSetComparison dataSetComparison = defaultDataSetComparator.compare(dataSet, emptyVariables);
        assertTrue(dataSetComparison.isMatch());
    }

    @Test
    public void lastRowIsAMatch() throws Exception {
        setResultSetRows("row1col1", "xxxx", "row1col2", "yyyy",
                "row2col1", "row2col1", "row2col2", "row2col2");
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
    public void lastRowIsBetterMatch() throws Exception {
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
        assertEquals("my_schema", schemaComparison.getDataSetSchema().getName());
        assertEquals("table_a", tableComparison.getDataSetTable().getName());
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
        comparisonResultSet.onceReturns(true).next();
        comparisonResultSet.onceReturns("row1").getRowIdentifier();
        setResultSetRowValues(expectedRow1Col1, actualRow1Col1, expectedRow1Col2, actualRow1Col2);
        comparisonResultSet.onceReturns(true).next();
        comparisonResultSet.onceReturns("row2").getRowIdentifier();
        setResultSetRowValues(expectedRow2Col1, actualRow2Col1, expectedRow2Col2, actualRow2Col2);
        comparisonResultSet.onceReturns(false).next();
        // second run finds the best comparisons
        comparisonResultSet.onceReturns(true).next();
        comparisonResultSet.onceReturns("row1").getRowIdentifier();
        setResultSetRowValues(expectedRow1Col1, actualRow1Col1, expectedRow1Col2, actualRow1Col2);
        comparisonResultSet.onceReturns(true).next();
        comparisonResultSet.onceReturns("row2").getRowIdentifier();
        setResultSetRowValues(expectedRow2Col1, actualRow2Col1, expectedRow2Col2, actualRow2Col2);
        comparisonResultSet.onceReturns(false).next();
    }

    private void setResultSetRowValues(String expectedCol1, String actualCol1, String expectedCol2, String actualCol2) throws Exception {
        RowComparison rowComparison = new RowComparison(row);
        rowComparison.addColumnComparison(createColumnComparison(expectedCol1, actualCol1));
        rowComparison.addColumnComparison(createColumnComparison(expectedCol2, actualCol2));
        comparisonResultSet.onceReturns(rowComparison).getRowComparison(null);
    }

    private ColumnComparison createColumnComparison(String expectedValue, String actualValue) {
        return new ColumnComparison(null, expectedValue, actualValue, false);
    }


    private void assertColumnComparison(int index, String expectedValue, String actualValue, DataSetComparison dataSetComparison) {
        SchemaComparison schemaComparison = dataSetComparison.getSchemaComparisons().get(0);
        TableComparison tableComparison = schemaComparison.getTableComparisons().get(0);
        RowComparison rowComparison = tableComparison.getBestRowComparisons().get(0);
        ColumnComparison columnComparison = rowComparison.getColumnComparisons().get(index);

        assertEquals(expectedValue, columnComparison.getExpectedValue());
        assertEquals(actualValue, columnComparison.getActualValue());
    }

    private DataSet createDataSet(Row row) {
        Schema schema = new Schema("my_schema", false);
        Table table = new Table("table_a", false);
        table.addRow(row);
        schema.addTable(table);
        return createDataSet(schema);
    }

    private Row createRow() {
        Row row = new Row();
        row.addColumn(createColumn("column_1", "1"));
        row.addColumn(createColumn("column_2", "2"));
        return row;
    }

    private DataSet createEmptyDataSet() {
        Schema schema = new Schema("my_schema", false);
        schema.addTable(new Table("table_a", false));
        return createDataSet(schema);
    }

    private DataSet createDataSet(Schema schema) {
        DataSet dataSet = new DataSet('=', '$');
        dataSet.addSchema(schema);
        return dataSet;
    }

    private Column createColumn(String name, String value) {
        return new Column(name, value, false);
    }

    private class TestDefaultDataSetComparator extends DefaultDataSetComparator {
        @Override
        protected RowComparator createRowComparator() {
            return rowComparator.getMock();
        }
    }

}