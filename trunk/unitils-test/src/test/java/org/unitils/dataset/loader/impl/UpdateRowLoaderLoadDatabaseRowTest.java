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
import org.unitils.core.UnitilsException;
import org.unitils.dataset.core.DatabaseColumn;
import org.unitils.dataset.core.DatabaseRow;
import org.unitils.dataset.core.Value;
import org.unitils.dataset.util.DatabaseAccessor;
import org.unitils.mock.Mock;

import static java.sql.Types.VARCHAR;
import static java.util.Arrays.asList;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class UpdateRowLoaderLoadDatabaseRowTest extends UnitilsJUnit4 {

    /* Tested object */
    private UpdateDataSetLoader updateDataSetLoader = new UpdateDataSetLoader();

    private Mock<DatabaseAccessor> databaseAccessor;

    private DatabaseRow databaseRow;
    private Value databaseColumnPk1;
    private Value databaseColumnPk2;
    private Value databaseColumnPkLiteral;
    private Value databaseColumn;
    private Value databaseColumnLiteral;


    @Before
    public void initialize() throws Exception {
        databaseAccessor.returns(1).executeUpdate(null, null);
        updateDataSetLoader.init(null, databaseAccessor.getMock());

        databaseRow = new DatabaseRow("my_schema.table_a");
        databaseColumnPk1 = new Value("1", false, new DatabaseColumn("pk1", VARCHAR, true));
        databaseColumnPk2 = new Value("2", false, new DatabaseColumn("pk2", VARCHAR, true));
        databaseColumnPkLiteral = new Value("pk-literal", true, new DatabaseColumn("pk3", VARCHAR, true));
        databaseColumn = new Value("3", false, new DatabaseColumn("column_1", VARCHAR, false));
        databaseColumnLiteral = new Value("literal", true, new DatabaseColumn("column_2", VARCHAR, false));
    }


    @Test
    public void loadDatabaseRow() throws Exception {
        databaseRow.addDatabaseColumnWithValue(databaseColumnPk1);
        databaseRow.addDatabaseColumnWithValue(databaseColumn);
        databaseRow.addDatabaseColumnWithValue(databaseColumnPk2);

        updateDataSetLoader.loadDatabaseRow(databaseRow);
        databaseAccessor.assertInvoked().executeUpdate("update my_schema.table_a set pk1=?, column_1=?, pk2=? where pk1=?, pk2=?", asList(databaseColumnPk1, databaseColumn, databaseColumnPk2, databaseColumnPk1, databaseColumnPk2));
    }

    @Test
    public void literalColumns() throws Exception {
        databaseRow.addDatabaseColumnWithValue(databaseColumnPk1);
        databaseRow.addDatabaseColumnWithValue(databaseColumnLiteral);
        databaseRow.addDatabaseColumnWithValue(databaseColumnPkLiteral);

        updateDataSetLoader.loadDatabaseRow(databaseRow);
        databaseAccessor.assertInvoked().executeUpdate("update my_schema.table_a set pk1=?, column_2=literal, pk3=pk-literal where pk1=?, pk3=pk-literal", asList(databaseColumnPk1, databaseColumnPk1));
    }

    @Test(expected = UnitilsException.class)
    public void noRecordWasUpdated() throws Exception {
        databaseRow.addDatabaseColumnWithValue(databaseColumnPk1);

        databaseAccessor.onceReturns(0).executeUpdate(null, null);
        updateDataSetLoader.loadDatabaseRow(databaseRow);
    }

    @Test
    public void onlyValuesForPkColumns() throws Exception {
        databaseRow.addDatabaseColumnWithValue(databaseColumnPk1);
        databaseRow.addDatabaseColumnWithValue(databaseColumnPk2);

        updateDataSetLoader.loadDatabaseRow(databaseRow);
        databaseAccessor.assertInvoked().executeUpdate("update my_schema.table_a set pk1=?, pk2=? where pk1=?, pk2=?", asList(databaseColumnPk1, databaseColumnPk2, databaseColumnPk1, databaseColumnPk2));
    }


    // todo move test
//
//    @Test
//    public void caseSensitive() throws Exception {
//        updateDataSetLoader.loadRow(createCaseSensitiveRow(), emptyVariables);
//
//        connection.assertInvoked().prepareStatement("update 'my_schema'.'table_a' set 'column_1'=?, 'column_2'=?, 'PK1'=?, 'Pk2'=? where 'PK1'=?, 'Pk2'=?");
//        preparedStatement.assertInvoked().executeUpdate();
//    }


}