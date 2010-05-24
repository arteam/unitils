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
import org.unitils.dataset.core.Column;
import org.unitils.dataset.core.Row;
import org.unitils.dataset.core.Value;
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

    private Row row1;
    private Row row2;

    @Before
    public void initialize() throws Exception {
        Column column1 = new Column("pk1", VARCHAR, true);
        Column column2 = new Column("column", VARCHAR, false);
        database.returns(asSet("pk1")).getPrimaryKeyColumnNames("schema.table");
        database.returns(asList(column1, column2)).getColumns("schema.table");

        tableContentRetriever.returns(tableContent).getTableContents("schema.table", asList(column1, column2), asSet("pk1"));
        tableContent.returns(2).getNrOfColumns();
        tableContent.returns(asList("column1", "column2")).getColumnNames();

        defaultDatabaseContentRetriever.init(database.getMock(), tableContentRetriever.getMock());

        tableComparison = new TableComparison("schema.table");
        dataSetComparison = createDataSetComparison(tableComparison);

        row1 = new Row("1", "schema.table");
        row1.addValue(new Value("row1col1", false, new Column("column1", VARCHAR, true)));
        row1.addValue(new Value("row1col2", false, new Column("column2", VARCHAR, true)));
        row2 = new Row("2", "schema.table");
        row2.addValue(new Value("row2col1", false, new Column("column1", VARCHAR, true)));
        row2.addValue(new Value("row2col2", false, new Column("column2", VARCHAR, true)));
    }


    @Test
    public void getContent() throws Exception {
        tableContent.onceReturns(row1).getRow();
        tableContent.onceReturns(row2).getRow();

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
        tableContent.onceReturns(row1).getRow();
        tableContent.onceReturns(row2).getRow();

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
        tableComparison.setMatchingRow(new RowComparison(new Row(null), new Row(identifier, null)));
    }


}