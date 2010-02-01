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
package org.unitils.dataset.loader.impl;

import org.junit.Before;
import org.unitils.UnitilsJUnit4;
import org.unitils.dataset.core.*;
import org.unitils.dataset.loader.DataSetLoader;
import org.unitils.mock.Mock;

import java.sql.*;
import java.util.LinkedHashSet;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public abstract class DataSetLoaderTestBase extends UnitilsJUnit4 {

    protected Mock<Database> database;
    protected Mock<Connection> connection;
    protected Mock<PreparedStatement> preparedStatement;
    protected Mock<ResultSet> primaryKeyResultSet;
    protected Mock<ParameterMetaData> parameterMetaData;

    protected DataSet dataSet;
    protected DataSet dataSetWithLiteralValues;
    protected DataSet dataSetWithEmptyTable;
    protected DataSet dataSetWithEmptyRows;
    protected DataSet dataSetWithVariableDeclarations;


    @Before
    public void initializeSchemas() {
        dataSet = createDataSet();
        dataSetWithLiteralValues = createDataSetWithLiteralValues();
        dataSetWithEmptyTable = createDataSetWithEmptyTable();
        dataSetWithEmptyRows = createDataSetWithEmptyRows();
        dataSetWithVariableDeclarations = createDataSetWithVariableDeclarations();
    }


    protected void initializeDataSetLoader(DataSetLoader dataSetLoader) throws Exception {
        database.returns(connection).createConnection();
        connection.returns(preparedStatement).prepareStatement(null);
        preparedStatement.returns(parameterMetaData).getParameterMetaData();
        dataSetLoader.init(database.getMock());
    }

    protected void initializePrimaryKeys(String... pkColumnNames) throws SQLException {
        for (String pkColumnName : pkColumnNames) {
            database.onceReturns(new LinkedHashSet<String>(asList(pkColumnName))).getPrimaryKeyColumnNames(null);
        }
    }

    protected void assertExceptionMessageContains(Exception e, String part) {
        assertTrue("Exception message did not contain " + part + ", message: " + e.getMessage(), e.getMessage().contains(part));
    }


    private DataSet createDataSet() {
        Schema schema = new Schema("my_schema", false);

        Table tableA = new Table("table_a", false);
        Row row1 = new Row();
        row1.addColumn(createColumn("column_1", "1"));
        row1.addColumn(createColumn("column_2", "2"));
        tableA.addRow(row1);
        Row row2 = new Row();
        row2.addColumn(createColumn("column_3", "3"));
        row2.addColumn(createColumn("column_4", "4"));
        tableA.addRow(row2);

        Table tableB = new Table("table_b", false);
        Row row3 = new Row();
        row3.addColumn(createColumn("column_5", "5"));
        row3.addColumn(createColumn("column_6", "6"));
        tableB.addRow(row3);

        schema.addTable(tableA);
        schema.addTable(tableB);

        return createDataSet(schema);
    }

    private DataSet createDataSetWithLiteralValues() {
        Schema schema = new Schema("my_schema", false);

        Table tableA = new Table("table_a", false);
        Row row = new Row();
        row.addColumn(createColumn("column_1", "=sysdate"));
        row.addColumn(createColumn("column_2", "=null"));
        row.addColumn(createColumn("column_3", "==escaped"));
        tableA.addRow(row);

        schema.addTable(tableA);
        return createDataSet(schema);
    }

    private DataSet createDataSetWithVariableDeclarations() {
        Schema schema = new Schema("my_schema", false);

        Table tableA = new Table("table_a", false);
        Row row = new Row();
        row.addColumn(createColumn("column_1", "value $0"));
        row.addColumn(createColumn("column_2", "$1$2"));
        row.addColumn(createColumn("column_3", "escaped $$1"));
        row.addColumn(createColumn("column_4", "=literal $1"));
        tableA.addRow(row);

        schema.addTable(tableA);
        return createDataSet(schema);
    }


    private DataSet createDataSetWithEmptyRows() {
        Schema schemaWithEmptyRows = new Schema("my_schema", false);
        Table tableWithEmptyRows = new Table("table_a", false);
        tableWithEmptyRows.addRow(new Row());
        tableWithEmptyRows.addRow(new Row());
        schemaWithEmptyRows.addTable(tableWithEmptyRows);
        return createDataSet(schemaWithEmptyRows);
    }

    private DataSet createDataSetWithEmptyTable() {
        Schema schemaWithEmptyTable = new Schema("my_schema", false);
        schemaWithEmptyTable.addTable(new Table("table_a", false));
        return createDataSet(schemaWithEmptyTable);
    }

    protected DataSet createDataSet(Schema schema) {
        DataSet dataSet = new DataSet('=', '$');
        dataSet.addSchema(schema);
        return dataSet;
    }

    protected Column createColumn(String name, String value) {
        return new Column(name, value, false);
    }
}