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
import org.unitils.dataset.comparison.impl.DefaultDatabaseContentRetriever;
import org.unitils.dataset.core.Column;
import org.unitils.dataset.core.Row;
import org.unitils.dataset.core.Schema;
import org.unitils.dataset.core.Table;
import org.unitils.dataset.loader.impl.Database;
import org.unitils.dataset.util.DatabaseAccessor;
import org.unitils.mock.Mock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import static org.junit.Assert.assertEquals;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DefaultDatabaseContentRetrieverTest extends UnitilsJUnit4 {

    /* Tested object */
    private DefaultDatabaseContentRetriever defaultDatabaseContentRetriever = new DefaultDatabaseContentRetriever();

    private Mock<Database> database;
    private Mock<DatabaseAccessor> databaseAccessor;
    private Mock<Connection> connection;
    private Mock<PreparedStatement> preparedStatement;
    private Mock<ResultSet> resultSet;
    private Mock<ResultSetMetaData> resultSetMetaData;
    protected Mock<ResultSet> primaryKeyResultSet;

    protected DataSetComparison dataSetComparison;
    protected TableComparison3 tableComparison;


    @Before
    public void initialize() throws Exception {
        database.returns(connection).getConnection();
        connection.returns(preparedStatement).prepareStatement(null);
        preparedStatement.returns(resultSet).executeQuery();
        resultSet.returns(resultSetMetaData).getMetaData();
        connection.returns(primaryKeyResultSet).getMetaData().getPrimaryKeys(null, null, null);
        defaultDatabaseContentRetriever.init(database.getMock(), databaseAccessor.getMock());

        tableComparison = createTableComparison();
        dataSetComparison = createDataSetComparison(tableComparison);
    }


    @Test
    public void getContent() throws Exception {
        resultSetMetaData.returns(2).getColumnCount();
        resultSetMetaData.returns("column1").getColumnName(1);
        resultSetMetaData.returns("column2").getColumnName(2);
        resultSet.onceReturns(true).next();
        resultSet.onceReturns("row1col1").getString(1);
        resultSet.onceReturns("row1col2").getString(2);
        resultSet.onceReturns(true).next();
        resultSet.onceReturns("row2col1").getString(1);
        resultSet.onceReturns("row2col2").getString(2);

        String result = defaultDatabaseContentRetriever.getActualDatabaseContentForDataSetComparison(dataSetComparison);
        assertEquals("my_schema.table_a\n" +
                "   column1   column2   \n" +
                "   row1col1  row1col2  \n" +
                "   row2col1  row2col2  \n", result);
    }

    @Test
    public void emptyTable() throws Exception {
        resultSetMetaData.returns(2).getColumnCount();
        resultSetMetaData.returns("column1").getColumnName(1);
        resultSetMetaData.returns("column2").getColumnName(2);
        String result = defaultDatabaseContentRetriever.getActualDatabaseContentForDataSetComparison(dataSetComparison);

        assertEquals("my_schema.table_a\n<empty table>", result);
    }

    @Test
    public void rowsWithExactMatch() throws Exception {
        setActualRowIdentifiersWithMatch("1");

        resultSetMetaData.returns(1).getColumnCount();
        resultSetMetaData.returns("column1").getColumnName(1);
        resultSet.onceReturns(true).next();
        resultSet.onceReturns("row1col1").getString(1);

        String result = defaultDatabaseContentRetriever.getActualDatabaseContentForDataSetComparison(dataSetComparison);

        assertEquals("my_schema.table_a\n" +
                "   column1   \n" +
                "-> row1col1  \n", result);
    }


    @Test
    public void noColumns() throws Exception {
        String result = defaultDatabaseContentRetriever.getActualDatabaseContentForDataSetComparison(dataSetComparison);
        assertEquals("", result);
    }


    private TableComparison3 createTableComparison() {
        Schema schema = new Schema("my_schema", false);
        Table table = new Table("table_a", false);
        schema.addTable(table);
        return new TableComparison3(table);
    }

    private DataSetComparison createDataSetComparison(TableComparison3 tableComparison) {
        DataSetComparison dataSetComparison = new DataSetComparison();
//        dataSetComparison.addSchemaComparison(schemaComparison);
        return dataSetComparison;
    }

    private Row createRow() {
        Row row = new Row();
        row.addColumn(createColumn("column_1", "1"));
        return row;
    }

    private Column createColumn(String name, String value) {
        return new Column(name, value, false);
    }

    private void setActualRowIdentifiersWithMatch(String identifier) {
        tableComparison.replaceIfBetterRowComparison(identifier, new RowComparison2(new Row()));
    }


}