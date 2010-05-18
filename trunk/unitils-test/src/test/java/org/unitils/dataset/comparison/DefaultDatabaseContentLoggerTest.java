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
import org.unitils.dataset.comparison.impl.DefaultDatabaseContentLogger;
import org.unitils.dataset.comparison.impl.TableContentRetriever;
import org.unitils.dataset.comparison.impl.TableContents;
import org.unitils.dataset.core.DatabaseColumn;
import org.unitils.dataset.loader.impl.Database;
import org.unitils.mock.Mock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import static java.sql.Types.VARCHAR;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.unitils.util.CollectionUtils.asSet;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DefaultDatabaseContentLoggerTest extends UnitilsJUnit4 {

    /* Tested object */
    private DefaultDatabaseContentLogger defaultDatabaseContentRetriever = new DefaultDatabaseContentLogger();

    protected Mock<Database> database;
    protected Mock<TableContentRetriever> tableContentRetriever;
    protected Mock<TableContents> tableContent;
    protected Mock<Connection> connection;
    protected Mock<PreparedStatement> preparedStatement;
    protected Mock<ResultSet> resultSet;
    protected Mock<ResultSetMetaData> resultSetMetaData;
    protected Mock<ResultSet> primaryKeyResultSet;

    protected DataSetComparison dataSetComparison;
    protected TableComparison tableComparison;


    @Before
    public void initialize() throws Exception {
        DatabaseColumn databaseColumn1 = new DatabaseColumn("pk1", VARCHAR, null, true);
        DatabaseColumn databaseColumn2 = new DatabaseColumn("column", VARCHAR, null, false);
        database.returns(asSet("pk1")).getPrimaryKeyColumnNames("schema.table");
        database.returns(asList(databaseColumn1, databaseColumn2)).getDatabaseColumns("schema.table");

        tableContentRetriever.returns(tableContent).getTableContents("schema.table", asList(databaseColumn1, databaseColumn2), asSet("pk1"));

        connection.returns(preparedStatement).prepareStatement(null);
        preparedStatement.returns(resultSet).executeQuery();
        resultSet.returns(resultSetMetaData).getMetaData();
        connection.returns(primaryKeyResultSet).getMetaData().getPrimaryKeys(null, null, null);
        defaultDatabaseContentRetriever.init(database.getMock(), tableContentRetriever.getMock());

        tableComparison = new TableComparison("schema.table");
        dataSetComparison = createDataSetComparison(tableComparison);
    }


    @Test
    public void getContent() throws Exception {
        //tableContentRetriever.onceReturns(DatabaseRow).getTableContents();
        resultSetMetaData.returns("column1").getColumnName(1);
        resultSetMetaData.returns("column2").getColumnName(2);
        resultSet.onceReturns(true).next();
        resultSet.onceReturns("row1col1").getString(1);
        resultSet.onceReturns("row1col2").getString(2);
        resultSet.onceReturns(true).next();
        resultSet.onceReturns("row2col1").getString(1);
        resultSet.onceReturns("row2col2").getString(2);

        String result = defaultDatabaseContentRetriever.getDatabaseContentForComparison(dataSetComparison);
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
        String result = defaultDatabaseContentRetriever.getDatabaseContentForComparison(dataSetComparison);

        assertEquals("my_schema.table_a\n<empty table>", result);
    }

    @Test
    public void rowsWithExactMatch() throws Exception {
        setActualRowIdentifiersWithMatch("1");

        resultSetMetaData.returns(1).getColumnCount();
        resultSetMetaData.returns("column1").getColumnName(1);
        resultSet.onceReturns(true).next();
        resultSet.onceReturns("row1col1").getString(1);

        String result = defaultDatabaseContentRetriever.getDatabaseContentForComparison(dataSetComparison);

        assertEquals("my_schema.table_a\n" +
                "   column1   \n" +
                "-> row1col1  \n", result);
    }


    @Test
    public void noColumns() throws Exception {
        String result = defaultDatabaseContentRetriever.getDatabaseContentForComparison(dataSetComparison);
        assertEquals("", result);
    }

    private DataSetComparison createDataSetComparison(TableComparison tableComparison) {
        DataSetComparison dataSetComparison = new DataSetComparison();
        dataSetComparison.addTableComparison(tableComparison);
        return dataSetComparison;
    }

    private void setActualRowIdentifiersWithMatch(String identifier) {
        //tableComparison.replaceIfBetterRowComparison(identifier, new RowComparison());
    }


}