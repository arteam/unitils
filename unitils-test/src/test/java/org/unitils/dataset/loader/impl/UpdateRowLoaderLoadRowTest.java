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
import org.unitils.dataset.core.database.Column;
import org.unitils.dataset.core.database.Row;
import org.unitils.dataset.core.database.Value;
import org.unitils.dataset.database.DatabaseAccessor;
import org.unitils.mock.Mock;

import static java.sql.Types.VARCHAR;
import static java.util.Arrays.asList;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class UpdateRowLoaderLoadRowTest extends UnitilsJUnit4 {

    /* Tested object */
    private UpdateDataSetLoader updateDataSetLoader = new UpdateDataSetLoader();

    protected Mock<DatabaseAccessor> databaseAccessor;

    private Row row;
    private Value valuePk1;
    private Value valuePk2;
    private Value valuePkLiteral;
    private Value value;
    private Value valueLiteral;


    @Before
    public void initialize() throws Exception {
        databaseAccessor.returns(1).executeUpdate(null, null);
        updateDataSetLoader.init(null, databaseAccessor.getMock());

        row = new Row("my_schema.table_a");
        valuePk1 = new Value("1", false, new Column("pk1", VARCHAR, true));
        valuePk2 = new Value("2", false, new Column("pk2", VARCHAR, true));
        valuePkLiteral = new Value("pk-literal", true, new Column("pk3", VARCHAR, true));
        value = new Value("3", false, new Column("column_1", VARCHAR, false));
        valueLiteral = new Value("literal", true, new Column("column_2", VARCHAR, false));
    }


    @Test
    public void loadRow() throws Exception {
        row.addValue(valuePk1);
        row.addValue(value);
        row.addValue(valuePk2);

        updateDataSetLoader.loadRow(row);
        databaseAccessor.assertInvoked().executeUpdate("update my_schema.table_a set pk1=?, column_1=?, pk2=? where pk1=?, pk2=?", asList(valuePk1, value, valuePk2, valuePk1, valuePk2));
    }

    @Test
    public void literalColumns() throws Exception {
        row.addValue(valuePk1);
        row.addValue(valueLiteral);
        row.addValue(valuePkLiteral);

        updateDataSetLoader.loadRow(row);
        databaseAccessor.assertInvoked().executeUpdate("update my_schema.table_a set pk1=?, column_2=literal, pk3=pk-literal where pk1=?, pk3=pk-literal", asList(valuePk1, valuePk1));
    }

    @Test(expected = UnitilsException.class)
    public void noRecordWasUpdated() throws Exception {
        row.addValue(valuePk1);

        databaseAccessor.onceReturns(0).executeUpdate(null, null);
        updateDataSetLoader.loadRow(row);
    }

    @Test
    public void onlyValuesForPkColumns() throws Exception {
        row.addValue(valuePk1);
        row.addValue(valuePk2);

        updateDataSetLoader.loadRow(row);
        databaseAccessor.assertInvoked().executeUpdate("update my_schema.table_a set pk1=?, pk2=? where pk1=?, pk2=?", asList(valuePk1, valuePk2, valuePk1, valuePk2));
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