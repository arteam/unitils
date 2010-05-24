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
package org.unitils.dataset.loader.impl;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.dataset.core.DataSetRowProcessor;
import org.unitils.dataset.core.DatabaseColumn;
import org.unitils.dataset.core.DatabaseRow;
import org.unitils.dataset.core.Value;
import org.unitils.dataset.util.DatabaseAccessor;
import org.unitils.mock.Mock;

import static java.sql.Types.VARCHAR;
import static java.util.Arrays.asList;

/**
 * Tests for creating using insert statements for loading data rows
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class InsertDataSetLoaderLoadDatabaseRowTest extends UnitilsJUnit4 {

    /* Tested object */
    private InsertDataSetLoader insertDataSetLoader = new InsertDataSetLoader();

    private Mock<DataSetRowProcessor> dataSetRowProcessor;
    private Mock<DatabaseAccessor> databaseAccessor;

    private DatabaseRow databaseRow;

    private Value databaseColumnPk;
    private Value databaseColumn;
    private Value databaseColumnLiteral;


    @Before
    public void initialize() throws Exception {
        insertDataSetLoader.init(dataSetRowProcessor.getMock(), databaseAccessor.getMock());

        databaseRow = new DatabaseRow("my_schema.table_a");
        databaseColumnPk = new Value("1", false, new DatabaseColumn("column_1", VARCHAR, true));
        databaseColumn = new Value("2", false, new DatabaseColumn("column_2", VARCHAR, false));
        databaseColumnLiteral = new Value("literal", true, new DatabaseColumn("column_3", VARCHAR, false));
    }


    @Test
    public void loadDatabaseRow() throws Exception {
        databaseRow.addDatabaseColumnWithValue(databaseColumnPk);
        databaseRow.addDatabaseColumnWithValue(databaseColumn);

        insertDataSetLoader.loadDatabaseRow(databaseRow);
        databaseAccessor.assertInvoked().executeUpdate("insert into my_schema.table_a (column_1, column_2) values (?, ?)", asList(databaseColumnPk, databaseColumn));
    }

    @Test
    public void databaseRowWithLiteralValue() throws Exception {
        databaseRow.addDatabaseColumnWithValue(databaseColumnPk);
        databaseRow.addDatabaseColumnWithValue(databaseColumnLiteral);

        insertDataSetLoader.loadDatabaseRow(databaseRow);
        databaseAccessor.assertInvoked().executeUpdate("insert into my_schema.table_a (column_1, column_3) values (?, literal)", asList(databaseColumnPk));
    }


    //todo move to somewhere else
//    @Test
//    public void insertDataSetWithVariableDeclarations() throws Exception {
//        insertDataSetRowLoader.load(dataSetWithVariableDeclarations, asList("1", "2", "3"));
//
//        databaseAccessor.assertInvoked().executeUpdate("insert into my_schema.table_a (column_1, column_2, column_3, column_4) values (?, ?, ?, literal 2)");
//        preparedStatement.assertInvoked().setObject(1, "value 1", VARCHAR);
//        preparedStatement.assertInvoked().setObject(2, "23", VARCHAR);
//        preparedStatement.assertInvoked().setObject(3, "escaped $1", VARCHAR);
//    }
//    @Test
//    public void exceptionDuringLoadingOfRow() throws Exception {
//        databaseAccessor.raises(SQLException.class).executeUpdate(null, null);
//        try {
//            insertDataSetRowLoader.load(databaseRow);
//            fail("Exception expected");
//        } catch (Exception e) {
//            assertExceptionMessageContains(e, "my_schema");
//            assertExceptionMessageContains(e, "table_a");
//            assertExceptionMessageContains(e, "column_1=\"1\"");
//            assertExceptionMessageContains(e, "column_2=\"2\"");
//        }
//    }
}