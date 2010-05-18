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
import org.unitils.dataset.comparison.impl.DefaultDatabaseContentLogger;
import org.unitils.dataset.comparison.impl.TableContentRetriever;
import org.unitils.dataset.comparison.impl.TableContents;
import org.unitils.dataset.core.DatabaseColumn;
import org.unitils.dataset.core.DatabaseColumnWithValue;
import org.unitils.dataset.core.DatabaseRow;
import org.unitils.dataset.loader.impl.Database;
import org.unitils.mock.Mock;

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

    protected DataSetComparison dataSetComparison;
    protected TableComparison tableComparison;

    private DatabaseRow databaseRow1;
    private DatabaseRow databaseRow2;

    @Before
    public void initialize() throws Exception {
        DatabaseColumn databaseColumn1 = new DatabaseColumn("pk1", VARCHAR, null, true);
        DatabaseColumn databaseColumn2 = new DatabaseColumn("column", VARCHAR, null, false);
        database.returns(asSet("pk1")).getPrimaryKeyColumnNames("schema.table");
        database.returns(asList(databaseColumn1, databaseColumn2)).getDatabaseColumns("schema.table");

        tableContentRetriever.returns(tableContent).getTableContents("schema.table", asList(databaseColumn1, databaseColumn2), asSet("pk1"));
        tableContent.returns(2).getNrOfColumns();
        tableContent.returns(asList("column1", "column2")).getColumnNames();

        defaultDatabaseContentRetriever.init(database.getMock(), tableContentRetriever.getMock());

        tableComparison = new TableComparison("schema.table");
        dataSetComparison = createDataSetComparison(tableComparison);

        databaseRow1 = new DatabaseRow("1", "schema.table");
        databaseRow1.addDatabaseColumnWithValue(new DatabaseColumnWithValue("column1", "row1col1", VARCHAR, null, false, true));
        databaseRow1.addDatabaseColumnWithValue(new DatabaseColumnWithValue("column2", "row1col2", VARCHAR, null, false, true));
        databaseRow2 = new DatabaseRow("2", "schema.table");
        databaseRow2.addDatabaseColumnWithValue(new DatabaseColumnWithValue("column1", "row2col1", VARCHAR, null, false, true));
        databaseRow2.addDatabaseColumnWithValue(new DatabaseColumnWithValue("column2", "row2col2", VARCHAR, null, false, true));
    }


    @Test
    public void getContent() throws Exception {
        tableContent.onceReturns(databaseRow1).getDatabaseRow();
        tableContent.onceReturns(databaseRow2).getDatabaseRow();

        String result = defaultDatabaseContentRetriever.getDatabaseContentForComparison(dataSetComparison);
        assertEquals("schema.table\n" +
                "   column1   column2   \n" +
                "   row1col1  row1col2  \n" +
                "   row2col1  row2col2  \n", result);
    }

    @Test
    public void emptyTable() throws Exception {
        String result = defaultDatabaseContentRetriever.getDatabaseContentForComparison(dataSetComparison);

        assertEquals("schema.table\n" +
                "   <empty table>", result);
    }

    @Test
    public void rowsWithExactMatch() throws Exception {
        setActualRowIdentifiersWithMatch("1");
        tableContent.onceReturns(databaseRow1).getDatabaseRow();
        tableContent.onceReturns(databaseRow2).getDatabaseRow();

        String result = defaultDatabaseContentRetriever.getDatabaseContentForComparison(dataSetComparison);
        assertEquals("schema.table\n" +
                "   column1   column2   \n" +
                "-> row1col1  row1col2  \n" +
                "   row2col1  row2col2  \n", result);
    }


    private DataSetComparison createDataSetComparison(TableComparison tableComparison) {
        DataSetComparison dataSetComparison = new DataSetComparison();
        dataSetComparison.addTableComparison(tableComparison);
        return dataSetComparison;
    }

    private void setActualRowIdentifiersWithMatch(String identifier) {
        tableComparison.setMatchingRow(new RowComparison(new DatabaseRow(null), new DatabaseRow(identifier, null)));
    }


}